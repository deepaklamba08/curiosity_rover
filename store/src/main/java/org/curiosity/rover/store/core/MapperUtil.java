package org.curiosity.rover.store.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.curiosity.rover.store.model.*;
import org.curiosity.rover.store.util.DataUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    static VersionMetadata mapVersionMetadata(JsonNode element) {
        return new VersionMetadata.Builder()
                .withVersion(element.get("version").asInt())
                .withCreateDate(LocalDateTime.parse(element.get("create_ts").asText()))
                .build();

    }

    static ObjectMetadata mapObjectMetadata(JsonNode element) {
        ObjectMetadata.Builder builder = new ObjectMetadata.Builder()
                .withObjectName(element.get("object_name").asText())
                .withFileCount(element.get("file_count").asInt())
                .withBaseLocation(element.get("base_location").asText())
                .withDataLocation(element.get("data_location").asText())
                .withStatus(element.get("status").booleanValue())
                .withCreatedBy(element.get("created_by").asText())
                .withDataFormat(DataFormat.getDataFormat(element.get("file_format").asText()));

        JsonNode createDate = element.get("create_date");
        if (createDate != null) {
            builder.withCreateDate(LocalDateTime.parse(createDate.asText()));
        }
        JsonNode updateDate = element.get("update_date");
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
        JsonNode partitions = element.get("partition");
        if (partitions != null) {
            partitions.elements().forEachRemaining(partitionElement -> {
                builder.withPartitionMetadata(MapperUtil.mapPartitionMetadata(partitionElement));
            });
        }

        return builder.build();
    }

    private static PartitionMetadata mapPartitionMetadata(JsonNode partitionElement) {
        return new PartitionMetadata.Builder()
                .withLevel(partitionElement.get("level").asInt())
                .withColumn(partitionElement.get("column").asText())
                .build();
    }

    static JsonNode convertObjectMetadata(ObjectMetadata metadata) {
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
            jsonNode.put("update_date", metadata.getUpdateDate().toString());
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
            jsonNode.set("files", filesNode);
            metadata.getFiles().forEach(fileMetadata -> filesNode.add(MapperUtil.convertFileMetadata(fileMetadata)));
        }

        return jsonNode;
    }

    static ObjectNode convertPartitionMetadata(PartitionMetadata pm) {
        ObjectMapper objectMapper = DataUtil.OBJECT_MAPPER;
        ObjectNode partitionNode = objectMapper.createObjectNode();
        partitionNode.put("level", pm.getLevel());
        partitionNode.put("column", pm.getColumn());
        return partitionNode;
    }

    static ObjectNode convertFileMetadata(FileMetadata fileMetadata) {
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
        if (fileMetadata.getPartition() != null) {
            ArrayNode partitionNode = objectMapper.createArrayNode();
            fileNode.set("partition", partitionNode);
            fileMetadata.getPartition().getPartitions()
                    .forEach(pm -> partitionNode.add(MapperUtil.convertPartitionDetail(pm)));

        }
        if (fileMetadata.getFieldStats() != null) {
            ArrayNode statsNode = objectMapper.createArrayNode();
            fileMetadata.getFieldStats().forEach(stats -> statsNode.add(convertFieldStats(stats)));
            fileNode.set("field_stats", statsNode);
        }
        return fileNode;
    }

    private static JsonNode convertFieldStats(FieldStats stats) {
        ObjectMapper objectMapper = DataUtil.OBJECT_MAPPER;
        ObjectNode node = objectMapper.createObjectNode();
        node.put("field_name", stats.getFieldName());
        node.put("total_count", stats.getTotalCount());
        node.put("null_count", stats.getNullCount());
        node.put("distinct_count", stats.getDistinctCount());
        node.put("duplicate_count", stats.getDuplicateCount());

        if (stats.getMinValue() != null)
            node.put("min_value", stats.getMinValue());
        if (stats.getMaxValue() != null)
            node.put("max_value", stats.getMaxValue());
        if (stats.getSum() != null)
            node.put("sum", stats.getSum());
        if (stats.getAverage() != null)
            node.put("average", stats.getAverage());

        if (stats.getMinLength() != null)
            node.put("min_length", stats.getMinLength());
        if (stats.getMaxLength() != null)
            node.put("max_length", stats.getMaxLength());
        if (stats.getAvgLength() != null)
            node.put("avg_length", stats.getAvgLength());

        return node;
    }

    static FileMetadata mapFileMetadata(JsonNode fileElement) {
        FileMetadata.Builder builder = new FileMetadata.Builder()
                .withFileName(fileElement.get("file_name").asText())
                .withFilePath(fileElement.get("file_path").asText())
                .withRecordCount(fileElement.get("record_count").asInt())
                .withCreateDate(LocalDateTime.parse(fileElement.get("create_date").asText()))
                .withCreatedBy(fileElement.get("created_by").asText())
                .makeCompact(fileElement.get("is_compact").asBoolean());

        JsonNode updateDate = fileElement.get("update_date");

        if (updateDate != null && !updateDate.isNull()) {
            builder.withUpdateDate(LocalDateTime.parse(updateDate.asText()));
        }
        JsonNode partitions = fileElement.get("partition");
        if (partitions != null) {
            List<PartitionDetail> detailList = new ArrayList<>();
            partitions.elements().forEachRemaining(partitionElement -> detailList.add(new PartitionDetail(
                    MapperUtil.mapPartitionMetadata(partitionElement),
                    partitionElement.get("value").asText())));
            builder.withPartition(new PartitionSet(detailList));
        }
        JsonNode statsNode = fileElement.get("field_stats");
        if (statsNode != null && !statsNode.isNull()) {
            statsNode.forEach(entry -> {
                builder.withFieldStat(mapFieldStats(entry));
            });
        }
        return builder.build();
    }

    private static FieldStats mapFieldStats(JsonNode node) {
        FieldStats.Builder builder = new FieldStats.Builder()
                .withFieldName(node.get("field_name").asText())
                .withTotalCount(node.get("total_count").asLong())
                .withNullCount(node.get("null_count").asLong())
                .withDistinctCount(node.get("distinct_count").asLong())
                .withDuplicateCount(node.get("duplicate_count").asLong());

        if (node.has("min_value"))
            builder.withMinValue(node.get("min_value").asDouble());
        if (node.has("max_value"))
            builder.withMaxValue(node.get("max_value").asDouble());
        if (node.has("sum"))
            builder.withSum(node.get("sum").asDouble());
        if (node.has("average"))
            builder.withAverage(node.get("average").asDouble());

        if (node.has("min_length"))
            builder.withMinLength(node.get("min_length").asInt());
        if (node.has("max_length"))
            builder.withMaxLength(node.get("max_length").asInt());
        if (node.has("avg_length"))
            builder.withAvgLength(node.get("avg_length").asDouble());

        return builder.build();
    }

    static ObjectNode convertPartitionDetail(PartitionDetail pm) {
        ObjectMapper objectMapper = DataUtil.OBJECT_MAPPER;
        ObjectNode fileNode = objectMapper.createObjectNode();
        fileNode.put("level", pm.getMetadata().getLevel());
        fileNode.put("column", pm.getMetadata().getColumn());
        fileNode.put("value", pm.getValue());
        return fileNode;
    }

}
