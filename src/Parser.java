/* CFG:
E -> TE'
E' -> +TE' | -TE' | E

F -> (E) | +F | -F | not F| literal | identifier

M -> (M)M | epsilon
G -> M#     // # indicates EOF token

*/

import sun.jvm.hotspot.debugger.cdbg.Sym;

import java.util.ArrayList;
import java.util.Iterator;

public final class Parser {
    // TODO: Generate p_code

    enum TYPE {
        I, R, B, C
    }

    private static Token currentToken;
    private static ArrayList<Token> tokenArrayList;
    private static Iterator<Token> it;

    public static void parse() {
        currentToken = getToken(); // Get initial token

        match("TK_PROGRAM");
        match("TK_IDENTIFIER");
        match("TK_SEMI_COLON");
        System.out.println(currentToken);

        if ("TK_VAR".equals(currentToken.getTokenType())) {
            declarations();
        } else {
//            begin();
        }
    }

    public static void declarations() {
        match("TK_VAR");

        // Store identifers in a list
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
        for (Token identifier: identifierArrayList) {
            identifier.setTokenType(dataType);
            SymbolTable.insert(identifier);
        }

        match("TK_SEMI_COLON");
    }


    public static Token getToken() {
        if (it.hasNext()) {
            return it.next();
        }
        return null;
    }

    public static void match(String tokenType) {
        if (!tokenType.equals(currentToken.getTokenType())) {
            throw new Error("The token does not match the current token");
        } else {
            currentToken = getToken();
        }

    }

    public static void setTokenArrayList(ArrayList<Token> tokenArrayList) {
        Parser.tokenArrayList = tokenArrayList;
        it = tokenArrayList.iterator();
    }
}
