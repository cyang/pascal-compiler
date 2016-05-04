import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public final class Parser {
    enum TYPE {
        I, R, B, C, S
    }

    private static int dp = 0;

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
        PRINT_INT, PRINT_CHAR, PRINT_BOOL, PRINT_REAL, PRINT_NEWLINE
    }

    private static final int ADDRESS_SIZE = 4;

    private static Token currentToken;
    private static Iterator<Token> it;

    private static final int INSTRUCTION_SIZE = 1000;

    private static Byte[] byteArray = new Byte[INSTRUCTION_SIZE];
    private static int ip = 0;

    public static Byte[] parse() {
        getToken(); // Get initial token

        match("TK_PROGRAM");
        match("TK_IDENTIFIER");
        match("TK_SEMI_COLON");

        program();

        return byteArray;
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

                Symbol symbol = new Symbol(identifier.getTokenValue(),
                        STRING_TYPE_HASH_MAP.get(dataType.toLowerCase().substring(3)),
                        dp);

                dp += 4;


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
        genOpCode(OP_CODE.HALT);
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
                    return;
            }
        }

    }


    // for <variable name> := <initial value> to <final value> do <stat>
    private static void forStat() {
        match("TK_FOR");

        String varName = currentToken.getTokenValue();
        assignmentStat();

        int target = ip;


        Symbol symbol = SymbolTable.lookup(varName);
        if (symbol != null) {
            int address = symbol.getAddress();
            match("TK_TO");

            // Generate op code for x <= <upper bound>
            genOpCode(OP_CODE.PUSH);
            genAddress(address);
            genOpCode(OP_CODE.PUSHI);
            genAddress(Integer.valueOf(currentToken.getTokenValue()));

            genOpCode(OP_CODE.LEQ);
            match("TK_INTLIT");

            match("TK_DO");

            genOpCode(OP_CODE.JFALSE);
            int hole = ip;
            genAddress(0);

            match("TK_BEGIN");
            statements();
            match("TK_END");
            match("TK_SEMI_COLON");

            // Generate op code for x := x + 1;
            genOpCode(OP_CODE.PUSH);
            genAddress(address);
            genOpCode(OP_CODE.PUSHI);
            genAddress(1);
            genOpCode(OP_CODE.ADD);

            genOpCode(OP_CODE.POP);
            genAddress(address);


            genOpCode(OP_CODE.JMP);
            genAddress(target);

            int save = ip;
            ip = hole;
            genAddress(save);
            ip = save;
        }
    }

    // repeat <stat> until <cond>
    private static void repeatStat() {
        match("TK_REPEAT");
        int target = ip;
        statements();
        match("TK_UNTIL");
        C();
        genOpCode(OP_CODE.JFALSE);
        genAddress(target);
    }


    // while <cond> do <stat>
    private static void whileStat() {
        match("TK_WHILE");
        int target = ip;
        C();
        match("TK_DO");

        genOpCode(OP_CODE.JFALSE);
        int hole = ip;
        genAddress(0);

        match("TK_BEGIN");
        statements();
        match("TK_END");
        match("TK_SEMI_COLON");


        genOpCode(OP_CODE.JMP);
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
        genOpCode(OP_CODE.JFALSE);
        int hole1 = ip;
        genAddress(0); // Holder value for the address
        statements();

        if(currentToken.getTokenType().equals("TK_ELSE")) {
            genOpCode(OP_CODE.JMP);
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
            // TODO works only for identifiers

            Symbol symbol =  SymbolTable.lookup(currentToken.getTokenValue());
            if (symbol != null) {

                int address = symbol.getAddress();

                TYPE t = E();
                switch (t) {
                    case I:
                        genOpCode(OP_CODE.PRINT_INT);
                        genAddress(address);
                        break;
                    case C:
                        genOpCode(OP_CODE.PRINT_CHAR);
                        genAddress(address);
                        break;
                    case R:
                        genOpCode(OP_CODE.PRINT_REAL);
                        genAddress(address);
                        break;
                    case B:
                        genOpCode(OP_CODE.PRINT_BOOL);
                        genAddress(address);
                        break;

                }


                switch (currentToken.getTokenType()) {
                    case "TK_COMMA":
                        match("TK_COMMA");
                        break;
                    case "TK_CLOSE_PARENTHESIS":
                        match("TK_CLOSE_PARENTHESIS");
                        genOpCode(OP_CODE.PRINT_NEWLINE);
                        return;
                    default:
                        throw new Error(String.format("Current token type (%s) is neither TK_COMMA nor TK_CLOSE_PARENTHESIS", currentToken.getTokenType()));
                }
            }
        }
    }

    public static void assignmentStat() {
        Symbol symbol = SymbolTable.lookup(currentToken.getTokenValue());

        if (symbol != null) {
            TYPE lhsType = symbol.getDataType();
            int lhsAddress = symbol.getAddress();


            match("TK_IDENTIFIER");
            match("TK_ASSIGNMENT");

            TYPE rhsType = E();
            if (lhsType == rhsType) {
                genOpCode(OP_CODE.POP);
                genAddress(lhsAddress);
            } else {
                throw new Error(String.format("LHS type (%s) is not equal to RHS type: (%s)", lhsType, rhsType));
            }
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
                    genOpCode(OP_CODE.PUSH);
                    genAddress(symbol.getAddress());

                    match("TK_IDENTIFIER");
                    return symbol.getDataType();
                } else {
                    throw new Error(String.format("Symbol not found (%s)", currentToken.getTokenValue()));
                }
            case "TK_INTLIT":
                genOpCode(OP_CODE.PUSHI);
                genAddress(Integer.valueOf(currentToken.getTokenValue()));

                match("TK_INTLIT");
                return TYPE.I;
            case "TK_FLOATLIT":
                genOpCode(OP_CODE.PUSHI);
                genAddress(Float.valueOf(currentToken.getTokenValue()));

                match("TK_FLOATLIT");
                return TYPE.R;
            case "TK_BOOLLIT":
                genOpCode(OP_CODE.PUSHI);
                genAddress(Boolean.valueOf(currentToken.getTokenValue()) ? 1 : 0);

                match("TK_BOOLLIT");
                return TYPE.B;
            case "TK_CHARLIT":
                genOpCode(OP_CODE.PUSHI);
                genAddress(currentToken.getTokenValue().charAt(0));

                match("TK_CHARLIT");
                return TYPE.C;
            case "TK_STRLIT":
                for (char c: currentToken.getTokenType().toCharArray()) {
                    genOpCode(OP_CODE.PUSHI);
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
        switch (op) {
            case "TK_PLUS":
                if (t1 == TYPE.I && t2 == TYPE.I) {
                    genOpCode(OP_CODE.ADD);
                    return TYPE.I;
                } else if (t1 == TYPE.I && t2 == TYPE.R) {
                    genOpCode(OP_CODE.XCHG);
                    genOpCode(OP_CODE.CVR);
                    genOpCode(OP_CODE.FADD);
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.I) {
                    genOpCode(OP_CODE.CVR);
                    genOpCode(OP_CODE.FADD);
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.R) {
                    genOpCode(OP_CODE.FADD);
                    return TYPE.R;
                }
            case "TK_MINUS":
                if (t1 == TYPE.I && t2 == TYPE.I) {
                    genOpCode(OP_CODE.SUB);
                    return TYPE.I;
                } else if (t1 == TYPE.I && t2 == TYPE.R) {
                    genOpCode(OP_CODE.XCHG);
                    genOpCode(OP_CODE.CVR);
                    genOpCode(OP_CODE.FSUB);
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.I) {
                    genOpCode(OP_CODE.CVR);
                    genOpCode(OP_CODE.FSUB);
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.R) {
                    genOpCode(OP_CODE.FSUB);
                    return TYPE.R;
                }
            case "TK_MULTIPLY":
                if (t1 == TYPE.I && t2 == TYPE.I) {
                    genOpCode(OP_CODE.MULT);
                    return TYPE.I;
                } else if (t1 == TYPE.I && t2 == TYPE.R) {
                    genOpCode(OP_CODE.XCHG);
                    genOpCode(OP_CODE.CVR);
                    genOpCode(OP_CODE.FMULT);
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.I) {
                    genOpCode(OP_CODE.CVR);
                    genOpCode(OP_CODE.FMULT);
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.R) {
                    genOpCode(OP_CODE.FMULT);
                    return TYPE.R;
                }
            case "TK_DIVIDE":
                if ((t1 == TYPE.R || t1 == TYPE.I) && (t2 == TYPE.R || t2 == TYPE.I)) {
                    genOpCode(OP_CODE.FDIV);
                    return TYPE.R;
                }
            case "TK_DIV":
                if (t1 == TYPE.I && t2 == TYPE.I) {
                    genOpCode(OP_CODE.DIV);
                    return TYPE.I;
                }
            case "TK_LESS_THAN":
                return emitBool(OP_CODE.LSS, t1, t2);
            case "TK_GREATER_THAN":
                return emitBool(OP_CODE.GTR, t1, t2);
            case "TK_LESS_THAN_EQUAL":
                return emitBool(OP_CODE.LEQ, t1, t2);
            case "TK_GREATER_THAN_EQUAL":
                return emitBool(OP_CODE.GEQ, t1, t2);
            case "TK_EQUAL":
                return emitBool(OP_CODE.EQL, t1, t2);
            case "TK_NOT_EQUAL":
                return emitBool(OP_CODE.NEQL, t1, t2);
        }

        return null;
    }

    public static TYPE emitBool(OP_CODE pred, TYPE t1, TYPE t2) {
        if (t1 == t2) {
            genOpCode(pred);
            return TYPE.B;
        } else if (t1 == TYPE.I && t2 == TYPE.R) {
            genOpCode(OP_CODE.XCHG);
            genOpCode(OP_CODE.CVR);
            genOpCode(pred);
            return TYPE.B;
        } else if (t1 == TYPE.R && t2 == TYPE.I) {
            genOpCode(OP_CODE.CVR);
            genOpCode(pred);
            return TYPE.B;
        }

        return null;
    }

    public static void genOpCode(OP_CODE b){
        System.out.println(String.format("OP_CODE: %s", b));
        byteArray[ip++] = (byte)(b.ordinal());
    }

    public static void genAddress(int a){
        System.out.println(String.format("ADDRESS_VALUE: %s", a));
        byte[] intBytes = ByteBuffer.allocate(ADDRESS_SIZE).putInt(a).array();

        for (byte b: intBytes) {
            byteArray[ip++] = b;
        }
    }

    public static void genAddress(float a){
        System.out.println(String.format("ADDRESS_VALUE: %s", a));
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
//            System.out.println(String.format("matched: %s", currentToken.getTokenType()));
            getToken();
        }
    }

    public static void setTokenArrayListIterator(ArrayList<Token> tokenArrayList) {
        it = tokenArrayList.iterator();
    }
}
