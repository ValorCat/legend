package legend.runtime;

import legend.compiletime.expression.value.NullValue;
import legend.compiletime.expression.value.Value;

import java.util.Optional;

public class Scope {

    private final Scope parent;
    private final Namespace namespace;
    private Value returnValue;
    private boolean returned;

    public Scope() {
        this(null);
    }

    public Scope(Scope parent) {
        this.parent = parent;
        this.namespace = new Namespace();
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
        namespace.assign(name, value);
    }

    /**
     * Retrieve a value in memory by its mapped variable name.
     * @param name the name to retrieve
     * @return the variable's value
     */
    public Value getVariable(String name) {
        Optional<Scope> scope = getDefiningScope(name);
        if (scope.isEmpty()) {
            if (name.equals("*")) {
                throw new RuntimeException("Cannot use '*' wildcard outside of 'where' expression");
            } else {
                throw new RuntimeException("Variable '" + name + "' is not defined");
            }
        }
        return scope.get().namespace.retrieve(name);
    }

    public Value getReturnValue() {
        return returnValue == null ? NullValue.NULL : returnValue;
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
        while (current != null && !current.namespace.defines(name)) {
            current = current.parent;
        }
        return Optional.ofNullable(current);
    }

}
