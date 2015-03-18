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
        translate();
    }

    private void translate() {
        for (int i = 0;i < tokens.size();i++) {
            Triple<Command, Object, Integer> token = tokens.get(i);
            String assemblySegment;
            Command command = token.x;

            if (command.isArithmeticOperation()) {
                assemblySegment = translator.arithmeticCommand(command);
            } else if (command.isMemoryCommand()) {
                Segment segment = (Segment) token.y;
                assemblySegment = translator.memoryCommand(command, segment, token.z);
            } else if (command.isFlowCommand()) {
                String label = (String) token.y;
                assemblySegment = translator.flowCommand(command, label);
            } else {
                throw new RuntimeException("Unimplemented Command: " + command);
            }

            assembly.append(assemblySegment);
        }
    }

    public String getAssembly() {
        return assembly.toString();
    }
}
