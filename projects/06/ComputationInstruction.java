import java.util.Collections;
import java.util.Map;
// import java.util.Arrays;

public class ComputationInstruction implements Instruction {
    public static String[] allKeys = {"dest", "comp", "jump"};
    public static String[] destKeys = {allKeys[0], allKeys[1]};
    public static String[] jumpKeys = {allKeys[1], allKeys[2]};

    public static String destCodes[] = {"000", "001", "010", "011", "100", "101", "110", "111"};
    public static String compCodes[] = {"0101010", "0111111", "0111010", "0001100", "0110000", "0001101", "0110001", "0001111", "0110011", "0011111", "0110111", "0001110", "0110010", "0000010", "0000010", "0010011", "0000111", "0000000", "0000000", "0010101", "0010101", "1110000", "1110001", "1110011", "1110111", "1110010", "1000010", "1000010", "1010011", "1000111", "1000000", "1000000", "1010101", "1010101"};
    public static String jumpCodes[] = {"000", "001", "010", "011", "100", "101", "110", "111"};

    public static String[][] codeArrays = {destCodes, compCodes, jumpCodes};
    
    public static String destMnemonics[] = {"NULL", "M", "D", "MD", "A", "AM", "AD", "AMD"};
    public static String compMnemonics[] = {"0", "1", "-1", "D", "A", "!D", "!A", "-D", "-A", "D+1", "A+1", "D-1", "A-1", "D+A", "A+D", "D-A", "A-D", "D&A", "A&D", "D|A", "A|D", "M", "!M", "-M", "M+1", "M-1", "D+M", "M+D", "D-M", "M-D", "D&M", "M&D", "D|M", "M|D"};
    public static String jumpMnemonics[] = {"NULL", "JGT", "JEQ", "JGE", "JLT", "JNE", "JLE", "JMP"};
    
    public static String[][] mnemonicArrays = {destMnemonics, compMnemonics, jumpMnemonics};

    private Map<String,String> lineMnmomics;
    private String binaryString = null;

    public ComputationInstruction(Map<String,String> lineMnmomics) {
        this.lineMnmomics = lineMnmomics;
    }

    public Map<String,String> getLineMnmomics() {
        return Collections.unmodifiableMap(lineMnmomics);
    }
    // Instruction
    public String getBinaryString() {
        if (binaryString == null) {
            String comp = lineMnmomics.get("comp");
            String dest = (lineMnmomics.get("dest") == null)?"NULL":lineMnmomics.get("dest");
            String jump = (lineMnmomics.get("jump") == null)?"NULL":( "J" + lineMnmomics.get("jump"));

            binaryString = "111"; // C instructions bits 15..13 are set to 111
            binaryString += compCodes[indexOf(comp, compMnemonics)];
            binaryString += destCodes[indexOf(dest, destMnemonics)];
            binaryString += jumpCodes[indexOf(jump, jumpMnemonics)];
        } 

        return binaryString;
    }

    public String toString() {
        return getBinaryString() + " == " + lineMnmomics.toString(); 
    }

    // I don't think ArrayList is warented just for indexOf()
    private static <T> int indexOf(T object, T[] array) {
        if (object == null) {
            throw new IllegalArgumentException("No nulls");
        }

        for (int x = 0;x < array.length;x++) {
            if (object.equals(array[x])) {
                return x;
            }
        }

        return -1;
    }
}
