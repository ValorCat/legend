package expression.group;

import execute.Environment;
import expression.Expression;
import expression.value.Value;

import java.util.List;

/**
 * @since 3/17/2019
 */
public class SquareBrackets implements Expression {

    private Expression contents;

    public SquareBrackets(Expression contents) {
        this.contents = contents;
    }

    @Override
    public Value evaluate(Environment env) {
        contents.evaluate(env);
        throw new RuntimeException("Missing index target before []");
    }

    @Override
    public boolean matches(String pattern) {
        return contents.matches(pattern);
    }

    @Override
    public List<Expression> getChildren() {
        return List.of(contents);
    }

    @Override
    public String toString() {
        return "bracket(" + contents + ")";
    }

}
