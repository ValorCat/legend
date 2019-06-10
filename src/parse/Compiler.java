package parse;

import instruction.Instruction;
import statement.Statement;
import statement.basic.EndStatement;
import statement.block.BlockStatementType;
import statement.block.clause.ClauseData;
import statement.block.clause.ClauseStatementType;

import java.util.ArrayList;
import java.util.List;

public class Compiler {

    private int address;
    private List<Statement> statements;

    public List<Instruction> compile(List<Statement> statements) {
        this.statements = statements;
        address = -1;
        return compileBlock();
    }

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

    public List<Instruction> compileBlockStatement(Statement header) {
        List<ClauseData> clauses = new ArrayList<>(1);
        List<Instruction> baseClauseBody = compileBlock();
        clauses.add(new ClauseData("base", header, baseClauseBody));
        while (!atEnd()) {
            Statement clauseHeader = statements.get(address);
            List<Instruction> clauseBody = compileBlock();
            clauses.add(new ClauseData(clauseHeader.TYPE.getKeyword(), clauseHeader, clauseBody));
        }
        return ((BlockStatementType) header.TYPE).build(clauses);
    }

    private boolean atEnd() {
        return statements.get(address).TYPE instanceof EndStatement;
    }

}
