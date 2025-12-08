package org.curiosity.rover.store.value;

import java.util.Objects;

public class IntegerValue implements Value {
    private final int value;

    public IntegerValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntegerValue that = (IntegerValue) o;
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