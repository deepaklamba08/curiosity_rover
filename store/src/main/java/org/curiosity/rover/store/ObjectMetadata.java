package org.curiosity.rover.store;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectMetadata {

    private final String objectName;
    private final String basePath;
    private final long fileCount;
    private final LocalDateTime createDate;
    private final LocalDateTime updateDate;
    private final List<FileMetadata> files;
    private final Map<String, String> properties;

    private ObjectMetadata(String objectName, String basePath, long fileCount, LocalDateTime createDate, LocalDateTime updateDate, List<FileMetadata> files, Map<String, String> properties) {
        this.objectName = objectName;
        this.basePath = basePath;
        this.fileCount = fileCount;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.files = files;
        this.properties = properties;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getBasePath() {
        return basePath;
    }

    public long getFileCount() {
        return fileCount;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public List<FileMetadata> getFiles() {
        return files;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public static class Builder {
        private String objectName;
        private String basePath;
        private long fileCount;
        private LocalDateTime createDate;
        private LocalDateTime updateDate;
        private List<FileMetadata> files;
        private Map<String, String> properties;

        public Builder withObjectName(String objectName) {
            this.objectName = objectName;
            return this;
        }

        public Builder withBasePath(String basePath) {
            this.basePath = basePath;
            return this;
        }

        public Builder withFileCount(long fileCount) {
            this.fileCount = fileCount;
            return this;
        }

        public Builder withCreateDate(LocalDateTime createDate) {
            this.createDate = createDate;
            return this;
        }

        public Builder withUpdateDate(LocalDateTime updateDate) {
            this.updateDate = updateDate;
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

        public Builder withProperties(Map<String,String> properties) {
            if (this.properties == null) {
                this.properties = new HashMap<>();
            }
            this.properties.putAll(properties);
            return this;
        }


        public ObjectMetadata build() {
            return new ObjectMetadata(this.objectName, this.basePath, this.fileCount, this.createDate, this.updateDate, this.files, this.properties);
        }

    }

}
