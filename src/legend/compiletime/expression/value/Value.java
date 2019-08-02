package legend.compiletime.expression.value;

import legend.compiletime.expression.Expression;
import legend.runtime.Scope;
import legend.runtime.type.RuntimeType;
import legend.runtime.type.Type;
import legend.runtime.type.TypeReference;

import java.util.List;

/**
 * Represents a value in the language, like an integer or list. Primitive types are concrete subclasses of this class,
 * while other types (i.e. more complex built-in types and user-defined types) are instances of {@link ObjectValue}. A
 * value is defined as having a type and 0 or more personal attributes, which are themselves values. A primitive value
 * has no personal attributes.
 * @see Type
 * @see legend.runtime.library
 * @since 12/24/2018
 */
public abstract class Value implements Expression {

    private TypeReference type;
    private Value[] attributes;

    public Value(TypeReference type, Value... attributes) {
        this.type = type;
        this.attributes = attributes;
    }

    public Value(RuntimeType type, Value... attributes) {
        this.type = TypeReference.to(type);
        this.attributes = attributes;
    }

    @Override
    public Value evaluate(Scope scope) {
        return this;
    }

    public int asInteger() {
        throw errorOnCast("int");
    }

    public String asString() {
        throw errorOnCast("str");
    }

    public boolean asBoolean() {
        throw errorOnCast("bool");
    }

    public List<Value> asList() {
        throw errorOnCast("list");
    }

    public Type asType() {
        throw errorOnCast("type");
    }
    
    public RuntimeType type() {
        return type.get();
    }

    public boolean isType(TypeReference type) {
        return type() == type.get();
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
