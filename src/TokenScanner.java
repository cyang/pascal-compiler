import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class TokenScanner {

    private static final HashMap<Character, String> charType;
    static {
        charType = new HashMap<>();

        for (int i = 65; i < 91; i++){
            // Add letters
            char currentChar = Character.toChars(i)[0];
            charType.put(currentChar, "letter");
            charType.put(Character.toLowerCase(currentChar), "letter");
        }




    }


    public static void main(String[] args) throws FileNotFoundException {
        File file = new File(args[0]);
        Scanner sc = new Scanner(file);

        while (sc.hasNext()) {
            switch (charType.get(sc.next().toCharArray()[0])){
                case "letter":

                    break;
            }
        }

        System.out.println("Hello World!");
    }
}
