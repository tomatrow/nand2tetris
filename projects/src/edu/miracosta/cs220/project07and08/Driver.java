package edu.miracosta.cs220.project07and08;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.stream.Collectors;
import java.nio.file.LinkOption;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
    Main class. 
    Usage: `java Driver path/to/vmfile.vm`
*/
public class Driver {
    public static String ORIGINAL_EXTENTION = "vm";
    public static String TRANSLATION_EXTENTION = "asm";
    public static boolean DEBUG = true;

    public static void main(String[] args) throws Exception {
        if (args.length != 0) { 
            run(args[0], true);
        } else {
            if (DEBUG) {
                test();
            } else {
                throw new IllegalArgumentException("USAGE: \"java Driver path/to/vmfile.vm\"");   
            }
        }
    }

    public static void run(String pathName, boolean bootStrap) throws Exception {
        // paths 
        Path programDirectory = Paths.get(pathName);
        if (!Files.isDirectory(programDirectory, LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("Not a valid directoy: " + programDirectory);
        }

        List<Path> vmFilePaths = Files.list(programDirectory).filter(path -> path.toString().matches(".+\\." + ORIGINAL_EXTENTION)).collect(Collectors.toList());
        if (vmFilePaths.size() == 0) {
            throw new IOException("No " + ORIGINAL_EXTENTION + " files in directory: " + programDirectory);
        }

        // translating 
        Translator translator = new Translator(new SymbolTable());
        List<String> writeLines = new ArrayList<String>(); // Files.write() only accepts Lists<? extends CharSequence>

        if (bootStrap) {
            writeLines.add(translator.bootStrap());
        }

        for (Path vmFilePath : vmFilePaths) {
            String fileNameWithoutExtention = vmFilePath.getFileName().toString().split("\\." + ORIGINAL_EXTENTION)[0];
            translator.getTable().setFileName(fileNameWithoutExtention);

            ArrayList<String> readlines = new ArrayList<String>(Files.readAllLines(vmFilePath));
            String translation = translate(readlines, translator);
            writeLines.add(translation);
        }

        // writing 
        Path assemblyFilepath = Paths.get(programDirectory.getFileName() + "." + TRANSLATION_EXTENTION);
        Path writePath = programDirectory.resolve(assemblyFilepath);
        Files.write(writePath, writeLines, StandardCharsets.US_ASCII); // Not using US_ASCII will break the CPUEmulator...which was a warning in the book.
    }

    public static String translate(ArrayList<String> vmLines, Translator translator) throws ParseException {
        // parsing 
        Parser parser = new Parser(vmLines);
        parser.parse();
        ArrayList<Triple<Command, Object, Integer>> tokens = parser.getTokenLines();

        // encoding
        Encoder encoder = new Encoder(translator, tokens);
        encoder.encode();

        return encoder.getAssembly();
    }

    public static void test() {
        System.out.println("RUNNING TESTS.");     
        JUnitCore.runClasses(DriverTest.class).getFailures()
            .stream()
            .forEach(System.out::println);
    }
}
