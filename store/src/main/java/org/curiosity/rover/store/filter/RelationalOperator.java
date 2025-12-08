package org.curiosity.rover.store.filter;

import org.curiosity.rover.store.value.Value;

public abstract class RelationalOperator implements Operator {

    private final String key;
    private final Value value;
    private final boolean caseSensitive;

    public RelationalOperator(String key, Value value) {
        this.key = key;
        this.value = value;
        this.caseSensitive = false;
    }

    public RelationalOperator(String key, Value value, boolean caseSensitive) {
        this.key = key;
        this.value = value;
        this.caseSensitive = caseSensitive;
    }

    public Value getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }

    public boolean iscaseSensitive() {
        return caseSensitive;
    }

    public static class Eq extends RelationalOperator {
        public Eq(String key, Value value) {
            super(key, value);
        }

        public Eq(String key, Value value, boolean caseSensitive) {
            super(key, value, caseSensitive);
        }
    }

    public static class NotEq extends RelationalOperator {
        public NotEq(String key, Value value) {
            super(key, value);
        }

        public NotEq(String key, Value value, boolean caseSensitive) {
            super(key, value, caseSensitive);
        }
    }

    public static class Gt extends RelationalOperator {
        public Gt(String key, Value value) {
            super(key, value);
        }
    }

    public static class GtEq extends RelationalOperator {
        public GtEq(String key, Value value) {
            super(key, value);
        }
    }

    public static class Lt extends RelationalOperator {
        public Lt(String key, Value value) {
            super(key, value);
        }
    }

    public static class LtEq extends RelationalOperator {
        public LtEq(String key, Value value) {
            super(key, value);
        }
    }

    public static class In extends RelationalOperator {
        public In(String key, Value value) {
            super(key, value);
        }

        public In(String key, Value value, boolean caseSensitive) {
            super(key, value, caseSensitive);
        }
    }

    public static class Contains extends RelationalOperator {
        public Contains(String key, Value value) {
            super(key, value);
        }

        public Contains(String key, Value value, boolean caseSensitive) {
            super(key, value, caseSensitive);
        }
    }
    

}
