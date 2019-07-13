package legend.compiletime.expression.group;

import legend.compiletime.expression.Expression;
import legend.compiletime.expression.value.Value;
import legend.runtime.Scope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represent a sequence of positional and/or keyword arguments in a
 * function call. If the function is a method, this class also stores
 * the method target (the object it is called upon).
 * @see legend.compiletime.expression.operation.FunctionCall
 * @see legend.compiletime.expression.value.function.LFunction
 * @since 12/24/2018
 */
public class ArgumentList {

    private Value[] args;
    private Map<String, Value> keywords;
    private Value target;
    private Scope scope;

    public ArgumentList(Scope scope, Value... args) {
        this.args = args;
        this.keywords = Map.of();
        this.scope = scope;
    }

    public ArgumentList(Scope scope, Parentheses args) {
        List<Value> argsList = new ArrayList<>();
        Map<String, Value> keywords = new HashMap<>();
        for (Expression child : args.getContents()) {
            if (child.matches(":")) {
                // now unused
                handleKeyword(child, scope, keywords);
            } else if (keywords.isEmpty()) {
                handleArgument(child, scope, argsList);
            } else {
                throw new RuntimeException("Sequential args must precede keyword args");
            }
        }
        this.args = argsList.toArray(new Value[0]);
        this.keywords = keywords;
        this.scope = scope;
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

    public Scope scope() {
        return scope;
    }

    public void setTarget(Value target) {
        this.target = target;
    }

    private static void handleArgument(Expression expr, Scope scope, List<Value> argsList) {
        argsList.add(expr.evaluate(scope));
    }

    private static void handleKeyword(Expression expr, Scope scope, Map<String, Value> keywords) {
        List<Expression> children = expr.getChildren();
        keywords.put(children.get(0).getIdentifier(), children.get(1).evaluate(scope));
    }

}
