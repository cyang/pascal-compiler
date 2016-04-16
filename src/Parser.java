import sun.jvm.hotspot.debugger.cdbg.Sym;

import java.nio.ByteBuffer;
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

    private static Byte p_code[] = new Byte[211];
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

                Symbol symbol = new Symbol(identifier.getTokenValue(), dataType.toLowerCase().substring(3));

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
        genOpcode("OP_JFALSE");
        int hole1 = ip;
        genAddress(0);
        statements();

        if(currentToken.getTokenType().equals("TK_ELSE")) {
            genOpcode("OP_JMP");
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

    public static void condition(){
        TYPE t= E();
        if (t != B)
            error();
    }

    public static void assignmentStat() {
        Token lhsAddress = currentToken;
        Symbol symbol = SymbolTable.lookup(currentToken.getTokenValue());

        if (symbol != null) {
            String lhsType = "";

            lhsType = symbol.getDataType();


            getToken();
            match("TK_ASSIGNMENT");

            String rhsType = E();
            if (lhsType.equals(rhsType)) {
                symbol.setValue(currentToken.getTokenValue());
            } else {
                throw new Error(String.format("LHS type (%s) is not equal to RHS type: (%s)", lhsType, rhsType));
            }

            //TODO generate pop of LHS_addr
            genOpcode("OP_POP");
            genAddress(lhsAddress);


        }


    }

    public static String E(){
        String t1 = T();
        while(currentToken.getTokenType().equals("TK_PLUS") || currentToken.getTokenType().equals("TK_MINUS")) {
            String op = currentToken.getTokenType();
            match(op);
            String t2 = T();

            t1 = emit(op, t1, t2);
        }

    }

    public static void emit(String op, String t1, String t2){

    }

    public static void genAddress(int a){
        byte[] intBytes = ByteBuffer.allocate(4).putInt(a).array();

        for (byte b: intBytes) {
            p_code[ip++] = b;
        }
    }

    public static void genOpcode(String b){
        p_code[ip++] = Byte.valueOf(b);
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

    public static void setTokenArrayList(ArrayList<Token> tokenArrayList) {
        Parser.tokenArrayList = tokenArrayList;
        it = tokenArrayList.iterator();
    }
}
