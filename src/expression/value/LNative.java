package expression.value;

/**
 * @since 2/9/2019
 */
public class LNative extends Value {

    private Object value;

    public LNative(Object javaValue) {
        super("*Native");
        this.value = javaValue;
    }

    @Override
    public Object asNative() {
        return value;
    }

    @Override
    public boolean equals(Value other) {
        return (this == other) || (type() == other.type() && value.equals(((LNative) other).value));
    }

}
