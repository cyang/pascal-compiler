import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public final class Parser {
    enum TYPE {
        I, R, B, C, S
    }

    private static final HashMap<String, TYPE> STRING_TYPE_HASH_MAP;
    static {
        STRING_TYPE_HASH_MAP = new HashMap<>();
        STRING_TYPE_HASH_MAP.put("integer", TYPE.I);
        STRING_TYPE_HASH_MAP.put("real", TYPE.R);
        STRING_TYPE_HASH_MAP.put("boolean", TYPE.B);
        STRING_TYPE_HASH_MAP.put("character", TYPE.C);
        STRING_TYPE_HASH_MAP.put("string", TYPE.S);
    }

    enum OP_CODE {
        PUSHI, PUSH, POP,
        JMP, JFALSE, JTRUE,
        CVR, CVI,
        DUP, XCHG, REMOVE,
        ADD, SUB, MULT, DIV, NEG,
        OR, AND,
        FADD, FSUB, FMULT, FDIV, FNEG,
        EQL, GEQ, LEQ, GTR, LSS,
        FGTR, FLSS,
        HALT
    }

    private static final int ADDRESS_SIZE = 4;

    private static Token currentToken;
    private static Iterator<Token> it;

    private static final int INSTRUCTION_SIZE = 1000;

    private static Byte[] byteArray = new Byte[INSTRUCTION_SIZE];
    private static int ip = 0;

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
        varDeclarations();
        // TODO
    }


    /*
    <var decl> ->
        var[<namelist>: <type>;]^+
     */
    public static void varDeclarations() {
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

                Symbol symbol = new Symbol(identifier.getTokenValue(), STRING_TYPE_HASH_MAP.get(dataType.toLowerCase().substring(3)));

                if (SymbolTable.lookup(identifier.getTokenValue()) == null) {
                    SymbolTable.insert(symbol);
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
	    <writeStat>
     */
    public static void statements(){
        // TODO finish statements
        switch (currentToken.getTokenType()){
            case "TK_WHILE":
                whileStat();
                break;
            case "TK_REPEAT":
                repeatStat();
                break;
            case "TK_IF":
                ifStat();
                break;
            case "TK_FOR":
                forStat();
                break;
            case "TK_WRITELN":
                writeStat();
                break;
            case "TK_IDENTIFIER":
                assignmentStat();
            default:
                break;
        }


    }

    private static void forStat() {
    }

    private static void repeatStat() {
    }

    private static void whileStat() {
    }

    public static void ifStat(){
        match("TK_IF");
//        condition();
        match("TK_THEN");
        genOpcode(OP_CODE.JFALSE);
        int hole1 = ip;
        genAddress(0);
        statements();

        if(currentToken.getTokenType().equals("TK_ELSE")) {
            genOpcode(OP_CODE.JMP);
            int hole2 = ip;
            genAddress(0);
            int save = ip;
            ip = hole1;
            genAddress(save);
            ip = save;
            hole1 = hole2; statements(); match("TK_ELSE"); statements();
        }

        int save = ip;
        ip = hole1;
        genAddress(save);
        ip = save;

    }


    public static void writeStat(){
        match("TK_WRITELN");
        match("TK_OPEN_PARENTHESIS");

        while (true) {
//            TYPE t = E();
//            switch (t) {
//                case I:
//                    generate print_int;
//                    break;
//                case C:
//                    generate print_char;
//                    break;
//                case R:
//                    generate printReal;
//                    break;
//                case B
//                    generate printBool;
//                    break;
//
//            }

            switch (currentToken.getTokenType()) {
                case "TK_COMMA":
                    getToken();
                    break;
                case "TK_CLOSE_PARENTHESIS":
                    getToken();
                    return;
                default:
                    throw new Error(String.format("Current token type (%s) is neither TK_COMMA nor TK_CLOSE_PARENTHESIS", currentToken.getTokenType()));
            }
        }
    }
//
//    public static void condition(){
//        TYPE t= E();
//        if (t != B)
//            error();
//    }
//
    public static void assignmentStat() {
        int lhsAddress = ip;
        Symbol symbol = SymbolTable.lookup(currentToken.getTokenValue());

        if (symbol != null) {
            TYPE lhsType = symbol.getDataType();


            getToken();
            match("TK_ASSIGNMENT");

            TYPE rhsType = E();
            if (lhsType == rhsType) {
                symbol.setValue(currentToken.getTokenValue());
            } else {
                throw new Error(String.format("LHS type (%s) is not equal to RHS type: (%s)", lhsType, rhsType));
            }

            genOpcode(OP_CODE.POP);
            genAddress(lhsAddress);
        }


    }

    public static TYPE E(){
        TYPE t1 = T();
        while(currentToken.getTokenType().equals("TK_PLUS") || currentToken.getTokenType().equals("TK_MINUS")) {
            String op = currentToken.getTokenType();
            match(op);
            TYPE t2 = T();

            t1 = emit(op, t1, t2);
            return t1;
        }

        return null;
    }

    public static TYPE T() {

        return null;
    }

    public static TYPE E_PRIME() {
        return null;
    }

    public static TYPE F() {
        String tokenType = currentToken.getTokenType();
        if (tokenType.equals("TK_IDENTIFIER") || //TODO: more lit?
                tokenType.equals("TK_STRLIT") ||
                tokenType.equals("TK_INTLIT") ||
                tokenType.equals("TK_FLOATLIT")){

            Symbol symbol = SymbolTable.lookup(currentToken.getTokenValue());

            if (symbol != null) {
                return symbol.getDataType();
            }
        }

        return null;
    }

    public static TYPE emit(String op, TYPE t1, TYPE t2){

        return null;
    }

    public static void genAddress(int a){
        byte[] intBytes = ByteBuffer.allocate(ADDRESS_SIZE).putInt(a).array();

        for (byte b: intBytes) {
            byteArray[ip++] = b;
        }
    }

    public static void genOpcode(OP_CODE b){
        byteArray[ip++] = (byte)(b.ordinal());
    }

    public static void getToken() {
        if (it.hasNext()) {
            currentToken =  it.next();
        }
    }

    public static void match(String tokenType) {
        if (!tokenType.equals(currentToken.getTokenType())) {
            throw new Error(String.format("Token type (%s) does not match current token type (%s)", tokenType, currentToken.getTokenType()));
        } else {
            System.out.println(String.format("matched: %s", currentToken.getTokenType()));
            getToken();
        }

    }

    public static void setTokenArrayListIterator(ArrayList<Token> tokenArrayList) {
        it = tokenArrayList.iterator();
    }
}
