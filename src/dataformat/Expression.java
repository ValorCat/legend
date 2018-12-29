package dataformat;

import dataformat.value.Value;
import execute.Environment;

import java.util.List;

/**
 * @since 12/23/2018
 */
public interface Expression {

    Value evaluate(Environment env);

    default boolean matches(String pattern) {
        return false;
    }

    default String getIdentifier() {
        throw new RuntimeException("Expected identifier, got " + this);
    }

    default List<Expression> getChildren() {
        return List.of();
    }

}
