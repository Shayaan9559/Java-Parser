/**
 * Parser for DSL programs.
 * This class is complete.
 * 
 * @author SS2697
 * @version 2024.1.23
 */
public class Parser
{
    // The lexical analyser.
    private final Tokenizer lex;
    // The global symbol table.
    private final SymbolTable st;
    // The current token driving the parse.
    // This is set by a call to getNextToken();
    private Token currentToken;
    // A debugging flag.
    // Set this to false when not required.
    private boolean debug = true;
    private int countOfBrackets;
    private int countOfBrackets2;
    private int countOfIfs;
    private int countOfFis;
    private String symbol;
    private String check;
    
    /**
     * Create a parser.
     *
     * @param lex The lexical analyser.
     * @param st  Global symbol table for the program.
     */
    public Parser(Tokenizer lex, SymbolTable st)
    {
        countOfBrackets = 0;
        countOfBrackets2 = 0;
        check = "eh";
        this.lex = lex;
        this.st = st;
        currentToken = null;
        // Set currentToken to the first token of the input.
        getNextToken();
        if(debug && currentToken != null) {
            // Show details of the first token.
            //System.out.print("The first token is: ");
            //System.out.println(getTokenDetails());
        }
    }

    /**
     * Parse the full input.
     * @return true if the parse is successful, false otherwise.
     */
    public boolean parseProgram()
    {
        // The first token is already available in the currentToken variable.
        if(currentToken == null){
            return false;
        }
        else if(currentToken == Token.SYMBOL && lex.getSymbol().equals(";")){
            return false;
        }
        if(parseDeclarations() && parseStatements()){
            if (countOfIfs != countOfFis) {
                throw new SyntaxException("Mismatched IF and FI count");
            }
            return true;
        }
        else{
            return false;
        }
        
    }

    /**
     * Parse a list of declarations:
     *     declarations ::= { declaration ';' } ;
     * @return true if a list of declarations is found, false otherwise.
     */
    public boolean parseDeclarations() {
        while (currentToken != null) {
            
            if (!parseDeclaration()) {
                break;
            }
            if (currentToken == Token.SYMBOL && lex.getSymbol().equals(";")) {
                getNextToken();  
            } else if (currentToken != null) {
                throw new SyntaxException("Expected semicolon at the end of declaration");
            }
        }
        return true;
    }

    /**
     * Parse a list of statements:
     *     statements ::= { statement ';'} ;
     * @return true if a list of statements is found, false otherwise.
     */
    public boolean parseStatements(){
        while (currentToken != null) {
            boolean isWhileStatement = currentToken == Token.KEYWORD && lex.getKeyword() == Keyword.WHILE;
            boolean isIfStatement = currentToken == Token.KEYWORD && lex.getKeyword() == Keyword.IF;
            if (!parseStatement()) {
                break;
            }
            if (!isWhileStatement && !isIfStatement && currentToken == Token.SYMBOL && lex.getSymbol().equals(";")) {
                getNextToken();  
            } else if (currentToken != null && !isWhileStatement) {
                throw new SyntaxException("Expected semicolon at the end of declaration");
            }
        }
        return true;
    }

    /**
     * Parse a declaration:
     *     declaration ::= INT identifiers ';';
     * @return true if a declaration is found, false otherwise.
     */
    public boolean parseDeclaration() {
        //System.out.println(getTokenDetails());
        if(currentToken == Token.KEYWORD && lex.getKeyword() == Keyword.INT){
            getNextToken();
            parseIdentifiers();
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Parse a list of identifiers:
     *     identifiers ::= IDENTIFIER { ',' IDENTIFIER } ;
     * @return true if a list of identifiers is found, false otherwise.
     */
    public void parseIdentifiers() {
        //System.out.println(getTokenDetails());
        if(currentToken == Token.IDENTIFIER){
            //System.out.println(getTokenDetails());
            if(st.isDeclared(getTokenDetails())){
                throw new SyntaxException("Identifier already declared");
            }
            else{
                st.declare(getTokenDetails());
                getNextToken();
                String temp = getTokenDetails();
                //System.out.println(temp + "before while");
                if(currentToken == Token.SYMBOL && temp.contains(",")){
                    //System.out.println("reached while loop");
                    getNextToken();
                    parseIdentifiers();
                }
                       
                //System.out.println(getTokenDetails());
            }
        }
        String temp = getTokenDetails();
        if(currentToken == Token.SYMBOL && temp.contains(";")){
            
        }
    }

    /**
     * Parse a statement:
     *     statement ::= print | conditional | loop | assignment
     * @return true if a statement is found, false otherwise.
     */
    public boolean parseStatement(){
        String identity = getTokenDetails();
        if(currentToken == Token.KEYWORD){
        check = lex.getKeyword().toString();
        }
        //System.out.println(identity + " this is identity");
        //print
        if(identity.contains("PRINT")){
            parsePrint();
        }
        //conditional
        if(check.contains("IF") || check.contains("THEN") || check.contains("ELSE") || check.contains("FI")){
            parseConditional();
        }
        //loop
        if(identity.contains("WHILE") || identity.contains("DO") || identity.contains("OD")){
            parseLoop();
        }
        //assignment
        if(identity.contains("IDENTIFIER")){
            parseAssignment();
        }
        

        return true;
    }

    /**
     * Parse a conditional statement:
     *    conditional ::= IF expression THEN statements (ELSE statements)? FI
     * @return true if a conditional statement is found, false otherwise.
     */
    private boolean parseConditional(){
        String temp = getTokenDetails();
        //System.out.println(temp + "parseConditional Value");
        if(temp.contains("IF")){
            countOfIfs++;
            getNextToken();
            //String temp2 = getTokenDetails();
            //System.out.println(temp2 + "parseConditional Value after IF");
            parseExpression();
        }
        if(temp.contains("THEN")){
            getNextToken();
            parseStatements();
        }
        if(temp.contains("ELSE")){
            getNextToken();
            parseStatements();
        }
        if(temp.contains("FI")){
            countOfFis++;
            getNextToken();
            parseExpression();
        }
        return true;
        
    }

    /**
     * Parse a loop statement:
     *    loop ::= WHILE expression DO statements OD
     * @return true if a loop statement is found, false otherwise.
     */
    private boolean parseLoop(){
        String temp = getTokenDetails();
        //System.out.println(temp + "parseLoop");
        if(temp.contains("WHILE")){
            
            getNextToken();
            parseExpression();
        }
        if(temp.contains("DO")){
            
            //System.out.println(getTokenDetails() + "parseLoop After le DO");
            getNextToken();
            parseStatements();
        }
        if(temp.contains("OD")){
            getNextToken();
            parseExpression();
        }
        return true;
    }
    /**
     * Parse a print statement:
     *     print ::= PRINT expression ;
     * This method is complete.
     * @return true if a print statement is found, false otherwise.
     */
    private boolean parsePrint()
    {
        if (expectKeyword(Keyword.PRINT)) {
            if (parseExpression()) {
                return true;
            } else {
                throw new SyntaxException("Missing expression");
            }
        }
        else {
            // Not a print statement, but could be something else.
            // So this is not an error.
            return false;
        }
    }

    /**
     * Parse an assignment statement:
     *    assignment ::= IDENTIFIER := expression ;
     * @return true if an assignment statement is found, false otherwise.
     */
    private boolean parseAssignment(){
        if(currentToken == Token.IDENTIFIER){
            //System.out.println(getTokenDetails() + "arrived at parseAssignment");
            if(!(st.isDeclared(getTokenDetails()))){
                throw new SyntaxException("Identifier already declared");
            }
            else{
                st.declare(getTokenDetails());
                getNextToken();
                String temp = getTokenDetails();
                //System.out.println(temp + "before while");
                if(!temp.contains(":=")){
                    throw new SyntaxException("Expected assignment operator");
                }
                if(temp.contains(":=")){
                    //System.out.println(":= reached");
                    getNextToken();
                    //System.out.println(getTokenDetails());
                    parseExpression();
                }
                else{
                    throw new SyntaxException("Expected assignment operator");
                }
            }
        }
        return true;
    }

    /**
     * Parse an expression:
     *     expression ::= term ( binaryOp term ) ? ;
     * @return true if an expression is found, false otherwise.
     */
    private boolean parseExpression()
    {
        
        if (!parseTerm()) {
            return false;
        }
        
    
        return true;
    }

    /**
     * Parse a term:
     *     term ::= IDENTIFIER ( , IDENTIFIER ) ? | INT_CONST | ( expression )
     * @return true if a term is found, false otherwise.
     */
    public boolean parseTerm(){
        if (currentToken == null) {
            return false;
        }
        else{
        //System.out.println(getTokenDetails() + "the term after ");
        if(currentToken == Token.IDENTIFIER){
            if(st.isDeclared(getTokenDetails())){
                //System.out.println(getTokenDetails() + "print true statement reached");
                getNextToken();
                String temp = getTokenDetails();
                //System.out.println(temp + "before while");
                if(currentToken == Token.SYMBOL && temp.contains(",")){
                    //System.out.println("reached while loop");
                    getNextToken();
                    parseIdentifiers();
                }
                       
                //System.out.println(getTokenDetails());
            }
        }
        if(currentToken == Token.INT_CONST){
            //System.out.println("parseTerm INT IF condition reached");
            getNextToken();
            parseExpression();
        }
        if(currentToken != null){
            symbol = getTokenDetails();
        }
        if(currentToken == Token.SYMBOL && symbol.contains("(")){
            countOfBrackets++;
            //System.out.println(countOfBrackets + " count of brackets (");
            getNextToken();
            parseExpression();
        }
        if(currentToken == Token.SYMBOL && symbol.contains(")")){
            countOfBrackets2++;
            //System.out.println(countOfBrackets + " count of brackets )");
            getNextToken();
            parseExpression();
            if (countOfBrackets != countOfBrackets2) {
            throw new SyntaxException("Expected closing bracket");
            }
        }
        //System.out.println(countOfBrackets + " count of brackets (" + countOfBrackets2 + " count of brackets )");
        if(currentToken == Token.SYMBOL && symbol.contains(";")){
            //System.out.println("reached ; part");
            return true;
        }
        if(symbol.contains("+") || symbol.contains("-") ||symbol.contains("=") || symbol.contains("!=") || symbol.contains("<") || symbol.contains(">") || symbol.contains("<=") || symbol.contains(">=")){
            parseBinaryOp();
            parseTerm();
            //System.out.println(currentToken + " current token");
        }
        
        if(symbol.contains("THEN")){
            getNextToken();
            parseStatements();
        }
        else if(currentToken == Token.KEYWORD && lex.getKeyword() == Keyword.INT){
            parseDeclaration();
        }
        else if(currentToken == Token.KEYWORD && !(lex.getKeyword() == Keyword.INT)){
            parseStatements();
        }
        }

        return true;

    }

    /**
     * Parse a binary operator:
     *     binaryOp ::= + | - | = | != | < | > | <= | >=
     * @return true if a binary operator is found, false otherwise.
     */
    public boolean parseBinaryOp() {
        if (currentToken == Token.SYMBOL) {
            String symbol = getTokenDetails();
            if (symbol.contains("+") || symbol.contains("-") || symbol.contains("=") || symbol.contains("!=") || symbol.contains("<") || symbol.contains(">") || symbol.contains("<=") || symbol.contains(">=")) {
                getNextToken();
                if (currentToken == Token.SYMBOL && (getTokenDetails().contains(";") || getTokenDetails().contains(")"))) {
                    throw new SyntaxException("Unexpected symbol after binary operator");
                    //System.out.println("reached error part");
                }
                if(currentToken == Token.IDENTIFIER && !(st.isDeclared(getTokenDetails()))){
                    throw new SyntaxException("Identifier not declared");
                }
                if (currentToken != Token.INT_CONST && currentToken != Token.IDENTIFIER && currentToken != Token.SYMBOL && !(symbol.contains("-"))) {
                    throw new SyntaxException("Expected INT_CONST or IDENTIFIER after binary operator");
                }
            } else {
                throw new SyntaxException("Expected binary operator");
            }
        }
        //System.out.println(getTokenDetails() + "parseBinaryOp");
        return true;
    }


    /**
     * Check whether the given Keyword is the current token.
     * If it is then <b>get the next token</b> and return true.
     * Otherwise, return false.
     * @param aKeyword The keyword to check for.
     * @return true if the keyword is the current token, false otherwise.
     */
    private boolean expectKeyword(Keyword aKeyword)
    {
        if(currentToken == Token.KEYWORD && lex.getKeyword() == aKeyword) {
            getNextToken();
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * A debugging method.
     * Access details of the current token from the tokenizer and
     * format a String with the details.
     * @return a formatted version of the current token.
     */
    private String getTokenDetails()
    {
        StringBuilder s = new StringBuilder();
        s.append(currentToken).append(' ');
        switch(currentToken) {
            case KEYWORD:
                s.append(lex.getKeyword()); break;
            case IDENTIFIER:
                s.append(lex.getIdentifier()); break;
            case SYMBOL:
                s.append(lex.getSymbol()); break;
            case INT_CONST:
                s.append(lex.getIntval()); break;
            default:
                s.append("???"); break;

        }
        s.append(' ');
        return s.toString();
    }
    /**
     * Advance to the next token.
     * Sets currentToken.
     */
    private void getNextToken()
    {
        if (lex.hasMoreTokens()) {
            lex.advance();
            currentToken = lex.getTokenType();
        } else {
            currentToken = null;
        }
    }
}
