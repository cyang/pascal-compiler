import java.nio.ByteBuffer;
import java.util.Stack;

public class Simulator {

    private static int ip = 0;
    private static Object val = 0;

    private static Stack<Object> stack = new Stack<>();

    private static Byte[] instructions;

    public static void simulate() {
        Parser.OP_CODE opCode;

        do {
            opCode = getOpCode();
            System.out.println(opCode);
            switch (opCode) {
                case PUSHI:
                    pushi();
                    break;
                case POP:
                    pop();
                    break;
                case CVR:
                    cvr();
                    break;
                case JMP:
                    getAddressValue();
                    break;
                case PRINT_REAL:
                    writeln();
                    break;
                case PRINT_INT:
                    writeln();
                    break;
                case PRINT_BOOL:
                    writeln();
                    break;
                case PRINT_CHAR:
                    writeln();
                    break;
                case HALT:
                    halt();
                    break;
                case LSS:
                    break;
                case JFALSE:
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

    public static void writeln(){
        Object val = stack.pop();
        System.out.println(val);
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
        float val1 = (float) stack.pop();
        float val2 = (float) stack.pop();
        stack.push(val1 / val2);
    }

    public static void div(){
        int val1 = (int) stack.pop();
        int val2 = (int) stack.pop();
        stack.push(val1 / val2);
    }

    public static void cvr(){
        float val = (float) stack.pop();
        stack.push(val);
    }

    public static void xchg(){
        Integer val1 = (int) stack.pop();
        Integer val2 = (int) stack.pop();
        stack.push(val1);
        stack.push(val2);
    }

    public static void pushi(){
        int val = getAddressValue();
//        System.out.println(val);
        stack.push(val);
    }

    public static void push(Object val){
        stack.push(val);
    }

    public static Object pop(){
        // TODO: Assign pop to symbolTable
        getAddressValue();
        return stack.pop();
    }

    public static void jmp(){
        ip = getAddressValue();
    }


    public static void halt() {
        System.out.println("\n Program finished running");
        System.exit(0);
    }

    public static int getAddressValue() {
        byte[] valArray = new byte[4];
        for (int i = 0; i < 4; i++) {
            valArray[i] = instructions[ip++];
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
