/**
 * Created by ChrisYang on 4/13/16.
 */
public class Symbol {
    private String name;
    private String dataType;
    private Object value;

    Symbol next; // pointer to the next entry in the symbolTable bucket list

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
