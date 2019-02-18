package dataformat.value;

import dataformat.ArgumentList;
import dataformat.value.NativeFunction.FunctionBody;
import execute.Environment;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    public String asString() {
        return name;
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
        Value value = findAttribute(attribute, object);
        if (value != null) {
            return value;
        }
        throw new RuntimeException("Type '" + name + "' has no attribute '" + attribute + "'");
    }

    public Optional<Value> getOptionalAttribute(String attribute, Value object) {
        return Optional.ofNullable(findAttribute(attribute, object));
    }

    public void setAttribute(String attribute, Value object, Value value) {
        Integer index = personalAttributes.get(attribute);
        if (index != null) {
            object.getAttributes()[index] = value;
        } else if (sharedAttributes.containsKey(attribute)) {
            sharedAttributes.put(attribute, value);
        } else {
            // todo handle inherited attributes
            throw new RuntimeException("Type '" + name + "' has no attribute '" + attribute + "'");
        }
    }

    public Value instantiate(ArgumentList args, Environment env) {
        if (initializer == null) {
            if (!args.keywords().isEmpty()) {
                throw new RuntimeException("Type '" + getName() + "' does not accept keyword arguments");
            }
            // todo check params = args
            return new LObject(this, args.args());
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


    private Value findAttribute(String name, Value object) {
        Integer index = personalAttributes.get(name);
        if (index != null) {
            return object.getAttributes()[index];
        }
        // todo handle inherited attributes
        return sharedAttributes.get(name);
    }


}
