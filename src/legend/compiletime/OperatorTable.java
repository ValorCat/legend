package legend.compiletime;

import legend.compiletime.expression.Expression;
import legend.compiletime.expression.group.Parentheses;
import legend.compiletime.expression.group.SquareBrackets;
import legend.compiletime.expression.operation.*;

import java.util.Set;

import static legend.compiletime.OperationDegree.*;

/**
 * This class stores valid operators and their precedence levels. It also instantiates
 * the expression objects that form syntax trees during parsing.
 * @since 12/22/2018
 */
public final class OperatorTable {

    /*
    Below is the list of operators defined in Legend, ordered by descending precedence
    level. Some operators, like "[]", don't correspond to a particular token, but
    instead an arrangement of other tokens. For example, a function call is indicated
    by an identifier followed by parentheses. These special operations are detected
    during tokenization and their corresponding operators are inserted implicitly.
     */
    public static final TableRow[] OPERATORS = {
            row(BINARY, ".", "()", "[]"),
            row(UNARYL, "-", "#"),
            row(UNARYR, "%", "?"),
            row(UNARYL, "not"),
            row(BINARY, "^"),
            row(BINARY, "*", "/", "//", "mod"),
            row(BINARY, "+", "-"),
            row(BINARY, "==", "!=", "<", "<=", ">", ">="),
            row(BINARY, "&", "?"),
            row(BINARY, "in", "is", "is not", "not in", "to"),
            row(BINARY, "and", "or", "nor"),
            row(BINARY, ":="),
            row(BINARY, ","),
    };

    /*
    These two constants are used by the lexer to assign the correct type to unusual operators.
     */
    public static final Set<String> LONG_SYMBOLS = Set.of("//", "==", "!=", "<=", ">=", ":=");
    public static final Set<String> KEYWORDS = Set.of("and", "def", "else", "end", "for", "if", "in", "mod", "or",
            "nor", "not", "repeat", "return", "to", "while"
    );

    public static void parseBinary(TokenLine line, int operIndex) {
        String operator = line.get(operIndex).VALUE;
        Expression left = line.get(operIndex - 1).asExpression();
        Expression right = line.get(operIndex + 1).asExpression();

        Operation operation;
        switch (operator) {
            case ".":       operation = new MemberSelectOperation(left, right);                 break;
            case "()":      operation = new InvokeOperation(left, ((Parentheses) right));       break;
            case "[]":      operation = new SubscriptOperation(left, ((SquareBrackets) right)); break;
            case "is":      operation = new IsOperation(left, right, false);                    break;
            case "is not":  operation = new IsOperation(left, right, true);                     break;
            case ":=":      operation = new InlineAssignOperation(left, right);                 break;
            case ",":       operation = new CommaOperation(left, right);                        break;
            case "in":
            case "not in":  operation = new FlippedBinaryOperation(operator, left, right);      break;
            default:        operation = new BinaryOperation(operator, left, right);             break;
        }

        operation.parse(line, operIndex);
    }

    public static void parseUnary(TokenLine line, int operIndex, OperationDegree degree) {
        String operator = line.get(operIndex).VALUE;
        Operation operation;
        if (degree == UNARYL) {
            Expression operand = line.get(operIndex + 1).asExpression();
            operation = new LeftUnaryOperation(operator, operand);
        } else {
            Expression operand = line.get(operIndex - 1).asExpression();
            operation = new RightUnaryOperation(operator, operand);
        }
        operation.parse(line, operIndex);
    }

    private static TableRow row(OperationDegree degree, String... operators) {
        return new TableRow(degree, operators);
    }

    public static class TableRow {

        public final OperationDegree DEGREE;
        public final Set<String> OPERATORS;

        public TableRow(OperationDegree DEGREE, String[] OPERATORS) {
            this.DEGREE = DEGREE;
            this.OPERATORS = Set.of(OPERATORS);
        }

    }

}
