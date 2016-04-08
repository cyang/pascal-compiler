import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by ChrisYang on 4/7/16.
 */
public final class Emulator {

    public static void main(String[] args) throws FileNotFoundException {
        ArrayList<Token> tokenArrayList = TokenScanner.scan(new File(args[0]));
        Parser.setTokenArrayList(tokenArrayList);

        Parser.parse();
    }
}
