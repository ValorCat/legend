package dataformat;

import dataformat.value.LFunction;
import dataformat.value.LFunction.FunctionBody;
import dataformat.value.Type;
import dataformat.value.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * A convenience builder class for creating new built-in types.
 * @since 12/27/2018
 */
public class TypeBuilder {

    private String name;
    private String[] personal;
    private Map<String, Value> shared;
    private FunctionBody initializer;

    public static TypeBuilder create(String name) {
        TypeBuilder builder = new TypeBuilder();
        builder.name = name;
        builder.personal = new String[0];
        builder.shared = new HashMap<>();
        return builder;
    }

    public TypeBuilder initializer(FunctionBody init) {
        initializer = init;
        return this;
    }

    public TypeBuilder personal(String... attributes) {
        personal = attributes;
        return this;
    }

    public TypeBuilder shared(String name, Value value) {
        shared.put(name, value);
        return this;
    }

    public TypeBuilder shared(String name, FunctionBody method) {
        shared.put(name, new LFunction(name, method));
        return this;
    }

    public Type build() {
        return new Type(name, initializer, personal, shared);
    }

}
