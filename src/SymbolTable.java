/**
 * Created by ChrisYang on 3/17/16.
 */

public class SymbolTable {

    static class Scope {
        Token scopeLink = null;
        Scope next = null;
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
        token.next = symbolTable[hashValue];

        symbolTable[hashValue] = token;

        token.scopeLink = scopeList.scopeLink;
        scopeList.scopeLink = token;

    }

    public static Token lookup(Token token){
        int hashValue = hash(token.getTokenValue());
        Token element = symbolTable[hashValue];

        while (element != null) {
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
        Scope s = new Scope();
        s.next = scopeList;
        scopeList = s;
        level++;
    }

    public static void deleteScope() {
        Token element = scopeList.scopeLink;
        Token temp;

        // Removes all entries in the current scope
        while (element != null) {
            symbolTable[hash(element.getTokenValue())] = element.next;

            temp = element.scopeLink;

            element.next = null;
            element.scopeLink = null;

            element = temp;
        }

        Scope s = scopeList;
        scopeList = scopeList.next;

        s.next = null;
        s.scopeLink = null;
        level--;

    }

    public static void main(String[] args) {
        System.out.println(SymbolTable.hash("hello world"));
    }
}
