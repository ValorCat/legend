package dataformat;

import execute.Environment;

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

    public ArgumentList(Expression root, Environment env) {
        List<Value> argsList = new ArrayList<>();
        keywords = new HashMap<>();
        if (root.matches(",")) {
            for (Expression child : root.getChildren()) {
                if (child.matches(":")) {
                    handleKeyword(child, env);
                } else if (keywords.isEmpty()) {
                    handleArgument(child, env, argsList);
                } else {
                    throw new RuntimeException("Sequential args must precede keyword args");
                }
            }
        } else if (root.matches(":")) {
            handleKeyword(root, env);
        } else {
            handleArgument(root, env, argsList);
        }
        args = argsList.toArray(new Value[0]);
    }

    private void handleArgument(Expression expr, Environment env, List<Value> argsList) {
        argsList.add(expr.evaluate(env));
    }

    private void handleKeyword(Expression expr, Environment env) {
        List<Expression> children = expr.getChildren();
        keywords.put(children.get(0).getIdentifier(), children.get(1).evaluate(env));
    }

    public Value[] args() {
        return args;
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
