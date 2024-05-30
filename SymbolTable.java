import java.util.TreeSet;
import java.util.Set;

/**
 * Symbol table for DSL programs.
 *
 * @author djb
 * @version 2023.12.07
 */
public class SymbolTable {
    // The set of declared identifiers.
    private final Set<String> identifiers;

    /**
     * Create a symbol table.
     */
    public SymbolTable()
    {
        identifiers = new TreeSet<>();
    }

    /**
     * Declare the given identifier.
     * @param id The id to declare.
     * @return true If the id was not already declared, false otherwise.
     */
    public boolean declare(String id)
    {
        return identifiers.add(id);
    }

    /**
     * Check whether id is declared or not.
     * @param id The id to check.
     * @return true if the id is declared, false otherwise.
     */
    public boolean isDeclared(String id)
    {
        return identifiers.contains(id);
    }

}