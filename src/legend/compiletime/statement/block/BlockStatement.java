package legend.compiletime.statement.block;

import legend.compiletime.Compiler;
import legend.compiletime.Parser;
import legend.compiletime.TokenLine;
import legend.compiletime.statement.Statement;
import legend.compiletime.statement.block.clause.Clause;
import legend.runtime.instruction.Instruction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a multi-line statement, such as an if statement or for loop.
 * @see legend.compiletime.statement.block
 */
public interface BlockStatement extends Statement {

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
     * @param optional the optional following clauses of the block (e.g. 'else if' clauses in an if statement)
     * @return the corresponding instructions
     */
    List<Instruction> build(Clause base, List<Clause> optional);

    /**
     * Return whether this block statement supports the specified clause type.
     * @param name the name of the clause (e.g. 'else')
     * @return true if the clause is supported, otherwise false
     * @see legend.compiletime.statement.block.clause
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
    default List<Instruction> compile(Compiler compiler) {
        return compiler.compileBlockStatement(this);
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
