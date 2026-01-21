package org.curiosity.rover.store.model;

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
}
