package org.curiosity.rover.store;

import java.time.LocalDateTime;

public class FileMetadata {

    private final String filePath;
    private final long recordCount;
    private final LocalDateTime createDate;
    private final LocalDateTime updateDate;

    private FileMetadata(String filePath, long recordCount, LocalDateTime createDate, LocalDateTime updateDate) {
        this.filePath = filePath;
        this.recordCount = recordCount;
        this.createDate = createDate;
        this.updateDate = updateDate;
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
        private String filePath;
        private long recordCount;
        private LocalDateTime createDate;
        private LocalDateTime updateDate;

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

        public FileMetadata build() {
            return new FileMetadata(this.filePath, this.recordCount, this.createDate, this.updateDate);
        }
    }
}
