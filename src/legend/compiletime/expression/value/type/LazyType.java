package legend.compiletime.expression.value.type;

public class LazyType {

    private String name;
    private Type type;

    public LazyType(String name) {
        this.name = name;
    }

    public LazyType(Type type) {
        this.name = type.getName();
        this.type = type;
    }

    public Type get() {
        if (type == null) {
            type = Type.of(name);
        }
        return type;
    }

    public String getName() {
        return name;
    }

}
