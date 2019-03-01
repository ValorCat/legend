package parse;

/**
 * @since 3/1/2019
 */
public class ParserException extends RuntimeException {

    private ErrorDescription desc;
    private int lineNumber;

    public ParserException(ErrorDescription desc, String message) {
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
