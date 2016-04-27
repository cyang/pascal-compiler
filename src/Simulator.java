import sun.jvm.hotspot.debugger.cdbg.Sym;

import java.nio.ByteBuffer;
import java.util.HashMap;
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
                    break;
                case JMP:
                    getAddressValue();
                    break;
                case FMULT:
                    break;
                case PRINT_REAL:
//                    System.out.println();
                    break;
                case PRINT_INT:
                    break;
                case HALT:
                    halt();
                    break;
                case LSS:
                    break;
                case JFALSE:
                    break;
                case ADD:
                    break;
                case SUB:
                    break;
                case MULT:
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
        Integer val1 = (Integer) stack.pop();
        Integer val2 = (Integer) stack.pop();
        stack.push(val1 + val2);
    }

    public static void sub(){
        Integer val1 = (Integer) stack.pop();
        Integer val2 = (Integer) stack.pop();
        stack.push(val1 - val2);
    }

    public static void div(){
        Integer val1 = (Integer) stack.pop();
        Integer val2 = (Integer) stack.pop();
        stack.push(val1 / val2);
    }

    public static void mult(){
        Integer val1 = (Integer) stack.pop();
        Integer val2 = (Integer) stack.pop();
        stack.push(val1 * val2);
    }

    public static void cvr(){
        Integer val = (Integer) stack.pop();
        stack.push((float) val);
    }

    public static void xchg(){
        Integer val1 = (Integer) stack.pop();
        Integer val2 = (Integer) stack.pop();
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
