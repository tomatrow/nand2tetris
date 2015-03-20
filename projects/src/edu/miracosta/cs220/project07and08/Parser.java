package edu.miracosta.cs220.project07and08;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.ListIterator;
import java.util.Collections;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.EnumMap;

/**
    Transforms an array of VM lines into triples of (Command, firstArgument, secondArguemnt).
*/
public class Parser {
    private ArrayList<String> originalLines;
    private ArrayList<Pair<String,Integer>> cleanedLines; // Cleaned line and original line number 
    private ArrayList<Triple<Command, Object, Integer>> tokenLines; // Command, firstArgument, secondArgument

    private static String symbolRegex = "[A-Za-z_.$:]{1}[A-Za-z0-9_.$:]*";    

    public Parser(ArrayList<String> vitualMachineLines) {
        this.originalLines = vitualMachineLines;
        this.cleanedLines = null;
        this.tokenLines = null;
    }

    public void parse() throws ParseException {
        clean();
        tokenize();
    }

    private void clean() {
        cleanedLines = new ArrayList<Pair<String,Integer>>(originalLines.size());

        for (int x = 0;x < originalLines.size();x++) {
            String originalLine = originalLines.get(x);
            String cleanedLine = null;

            if (originalLine.length() == 0) { // blank line 
                // do nothing 
            } else if (originalLine.matches(".*//.*")) { // possible command with comment 
                int commentStartIndex = originalLine.indexOf("//");
                if (commentStartIndex > 0) {
                    cleanedLine = originalLine.substring(0, commentStartIndex);
                }
            } else { // possible command
                // replace inner spaces with single spaces. 
                cleanedLine = originalLine.replaceAll("\\s+", " ");
            }

            if (cleanedLine != null) {
                cleanedLines.add(new Pair<String, Integer>(cleanedLine.trim(), x));
            }
        }
    }

    private void tokenize() throws ParseException {
        tokenLines = new ArrayList<Triple<Command, Object, Integer>>(cleanedLines.size());

        for (int x = 0;x < cleanedLines.size();x++) {
            Pair<String, Integer> cleanedLine = cleanedLines.get(x);
            String[] possibleTokens = cleanedLine.x.split("\\s");

            Command command = Command.symbolMap.get(possibleTokens[0]);
            int argumentCount = command.argumentCount();
            if (command == null || argumentCount != possibleTokens.length - 1) {
                throw new ParseException("Invalid arguments to " + command.toString(), cleanedLine.y);
            }

            Object firstArgumentValue = null;
            Integer secondArgumentValue = null;
            
            try {
                if (argumentCount == 1 || argumentCount == 2) {
                    firstArgumentValue = parseArgument(command.firstArgument(), possibleTokens[1]);
                }
                if (argumentCount == 2) {
                    // The second argument is always an integer
                    secondArgumentValue = (Integer) parseArgument(command.secondArgument(), possibleTokens[2]);
                }
            } catch (Exception e) {
                throw new ParseException(e.getMessage(), cleanedLine.y);
            }
             
            tokenLines.add(new Triple<Command, Object, Integer>(command, firstArgumentValue, secondArgumentValue));
        }
    }

    public ArrayList<String> getOriginalLines() {
        return new ArrayList<String>(Collections.unmodifiableList(originalLines));
    }

    public ArrayList<Pair<String, Integer>> getCleanedLines() {
        return new ArrayList<Pair<String, Integer>>(Collections.unmodifiableList(cleanedLines));
    }

    public ArrayList<Triple<Command, Object, Integer>> getTokenLines() {
        return new ArrayList<Triple<Command, Object, Integer>>(Collections.unmodifiableList(tokenLines));
    }

    // private helper functions 
    private Object parseArgument(Argument argument, String string) throws RuntimeException {
        switch (argument) {
            case SEGMENT:
                return Segment.symbolMap.get(string);
            case INTEGER:
                try {
                    return Integer.valueOf(string);
                } catch (Exception e) {
                    throw new RuntimeException("Invalid constant integer: " + "\"" + string + "\"");
                }
            case NULL:
                return null;
            case FUNCTION_NAME:
            case SYMBOL: // Just return the string 
                if (!string.matches(symbolRegex)) {
                    throw new RuntimeException("Invalid label: " + string);
                }
                return string;
            default: 
                throw new RuntimeException("Unimplemented argument: " + argument);
        }
    }
}