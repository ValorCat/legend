package execute;

import dataformat.Expression;

import java.util.List;

/**
 * Execute a sequence of parsed syntax trees.
 * @since 12/24/2018
 */
public class Executor {

    public void execute(List<Expression> statements) {
        Environment env = new Environment(Environment.GLOBAL);
        while (env.getCounter() < statements.size()) {
            statements.get(env.getCounter()).evaluate(env);
            env.incrementCounter();
        }
    }

}
