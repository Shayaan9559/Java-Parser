/**
 * Keywords of the DSL language.
 *
 * @author djb
 * @version 2023.12.07
 */
public enum Keyword {
    DO, ELSE, FI, IF, INT, OD, PRINT, THEN, WHILE;

    /**
     * Identify the given keyword.
     * @param s Text of the keyword.
     * @return The corresponding Keyword object, or null.
     */
    public static Keyword identify(String s)
    {
        switch(s) {
            case "do": return DO;
            case "else": return ELSE;
            case "fi": return FI;
            case "if": return IF;
            case "int": return INT;
            case "od": return OD;
            case "print": return PRINT;
            case "then": return THEN;
            case "while": return WHILE;
            default:
                return null;
        }
    }
}
