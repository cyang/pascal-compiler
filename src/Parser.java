import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

public final class Parser {
    // TODO: Generate p_code

    private static Token currentToken;
    private static ArrayList<Token> tokenArrayList;
    private static Iterator<Token> it;
    private static Stack<Token> tokenStack;

    public static void parse() {
        getToken(); // Get initial token

        match("TK_PROGRAM");
        match("TK_IDENTIFIER");
        match("TK_SEMI_COLON");

        program();
    }

    /*
    <pascal program> ->
	    [<program stat>]
	    <declarations>
	    <begin-statement>.
    <program stat> -> E
     */
    public static void program() {
        declarations();
        begin();
    }

    /*
    <declarations> ->
	    <var decl><declarations>
	    <label ______,,______>
	    <type ______,,______>
	    <const ______,,______>
	    <procedure ______,,______>
	    <function ______,,______>
	-> E
     */
    public static void declarations() {
        var_declarations();
        // TODO
    }


    /*
    <var decl> ->
        var[<namelist>: <type>;]^+
     */
    public static void var_declarations() {
        while(true) {
            if ("TK_VAR".equals(currentToken.getTokenType())) {
                match("TK_VAR");
            } else {
                // currentToken is not "TK_VAR"
                break;
            }

            // Store identifiers in a list
            ArrayList<Token> identifierArrayList = new ArrayList<>();

            while ("TK_IDENTIFIER".equals(currentToken.getTokenType())) {

                identifierArrayList.add(currentToken);

                match("TK_IDENTIFIER");

                if ("TK_COMMA".equals(currentToken.getTokenType())) {
                    match("TK_COMMA");
                }
            }

            match("TK_COLON");
            String dataType = currentToken.getTokenType();
            match(dataType);

            // Add the correct datatype for each identifier and insert into symbol table
            for (Token identifier : identifierArrayList) {
                identifier.setTokenType(dataType);
                if (!SymbolTable.lookup(identifier)) {
                    SymbolTable.insert(identifier);
                }
            }

            match("TK_SEMI_COLON");
        }
    }

    /*
    <begin_statement> ->
        begin <stats> end
     */
    public static void begin(){
        match("TK_BEGIN");
        statements();
        match("TK_END");
    }

    /*
    <stats> ->
	    <while stat>; <stats>
	    <repeat ...
	    <goto ...
	    <case ...
	    <if ...
	    <for ...
	    <assignment> TK_A_VAR
	    <labelling> TK_A_LABEL
	    <procedure call> TK_A_PROC
	    <writeln>
     */
    public static void statements(){
        // TODO
        switch (currentToken.getTokenType()){
            case "TK_WRITELN":
                writeln();
                break;
        }
    }

    public static void writeln(){
        match("TK_WRITELN");
        match("TK_OPEN_PARENTHESIS");

        tokenStack = new Stack<>();
        
        while(true) {
            tokenStack.push(currentToken);
            // TODO call built in print

            getToken();
            if ("TK_COMMA".equals(currentToken.getTokenType())) {
                match("TK_COMMA");
            } else {
                break;
            }
        }

        // TODO pop stack??


        match("TK_CLOSE_PARENTHESIS");
        match("TK_SEMI_COLON");
    }


    public static void getToken() {
        if (it.hasNext()) {
            currentToken =  it.next();
        }
    }

    public static void match(String tokenType) {
        if (!tokenType.equals(currentToken.getTokenType())) {
            throw new Error("The token does not match the current token");
        } else {
            System.out.println(String.format("matched: %s", currentToken.getTokenType()));
            getToken();
        }

    }

    public static void setTokenArrayList(ArrayList<Token> tokenArrayList) {
        Parser.tokenArrayList = tokenArrayList;
        it = tokenArrayList.iterator();
    }
}
