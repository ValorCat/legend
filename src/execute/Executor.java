package execute;

import dataformat.Expression;

import java.util.List;

/**
 * Execute a sequence of parsed syntax trees.
 * @since 12/24/2018
 */
public final class Executor {

    private Executor() {}

    public static void execute(Environment env) {
        List<Expression> statements = env.getProgram();
        while (env.getCounter() < statements.size()) {
            statements.get(env.getCounter()).evaluate(env);
            env.incrementCounter();
        }
    }

}
