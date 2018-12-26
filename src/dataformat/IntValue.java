package dataformat;

import execute.Environment;

/**
 * @since 12/24/2018
 */
public class IntValue extends Value {

    private int value;

    public IntValue(int value) {
        super(Environment.INT_TYPE);
        this.value = value;
    }

    @Override
    public boolean matches(String pattern) {
        return String.valueOf(value).equals(pattern);
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
