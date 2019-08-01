package legend.compiletime.expression.value;

import legend.runtime.type.BuiltinType;
import legend.runtime.type.Type;

public class TypeValue extends Value {

    private Type value;

    public TypeValue(Type type) {
        super(BuiltinType.TYPE);
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
                && value.equals(((TypeValue) other).value);
    }

    @Override
    public String toString() {
        return value.getName();
    }

}
