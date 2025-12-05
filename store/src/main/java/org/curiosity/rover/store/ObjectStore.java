package org.curiosity.rover.store;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.curiosity.rover.store.util.DataUtil;
import org.curiosity.rover.store.util.IOUtil;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ObjectStore {

    private static final String METADATA_FILE_NAME = "store.json";
    private static final String METADATA_DIR_NAME = "object";
    private final String directory;

    public ObjectStore(String directory) {
        this.directory = directory;
    }

    public void registerObject(String name, Map<String, String> properties) {
        // Implementation for registering an object in the store
        ObjectMetadata existing = this.readObjectMetadata(name);
        if (existing != null) {
            throw new IllegalArgumentException("Object already exists- " + name);
        }
        ObjectMetadata metadata = new ObjectMetadata.Builder()
                .withObjectName(name)
                .withCreateDate(LocalDateTime.now())
                .withFileCount(0)
                .withProperties(properties)
                .build();

        this.writeObjectMetadata(metadata);
    }

    public QueryStatement queryObject(String name) {
        // Implementation for querying an object by name
        ObjectMetadata existing = this.readObjectMetadata(name);
        if (existing == null) {
            throw new IllegalArgumentException("Object not exists- " + name);
        }
        return new QueryStatement(this, existing);
    }


    private ObjectMetadata readObjectMetadata(String name) {
        ObjectMetadata metadata = this.readObjectMetadata().stream().filter(element -> element.getObjectName().equals(name))
                .findAny().orElse(null);
        return metadata;
    }

    private List<ObjectMetadata> readObjectMetadata() {
        File metadataFile = this.getMetadataFilePath();
        List<ObjectMetadata> metadataElements = IOUtil.readFile(metadataFile, this::mapObjectMetadata);
        return metadataElements;
    }


    private void writeObjectMetadata(ObjectMetadata metadata) {
        List<ObjectMetadata> existingElements = this.readObjectMetadata();
        existingElements.add(metadata);
        this.overwriteMetadata(existingElements);
    }

    private void updateObjectMetadata(ObjectMetadata metadata) {
        List<ObjectMetadata> existingElements = this.readObjectMetadata().stream()
                .filter(element -> !element.getObjectName().equals(metadata.getObjectName()))
                .collect(Collectors.toList());
        existingElements.add(metadata);
        this.overwriteMetadata(existingElements);
    }

    private void overwriteMetadata(List<ObjectMetadata> elements) {
        IOUtil.writeFile(this.getMetadataFilePath(), this::convertObjectMetadata, elements);
    }

    private ObjectMetadata mapObjectMetadata(JsonNode element) {
        ObjectMetadata.Builder builder = new ObjectMetadata.Builder()
                .withObjectName(element.get("objectName").asText())
                .withFileCount(element.get("fileCount").asInt())
                .withBasePath(element.get("basePath").asText());

        JsonNode createDate = element.get("createDate");
        if (createDate != null) {
            builder.withCreateDate(LocalDateTime.parse(createDate.asText()));
        }
        JsonNode updateDate = element.get("updateDate");
        if (updateDate != null) {
            builder.withUpdateDate(LocalDateTime.parse(updateDate.asText()));
        }

        JsonNode properties = element.get("properties");
        ObjectMapper objectMapper = DataUtil.OBJECT_MAPPER;
        if (properties != null) {
            builder.withProperties(objectMapper.convertValue(properties, Map.class));
        }

        JsonNode files = element.get("files");
        if (files != null) {
            AtomicInteger fileCount = new AtomicInteger();
            files.elements().forEachRemaining(fileElement -> {
                fileCount.getAndIncrement();
                builder.withFileMetadata(this.mapFileMetadata(fileElement));
            });
            builder.withFileCount(fileCount.get());
        }

        return builder.build();
    }

    private FileMetadata mapFileMetadata(JsonNode fileElement) {
        FileMetadata fileMetadata = new FileMetadata.Builder()
                .withFilePath(fileElement.get("filePath").asText())
                .withCreateDate(LocalDateTime.parse(fileElement.get("createDate").asText()))
                .withRecordCount(fileElement.get("recordCount").asLong())
                .withUpdateDate(LocalDateTime.parse(fileElement.get("updateDate").asText()))
                .build();
        return fileMetadata;
    }

    private JsonNode convertObjectMetadata(ObjectMetadata metadata) {
        ObjectMapper objectMapper = DataUtil.OBJECT_MAPPER;
        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("objectName", metadata.getObjectName());
        jsonNode.put("createDate", metadata.getCreateDate().toString());
        jsonNode.put("fileCount", metadata.getFileCount());
        jsonNode.put("basePath", metadata.getBasePath());

        if (metadata.getUpdateDate() != null) {
            jsonNode.put("updateDate", metadata.getUpdateDate().toString());
        }

        if (metadata.getProperties() != null) {
            ObjectNode propertiesNode = objectMapper.createObjectNode();
            propertiesNode.putAll(objectMapper.convertValue(metadata.getProperties(), ObjectNode.class));
            jsonNode.set("properties", propertiesNode);
        }
        if (metadata.getFiles() != null) {
            ArrayNode filesNode = objectMapper.createArrayNode();
            jsonNode.set("files", filesNode);
            metadata.getFiles().forEach(fileMetadata -> filesNode.add(this.convertFileMetadata(fileMetadata)));
        }

        return jsonNode;
    }

    private ObjectNode convertFileMetadata(FileMetadata fileMetadata) {
        ObjectMapper objectMapper = DataUtil.OBJECT_MAPPER;
        ObjectNode fileNode = objectMapper.createObjectNode();
        fileNode.put("filePath", fileMetadata.getFilePath());
        fileNode.put("createDate", fileMetadata.getCreateDate().toString());
        fileNode.put("updateDate", fileMetadata.getUpdateDate().toString());
        fileNode.put("recordCount", fileMetadata.getRecordCount());
        return fileNode;
    }

    private File getMetadataFilePath() {
        File baseDir = new File(this.directory, METADATA_DIR_NAME);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
        File metadataFile = new File(baseDir, METADATA_FILE_NAME);
        if (!metadataFile.exists()) {
            try {
                metadataFile.createNewFile();
            } catch (IOException e) {
                throw new IllegalStateException("Error occurred while creating metadata file", e);
            }
        }
        return metadataFile;
    }
}
