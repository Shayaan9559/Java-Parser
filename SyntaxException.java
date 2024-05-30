/**
 * Represent the occurrence of a syntax error.
 * In practice, this would not be handled by throwing an exception
 * but error recovery is not expected for this project.
 * @author djb
 * @version 2023.12.08
 */
public class SyntaxException extends RuntimeException
{
    /**
     * Create a SyntaxException
     * @param message The syntax error that has occurred.
     */
    public SyntaxException(String message)
    {
        super(message);
    }

    public SyntaxException()
    {
        super();
    }
}
