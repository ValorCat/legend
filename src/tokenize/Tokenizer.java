package tokenize;

import parse.OperatorTable;
import tokenize.Token.TokenType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static java.lang.Character.isLetterOrDigit;
import static java.lang.Character.isWhitespace;
import static tokenize.Token.TokenType.*;

/**
 * @since 12/21/2018
 */
public class Tokenizer {

    public static void main(String[] args) throws IOException {
        String input = new String(Files.readAllBytes(Paths.get("src/input.txt")));
        Tokenizer t = new Tokenizer();
        for (List<Token> line : t.tokenize(input)) {
            for (Token token : line) {
                System.out.print(token + "  ");
            }
            System.out.println();
        }
    }

    private List<List<Token>> tokenized;
    private List<Token> currStatement;
    private StringBuilder currToken;
    private char prev;
    private char stringType;

    public List<List<Token>> tokenize(String input) {
        initialize();
        if (!input.endsWith("\n")) {
            input += "\n";
        }
        for (int i = 0; i < input.length(); i++) {
            char curr = input.charAt(i);
            breakToken(curr);
            breakStatement(curr);
            if (curr == '~') {
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
    }

    private void buildToken(char curr) {
        if (curr == '\'' || curr == '"') {
            if (stringType == 0) {
                stringType = curr;
                currToken.append(curr);
            } else {
                stringType = 0;
            }
        } else if (stringType != 0 || !isWhitespace(curr)) {
            currToken.append(curr);
        }
    }

    private void breakToken(char curr) {
        TokenType type = null;

        if (stringType == curr) {
            type = LITERAL;
            currToken.deleteCharAt(0);
        } else if (stringType == 0) {
            if (isValue(prev) && !isValue(curr)) {
                if (isKeyword(currToken)) {
                    type = OPERATOR;
                } else if (isLiteral(currToken)) {
                    type = LITERAL;
                } else {
                    type = IDENTIFIER;
                }
            } else if (isSymbol(prev) && !isLongSymbol(prev, curr)) {
                type = OPERATOR;
            }
        }

        if (type != null) {
            currStatement.add(new Token(type, currToken.toString()));
            currToken.setLength(0);
        }
    }

    private void breakStatement(char curr) {
        if (curr == '\n' || curr == '~') {
            if (stringType != 0) {
                throw new RuntimeException("Unterminated string literal");
            }
            if (!currStatement.isEmpty()) {
                tokenized.add(currStatement);
                aggregateGroups(currStatement);
                currStatement = new ArrayList<>();
            }
        }
    }

    private static boolean isValue(char c) {
        return isLetterOrDigit(c) || c == '_';
    }

    private static boolean isKeyword(CharSequence token) {
        return List.of("else", "end", "match", "repeat", "to")
                .contains(token.toString());
    }

    private static boolean isLiteral(StringBuilder token) {
        String str = token.toString();
        return str.matches("\\d+") || str.equals("true") || str.equals("false");
    }

    private static boolean isSymbol(char c) {
        return "!@#$%^&*()-=+[]{};:,.<>/?".indexOf(c) >= 0;
    }

    private static boolean isLongSymbol(char first, char second) {
        String symbol = new String(new char[] {first, second});
        return OperatorTable.LONG_SYMBOLS.contains(symbol);
    }

    private static void aggregateGroups(List<Token> tokens) {
        Stack<TokenType> delimiters = new Stack<>();
        Stack<Integer> starts = new Stack<>();
        for (int i = 0; i < tokens.size(); i++) {
            String value = tokens.get(i).VALUE;
            if (value.equals("(")) {
                if (i > 0 && tokens.get(i - 1).TYPE != LITERAL && tokens.get(i - 1).TYPE != OPERATOR) {
                    tokens.add(i, new Token(OPERATOR, "call"));
                    i++;
                }
                delimiters.push(PARENS);
                starts.push(i);
            } else if (value.equals(")")) {
                if (delimiters.isEmpty() || delimiters.pop() != PARENS) {
                    throw new RuntimeException("Unexpected ')'");
                }
                int start = starts.pop();
                List<Token> subTokens = tokens.subList(start + 1, i);
                Token gathered = new Token(PARENS, new ArrayList<>(subTokens));
                Token.consolidate(tokens, gathered, start, i - start + 1);
                i = start;
            }
        }
        if (!delimiters.isEmpty()) {
            throw new RuntimeException("Expected ')'");
        }
    }

}
