package edu.miracosta.cs220.project07and08;

import org.junit.Test;
import static org.junit.Assert.fail;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.util.List;

public class DriverTest {
    public static final String TEST_SCRIPT_LOCATION = "CPUEmulator.sh"; // Its in my path.
    public static final String CURRENT_DIRECTORY = System.getProperty("user.dir");
    public static final Path CURRENT_DIRECTORY_PATH = Paths.get(CURRENT_DIRECTORY); 
    public static final String CHAPTER07DIRECTORY = CURRENT_DIRECTORY + "/07";
    public static final String CHAPTER08DIRECTORY = CURRENT_DIRECTORY + "/08";
    public static final int SEARCH_DEPTH = 3; 

    @Test
    public void testDriver() throws Exception {
        runDriver();
        runScripts();
    }

    public void runDriver() throws Exception {
        List<String> vmProjectDirectories = testFiles().map(p -> p.subpath(CURRENT_DIRECTORY_PATH.getNameCount(),p.getNameCount() - 1)) // take advatage that there is one test file per project
                                                       .map(p -> p.toString()) 
                                                       .collect(Collectors.toList());
        for (String projectDirectory : vmProjectDirectories) {
            boolean runWithBootStrap = projectDirectory.endsWith("StaticsTest") || projectDirectory.endsWith("FibonacciElement");
            Driver.run(projectDirectory, runWithBootStrap);
        }
    }

    public void runScripts() throws Exception {
        String errorMessages = testFiles().map(DriverTest::executeScriptWithTestFile) // run script and get results
                                          .filter(p -> p.y != 0) // get all the failing test results 
                                          .map(p -> p.x) // get their error messages
                                          .reduce("", (acc, s) -> acc + "\t" + s + "\n"); // make one string 

        if (errorMessages.length() != 0) { 
            // there was at least one failure 
            fail("\n" + errorMessages);
        }
    }

    private static Stream<Path> testFiles() throws IOException {
        return Stream.concat(
            Files.find(Paths.get(CHAPTER07DIRECTORY), SEARCH_DEPTH, DriverTest::isTestFile), 
            Files.find(Paths.get(CHAPTER08DIRECTORY), SEARCH_DEPTH, DriverTest::isTestFile)
        );
    }

    private static boolean isTestFile(Path path, BasicFileAttributes attributes) {
        boolean isTest = path.toString().endsWith(".tst");
        boolean isVMtest = path.toString().endsWith("VME.tst");
        return isTest && !isVMtest;
    }

    private static Pair<String,Integer> executeScriptWithTestFile(Path testFileLocation)  {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(TEST_SCRIPT_LOCATION, testFileLocation.toString());
            Process process = processBuilder.start(); 
            process.waitFor();

            // Code from: http://stackoverflow.com/questions/16714127/how-to-redirect-process-builders-output-to-a-string
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ( (line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }

            String errorOutput = builder.toString();
            String result = "\"" + ( (errorOutput.length() != 0) ? (errorOutput.replaceAll("\n","")) : ("Success") ) + "\"";
            String fileName = testFileLocation.getFileName().toString().split("\\.")[0];
            
            return new Pair<String,Integer>(fileName + ":" + result, process.exitValue());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
