package dataformat;

import execute.StandardLibrary;

/**
 * @since 12/24/2018
 */
public class IntValue extends Value {

    private int value;

    public IntValue(int value) {
        super(StandardLibrary.type("int"));
        this.value = value;
    }

    @Override
    public boolean matches(String pattern) {
        return String.valueOf(value).equals(pattern);
    }

    @Override
    public int asInt() {
        return value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
