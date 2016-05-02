public class Symbol {
    private String name = "";
    private Parser.TYPE dataType = null;
    private Object value = null;
    private int address;

    Symbol next; // pointer to the next entry in the symbolTable bucket list

    public Symbol(String name, Parser.TYPE dataType, int address){
        this.name = name;
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
}
