package edu.miracosta.cs220.project07and08;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

/**
    Represents one VM Command transformed into assembly. 
*/
public class AssemblySection {
    // private ArrayList<Pair<String,String>> assemblyWithComments;

    // public AssemblySection() {
    //     assemblyWithComments = new ArrayList<Pair<String,String>>();
    // }

    // /** 
    //     Represents a line of assembly code. 
    //     - null assembly and null comments are blank spaces. 
    //     @param assembly Accepts only valid assembly.
    //     @param comment A comment about the current line. 
    // */
    // public void add(String assembly, String comment) {
    //     assemblyWithComments.add(new Pair<String, String>(assembly, comment));
    // }

    // public void add(String[] assembly, String[] comments) {
    //     if (assembly.length != comments.length) {
    //         throw new IllegalArgumentException("Assembly and comments must be parallel.");
    //     }

    //     int length = assembly.length;

    //     for (int x = 0;x < length;x++) {
    //         add(assembly[x], comments[x]);
    //     }
    // }

    // public void add(ArrayList<String> assembly, ArrayList<String> comments) {
    //     add((String[]) assembly.toArray(), (String[]) comments.toArray());
    // }

    // public void add(AssemblySection section) {
    //     if (section == null) {
    //         throw new IllegalArgumentException("null section.");
    //     }

    //     this.assemblyWithComments.addAll(section.assemblyWithComments);
    // }

    // public void add(AssemblySection[] assemblySections) {
    //     if (assemblySections == null) {
    //         throw new IllegalArgumentException("null sections.");
    //     }        

    //     for (int x = 0;x < assemblySections.length;x++) {
    //         add(assemblySections[x]);
    //     }
    // }
    // public void add(ArrayList<AssemblySection> assemblySections) {
    //     if (assemblySections == null) {
    //         throw new IllegalArgumentException("null sections.");
    //     }
    //     add((AssemblySection[]) assemblySections.toArray());
    // }

    // public ArrayList<String> getAssembly() {
    //     List<String> lines = assemblyWithComments
    //                         .stream()
    //                         .map(this::pairToLine) 
    //                         .collect(Collectors.toList());

    //     return new ArrayList<String>(lines);
    // }

    // public ArrayList<Pair<String,String>> getAssemblyWithComments() {
    //     return new ArrayList<Pair<String,String>>(Collections.unmodifiableList(assemblyWithComments));
    // }

    // // Private helper methods 
    // private String pairToLine(Pair<String,String> pair) {
    //     String assembly = (pair.x != null)? (pair.x) : ("");
    //     String comment = (pair.y != null)? ("// " + pair.y) : ("");

    //     return assembly + comment;
    // }
}
