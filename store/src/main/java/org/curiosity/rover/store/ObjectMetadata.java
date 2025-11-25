package org.curiosity.rover.store;

import java.time.LocalDateTime;
import java.util.List;

public class ObjectMetadata {

    private final String objectName;
    private final String basePath;
    private final long fileCount;
    private final LocalDateTime createDate;
    private final LocalDateTime updateDate;
    private final List<FileMetadata> files;

    public ObjectMetadata(String objectName, String basePath, long fileCount, LocalDateTime createDate, LocalDateTime updateDate, List<FileMetadata> files) {
        this.objectName = objectName;
        this.basePath = basePath;
        this.fileCount = fileCount;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.files = files;
    }
}
