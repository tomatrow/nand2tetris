package edu.miracosta.cs220.project07and08;

import java.util.ArrayList;
import java.util.Collections;

public class Encoder {

    private String fileName;
    private ArrayList<Triple<Command, Object, Integer>> tokens;
    private StringBuilder assemblyProgram;

    public Encoder(ArrayList<Triple<Command, Object, Integer>> tokens, String fileName) {
        this.tokens = tokens;
        this.fileName = fileName;
        this.assemblyProgram = new StringBuilder();
    }

    public void encode() {
        validate();
        translate();
    }

    public void validate() {
        for (int i = 0;i < tokens.size();i++) {
            Triple<Command, Object, Integer> token = tokens.get(i);
            boolean validFirstArgument = token.x.validFirstArgument(token.y);
            boolean validSecondArgument = token.x.validSecondArgument(token.z);

            if (!validFirstArgument || !validSecondArgument) {
                String argumentErrors = "" + ((validFirstArgument)?"":"1") + ((validSecondArgument)?"":", 2");
                throw new RuntimeException("Invalid token: " + token.toString() + argumentErrors);
            } 
        }
    }

    private void translate() {
        for (int i = 0;i < tokens.size();i++) {
            Triple<Command, Object, Integer> token = tokens.get(i);
            String assemblyString;
            Command command = token.x;

            if (command.isArithmeticOperation()) {
                assemblyString = Translator.arithmeticCommand(command);
            } else if (command.isMemoryCommand()) {
                Segment segment = (Segment) token.y;
                assemblyString = Translator.memoryCommand(command, segment, token.z);
            } else {
                throw new RuntimeException("Unimplemented Command: " + command);
            }

            assemblyProgram.append(assemblyString);
        }
    }

    public String getAssemblyProgram() {
        return assemblyProgram.toString();
    }
}

