package org.curiosity.rover.store.io;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.curiosity.rover.store.record.Record;
import org.curiosity.rover.store.util.DataUtil;
import org.curiosity.rover.store.util.IOUtil;
import org.curiosity.rover.store.value.*;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JsonDataWriter implements DataWriter {
    @Override
    public void writeData(File file, Collection<Record> records) {
        List<JsonNode> elements = records.stream().map(r -> parseRecord(r.getAll())).collect(Collectors.toList());
        IOUtil.writeFile(file, Function.identity(), elements);
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
}
