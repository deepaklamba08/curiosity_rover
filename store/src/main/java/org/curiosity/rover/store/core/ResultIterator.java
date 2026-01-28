package org.curiosity.rover.store.core;

import org.curiosity.rover.store.record.Record;

import java.util.function.Consumer;

public interface ResultIterator {


    public boolean hasNext();

    public Record next();

    public void forEachRemaining(Consumer<? super Record> action);
}
