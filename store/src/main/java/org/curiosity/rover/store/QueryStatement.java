package org.curiosity.rover.store;

import org.curiosity.rover.store.filter.Operator;
import org.curiosity.rover.store.util.IOUtil;

import java.util.function.Function;
import java.util.function.Predicate;

public class QueryStatement {

    private final ObjectStore store;
    private final ObjectMetadata metadata;

    public QueryStatement(ObjectStore store, ObjectMetadata metadata) {
        this.metadata = metadata;
        this.store = store;
    }

    public QueryResultIterator executeQuery() {


        return null;
    }

    public QueryResultIterator executeQuery(Operator filter) {
        return null;
    }

    public <T> T executeQuery(Function<QueryResultIterator, T> mapper) {
        return null;
    }

    public <T> T executeQuery(Operator filter, Function<QueryResultIterator, T> mapper) {
        return null;
    }

}
