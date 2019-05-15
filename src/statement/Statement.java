package statement;

import execute.Scope;

/**
 * Classes that implement Statement represent a single statement type, like
 * assignment or a for loop.
 * @since 2/28/2019
 */
public interface Statement {

    void execute(Scope scope);

}
