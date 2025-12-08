package org.curiosity.rover.store.filter;

import org.curiosity.rover.store.record.Record;

import java.util.function.Predicate;

public class RecordExpression implements Expression {

    private final Predicate<Record> predicate;

    public RecordExpression(Predicate<Record> predicate) {
        this.predicate = predicate;
    }

    public Predicate<Record> getPredicate() {
        return predicate;
    }
}
