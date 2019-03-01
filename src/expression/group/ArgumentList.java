package expression.group;

import execute.Environment;
import expression.Expression;
import expression.value.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @since 12/24/2018
 */
public class ArgumentList {

    private Value[] args;
    private Map<String, Value> keywords;
    private Value target;

    public ArgumentList(Value... args) {
        this.args = args;
        this.keywords = Map.of();
    }

    public ArgumentList(Parentheses args, Environment env) {
        List<Value> argsList = new ArrayList<>();
        keywords = new HashMap<>();
        for (Expression child : args.getContents()) {
            if (child.matches(":")) {
                handleKeyword(child, env);
            } else if (keywords.isEmpty()) {
                handleArgument(child, env, argsList);
            } else {
                throw new RuntimeException("Sequential args must precede keyword args");
            }
        }
        this.args = argsList.toArray(new Value[0]);
    }

    private void handleArgument(Expression expr, Environment env, List<Value> argsList) {
        argsList.add(expr.evaluate(env));
    }

    private void handleKeyword(Expression expr, Environment env) {
        List<Expression> children = expr.getChildren();
        keywords.put(children.get(0).getIdentifier(), children.get(1).evaluate(env));
    }

    public Value arg(int index) {
        return args[index];
    }

    public Value[] args() {
        return args;
    }

    public int size() {
        return args.length;
    }

    public Map<String, Value> keywords() {
        return keywords;
    }

    public Value target() {
        return target;
    }

    public void setTarget(Value target) {
        this.target = target;
    }

}
