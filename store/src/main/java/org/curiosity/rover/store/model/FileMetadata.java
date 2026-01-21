package org.curiosity.rover.store.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileMetadata {
    private final String fileName;
    private final String filePath;
    private final long recordCount;
    private final LocalDateTime createDate;
    private final LocalDateTime updateDate;
    private final String createdBy;
    private final boolean isCompact;
    private final List<PartitionDetail> partitionDetails;

    public FileMetadata(String fileName, String filePath, long recordCount, LocalDateTime createDate, LocalDateTime updateDate, String createdBy, boolean isCompact, List<PartitionDetail> partitionDetails) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.recordCount = recordCount;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.createdBy = createdBy;
        this.isCompact = isCompact;
        this.partitionDetails = partitionDetails;
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

    public List<PartitionDetail> getPartitionDetails() {
        return partitionDetails;
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

    public static class Builder {
        private String fileName;
        private String filePath;
        private long recordCount;
        private LocalDateTime createDate;
        private LocalDateTime updateDate;
        private String createdBy;
        private boolean isCompact;
        private List<PartitionDetail> partitionDetails;

        public Builder withFileName(String fileName) {
            this.filePath = fileName;
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
            this.filePath = createdBy;
            return this;
        }

        public Builder makeCompact(boolean isCompact) {
            this.isCompact = isCompact;
            return this;
        }

        public Builder withPartitionDetail(PartitionDetail partitionDetail) {
            if (this.partitionDetails == null) {
                this.partitionDetails = new ArrayList<>();
            }
            this.partitionDetails.add(partitionDetail);
            return this;
        }

        public FileMetadata build() {
            return new FileMetadata(
                    this.fileName, this.filePath, this.recordCount, this.createDate, this.updateDate, this.createdBy, this.isCompact, this.partitionDetails
            );
        }
    }
}
