package org.curiosity.rover.store;

import java.util.Iterator;
import java.util.function.Consumer;

public class QueryResultIterator implements Iterator<Record> {
    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Record next() {
        return null;
    }

    @Override
    public void remove() {
        Iterator.super.remove();
    }

    @Override
    public void forEachRemaining(Consumer<? super Record> action) {
        Iterator.super.forEachRemaining(action);
    }
}
