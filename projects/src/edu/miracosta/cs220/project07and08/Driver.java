package edu.miracosta.cs220.project07and08;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
    Main class. 
    Usage: `java Driver path/to/vmfile.vm`
*/
public class Driver {

    public static void main(String[] args) throws Exception {
        // need a pathname
        if (args.length == 0) {
            throw new IllegalArgumentException("No Virtual Machine path to read.");
        }

        // paths 
        String pathName = args[0];
        Path readPath = Paths.get(pathName);
        if (!readPath.toString().endsWith(".vm")) {
            throw new IOException("Not an Virtual Machine file: " + "\"" + readPath + "\"");
        }

        // reading 
        ArrayList<String> readlines = new ArrayList<String>(Files.readAllLines(readPath));

        // parsing 
        Parser parser = new Parser(readlines);
        parser.parse();

        // encoding
        SymbolTable table = new SymbolTable();
        table.setFileName(readPath.getFileName().toString().split("\\.vm")[0]);
        Translator translator = new Translator(table);
        Encoder encoder = new Encoder(translator, parser.getTokenLines());
        encoder.encode();

        // writing 
        String pathNameWithoutExtention = readPath.toString().split("\\.vm")[0];
        Path writePath = Paths.get(pathNameWithoutExtention + ".asm");

        // Not using US_ASCII will break the CPUEmulator...which was a warning in the book.
        Files.write(writePath, new ArrayList<String>(){{add(encoder.getAssembly());}}, StandardCharsets.US_ASCII); 
    }
}
