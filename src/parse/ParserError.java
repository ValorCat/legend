package parse;

import java.util.LinkedList;
import java.util.List;

/**
 * @since 2/28/2019
 */
public final class ParserError extends RuntimeException {

    private static List<ParserError> errors = new LinkedList<>();

    public static void log(ErrorDescription desc, int lineNumber, String message, Object... arguments) {
        ParserError e = error(desc, message, arguments);
        e.setLineNumber(lineNumber);
    }

    public static ParserError error(ErrorDescription desc, String message, Object... arguments) {
        for (int i = 0; i < arguments.length; i++) {
            if (arguments[i] instanceof Token) {
                arguments[i] = ((Token) arguments[i]).VALUE;
            }
        }
        ParserError error = new ParserError(desc, String.format(message, arguments));
        errors.add(error);
        return error;
    }

    public static boolean foundErrors() {
        return !errors.isEmpty();
    }

    public static List<ParserError> getErrors() {
        return errors;
    }

    private ErrorDescription desc;
    private int lineNumber;

    private ParserError(ErrorDescription desc, String message) {
        super(message);
        this.desc = desc;
    }

    public String getDetails() {
        return desc.DETAILS;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

}
