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

            switch (token.x) {
                case ADD:
                    assemblyString = Translator.add();
                    break;
                case SUB:
                    assemblyString = Translator.sub();
                    break;
                case OR:
                    assemblyString = Translator.or();
                    break;
                case AND:
                    assemblyString = Translator.and();
                    break;
                case NOT:
                    assemblyString = Translator.not();
                    break;
                case NEG:
                    assemblyString = Translator.neg();
                    break;
                case EQ:
                    assemblyString = Translator.eq();
                    break;
                case LT:
                    assemblyString = Translator.lt();
                    break;
                case GT:
                    assemblyString = Translator.gt();
                    break;
                case PUSH:
                    assemblyString = Translator.pushConstant(token.z);
                    break;
                default: 
                    throw new RuntimeException("Not implemented");
            }

            assemblyProgram.append(assemblyString);
        }
    }
    
    private void push(Segment segment, Integer index) {
        if (segment == null || index == null) {
            throw new IllegalArgumentException();
        }

        switch (segment) {
            case CONSTANT:
                assemblyProgram.append(Translator.pushConstant(index));
            default:
                throw new RuntimeException("Unimplemented segment: " + segment.toString());
        }

    }
    
    private void pop(Segment segment, Integer index) {

    }

    public String getAssemblyProgram() {
        return assemblyProgram.toString();
    }
}

