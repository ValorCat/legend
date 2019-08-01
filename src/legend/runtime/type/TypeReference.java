package legend.runtime.type;

public interface TypeReference {

    Type get();

    static TypeReference to(Type type) {
        return () -> type;
    }

}
