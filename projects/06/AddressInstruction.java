import java.util.BitSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class AddressInstruction implements Instruction {

    private int address;

    public AddressInstruction(int address) {
        this.address = address;
    }
    public BitSet getBinaryRepresentation() {
        throw new RuntimeException();
    }
    public int getAddress() {
        return address;
    }
    public String toString() {
        return "" + address;
    }
}