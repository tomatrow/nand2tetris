import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.ArrayList;
import java.text.ParseException;

public class Driver {
    // Don't forget to remove the exception throwing. 
    public static void main(String[] args) throws Exception { // Seriously 
        ArrayList<String> list = new ArrayList<String>(Files.readAllLines(Paths.get(args[0]), Charset.forName("US-ASCII")));
        Parser parser = new Parser(list);

        parser.parse();

        for (Instruction instruction : parser.getInstructions()) {
            System.out.println(instruction);
        }

        // System.out.println(parser.getSymbolTable().getLabels());
    }
}