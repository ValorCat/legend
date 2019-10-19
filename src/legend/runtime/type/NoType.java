package legend.runtime.type;

import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.value.Value;

public class NoType implements Type {

    public static final Type NO_TYPE = new NoType();

    @Override
    public String getName() {
        return "<untyped>";
    }

    @Override
    public boolean isSupertypeOf(RuntimeType type) {
        return true;
    }

    @Override
    public Value buildNew(ArgumentList args) {
        // how would we even get here?
        throw new RuntimeException("Cannot create untyped object");
    }

}
