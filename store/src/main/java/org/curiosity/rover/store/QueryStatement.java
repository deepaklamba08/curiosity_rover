package org.curiosity.rover.store;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.curiosity.rover.store.filter.Operator;
import org.curiosity.rover.store.filter.RecordVisitor;
import org.curiosity.rover.store.record.JsonRecord;
import org.curiosity.rover.store.record.Record;
import org.curiosity.rover.store.util.DataUtil;
import org.curiosity.rover.store.util.IOUtil;
import org.curiosity.rover.store.value.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueryStatement {

    private final ObjectStore store;
    private final String objectName;
    private final RecordVisitor visitor;

    public QueryStatement(ObjectStore store, String objectName) {
        this.objectName = objectName;
        this.store = store;
        this.visitor = new RecordVisitor();
    }

    public QueryResultIterator executeQuery() {
        Iterator<Record> elements = this.readFiles().iterator();
        return new QueryResultIterator(elements);
    }

    public QueryResultIterator executeQuery(Operator filter) {
        Predicate<Record> predicate = this.visitor.visit(filter).getPredicate();

        Iterator<Record> elements = this.readFiles().filter(predicate).iterator();
        return new QueryResultIterator(elements);
    }

    public <T> List<T> executeQuery(Function<Record, T> mapper) {
        return this.readFiles().map(mapper).collect(Collectors.toList());
    }

    public <T> List<T> executeQuery(Operator filter, Function<Record, T> mapper) {
        Predicate<Record> predicate = this.visitor.visit(filter).getPredicate();
        return this.readFiles().filter(predicate).map(mapper).collect(Collectors.toList());

    }

    public void insertRecord(Record record) {
        this.batchInsertRecord(Collections.singletonList(record));
    }

    public void batchInsertRecord(List<Record> records) {
        List<JsonNode> elements = records.stream().map(r -> parseRecord(r.getAll())).collect(Collectors.toList());
        String fileName = this.generateFileName();
        File dataFilePath = this.getDataFilePath(fileName);
        IOUtil.writeFile(dataFilePath, Function.identity(), elements);
        FileMetadata fileMetadata = new FileMetadata.Builder()
                .withFilePath(dataFilePath.getAbsolutePath())
                .withRecordCount(records.size())
                .withCreateDate(LocalDateTime.now())
                .build();
        this.store.addFile(this.objectName, fileMetadata);

    }

    private JsonNode parseRecord(Map<String, Value> values) {
        ObjectNode dataNode = DataUtil.OBJECT_MAPPER.createObjectNode();
        values.forEach((header, value) -> addValue(header, value, dataNode));
        return dataNode;
    }

    private void addValue(String header, Value source, ObjectNode target) {
        if (source instanceof StringValue) {
            StringValue stringValue = (StringValue) source;
            target.put(header, stringValue.getValue());
        } else if (source instanceof IntegerValue) {
            IntegerValue integerValue = (IntegerValue) source;
            target.put(header, integerValue.getValue());
        } else if (source instanceof BooleanValue) {
            BooleanValue booleanValue = (BooleanValue) source;
            target.put(header, booleanValue.getValue());
        } else if (source instanceof ArrayValue) {
            ArrayValue arrayValue = (ArrayValue) source;
            target.set(header, parseArrayValue(arrayValue));
        } else if (source instanceof MapValue) {
            MapValue mapValue = (MapValue) source;
            target.set(header, parseRecord(mapValue.getValue()));
        } else {
            throw new IllegalStateException("Value not supported- " + source.getClass());
        }
    }

    private JsonNode parseArrayValue(ArrayValue arrayValue) {
        ArrayNode node = DataUtil.OBJECT_MAPPER.createArrayNode();
        Arrays.stream(arrayValue.getValue()).forEach(value -> {
            if (value instanceof StringValue) {
                StringValue stringValue = (StringValue) value;
                node.add(stringValue.getValue());
            } else if (value instanceof IntegerValue) {
                IntegerValue integerValue = (IntegerValue) value;
                node.add(integerValue.getValue());
            } else if (value instanceof BooleanValue) {
                BooleanValue booleanValue = (BooleanValue) value;
                node.add(booleanValue.getValue());
            } else if (value instanceof ArrayValue) {
                ArrayValue av = (ArrayValue) value;
                node.add(parseArrayValue(av));
            } else if (value instanceof MapValue) {
                MapValue mapValue = (MapValue) value;
                node.add(parseRecord(mapValue.getValue()));
            } else {
                throw new IllegalStateException("Value not supported- " + value.getClass());
            }
        });
        return node;
    }

    private File getDataFilePath(String fileName) {
        ObjectMetadata metadata = this.store.getObject(this.objectName);
        File dataFile = new File(metadata.getBasePath(), metadata.getObjectName());
        if (!dataFile.exists()) {
            dataFile.mkdirs();
        }
        File metadataFile = new File(dataFile, fileName);
        if (!metadataFile.exists()) {
            try {
                metadataFile.createNewFile();
            } catch (IOException e) {
                throw new IllegalStateException("Error occurred while creating data file", e);
            }
        }
        return metadataFile;
    }

    private String generateFileName() {
        LocalDateTime dateTime = LocalDateTime.now();
        StringBuilder builder = new StringBuilder();
        builder.append(dateTime.getYear()).append("_")
                .append(dateTime.getMonthValue()).append("_")
                .append(dateTime.getDayOfMonth()).append("_")
                .append(dateTime.getHour()).append("_")
                .append(dateTime.getMinute()).append("_")
                .append(dateTime.getSecond()).append("_")
                .append(".json");
        return builder.toString();
    }

    private Stream<Record> readFiles() {
        ObjectMetadata metadata = this.store.getObject(this.objectName);
        Function<JsonNode, Record> mapper = (JsonRecord::new);
        return metadata.getFiles().stream().flatMap(fileMetadata ->
                IOUtil.readFile(new File(fileMetadata.getFilePath()), Function.identity()).stream()
        ).map(mapper);
    }
}
