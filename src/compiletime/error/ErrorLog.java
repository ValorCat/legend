package compiletime.error;

import compiletime.Token;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @since 2/28/2019
 */
public final class ErrorLog {

    private static List<ParserException> errors = new ArrayList<>();

    public static void log(ErrorDescription desc, int lineNumber, String message, Object... arguments) {
        ParserException e = raise(desc, message, arguments);
        e.setLineNumber(lineNumber);
    }

    public static ParserException raise(ErrorDescription desc, String message, Object... arguments) {
        for (int i = 0; i < arguments.length; i++) {
            if (arguments[i] instanceof Token) {
                arguments[i] = ((Token) arguments[i]).VALUE;
            }
        }
        ParserException error = new ParserException(desc, String.format(message, arguments));
        errors.add(error);
        return error;
    }

    public static boolean foundErrors() {
        return !errors.isEmpty();
    }

    public static List<ParserException> getErrors() {
        errors.sort(Comparator.comparing(ParserException::getLineNumber));
        return errors;
    }

}
