package legend.compiletime.expression.value;

import legend.runtime.type.BuiltinType;

/**
 * @since 12/24/2018
 */
public class StrValue extends Value {

    private String value;

    public StrValue(String value) {
        super(BuiltinType.STR);
        this.value = value;
    }

    @Override
    public boolean matches(String pattern) {
        return value.equals(pattern);
    }

    @Override
    public int asInteger() {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("String '" + value + "' cannot be converted to an integer");
        }
    }

    @Override
    public String asString() {
        return value;
    }

    @Override
    public boolean equals(Value other) {
        return type() == other.type()
                && ((StrValue) other).value.equals(value);
    }

    @Override
    public String toString() {
        return "'" + value + "'";
    }
}
