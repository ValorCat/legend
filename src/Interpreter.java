import compiletime.Compiler;
import compiletime.Lexer;
import compiletime.Parser;
import compiletime.TokenLine;
import compiletime.error.ErrorLog;
import compiletime.error.InterpreterException;
import compiletime.expression.value.LNull;
import compiletime.expression.value.Value;
import compiletime.statement.Statement;
import runtime.Program;
import runtime.instruction.Instruction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * This class is the public access point of the interpreter. You can execute a Legend file by calling
 * {@link #interpret(File)} and passing in the file object.
 *
 * Interpretation is a 4-step process:
 *
 * 1. The source code is divided into tokens by the {@link Lexer}.
 * 2. The tokens are parsed into statements and syntax trees by the {@link Parser}.
 * 3. The statements are compiled into instructions by the {@link Compiler}.
 * 4. The instructions are executed by the {@link Program}.
 *
 * @see compiletime.Token
 * @see compiletime.statement.Statement
 * @see runtime.instruction.Instruction
 * @since 1/15/2019
 */
public class Interpreter {

    /* This main method executes the provided source file in input.leg. If you don't want to make a new source file,
       you can simply edit the provided file and run this method. */
    public static void main(String[] args) {
        String sourcePath;
        if (args.length == 0) {
            sourcePath = "input.leg";
        } else if (args.length == 1) {
            sourcePath = args[0];
            if (Files.notExists(Paths.get(sourcePath))) {
                System.err.println("Cannot find specified input file: " + sourcePath);
                return;
            }
        } else {
            System.err.println("Unexpected argument " + args[1]);
            return;
        }
        interpret(new File(sourcePath));
    }

    /**
     * Execute a Legend source file.
     * @param sourceFile the file to execute
     */
    public static void interpret(File sourceFile) {
        if (Files.notExists(sourceFile.toPath())) {
            throw new RuntimeException("Couldn't locate source file: " + sourceFile.getAbsolutePath());
        }

        String fileName = sourceFile.getName();
        if (!fileName.endsWith(".leg")) {
            throw new RuntimeException("Couldn't read source file: does not end in .leg");
        }
        String input;

        try {
            input = new String(Files.readAllBytes(sourceFile.toPath()));
        } catch (IOException e) {
            throw new RuntimeException("Couldn't read source file: " + e.getMessage());
        }

        Lexer lexer = new Lexer();
        Parser parser = new Parser();
        Compiler compiler = new Compiler();

        List<TokenLine> tokens = lexer.tokenize(input);
        List<Statement> statements = parser.parse(tokens);
        List<Instruction> instructions = compiler.compile(statements);

        if (ErrorLog.foundErrors()) {
            List<InterpreterException> errors = ErrorLog.getErrors();
            System.err.printf("The interpeter encountered %d error(s) during parsing.\n\n", errors.size());
            for (InterpreterException e : errors) {
                System.err.printf("(line %d) %s\n", e.getLineNumber(), e.getMessage());
            }
            System.err.println("\nInterpretation aborted.\n");
            return;
        }

        Program.PROGRAM = new Program(instructions);
        Value returnValue = Program.PROGRAM.execute();

        if (returnValue != LNull.NULL) {
            System.out.println("Program returned value: " + returnValue.asString());
        }
    }

}
