package dataformat.value;

/**
 * Represents a value retrieved from an object with the . operator.
 * This class is used instead of simply returning the retrieved value
 * so that methods can have a reference to their owner object.
 * @since 12/27/2018
 */
public class Attribute extends Value {

    private Value owner, value;

    public Attribute(Value owner, Value value) {
        super(value.type(), value.getAttributes());
        this.owner = owner;
        this.value = value;
    }

    @Override
    public boolean hasOwner() {
        return true;
    }

    @Override
    public Value getOwner() {
        return owner;
    }

    public Value getValue() {
        return value;
    }

    @Override
    public boolean equals(Value other) {
        // this shouldn't be reachable if operator precedence rules are working
        throw new UnsupportedOperationException("Cannot compare attribute name");
    }

}
