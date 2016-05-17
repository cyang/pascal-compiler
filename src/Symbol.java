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
    private int address;
    private int returnAddress; // return address for procedure

    private Object low; // low value range for array
    private Object high; // high value range for array

    private Parser.TYPE indexType; // index type for array
    private Parser.TYPE valueType; // value type for array

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

    public Object getLow() {
        return low;
    }

    public void setLow(Object low) {
        this.low = low;
    }

    public Object getHigh() {
        return high;
    }

    public void setHigh(Object high) {
        this.high = high;
    }

    public Parser.TYPE getIndexType() {
        return indexType;
    }

    public void setIndexType(Parser.TYPE indexType) {
        this.indexType = indexType;
    }

    public Parser.TYPE getValueType() {
        return valueType;
    }

    public void setValueType(Parser.TYPE valueType) {
        this.valueType = valueType;
    }
}
