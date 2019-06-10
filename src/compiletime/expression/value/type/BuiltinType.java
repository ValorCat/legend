package compiletime.expression.value.type;

import compiletime.expression.group.ArgumentList;
import compiletime.expression.value.LObject;
import compiletime.expression.value.Value;
import compiletime.expression.value.function.LFunction;
import runtime.Scope;

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
    public Value instantiate(ArgumentList args, Scope scope) {
        Value instance = initialize(args, scope);
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

    protected Value initialize(ArgumentList args, Scope scope) {
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
