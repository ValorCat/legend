package legend.compiletime.expression.value;

/**
 * @since 12/24/2018
 */
public class LInteger extends Value {

    private int value;

    public LInteger(int value) {
        super("Integer");
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
                && value == ((LInteger) other).value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
