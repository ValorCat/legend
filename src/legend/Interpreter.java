package legend;

import legend.compiletime.Compiler;
import legend.compiletime.Lexer;
import legend.compiletime.Parser;
import legend.compiletime.TokenLine;
import legend.compiletime.error.ErrorLog;
import legend.compiletime.error.InterpreterException;
import legend.compiletime.expression.value.LNull;
import legend.compiletime.expression.value.Value;
import legend.compiletime.statement.Statement;
import legend.runtime.Program;
import legend.runtime.instruction.Instruction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

/**
 * This class is the public entry point of the interpreter. You can execute a Legend file by calling
 * {@link #interpret(File)} and passing in the file object.
 *
 * Interpretation is a 4-step process:
 *
 * 1. The source code is divided into tokens by the {@link Lexer}.
 * 2. The tokens are parsed into statements and syntax trees by the {@link Parser}.
 * 3. The statements are compiled into instructions by the {@link Compiler}.
 * 4. The instructions are executed by the {@link Program}.
 *
 * @see legend.compiletime.Token
 * @see legend.compiletime.statement.Statement
 * @see legend.runtime.instruction.Instruction
 * @since 1/15/2019
 */
public class Interpreter {

    public static void main(String[] args) {
        if (args.length != 1) {
            // the parentheses line up after backslashes are escaped
            System.err.println("Usage: .\\legend.exe <path\\to\\input.leg>          (on Windows)");
            System.err.println("Usage: java -jar legend.jar <path/to/input.leg>  (on Mac/Linux)");
        } else {
            String sourcePath = args[0];
            if (Files.exists(Paths.get(sourcePath))) {
                interpret(new File(sourcePath));
            } else {
                System.err.println("Cannot find specified source file: " + sourcePath);
            }
        }
        System.out.println("\nPress the enter key to exit...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        scanner.close();
    }

    /**
     * Execute a Legend source file.
     * @param sourceFile the file to execute
     */
    private static void interpret(File sourceFile) {
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
