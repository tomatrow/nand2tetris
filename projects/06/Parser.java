import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.ListIterator;
import java.util.Collections;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.Arrays;

public class Parser {
    private static String commentSpaceRegex = "//.*";
    private static String whiteSpaceRegex = "\\s*";
    private static String cleanRegex = String.format("%s|%s", commentSpaceRegex, whiteSpaceRegex);

    private static String addressRegex = "@((?<symbol>[A-Za-z_.$:]{1}[A-Za-z0-9_.$:]*)|(?<number>\\d+))";
    private static Pattern addressPattern = Pattern.compile(addressRegex);
    private static String jumpComputationRegex = "(?<comp>(D[+\\-&|][AM])|([AM]-D)|([DAM][\\+-]1)|((!|-)?[DAM])|(-?)1|(0))(;J(?<jump>GT|EQ|GE|LT|NE|LE|MP))";
    private static Pattern jumpComputationPattern = Pattern.compile(jumpComputationRegex); 
    private static String destComputationRegex = "((?<dest>[DAM]{1,3})=)(?<comp>(D[+\\-&|][AM])|([AM]-D)|([DAM][\\+-]1)|((!|-)?[DAM])|(-?)1|(0))";
    private static Pattern destComputationPattern = Pattern.compile(destComputationRegex); 

    // A user-defined symbol can be any sequence of letters, digits, underscore (_), dot (.), dollar sign ($), and colon (:) that does not begin with a digit.
    // \([A-Za-z_.$:]{1}[A-Za-z0-9_.$:]*\)
    private static String labelRegex = "\\([A-Za-z_.$:]{1}[A-Za-z0-9_.$:]*\\)";    
    private static String symbolicAddressRegex = "@[A-Za-z_.$:]{1}[A-Za-z0-9_.$:]*";
    // private static Pattern symbolicAddressPattern = Pattern.compile(symbolicAddressRegex);

    private ArrayList<String> originalAssemblyLines;
    private ArrayList<String> cleanedAssemblyLines;
    private SymbolTable table;
    private ArrayList<Instruction> instructions;

    public Parser(ArrayList<String> assemblyLines) {
        this.originalAssemblyLines = assemblyLines;
        this.table = new SymbolTable();
        this.cleanedAssemblyLines = null;
        this.instructions = null;
    }

    public void parse() throws Exception {
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
            } else if (cleanedLine.startsWith("(")) { // is label 
                // Get rid of () around the symbol
                String symbol = cleanedLine.substring(1, cleanedLine.length()-1);
                int address = iterator.nextIndex() - 1; // The address is from 0..n, so we subtract one. 
                if (!table.contains(symbol)) {
                    table.addLabel(symbol, address); // works even if the next line is a comment. 
                } 
                // else { // Doesn't work, we might need seperate maps for labels and @label
                //     throw new RuntimeException("Label already defined: " + symbol);
                // }

                iterator.remove();
            } else { // A line that's not a label, comment, or whitespace. 
                if (Pattern.matches(symbolicAddressRegex, cleanedLine)) { // Like @mySymbol
                    String possibleSymbol = cleanedLine.substring(1);
                    if (!table.contains(possibleSymbol)) {
                        table.addAddressSymbol(possibleSymbol);
                    }
                }
                iterator.set(cleanedLine);
            }
        }

        return cleanedLines;
    }

    // get instructions from cleaned input 
    private ArrayList<Instruction> getInstructionsFromCleanAssemblyLines(final ArrayList<String> assemblyLines) throws Exception {
        ArrayList<Instruction> instructions = new ArrayList<Instruction>(assemblyLines.size());
        ListIterator<String> iterator = assemblyLines.listIterator();

        while (iterator.hasNext()) {  
            String assemblyLine = iterator.next();

            // try {
                if (assemblyLine.startsWith("@")) { // A instruction 
                    instructions.add(parseAddressInstruction(assemblyLine));
                } else { // C instruction 
                    instructions.add(parseComputationInstruction(assemblyLine));
                }
            // } catch(Exception e) {
            //     System.out.println(assemblyLine );
            //     throw new ParseException(e.getMessage(), iterator.nextIndex());
            // }
        }

        return instructions;
    }

    private AddressInstruction parseAddressInstruction(String assemblyLine) throws Exception, NumberFormatException {
        Matcher matcher = addressPattern.matcher(assemblyLine);
        Integer address;
        // System.out.println(assemblyLine);
        if (matcher.find()) {
            String symbol = matcher.group("symbol");
            String number = matcher.group("number");
            
            if (number != null) {
                address = new Integer(number);
            } else if (symbol != null) {
                if (table.contains(symbol)) {
                    address = table.addressForSymbol(symbol);
                } else {
                    throw new Exception("Table does not have: " + symbol);    
                }
            } else {
                throw new Exception("A:No group found " + assemblyLine);
            }
        } else {
            throw new Exception("A:No Match " + assemblyLine);
        }

        return new AddressInstruction(address);
    }

    private ComputationInstruction parseComputationInstruction(String assemblyLine) throws Exception {
        Map<String,String> map = new HashMap<String,String>();
        Matcher matcher;
        String[] keys;
        
        if (assemblyLine.contains(";")) { // jump
            matcher = jumpComputationPattern.matcher(assemblyLine);
            keys = ComputationInstruction.jumpKeys;
        } else if (assemblyLine.contains("=")) { // dest
            matcher = destComputationPattern.matcher(assemblyLine);
            keys = ComputationInstruction.destKeys;
        } else {
            throw new Exception("Does not have ;|=: \"" + assemblyLine + "\"");
        }


        if (matcher.find()) {
            for (String key : keys) {
                map.put(key, matcher.group(key));
            }
        } else {
            System.out.println(matcher);
            System.out.println(Arrays.toString(keys));
            throw new Exception("C:No match " + "\"" + assemblyLine + "\"");
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