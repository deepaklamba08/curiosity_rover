package org.curiosity.rover.store.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class FileMetadata {
    private final String fileName;
    private final String filePath;
    private final long recordCount;
    private final LocalDateTime createDate;
    private final LocalDateTime updateDate;
    private final String createdBy;
    private final boolean isCompact;
    private final PartitionSet partition;
    private final Map<String, FieldStats> fieldStats;

    public FileMetadata(String fileName, String filePath, long recordCount, LocalDateTime createDate,
            LocalDateTime updateDate, String createdBy, boolean isCompact, PartitionSet partition,
            Map<String, FieldStats> fieldStats) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.recordCount = recordCount;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.createdBy = createdBy;
        this.isCompact = isCompact;
        this.partition = partition;
        this.fieldStats = fieldStats;
    }

    public String getFileName() {
        return fileName;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public boolean isCompact() {
        return isCompact;
    }

    public PartitionSet getPartition() {
        return partition;
    }

    public String getFilePath() {
        return filePath;
    }

    public long getRecordCount() {
        return recordCount;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public Map<String, FieldStats> getFieldStats() {
        return fieldStats;
    }

    public static class Builder {
        private String fileName;
        private String filePath;
        private long recordCount;
        private LocalDateTime createDate;
        private LocalDateTime updateDate;
        private String createdBy;
        private boolean isCompact;
        private PartitionSet partition;
        private Map<String, FieldStats> fieldStats;

        public Builder withFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder withFilePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder withRecordCount(long recordCount) {
            this.recordCount = recordCount;
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

        public Builder withCreatedBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder makeCompact(boolean isCompact) {
            this.isCompact = isCompact;
            return this;
        }

        public Builder withPartition(PartitionSet partition) {
            this.partition = partition;
            return this;
        }

        public Builder withFieldStats(Map<String, FieldStats> fieldStats) {
            this.fieldStats = fieldStats;
            return this;
        }

        public Builder withFieldStat(String fieldName, FieldStats stats) {
            if (this.fieldStats == null) {
                this.fieldStats = new HashMap<>();
            }
            this.fieldStats.put(fieldName, stats);
            return this;
        }

        public FileMetadata build() {
            return new FileMetadata(
                    this.fileName, this.filePath, this.recordCount, this.createDate, this.updateDate, this.createdBy,
                    this.isCompact, this.partition, this.fieldStats);
        }
    }
}
