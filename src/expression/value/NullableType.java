package expression.value;

import execute.Environment;
import expression.group.ArgumentList;

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
    public Value instantiate(ArgumentList args, Environment env) {
        return wrappedType.instantiate(args, env);
    }

    @Override
    public void deanonymize(String name) {
        wrappedType.deanonymize(name);
    }

    @Override
    public boolean encompasses(Type other) {
        return wrappedType.encompasses(other) || other == LNull.NULL_TYPE;
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
