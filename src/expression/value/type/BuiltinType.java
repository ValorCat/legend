package expression.value.type;

import execute.Environment;
import expression.group.ArgumentList;
import expression.value.LObject;
import expression.value.Value;
import expression.value.function.LFunction;

import java.util.HashMap;
import java.util.Map;

public abstract class BuiltinType extends Type {

    public BuiltinType(String name, LFunction... methods) {
        this(name, new String[0], methods);
    }

    public BuiltinType(String name, String[] personal, LFunction... methods) {
        super(name, personal, toSharedMap(methods));
    }

    @Override
    public Value instantiate(ArgumentList args, Environment env) {
        Value instance = initialize(args, env);
        if (instance == null) {
            return new LObject(this, args.args());
        }
        instance.setType(this);
        return instance;
    }

    @Override
    protected boolean setShared(String attribute, Value value) {
        throw new RuntimeException("Attribute '" + attribute + "' of type '" + name + "' is read-only");
    }

    @Override
    public void deanonymize(String name) {}

    protected Value initialize(ArgumentList args, Environment env) {
        if (!args.keywords().isEmpty()) {
            throw new RuntimeException("Type '" + getName() + "' does not accept keyword arguments");
        }
        return null;
    }

    private static Map<String, Value> toSharedMap(LFunction... methods) {
        Map<String, Value> map = new HashMap<>(methods.length);
        for (LFunction method : methods) {
            map.put(method.getName(), method);
        }
        return map;
    }

}
