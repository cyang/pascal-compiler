import java.util.ArrayList;

/**
 * Created by ChrisYang on 3/17/16.
 */
public class SymbolTable {
    /*
    TODO:
    hashfunction
    create pointers to next address
     */


    private static ArrayList<Token> tokensList;

    public SymbolTable(){
        tokensList = new ArrayList<>();
    }

    public void insert(Token token){
        int hashValue = hash(token.getTokenValue());
        Token element = tokensList.get(hashValue);

        if (element == null) {
            tokensList.add(hashValue, token);
        } else {
            // Existing token at the hashValue

            while(element.getNext() != null){
                // Continue through the bucket until the next element is null
                element = element.getNext();
            }

            element.setNext(token);

        }
    }

    public int lookup(Token token){
        // TODO: return index of the token
        int index = 0;

        return index;
    }

    public int hash(String tokenValue){
        // TODO: implement hash function
        int value = 0;

        return value;
    }
}
