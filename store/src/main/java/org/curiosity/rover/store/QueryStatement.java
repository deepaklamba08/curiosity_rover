package org.curiosity.rover.store;

import java.util.function.Function;
import java.util.function.Predicate;

public class QueryStatement {

    public QueryResultIterator executeQuery() {
        return null;
    }

    public QueryResultIterator executeQuery(Predicate<QueryResult> filter) {
        return null;
    }

    public <T> T executeQuery(Function<QueryResultIterator, T> mapper) {
        return null;
    }

    public <T> T executeQuery(Predicate<QueryResult> filter, Function<QueryResultIterator, T> mapper) {
        return null;
    }

}
