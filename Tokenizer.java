import java.io.BufferedReader;
import java.io.IOException;

/**
 * Tokenizer/lexical analyser for the DSL language.
 *
 * @author djb
 * @version 2023.12.07
 */
public class Tokenizer {
    private final BufferedReader input;
    // The current line being processed.
    private String currentLine;
    // The current line number.
    private int lineNumber = 0;

    // Elements of the next token.
    private Token tokenType;
    private Keyword keyword;
    private String symbol;
    private String identifier;
    private int intVal;

    private final boolean debug = false;

    /**
     * Create a Tokenizer for the given input.
     * @param reader The file to be read.
     */
    public Tokenizer(BufferedReader reader)
    {
        this.input = reader;
        currentLine = "";
        moveToNextToken();
    }

    /**
     * Are there any more tokens?
     * @return true if there is at least one more token.
     */
    public boolean hasMoreTokens()
    {
        return currentLine != null;
    }

    /**
     * Advance to the next token.
     */
    public void advance()
    {
        assert hasMoreTokens();

        tokenType = null;
        keyword = null;
        symbol = null;
        identifier = null;
        intVal = Integer.MIN_VALUE;

        decodeNextToken();
        debug(getTokenDetails());
        moveToNextToken();
    }

    /**
     * A debugging method.
     * Return details of the current token.
     * @return a formatted version of the current token.
     */
    public String getTokenDetails()
    {
        StringBuilder s = new StringBuilder();
        s.append(tokenType).append(' ');
        switch(tokenType) {
            case KEYWORD:
                s.append(keyword); break;
            case IDENTIFIER:
                s.append(identifier); break;
            case SYMBOL:
                s.append(symbol); break;
            case INT_CONST:
                s.append(intVal); break;
            default:
                s.append("???"); break;

        }
        s.append(' ');
        return s.toString();
    }

    /**
     * Return the current token.
     * @return the current token.
     */
    public Token getTokenType()
    {
        return tokenType;
    }

    /**
     * Return the current keyword.
     * @return the current keyword.
     */
    public Keyword getKeyword()
    {
        assert tokenType == Token.KEYWORD;
        return keyword;
    }

    /**
     * Return the current symbol.
     * @return the current symbol.
     */
    public String getSymbol()
    {
        assert tokenType == Token.SYMBOL;
        return symbol;
    }

    /**
     * Return the text of the current identifier.
     * @return the current identifier.
     */
    public String getIdentifier()
    {
        assert tokenType == Token.IDENTIFIER;
        return identifier;
    }

    /**
     * Return the integer that is the current INT_CONST
     * @return the current integer.
     */
    public int getIntval()
    {
        assert tokenType == Token.INT_CONST;
        return intVal;
    }

    /**
     * Return the number of the current line being processed.
     * @return the current line number.
     */
    public int getLineNumber()
    {
        return lineNumber;
    }

    /**
     * Decode the next token.
     * The current line is position at its first character.
     */
    private void decodeNextToken()
    {
        char c = currentLine.charAt(0);
        if(Character.isAlphabetic(c) || c == '_') {
            int index = 1;
            int len = currentLine.length();
            while(index < len && isIdChar(currentLine.charAt(index))) {
                index++;
            }
            String word = currentLine.substring(0, index);
            currentLine = currentLine.substring(index);
            keyword = Keyword.identify(word);
            if(keyword != null) {
                tokenType = Token.KEYWORD;
            }
            else {
                tokenType = Token.IDENTIFIER;
                identifier = word;
            }
        }
        else if(Character.isDigit(c)) {
            tokenType = Token.INT_CONST;
            int index = 1;
            int len = currentLine.length();
            while(index < len && Character.isDigit(currentLine.charAt(index))) {
                index++;
            }
            String num = currentLine.substring(0, index);
            currentLine = currentLine.substring(index);
            intVal = Integer.parseInt(num);
        }
        else if(c == '<' || c == '>' || c == '!' || c == ':') {
            // Possible two-character symbols.
            tokenType = Token.SYMBOL;
            if(currentLine.length() >= 2 && currentLine.charAt(1) == '=') {
                symbol = currentLine.substring(0, 2);
                currentLine = currentLine.substring(2);
            }
            else {
                symbol = c + "";
                currentLine = currentLine.substring(1);
            }
        }
        else {
            // Single-character symbols.
            currentLine = currentLine.substring(1);
            switch(c) {
                case '(':
                case ')':
                case ',':
                case ';':
                case '=':
                case '+':
                case '-':
                    tokenType = Token.SYMBOL;
                    symbol = c + "";
                    break;
                default:
                    throw new IllegalStateException(
                            "Unrecognised character: " + c);
            }
        }
        currentLine = currentLine.trim();
    }

    /**
     * Is the given character belongs in an identifier?
     * @param c The character to test.
     * @return true if the character belongs in an identifier.
     */
    private boolean isIdChar(char c)
    {
        return Character.isAlphabetic(c) ||
                Character.isDigit(c) ||
                c == '_';
    }

    /**
     * Find the start of the next token, skipping any
     * blank lines.
     */
    private void moveToNextToken()
    {
        currentLine = currentLine.trim();
        while(currentLine != null && currentLine.isEmpty()) {
            currentLine = readNonBlankLine();
        }
        if(currentLine != null) {
            debug(currentLine);
        }
    }

    /**
     * Read and return the next non-blank line.
     * @return The next line, or null if there are none.
     * @throws UnexpectedIOException on any input error.
     */
    private String readNonBlankLine()
            throws UnexpectedIOException
    {
        try {
            String line = input.readLine();
            lineNumber++;
            debug(line);
            while(line != null && line.trim().isEmpty()) {
                line = input.readLine();
                lineNumber++;
                debug(line);
            }
            if(line != null) {
                //System.out.println(line);
                return line.trim();
            }
            else {
                return line;
            }
        } catch (IOException ex) {
            throw new UnexpectedIOException(ex.getMessage());
        }
    }

    /**
     * Print the given string.
     * @param s The string to be printed.
     */
    private void debug(String s)
    {
        if(debug && s != null) {
            System.out.println(s);
        }
    }

    /**
     * An unexpected read error occurred.
     * Handle via an unchecked exception rather than an IOException.
     */
    private static class UnexpectedIOException extends RuntimeException
    {
        public UnexpectedIOException(String message)
        {
            super(message);
        }
    }
}