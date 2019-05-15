package execute;

import expression.value.Value;
import statement.Statement;
import statement.structure.FlowController;

import java.util.List;
import java.util.Stack;

public class Program {

    public static Program PROGRAM;

    private List<Statement> program;
    private int counter;
    private boolean counterMoved;
    private Stack<Value> stack;
    private Scope globalNamespace;

    // temporary during migration
    private Stack<FlowController> controlStack;

    public Program(List<Statement> program) {
        this.program = program;
        this.counter = 0;
        this.counterMoved = false;
        this.stack = new Stack<>();
        this.globalNamespace = new Scope();
        this.controlStack = new Stack<>();
        TypeLibrary.updateNamespace(globalNamespace);
    }

    public Value execute() {
        return executeSubroutine(0, program.size(), globalNamespace);
    }

    public Value executeSubroutine(int startAddress, int endAddress, Scope scope) {
        counter = startAddress;
        while (counter <= endAddress) {
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

    public Stack<FlowController> getControlStack() {
        return controlStack;
    }

}
