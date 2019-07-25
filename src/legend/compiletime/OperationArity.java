package legend.compiletime;

/**
 * Represents the arity of an operation, i.e. the number of operands it accepts.
 */
public enum OperationArity {

    UNARY_L(false, true),   // 1 operand, operator on left
    UNARY_R(true, false),   // 1 operand, operator on right
    BINARY(true, true),     // 2 operands
    NULLARY(false, false);  // no operands

    private boolean left, right;

    OperationArity(boolean left, boolean right) {
        this.left = left;
        this.right = right;
    }

    public boolean matches(boolean leftOperand, boolean rightOperand) {
        return leftOperand == left && rightOperand == right;
    }

    public boolean hasLeft() {
        return left;
    }

    public boolean hasRight() {
        return right;
    }

}
