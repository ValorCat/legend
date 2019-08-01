package legend.compiletime.expression.value;

/**
 * @since 2/9/2019
 */
public final class NullValue extends Value {

    public static NullValue NULL = new NullValue();

    private NullValue() {
        super("*null");
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