package edu.miracosta.cs220.project07and08;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.Optional;

/**
    Represents one VM Command transformed into assembly. 
*/
public class AssemblySection {
    private ArrayList<Pair<String,String>> assemblyWithComments;
    private int realAssemblyLineCount;
    public final String abstraction;

    public AssemblySection(String abstraction) {
        if (abstraction == null || abstraction.length() == 0) {
            throw new IllegalArgumentException("Need an abstraction string.");
        }

        assemblyWithComments = new ArrayList<Pair<String,String>>();
        realAssemblyLineCount = 0; 
        this.abstraction = abstraction;
    }

    /** 
        Represents a line of assembly code. 
        - null assembly and null comments are blank spaces. 
        @param assembly Accepts only valid assembly.
        @param comment A comment about the current line. 
    */
    public AssemblySection addLine(String assembly, String comment) {
        if (assembly != null && !isLabel(assembly)) { 
            realAssemblyLineCount++;
        }
        assemblyWithComments.add(new Pair<String, String>(assembly, comment));
        return this;
    }

    public AssemblySection addLines(ArrayList<String> assembly, ArrayList<String> comments) {
        if (assembly.size() != comments.size()) {
            throw new IllegalArgumentException("Assembly and comments must be parallel.");
        }

        int length = assembly.size();

        for (int x = 0;x < length;x++) {
            addLine(assembly.get(x), comments.get(x));
        }

        return this;
    }
    // maybe tab each line in the section 
    public AssemblySection addSection(AssemblySection section) {
        if (section == null) {
            throw new IllegalArgumentException("null section.");
        }
        this.realAssemblyLineCount += section.realAssemblyLineCount;

        this.addLine(null, "<" + section.abstraction + ">");
        this.assemblyWithComments.addAll(section.assemblyWithComments);
        this.addLine(null, "</" + section.abstraction + ">");

        return this;
    }

    public AssemblySection addSections(ArrayList<AssemblySection> assemblySections) {
        if (assemblySections == null) {
            throw new IllegalArgumentException("null sections.");
        }

        int length = assemblySections.size();

        for (int x = 0;x < length;x++) {
            addSection(assemblySections.get(x));
        }

        return this;
    }

    public ArrayList<String> getAssembly() {
        List<String> lines = assemblyWithComments
                            .stream()
                            .map(this::lineFromPair) 
                            .collect(Collectors.toList());

        return new ArrayList<String>(lines);
    }

    public ArrayList<Pair<String,String>> getAssemblyWithComments() {
        return new ArrayList<Pair<String,String>>(Collections.unmodifiableList(assemblyWithComments));
    }

    @Override
    public String toString() {
        return getAssemblyWithStartIndex(Optional.empty())
               .stream()
               .reduce("", (acc, s) -> acc + s + "\n");
    }

    public ArrayList<String> getAssemblyWithStartIndex(Optional<Integer> index) {                
        ArrayList<String> lines = new ArrayList<String>();
        int lineCount = index.orElse(0);

        for (Pair<String,String> pair : assemblyWithComments) {
            String metaData = null;
            
            // We only count assembly lines that are not comments or labels.. 
            if (index.isPresent() && pair.x != null && !isLabel(pair.x)) { 
                metaData = "" + lineCount++;
            }

            lines.add(lineFromPairWithMetaData(pair, metaData));
        }

        return new ArrayList<String>(lines);
    }

    public int getRealAssemblyLineCount() {
        return realAssemblyLineCount;
    }

    // Private helper methods 
    private String lineFromPair(Pair<String,String> pair) {
        return lineFromPairWithMetaData(pair, null);
    }

    private String lineFromPairWithMetaData(Pair<String,String> pair, String metaData) {
        return ( (pair.x != null) ? (pair.x + " ") : ("") ) + // 'assembly'
               ( (pair.y != null || metaData != null) ? ("//") : ("") ) +  // 'assembly' '//'
               ( (metaData != null) ? ("(" + metaData + ")") : ("") )+ // 'assembly' //'(m)'
               ( (pair.y != null) ? (" " + pair.y) : ("") ); // 'assembly' //'(m)' comment'
    }

    private boolean isLabel(String assembly) {
        if (assembly == null) {
            throw new IllegalArgumentException("Null argument.");
        }

        return assembly.startsWith("(");
    }
}
