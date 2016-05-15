/*
Example:
    var x,y,z : integer;
    x := 2

    For variable x:
        name = "x"
        tokenType = "TK_A_VAR"
        dataType = TYPE.I
        value = 2
        address = 0
 */

public class Symbol {
    private String name = "";
    private String tokenType = "";
    private Parser.TYPE dataType = null;
    private Object value = null;
    private int address;
    private int returnAddress;

    Symbol next; // pointer to the next entry in the symbolTable bucket list

    public Symbol(String name, String tokenType, Parser.TYPE dataType, int address){
        this.name = name;
        this.tokenType = tokenType;
        this.dataType = dataType;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Parser.TYPE getDataType() {
        return dataType;
    }

    public void setDataType(Parser.TYPE dataType) {
        this.dataType = dataType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public int getReturnAddress() {
        return returnAddress;
    }

    public void setReturnAddress(int returnAddress) {
        this.returnAddress = returnAddress;
    }
}
