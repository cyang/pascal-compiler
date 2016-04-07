

public class SymbolTable {

    static class Scope {
        Token[] symbolTable = new Token[HASH_TABLE_SIZE]; // symbol table for the current scope
        Scope next = null; // pointer to the next outer scope
    }

    private static final int HASH_TABLE_SIZE = 211;
    private static Scope headerScope = new Scope();

    public static void insert(Token token) {
        int hashValue = hash(token.getTokenValue());

        Token bucketCursor = headerScope.symbolTable[hashValue];
        if (bucketCursor == null) {
            // Empty bucket
            headerScope.symbolTable[hashValue] = token;
        } else {
            // Existing Tokens in bucket
            while (bucketCursor.next != null) {
                bucketCursor = bucketCursor.next;
            }

            // Append token at the end of the bucket
            bucketCursor.next = token;
        }
    }

    public static Token lookup(Token token) {
        int hashValue = hash(token.getTokenValue());
        Token bucketCursor = headerScope.symbolTable[hashValue];
        Scope scopeCursor = headerScope;

        while (scopeCursor != null) {
            while (bucketCursor != null) {
                if (bucketCursor.getTokenValue().equals(token.getTokenValue())) {
                    return bucketCursor;
                }
                bucketCursor = bucketCursor.next;
            }
            scopeCursor = scopeCursor.next;
        }

        // Token does not exist
        return null;
    }

    public static int hash(String tokenValue) {
        int h = 0;
        for (int i = 0; i < tokenValue.length(); i++) {
            h = h + h + tokenValue.charAt(i);
        }

        h = h % HASH_TABLE_SIZE;

        return h;
    }

    public static void openScope() {
        Scope innerScope = new Scope();

        // Add new scope to the headerScope
        innerScope.next = headerScope;

        // Move headerScope to the front of the Scope linked list
        headerScope = innerScope;
    }

    public static void closeScope() {
        headerScope = headerScope.next;
    }

    public static Scope getHeaderScope() {
        return headerScope;
    }
}
