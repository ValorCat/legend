package legend.compiletime.expression.value.type;

import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.value.Value;
import legend.runtime.TypeLibrary;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * @since 12/23/2018
 */
public abstract class Type extends Value {

    public static final String NO_PARENT = "<noparent>";

    protected String name;
    private LazyType supertype;
    private Map<String, Integer> personal;                  // personal members
    private Map<String, Value> shared;                      // shared members
    private Map<String, UnaryOperator<Value>> unaryOps;     // unary operation handlers
    private Map<String, BinaryOperator<Value>> binaryOps;   // binary operation handlers

    public Type(String name, String supertype, String[] personal) {
        this(name, supertype, personal, Map.of(), Map.of(), Map.of());
    }

    public Type(String name, String supertype, String[] personal, Map<String, Value> shared, Map<String,
            UnaryOperator<Value>> unaryOps, Map<String, BinaryOperator<Value>> binaryOps) {
        super("type");
        this.name = name;
        //noinspection StringEquality
        this.supertype = supertype == NO_PARENT ? null : new LazyType(supertype);
        this.personal = buildPersonalMap(personal);
        this.shared = shared;
        this.unaryOps = unaryOps;
        this.binaryOps = binaryOps;
    }

    public Type(Type other) {
        super("type");
        this.name = other.name;
        this.supertype = other.supertype;
        this.personal = other.personal;
        this.shared = other.shared;
        this.unaryOps = other.unaryOps;
        this.binaryOps = other.binaryOps;
    }

    public abstract Value buildNew(ArgumentList args);

    @Override
    public boolean matches(String name) {
        return this.name.equals(name);
    }

    @Override
    public String asString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public Optional<Type> getSuperType() {
        return Optional.ofNullable(supertype == null ? null : supertype.get());
    }

    public Value getAttribute(String attribute, Value target) {
        return getPersonal(target, attribute)
                .or(() -> getShared(attribute))
                .orElseThrow(() -> new RuntimeException("Type '" + name + "' has no attribute '" + attribute + "'"));
    }

    public void setAttribute(String attribute, Value target, Value value) {
        if (!setPersonal(target, attribute, value) && !setShared(attribute, value)) {
            throw new RuntimeException("Type '" + name + "' has no attribute '" + attribute + "'");
        }
    }

    public Value operateUnary(String operator, Value operand) {
        return getOperatorHandler(operator, type -> type.unaryOps)
                .orElseThrow(() -> new RuntimeException(
                        "Cannot apply operator '" + operator + "' to type '" + name + "'"))
                .apply(operand);
    }

    public Value operateBinary(String operator, Value left, Value right) {
        return getOperatorHandler(operator, type -> type.binaryOps)
                .orElseThrow(() -> new RuntimeException(
                        "Cannot apply operator '" + operator + "' to types '" + name + "' and '" + right.type().name + "'"))
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
    private <Handler> Optional<Handler> getOperatorHandler(String operator,
                                                           Function<Type, Map<String, Handler>> mapGetter) {
        return Optional.ofNullable(mapGetter.apply(this).get(operator))
                .or(() -> getSuperType().flatMap(parent -> parent.getOperatorHandler(operator, mapGetter)));
    }

    /**
     * Return whether this type is equal to or a supertype of the specified type.
     * @param other the other type
     * @return true if this type is equal to or a supertype of the specified type, otherwise false
     */
    public boolean encompasses(Type other) {
        return this == other || other.getSuperType()
                .map(this::encompasses)
                .orElse(false);
    }

    public LazyType asLazy() {
        return new LazyType(this);
    }

    @Override
    public boolean equals(Value other) {
        return this == other;
    }

    @Override
    public String toString() {
        return "type[" + name + "]";
    }

    private Optional<Value> getPersonal(Value target, String attribute) {
        return Optional.ofNullable(personal.get(attribute))
                .map(target::getAttribute);
    }

    private boolean setPersonal(Value target, String attribute, Value value) {
        Integer index = personal.get(attribute);
        if (index != null) {
            target.setAttribute(index, value);
            return true;
        }
        return false;
    }

    private Optional<Value> getShared(String attribute) {
        return Optional.ofNullable(shared.get(attribute));
    }

    protected boolean setShared(String attribute, Value value) {
        if (shared.containsKey(attribute)) {
            shared.put(attribute, value);
            return true;
        }
        return false;
    }

    public static Type of(String name) {
        return TypeLibrary.getType(name);
    }

    private static Map<String, Integer> buildPersonalMap(String[] names) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < names.length; i++) {
            map.put(names[i], i);
        }
        return map;
    }

}
