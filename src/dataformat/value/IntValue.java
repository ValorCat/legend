package dataformat.value;

import execute.StandardLibrary;

/**
 * @since 12/24/2018
 */
public class IntValue extends Value {

    private int value;

    public IntValue(int value) {
        super(StandardLibrary.type("Integer"));
        this.value = value;
    }

    @Override
    public boolean matches(String pattern) {
        return asString().equals(pattern);
    }

    @Override
    public int asInteger() {
        return value;
    }

    @Override
    public String asString() {
        return String.valueOf(value);
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Value other) {
        return type() == other.type()
                && value == ((IntValue) other).value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
