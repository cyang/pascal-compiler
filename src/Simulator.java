import java.nio.ByteBuffer;
import java.util.Stack;

public class Simulator {

    private static int ip = 0;
    private static int dp = 0;

    private static Stack<Object> stack = new Stack<>();

    private static Byte[] dataArray = new Byte[1000];

    private static Byte[] instructions;

    public static void simulate() {
        Parser.OP_CODE opCode;

        do {
            opCode = getOpCode();
//            System.out.println(opCode);
            switch (opCode) {
                case PUSH:
                    push();
                    break;
                case PUSHI:
                    pushi();
                    break;
                case PUSHF:
                    pushf();
                    break;
                case POP:
                    pop();
                    break;
                case GET:
                    get();
                    break;
                case PUT:
                    put();
                    break;
                case CVR:
                    cvr();
                    break;
                case XCHG:
                    xchg();
                    break;
                case JMP:
                    jmp();
                    break;
                case PRINT_REAL:
                    printReal();
                    break;
                case PRINT_INT:
                    printInt();
                    break;
                case PRINT_BOOL:
                    printBool();
                    break;
                case PRINT_CHAR:
                    printChar();
                    break;
                case PRINT_NEWLINE:
                    System.out.println();
                    break;
                case HALT:
                    halt();
                    break;
                case EQL:
                    eql();
                    break;
                case NEQL:
                    neql();
                    break;
                case LSS:
                    less();
                    break;
                case LEQ:
                    lessEql();
                    break;
                case GTR:
                    greater();
                    break;
                case GEQ:
                    greaterEql();
                    break;
                case JFALSE:
                    jfalse();
                    break;
                case JTRUE:
                    jtrue();
                    break;
                case ADD:
                    add();
                    break;
                case FADD:
                    fadd();
                    break;
                case SUB:
                    sub();
                    break;
                case FSUB:
                    fsub();
                    break;
                case MULT:
                    mult();
                    break;
                case FMULT:
                    fmult();
                    break;
                case DIV:
                    div();
                    break;
                case FDIV:
                    fdiv();
                    break;
                default:
                    throw new Error(String.format("Unhandled case: %s", opCode));
            }

        }
        while (opCode != Parser.OP_CODE.HALT);
    }

    private static void pushf() {
        float val = getFloatValue();
        stack.push(val);
    }

    private static void get() {
        dp = (int)stack.pop();
        stack.push(getData(dp));
    }

    private static Object put() {
        Object val = stack.pop();
        dp = (int)stack.pop();


        byte[] valBytes;
        if (val instanceof Integer) {
            valBytes = ByteBuffer.allocate(4).putInt((int) val).array();
        } else {
            valBytes = ByteBuffer.allocate(4).putFloat((float) val).array();
        }


        for (byte b: valBytes) {
            dataArray[dp++] = b;
        }

        return val;
    }

    private static void jtrue() {
        if (stack.pop().toString().equals("true")){
            ip = getAddressValue();
        } else {
            getAddressValue();
        }
    }

    private static void jfalse() {
        if (stack.pop().toString().equals("false")){
            ip = getAddressValue();
        } else {
            getAddressValue();
        }
    }

    private static void eql() {
        Integer intVal2 = (Integer) stack.pop();
        Float val2 = (float) intVal2;

        Integer intVal1 = (Integer) stack.pop();
        Float val1 = (float) intVal1;

        stack.push(val1.equals(val2));
    }

    private static void neql() {
        Integer intVal2 = (Integer) stack.pop();
        Float val2 = (float) intVal2;

        Integer intVal1 = (Integer) stack.pop();
        Float val1 = (float) intVal1;

        stack.push(!val1.equals(val2));
    }

    private static void less() {
        Integer intVal2 = (Integer) stack.pop();
        Float val2 = (float) intVal2;

        Integer intVal1 = (Integer) stack.pop();
        Float val1 = (float) intVal1;

        stack.push(val1 < val2);
    }

    private static void greater() {
        Integer intVal2 = (Integer) stack.pop();
        Float val2 = (float) intVal2;

        Integer intVal1 = (Integer) stack.pop();
        Float val1 = (float) intVal1;

        stack.push(val1 > val2);
    }

    private static void lessEql() {
        Integer intVal2 = (Integer) stack.pop();
        Float val2 = (float) intVal2;

        Integer intVal1 = (Integer) stack.pop();
        Float val1 = (float) intVal1;

        stack.push(val1 <= val2);
    }

    private static void greaterEql() {
        Integer intVal2 = (Integer) stack.pop();
        Float val2 = (float) intVal2;

        Integer intVal1 = (Integer) stack.pop();
        Float val1 = (float) intVal1;

        stack.push(val1 >= val2);
    }

    private static void printReal() {

        Object val = stack.pop();

        if (val instanceof Integer) {
            byte[] valArray = ByteBuffer.allocate(4).putInt((int) val).array();

            System.out.print(ByteBuffer.wrap(valArray).getFloat());
        } else {
            System.out.print(val);
        }

    }

    private static void printBool() {
        int val = (int) stack.pop();
        if (val == 1) {
            System.out.print("True");
        } else {
            System.out.print("False");
        }
    }

    public static void printInt(){
        System.out.print(stack.pop());

    }

    public static void printChar(){
        System.out.print(Character.toChars((Integer) stack.pop())[0]);
    }

    public static void add(){
        int val1 = (int) stack.pop();
        int val2 = (int) stack.pop();
        stack.push(val1 + val2);
    }

    private static void fadd() {
        float val1 = (float) stack.pop();
        float val2 = (float) stack.pop();
        stack.push(val1 + val2);
    }


    public static void sub(){
        int val1 = (int) stack.pop();
        int val2 = (int) stack.pop();
        stack.push(val1 - val2);
    }

    public static void fsub(){
        float val1 = (float) stack.pop();
        float val2 = (float) stack.pop();
        stack.push(val1 - val2);
    }

    public static void mult(){
        int val1 = (int) stack.pop();
        int val2 = (int) stack.pop();
        stack.push(val1 * val2);
    }

    public static void fmult(){
        float val1 = (float) stack.pop();
        float val2 = (float) stack.pop();
        stack.push(val1 * val2);
    }

    public static void fdiv(){
        float val2 = (float) stack.pop();
        float val1 = (float) stack.pop();

        stack.push(val1 / val2);
    }

    public static void div(){
        int val2 = (int) stack.pop();
        int val1 = (int) stack.pop();
        stack.push(val1 / val2);
    }

    public static void cvr(){
        float val = Float.valueOf(String.valueOf(stack.pop()));
        stack.push(val);
    }

    public static void xchg(){
        Object val1 = stack.pop();
        Object val2 = stack.pop();
        stack.push(val1);
        stack.push(val2);
    }

    public static void pushi(){
        int val = getAddressValue();
        stack.push(val);
    }

    public static void push(){
        dp = getAddressValue();
        stack.push(getData(dp));
    }

    public static Object pop(){
        Object val = stack.pop();
        dp = getAddressValue();


        byte[] valBytes;
        if (val instanceof Integer) {
            valBytes = ByteBuffer.allocate(4).putInt((int) val).array();
        } else {
            valBytes = ByteBuffer.allocate(4).putFloat((float) val).array();
        }


        for (byte b: valBytes) {
            dataArray[dp++] = b;
        }


        return val;
    }

    public static void jmp(){
        ip = getAddressValue();
    }


    public static void halt() {
        System.out.print("\nProgram finished with exit code 0\n");
        System.exit(0);
    }

    public static int getAddressValue() {
        byte[] valArray = new byte[4];
        for (int i = 0; i < 4; i++) {
            valArray[i] = instructions[ip++];
        }

        return ByteBuffer.wrap(valArray).getInt();
    }

    public static float getFloatValue() {
        byte[] valArray = new byte[4];
        for (int i = 0; i < 4; i++) {
            valArray[i] = instructions[ip++];
        }

        return ByteBuffer.wrap(valArray).getFloat();
    }

    public static int getData(int dp) {
        byte[] valArray = new byte[4];
        for (int i = 0; i < 4; i++) {
            valArray[i] = dataArray[dp++];
        }

        return ByteBuffer.wrap(valArray).getInt();
    }


    public static Parser.OP_CODE getOpCode(){
        return Parser.OP_CODE.values()[instructions[ip++]];
    }

    public static void setInstructions(Byte[] instructions) {
        Simulator.instructions = instructions;
    }

}
