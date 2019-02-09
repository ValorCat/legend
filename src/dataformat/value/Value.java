package dataformat.value;

import dataformat.ArgumentList;
import dataformat.Expression;
import dataformat.operation.function.FunctionCall;
import execute.Environment;
import execute.StandardLibrary;

import java.util.Optional;

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

    public Optional<Value> getOptionalAttribute(String name) {
        return type().getOptionalAttribute(name, this);
    }

    public void setAttribute(String name, Value value) {
        type().setAttribute(name, this, value);
    }

    public Value callMethod(String name, Environment env, Value... args) {
        Value method = getAttribute(name);
        ArgumentList argList = new ArgumentList(args);
        argList.setTarget(this);
        return FunctionCall.call(method, argList, env);
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
