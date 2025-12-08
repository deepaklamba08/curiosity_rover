package org.curiosity.rover.store.record;

import org.curiosity.rover.store.value.Value;

import java.util.List;
import java.util.Map;

public interface Record {

    public Map<String, Value> getAll();

    public List<String> fieldNames();

    public Value getValue(String field);
}
