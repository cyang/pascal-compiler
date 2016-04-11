/* CFG:
E -> TE'
E' -> +TE' | -TE' | E

F -> (E) | +F | -F | not F| literal | identifier

M -> (M)M | epsilon
G -> M#     // # indicates EOF token

*/

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
        // TODO: Test
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
        while(true) {
            match("TK_VAR");

            SymbolTable.insert(currentToken);
            match("TK_IDENTIFIER");

            if ("TK_COMMA".equals(currentToken.getTokenType())) {
                match("TK_COMMA");
            } else if ("TK_IDENTIFIER".equals(currentToken.getTokenType())){
                SymbolTable.insert(currentToken);
                match("TK_IDENTIFIER");
            } else if ("TK_COLON".equals(currentToken.getTokenType())) {
                match("TK_COLON");


            }



            if ("TK_SEMI_COLON".equals(currentToken.getTokenType())) {
                match("TK_SEMI_COLON");
                break;
            }
        }

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
