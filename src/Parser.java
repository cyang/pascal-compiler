/* CFG:
E -> TE'
E' -> +TE' | -TE' | E

F -> (E) | +F | -F | not F| literal | identifier

M -> (M)M | epsilon
G -> M#     // # indicates EOF token

*/

public class Parser {
    // TODO: Generate p_code

    enum TYPE {
        I, R, B, C
    }

    private static Token currentToken;

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

    public void match(Token token) {
        if (token != currentToken) {
            throw new Error("The token does not match the current token");
        } else {
            currentToken = getToken();
        }

    }

    // TODO: Get next token
    public Token getToken() {
        //
        return SymbolTable.getScopeList().scopeLink;
    }
}
