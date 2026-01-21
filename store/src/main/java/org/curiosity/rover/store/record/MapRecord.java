package org.curiosity.rover.store.record;

import org.curiosity.rover.store.value.Value;

import java.util.*;

public class MapRecord implements Record {

    private final Map<String, Value> value;

    public MapRecord(Map<String, Value> value) {
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

    @Override
    public boolean hasField(String field) {
        return this.value.containsKey(field);
    }
}
