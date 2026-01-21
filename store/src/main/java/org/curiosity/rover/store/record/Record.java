package org.curiosity.rover.store.record;

import org.curiosity.rover.store.value.Value;

import java.util.List;
import java.util.Map;

public interface Record {

     Map<String, Value> getAll();

     List<String> fieldNames();

     Value getValue(String field);

     boolean hasField(String field);
}
