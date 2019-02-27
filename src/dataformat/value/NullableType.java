package dataformat.value;

/**
 * @since 2/26/2019
 */
public class NullableType extends Type {

    private Type type;

    public NullableType(Type type) {
        super(type);
        this.type = type;
    }

    @Override
    public boolean encompasses(Type other) {
        return type.encompasses(other) || other == LNull.NULL_TYPE;
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
        return other instanceof NullableType && type == ((NullableType) other).type;
    }

}
