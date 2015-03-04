import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private HashMap<String,Integer> labels;
    private static SymbolTable sharedSymbolTable = new SymbolTable();

    private SymbolTable() {
        this.labels = new HashMap<String,Integer>();
        addConstantSymbols();
    }
    public void add(String symbol, int address) {
        labels.put(symbol, address);
    }
    public int addressForSymbol(String symbol) {
        return labels.get(symbol);
    }
    public boolean contains(String symbol) {
        return labels.containsKey(symbol);
    }    
    public Map<String,Integer> getLabels() {
        return Collections.unmodifiableMap(labels);
    }
    public static SymbolTable getSharedSymbolTable() {
        return sharedSymbolTable;
    }

    private void addConstantSymbols() {
        labels.put("R0",0);
        labels.put("R1",1);
        labels.put("R2",2);
        labels.put("R3",3);
        labels.put("R4",4);
        labels.put("R5",5);
        labels.put("R6",6);
        labels.put("R7",7);
        labels.put("R8",8);
        labels.put("R9",9);
        labels.put("R10",10);
        labels.put("R11",11);
        labels.put("R12",12);
        labels.put("R13",13);
        labels.put("R14",14);
        labels.put("R15",15);

        labels.put("SP",0);
        labels.put("LCL",1);
        labels.put("ARG",2);
        labels.put("THIS",3);
        labels.put("THAT",4);

        labels.put("SCREEN",16384);
        labels.put("KBD",24576);
    }
}




