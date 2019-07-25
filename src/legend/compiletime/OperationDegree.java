package legend.compiletime;

public enum OperationDegree {

    UNARYL(false, true),
    UNARYR(true, false),
    BINARY(true, true),
    SYMBOL(false, false);

    private boolean left, right;

    OperationDegree(boolean left, boolean right) {
        this.left = left;
        this.right = right;
    }

    public boolean matches(boolean leftOperand, boolean rightOperand) {
        return leftOperand == left && rightOperand == right;
    }

}
