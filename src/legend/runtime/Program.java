package legend.runtime;

import legend.compiletime.expression.value.Value;
import legend.runtime.instruction.Instruction;
import legend.runtime.type.BuiltinType;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class Program {

    public static Program PROGRAM;

    private List<Instruction> program;
    private int counter;
    private boolean counterMoved;
    private Stack<Value> stack;
    private Scope globalNamespace;

    public Program(List<Instruction> program) {
        this.program = Collections.unmodifiableList(program);
        this.counter = 0;
        this.counterMoved = false;
        this.stack = new Stack<>();
        this.globalNamespace = new Scope();
        BuiltinType.addAllToScope(globalNamespace);
    }

    public Value execute() {
        return executeSubroutine(0, globalNamespace);
    }

    public Value executeSubroutine(int startAddress, Scope scope) {
        int lineCount = program.size();
        counter = startAddress;
        while (!scope.hasReturned() && counter < lineCount) {
            program.get(counter).execute(scope);
            if (!counterMoved) {
                counter++;
            }
            counterMoved = false;
        }
        return scope.getReturnValue();
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
        this.counterMoved = true;
    }

    public void offsetCounter(int jumpAmount) {
        this.counter += jumpAmount;
        this.counterMoved = true;
    }

    public Stack<Value> getStack() {
        return stack;
    }

}
