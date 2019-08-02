package legend.runtime.type;

import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.value.Value;

public class UnionType implements Type {

    private Type left, right;

    public UnionType(Type left, Type right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String getName() {
        return String.format("%s | %s", left.getName(), right.getName());
    }

    @Override
    public boolean isSupertypeOf(RuntimeType type) {
        return left.isSupertypeOf(type) || right.isSupertypeOf(type);
    }

    @Override
    public Value buildNew(ArgumentList args) {
        throw new RuntimeException("Cannot create object of union type " + getName());
    }

}
