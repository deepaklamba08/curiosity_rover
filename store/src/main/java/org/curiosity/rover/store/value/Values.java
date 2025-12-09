package org.curiosity.rover.store.value;

import java.util.Map;

public class Values {

    public static Value stringValue(String value) {
        return new StringValue(value);
    }

    public static Value intValue(int value) {
        return new IntegerValue(value);
    }
    public static Value longValue(long value) {
        return new LongValue(value);
    }

    public static Value booleanValue(boolean value) {
        return new BooleanValue(value);
    }
    public static Value arrayValue(Value [] value) {
        return new ArrayValue(value);
    }
    public static Value mapValue(Map<String, Value> value) {
        return new MapValue(value);
    }

}
