package legend.compiletime.statement;

import legend.compiletime.Compiler;
import legend.compiletime.Parser;
import legend.compiletime.TokenLine;
import legend.compiletime.error.InterpreterException;
import legend.compiletime.statement.basic.*;
import legend.compiletime.statement.block.ForLoop;
import legend.compiletime.statement.block.FunctionDefinition;
import legend.compiletime.statement.block.IfStatement;
import legend.compiletime.statement.block.WhileLoop;
import legend.compiletime.statement.block.clause.ElseClause;
import legend.compiletime.statement.block.clause.ElseIfClause;
import legend.runtime.instruction.Instruction;

import java.util.List;

public interface Statement {

    List<Statement> STATEMENT_PROTOTYPES = List.of(
            new AssignmentStatement(),
            new ElseIfClause(),
            new ElseClause(),
            new EndStatement(),
            new ForLoop(),
            new FunctionDefinition(),
            new IfStatement(),
            new PrintStatement(),
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
        return tokens.get(0).matches(getName());
    }

    /**
     * Extract the necessary values from the specified list of tokens and package them into a single object. If the
     * statement is malformed, log an interpreter error and skip the statement.
     * @param tokens the tokens to parse
     * @param parser the parser object to use for parsing subexpressions
     * @return a parsed statement
     * @throws InterpreterException if the statement is malformed
     * @see Parser
     */
    Statement parse(TokenLine tokens, Parser parser);

    /**
     * Compile a parsed statement into a sequence of runtime instructions.
     * @param compiler the compiler object to use for compiling multiline statements (e.g. control structures)
     * @return the corresponding instructions
     * @see Compiler
     */
    List<Instruction> compile(Compiler compiler);

    /**
     * Get the name of this statement, like 'if' or 'return'.
     * @return this statement's name
     */
    String getName();

    /**
     * Determine the statement type that most likely represents the specified list of tokens. Statement types are drawn
     * from the {@link #STATEMENT_PROTOTYPES} constant above. A token list that does not match any specific statement type
     * resolves to the {@link ExpressionStatement} type. Tokens are loosely parsed, so a statement that begins with 'if'
     * but in no other way looks like an if statement will still be resolved to an if statement by this method. Full
     * parsing, and error logging if necessary, is performed by the {@link #parse} method.
     * @param line the tokens to compare against
     * @return the statement type that most likely represents the specified tokens
     * @see #STATEMENT_PROTOTYPES
     */
    static Statement resolve(TokenLine line, Parser parser) {
        for (Statement stmt : STATEMENT_PROTOTYPES) {
            if (stmt.matches(line)) {
                return stmt.parse(line, parser);
            }
        }
        // this should never be reached
        throw new RuntimeException("Failed to resolve input to a statement type: " + line);
    }

}
