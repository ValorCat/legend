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

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
