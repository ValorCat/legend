package statement;

import instruction.Instruction;
import parse.Parser;
import parse.Token;
import parse.Token.TokenType;
import parse.TokenLine;
import statement.basic.*;
import statement.block.*;
import statement.block.clause.ElseClause;
import statement.block.clause.ElsifClause;

import java.util.List;

public interface StatementType {

    StatementData parse(TokenLine tokens, Parser parser);
    List<Instruction> compile(StatementData data, Parser parser);
    String getKeyword();

    default boolean matches(TokenLine tokens) {
        Token initial = tokens.get(0);
        return initial.TYPE == TokenType.OPERATOR && initial.matches(getKeyword());
    }

    List<StatementType> STATEMENT_TYPES = List.of(
            new AssignmentStatement(),
            new ElseClause(),
            new ElsifClause(),
            new EndStatement(),
            new ForLoop(),
            new FunctionDefinition(),
            new IfStatement(),
            new PrintStringStatement(),
            new RepeatLoop(),
            new ReturnStatement(),
            new WhileLoop(),
            new ExpressionStatement()
    );

    static StatementType resolve(TokenLine line) {
        for (StatementType stmt : STATEMENT_TYPES) {
            if (stmt.matches(line)) {
                return stmt;
            }
        }
        // this should never be reached
        throw new RuntimeException("Failed to resolve input to a statement type: " + line);
    }

    static boolean isEnd(TokenLine line) {
        return line.get(0).TYPE == TokenType.OPERATOR && line.get(0).matches("end");
    }

}
