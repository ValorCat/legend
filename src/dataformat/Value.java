package dataformat;

import execute.Environment;
import execute.StandardLibrary;

import java.util.Arrays;

/**
 * @since 12/24/2018
 */
public class Value implements Expression {

    private Type type;
    private Value[] attributes;

    public Value(Value[] attributes) {
        this(null, attributes);
    }

    public Value(Type type, Value... attributes) {
        this.type = type;
        this.attributes = attributes;
    }

    @Override
    public Value evaluate(Environment env) {
        return this;
    }

    public int asInt() {
        throw new RuntimeException("Type '" + type().getName() + "' cannot be converted to an integer");
    }

    public Type type() {
        if (type == null) type = StandardLibrary.type("type");
        return type;
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

    @Override
    public String toString() {
        return type().getName() + Arrays.toString(attributes);
    }

}
