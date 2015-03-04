import java.util.BitSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Collections;
import java.util.Map;
import java.util.Arrays;

public class ComputationInstruction implements Instruction {
    private BitSet type = new BitSet(3);
    private BitSet computation = new BitSet(7);
    private BitSet destination = new BitSet(3);
    private BitSet jump = new BitSet(3);
    public static String[] allKeys = {"dest", "notLeft", "left", "op", "notRight", "right", "jump"};
    public static String[] jumpKeys = Arrays.copyOfRange(allKeys, 1, allKeys.length);
    public static String[] destKeys = Arrays.copyOfRange(allKeys, 0, allKeys.length-1);

    private Map<String,String> lineMnmomics;

    public ComputationInstruction(Map<String,String> lineMnmomics) {
        this.lineMnmomics = lineMnmomics;
    }
    public BitSet getBinaryRepresentation() {
        throw new RuntimeException();
    }
    public Map<String,String> getLineMnmomics() {
        return Collections.unmodifiableMap(lineMnmomics);
    }
    public String toString() {
        String toString = "";
        for (String key : lineMnmomics.keySet()) {
            String value = lineMnmomics.get(key);
            if (value != null) {
                toString += "[" + key + ":" + value + "],";
            }
        }
        if (toString.length() > 0){
            toString = toString.substring(0, toString.length()-1);
        }

        return toString;
    }
}