package org.curiosity.rover.store;

import org.curiosity.rover.store.filter.Operator;
import org.curiosity.rover.store.filter.RecordVisitor;
import org.curiosity.rover.store.io.DataReader;
import org.curiosity.rover.store.io.DataReaderFactory;
import org.curiosity.rover.store.io.DataWriter;
import org.curiosity.rover.store.io.DataWriterFactory;
import org.curiosity.rover.store.record.Record;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueryStatement {

    private final ObjectStore store;
    private final String objectName;
    private final RecordVisitor visitor;

    public QueryStatement(ObjectStore store, String objectName) {
        this.objectName = objectName;
        this.store = store;
        this.visitor = new RecordVisitor();
    }

    public QueryResultIterator executeQuery() {
        Iterator<Record> elements = this.readFiles().iterator();
        return new QueryResultIterator(elements);
    }

    public QueryResultIterator executeQuery(Operator filter) {
        Predicate<Record> predicate = this.visitor.visit(filter).getPredicate();

        Iterator<Record> elements = this.readFiles().filter(predicate).iterator();
        return new QueryResultIterator(elements);
    }

    public <T> List<T> executeQuery(Function<Record, T> mapper) {
        return this.readFiles().map(mapper).collect(Collectors.toList());
    }

    public <T> List<T> executeQuery(Operator filter, Function<Record, T> mapper) {
        Predicate<Record> predicate = this.visitor.visit(filter).getPredicate();
        return this.readFiles().filter(predicate).map(mapper).collect(Collectors.toList());
    }

    public void insertRecord(Record record) {
        this.batchInsertRecord(Collections.singletonList(record));
    }

    public void batchInsertRecord(List<Record> records) {
        ObjectMetadata metadata = this.store.getObject(this.objectName);
        String fileName = this.generateFileName(metadata.getFormat());
        File dataFilePath = this.getDataFilePath(fileName);


        DataWriter writer = DataWriterFactory.getDataWriter(metadata.getFormat());
        writer.writeData(dataFilePath, records);

        FileMetadata fileMetadata = new FileMetadata.Builder()
                .withFilePath(dataFilePath.getAbsolutePath())
                .withRecordCount(records.size())
                .withCreateDate(LocalDateTime.now())
                .build();
        this.store.addFile(this.objectName, fileMetadata);

    }


    private File getDataFilePath(String fileName) {
        ObjectMetadata metadata = this.store.getObject(this.objectName);
        File dataFile = new File(metadata.getBasePath(), metadata.getObjectName());
        if (!dataFile.exists()) {
            dataFile.mkdirs();
        }
        File metadataFile = new File(dataFile, fileName);
        if (!metadataFile.exists()) {
            try {
                metadataFile.createNewFile();
            } catch (IOException e) {
                throw new IllegalStateException("Error occurred while creating data file", e);
            }
        }
        return metadataFile;
    }

    private String generateFileName(DataFormat format) {
        LocalDateTime dateTime = LocalDateTime.now();
        StringBuilder builder = new StringBuilder();
        builder.append(dateTime.getYear()).append("_")
                .append(dateTime.getMonthValue()).append("_")
                .append(dateTime.getDayOfMonth()).append("_")
                .append(dateTime.getHour()).append("_")
                .append(dateTime.getMinute()).append("_")
                .append(dateTime.getSecond())
                .append(".").append(format.getFormatName());
        return builder.toString();
    }

    private Stream<Record> readFiles() {
        ObjectMetadata metadata = this.store.getObject(this.objectName);
        DataReader reader = DataReaderFactory.getDataReader(metadata.getFormat());
        return metadata.getFiles().stream().flatMap(fileMetadata ->
                reader.readData(new File(fileMetadata.getFilePath())).stream()
        );
    }
}
