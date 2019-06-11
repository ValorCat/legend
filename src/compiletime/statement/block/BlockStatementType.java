package compiletime.statement.block;

import compiletime.Compiler;
import compiletime.Parser;
import compiletime.TokenLine;
import compiletime.statement.Statement;
import compiletime.statement.StatementType;
import compiletime.statement.block.clause.Clause;
import runtime.instruction.Instruction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a multi-line statement, such as an if statement or for loop.
 * @see compiletime.statement.block
 */
public interface BlockStatementType extends StatementType {

    /**
     * Extract the necessary values from the specified list of tokens and package them into a single object. If the
     * statement is malformed, log an interpreter error and skip the statement.
     * @param tokens the list of tokens representing the statement's header
     * @param parser the parser object to use for parsing subexpressions
     * @return a parsed statement header
     */
    Statement parseHeader(TokenLine tokens, Parser parser);

    /**
     * Generate the runtime instructions for a block statement.
     * @param base the base clause of the block (e.g. 'if' clause in an if statement)
     * @param optional the optional following clauses of the block (e.g. 'elsif' clauses in an if statement)
     * @return the corresponding instructions
     */
    List<Instruction> build(Clause base, List<Clause> optional);

    /**
     * Return whether this block statement supports the specified clause type.
     * @param name the name of the clause (e.g. 'else')
     * @return true if the clause is supported, otherwise false
     * @see compiletime.statement.block.clause
     */
    default boolean allowsClause(String name) {
        return false;
    }

    @Override
    default Statement parse(TokenLine tokens, Parser parser) {
        parser.getControlStack().push(this);
        return parseHeader(tokens, parser);
    }

    @Override
    default List<Instruction> compile(Statement stmt, Compiler compiler) {
        return compiler.compileBlockStatement(stmt);
    }

    /**
     * Construct a list of the specified instructions. This is a utility function for implementing the {@link #build}
     * method. Each argument after the first should either be an Instruction object or a list of of Instruction objects.
     * Individual instructions are appended to the result list, while instruction lists are unpacked and each element is
     * appended. This is essentially "flattens" the arguments into a single, unnested list.
     * @param length the optimal capacity of the result list
     * @param instructions the instructions and instruction lists to append
     * @return the flattened list of instructions
     * @see Instruction
     */
    @SuppressWarnings("unchecked")
    default List<Instruction> asList(int length, Object... instructions) {
        List<Instruction> result = new ArrayList<>(length);
        for (Object instr : instructions) {
            if (instr instanceof Instruction) {
                result.add((Instruction) instr);
            } else if (instr instanceof List) {
                result.addAll((Collection<? extends Instruction>) instr);
            }
        }
        return result;
    }


}
