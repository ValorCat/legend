package execute;

import dataformat.statement.FlowController;
import dataformat.statement.Statement;
import dataformat.value.LFunction;
import dataformat.value.LNull;
import dataformat.value.Type;
import dataformat.value.Value;

import java.util.*;

/**
 * An environment stores execution data related to a particular scope,
 * including the instruction list, program counter, control structure
 * stack, variable mappings, and function return values.
 * @since 12/23/2018
 */
public class Environment {

    /*
    The global environment is the default environment, and directly or
    indirectly the parent of all other environments.
     */
    public static final Environment GLOBAL = new Environment();

    /*
    Variable data is stored here and referenced by its index (address).
    // todo actual garbage collection
     */
    private static List<Value> memory = new ArrayList<>();

    static {
        // add the standard library types and functions to the global environment
        for (Map.Entry<String, Type> type : StandardLibrary.TYPES.entrySet()) {
            GLOBAL.assign(type.getKey(), type.getValue());
        }

        for (LFunction func : StandardLibrary.FUNCTIONS) {
            GLOBAL.assign(func.getName(), func);
        }
    }

    private Environment parent;
    private Map<String, Integer> namespace;
    private Stack<FlowController> controlStack;

    private List<Statement> statements;
    private int programCounter;
    private int addressOffset;
    private boolean programCounterChanged;
    private Value returnValue;

    private Environment(List<Statement> statements, int startAddress, Environment parent) {
        this.parent = parent;
        this.namespace = new HashMap<>();
        this.controlStack = new Stack<>();

        this.statements = statements;
        this.programCounter = startAddress;
        this.addressOffset = startAddress;
        this.programCounterChanged = false;
    }

    private Environment() {
        this(List.of(), 0, null);
    }

    public Environment(List<Statement> statements, int startAddress) {
        this(statements, startAddress, GLOBAL);
    }

    /**
     * Store a value in memory and then map it to a variable name.
     * If the variable is already defined in an outer scope, that
     * variable will be reassigned. Otherwise, create a new local
     * variable.
     * @param name the name of the variable
     * @param value the value to store
     */
    public void assign(String name, Value value) {
        Environment env = findName(name).orElse(this);
        env.assignLocal(name, value);
        if (value.type() == StandardLibrary.type("Type")) {
            // todo deanonymize functions
            ((Type) value).deanonymize(name);
        }
    }

    /**
     * Store a value in memory and then map it to a variable name.
     * @param name the name of the variable
     * @param value the value to store
     */
    public void assignLocal(String name, Value value) {
        namespace.put(name, store(value));
    }

    /**
     * Retrieve a value in memory by its mapped variable name.
     * @param name the name to retrieve
     * @return the variable's value
     */
    public Value fetch(String name) {
        Optional<Environment> definingEnv = findName(name);
        if (!definingEnv.isPresent()) {
            throw new RuntimeException("Variable '" + name + "' is not defined");
        }
        int address = definingEnv.get().namespace.get(name);
        Value value = memory.get(address);
        if (value == null) {
            throw new RuntimeException("Variable '" + name + "' (address "
                    + Integer.toHexString(address) + ") points to null");
        }
        return value;
    }

    public Map<String, Integer> getNamespace() {
        return namespace;
    }

    public Stack<FlowController> getControlStack() {
        return controlStack;
    }

    public void setProgram(List<Statement> statements) {
        this.statements = statements;
    }

    public boolean hasMoreInstructions() {
        return programCounter - addressOffset < statements.size();
    }

    public Statement getInstruction() {
        return statements.get(programCounter - addressOffset);
    }

    public List<Statement> getSubroutine(int startAddress, int endAddress) {
        return statements.subList(startAddress - addressOffset, endAddress - addressOffset);
    }

    public int getCounter() {
        return programCounter;
    }

    public void incrementCounter() {
        if (!this.programCounterChanged) {
            this.programCounter++;
        }
        this.programCounterChanged = false;
    }

    public void setCounter(int counter) {
        this.programCounter = counter;
        this.programCounterChanged = true;
    }

    public Value getReturnValue() {
        return returnValue == null ? LNull.NULL : returnValue;
    }

    public void setReturnValue(Value returnValue) {
        this.returnValue = returnValue;
    }

    /**
     * Find the nearest scope in which a variable is defined.
     * @param name the variable name to search for
     * @return an optional containing the innermost environment
     *         scope, or an empty optional if it is undefined
     */
    private Optional<Environment> findName(String name) {
        Environment current = this;
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
