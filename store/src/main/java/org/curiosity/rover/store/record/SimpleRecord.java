package org.curiosity.rover.store.record;

import com.fasterxml.jackson.databind.JsonNode;
import org.curiosity.rover.store.value.Value;

import java.util.*;
import java.util.stream.Collectors;

public class SimpleRecord implements Record {

    private final Map<String, Value> value;

    public SimpleRecord(Map<String, Value> value) {
        this.value = value;
    }


    @Override
    public Map<String, Value> getAll() {
        return this.value;
    }

    @Override
    public List<String> fieldNames() {
        return new ArrayList<>(this.value.keySet());
    }

    @Override
    public Value getValue(String field) {
        return this.value.get(field);
    }
}
