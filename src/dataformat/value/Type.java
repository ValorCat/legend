package dataformat.value;

import dataformat.ArgumentList;
import dataformat.value.FunctionValue.FunctionBody;
import execute.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 12/23/2018
 */
public class Type extends Value {

    private String name;
    private Map<String, Integer> personalAttributes; // todo avoid Integer -> int unboxing
    private Map<String, Value> sharedAttributes;
    private FunctionBody initializer;
    private boolean anonymous;

    public Type(String[] attrNames) {
        this("(anonymous type)", null, attrNames, Map.of());
        this.anonymous = true;
    }

    public Type(String name, FunctionBody init, String[] personal, Map<String, Value> shared) {
        super(null);
        this.name = name;
        this.initializer = init;
        this.personalAttributes = new HashMap<>(personal.length);
        for (int i = 0; i < personal.length; i++) {
            this.personalAttributes.put(personal[i], i);
        }
        this.sharedAttributes = shared;
        this.anonymous = false;
    }

    @Override
    public boolean matches(String name) {
        return getName().equals(name);
    }

    @Override
    public String asStr() {
        return "type[" + getName() + "]";
    }

    public String getName() {
        return name;
    }

    public void deanonymize(String name) {
        if (anonymous) {
            this.name = name;
            this.anonymous = false;
        }
    }

    public Value getAttribute(String attribute, Value object) {
        Integer index = personalAttributes.get(attribute);
        if (index != null) {
            return object.getAttributes()[index];
        }
        Value value = sharedAttributes.get(attribute);
        if (value != null) {
            return value;
        }
        // todo handle inherited attributes
        throw new RuntimeException("Type '" + name + "' has no attribute '" + attribute + "'");
    }

    public Value instantiate(ArgumentList args, Environment env) {
        if (initializer == null) {
            if (!args.keywords().isEmpty()) {
                throw new RuntimeException("Type '" + getName() + "' does not accept keyword arguments");
            }
            // todo check params = args
            return new ObjectValue(this, args.args());
        } else {
            Value instance = initializer.apply(args, env);
            instance.setType(this);
            return instance;
        }
    }

    @Override
    public boolean equals(Value other) {
        return this == other;
    }

    @Override
    public String toString() {
        return "type[" + getName() + "]";
    }

}
