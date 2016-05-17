/*
Example:
TK_INTLIT: 2

    tokenType: "TK_INTLIT"
    tokenValue: "2"
 */

public final class Token {
    private String tokenType= "";
    private String tokenValue = "";

    private int lineCol = 0;
    private int lineRow = 0;

    public Token(String tokenType, String tokenValue, int lineCol, int lineRow){
        this.tokenType = tokenType;
        this.tokenValue = tokenValue;

        this.lineCol = lineCol;
        this.lineRow = lineRow;
    }

    @Override
    public String toString(){
        return tokenValue;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public int getLineCol() {
        return lineCol;
    }

    public int getLineRow() {
        return lineRow;
    }
}
