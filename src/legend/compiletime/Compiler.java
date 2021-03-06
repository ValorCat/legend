package legend.compiletime;

import legend.compiletime.statement.Statement;
import legend.compiletime.statement.basic.EndStatement;
import legend.compiletime.statement.block.BlockStatementType;
import legend.compiletime.statement.block.clause.Clause;
import legend.compiletime.statement.block.clause.ClauseStatementType;
import legend.runtime.instruction.Instruction;

import java.util.ArrayList;
import java.util.List;

/**
 * Compilation is the third step in interpretation, after parsing. The compiler translates the sequence of parsed
 * statements generated by the {@link Parser} into runtime instructions that can be executed. Although statements are
 * compiled to the instruction format, nested values and expressions remain as Java objects and retain their format from
 * parsing. For example:
 *
 *   for i in 1 to 10
 *       i show
 *   end
 *
 * The above source code compiles to the following instructions:
 *
 *   psh unop(biop(.(1 to) 10) _loop)   ~ push 1.to(10)._loop()
 *   jun 4 unop(stack has_next)         ~ jump 4 unless stack.has_next()
 *   set j unop(stack next)             ~ set i = stack.next()
 *   evl unop(j show)                   ~ evaluate i.show()
 *   jmp -3                             ~ jump -3
 *   pop                                ~ pop and discard stack
 *
 *
 * @see Parser
 * @see Statement
 * @see Instruction
 */
public class Compiler {

    private int address;                // current index in statements
    private List<Statement> statements; // all statements to compile

    /**
     * Translate a sequence of statements into executable instructions, as detailed at the top of this class.
     * @param statements the statements to compile
     * @return the compiled instructions
     */
    public List<Instruction> compile(List<Statement> statements) {
        this.statements = statements;
        address = -1;
        return compileBlock();
    }

    /**
     * Beginning at the current address, compile all statements until an 'end' or clause header is reached. This method
     * is recursively called if a nested block is encountered.
     * @return the compiled instructions
     */
    private List<Instruction> compileBlock() {
        List<Instruction> output = new ArrayList<>();
        for (address++; address < statements.size(); address++) {
            Statement stmt = statements.get(address);
            if (stmt.TYPE instanceof EndStatement || stmt.TYPE instanceof ClauseStatementType) {
                break;
            }
            output.addAll(stmt.compile(this));
        }
        return output;
    }

    /**
     * Compile a control structure. Each clause defers to {@link #compileBlock()} for generating instructions for the
     * clause's body.
     * @param header the header statement of this control structure
     * @return the compiled instructions
     * @see BlockStatementType
     */
    public List<Instruction> compileBlockStatement(Statement header) {
        Clause base = new Clause(header, compileBlock());
        List<Clause> optional = new ArrayList<>(0);
        while (!atEnd()) {
            Statement clauseHeader = statements.get(address);
            List<Instruction> clauseBody = compileBlock();
            optional.add(new Clause(clauseHeader, clauseBody));
        }
        return ((BlockStatementType) header.TYPE).build(base, optional);
    }

    /**
     * Return whether the current statement is 'end'.
     * @return true if the current statement is 'end', otherwise false
     */
    private boolean atEnd() {
        return statements.get(address).TYPE instanceof EndStatement;
    }

}
