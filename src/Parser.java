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
        currentToken = getToken();
        System.out.println(currentToken);
    }

    // TODO: Write grammar
//    public static TYPE E() {
//
//    }
//
//    public static TYPE T() {
//
//    }
//
//    public static TYPE F() {
//
//
//    }
//    public static void M() {
//
//    }

//    public void match(Token token) {
//        if (token != currentToken) {
//            throw new Error("The token does not match the current token");
//        } else {
//            currentToken = getToken();
//        }
//
//    }

    public static Token getToken() {
        if (it.hasNext()) {
            return it.next();
        }
        return null;
    }

    public static void setTokenArrayList(ArrayList<Token> tokenArrayList) {
        Parser.tokenArrayList = tokenArrayList;
        it = tokenArrayList.iterator();
    }
}
