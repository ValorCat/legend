package legend.compiletime;

import legend.compiletime.expression.Expression;
import legend.compiletime.expression.Variable;
import legend.compiletime.expression.group.Parentheses;
import legend.compiletime.expression.group.SquareBrackets;
import legend.compiletime.expression.operation.*;

import java.util.Set;

import static legend.compiletime.OperationArity.*;

/**
 * This class stores the precedence level and arity (unary, binary, etc.) of all operators.
 * @see Operation
 * @see OperationArity
 * @since 12/22/2018
 */
public final class OperatorTable {

    public static final TableRow[] OPERATORS = {
            row(NULLARY, "*"),
            row(BINARY,  ".", "()", "[]"),
            row(UNARY_L, "-", "#"),
            row(UNARY_R, "%"),
            row(UNARY_L, "not"),
            row(BINARY,  "^"),
            row(BINARY,  "*", "/", "//", "mod"),
            row(BINARY,  "+", "-"),
            row(BINARY,  "==", "!=", "<", "<=", ">", ">="),
            row(BINARY,  "&", "?"),
            row(BINARY,  "in", "is", "is not", "not in", "to", "where"),
            row(BINARY,  "and", "or", "nor"),
            row(BINARY,  ":="),
            row(BINARY,  ","),
    };

    /*
    These two constants are used by the lexer to assign the correct type to unusual operators.
     */
    public static final Set<String> LONG_SYMBOLS = Set.of("//", "==", "!=", "<=", ">=", ":=", ">>");
    public static final Set<String> KEYWORDS = Set.of("and", "def", "else", "end", "for", "if", "in", "mod", "or",
            "nor", "not", "return", "to", "where", "while"
    );

    public static void parseBinary(TokenLine line, int operIndex) {
        String operator = line.get(operIndex).VALUE;
        Expression left = line.get(operIndex - 1).asExpression();
        Expression right = line.get(operIndex + 1).asExpression();

        Operation operation;
        switch (operator) {
            // unique operations
            case ".":       operation = new MemberSelectOperation(left, right);                 break;
            case "()":      operation = new InvokeOperation(left, ((Parentheses) right));       break;
            case "[]":      operation = new SubscriptOperation(left, ((SquareBrackets) right)); break;
            case "is":      operation = new IsOperation(left, right, false);                    break;
            case "is not":  operation = new IsOperation(left, right, true);                     break;
            case "where":   operation = new WhereOperation(left, right);                        break;
            case ":=":      operation = new InlineAssignOperation(left, right);                 break;
            case ",":       operation = new CommaOperation(left, right);                        break;

            // flipped operations
            case "in":
            case "not in":  operation = new FlippedBinaryOperation(operator, left, right);      break;

            // all other binary operations
            default:        operation = new BinaryOperation(operator, left, right);             break;
        }

        operation.parse(line, operIndex);
    }

    public static void parseUnary(TokenLine line, int operIndex, OperationArity arity) {
        String operator = line.get(operIndex).VALUE;
        Operation operation;
        if (arity == UNARY_L) {
            Expression operand = line.get(operIndex + 1).asExpression();
            operation = new LeftUnaryOperation(operator, operand);
        } else {
            Expression operand = line.get(operIndex - 1).asExpression();
            operation = new RightUnaryOperation(operator, operand);
        }
        operation.parse(line, operIndex);
    }

    public static void parseSymbol(TokenLine line, int symIndex) {
        String symbol = line.get(symIndex).VALUE;
        line.set(symIndex, Token.newExpression(symbol, new Variable(symbol)));
    }

    private static TableRow row(OperationArity arity, String... operators) {
        return new TableRow(arity, operators);
    }

    public static class TableRow {

        public final OperationArity ARITY;
        public final Set<String> OPERATORS;

        public TableRow(OperationArity arity, String[] operators) {
            this.ARITY = arity;
            this.OPERATORS = Set.of(operators);
        }

    }

}
