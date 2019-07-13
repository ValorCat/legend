package legend.compiletime.expression.value.type;

import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.value.LNull;
import legend.compiletime.expression.value.Value;

/**
 * @since 2/26/2019
 */
public class NullableType extends Type {

    private Type wrappedType;

    public NullableType(Type type) {
        super(type);
        this.wrappedType = type;
    }

    @Override
    public Value instantiate(ArgumentList args) {
        return wrappedType.instantiate(args);
    }

    @Override
    public void deanonymize(String name) {
        wrappedType.deanonymize(name);
    }

    @Override
    public boolean encompasses(Type other) {
        return wrappedType.encompasses(other) || other == LNull.NULL.type();
    }

    @Override
    public String asString() {
        return super.asString() + "?";
    }

    @Override
    public String getName() {
        return super.getName() + "?";
    }

    @Override
    public boolean equals(Value other) {
        return other instanceof NullableType && wrappedType == ((NullableType) other).wrappedType;
    }

}
