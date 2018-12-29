package dataformat;

import execute.StandardLibrary;

/**
 * @since 12/24/2018
 */
public class StringValue extends Value {

    private String value;

    public StringValue(String value) {
        super(StandardLibrary.type("str"));
        this.value = value;
    }

    @Override
    public boolean matches(String pattern) {
        return value.equals(pattern);
    }

    @Override
    public int asInt() {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("String '" + value + "' cannot be converted to an integer");
        }
    }

    @Override
    public String asStr() {
        return value;
    }

    @Override
    public boolean asBool() {
        switch (value) {
            case "true": return true;
            case "false": return false;
            default: throw new RuntimeException("String '" + value + "' cannot be converted to a boolean");
        }
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Value other) {
        return type() == other.type()
                && ((StringValue) other).value.equals(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
