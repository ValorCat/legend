package legend.runtime.type;

public interface TypeReference {

    RuntimeType get();

    static TypeReference to(RuntimeType type) {
        return () -> type;
    }

}
