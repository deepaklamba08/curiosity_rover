package org.curiosity.rover.store;

import java.time.LocalDateTime;

public class FileMetadata {

    private final String filePath;
    private final long recordCount;
    private final LocalDateTime createDate;
    private final LocalDateTime updateDate;

    public FileMetadata(String filePath, long recordCount, LocalDateTime createDate, LocalDateTime updateDate) {
        this.filePath = filePath;
        this.recordCount = recordCount;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }
}
