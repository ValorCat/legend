package legend.compiletime.error;

import legend.compiletime.Token;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @since 2/28/2019
 */
public final class ErrorLog {

    private static List<InterpreterException> errors = new ArrayList<>();

    public static void log(int lineNumber, String message, Object... arguments) {
        InterpreterException e = get(message, arguments);
        e.setLineNumber(lineNumber);
    }

    public static InterpreterException get(String message, Object... arguments) {
        for (int i = 0; i < arguments.length; i++) {
            if (arguments[i] instanceof Token) {
                arguments[i] = ((Token) arguments[i]).VALUE;
            }
        }
        InterpreterException error = new InterpreterException(String.format(message, arguments));
        errors.add(error);
        return error;
    }

    public static boolean foundErrors() {
        return !errors.isEmpty();
    }

    public static List<InterpreterException> getErrors() {
        errors.sort(Comparator.comparing(InterpreterException::getLineNumber));
        return errors;
    }

}
