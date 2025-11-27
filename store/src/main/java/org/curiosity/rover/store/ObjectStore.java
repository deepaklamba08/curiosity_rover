package org.curiosity.rover.store;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ObjectStore {

    private static final String METADATA_FILE_NAME = "store.json";
    private static final String METADATA_DIR_NAME = "object";
    private final String directory;
    private ObjectMapper objectMapper = new ObjectMapper();

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
        return null;
    }


    private ObjectMetadata readObjectMetadata(String name) {
        ObjectMetadata metadata = readObjectMetadata().stream().filter(element -> element.getObjectName().equals(name))
                .findAny().orElse(null);
        return metadata;
    }

    private List<ObjectMetadata> readObjectMetadata() {
        File metadataFile = this.getMetadataFilePath();
        List<ObjectMetadata> metadataElements = new ArrayList<>();
        try (InputStream inputStream = Files.newInputStream(metadataFile.toPath())) {
            JsonNode jsonNode = objectMapper.readTree(inputStream);
            jsonNode.elements().forEachRemaining(element -> {
                metadataElements.add(this.mapObjectMetadata(element));
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private ObjectMetadata mapObjectMetadata(JsonNode element) {
        return null;
    }


    private void writeObjectMetadata(ObjectMetadata metadata) {
        List<ObjectMetadata> existingElements = readObjectMetadata();
        existingElements.add(metadata);
        this.overwriteMetadata(existingElements);
    }

    private void updateObjectMetadata(ObjectMetadata metadata) {
        List<ObjectMetadata> existingElements = readObjectMetadata().stream()
                .filter(element -> !element.getObjectName().equals(metadata.getObjectName()))
                .collect(Collectors.toList());
        existingElements.add(metadata);
        this.overwriteMetadata(existingElements);
    }

    private void overwriteMetadata(List<ObjectMetadata> elements) {

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
