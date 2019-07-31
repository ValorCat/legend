package legend.runtime;

import legend.compiletime.expression.value.LNull;
import legend.compiletime.expression.value.Value;

import java.util.*;

public class Scope {

    private static List<Value> memory = new ArrayList<>(); // todo garbage collection

    private final Scope parent;
    private final Map<String, Integer> namespace;
    private Value returnValue;
    private boolean returned;

    public Scope() {
        this(null);
    }

    public Scope(Scope parent) {
        this.parent = parent;
        this.namespace = new HashMap<>();
    }

    /**
     * Store a value in memory and then map it to a variable name.
     * If the variable is already defined in an outer scope, that
     * variable will be reassigned. Otherwise, create a new local
     * variable.
     * @param name the name of the variable
     * @param value the value to store
     */
    public void setVariable(String name, Value value) {
        Scope scope = getDefiningScope(name).orElse(this);
        scope.setLocalVariable(name, value);
    }

    /**
     * Store a value in memory and then map it to a variable name.
     * @param name the name of the variable
     * @param value the value to store
     */
    public void setLocalVariable(String name, Value value) {
        namespace.put(name, store(value));
    }

    /**
     * Retrieve a value in memory by its mapped variable name.
     * @param name the name to retrieve
     * @return the variable's value
     */
    public Value getVariableValue(String name) {
        Optional<Scope> scope = getDefiningScope(name);
        if (scope.isEmpty()) {
            if (name.equals("*")) {
                throw new RuntimeException("Cannot use '*' wildcard outside of 'where' expression");
            } else {
                throw new RuntimeException("Variable '" + name + "' is not defined");
            }
        }
        int address = scope.get().namespace.get(name);
        Value value = memory.get(address);
        if (value == null) {
            throw new RuntimeException("Variable '" + name + "' (address "
                    + Integer.toHexString(address) + ") points to null");
        }
        return value;
    }

    public Value getReturnValue() {
        return returnValue == null ? LNull.NULL : returnValue;
    }

    public void setReturnValue(Value returnValue) {
        this.returnValue = returnValue;
    }

    public boolean hasReturned() {
        return returned;
    }

    public void setReturnFlag() {
        this.returned = true;
    }

    /**
     * Find the innermost scope in which a variable is defined.
     * @param name the variable name to search for
     * @return an optional containing the innermost scope that defines
     *         the specified name, or an empty optional if the name
     *         is undefined
     */
    private Optional<Scope> getDefiningScope(String name) {
        Scope current = this;
        while (current != null && !current.namespace.containsKey(name)) {
            current = current.parent;
        }
        return Optional.ofNullable(current);
    }

    /**
     * Store a value in memory and return its address.
     * @param value the value to store
     * @return the value's new address
     */
    private static int store(Value value) {
        memory.add(value);
        return memory.size() - 1;
    }

}
