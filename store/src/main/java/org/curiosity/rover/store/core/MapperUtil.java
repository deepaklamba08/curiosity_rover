package org.curiosity.rover.store.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.curiosity.rover.store.model.*;
import org.curiosity.rover.store.util.DataUtil;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class MapperUtil {

     static JsonNode convertVersionMetadata(VersionMetadata versionMetadata) {
        ObjectMapper objectMapper = DataUtil.OBJECT_MAPPER;
        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("version", versionMetadata.getVersion());
        jsonNode.put("create_ts", versionMetadata.getCreateDate().toString());

        return jsonNode;
    }

     static  VersionMetadata mapVersionMetadata(JsonNode element) {
        return new VersionMetadata.Builder()
                .withVersion(element.get("version").asInt())
                .withCreateDate(LocalDateTime.parse(element.get("create_ts").asText()))
                .build();

    }

     static  ObjectMetadata mapObjectMetadata(JsonNode element) {
        ObjectMetadata.Builder builder = new ObjectMetadata.Builder()
                .withObjectName(element.get("objectName").asText())
                .withFileCount(element.get("fileCount").asInt())
                .withBaseLocation(element.get("basePath").asText())
                .withDataFormat(DataFormat.getDataFormat(element.get("format").asText()));

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
                builder.withFileMetadata(MapperUtil.mapFileMetadata(fileElement));
            });
            builder.withFileCount(fileCount.get());
        }

        return builder.build();
    }

     static FileMetadata mapFileMetadata(JsonNode fileElement) {
        FileMetadata.Builder builder = new FileMetadata.Builder()
                .withFilePath(fileElement.get("filePath").asText())
                .withCreateDate(LocalDateTime.parse(fileElement.get("createDate").asText()))
                .withRecordCount(fileElement.get("recordCount").asLong());

        JsonNode updateDate = fileElement.get("updateDate");

        if (updateDate != null && !updateDate.isNull()) {
            builder = builder.withUpdateDate(LocalDateTime.parse(updateDate.asText()));
        }
        return builder.build();
    }

     static  JsonNode convertObjectMetadata(ObjectMetadata metadata) {
        ObjectMapper objectMapper = DataUtil.OBJECT_MAPPER;


        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("object_name", metadata.getObjectName());
        jsonNode.put("create_date", metadata.getCreateDate().toString());
        jsonNode.put("file_count", metadata.getFileCount());
        jsonNode.put("created_by", metadata.getCreatedBy());
        jsonNode.put("file_format", metadata.getFormat().getFormatName());
        jsonNode.put("base_location", metadata.getBaseLocation());
        jsonNode.put("data_location", metadata.getDataLocation());

        if (metadata.getUpdateDate() != null) {
            jsonNode.put("updateDate", metadata.getUpdateDate().toString());
        }
        jsonNode.put("status", metadata.isStatus());
        if (metadata.getProperties() != null) {
            ObjectNode propertiesNode = objectMapper.createObjectNode();
            propertiesNode.putAll(objectMapper.convertValue(metadata.getProperties(), ObjectNode.class));
            jsonNode.set("properties", propertiesNode);
        }
        if (metadata.getPartition() != null) {
            ArrayNode partitionNode = objectMapper.createArrayNode();
            jsonNode.set("partition", partitionNode);
            metadata.getPartition().forEach(pm -> partitionNode.add(MapperUtil.convertPartitionMetadata(pm)));
        }
        if (metadata.getFiles() != null) {
            ArrayNode filesNode = objectMapper.createArrayNode();
            jsonNode.set("partition", filesNode);
            metadata.getFiles().forEach(fileMetadata -> filesNode.add(MapperUtil.convertFileMetadata(fileMetadata)));
        }


        return jsonNode;
    }

     static  ObjectNode convertPartitionMetadata(PartitionMetadata pm) {
        ObjectMapper objectMapper = DataUtil.OBJECT_MAPPER;
        ObjectNode partitionNode = objectMapper.createObjectNode();
        partitionNode.put("level", pm.getLevel());
        partitionNode.put("column", pm.getColumn());
        return partitionNode;
    }

     static  ObjectNode convertFileMetadata(FileMetadata fileMetadata) {
        ObjectMapper objectMapper = DataUtil.OBJECT_MAPPER;
        ObjectNode fileNode = objectMapper.createObjectNode();
        fileNode.put("file_name", fileMetadata.getFileName());
        fileNode.put("file_path", fileMetadata.getFilePath());
        fileNode.put("record_count", fileMetadata.getRecordCount());
        fileNode.put("create_date", fileMetadata.getCreateDate().toString());
        fileNode.put("created_by", fileMetadata.getCreatedBy());
        fileNode.put("is_compact", fileMetadata.isCompact());
        if (fileMetadata.getUpdateDate() != null) {
            fileNode.put("update_date", fileMetadata.getUpdateDate().toString());
        }
        if (fileMetadata.getPartitionDetails() != null) {
            ArrayNode partitionNode = objectMapper.createArrayNode();
            fileNode.set("partition", partitionNode);
            fileMetadata.getPartitionDetails().forEach(pm -> partitionNode.add(MapperUtil.convertPartitionDetail(pm)));

        }
        return fileNode;
    }

     static  ObjectNode convertPartitionDetail(PartitionDetail pm) {
        ObjectMapper objectMapper = DataUtil.OBJECT_MAPPER;
        ObjectNode fileNode = objectMapper.createObjectNode();
        fileNode.put("level", pm.getMetadata().getLevel());
        fileNode.put("column", pm.getMetadata().getColumn());
        fileNode.put("value", pm.getValue());
        return fileNode;
    }

}
