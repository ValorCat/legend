package compiletime.expression.value.type;

import compiletime.expression.group.ArgumentList;
import compiletime.expression.value.Value;
import runtime.Scope;
import runtime.TypeLibrary;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @since 12/23/2018
 */
public abstract class Type extends Value {

    protected String name;
    private Map<String, Integer> personal;
    private Map<String, Value> shared;

    public Type(String name, String[] personal, Map<String, Value> shared) {
        super("Type");
        this.name = name;
        this.personal = buildPersonalMap(personal);
        this.shared = shared;
    }

    public Type(Type other) {
        super("Type");
        this.name = other.name;
        this.personal = other.personal;
        this.shared = other.shared;
    }

    public abstract Value instantiate(ArgumentList args, Scope scope);
    public abstract void deanonymize(String name);

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

    public boolean encompasses(Type other) {
        return this == other;
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
        return "type[" + getName() + "]";
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
