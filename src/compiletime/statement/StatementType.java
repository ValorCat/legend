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

/**
 * Represents a particular type of statement, like 'return' or 'while'. Generally single-line statements should
 * implement the subinterface {@link BasicStatementType}, control structures should implement {@link BlockStatementType},
 * and control structure subclauses (like 'else') should implement {@link compiletime.statement.block.clause.ClauseStatementType}.
 * Only one instance of each statement type should be created, and it should reside in the {@link #STATEMENT_TYPES}
 * static field of this class.
 */
public interface StatementType {

    /**
     * The list of statement types that each line of tokens is compared against. The final entry should always be
     * {@link ExpressionStatement}, which is guaranteed to match against any sequence of tokens. Although Java requires
     * vthat interface fields be implicitly public, this variable does not need to be accessed outside of the
     * {@link StatementType#resolve} method.
     */
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

    /**
     * Return whether the specified token list likely corresponds to a statement of this type. This method should
     * implement the simplest possible method of matching against the tokens, such as checking if the first token is
     * 'while' for a while loop. More thorough parsing of the tokens for syntax errors is handled by the {@link #parse}
     * method. The default implementation of this method simply checks if the first token is an operator and has the
     * same name as this statement type (as determined by the {@link #getName()} method.
     * @param tokens the tokens to check against
     * @return true if the tokens likely represent this statement type, otherwise false
     */
    default boolean matches(TokenLine tokens) {
        Token initial = tokens.get(0);
        return initial.TYPE == TokenType.OPERATOR && initial.matches(getName());
    }

    /**
     * Extract the necessary values from the specified list of tokens and package them into a single object. If the
     * statement is malformed, log an interpreter error and skip the statement.
     * @param tokens the tokens to parse
     * @param parser the parser object to use for parsing subexpressions
     * @return a parsed statement
     * @throws compiletime.error.ParserException if the statement is malformed
     * @see Parser
     */
    Statement parse(TokenLine tokens, Parser parser);

    /**
     * Compile a parsed statement into a sequence of runtime instructions.
     * @param stmt the statement to compile
     * @param compiler the compiler object to use for compiling multiline statements (e.g. control structures)
     * @return the corresponding instructions
     * @see Compiler
     */
    List<Instruction> compile(Statement stmt, Compiler compiler);

    /**
     * Get the name of this statement, like 'if' or 'return'.
     * @return this statement's name
     */
    String getName();

    /**
     * Determine the statement type that most likely represents the specified list of tokens. Statement types are drawn
     * from the {@link #STATEMENT_TYPES} constant above. A token list that does not match any specific statement type
     * resolves to the {@link ExpressionStatement} type. Tokens are loosely parsed, so a statement that begins with 'if'
     * but in no other way looks like an if statement will still be resolved to an if statement by this method. Full
     * parsing, and error logging if necessary, is performed by the {@link #parse} method.
     * @param line the tokens to compare against
     * @return the statement type that most likely represents the specified tokens
     * @see #STATEMENT_TYPES
     */
    static StatementType resolve(TokenLine line) {
        for (StatementType type : STATEMENT_TYPES) {
            if (type.matches(line)) {
                return type;
            }
        }
        // this should never be reached
        throw new RuntimeException("Failed to resolve input to a statement type: " + line);
    }

}
