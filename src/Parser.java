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
        EQL, NEQL, GEQ, LEQ, GTR, LSS,
        FGTR, FLSS,
        HALT,
        PRINT_INT, PRINT_CHAR, PRINT_BOOL, PRINT_REAL
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
        match("TK_DOT");
        match("TK_EOF");
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
        while(!currentToken.getTokenType().equals("TK_END")) {
            switch (currentToken.getTokenType()) {
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
                    break;
                case "TK_SEMI_COLON":
                    match("TK_SEMI_COLON");
                    break;
                default:
                    break;
            }
        }

    }

    private static void forStat() {
    }

    // repeat <stat> until <cond>
    private static void repeatStat() {
        match("TK_REPEAT");
        int target = ip;
        statements();
        match("TK_UNTIL");
        C();
        genOpcode(OP_CODE.JFALSE);
        genAddress(target);
    }


    // while <cond> do <stat>
    private static void whileStat() {
        match("TK_WHILE");
        int target = ip;
        C();
        match("TK_DO");

        genOpcode(OP_CODE.JFALSE);
        int hole = ip;
        genAddress(0);

        statements();
        genOpcode(OP_CODE.JMP);
        genAddress(target);

        int save = ip;
        ip = hole;
        genAddress(save);
        ip = save;

    }

    // if <cond> then <stat>
    // if <cond> then <stat> else <stat>
    public static void ifStat(){
        match("TK_IF");
        C();
        match("TK_THEN");
        genOpcode(OP_CODE.JFALSE);
        int hole1 = ip;
        genAddress(0); // Holder value for the address
        statements();

        if(currentToken.getTokenType().equals("TK_ELSE")) {
            genOpcode(OP_CODE.JMP);
            int hole2 = ip;
            genAddress(0);
            int save = ip;
            ip = hole1;
            genAddress(save); // JFALSE to this else statement
            ip = save;
            hole1 = hole2;
            statements();
            match("TK_ELSE");
            statements();
        }

        int save = ip;
        ip = hole1;
        genAddress(save); // JFALSE to outside the if statement in if-then or JMP past the else statement in if-else
        ip = save;
    }

    /*
    case E of
        [<tags>: <statement>]^+
            [else <statement>]

    end

    <tags> -> <single tag> 10:
          <range> 3..9:
          <list> 3,5,7:
          <list of ranges> 1..2,30..40:
    */
    public static void caseStat() {

    }

    public static void writeStat(){
        match("TK_WRITELN");
        match("TK_OPEN_PARENTHESIS");

        while (true) {
            TYPE t = E();
            switch (t) {
                case I:
                    genOpcode(OP_CODE.PRINT_INT);
                    break;
                case C:
                    genOpcode(OP_CODE.PRINT_CHAR);
                    break;
                case R:
                    genOpcode(OP_CODE.PRINT_REAL);
                    break;
                case B:
                    genOpcode(OP_CODE.PRINT_BOOL);
                    break;

            }



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

    /*
    Condition
    C -> EC'
    C' -> < EC' | > EC' | <= EC' | >= EC' | = EC' | <> EC' | epsilon
     */
    public static TYPE C(){
        TYPE e1 = E();
        while (currentToken.getTokenType().equals("TK_LESS_THAN") ||
                currentToken.getTokenType().equals("TK_GREATER_THAN") ||
                currentToken.getTokenType().equals("TK_LESS_THAN_EQUAL") ||
                currentToken.getTokenType().equals("TK_GREATER_THAN_EQUAL") ||
                currentToken.getTokenType().equals("TK_EQUAL") ||
                currentToken.getTokenType().equals("TK_NOT_EQUAL")) {
            String pred = currentToken.getTokenType();
            match(pred);
            TYPE e2 = T();

            switch (pred) {
                case "TK_LESS_THAN":
                    genOpcode(OP_CODE.LSS);
                    break;
                case "TK_GREATER_THAN":
                    genOpcode(OP_CODE.GTR);
                    break;
                case "TK_LESS_THAN_EQUAL":
                    genOpcode(OP_CODE.LEQ);
                    break;
                case "TK_GREATER_THAN_EQUAL":
                    genOpcode(OP_CODE.GEQ);
                    break;
                case "TK_EQUAL":
                    genOpcode(OP_CODE.EQL);
                    break;
                case "TK_NOT_EQUAL":
                    genOpcode(OP_CODE.NEQL);
                    break;
            }

            e1 = emit(pred, e1, e2);
        }

        return e1;
    }


    /*
    Expression
    E -> TE'
    E' -> +TE' | -TE' | epsilon
     */
    public static TYPE E(){
        TYPE t1 = T();
        while (currentToken.getTokenType().equals("TK_PLUS") || currentToken.getTokenType().equals("TK_MINUS")) {
            String op = currentToken.getTokenType();
            match(op);
            TYPE t2 = T();

            switch (op) {
                case "TK_PLUS":
                    genOpcode(OP_CODE.ADD);
                    break;
                case "TK_MINUS":
                    genOpcode(OP_CODE.SUB);
                    break;
            }

            t1 = emit(op, t1, t2);
        }

        return t1;
    }

    /*
    Term
    T -> FT'
    T' ->  *FT' | /FT' | epsilon
     */
    public static TYPE T() {
        TYPE f1 = F();
        while (currentToken.getTokenType().equals("TK_MULTIPLY") ||
                currentToken.getTokenType().equals("TK_DIVIDE") ||
                currentToken.getTokenType().equals("TK_DIV")) {
            String op = currentToken.getTokenType();
            match(op);
            TYPE f2 = F();

            switch (op) {
                case "TK_MULTIPLY":
                    genOpcode(OP_CODE.MULT);
                    break;
                case "TK_DIVIDE":
                    genOpcode(OP_CODE.DIV);
                    break;
            }

            f1 = emit(op, f1, f2);
        }
        return f1;
    }


    /*
    Factor
    F -> id | lit | (E) | not F | +F | -F
     */
    public static TYPE F() {
        switch (currentToken.getTokenType()) {
            case "TK_IDENTIFIER":
                Symbol symbol = SymbolTable.lookup(currentToken.getTokenValue());
                if (symbol != null) {
                    match("TK_IDENTIFIER");
                    return symbol.getDataType();
                } else {
                    throw new Error(String.format("Symbol not found (%s)", currentToken.getTokenValue()));
                }
            case "TK_INTLIT":
                genOpcode(OP_CODE.PUSHI);
                genAddress(Integer.valueOf(currentToken.getTokenValue()));

                match("TK_INTLIT");
                return TYPE.I;
            case "TK_FLOATLIT":
                genOpcode(OP_CODE.PUSHI);
                genAddress(Float.valueOf(currentToken.getTokenValue()));

                match("TK_FLOATLIT");
                return TYPE.R;
            case "TK_BOOLLIT":
                genOpcode(OP_CODE.PUSHI);
                genAddress(Boolean.valueOf(currentToken.getTokenValue()) ? 1 : 0);

                match("TK_BOOLLIT");
                return TYPE.B;
            case "TK_CHARLIT":
                genOpcode(OP_CODE.PUSHI);
                genAddress(currentToken.getTokenValue().charAt(0));

                match("TK_CHARLIT");
                return TYPE.C;
            case "TK_STRLIT":
                for (char c: currentToken.getTokenType().toCharArray()) {
                    genOpcode(OP_CODE.PUSHI);
                    genAddress(c);
                }

                match("TK_STRLIT");
                return TYPE.S;
            case "TK_NOT":
                match("TK_NOT");
                return F();
            case "TK_OPEN_PARENTHESIS":
                match("TK_OPEN_PARENTHESIS");
                TYPE t = E();
                match("TK_CLOSE_PARENTHESIS");
                return t;
            default:
                throw new Error("Unknown data type");
        }

    }


    public static TYPE emit(String op, TYPE t1, TYPE t2){
        // TODO generate opcode for emit
        switch (op) {
            case "TK_PLUS":
                if (t1 == TYPE.I && t2 == TYPE.I) {
                    genOpcode(OP_CODE.ADD);
                    return TYPE.I;
                } else if (t1 == TYPE.I && t2 == TYPE.R) {
                    genOpcode(OP_CODE.XCHG);
                    genOpcode(OP_CODE.CVR);
                    genOpcode(OP_CODE.FADD);
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.I) {
                    genOpcode(OP_CODE.CVR);
                    genOpcode(OP_CODE.FADD);
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.R) {
                    genOpcode(OP_CODE.FADD);
                    return TYPE.R;
                }
            case "TK_MINUS":
                if (t1 == TYPE.I && t2 == TYPE.I) {
                    genOpcode(OP_CODE.SUB);
                    return TYPE.I;
                } else if (t1 == TYPE.I && t2 == TYPE.R) {
                    genOpcode(OP_CODE.XCHG);
                    genOpcode(OP_CODE.CVR);
                    genOpcode(OP_CODE.FSUB);
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.I) {
                    genOpcode(OP_CODE.CVR);
                    genOpcode(OP_CODE.FSUB);
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.R) {
                    genOpcode(OP_CODE.FSUB);
                    return TYPE.R;
                }
            case "TK_MULTIPLY":
                if (t1 == TYPE.I && t2 == TYPE.I) {
                    genOpcode(OP_CODE.MULT);
                    return TYPE.I;
                } else if (t1 == TYPE.I && t2 == TYPE.R) {
                    genOpcode(OP_CODE.XCHG);
                    genOpcode(OP_CODE.CVR);
                    genOpcode(OP_CODE.FMULT);
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.I) {
                    genOpcode(OP_CODE.CVR);
                    genOpcode(OP_CODE.FMULT);
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.R) {
                    genOpcode(OP_CODE.FMULT);
                    return TYPE.R;
                }
            case "TK_DIVIDE":
                if ((t1 == TYPE.R || t1 == TYPE.I) && (t2 == TYPE.R || t2 == TYPE.I)) {
                    genOpcode(OP_CODE.FDIV);
                    return TYPE.R;
                }
            case "TK_DIV":
                if (t1 == TYPE.I && t2 == TYPE.I) {
                    genOpcode(OP_CODE.DIV);
                    return TYPE.I;
                }
        }

        return null;
    }

    public static void genOpcode(OP_CODE b){
        byteArray[ip++] = (byte)(b.ordinal());
    }

    public static void genAddress(int a){
        byte[] intBytes = ByteBuffer.allocate(ADDRESS_SIZE).putInt(a).array();

        for (byte b: intBytes) {
            byteArray[ip++] = b;
        }
    }

    public static void genAddress(float a){
        byte[] intBytes = ByteBuffer.allocate(ADDRESS_SIZE).putFloat(a).array();

        for (byte b: intBytes) {
            byteArray[ip++] = b;
        }
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
