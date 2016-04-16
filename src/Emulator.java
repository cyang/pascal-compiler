import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public final class Emulator {

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Scanner output:");
        ArrayList<Token> tokenArrayList = TokenScanner.scan(new File(args[0]));

        System.out.println("\n Parser output:");
        Parser.setTokenArrayList(tokenArrayList);

        Parser.parse();
    }
}
