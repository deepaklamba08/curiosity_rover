package org.curiosity.rover.store.core;

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
                .withPartitionMetadata(partitions)
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

    public void addFiles(String objectName, List<FileMetadata> files) {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("File metadata can not be null");
        }
        ObjectMetadata existing = this.readObjectMetadata(objectName);

        if (existing == null) {
            throw new IllegalArgumentException("Object not exists- " + objectName);
        }
        List<FileMetadata> existingFiles = existing.getFiles();
        if (existingFiles == null) {
            existingFiles = new ArrayList<>(files.size());
        }
        existingFiles.addAll(files);

        ObjectMetadata newMetadata = new ObjectMetadata.Builder()
                .withObjectName(existing.getObjectName())
                .withBaseLocation(existing.getBaseLocation())
                .withDataLocation(existing.getDataLocation())
                .withProperties(existing.getProperties())
                .withCreateDate(existing.getCreateDate())
                .withFileCount(existingFiles.size())
                .withUpdateDate(LocalDateTime.now())
                .withFileMetadata(existingFiles)
                .withDataFormat(existing.getFormat())
                .withPartitionMetadata(existing.getPartition())
                .withStatus(existing.isStatus())
                .build();
        this.updateObjectMetadata(newMetadata);
    }

    public void addFile(String objectName, FileMetadata fileMetadata) {
        this.addFiles(objectName, Collections.singletonList(fileMetadata));
    }

    public void rollbackToVersion(String objectName, int toVersion) {
        if (objectName == null || objectName.isEmpty() || toVersion < 0) {
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

        Optional<Integer> exists = versions.stream().filter(v -> v == toVersion).findAny();
        if (!exists.isPresent()) {
            throw new IllegalArgumentException("Version not present in available versions");
        }

        if (versions.size() == 1) {
            throw new IllegalArgumentException("Can not rollback only available version");
        }

        this.updateMetadataVersion(toVersion, objectName);
    }

    public void deleteVersion(String objectName, int deleteVersion) {
        if (objectName == null || objectName.isEmpty() || deleteVersion < 0) {
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

        Optional<Integer> exists = versions.stream().filter(v -> v == deleteVersion).findAny();
        if (!exists.isPresent()) {
            throw new IllegalArgumentException("Version not present in available versions");
        }

        if (versions.size() == 1) {
            throw new IllegalArgumentException("Can not rollback only available version");
        }

        int lastVersion = versions.get(versions.size() - 1);
        if (deleteVersion == lastVersion) {
            int newVersion = versions.get(versions.size() - 2);
            this.updateMetadataVersion(newVersion, objectName);
        }
        Map<Integer, ObjectMetadata> versionsMap = this.getAllVersions(objectName);
        ObjectMetadata deleteVerObject = versionsMap.get(deleteVersion);
        Map<String, FileMetadata> deleteVerFiles = deleteVerObject.getFiles()
                .stream()
                .collect(Collectors.toMap(FileMetadata::getFilePath, Function.identity()));

        versions.stream()
                .filter(v -> v != deleteVersion)
                .map(versionsMap::get)
                .filter(Objects::nonNull)
                .filter(objMeta -> objMeta.getFiles() != null && !objMeta.getFiles().isEmpty())
                .flatMap(objMeta -> objMeta.getFiles().stream())
                .forEach(file -> {
                    FileMetadata existingFile = deleteVerFiles.get(file.getFilePath());
                    if (existingFile != null) {
                        deleteVerFiles.remove(file.getFilePath());
                    }
                });
        if (!deleteVerFiles.isEmpty()) {
            deleteVerFiles.values().forEach(fileMetadata -> {
                File fileLocation = new File(fileMetadata.getFilePath());
                if (fileLocation.exists()) {
                    fileLocation.delete();
                }
            });
        }
        File deleteMetaFile = this.getMetadataFileLocation(objectName, deleteVersion);
        if (deleteMetaFile.exists()) {
            deleteMetaFile.delete();
        }
    }

    public ObjectMetadata getObjectByVersion(String objectName, int version) {
        File objectBaseLocation = getObjectBaseLocation(objectName);
        File[] childFiles = objectBaseLocation.listFiles();
        if (childFiles == null || childFiles.length == 0) {
            return null;
        }

        return Arrays.stream(childFiles)
                .map(File::getName)
                .filter(fileName -> fileName.startsWith(StoreConstants.STORE_FILE_NAME))
                .map(ObjectStore::getVersionFromFileName)
                .map(v -> getMetadataFileLocation(objectName, v))
                .flatMap(fileLocation -> IOUtil.readFile(fileLocation, MapperUtil::mapObjectMetadata).stream())
                .findFirst()
                .get();


    }

    public Map<Integer, ObjectMetadata> getAllVersions(String objectName) {
        File objectBaseLocation = getObjectBaseLocation(objectName);
        File[] childFiles = objectBaseLocation.listFiles();
        if (childFiles == null || childFiles.length == 0) {
            return null;
        }

        return Arrays.stream(childFiles)
                .filter(fileLocation -> fileLocation.getName().startsWith(StoreConstants.STORE_FILE_NAME))
                .collect(Collectors.toMap(fileLocation ->
                                getVersionFromFileName(fileLocation.getName())
                        , fileLocation -> {
                            List<ObjectMetadata> metadataList = IOUtil.readFile(fileLocation, MapperUtil::mapObjectMetadata);
                            return metadataList.get(0);
                        }));
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
                .map(ObjectStore::getVersionFromFileName)
                .sorted(Comparator.comparingInt(a -> a))
                .collect(Collectors.toList());
    }

    private static int getVersionFromFileName(String fileName) {
        String versionNum = fileName.replace(StoreConstants.STORE_FILE_NAME, "")
                .replace("_V", "")
                .replace(".json", "");
        return Integer.parseInt(versionNum);
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
