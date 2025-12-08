package org.curiosity.rover.store.value;

public interface Value {

    public boolean neq(Value other);

    public boolean gt(Value other);

    public boolean gtEq(Value other);

    public boolean lt(Value other);

    public boolean ltEq(Value other);

}
