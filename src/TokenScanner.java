import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class TokenScanner {

    private static final int LETTER = 0;
    private static final int DIGIT = 1;
    private static final int SPACE = 2;
    private static final int OPERATOR = 3;
    private static final int QUOTE = 4;

    private static final HashMap<String, String> KEYWORDS_TOKEN;
    static {
        KEYWORDS_TOKEN = new HashMap<>();
        String word;

        try {
            Scanner sc = new Scanner(new File("keywords.txt"));
            while(sc.hasNext()){
                word = sc.next();
                KEYWORDS_TOKEN.put(word, String.format("TK_%s", word.toUpperCase()));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static final HashMap<Character, String> OPERATORS_TOKEN;
    static {
        OPERATORS_TOKEN = new HashMap<>();
        OPERATORS_TOKEN.put('(', "TK_OPEN_PARENTHESIS");
        OPERATORS_TOKEN.put(')', "TK_CLOSE_PARENTHESIS");
        OPERATORS_TOKEN.put('.', "TK_DOT");
        OPERATORS_TOKEN.put(';', "TK_SEMI_COLON");
        OPERATORS_TOKEN.put('+', "TK_PLUS");
        OPERATORS_TOKEN.put('-', "TK_MINUS");
        OPERATORS_TOKEN.put('*', "TK_MULTIPLY");
        OPERATORS_TOKEN.put('/', "TK_DIVIDE");
        OPERATORS_TOKEN.put('<', "TK_LESS_THAN");
        OPERATORS_TOKEN.put('>', "TK_GREATER_THAN");
        OPERATORS_TOKEN.put('=', "TK_EQUAL");
    }

    private static final HashMap<Character, Integer> CHAR_TYPE;
    static {
        CHAR_TYPE = new HashMap<>();

        for (int i = 65; i < 91; i++){
            // Add letters
            char currentChar = Character.toChars(i)[0];
            CHAR_TYPE.put(currentChar, LETTER);
            CHAR_TYPE.put(Character.toLowerCase(currentChar), LETTER);
        }
        for (int i = 48; i < 58; i++){
            // Add digits
            char currentChar = Character.toChars(i)[0];
            CHAR_TYPE.put(currentChar, DIGIT);
        }
        for (int i = 1; i < 33; i++){
            // Add spaces
            char currentChar = Character.toChars(i)[0];
            CHAR_TYPE.put(currentChar, SPACE);
        }

        for (Character key: OPERATORS_TOKEN.keySet()) {
            CHAR_TYPE.put(key, OPERATOR);
        }

        CHAR_TYPE.put(Character.toChars(39)[0], QUOTE);
    }

    public static void main(String[] args) throws FileNotFoundException {
        String tokenName = "";
        int lineRow = 0;
        int lineCol = 0;
        boolean readString = false;

        // Delimiter to scan each char
        Scanner sc = new Scanner(new File(args[0])).useDelimiter("");

        while (sc.hasNext()) {
            char element = sc.next().charAt(0);

            switch (CHAR_TYPE.get(element)){
                case LETTER:
                    tokenName += element;

                    lineCol++;
                    break;
                case DIGIT:

                    lineCol++;
                    break;
                case SPACE:
                    if (readString){
                        tokenName += element;
                    } else {
                        tokenName = endOfWord(tokenName);

                        if (element == Character.toChars(10)[0]){
                            // Check for newline on unix
                            lineRow++;
                            lineCol = 0;
                        } else {
                            lineCol++;
                        }
                    }
                    break;
                case OPERATOR:
                    if (element == ';'){
                        tokenName = endOfWord(tokenName);
                    }

                    if(KEYWORDS_TOKEN.containsKey(tokenName)){
                        System.out.println(KEYWORDS_TOKEN.get(tokenName));
                        tokenName = "";
                    }

                    if(OPERATORS_TOKEN.containsKey(element)){
                        System.out.println(OPERATORS_TOKEN.get(element));
                        tokenName = "";
                    }

                    lineCol++;
                    break;
                case QUOTE:
                    readString = !readString;
                    tokenName += element;

                    if (!readString) {
                        System.out.println("TK_STRLIT: " + tokenName );
                        tokenName = "";
                    }

                    lineCol++;
                    break;
                default:
                    lineCol++;
                    break;

            }
        }

        System.out.println("TK_EOF");
    }

    public static String endOfWord(String tokenName){
        if(KEYWORDS_TOKEN.containsKey(tokenName)){
            System.out.println(KEYWORDS_TOKEN.get(tokenName));
            tokenName = "";
        } else {
            if (tokenName.length() > 0) {
                System.out.println("TK_IDENTIFIER: " + tokenName);
                tokenName = "";
            }
        }

        return tokenName;
    }

}
