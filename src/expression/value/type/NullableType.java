package expression.value.type;

import execute.Scope;
import expression.group.ArgumentList;
import expression.value.LNull;
import expression.value.Value;

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
    public Value instantiate(ArgumentList args, Scope scope) {
        return wrappedType.instantiate(args, scope);
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
