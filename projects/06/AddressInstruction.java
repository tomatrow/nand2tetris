public class AddressInstruction implements Instruction {
    private int address;
    private String binaryString = null;

    public AddressInstruction(int address) {
        this.address = address;
        if (address > 32767 || address < -16383) {
            throw new IllegalArgumentException("Value of " + address + " not supported");
        }
    }

    public int getAddress() {
        return address;
    }

    // Instruction
    public String getBinaryString() {
        if (binaryString == null) {
            String negativeBit = null;
            
            if (address > 16383) { // unsigned 
                negativeBit = "";
            } else if (address < 0) { // +signed 
                negativeBit = "1";
            } else { // -signed 
                negativeBit = "0";
            }

            int positiveInteger = Math.abs(address);

            String positiveIntegerBinaryString = Integer.toBinaryString(positiveInteger);
            // Integer.toBinaryString() returns in byte increments. 
            // We need 14 bits. 
            String fill = repeatedString("0", 14 - positiveIntegerBinaryString.length());
            binaryString = "0" + negativeBit + fill + positiveIntegerBinaryString;
        }

        return binaryString;
    }

    // Object
    public String toString() {
        return  getBinaryString() + " == " + getAddress();
    }

    // Convenience 
    private String repeatedString(String string, int times) {
        if (times == 0) {
            return "";
        }
        String out = "";
        for (int x = 0;x < times;x++) {
            out += string;
        }
        return out;
    }
}

