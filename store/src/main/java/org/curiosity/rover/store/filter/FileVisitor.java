package org.curiosity.rover.store.filter;

import org.curiosity.rover.store.model.FileMetadata;
import org.curiosity.rover.store.model.PartitionDetail;
import org.curiosity.rover.store.model.PartitionSet;
import org.curiosity.rover.store.value.IntegerValue;
import org.curiosity.rover.store.value.StringValue;
import org.curiosity.rover.store.value.Value;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FileVisitor implements Visitor {

    public FileVisitor() {
    }

    @Override
    public FileExpression visit(Operator operator) {
        if (operator instanceof LogicalOperator) {
            return this.parseLogicalOperator((LogicalOperator) operator);
        } else if (operator instanceof RelationalOperator) {
            return this.parseRelationalOperator((RelationalOperator) operator);
        } else {
            throw new IllegalStateException("Unknown operator- " + operator);
        }
    }

    private FileExpression parseLogicalOperator(LogicalOperator operator) {
        if (operator instanceof LogicalOperator.And) {
            return operator.getOperators().stream().map(this::visit)
                    .reduce((e1, e2) -> new FileExpression(e1.getPredicate().and(e2.getPredicate()))).orElseGet(null);
        } else if (operator instanceof LogicalOperator.Or) {
            return operator.getOperators().stream().map(this::visit)
                    .reduce((e1, e2) -> new FileExpression(e1.getPredicate().or(e2.getPredicate()))).orElseGet(null);
        } else {
            throw new IllegalStateException("Unknown operator- " + operator);
        }
    }

    private FileExpression parseRelationalOperator(RelationalOperator operator) {
        String key = operator.getKey();
        Value value = operator.getValue();
        Predicate<FileMetadata> predicate = (file -> compareFile(file.getPartition(), key, value,
                RelationalOperator.Eq.class));
        return new FileExpression(predicate);
    }

    private boolean compareFile(PartitionSet partition, String key, Value value, Class<RelationalOperator.Eq> opCls) {

        List<PartitionDetail> selectPartition = partition.getPartitions().stream()
                .filter(p -> p.getMetadata().getColumn().equals(key)).collect(Collectors.toList());
        if (!selectPartition.isEmpty()) {
            return selectPartition.stream().filter(p -> compareValue(p.getValue(), value, opCls)).findAny().isPresent();
        } else {
            return true;
        }
    }

    private boolean compareValue(String partitionValue, Value source, Class<RelationalOperator.Eq> opCls) {
        if (source instanceof StringValue) {
            StringValue target = (StringValue) source;
            if (opCls.equals(RelationalOperator.Eq.class)) {
                return partitionValue.equals(target.getValue());
            } else if (opCls.equals(RelationalOperator.NotEq.class)) {
                return !partitionValue.equals(target.getValue());
            } else {
                return true;
            }
        } else if (source instanceof IntegerValue) {
            String target = String.valueOf(((IntegerValue) source).getValue());
            if (opCls.equals(RelationalOperator.Eq.class)) {
                return partitionValue.equals(target);
            } else if (opCls.equals(RelationalOperator.NotEq.class)) {
                return !partitionValue.equals(target);
            } else {
                return true;
            }
        }
        return true;
    }

    public static class FileExpression implements Expression {
        private final Predicate<FileMetadata> predicate;

        public FileExpression(Predicate<FileMetadata> predicate) {
            this.predicate = predicate;
        }

        public Predicate<FileMetadata> getPredicate() {
            return predicate;
        }
    }
}
