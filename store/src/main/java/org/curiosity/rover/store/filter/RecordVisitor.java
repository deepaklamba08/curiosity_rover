package org.curiosity.rover.store.filter;

import org.curiosity.rover.store.record.Record;
import org.curiosity.rover.store.value.Value;

import java.util.function.Predicate;

public class RecordVisitor implements Visitor {


    @Override
    public RecordExpression visit(Operator operator) {

        if (operator instanceof LogicalOperator) {
            return this.parseLogicalOperator((LogicalOperator) operator);
        } else if (operator instanceof RelationalOperator) {
            return this.parseRelationalOperator((RelationalOperator) operator);
        } else {
            throw new IllegalStateException("Unknown operator- " + operator);
        }
    }

    private RecordExpression parseLogicalOperator(LogicalOperator operator) {
        if (operator instanceof LogicalOperator.And) {
            return operator.getOperators().stream().map(this::visit).reduce((e1, e2) -> new RecordExpression(e1.getPredicate().and(e2.getPredicate()))).orElseGet(null);
        } else if (operator instanceof LogicalOperator.Or) {
            return operator.getOperators().stream().map(this::visit).reduce((e1, e2) -> new RecordExpression(e1.getPredicate().or(e2.getPredicate()))).orElseGet(null);
        } else {
            throw new IllegalStateException("Unknown operator- " + operator);
        }
    }

    private RecordExpression parseRelationalOperator(RelationalOperator operator) {
        String key = operator.getKey();
        Value value = operator.getValue();
        Predicate<Record> predicate = (record ->
                record.hasField(key) &&
                        compareValues(record.getValue(key), value, operator.getClass()));
        return new RecordExpression(predicate);
    }

    private boolean compareValues(Value source, Value target, Class<? extends RelationalOperator> comp) {
        if (source == null) {
            return false;
        }

        if (comp.equals(RelationalOperator.Eq.class)) {
            return source.equals(target);
        } else if (comp.equals(RelationalOperator.NotEq.class)) {
            return !source.equals(target);
        } else if (comp.equals(RelationalOperator.Gt.class)) {
            return source.gt(target);
        } else if (comp.equals(RelationalOperator.GtEq.class)) {
            return source.gtEq(target);
        } else if (comp.equals(RelationalOperator.Lt.class)) {
            return source.lt(target);
        } else if (comp.equals(RelationalOperator.LtEq.class)) {
            return source.ltEq(target);
        } else {
            throw new IllegalStateException("Unknown operator- " + comp);
        }
    }
}
