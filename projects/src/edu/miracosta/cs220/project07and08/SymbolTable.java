package edu.miracosta.cs220.project07and08;

import java.util.ArrayList;

/**
    Generates labels. 
*/
public class SymbolTable {
    
    private ArrayList<String> fileNames;
    private int jumpLabelCounter;

    SymbolTable() {
        fileNames = new ArrayList<String>();
        jumpLabelCounter = 0;
    }

    public String getFileName() {
        if (fileNames.size() == 0) {
            return null;
        }

        return fileNames.get(fileNames.size() - 1);
    }

    public void setFileName(String fileName) {
        if (fileName == null || fileNames.contains(fileName)) {
            throw new IllegalArgumentException("Already used filename:" + "\"" + fileName + "\"");
        }

        fileNames.add(fileName);
    }

    public String labelForJumpCommand(Command command) {
        if (command == null || !command.isJumpCommand()) {
            throw new IllegalArgumentException("Not a jump command:" + "\"" + command + "\"");
        }

        String label = command.operator() + jumpLabelCounter;
        jumpLabelCounter++;

        return label;
    }

    public String labelForStaticIndex(Integer i) {
        return getFileName() + "." + i;
    }
}