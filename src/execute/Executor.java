package execute;

/**
 * Execute a sequence of parsed syntax trees.
 * @since 12/24/2018
 */
public final class Executor {

    private Executor() {}

    public static void execute(Environment env) {
        while (env.hasMoreInstructions()) {
            env.getInstruction().execute(env);
            env.incrementCounter();
        }
    }

}
