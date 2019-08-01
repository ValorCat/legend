package legend.compiletime.expression.value;

import legend.compiletime.expression.type.Type;

public class LType extends Value {

    private Type value;

    public LType(Type type) {
        super("type");
        this.value = type;
    }

    @Override
    public boolean matches(String pattern) {
        return asString().equals(pattern);
    }

    @Override
    public Type asType() {
        return value;
    }

    @Override
    public String asString() {
        return value.getName();
    }

    @Override
    public boolean equals(Value other) {
        return type() == other.type()
                && value.equals(((LType) other).value);
    }

    @Override
    public String toString() {
        return value.getName();
    }

}
