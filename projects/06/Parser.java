import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.ListIterator;
import java.util.Collections;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Parser {
    private static String commentSpaceRegex = "//.*";
    private static String whiteSpaceRegex = "\\s*";
    private static String cleanRegex = String.format("%s|%s", commentSpaceRegex, whiteSpaceRegex);

    // @(([A-Za-z_.$:]{1}[A-Za-z0-9_.$:]*)|(\d+))
    private static String addressRegex = "@((?<symbol>[A-Za-z_.$:]{1}[A-Za-z0-9_.$:]*)|(?<number>\\d+))";
    private static Pattern addressPattern = Pattern.compile(addressRegex);
    // ((?<notLeft>!)?([?<left>AMD])(?<op>[\+\-\&|]))?(<notRight>!)?(?<right>[AMD01]);J(?<jump>GT|EQ|GE|LT|NE|LE|MO|MP)
    private static String jumpComputationRegex = "((?<notLeft>!)?(?<left>[AMD])(?<op>[\\+\\-\\&|]))?(?<notRight>!)?(?<right>[AMD01]);J(?<jump>GT|EQ|GE|LT|NE|LE|MO|MP)";
    private static Pattern jumpComputationPattern = Pattern.compile(jumpComputationRegex); 
    // (?<dest>[AMD]){1,3}=((?<notLeft>!)?(?<left>[AMD])(?<op>[\+\-\&|]))?(<notRight>!)?(?<right>[AMD01])
    private static String destComputationRegex = "(?<dest>[AMD]){1,3}=((?<notLeft>!)?(?<left>[AMD])(?<op>[\\+\\-\\&|]))?(?<notRight>!)?(?<right>[AMD01])";
    private static Pattern destComputationPattern = Pattern.compile(destComputationRegex); 

    // A user-defined symbol can be any sequence of letters, digits, underscore (_), dot (.), dollar sign ($), and colon (:) that does not begin with a digit.
    // \([A-Za-z_.$:]{1}[A-Za-z0-9_.$:]*\)
    private static String labelRegex = "\\([A-Za-z_.$:]{1}[A-Za-z0-9_.$:]*\\)";    

    private ArrayList<String> originalAssemblyLines;
    private ArrayList<String> cleanedAssemblyLines;
    private SymbolTable table;
    private ArrayList<Instruction> instructions;

    public Parser(ArrayList<String> assemblyLines) {
        this.originalAssemblyLines = assemblyLines;
        this.table = SymbolTable.getSharedSymbolTable();
        this.cleanedAssemblyLines = null;
        this.instructions = null;
    }

    public void parse() throws ParseException {
        // First pass
        cleanedAssemblyLines = getCleanInput(originalAssemblyLines);
        // Second pass
        instructions = getInstructionsFromCleanAssemblyLines(cleanedAssemblyLines);
    }
    /**
     * Clean and Build symbol table 
     * - deletes all whitespace and comments
     */ 
    private ArrayList<String> getCleanInput(final ArrayList<String> assemblyLines) {
        ArrayList<String> cleanedLines = new ArrayList<String>(assemblyLines);
        ListIterator<String> iterator = cleanedLines.listIterator();

        while (iterator.hasNext()) {
            String cleanedLine = iterator.next().replaceAll(cleanRegex, ""); 
            if (cleanedLine.length() == 0) { // blank line 
                iterator.remove();
            } else if (cleanedLine.matches(labelRegex)) { // is label 
                // Get rid of () around the symbol
                String symbol = cleanedLine.substring(1, cleanedLine.length()-1);
                int address = iterator.nextIndex() - 1; // The address is from 0..n, so we subtract one. 

                table.add(symbol, address); // works even if the next line is a comment. 
                iterator.remove();
            } else { // A line that's not a label, comment, or whitespace. 
                iterator.set(cleanedLine);
            }
        }

        return cleanedLines;
    }

    // get instructions from cleaned input 
    private ArrayList<Instruction> getInstructionsFromCleanAssemblyLines(final ArrayList<String> assemblyLines) throws ParseException {
        ArrayList<Instruction> instructions = new ArrayList<Instruction>(assemblyLines.size());
        ListIterator<String> iterator = assemblyLines.listIterator();

        while (iterator.hasNext()) {  
            String assemblyLine = iterator.next();

            try {
                if (assemblyLine.startsWith("@")) { // A instruction 
                    instructions.add(parseAddressInstruction(assemblyLine));
                } else { // C instruction 
                    instructions.add(parseComputationInstruction(assemblyLine));
                }
            } catch(Exception e) {
                throw new ParseException(e.getMessage(), iterator.nextIndex());
            }
        }

        return instructions;
    }

    private AddressInstruction parseAddressInstruction(String assemblyLine) throws Exception {
        Matcher matcher = addressPattern.matcher(assemblyLine);
        int address;

        if (matcher.find()) {
            String symbol = matcher.group("symbol");
            String number = matcher.group("number");

            if (number != null) {
                address = new Integer(number);
            } else if (symbol != null) {
                address = table.addressForSymbol(symbol);
            } else {
                throw new Exception(assemblyLine);
            }
        } else {
            throw new Exception(assemblyLine);
        }

        return new AddressInstruction(address);
    }
    private ComputationInstruction parseComputationInstruction(String assemblyLine) throws Exception {
        if (assemblyLine.contains(";")) { // jump
            return parseJumpComputationInstruction(assemblyLine);
        } else if (assemblyLine.contains("=")) { // dest
            return parseDestComputationInstruction(assemblyLine);
        } else {
            throw new Exception(assemblyLine);
        }
    }
    private ComputationInstruction parseJumpComputationInstruction(String assemblyLine) throws Exception {
        Matcher matcher = jumpComputationPattern.matcher(assemblyLine);
        Map<String,String> map = new HashMap<String,String>();
        if (matcher.find()) {
            for (String key : ComputationInstruction.jumpKeys) {
                map.put(key, matcher.group(key));
            }
        }
        return new ComputationInstruction(map);
    }
    private ComputationInstruction parseDestComputationInstruction(String assemblyLine) throws Exception {
        Matcher matcher = destComputationPattern.matcher(assemblyLine);
        Map<String,String> map = new HashMap<String,String>();
        if (matcher.find()) {
            for (String key : ComputationInstruction.destKeys) {
                map.put(key, matcher.group(key));
            }
        }
        return new ComputationInstruction(map);
    }

    // Accessors 
    public ArrayList<String> getOriginalAssemblyLines() {
        return new ArrayList<String>(Collections.unmodifiableList(originalAssemblyLines));
    }
    public ArrayList<String> getCleanedAssemblyLines() {
        return new ArrayList<String>(Collections.unmodifiableList(cleanedAssemblyLines));
    }
    public ArrayList<Instruction> getInstructions() {
        return new ArrayList<Instruction>(Collections.unmodifiableList(instructions));
    }
    public SymbolTable getSymbolTable() {
        return table;
    }
}