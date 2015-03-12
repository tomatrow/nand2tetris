import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.ArrayList;
import java.text.ParseException;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.ListIterator;
import java.io.IOException;
import java.nio.file.Path;

public class Driver {
    public static Charset characterSet = Charset.forName("US-ASCII");

    public static void main(String[] args) { 
        try {
            // need a pathname
            if (args.length == 0) {
                throw new IllegalArgumentException("No assembly path to read.");
            }

            // paths 
            String pathName = args[0];
            Path readPath = Paths.get(pathName);
            if (!readPath.toString().endsWith(".asm")) {
                throw new IOException("Not an assembly file: " + "\"" + readPath + "\"");
            }
            // replace asm with hack 
            Path writePath = Paths.get(readPath.toString().substring(0,readPath.toString().length()-3) + "hack");

            // reading 
            ArrayList<String> readlines = new ArrayList<String>(Files.readAllLines(readPath, characterSet));

            // parsing 
            Parser parser = new Parser(readlines);
            parser.parse();

            ArrayList<Instruction> instructions = parser.getInstructions();

            // writing 
            ArrayList<String> writeLines = new ArrayList<String>(instructions.size());
            for (Instruction instruction : instructions) {
                writeLines.add(instruction.getBinaryString());
            }

            Files.write(writePath, writeLines, characterSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}