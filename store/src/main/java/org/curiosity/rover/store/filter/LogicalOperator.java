package org.curiosity.rover.store.filter;

import java.util.Arrays;
import java.util.List;

public abstract class LogicalOperator implements Operator{

    private final List<Operator> operators;

    public LogicalOperator(List<Operator> operators) {
        this.operators = operators;
    }
    public LogicalOperator(Operator ... operators) {
        this.operators = Arrays.asList(operators);
    }

    public List<Operator> getOperators() {
        return operators;
    }

    public static class And extends LogicalOperator {
        public And(List<Operator> operators) {
            super(operators);
        }

        public And(Operator... operators) {
            super(operators);
        }
    }
    public static class Or extends LogicalOperator {
        public Or(List<Operator> operators) {
            super(operators);
        }

        public Or(Operator... operators) {
            super(operators);
        }
    }
}
