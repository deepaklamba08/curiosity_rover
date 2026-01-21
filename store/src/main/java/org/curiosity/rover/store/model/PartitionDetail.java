package org.curiosity.rover.store.model;

import java.util.Objects;

public class PartitionDetail {

    private final PartitionMetadata metadata;
    private final String value;

    public PartitionDetail(PartitionMetadata metadata, String value) {
        this.metadata = metadata;
        this.value = value;
    }

    public PartitionMetadata getMetadata() {
        return metadata;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PartitionDetail that = (PartitionDetail) o;
        return Objects.equals(metadata, that.metadata) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metadata, value);
    }

    @Override
    public String toString() {
        return "PartitionDetail{" +
                "metadata=" + metadata +
                ", value='" + value + '\'' +
                '}';
    }
}
