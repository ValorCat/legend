package legend.runtime.type;

import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.value.Value;

import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public abstract class Type {

    private String name;
    private TypeReference supertype;
    private Map<String, UnaryOperator<Value>> unaryOps;     // unary operation handlers
    private Map<String, BinaryOperator<Value>> binaryOps;   // binary operation handlers

    public Type(String name, TypeReference supertype) {
        this(name, supertype, Map.of(), Map.of());
    }

    public Type(String name, TypeReference supertype, Map<String, UnaryOperator<Value>> unaryOps,
                Map<String, BinaryOperator<Value>> binaryOps) {
        this.name = name;
        this.supertype = supertype;
        this.unaryOps = unaryOps;
        this.binaryOps = binaryOps;
    }

    public Type(Type other) {
        this.name = other.name;
        this.supertype = other.supertype;
        this.unaryOps = other.unaryOps;
        this.binaryOps = other.binaryOps;
    }

    public abstract Value buildNew(ArgumentList args);
    public abstract Value getAttribute(String attribute, Value target);
    public abstract void setAttribute(String attribute, Value target, Value value);

    public String getName() {
        return name;
    }

    private Optional<Type> getSuperType() {
        return Optional.ofNullable(supertype == null ? null : supertype.get());
    }

    public Value operateUnary(String operator, Value operand) {
        return getOperatorHandler(operator, type -> type.unaryOps)
                .orElseThrow(() -> new RuntimeException("Cannot apply operator '" + operator + "' to type '" + name + "'"))
                .apply(operand);
    }

    public Value operateBinary(String operator, Value left, Value right) {
        return getOperatorHandler(operator, type -> type.binaryOps)
                .orElseThrow(() -> new RuntimeException("Cannot apply operator '" + operator + "' to types '" + name
                        + "' and '" + right.type().getName() + "'"))
                .apply(left, right);
    }

    /**
     * Get the operation handler for the specified operator from the specified table (either unaryOps or binaryOps). If
     * this type does not define a handler, its supertype is queried recursively until either a handler is found or no
     * more supertypes exist, which results in an empty Optional being returned.
     * @param operator the operator to look up
     * @param mapGetter a function that accepts a type and returns a handler table, e.g. t -> t.binaryOps
     * @param <Handler> the functional interface that the operation handler implements
     * @return an optional containing the operation handler, if found
     */
    private <Handler> Optional<Handler> getOperatorHandler(String operator, Function<Type, Map<String, Handler>> mapGetter) {
        return Optional.ofNullable(mapGetter.apply(this).get(operator))
                .or(() -> getSuperType().flatMap(parent -> parent.getOperatorHandler(operator, mapGetter)));
    }

    /**
     * Return whether this type is equal to or a supertype of the specified type.
     * @param other the other type
     * @return true if this type is equal to or a supertype of the specified type, otherwise false
     */
    public boolean isSuperTypeOf(Type other) {
        return this == other || other.getSuperType()
                .map(this::isSuperTypeOf)
                .orElse(false);
    }

    @Override
    public String toString() {
        return "type[" + name + "]";
    }

}
