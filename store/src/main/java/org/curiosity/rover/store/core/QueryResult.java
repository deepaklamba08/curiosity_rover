package org.curiosity.rover.store.core;

import org.curiosity.rover.store.io.DataReader;
import org.curiosity.rover.store.model.FileMetadata;
import org.curiosity.rover.store.record.Record;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A ResultIterator that reads data files lazily and caches results locally.
 * Files are read sequentially only when needed to satisfy hasNext() or next().
 */
public class QueryResult implements ResultIterator {

    private final List<FileMetadata> files;
    private final DataReader reader;
    private final Predicate<Record> filter;
    private final List<Record> cache = new ArrayList<>();

    private int fileIndex = 0;
    private Iterator<Record> currentFileIterator = null;
    private Record nextRecord = null;

    public QueryResult(List<FileMetadata> files, DataReader reader, Predicate<Record> filter) {
        this.files = files != null ? files : Collections.emptyList();
        this.reader = reader;
        this.filter = filter;
    }

    @Override
    public boolean hasNext() {
        if (nextRecord != null) {
            return true;
        }
        return findNext();
    }

    @Override
    public Record next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        Record r = nextRecord;
        nextRecord = null;
        cache.add(r);
        return r;
    }

    private boolean findNext() {
        while (true) {
            if (currentFileIterator != null && currentFileIterator.hasNext()) {
                Record r = currentFileIterator.next();
                if (filter == null || filter.test(r)) {
                    nextRecord = r;
                    return true;
                }
                continue;
            }

            if (fileIndex >= files.size()) {
                return false;
            }

            FileMetadata fileMetadata = files.get(fileIndex++);
            Collection<Record> records = reader.readData(new File(fileMetadata.getFilePath()));
            currentFileIterator = records.iterator();
        }
    }

    @Override
    public void forEachRemaining(Consumer<? super Record> action) {
        while (hasNext()) {
            action.accept(next());
        }
    }
}
