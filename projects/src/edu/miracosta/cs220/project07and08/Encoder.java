package edu.miracosta.cs220.project07and08;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

/**
    Encode a series of (Command,Object,Integer) triples into an assembly program. 
*/
public class Encoder {

    private Translator translator;
    private ArrayList<Triple<Command, Object, Integer>> tokens;
    private AssemblySection fileSection;

    public Encoder(Translator translator, ArrayList<Triple<Command, Object, Integer>> tokens) {
        this.translator = translator;
        this.tokens = tokens;
        this.fileSection = null;
    }

    public void encode() {
        this.fileSection = translate();
    }

    private AssemblySection translate() {
        AssemblySection fileSection = new AssemblySection(translator.getTable().getFileName());

        for (int i = 0;i < tokens.size();i++) {
            Triple<Command, Object, Integer> token = tokens.get(i);
            AssemblySection section;
            Command command = token.x;

            if (command.isArithmeticOperation()) {
                section = translator.arithmeticCommand(command);
            } else if (command.isMemoryCommand()) {
                Segment segment = (Segment) token.y;
                section = translator.memoryCommand(command, segment, token.z);
            } else if (command.isFlowCommand()) {
                String label = (String) token.y;
                section = translator.flowCommand(command, label);
            } else if (command.isFunctionCommand()) {
                String label = (String) token.y;
                Integer number = token.z;
                section = translator.functionCommand(command, label, number);
            } else {
                throw new RuntimeException("Unimplemented Command: " + command);
            }

            fileSection.addSection(section);
        }

        return fileSection;
    }


    public AssemblySection getAssemblyFileSection() {
        return fileSection;
    }
}
