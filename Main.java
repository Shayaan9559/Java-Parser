import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Recognizer for DSL programs.
 *
 * @author djb
 * @version 2023.12.08
 */
public class Main {

    /**
     * Run the program with a single program argument: a file
     * with a .dsl suffix containing DSL code.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java Main file.dsl");
        } else {
            String filename = args[0];
            if (filename.endsWith(".dsl")) {
                try (BufferedReader input
                             = new BufferedReader(new FileReader(filename))) {
                    Tokenizer tk = new Tokenizer(input);
                    SymbolTable st = new SymbolTable();
                    Parser p = new Parser(tk, st);
                    if(p.parseProgram()) {
                        System.out.println("ok");
                    }
                    else {
                        System.out.println("error");
                    }
                }
                catch(SyntaxException ex) {
                    System.out.println("error");
                }
                catch (Exception ex) {
                    System.err.println("Unexpected exception parsing: " + filename);
                    System.err.println(ex);
                    System.out.println("error");
                }
            } else {
                System.err.println("Unrecognised file type: " + filename);
            }
        }
    }

}
