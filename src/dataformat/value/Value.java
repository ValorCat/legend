package dataformat.value;

import dataformat.Expression;
import execute.Environment;
import execute.StandardLibrary;

/**
 * @since 12/24/2018
 */
public abstract class Value implements Expression {

    private Type type;
    private Value[] attributes;

    public Value(Type type, Value... attributes) {
        this.type = type;
        this.attributes = attributes;
    }

    @Override
    public Value evaluate(Environment env) {
        return this;
    }

    public int asInt() {
        throw errorOnCast("integer");
    }

    public String asStr() {
        throw errorOnCast("string");
    }

    public boolean asBool() {
        throw errorOnCast("boolean");
    }

    public Object asNative() {
        throw errorOnCast("*native");
    }

    public Type type() {
        if (type == null) type = StandardLibrary.type("type");
        return type;
    }

    public boolean isType(String type) {
        return type().matches(type);
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Value getAttribute(String name) {
        return type().getAttribute(name, this);
    }

    public Value[] getAttributes() {
        return attributes;
    }

    public boolean hasOwner() {
        return false;
    }

    public Value getOwner() {
        throw new UnsupportedOperationException("Cannot get owner of non-attribute");
    }

    public abstract boolean equals(Value other);

    private RuntimeException errorOnCast(String resultType) {
        return new RuntimeException("Type '" + type().getName() + "' cannot be converted to a " + resultType);
    }

}
