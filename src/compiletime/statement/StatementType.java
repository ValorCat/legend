package compiletime.statement;

import compiletime.Compiler;
import compiletime.Parser;
import compiletime.Token;
import compiletime.Token.TokenType;
import compiletime.TokenLine;
import compiletime.statement.basic.*;
import compiletime.statement.block.*;
import compiletime.statement.block.clause.ElseClause;
import compiletime.statement.block.clause.ElsifClause;
import runtime.instruction.Instruction;

import java.util.List;

public interface StatementType {

    Statement parse(TokenLine tokens, Parser parser);
    List<Instruction> compile(Statement stmt, Compiler compiler);
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
