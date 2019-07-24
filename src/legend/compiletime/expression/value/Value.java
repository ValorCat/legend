package legend.compiletime.expression.value;

import legend.compiletime.expression.Expression;
import legend.compiletime.expression.value.type.LazyType;
import legend.compiletime.expression.value.type.Type;
import legend.runtime.Scope;

import java.util.List;

/**
 * Represents a value in the language, like an integer or list. Primitive types are concrete subclasses of this class,
 * while other types (i.e. more complex built-in types and user-defined types) are instances of {@link LObject}. A
 * value is defined as having a type and 0 or more personal attributes, which are themselves values. A primitive value
 * has no personal attributes.
 * @see legend.compiletime.expression.value.type.Type
 * @see legend.runtime.library
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

    public List<Value> asList() {
        throw errorOnCast("List");
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

    public Value operateUnary(String operator) {
        return type().operateUnary(operator, this);
    }

    public Value operateBinary(String operator, Value other) {
        return type().operateBinary(operator, this, other);
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
