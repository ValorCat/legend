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
    private static List<Value> memory = new ArrayList<>();

    static {
        for (Map.Entry<String, Type> type : StandardLibrary.TYPES.entrySet()) {
            GLOBAL.assign(type.getKey(), type.getValue());
        }

        for (FunctionValue func : StandardLibrary.FUNCTIONS) {
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
        if (value.type() == StandardLibrary.type("type")) {
            // todo deanonymize functions
            ((Type) value).deanonymize(name);
        }
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

    private static int store(Value value) {
        memory.add(value);
        return memory.size() - 1;
    }

}
