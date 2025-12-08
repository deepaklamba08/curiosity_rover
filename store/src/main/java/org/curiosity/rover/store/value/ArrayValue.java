package org.curiosity.rover.store.value;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ArrayValue implements Value {
    private final Value[] values;

    public ArrayValue(Value... values) {
        this.values = values;

        Set<? extends Class<? extends Value>> valueTypes = Arrays.stream(values).map(Value::getClass).collect(Collectors.toSet());
        if (valueTypes.size() > 1) {
            throw new IllegalArgumentException("value should have same type");
        }

    }

    public Value[] getValue() {
        return values;
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