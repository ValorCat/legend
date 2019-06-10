package parse;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 3/1/2019
 */
public class TokenLine extends ArrayList<Token> {

    public static TokenLine EMPTY = new TokenLine(List.of(), 0);

    private int lineNumber;

    public TokenLine(List<Token> tokens, int lineNumber) {
        super(tokens);
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public TokenLine copy() {
        return new TokenLine(this, lineNumber);
    }

    public int indexOf(String token) {
        for (int i = 0; i < this.size(); i++) {
            if (get(i).matches(token)) {
                return i;
            }
        }
        return -1;
    }

    public boolean contains(String token) {
        return indexOf(token) >= 0;
    }

    @Override
    public TokenLine subList(int from, int to) {
        return new TokenLine(super.subList(from, to), lineNumber);
    }

    /**
     * Replace all the tokens between two indices with a single token.
     * @param newToken the replacement token
     * @param start the first index to remove
     * @param length the number of elements to remove after start
     */
    public void consolidate(Token newToken, int start, int length) {
        for (int i = 0; i < length; i++) {
            remove(start);
        }
        add(start, newToken);
    }

}
