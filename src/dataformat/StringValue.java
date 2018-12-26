package dataformat;

import execute.Environment;

/**
 * @since 12/24/2018
 */
public class StringValue extends Value {

    private String value;

    public StringValue(String value) {
        super(Environment.STR_TYPE);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
