package legend.compiletime.expression.value;

/**
 * @since 12/24/2018
 */
public class LString extends Value {

    private String value;

    public LString(String value) {
        super("str");
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
                && ((LString) other).value.equals(value);
    }

    @Override
    public String toString() {
        return "'" + value + "'";
    }
}
