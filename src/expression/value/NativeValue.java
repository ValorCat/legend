package expression.value;

/**
 * @since 2/9/2019
 */
public class NativeValue extends Value {

    private Object value;

    public NativeValue(Object javaValue) {
        super("*Native");
        this.value = javaValue;
    }

    @Override
    public Object asNative() {
        return value;
    }

    @Override
    public boolean equals(Value other) {
        return (this == other) || (type() == other.type() && value.equals(((NativeValue) other).value));
    }

}
