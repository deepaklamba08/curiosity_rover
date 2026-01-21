package org.curiosity.rover.store.model;

import java.time.LocalDateTime;

public class VersionMetadata {
    private final int version;
    private final LocalDateTime createDate;

    public VersionMetadata(int version, LocalDateTime createDate) {
        this.version = version;
        this.createDate = createDate;
    }

    public int getVersion() {
        return version;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public static class Builder {
        private int version;
        private LocalDateTime createDate;

        public Builder() {
        }

        public Builder withVersion(int version) {
            this.version = version;
            return this;
        }

        public Builder withCreateDate(LocalDateTime createDate) {
            this.createDate = createDate;
            return this;
        }

        public VersionMetadata build() {
            return new VersionMetadata(this.version, this.createDate);
        }
    }
}
