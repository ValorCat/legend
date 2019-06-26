package legend.compiletime.error;

/**
 * @since 3/1/2019
 */
public class InterpreterException extends RuntimeException {

    private int lineNumber;

    public InterpreterException(String message) {
        super(message);
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

}
