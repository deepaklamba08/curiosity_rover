package org.curiosity.rover.store.filter;

public interface Visitor {

    public Expression visit(Operator operator);
}
