package org.curiosity.rover.store.model;

import java.util.List;
import java.util.Objects;

public class PartitionSet {
    private final List<PartitionDetail> partitions;

    public PartitionSet(List<PartitionDetail> partitions) {
        this.partitions = partitions;
    }

    public List<PartitionDetail> getPartitions() {
        return partitions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PartitionSet that = (PartitionSet) o;
        return Objects.equals(partitions, that.partitions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partitions);
    }

    @Override
    public String toString() {
        return "PartitionSet{" +
                "partitions=" + partitions +
                '}';
    }
}
