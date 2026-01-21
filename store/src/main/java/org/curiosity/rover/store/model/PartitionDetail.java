package org.curiosity.rover.store.model;

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
}
