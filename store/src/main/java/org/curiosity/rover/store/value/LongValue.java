package org.curiosity.rover.store.value;

import java.util.Objects;

public class LongValue implements Value {
    private final long value;

    public LongValue(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LongValue that = (LongValue) o;
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