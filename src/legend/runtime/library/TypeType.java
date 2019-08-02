package legend.runtime.library;


import legend.compiletime.expression.value.TypeValue;
import legend.compiletime.expression.value.Value;
import legend.runtime.type.IntersectionType;
import legend.runtime.type.PrimitiveType;
import legend.runtime.type.Type;
import legend.runtime.type.UnionType;

public class TypeType extends PrimitiveType {

    public TypeType() {
        super(new PrimitiveType.Builder("type")
                .binaryOper("and", TypeType::operAnd)
                .binaryOper("or", TypeType::operOr)
        );
    }

    private static Value operAnd(Value left, Value right) {
        Type type1 = left.asType();
        Type type2 = right.asType();
        return new TypeValue(new IntersectionType(type1, type2));
    }

    private static Value operOr(Value left, Value right) {
        Type type1 = left.asType();
        Type type2 = right.asType();
        return new TypeValue(new UnionType(type1, type2));
    }

}
