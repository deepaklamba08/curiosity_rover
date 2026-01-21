package org.curiosity.rover.store.model;

import java.util.Objects;

public class PartitionMetadata {

    private final int level;
    private final String column;

    public PartitionMetadata(int level, String column) {
        this.level = level;
        this.column = column;
    }

    public int getLevel() {
        return level;
    }

    public String getColumn() {
        return column;
    }
    public static class Builder {
        private int level;
        private String column;

        public Builder withLevel(int level) {
            this.level = level;
            return this;
        }

        public Builder withColumn(String column) {
            this.column = column;
            return this;
        }

        public PartitionMetadata build() {
            return new PartitionMetadata(level, column);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PartitionMetadata that = (PartitionMetadata) o;
        return level == that.level && Objects.equals(column, that.column);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, column);
    }

    @Override
    public String toString() {
        return "PartitionMetadata{" +
                "level=" + level +
                ", column='" + column + '\'' +
                '}';
    }
}
