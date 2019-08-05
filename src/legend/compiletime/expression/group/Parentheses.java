package legend.compiletime.expression.group;

import legend.compiletime.expression.Expression;
import legend.compiletime.expression.operation.CommaOperation;
import legend.compiletime.expression.value.Value;
import legend.runtime.Scope;

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
    public Value evaluate(Scope scope) {
        return contents.evaluate(scope);
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
        return contents instanceof CommaOperation
                ? contents.getChildren()
                : List.of(contents);
    }

    @Override
    public boolean isCompact() {
        return true;
    }

    @Override
    public String toString() {
        return "paren(" + contents + ")";
    }

    public static final Parentheses EMPTY_PARENS = new Parentheses(null) {

        @Override
        public Value evaluate(Scope scope) {
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
