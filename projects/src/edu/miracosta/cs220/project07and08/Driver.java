package edu.miracosta.cs220.project07and08;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.StandardOpenOption;

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

        // System.out.println(writePath);
        Files.write(writePath, new ArrayList<String>(){{
            add(encoder.getAssemblyProgram());
        }}, StandardOpenOption.WRITE);
    }

    public static void testParser(Parser parser) {
        ArrayList<String> originalLines = parser.getOriginalLines();
        ArrayList<Pair<String, Integer>> cleanLines =  parser.getCleanedLines();
        ArrayList<Triple<Command, Object, Integer>> tokenLines = parser.getTokenLines();

        for (int x = 0;x < tokenLines.size();x++) {
            Triple<Command, Object, Integer> tokenLine = tokenLines.get(x);
            Pair<String, Integer> cleanLine = cleanLines.get(x);
            String origianlLine = originalLines.get(cleanLine.y);

            System.out.format("%s\n%s\n", origianlLine, tokenLine);
        }       
    }
}