/**
 * Created by ChrisYang on 3/17/16.
 */

public class SymbolTable {
    //TODO: incorporate level into Token class
    
    static class Scope {
        Token scopeLink = null; // pointer to latest token for current scope
        Scope next = null; // pointer to the outer scope
    }

    static int level;
    private static Scope scopeList;

    private static final int HASH_TABLE_SIZE = 200;

    private static Token[] symbolTable;

    public SymbolTable(){
        symbolTable = new Token[HASH_TABLE_SIZE];
        scopeList = new Scope();
        level = 0;
    }

    public static void insert(Token token){
        int hashValue = hash(token.getTokenValue());

        // Move the current value as next
        token.next = symbolTable[hashValue];

        // Insert token value
        symbolTable[hashValue] = token;

        // Update scopeLinks
        token.scopeLink = scopeList.scopeLink;
        scopeList.scopeLink = token;
    }

    public static Token lookup(Token token){
        int hashValue = hash(token.getTokenValue());
        Token element = symbolTable[hashValue];

        while (element != null) {
            // Continue until token is found or null
            if (element.getTokenValue().equals(token.getTokenValue())) {
                return element;
            }
            element = element.next;
        }

        return null;
    }

    public static int hash(String tokenValue){
        int h = 0;
        for (int i = 0; i < tokenValue.length(); i++) {
            h = h + h + tokenValue.charAt(i);
        }

        h = h % HASH_TABLE_SIZE;

        return h;
    }

    public static void openScope() {
        // Add new scope to the scopeList
        Scope s = new Scope();
        s.next = scopeList;

        // Make head of scope to be the new scope
        scopeList = s;
        level++;
    }

    public static void deleteScope() {
        // Retrieve the list of tokens related to the current scope
        Token element = scopeList.scopeLink;
        Token temp;

        // Removes all entries in the current scope
        while (element != null) {
            symbolTable[hash(element.getTokenValue())] = element.next;

            temp = element.scopeLink;

            // Clear links
            element.next = null;
            element.scopeLink = null;

            // Move to the next token in the scope
            element = temp;
        }

        // Remove the current scope from the scopeList
        Scope s = scopeList;
        scopeList = scopeList.next;

        s.next = null;
        s.scopeLink = null;
        level--;

    }
}
