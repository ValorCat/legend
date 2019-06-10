package compiletime.expression.value;

import compiletime.expression.Expression;
import compiletime.expression.group.ArgumentList;
import compiletime.expression.operation.FunctionCall;
import compiletime.expression.value.type.LazyType;
import compiletime.expression.value.type.Type;
import runtime.Scope;

/**
 * @since 12/24/2018
 */
public abstract class Value implements Expression {

    private LazyType type;
    private Value[] attributes;

    public Value(String type, Value... attributes) {
        this.type = new LazyType(type);
        this.attributes = attributes;
    }

    public Value(Type type, Value... attributes) {
        this.type = type.asLazy();
        this.attributes = attributes;
    }

    @Override
    public Value evaluate(Scope scope) {
        return this;
    }

    public int asInteger() {
        throw errorOnCast("Integer");
    }

    public String asString() {
        throw errorOnCast("String");
    }

    public boolean asBoolean() {
        throw errorOnCast("Boolean");
    }

    public Object asNative() {
        throw errorOnCast("Native");
    }

    public Type type() {
        return type.get();
    }

    public boolean isType(String type) {
        return type().matches(type);
    }

    // todo eliminate setType
    public void setType(Type type) {
        this.type = type.asLazy();
    }

    public Value getAttribute(String name) {
        return type().getAttribute(name, this);
    }

    public void setAttribute(String name, Value value) {
        type().setAttribute(name, this, value);
    }

    public Value callMethod(String name, Scope scope, Value... args) {
        Value method = getAttribute(name);
        ArgumentList argList = new ArgumentList(args);
        argList.setTarget(this);
        return FunctionCall.call(method, argList, scope);
    }

    public Value callMetamethod(String name, ArgumentList args, Scope scope, String targetDesc) {
        Value method;
        try {
            method = type().getAttribute(name, this);
        } catch (RuntimeException e) {
            throw new RuntimeException("Expected " + targetDesc + " to have metamethod '" + name + "'");
        }
        if (!method.isType("Function")) {
            throw new RuntimeException("Expected " + targetDesc + " to have metamethod '" + name + "' but found type "
                    + method.type().getName());
        }
        args.setTarget(this);
        return FunctionCall.call(method, args, scope);
    }

    public Value[] getAttributes() {
        return attributes;
    }

    public Value getAttribute(int index) {
        return attributes[index];
    }

    public void setAttribute(int index, Value value) {
        attributes[index] = value;
    }

    public boolean hasOwner() {
        return false;
    }

    public Value getOwner() {
        throw new UnsupportedOperationException("Cannot get owner of non-attribute");
    }

    public abstract boolean equals(Value other);

    private RuntimeException errorOnCast(String resultType) {
        return new RuntimeException("Type '" + type().getName() + "' cannot be converted to " + resultType);
    }

}
