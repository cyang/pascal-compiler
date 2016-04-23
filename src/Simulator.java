import java.util.Stack;

public class Simulator {

    private static int ip = 0;
    private static Stack<Object> stack = new Stack<>();

    private static byte[] instructions;


    public static void simulate() {


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
        
    }

    public static void push(Object val){
        stack.push(val);
    }

    public static void pop(){
        stack.pop();
    }

    public static void jmp(){
        ip = getAddressValue();
    }


    public static void halt() {
        System.out.println("Program finished running");
        System.exit(0);
    }

    public static int getAddressValue() {
        String binaryNumber = "";
        for (int i = 0; i < 4; i++) {
            binaryNumber += instructions[ip+i];
        }

        return Integer.parseInt(binaryNumber);
    }

    public static void setInstructions(byte[] instructions) {
        Simulator.instructions = instructions;
    }
}
