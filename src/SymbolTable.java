public final class SymbolTable {

    static class Scope {
        Symbol[] symbolTable = new Symbol[HASH_TABLE_SIZE]; // symbol table for the current scope
        Scope next = null; // pointer to the next outer scope
    }

    private static final int HASH_TABLE_SIZE = 211;
    private static Scope headerScope = new Scope();

    public static void insert(Symbol symbol) {
        int hashValue = hash(symbol.getName());

        Symbol bucketCursor = headerScope.symbolTable[hashValue];
        if (bucketCursor == null) {
            // Empty bucket
            headerScope.symbolTable[hashValue] = symbol;
        } else {
            // Existing Symbols in bucket
            while (bucketCursor.next != null) {
                bucketCursor = bucketCursor.next;
            }

            // Append symbol at the end of the bucket
            bucketCursor.next = symbol;
        }
    }

    public static Symbol lookup(Symbol symbol) {
        int hashValue = hash(symbol.getName());
        Symbol bucketCursor = headerScope.symbolTable[hashValue];
        Scope scopeCursor = headerScope;

        while (scopeCursor != null) {
            while (bucketCursor != null) {
                if (bucketCursor.getName().equals(symbol.getName())) {
                    return bucketCursor;
                }
                bucketCursor = bucketCursor.next;
            }
            scopeCursor = scopeCursor.next;
        }

        // Symbol does not exist
        return null;
    }

    public static int hash(String symbolName) {
        int h = 0;
        for (int i = 0; i < symbolName.length(); i++) {
            h = h + h + symbolName.charAt(i);
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
