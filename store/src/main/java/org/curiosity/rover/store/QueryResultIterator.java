package org.curiosity.rover.store;

import java.util.Iterator;
import java.util.function.Consumer;

public class QueryResultIterator implements Iterator<QueryResult> {
    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public QueryResult next() {
        return null;
    }

    @Override
    public void remove() {
        Iterator.super.remove();
    }

    @Override
    public void forEachRemaining(Consumer<? super QueryResult> action) {
        Iterator.super.forEachRemaining(action);
    }
}
