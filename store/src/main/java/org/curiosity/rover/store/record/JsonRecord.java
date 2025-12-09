package org.curiosity.rover.store.record;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.curiosity.rover.store.value.Value;
import org.curiosity.rover.store.value.Values;

import java.util.*;

public class JsonRecord implements Record {

    private final JsonNode data;
    private final Map<String, Value> valueMap;

    public JsonRecord(JsonNode data) {
        this.data = data;
        this.valueMap = this.structureData(data);
    }

    @Override
    public Map<String, Value> getAll() {
        return this.valueMap;
    }

    @Override
    public List<String> fieldNames() {
        return new ArrayList<>(valueMap.keySet());
    }

    @Override
    public Value getValue(String field) {
        return this.valueMap.get(field);
    }

    private Map<String, Value> structureData(JsonNode element) {
        if (element instanceof ObjectNode) {
            Map<String, Value> dataMap = new HashMap<>();
            Iterator<Map.Entry<String, JsonNode>> fields = element.fields();
            fields.forEachRemaining(node -> dataMap.put(node.getKey(), parseValue(node.getValue())));
            return dataMap;
        } else if (element instanceof ArrayNode) {
            List<Value> values = new ArrayList<>();
            element.elements().forEachRemaining(elem -> values.add(parseValue(elem)));
            Value[] arr = new Value[values.size()];
            for (int i = 0; i < values.size(); i++) {
                arr[i] = values.get(i);
            }
            return Collections.singletonMap("", Values.arrayValue(arr));
        } else {
            throw new IllegalStateException("Invalid value node - " + element);
        }

    }

    private Value parseValue(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        } else if (node.isTextual()) {
            return Values.stringValue(node.asText());
        } else if (node.isInt()) {
            return Values.intValue(node.asInt());
        } else if (node.isLong()) {
            return Values.longValue(node.asLong());
        } else if (node.isBoolean()) {
            return Values.booleanValue(node.asBoolean());
        } else if (node.isArray()) {
            List<Value> values = new ArrayList<>();
            node.elements().forEachRemaining(elem -> values.add(parseValue(elem)));
            Value[] arr = new Value[values.size()];
            for (int i = 0; i < values.size(); i++) {
                arr[i] = values.get(i);
            }
            return Values.arrayValue(arr);
        } else if (node.isObject()) {
            Map<String, Value> dataMap = new HashMap<>();
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            fields.forEachRemaining(elem -> dataMap.put(elem.getKey(), parseValue(elem.getValue())));
            return Values.mapValue(dataMap);
        } else {
            throw new IllegalStateException("Invalid value node - " + node);
        }
    }

    @Override
    public String toString() {
        return "JsonRecord{" +
                "valueMap=" + valueMap +
                '}';
    }
}
