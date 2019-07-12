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

    public ArgumentList(Value... args) {
        this.args = args;
        this.keywords = Map.of();
    }

    public ArgumentList(Parentheses args, Scope scope) {
        List<Value> argsList = new ArrayList<>();
        keywords = new HashMap<>();
        for (Expression child : args.getContents()) {
            if (child.matches(":")) {
                // now unused
                handleKeyword(child, scope);
            } else if (keywords.isEmpty()) {
                handleArgument(child, scope, argsList);
            } else {
                throw new RuntimeException("Sequential args must precede keyword args");
            }
        }
        this.args = argsList.toArray(new Value[0]);
    }

    private void handleArgument(Expression expr, Scope scope, List<Value> argsList) {
        argsList.add(expr.evaluate(scope));
    }

    private void handleKeyword(Expression expr, Scope scope) {
        List<Expression> children = expr.getChildren();
        keywords.put(children.get(0).getIdentifier(), children.get(1).evaluate(scope));
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
