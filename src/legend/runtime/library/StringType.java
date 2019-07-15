package legend.runtime.library;

import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.value.LInteger;
import legend.compiletime.expression.value.LNull;
import legend.compiletime.expression.value.LString;
import legend.compiletime.expression.value.Value;
import legend.compiletime.expression.value.type.BuiltinType;

public class StringType extends BuiltinType {

    private static StringIteratorType iterator = new StringIteratorType();

    public StringType() {
        super(new BuiltinType.Builder("String", "Any")
                .shared("show", StringType::show)
                .unaryOper("for", StringType::operIterate)
                .unaryOper("#", StringType::operSize)
                .binaryOper("[]", StringType::operSubscript)
        );
    }

    private static Value show(ArgumentList args) {
        System.out.println(args.target().asString());
        return LNull.NULL;
    }

    private static Value operIterate(Value operand) {
        return iterator.instantiate(new ArgumentList(new LInteger(0), operand));
    }

    private static Value operSize(Value operand) {
        return new LInteger(operand.asString().length());
    }

    private static Value operSubscript(Value target, Value subscript) {
        String string = target.asString();
        int index = subscript.asInteger();
        if (index >= 0 && index < string.length()) {
            return new LString(string.substring(index, index + 1));
        }
        throw new RuntimeException("Cannot get index " + index + " of string of length " + string.length());
    }

    private static class StringIteratorType extends BuiltinType {

        public StringIteratorType() {
            super(new BuiltinType.Builder("StringIterator", "Any")
                    .personal("index", "string")
                    .unaryOper("next", StringIteratorType::operNext)
            );
        }

        private static Value operNext(Value operand) {
            int index = operand.getAttribute("index").asInteger();
            String string = operand.getAttribute("string").asString();
            if (index < string.length()) {
                operand.setAttribute("index", new LInteger(index + 1));
                return new LString(string.substring(index, index + 1));
            }
            return LNull.NULL;
        }

    }

}
