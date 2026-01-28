package org.curiosity.rover.store.core;

import org.curiosity.rover.store.filter.FileVisitor;
import org.curiosity.rover.store.filter.Operator;
import org.curiosity.rover.store.filter.RecordVisitor;
import org.curiosity.rover.store.io.DataReader;
import org.curiosity.rover.store.io.DataReaderFactory;
import org.curiosity.rover.store.io.DataWriter;
import org.curiosity.rover.store.io.DataWriterFactory;
import org.curiosity.rover.store.model.*;
import org.curiosity.rover.store.stats.FieldStatsCalculator;
import org.curiosity.rover.store.record.Record;
import org.curiosity.rover.store.value.IntegerValue;
import org.curiosity.rover.store.value.StringValue;
import org.curiosity.rover.store.value.Value;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueryStatement {

    private final ObjectStore store;
    private final String objectName;
    private final RecordVisitor visitor;
    private final FileVisitor fileVisitor;

    public QueryStatement(ObjectStore store, String objectName) {
        this.objectName = objectName;
        this.store = store;
        this.visitor = new RecordVisitor();
        this.fileVisitor = new FileVisitor();
    }

    public QueryResultIterator executeQuery() {
        Iterator<Record> elements = this.readFiles(Optional.empty()).iterator();
        return new QueryResultIterator(elements);
    }

    public QueryResultIterator executeQuery(Operator filter) {
        Predicate<Record> predicate = this.visitor.visit(filter).getPredicate();
        Iterator<Record> elements = this.readFiles(Optional.of(filter)).filter(predicate).iterator();
        return new QueryResultIterator(elements);
    }

    public <T> List<T> executeQuery(Function<Record, T> mapper) {
        return this.readFiles(Optional.empty()).map(mapper).collect(Collectors.toList());
    }

    public <T> List<T> executeQuery(Operator filter, Function<Record, T> mapper) {
        Predicate<Record> predicate = this.visitor.visit(filter).getPredicate();
        return this.readFiles(Optional.of(filter)).filter(predicate).map(mapper).collect(Collectors.toList());
    }

    public void insertRecord(Record record) {
        this.batchInsertRecord(Collections.singletonList(record));
    }

    public void batchInsertRecord(List<Record> records) {
        ObjectMetadata metadata = this.store.getObject(this.objectName);
        if (metadata == null) {
            throw new IllegalStateException("Object " + this.objectName + " does not exist in the store.");
        }
        DataWriter writer = DataWriterFactory.getDataWriter(metadata.getFormat());
        List<FileMetadata> files;
        if (metadata.isPartitioned()) {
            List<PartitionMetadata> partitions = metadata.getPartition();
            Map<PartitionSet, List<Record>> partitionedRecords = this.aggregateRecordsByPartitions(records, partitions);
            files = partitionedRecords.entrySet().stream().map(entry -> {
                PartitionSet partition = entry.getKey();
                return writeDataToFile(metadata, writer, entry.getValue(),
                        Optional.of(partition), false);
            }).collect(Collectors.toList());
        } else {
            files = Collections.singletonList(this.writeDataToFile(metadata,
                    writer, records, Optional.empty(), false));
        }
        this.store.addFiles(this.objectName, files);

    }

    public void runCompaction() {
        if (objectName == null || objectName.isEmpty()) {
            throw new IllegalArgumentException("Invalid object name");
        }

        ObjectMetadata metadata = this.store.getObject(objectName);
        if (metadata == null) {
            throw new IllegalArgumentException("Object not exists- " + objectName);
        }

        List<FileMetadata> files = metadata.getFiles();
        if (files == null || files.isEmpty() || files.size() == 1) {
            return;
        }
        DataReader reader = DataReaderFactory.getDataReader(metadata.getFormat());
        DataWriter writer = DataWriterFactory.getDataWriter(metadata.getFormat());

        List<FileMetadata> compactFiles = new ArrayList<>();
        if (metadata.isPartitioned()) {
            Map<PartitionSet, List<FileMetadata>> partitionFileMap = files
                    .stream()
                    .collect(Collectors.groupingBy(FileMetadata::getPartition))
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().size() > 1)
                    .collect(Collectors.toMap(
                            entry -> entry.getKey(),
                            entry -> entry.getValue()));

            partitionFileMap.forEach((partitionSet, metadataFiles) -> {
                List<Record> records = metadataFiles
                        .stream()
                        .filter(file -> file.getRecordCount() > 0)
                        .flatMap(file -> reader.readData(new File(file.getFilePath())).stream())
                        .collect(Collectors.toList());

                if (records.size() > 0) {
                    FileMetadata compactFile = writeDataToFile(metadata, writer,
                            records, Optional.of(partitionSet), true);
                    compactFiles.add(compactFile);
                }
            });
        } else {
            List<Record> records = files
                    .stream()
                    .filter(file -> file.getRecordCount() > 0)
                    .flatMap(file -> reader.readData(new File(file.getFilePath())).stream())
                    .collect(Collectors.toList());
            if (records.size() > 0) {
                FileMetadata compactFile = writeDataToFile(metadata, writer, records, Optional.empty(), true);
                compactFiles.add(compactFile);
            }
        }

        ObjectMetadata newMetadata = new ObjectMetadata.Builder()
                .withObjectName(metadata.getObjectName())
                .withBaseLocation(metadata.getBaseLocation())
                .withDataLocation(metadata.getDataLocation())
                .withProperties(metadata.getProperties())
                .withCreateDate(metadata.getCreateDate())
                .withFileCount(compactFiles.size())
                .withUpdateDate(LocalDateTime.now())
                .withFileMetadata(compactFiles)
                .withDataFormat(metadata.getFormat())
                .withPartitionMetadata(metadata.getPartition())
                .withStatus(metadata.isStatus())
                .build();

        this.store.updateObjectMetadata(newMetadata);

    }

    private FileMetadata writeDataToFile(ObjectMetadata metadata, DataWriter writer,
            List<Record> records, Optional<PartitionSet> partition, boolean isCompact) {
        String dataDirLocation = metadata.getDataLocation();
        DataFormat format = metadata.getFormat();
        File dataFilePath = this.generateDataFilePath(dataDirLocation, format, partition, isCompact);
        if (!dataFilePath.getParentFile().exists()) {
            dataFilePath.getParentFile().mkdirs();
        }
        writer.writeData(dataFilePath, records);
        FileMetadata.Builder builder = new FileMetadata.Builder()
                .withFileName(dataFilePath.getName())
                .withCreateDate(LocalDateTime.now())
                .makeCompact(isCompact)
                .withFilePath(dataFilePath.getAbsolutePath())
                .withCreatedBy("system")
                .withRecordCount(records.size());
        if (partition.isPresent()) {
            builder.withPartition(partition.get());
        }

        // Calculate field statistics if enabled
        if (metadata.isEnableStats() && records != null && !records.isEmpty()) {
            FieldStatsCalculator calculator = new FieldStatsCalculator();
            Map<String, FieldStats> fieldStats = calculator.calculateAllStats(records);
            builder.withFieldStats(fieldStats);
        }

        return builder.build();
    }

    private Map<PartitionSet, List<Record>> aggregateRecordsByPartitions(List<Record> records,
            List<PartitionMetadata> partitions) {
        Map<PartitionSet, List<Record>> aggRecords = new HashMap<>();
        for (Record record : records) {
            PartitionSet partitionSet = this.classifyRecord(record, partitions);
            List<Record> pRecords = aggRecords.get(partitionSet);
            if (pRecords == null) {
                pRecords = new ArrayList<>();
                aggRecords.put(partitionSet, pRecords);
            }
            pRecords.add(record);
        }
        return aggRecords;
    }

    private PartitionSet classifyRecord(Record record, List<PartitionMetadata> partitions) {
        List<PartitionDetail> pDetails = partitions.stream().map(partition -> {
            Value value = record.getValue(partition.getColumn());
            if (value == null) {
                return new PartitionDetail(partition, "null");
            } else if (value instanceof StringValue) {
                return new PartitionDetail(partition, ((StringValue) value).getValue());
            } else if (value instanceof IntegerValue) {
                return new PartitionDetail(partition, String.valueOf(((IntegerValue) value).getValue()));
            } else {
                throw new IllegalArgumentException("Unsupported partition value type: " + value.getClass().getName());
            }
        }).collect(Collectors.toList());

        return new PartitionSet(pDetails);
    }

    public void importFile(String filePath) {
        ObjectMetadata metadata = this.store.getObject(this.objectName);
        if (metadata == null) {
            throw new IllegalStateException("Object " + this.objectName + " does not exist in the store.");
        }
        DataReader reader = DataReaderFactory.getDataReader(metadata.getFormat());

        Collection<Record> records = reader.readData(new File(filePath));
        FileMetadata fileMetadata = new FileMetadata.Builder()
                .withFilePath(filePath)
                .withRecordCount(records.size())
                .withCreateDate(LocalDateTime.now())
                .build();
        this.store.addFile(this.objectName, fileMetadata);
    }

    private File generateDataFilePath(String dataDirLocation, DataFormat format, Optional<PartitionSet> partition,
            boolean isCompact) {
        LocalDateTime dateTime = LocalDateTime.now();
        StringBuilder fileName = new StringBuilder();
        if (isCompact) {
            fileName.append("compact_");
        }
        fileName.append(dateTime.getYear()).append("_")
                .append(dateTime.getMonthValue()).append("_")
                .append(dateTime.getDayOfMonth()).append("_")
                .append(dateTime.getHour()).append("_")
                .append(dateTime.getMinute()).append("_")
                .append(dateTime.getSecond()).append("_")
                .append(dateTime.getNano())
                .append(".").append(format.getFormatName());

        if (partition.isPresent()) {
            PartitionSet partitionSet = partition.get();
            List<PartitionDetail> partitionDetails = partitionSet.getPartitions().stream()
                    .sorted(Comparator.comparing(o -> o.getMetadata().getColumn()))
                    .collect(Collectors.toList());
            StringBuilder pPath = new StringBuilder();
            partitionDetails.forEach(pDetail -> {
                pPath.append(pDetail.getMetadata().getColumn())
                        .append("=")
                        .append(pDetail.getValue())
                        .append(File.separator);
            });
            pPath.append(fileName);

            return new File(dataDirLocation, pPath.toString());
        } else {
            return new File(dataDirLocation, fileName.toString());
        }
    }

    private Stream<Record> readFiles(Optional<Operator> filter) {
        ObjectMetadata metadata = this.store.getObject(this.objectName);
        if (metadata == null) {
            throw new IllegalStateException("Object " + this.objectName + " does not exist in the store.");
        }
        if (metadata.getFiles() == null || metadata.getFiles().isEmpty()) {
            return Stream.empty();
        }
        List<FileMetadata> selectedFiles;
        if (metadata.isPartitioned() && filter.isPresent()) {
            selectedFiles = this.filterFiles(filter.get(), metadata.getFiles());
        } else {
            selectedFiles = metadata.getFiles();
        }
        if (selectedFiles == null || selectedFiles.isEmpty()) {
            return Stream.empty();
        }

        DataReader reader = DataReaderFactory.getDataReader(metadata.getFormat());
        return selectedFiles
                .stream()
                .map(FileMetadata::getFilePath)
                .distinct()
                .flatMap(path -> reader.readData(new File(path)).stream());
    }

    private List<FileMetadata> filterFiles(Operator operator, List<FileMetadata> files) {
        Predicate<FileMetadata> predicate = fileVisitor.visit(operator).getPredicate();
        return files.stream().filter(file -> predicate.test(file)).collect(Collectors.toList());
    }
}
