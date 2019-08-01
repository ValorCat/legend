package legend.runtime.library;

import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.value.*;
import legend.runtime.type.ClassType;
import legend.runtime.type.PrimitiveType;

public class StringType extends PrimitiveType {

    private static StringIteratorType iterator = new StringIteratorType();

    public StringType() {
        super(new PrimitiveType.Builder("str")
                .unaryOper("for", StringType::operIterate)
                .unaryOper("#", StringType::operSize)
                .binaryOper("in", StringType::operIn)
                .binaryOper("[]", StringType::operSubscript)
        );
    }

    private static Value operIn(Value string, Value substring) {
        String javaString = string.asString();
        String javaSubstring = substring.asString();
        return BoolValue.resolve(javaString.contains(javaSubstring));
    }

    private static Value operIterate(Value operand) {
        return iterator.buildNew(new ArgumentList(new IntValue(0), operand));
    }

    private static Value operSize(Value operand) {
        return new IntValue(operand.asString().length());
    }

    private static Value operSubscript(Value target, Value subscript) {
        String string = target.asString();
        if (subscript.isType("int")) {
            int index = subscript.asInteger();
            if (index >= 0 && index < string.length()) {
                return new StrValue(string.substring(index, index + 1));
            }
            throw new RuntimeException("Cannot get index " + index + " of string of length " + string.length());
        } else if (subscript.isType("range")) {
            int left = subscript.getAttribute("left").asInteger();
            int right = subscript.getAttribute("right").asInteger();
            if (left >= 0 && right >= 0 && left < string.length() && right < string.length() && left <= right) {
                return new StrValue(string.substring(left, right + 1));
            }
            throw new RuntimeException("Cannot get substring [" + left + "," + right + "] of string of length " + string.length());
        }
        throw new RuntimeException("Cannot apply operator '[]' to types 'List' and '" + subscript.type().getName() + "'");
    }

    private static class StringIteratorType extends ClassType {

        public StringIteratorType() {
            super(new ClassType.Builder("StringIterator")
                    .personal("index", "string")
                    .unaryOper("next", StringIteratorType::operNext)
            );
        }

        private static Value operNext(Value operand) {
            int index = operand.getAttribute("index").asInteger();
            String string = operand.getAttribute("string").asString();
            if (index < string.length()) {
                operand.setAttribute("index", new IntValue(index + 1));
                return new StrValue(string.substring(index, index + 1));
            }
            return NullValue.NULL;
        }

    }

}
