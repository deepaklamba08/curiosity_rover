package org.curiosity.rover.store.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectMetadata {

    private final String objectName;
    private final LocalDateTime createDate;
    private final long fileCount;
    private final String createdBy;
    private final DataFormat format;
    private final String baseLocation;
    private final String dataLocation;
    private final LocalDateTime updateDate;
    private final boolean status;
    private final Map<String, String> properties;
    private final List<PartitionMetadata> partition;
    private final List<FileMetadata> files;

    private ObjectMetadata(String objectName, LocalDateTime createDate, long fileCount, String createdBy,
            DataFormat format, String baseLocation, String dataLocation, LocalDateTime updateDate, boolean status,
            Map<String, String> properties, List<PartitionMetadata> partition, List<FileMetadata> files) {
        this.objectName = objectName;
        this.createDate = createDate;
        this.fileCount = fileCount;
        this.createdBy = createdBy;
        this.format = format;
        this.baseLocation = baseLocation;
        this.dataLocation = dataLocation;
        this.updateDate = updateDate;
        this.status = status;
        this.properties = properties;
        this.partition = partition;
        this.files = files;
    }

    public boolean isPartitioned() {
        return this.partition != null && !this.partition.isEmpty();
    }

    public String getObjectName() {
        return objectName;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public long getFileCount() {
        return fileCount;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public DataFormat getFormat() {
        return format;
    }

    public String getBaseLocation() {
        return baseLocation;
    }

    public String getDataLocation() {
        return dataLocation;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public boolean isStatus() {
        return status;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public List<PartitionMetadata> getPartition() {
        return partition;
    }

    public List<FileMetadata> getFiles() {
        return files;
    }

    public static class Builder {
        private String objectName;
        private LocalDateTime createDate;
        private long fileCount;
        private String createdBy;
        private DataFormat format;
        private String baseLocation;
        private String dataLocation;
        private LocalDateTime updateDate;
        private boolean status;
        private Map<String, String> properties;
        private List<PartitionMetadata> partition;
        private List<FileMetadata> files;

        public Builder withObjectName(String objectName) {
            this.objectName = objectName;
            return this;
        }

        public Builder withCreateDate(LocalDateTime createDate) {
            this.createDate = createDate;
            return this;
        }

        public Builder withFileCount(long fileCount) {
            this.fileCount = fileCount;
            return this;
        }

        public Builder withCreatedBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder withDataFormat(DataFormat format) {
            this.format = format;
            return this;
        }

        public Builder withBaseLocation(String baseLocation) {
            this.baseLocation = baseLocation;
            return this;
        }

        public Builder withDataLocation(String dataLocation) {
            this.dataLocation = dataLocation;
            return this;
        }

        public Builder withUpdateDate(LocalDateTime updateDate) {
            this.updateDate = updateDate;
            return this;
        }

        public Builder withStatus(boolean status) {
            this.status = status;
            return this;
        }

        public Builder withFileMetadata(FileMetadata metadata) {
            if (this.files == null) {
                this.files = new ArrayList<>();
            }
            this.files.add(metadata);
            return this;
        }

        public Builder withFileMetadata(List<FileMetadata> metadataList) {
            if (this.files == null) {
                this.files = new ArrayList<>();
            }
            this.files.addAll(metadataList);
            return this;
        }

        public Builder withProperty(String key, String value) {
            if (this.properties == null) {
                this.properties = new HashMap<>();
            }
            this.properties.put(key, value);
            return this;
        }

        public Builder withProperties(Map<String, String> properties) {
            if (this.properties == null) {
                this.properties = new HashMap<>();
            }
            this.properties.putAll(properties);
            return this;
        }

        public Builder withPartitionMetadata(PartitionMetadata metadata) {
            if (this.partition == null) {
                this.partition = new ArrayList<>();
            }
            this.partition.add(metadata);
            return this;
        }

        public Builder withPartitionMetadata(List<PartitionMetadata> metadataList) {
            if (this.partition == null) {
                this.partition = new ArrayList<>();
            }
            this.partition.addAll(metadataList);
            return this;
        }

        public ObjectMetadata build() {
            return new ObjectMetadata(this.objectName, this.createDate, this.fileCount, this.createdBy, this.format,
                    this.baseLocation, this.dataLocation, this.updateDate, this.status, this.properties, this.partition,
                    this.files);
        }

    }

}
