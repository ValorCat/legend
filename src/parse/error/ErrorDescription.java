package parse.error;

/**
 * @since 2/28/2019
 */
public enum ErrorDescription {

    BAD_ASSIGN(
            "The syntax for assignment is \"<variable> = <expression>\". Assignment statements cannot be nested " +
            "within another statement. For a C-style assignment expression, use the ':=' operator instead."),
    BAD_FOR_LOOP(
            "The syntax for a for loop is \"for <variable> in <expression>\"."),
    BAD_FUNC_DEF(
            "The syntax for a function definition is \"def <name> ( <optional parameters> )\"."),
    BAD_IF(
            "The syntax for an if statement is \"if <expression>\". An else-if branch can be formed with \"elsif " +
            "<expression>\", and an else branch can be formed with the lone keyword \"else\"."),
    BAD_JUMP_POINT(
            "The clauses 'else' and 'elsif' can only be used inside an 'if' block. Make sure that nested control " +
            "structures are closed with an 'end' statement before this 'else' or 'elsif' branch."),
    BAD_NESTING(
            "Every control structure ('if', 'for', 'def', etc.) must be accompanied by a corresponding 'end' " +
            "statement. An 'end' must appear alone on its own line. "),
    BAD_PARENS(
            "The interpreter found mismatched parentheses. Make sure that every opening parenthesis has a " +
            "corresponding closing parenthesis on the same line."),
    BAD_REPEAT(
            "The 'repeat' keyword begins an infinite loop block."),
    BAD_RETURN(
            "The syntax for a return statement is \"return <optional expression>\"."),
    BAD_STRING(
            "A string literal must be enclosed between single quotes ('), and the closing quote must appear on the " +
            "same line as the opening quote. Single quotes inside the string must be escaped with a backslash (\\)."),
    BAD_WHILE_LOOP(
            "The syntax for a while loop is \"while <expression>\"."),
    MISPLACED_SYMBOL(
            "The interpreter did not expect a symbol in that location."),
    MISSING_OPER(
            "The interpreter found two values with no operator between them. Operations like multiplication must be " +
            "written explicitly with the '*' operator."),
    UNKNOWN_OPER(
            "A symbol was used like an operator, but the interpreter doesn't recognize it.");

    public final String DETAILS;

    ErrorDescription(String details) {
        DETAILS = details;
    }

}
