package dataformat.group;

import dataformat.Expression;
import dataformat.operation.CommaList;
import dataformat.value.Value;
import execute.Environment;

import java.util.List;

/**
 * @since 2/17/2019
 */
public class Parentheses implements Expression {

    private Expression contents;

    public Parentheses(Expression contents) {
        this.contents = contents;
    }

    @Override
    public Value evaluate(Environment env) {
        return contents.evaluate(env);
    }

    @Override
    public boolean matches(String pattern) {
        return contents.matches(pattern);
    }

    @Override
    public List<Expression> getChildren() {
        return List.of(contents);
    }

    public List<Expression> getContents() {
        return contents instanceof CommaList
                ? contents.getChildren()
                : List.of(contents);
    }

    @Override
    public String toString() {
        return "paren(" + contents + ")";
    }

    public static final Parentheses EMPTY_PARENS = new Parentheses(null) {

        @Override
        public Value evaluate(Environment env) {
            throw new RuntimeException("Unexpected symbol '()'");
        }

        @Override
        public boolean matches(String pattern) {
            return false;
        }

        @Override
        public List<Expression> getChildren() {
            return List.of();
        }

        @Override
        public List<Expression> getContents() {
            return List.of();
        }

        @Override
        public String toString() {
            return "paren()";
        }

    };

}
