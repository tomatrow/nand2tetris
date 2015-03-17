package edu.miracosta.cs220.project07and08;

import java.util.ArrayList;
import java.util.Collections;
/**
    Encode a series of (Command,Object,Integer) triples into an assembly program. 
*/
public class Encoder {

    private Translator translator;
    private ArrayList<Triple<Command, Object, Integer>> tokens;
    private StringBuilder assembly;

    public Encoder(Translator translator, ArrayList<Triple<Command, Object, Integer>> tokens) {
        this.translator = translator;
        this.tokens = tokens;
        this.assembly = new StringBuilder();
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
            String assemblyInstructions;
            Command command = token.x;

            if (command.isArithmeticOperation()) {
                assemblyInstructions = translator.arithmeticCommand(command);
            } else if (command.isMemoryCommand()) {
                Segment segment = (Segment) token.y;
                assemblyInstructions = translator.memoryCommand(command, segment, token.z);
            } else {
                throw new RuntimeException("Unimplemented Command: " + command);
            }

            assembly.append(assemblyInstructions);
        }
    }

    public String getAssembly() {
        return assembly.toString();
    }
}
