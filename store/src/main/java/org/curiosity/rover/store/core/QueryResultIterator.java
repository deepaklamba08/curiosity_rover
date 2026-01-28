package org.curiosity.rover.store.core;

import org.curiosity.rover.store.record.Record;

import java.util.Iterator;
import java.util.function.Consumer;

public class QueryResultIterator implements ResultIterator {

    private final Iterator<Record> iterator;

    public QueryResultIterator(Iterator<Record> iterator) {
        this.iterator = iterator;
    }


    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Record next() {
        return iterator.next();
    }

    @Override
    public void forEachRemaining(Consumer<? super Record> action) {
        iterator.forEachRemaining(action);
    }
}
