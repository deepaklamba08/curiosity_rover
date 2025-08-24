package org.curiosity.rover.basic.cfg;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class JacksonConfiguration implements Configuration {

    private final JsonNode node;

    public JacksonConfiguration(JsonNode node) {
        this.node = node != null ? node : NullNode.getInstance();
    }

    @Override
    public Iterator<String> getFieldNames() {
        return node.fieldNames();
    }

    @Override
    public Configuration getConfiguration(String fieldName) {
        JsonNode child = node.get(fieldName);
        return new JacksonConfiguration(child);
    }

    @Override
    public Configuration getConfiguration(String fieldName, Configuration defaultValue) {
        JsonNode child = node.get(fieldName);
        return (child == null || child.isNull()) ? defaultValue : new JacksonConfiguration(child);
    }

    @Override
    public boolean isNull() {
        return node.isNull();
    }

    @Override
    public boolean isNull(String fieldName) {
        JsonNode child = node.get(fieldName);
        return child == null || child.isNull();
    }

    @Override
    public boolean hasField(String fieldName) {
        return node.has(fieldName);
    }

    @Override
    public boolean isPresent(String fieldName) {
        return node.hasNonNull(fieldName);
    }

    @Override
    public String getString(String fieldName) {
        JsonNode child = node.get(fieldName);
        return child != null && child.isTextual() ? child.asText() : null;
    }

    @Override
    public String getString(String fieldName, String defaultValue) {
        String value = getString(fieldName);
        return value != null ? value : defaultValue;
    }

    @Override
    public boolean isString(String fieldName) {
        JsonNode child = node.get(fieldName);
        return child != null && child.isTextual();
    }

    @Override
    public boolean isString() {
        return node.isTextual();
    }

    @Override
    public Integer getInt(String fieldName) {
        JsonNode child = node.get(fieldName);
        return child != null && child.isInt() ? child.asInt() : null;
    }

    @Override
    public Integer getInt(String fieldName, int defaultValue) {
        Integer value = getInt(fieldName);
        return value != null ? value : defaultValue;
    }

    @Override
    public boolean isInt(String fieldName) {
        JsonNode child = node.get(fieldName);
        return child != null && child.isInt();
    }

    @Override
    public boolean isInt() {
        return node.isInt();
    }

    @Override
    public Long getLong(String fieldName) {
        JsonNode child = node.get(fieldName);
        return child != null && child.isIntegralNumber() ? child.asLong() : null;
    }

    @Override
    public Long getLong(String fieldName, long defaultValue) {
        Long value = getLong(fieldName);
        return value != null ? value : defaultValue;
    }

    @Override
    public boolean isLong(String fieldName) {
        JsonNode child = node.get(fieldName);
        return child != null && child.isIntegralNumber();
    }

    @Override
    public boolean isLong() {
        return node.isIntegralNumber();
    }

    @Override
    public Float getFloat(String fieldName) {
        JsonNode child = node.get(fieldName);
        return child != null && child.isFloatingPointNumber() ? (float) child.asDouble() : null;
    }

    @Override
    public Float getFloat(String fieldName, float defaultValue) {
        Float value = getFloat(fieldName);
        return value != null ? value : defaultValue;
    }

    @Override
    public boolean isFloat(String fieldName) {
        JsonNode child = node.get(fieldName);
        return child != null && child.isFloatingPointNumber();
    }

    @Override
    public boolean isFloat() {
        return node.isFloatingPointNumber();
    }

    @Override
    public boolean isDouble() {
        return node.isDouble();
    }

    @Override
    public Boolean getBoolean(String fieldName) {
        JsonNode child = node.get(fieldName);
        return child != null && child.isBoolean() ? child.asBoolean() : null;
    }

    @Override
    public Boolean getBoolean(String fieldName, boolean defaultValue) {
        Boolean value = getBoolean(fieldName);
        return value != null ? value : defaultValue;
    }

    @Override
    public boolean isBoolean(String fieldName) {
        JsonNode child = node.get(fieldName);
        return child != null && child.isBoolean();
    }

    @Override
    public boolean isBoolean() {
        return node.isBoolean();
    }

    @Override
    public Date getDate(String fieldName) {
        String dateStr = getString(fieldName);
        try {
            return dateStr != null ? new Date(Long.parseLong(dateStr)) : null; // or use Date parsing
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Date getDate(String fieldName, Date defaultValue) {
        Date date = getDate(fieldName);
        return date != null ? date : defaultValue;
    }

    @Override
    public boolean isArray(String fieldName) {
        JsonNode child = node.get(fieldName);
        return child != null && child.isArray();
    }

    @Override
    public boolean isArray() {
        return node.isArray();
    }

    @Override
    public List<Configuration> getListValue(String fieldName) {
        JsonNode arrayNode = node.get(fieldName);
        if (arrayNode == null || !arrayNode.isArray()) {
            return Collections.emptyList();
        }
        return StreamSupport.stream(arrayNode.spliterator(), false)
                .map(JacksonConfiguration::new)
                .collect(Collectors.toList());
    }

    @Override
    public <T> List<T> getListValue(String fieldName, Function<Configuration, T> converter) {
        return getListValue(fieldName).stream()
                .map(converter)
                .collect(Collectors.toList());
    }

    @Override
    public List<Configuration> asList() {
        if (!node.isArray()) return Collections.emptyList();
        return StreamSupport.stream(node.spliterator(), false)
                .map(JacksonConfiguration::new)
                .collect(Collectors.toList());
    }

    @Override
    public <T> List<T> asList(Function<Configuration, T> converter) {
        return asList().stream()
                .map(converter)
                .collect(Collectors.toList());
    }

    @Override
    public <T> Map<String, T> getMapValues(String fieldName, Function<Configuration, T> converter) {
        JsonNode mapNode = node.get(fieldName);
        if (mapNode == null || !mapNode.isObject()) return Collections.emptyMap();

        Iterator<Map.Entry<String, JsonNode>> fields = mapNode.fields();
        Map<String, T> result = new LinkedHashMap<>();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            result.put(entry.getKey(), converter.apply(new JacksonConfiguration(entry.getValue())));
        }
        return result;
    }

    @Override
    public Map<String, Configuration> getMapValues(String fieldName) {
        JsonNode mapNode = node.get(fieldName);
        if (mapNode == null || !mapNode.isObject()) return Collections.emptyMap();

        Iterator<Map.Entry<String, JsonNode>> fields = mapNode.fields();
        Map<String, Configuration> result = new LinkedHashMap<>();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            result.put(entry.getKey(), new JacksonConfiguration(entry.getValue()));
        }
        return result;
    }

    @Override
    public boolean isObject(String fieldName) {
        JsonNode child = node.get(fieldName);
        return child != null && child.isObject();
    }

    @Override
    public boolean isObject() {
        return node.isObject();
    }

    @Override
    public String asString() {
        return node.isTextual() ? node.asText() : node.toString();
    }

    @Override
    public boolean asBoolean() {
        return node.asBoolean();
    }

    @Override
    public int asInt() {
        return node.asInt();
    }

    @Override
    public long asLong() {
        return node.asLong();
    }

    @Override
    public double asDouble() {
        return node.asDouble();
    }
}
