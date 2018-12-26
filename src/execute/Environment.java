package execute;

import dataformat.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @since 12/23/2018
 */
public class Environment {

    public static final Environment GLOBAL = new Environment(null);
    public static final Type FUNC_TYPE, INT_TYPE, LIST_TYPE, RANGE_TYPE, STR_TYPE, TYPE_TYPE;

    private static List<Value> memory = new ArrayList<>();

    static {
        FUNC_TYPE = new Type("func");
        INT_TYPE = new Type("int");
        LIST_TYPE = new Type("list");
        RANGE_TYPE = new Type("range", "left", "right");
        STR_TYPE = new Type("str");
        TYPE_TYPE = new Type("type", (args, env) -> {
            String[] attributes = args.keywords().keySet().toArray(new String[0]);
            // todo use attribute types/bounds
            return new Type(attributes);
        });

        Type[] builtinTypes = {FUNC_TYPE, INT_TYPE, LIST_TYPE, RANGE_TYPE, STR_TYPE, TYPE_TYPE};
        FunctionValue[] builtinFunctions = StandardLibrary.getFunctions();

        for (Type type : builtinTypes) {
            GLOBAL.assign(type.getName(), type);
        }

        for (FunctionValue func : builtinFunctions) {
            GLOBAL.assign(func.getName(), func);
        }
    }

    private Map<String, Integer> namespace;
    private Environment parent;

    public Environment(Environment parent) {
        this.namespace = new HashMap<>();
        this.parent = parent;
    }

    public void assign(String name, Value value) {
        namespace.put(name, store(value));
        if (value.type() == TYPE_TYPE) {
            ((Type) value).deanonymize(name);
        }
    }

    public int store(Value value) {
        memory.add(value);
        return memory.size() - 1;
    }

    public Value fetch(String name) {
        Integer address = null;
        Environment env = this;
        while (address == null && env != null) {
            address = env.namespace.get(name);
            if (address == null) env = env.parent;
        }
        if (address == null) {
            throw new RuntimeException("Variable '" + name + "' is not defined");
        }
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

}
