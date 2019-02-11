package dataformat.value;

import execute.StandardLibrary;

/**
 * @since 2/9/2019
 */
public final class NullValue extends Value {

    public static NullValue NULL = new NullValue();

    private NullValue() {
        super(StandardLibrary.type("*Null"));
    }

    @Override
    public String asString() {
        return "null";
    }

    @Override
    public boolean asBoolean() {
        return false;
    }

    @Override
    public boolean equals(Value other) {
        return this == other;
    }

    @Override
    public String toString() {
        return "null";
    }

}
