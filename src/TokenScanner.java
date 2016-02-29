import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class TokenScanner {

    private static final int LETTER = 0;
    private static final int DIGIT = 1;
    private static final int SPACE = 2;
    private int tokenType;
    private int tokenValue;
    private String tokenName = "";

    private static final HashMap<Character, Integer> charType;
    static {
        charType = new HashMap<>();

        for (int i = 65; i < 91; i++){
            // Add letters
            char currentChar = Character.toChars(i)[0];
            charType.put(currentChar, LETTER);
            charType.put(Character.toLowerCase(currentChar), LETTER);
        }
        for (int i = 48; i < 58; i++){
            // Add digits
            char currentChar = Character.toChars(i)[0];
            charType.put(currentChar, DIGIT);
        }
        for (int i = 1; i < 33; i++){
            // Add spaces
            char currentChar = Character.toChars(i)[0];
            charType.put(currentChar, SPACE);
        }
    }

    private static final HashMap<String, Integer> tokenTable;
    static {
        tokenTable = new HashMap<>();
        String word;
        int value = 0;

        try {
            Scanner sc = new Scanner(new File("keywords.txt"));
            while(sc.hasNext()){
                word = sc.next();
                tokenTable.put(word, value++);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        // Scan each char
        Scanner sc = new Scanner(new File(args[0])).useDelimiter("");

        while (sc.hasNext()) {
            switch (charType.get(sc.next().toCharArray()[0])){
                case LETTER:
                    System.out.println("Letter");
                    break;
                case DIGIT:
                    System.out.println("digit");
                    break;
                case SPACE:
                    System.out.println("space");
                    break;
                default:
                    System.out.println("idk");
                    break;
            }
        }

//        System.out.println("Hello World!");
    }
}
