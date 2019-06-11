package compiletime;

import compiletime.Token.TokenType;
import compiletime.error.ErrorLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static compiletime.Token.TokenType.*;

/**
 * Lexing is the first step in interpretation. The lexer divides the raw source code into atomic units called tokens and
 * attaches type information like 'operator', 'identifier', and so on (see {@link Token.TokenType} for all types). The
 * lexified tokens produced by this class can be passed directly to {@link Parser} for parsing. Tokenization eliminates
 * whitespace and comments, but preserves original line numbers for use in error messages. For example:
 *
 *  1  if #name == 0
 *  2      "You must enter a name."
 *  3      return
 *  4  end
 *
 * The above source code produces the following token stream:
 *
 *    [op 'if', op '#', id 'name', op '==', lt '0'] line=1
 *    [ps 'You must enter a name']                  line=2
 *    [op 'return']                                 line=3
 *    [op 'end']                                    line=4
 *
 * Each line in the output is represented by a {@link TokenLine} object that contains a list of {@link Token} objects.
 *
 * @see Token
 * @see Token.TokenType
 * @see TokenLine
 * @since 12/21/2018
 */
public class Lexer {

    private static final String SYMBOLS = "#%^*()-=+[]:,.<>/?";

    private List<TokenLine> tokenized;      // completed lines
    private List<Token> currStatement;      // current line being tokenized
    private StringBuilder currToken;        // current token being built
    private char prev;                      // the previous character
    private char stringType;                // type of the enclosing string -- either '\'', '"', or '\0' for n/a
    private int lineNumber;                 // current line number in source file

    /**
     * Translate raw source code into lexified tokens, as detailed at the top of this class.
     * @param input the raw source code
     * @return a list of lexified lines
     */
    public List<TokenLine> tokenize(String input) {
        initialize();

        // add a final newline if it's missing
        if (!input.endsWith("\n")) {
            input += "\n";
        }

        // go through each character
        for (int i = 0; i < input.length(); i++) {
            char curr = input.charAt(i);
            breakToken(curr);
            breakStatement(curr);

            // check for comments
            if (curr == '~') {
                // jump to end of comment
                int lineEnd = input.indexOf('\n', i);
                i = lineEnd < 0 ? input.length() : lineEnd;
                curr = '\n';
            } else {
                buildToken(curr);
            }
            prev = curr;
        }
        return tokenized;
    }

    private void initialize() {
        tokenized = new ArrayList<>();
        currStatement = new ArrayList<>();
        currToken = new StringBuilder();
        prev = 0;
        stringType = 0;
        lineNumber = 1;
    }

    /**
     * Add a character to the current token.
     * @param c the character to add
     */
    private void buildToken(char c) {
        // check if the character is the start or end of a string
        if (c == '\'' || c == '"') {
            if (stringType == 0) {
                // we are now inside a string
                stringType = c;
                currToken.append(c);
            } else {
                // we are now outside a string
                stringType = 0;
            }
        } else if (stringType != 0 || !Character.isWhitespace(c)) {
            currToken.append(c);
        }
    }

    /**
     * Determine if a character is part of the current token. If it isn't, the
     * current token is completed and added to the final token list.
     * @param c the character to check with
     */
    private void breakToken(char c) {
        TokenType type = null;
        if (stringType == c) {
            // we are at the end of a string
            type = c == '\'' ? LITERAL : PRINT_STRING;
            currToken.deleteCharAt(0);
        } else if (stringType == 0) {
            // we are not in a string
            if (isValue(prev)) {
                if (!isValue(c)) {
                    if (isKeyword(currToken) || isContextKeyword(currToken, currStatement)) {
                        type = OPERATOR;
                    } else if (isLiteral(currToken)) {
                        type = LITERAL;
                    } else {
                        type = IDENTIFIER;
                    }
                }
            } else if (isSymbol(prev)) {
                if (!isLongSymbol(prev, c)) {
                    type = OPERATOR;
                }
            } else if (prev != 0 && !Character.isWhitespace(prev) && prev != '\'' && prev != '"') {
                type = INVALID;
            }
        }

        // check if the current token is finished
        if (type != null) {
            Token token = Token.newToken(type, currToken.toString());
            currStatement.add(token);
            currToken.setLength(0);
        }
    }

    /**
     * Determine if a character marks the end of the current statement. If it
     * does, the statement is added to the final list of statements.
     * @param c the character to check with
     */
    private void breakStatement(char c) {
        if (c == '\n' || c == '~') {
            if (stringType != 0) {
                ErrorLog.log(lineNumber, "Unterminated string literal");
                currToken.setLength(0);
                stringType = 0;
            }
            if (!currStatement.isEmpty()) {
                TokenLine line = new TokenLine(currStatement, lineNumber);
                aggregateGroups(line);
                tokenized.add(line);
                currStatement = new ArrayList<>();
            }
            lineNumber++;
        }
    }

    /**
     * Determine if a character is a valid part of an identifier, i.e. it
     * is a letter, digit, or underscore.
     * @param c the character to check
     * @return whether the character is a valid part of an identifier
     */
    private static boolean isValue(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    /**
     * Determine if a token is a valid keyword, like 'if' or 'while'.
     * @param token the token to check
     * @return whether the token is a valid keyword
     */
    private static boolean isKeyword(CharSequence token) {
        return OperatorTable.KEYWORDS.contains(token.toString());
    }

    /**
     * Determine if a token is a literal integer or boolean. This method does not
     * handle string literals.
     * @param token the token to check
     * @return whether the token is a valid literal
     */
    private static boolean isLiteral(CharSequence token) {
        String str = token.toString();
        return str.matches("\\d+") || str.equals("true") || str.equals("false") || str.equals("null");
    }

    /**
     * Determine if a character is punctuation.
     * @param c the character to check
     * @return whether the character is punctuation
     */
    private static boolean isSymbol(char c) {
        return SYMBOLS.indexOf(c) >= 0;
    }

    /**
     * Determine if two characters form a valid token, like '!=' and '<='.
     * @param first the first character in the symbol
     * @param second the second character in the symbol
     * @return whether the two characters form a valid token
     */
    private static boolean isLongSymbol(char first, char second) {
        String symbol = new String(new char[] {first, second});
        return OperatorTable.LONG_SYMBOLS.contains(symbol);
    }

    /**
     * Determine if a token is a "context keyword": a token that acts as
     * a keyword in certain environments, but is otherwise a normal
     * identifier. For example, the token "in" can be used as a variable
     * name, but has special meaning in a for loop (for X in Y).
     * @param token the token to check
     * @param statement the part of the statement prior to this token
     * @return whether this token is a context keyword
     */
    private static boolean isContextKeyword(CharSequence token, List<Token> statement) {
        // in is only a keyword within for loop headers
        if (token.toString().equals("in")) {
            int size = statement.size();
            return size > 1
                    && statement.get(0).matches("for")
                    && !statement.contains(Token.newOperator("in"))
                    && (size == 2 || !statement.get(size - 1).matches(","));
        }
        return false;
    }

    /**
     * Search a finished statement for matching pairs of (), [], or {}, and
     * combine all the tokens inbetween into a single aggregate token.
     * @param line the statement to search
     */
    private void aggregateGroups(TokenLine line) {
        Stack<String> delimiters = new Stack<>();
        Stack<Integer> starts = new Stack<>();
        for (int i = 0; i < line.size(); i++) {
            String token = line.get(i).VALUE;
            switch (token) {
                case "(": case "[":
                    delimiters.push(getMatchingWrapper(token));
                    starts.push(i);
                    break;
                case ")": case "]":
                    if (delimiters.isEmpty() || !delimiters.pop().equals(token)) {
                        ErrorLog.log(lineNumber, "Extraneous '%s'", token);
                        line.remove(i);
                        i--;
                    } else {
                        int start = starts.pop();
                        TokenLine sublist = line.subList(start + 1, i);
                        Token gathered = Token.newGroup(getMatchingWrapper(token) + token, sublist);
                        line.consolidate(gathered, start, i - start + 1);
                        i = start;
                    }
            }
        }
        if (!delimiters.isEmpty()) {
            String found = delimiters.peek();
            String missing = getMatchingWrapper(found);
            ErrorLog.log(lineNumber, "Missing '%s' to close '%s'", missing, found);
            // fix the mismatch so more errors aren't raised
            while (!delimiters.isEmpty()) {
                line.add(Token.newOperator(getMatchingWrapper(delimiters.pop())));
            }
            aggregateGroups(line);
        }
    }

    private static String getMatchingWrapper(String symbol) {
        switch (symbol) {
            case "(": return ")";
            case "[": return "]";
            case "{": return "}";
            case ")": return "(";
            case "]": return "[";
            case "}": return "{";
        }
        throw new IllegalArgumentException("Not a wrapper: " + symbol);
    }

}
