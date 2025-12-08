package org.curiosity.rover.store.value;

import java.util.Map;

public class MapValue implements Value {
    private final Map<String, Value> value;

    public MapValue(Map<String, Value> value) {
        this.value = value;
    }

    public Map<String, Value> getValue() {
        return value;
    }

    @Override
    public boolean neq(Value other) {
        return false;
    }

    @Override
    public boolean gt(Value other) {
        return false;
    }

    @Override
    public boolean gtEq(Value other) {
        return false;
    }

    @Override
    public boolean lt(Value other) {
        return false;
    }

    @Override
    public boolean ltEq(Value other) {
        return false;
    }
}