package org.curiosity.rover.store.core;

import org.curiosity.rover.store.filter.RelationalOperator;
import org.curiosity.rover.store.model.*;
import org.curiosity.rover.store.util.IOUtil;
import org.curiosity.rover.store.util.StoreConstants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ObjectStore {

    private final File baseDirectory;

    public ObjectStore(String directory) {
        this.baseDirectory = new File(directory, StoreConstants.DB_FOLDER_NAME);

        if (!this.baseDirectory.exists()) {
            this.baseDirectory.mkdirs();
        }

    }

    public void registerObject(String objectName, Map<String, String> properties, List<PartitionMetadata> partitions) {
        ObjectMetadata existing = this.readObjectMetadata(objectName);
        if (existing != null) {
            throw new IllegalArgumentException("Object already exists- " + objectName);
        }
        File baseLocation = getObjectBaseLocation(objectName);
        File dataLocation = getObjectDataLocation(objectName);
        ObjectMetadata metadata = new ObjectMetadata.Builder()
                .withObjectName(objectName)
                .withBaseLocation(baseLocation.getAbsolutePath())
                .withDataLocation(dataLocation.getAbsolutePath())
                .withDataFormat(DataFormat.JSON)
                .withCreateDate(LocalDateTime.now())
                .withFileCount(0)
                .withStatus(true)
                .withProperties(properties)
                .build();

        int baseVersion = 0;
        if (!baseLocation.exists()) {
            baseLocation.mkdirs();
        }
        if (!dataLocation.exists()) {
            dataLocation.mkdirs();
        }
        File metadataLocation = getMetadataFileLocation(objectName, baseVersion);
        IOUtil.writeFile(metadataLocation, MapperUtil::convertObjectMetadata, metadata);

        VersionMetadata versionMetadata = new VersionMetadata.Builder()
                .withVersion(baseVersion)
                .withCreateDate(LocalDateTime.now())
                .build();

        File versionLocation = getVersionFileLocation(objectName);
        IOUtil.writeFile(versionLocation, MapperUtil::convertVersionMetadata, versionMetadata);

    }

    private File getObjectDataLocation(String objectName) {
        File baseLocation = getObjectBaseLocation(objectName);
        return new File(baseLocation, StoreConstants.DATA_FOLDER_NAME);

    }

    public ObjectMetadata getObject(String objectName) {
        return this.readObjectMetadata(objectName);
    }

    public QueryStatement queryObject(String name) {
        // Implementation for querying an object by name
        ObjectMetadata existing = this.readObjectMetadata(name);
        if (existing == null) {
            throw new IllegalArgumentException("Object not exists- " + name);
        }
        return new QueryStatement(this, existing.getObjectName());
    }

    public void addFile(String objectName, FileMetadata fileMetadata) {
        ObjectMetadata existing = this.readObjectMetadata(objectName);

        if (fileMetadata == null) {
            throw new IllegalArgumentException("File metadata can not be null");
        }
        if (existing == null) {
            throw new IllegalArgumentException("Object not exists- " + objectName);
        }
        List<FileMetadata> files;
        if (existing.getFiles() != null) {
            files = new ArrayList<>(existing.getFiles());
        } else {
            files = new ArrayList<>(1);
        }
        files.add(fileMetadata);

        ObjectMetadata newMetadata = new ObjectMetadata.Builder()
                .withBaseLocation(existing.getBaseLocation())
                .withProperties(existing.getProperties())
                .withCreateDate(existing.getCreateDate())
                .withFileCount(files.size())
                .withObjectName(existing.getObjectName())
                .withUpdateDate(LocalDateTime.now())
                .withFileMetadata(files)
                .withDataFormat(existing.getFormat())
                .build();
        this.updateObjectMetadata(newMetadata);
    }

    public void rollbackToVersion(String objectName, int version) {
        if (objectName == null || objectName.isEmpty() || version < 0) {
            throw new IllegalArgumentException("Invalid object name or version number");
        }

        ObjectMetadata objectMetadata = this.getObject(objectName);
        if (objectMetadata == null) {
            throw new IllegalArgumentException("Object not exists- " + objectName);
        }

        List<Integer> versions = this.listAllVersionNumbers(objectName);
        if (versions == null || versions.isEmpty()) {
            throw new IllegalArgumentException("Versions not found for object");
        }

        Optional<Integer> exists = versions.stream().filter(v -> v == version).findAny();
        if (!exists.isPresent()) {
            throw new IllegalArgumentException("Version not present in available versions");
        }

        if (versions.size() == 1) {
            throw new IllegalArgumentException("Can not rollback only available version");
        }


    }


    private ObjectMetadata readObjectMetadata(String name) {
        return this.readObjectMetadata().stream().filter(element -> element.getObjectName().equals(name))
                .findAny().orElse(null);
    }

    private List<ObjectMetadata> readObjectMetadata() {
        File[] baseFiles = this.baseDirectory.listFiles();
        if (baseFiles == null || baseFiles.length == 0) {
            return Collections.emptyList();
        }
        List<File> dataFiles = Arrays.asList(baseFiles);
        dataFiles = dataFiles.stream().filter(file -> !file.isFile()).collect(Collectors.toList());
        List<ObjectMetadata> metadataElements = new ArrayList<>(dataFiles.size());
        for (File dataFile : dataFiles) {
            String objectName = dataFile.getName();
            File versionFileLocation = getVersionFileLocation(objectName);
            if (!versionFileLocation.exists()) {
                continue;
            }
            int currentVersion = getCurrentVersion(objectName);
            File metadataFileLocation = getMetadataFileLocation(objectName, currentVersion);
            if (!metadataFileLocation.exists()) {
                continue;
            }
            List<ObjectMetadata> metadataList = IOUtil.readFile(metadataFileLocation, MapperUtil::mapObjectMetadata)
                    .stream().filter(ObjectMetadata::isStatus)
                    .collect(Collectors.toList());
            metadataElements.addAll(metadataList);
        }
        return metadataElements;
    }

    private void updateObjectMetadata(ObjectMetadata metadata) {
        List<ObjectMetadata> existingElements = this.readObjectMetadata().stream()
                .filter(element -> !element.getObjectName().equals(metadata.getObjectName()))
                .collect(Collectors.toList());
        existingElements.add(metadata);
        this.overwriteMetadata(existingElements);
    }

    private void overwriteMetadata(List<ObjectMetadata> elements) {
        elements.stream().collect(Collectors.toMap(ObjectMetadata::getObjectName, Function.identity()))
                .forEach((objectName, objectMetadata) -> {
                    int beforeVersion = getCurrentVersion(objectName);
                    int nextVersion = beforeVersion + 1;
                    File metadataLocation = getMetadataFileLocation(objectName, nextVersion);
                    IOUtil.writeFile(metadataLocation, MapperUtil::convertObjectMetadata, objectMetadata);

                    int afterVersion = getCurrentVersion(objectName);
                    if (beforeVersion != afterVersion) {
                        throw new IllegalStateException("Version mismatch: " + beforeVersion + " != " + afterVersion);
                    }
                    updateMetadataVersion(nextVersion, objectName);
                });
    }

    private void updateMetadataVersion(int nextVersion, String objectName) {
        VersionMetadata nextVersionMetadata = new VersionMetadata.Builder()
                .withVersion(nextVersion)
                .withCreateDate(LocalDateTime.now())
                .build();
        File versionLocation = getVersionFileLocation(objectName);
        File versionTempLocation = getTempVersionFile(objectName);

        IOUtil.writeFile(versionTempLocation, MapperUtil::convertVersionMetadata, nextVersionMetadata);
        try {
            Files.move(
                    versionTempLocation.toPath(),
                    versionLocation.toPath(),
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private File getTempVersionFile(String objectName) {
        File baseLocation = getObjectBaseLocation(objectName);
        String versionFile = StoreConstants.VERSION_FILE_NAME +
                "_" +
                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) +
                ".json";
        return new File(baseLocation, versionFile);
    }

    private int getCurrentVersion(String objectName) {
        return readCurrentVersion(objectName).getVersion();
    }

    private VersionMetadata readCurrentVersion(String objectName) {
        File versionFileLocation = getVersionFileLocation(objectName);
        List<VersionMetadata> metadata = IOUtil.readFile(versionFileLocation, MapperUtil::mapVersionMetadata);
        return metadata.get(0);
    }

    private List<Integer> listAllVersionNumbers(String objectName) {
        File objectBaseLocation = getObjectBaseLocation(objectName);
        File[] childFiles = objectBaseLocation.listFiles();
        if (childFiles == null || childFiles.length == 0) {
            return null;
        }

        return Arrays.stream(childFiles).map(File::getName)
                .filter(name -> name.startsWith(StoreConstants.STORE_FILE_NAME))
                .map(fileName -> fileName.replace(StoreConstants.STORE_FILE_NAME, "")
                        .replace("_V", "")
                        .replace(".json", ""))
                .map(Integer::parseInt)
                .sorted(Comparator.comparingInt(a -> a))
                .collect(Collectors.toList());
    }

    private File getObjectBaseLocation(String objectName) {
        return new File(this.baseDirectory, objectName);
    }

    private File getVersionFileLocation(String objectName) {
        String versionFile = StoreConstants.VERSION_FILE_NAME + ".json";
        File objectBaseLocation = getObjectBaseLocation(objectName);
        return new File(objectBaseLocation, versionFile);
    }

    private File getMetadataFileLocation(String objectName, int versionNo) {
        String versionFile = StoreConstants.STORE_FILE_NAME +
                "_V" +
                versionNo +
                ".json";
        File baseLocation = getObjectBaseLocation(objectName);
        return new File(baseLocation, versionFile);
    }
}
