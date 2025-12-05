package org.curiosity.rover.store;

import org.curiosity.rover.store.Value;

public class Values {

    public static Value stringValue(String value){
        return new StringValue(value);
    }
    public static Value intValue(int value){
        return new IntegerValue(value);
    }

    public static class StringValue implements Value{
        private final String value;

        public StringValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public static class IntegerValue implements Value{
        private final int value;

        public IntegerValue(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
