package legend.runtime.type;

import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.value.Value;

public interface Type {

    String getName();
    boolean isSupertypeOf(RuntimeType type);
    Value buildNew(ArgumentList args);

}
