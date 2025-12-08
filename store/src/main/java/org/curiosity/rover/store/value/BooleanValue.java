package org.curiosity.rover.store.value;

import java.util.Objects;

public class BooleanValue implements Value {
    private final boolean value;

    public BooleanValue(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BooleanValue that = (BooleanValue) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
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