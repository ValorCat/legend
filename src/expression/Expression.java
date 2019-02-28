package expression;

import statement.Statement;
import expression.value.Value;
import execute.Environment;

import java.util.Collections;
import java.util.List;

/**
 * Classes that implement this interface represent nodes in a
 * parsed statement's syntax tree.
 * @since 12/23/2018
 */
public interface Expression extends Statement {

    /**
     * Execute this expression, also executing any sub-expressions as necessary.
     * @param env the environment in which to execute this expression
     * @return the value that this expression resolved to
     */
    Value evaluate(Environment env);

    /**
     * Determine if this expression's root is equal to a particular string.
     * @param pattern the string to check against
     * @return whether this expression's root equals the pattern
     */
    default boolean matches(String pattern) {
        return false;
    }

    /**
     * Return this expression if it is an identifier, or else throw an exception.
     * @return a variable identifier
     */
    default String getIdentifier() {
        throw new RuntimeException("Expected identifier, got " + this);
    }

    /**
     * Return this expression's sub-expressions. If it has none, return an
     * empty list instead.
     * @return this expressions's sub-expressions, if any
     */
    default List<Expression> getChildren() {
        return Collections.emptyList();
    }

    @Override
    default void execute(Environment env) {
        evaluate(env);
    }

}
