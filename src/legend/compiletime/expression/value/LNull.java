package legend.compiletime.expression.value;

/**
 * @since 2/9/2019
 */
public final class LNull extends Value {

    public static LNull NULL = new LNull();

    private LNull() {
        super("*Null");
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
