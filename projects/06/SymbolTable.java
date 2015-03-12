import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private HashMap<String,Integer> labels;
    public static Map<String,Integer> constantMap = new HashMap<String,Integer>() {{
        put("R0",0);
        put("R1",1);
        put("R2",2);
        put("R3",3);
        put("R4",4);
        put("R5",5);
        put("R6",6);
        put("R7",7);
        put("R8",8);
        put("R9",9);
        put("R10",10);
        put("R11",11);
        put("R12",12);
        put("R13",13);
        put("R14",14);
        put("R15",15);
        
        put("SP",0);
        put("LCL",1);
        put("ARG",2);
        put("THIS",3);
        put("THAT",4);

        put("SCREEN",16384);
        put("KBD",24576);
    }};
    public static int USER_MEMORY_START_ADDRESS = 16;

    private int nextUserSymbolAddress;

    public SymbolTable() {
        this.labels = new HashMap<String,Integer>(){{
            putAll(constantMap);
        }};
        this.nextUserSymbolAddress = USER_MEMORY_START_ADDRESS;
    }
    public void addAddressSymbol(String symbol) {
        if (contains(symbol)) {
            throw new IllegalArgumentException("Already have: " + symbol);
        }
        add(symbol, nextUserSymbolAddress);
        nextUserSymbolAddress++;
    }
    public void addLabel(String symbol, int address) {
        if (contains(symbol)) {
            throw new IllegalArgumentException("Already have: " + symbol);
        }
        add(symbol, address);
    }

    private void add(String symbol, int address) {
        labels.put(symbol, address);
    }

    public int addressForSymbol(String symbol) {
        if (contains(symbol)) {
            return labels.get(symbol);
        } else {
            throw new IllegalArgumentException("Missing Symbol: " + symbol);
        }
    }
    public boolean contains(String symbol) {
        return labels.containsKey(symbol);
    }    
    public Map<String,Integer> getLabels() {
        return Collections.unmodifiableMap(labels);
    }
}
