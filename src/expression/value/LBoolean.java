package expression.value;

/**
 * @since 12/28/2018
 */
public final class LBoolean extends Value {

    public static final LBoolean TRUE = new LBoolean(true);
    public static final LBoolean FALSE = new LBoolean(false);

    public static LBoolean resolve(boolean b) {
        return b ? TRUE : FALSE;
    }

    private boolean value;
    private String string;

    private LBoolean(boolean value) {
        super("Boolean");
        this.value = value;
        this.string = String.valueOf(value);
    }

    @Override
    public boolean matches(String pattern) {
        return string.equals(pattern);
    }

    @Override
    public String asString() {
        return string;
    }

    @Override
    public boolean asBoolean() {
        return value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public boolean equals(Value other) {
        return this == other;
    }

    @Override
    public String toString() {
        return string;
    }

}
