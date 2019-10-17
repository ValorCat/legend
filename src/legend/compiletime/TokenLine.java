package legend.compiletime;

import java.util.ArrayList;
import java.util.Arrays;
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
        return indexOf(token, 0);
    }

    public int indexOf(String token, int startIndex) {
        for (int i = startIndex; i < size(); i++) {
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

    public boolean hasValueAt(int index) {
        return index >= 0 && index < size() && get(index).isValue();
    }

    public List<Token[]> split(String separator) {
        Token[] array = toArray(new Token[size()]);
        List<Token[]> partitions = new ArrayList<>();
        int last = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i].matches(separator)) {
                partitions.add(Arrays.copyOfRange(array, last + 1, i));
                last = i;
            }
        }
        if (array.length > 0) {
            partitions.add(Arrays.copyOfRange(array, last + 1, array.length));
        }
        return partitions;
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
