package edu.miracosta.cs220.project07and08;

/**
  Translates one VM command and it's arguments into assembly.
*/
public class Translator {

    private SymbolTable table;

    public Translator(SymbolTable table) {
        if (table == null) {
            throw new IllegalArgumentException("Need a table.");
        }
        this.table = table;
    }

    public SymbolTable getTable() {
        return table;
    }

    public AssemblySection bootStrap() {
        return new AssemblySection("BootStrap") {{
            addLine("@256", null);
            addLine("D = A", null);
            addLine("@SP", null);
            addLine("M = D", null);
            addSection(functionCommand(Command.CALL, "Sys.init", 0));
        }};
    }

    public AssemblySection memoryCommand(Command command, Segment segment, Integer index) {
        if (command == null || !command.isMemoryCommand()) {
            throw new IllegalArgumentException("Non memory command: " + command);
        }

        switch (command) { 
            case PUSH:
                switch (segment) {
                    case CONSTANT:
                        return pushConstant(index);
                    case STATIC:
                        return pushStatic(index);
                    case TEMP:
                    case POINTER:
                        return pushReal(segment.baseAddress() + index);
                    case ARGUMENT:
                    case LOCAL:
                    case THIS:
                    case THAT:
                        return pushVirtual(segment, index);
                }
            case POP:
                switch (segment) {
                    case STATIC:
                        return popStatic(index);
                    case TEMP:
                    case POINTER:
                        return popReal(segment.baseAddress() + index);
                    case ARGUMENT:
                    case LOCAL:
                    case THIS:
                    case THAT:
                        return popVirtual(segment, index);
                    case CONSTANT:
                        throw new IllegalArgumentException("Constant segment cannot be popped.");
                }
            default:
                throw new RuntimeException("Unimplemented Command: " + command.toString());
        }
    }

    public AssemblySection arithmeticCommand(Command command) {
        if (command == null || !command.isArithmeticOperation()) {
            throw new IllegalArgumentException("Non arithmetic command: " + command);
        }

        switch (command) {
            case NOT:
            case NEG:
                return unaryArithmeticOperation(command.operator());
            case ADD:
            case SUB:
            case OR:
            case AND:
                return binaryArithmeticOperation(command.operator());
            case EQ:
            case LT:
            case GT:
                return equalityOperation(command);
            default: 
                throw new RuntimeException("Unimplemented Command: " + command.toString());
        }
    }

    public AssemblySection flowCommand(Command command, String label) {
        if (command == null || !command.isFlowCommand()) {
            throw new IllegalArgumentException("Non flow command: " + command);
        }

        String fullLabel = table.labelForFlowCommand(label);

        switch (command) {
            case LABEL:
                return new AssemblySection("LABEL" + " " + fullLabel){{ addLine("(" + fullLabel + ")", null); }};
            case GOTO:
                return new AssemblySection("GOTO" + " " + fullLabel){{ 
                    addLine("@" + fullLabel, null); 
                    addLine("0;JMP", null);
                }};
            case IF:
                return new AssemblySection("IF-GOTO" + " " + fullLabel){{ 
                    addSection(popWorkingStackIntoD());
                    addLine("@" + fullLabel, null);
                    addLine("D;JNE", "jump iff (stackTop != 0)");
                }};
            default: 
                throw new RuntimeException("Unimplemented Command: " + command.toString());
        }
    }

    public AssemblySection functionCommand(Command command, String label, Integer number) {
        if (command == null || !command.isFunctionCommand()) {
            throw new IllegalArgumentException("Non function command: " + command);
        }

        switch (command) {
            case FUNCTION:
                table.setFunctionName(label);

                AssemblySection section = new AssemblySection("FUNCTION" + " " + label);
                section.addLine("(" + label + ")", null);
                for (int x = 0;x < number;x++) {
                    section.addSection(memoryCommand(Command.PUSH, Segment.CONSTANT, 0));
                }
                return section;
            case RETURN:
                return returnFromFunction();
            case CALL:
                return call(label, number);
            default:
                throw new RuntimeException("Unimplemented Command: " + command.toString());
        }
    }

    private AssemblySection call(String functionName, int argumentCount) {
        /*   push return-address
             push LCL
             push ARG
             push THIS
             push THAT
             ARG = SP-(N+5)
             LCL = SP
             goto f
         (return-address) 
        */
        AssemblySection section = new AssemblySection("call" + "(" + functionName + ")" + "withArguments" + argumentCount);

        String returnAddress = table.labelForFunctionReturn();        
        
        // push return-address
        section.addLine("@" + returnAddress, "A = return-address")
               .addLine("D = A", "D = return-address")
               .addSection(pushDontoWorkingStack());  // push(return-address)  

        // push LCL
        // push ARG
        // push THIS
        // push THAT
        Segment[] segments = { Segment.LOCAL, Segment.ARGUMENT, Segment.THIS, Segment.THAT };
        for (int x = 0; x < segments.length;x++) {
            section.addSection(setDtoPointer(segments[x]))
                   .addSection(pushDontoWorkingStack());
        }

        // ARG = SP-(N+5)
        section.addLine("@SP","A = &SP")
               .addLine("D = M","D = SP")
               .addLine("@" + (argumentCount + 5),"A = argumentCount + 5")
               .addLine("D = D - A","D = SP - argumentCount - 5")
               .addLine("@ARG","A = &ARG")
               .addLine("M = D","ARG = SP - argumentCount - 5");
        
        // LCL = SP
        section.addLine("@SP", "A = &SP")
               .addLine("D = M", "D = SP")
               .addLine("@LCL", "A = &LCL")
               .addLine("M = D", "LCL = SP");
        
        // goto f
        section.addLine("@" + functionName, "A = functionName")
               .addLine("0;JMP", "goto functionName");

        // (return-address)
        section.addLine("(" + returnAddress + ")", null);

        return section;
    }

    private AssemblySection returnFromFunction() {
        /* FRAME = LCL
           *ARG = pop()
           SP = ARG+1
           THAT = *(FRAME - 1)
           THIS = *(FRAME - 2)
           ARG = *(FRAME - 3) 
           LCL = *(FRAME - 4)
           RET = *(FRAME - 5)
           goto RET */
        AssemblySection section = new AssemblySection("returnFromFunction");
        
        // FRAME = LCL
        section.addLine("@LCL","A = &LCL")
               .addLine("D = M","D = LCL")
               .addLine("@R13","A = &R13")
               .addLine("M = D","R13 = LCL");

        // *ARG = pop()
        section.addSection(popWorkingStackIntoD())
               .addLine("@ARG","A = &ARG")
               .addLine("A = M","A = ARG")
               .addLine("M = D","*ARG = pop()");

        // SP = ARG+1            
        section.addLine("@ARG","A = &ARG")
               .addLine("D = M + 1","D = ARG + 1")
               .addLine("@SP","A = &SP")
               .addLine("M = D","SP = ARG + 1");

        // THAT = *(FRAME - 1)
        // THIS = *(FRAME - 2)
        // ARG = *(FRAME - 3) 
        // LCL = *(FRAME - 4)
        AssemblySection setDtoDecrementedFramePointer = new AssemblySection("setDtoDecrementedFramePointer") {{
            addLine("@R13","A = &R13");
            addLine("AM = M - 1","AM = R13 - 1");
            addLine("D = M","D = *(R13 - 1)");
        }};
        Segment[] segments = {Segment.THAT, Segment.THIS, Segment.ARGUMENT, Segment.LOCAL};
        for (int x = 0; x < segments.length;x++) {
            section.addSection(setDtoDecrementedFramePointer)
                   .addSection(setPointerToD(segments[x]));
        }

        // RET = *(FRAME - 5)
        section.addSection(setDtoDecrementedFramePointer)
               .addLine("A = D","A = *(R13 - 5)")
               .addLine("0;JMP","jump to RET");

        return section;
    }

    private AssemblySection pushStatic(Integer index) {
        return new AssemblySection("pushStatic" + index) {{
            addLine("@" + table.labelForStaticIndex(index), "A = staticIndexlabel");
            addLine("D = M", "D = *fileName.i");
            addSection(pushDontoWorkingStack());
        }};
    }

    private AssemblySection pushReal(int address) {
        return new AssemblySection("pushReal" + address) {{
            addLine("@" + address, null);
            addLine("D = M", null);
            addSection(pushDontoWorkingStack());
        }};
    }

    private AssemblySection pushVirtual(Segment segment, int index) {
        return new AssemblySection("pushVirtual" + segment + "." + index) {{
            addSection(dereferencePointerIntoD(segment, index));
            addSection(pushDontoWorkingStack());
        }};
    }

    private AssemblySection pushConstant(Integer constant) {
        return new AssemblySection("pushConstant" + constant) {{
            addLine("@" + constant,"A = constant");
            addLine("D = A","D = constant");
            addLine("@SP","A = &SP");
            addLine("A = M","A = SP");
            addLine("M = D","*SP = constant");
            addLine("@SP","A = &SP");
            addLine("M = M + 1","SP = SP + 1");
        }};
    }

    private AssemblySection popStatic(int index) {
        return new AssemblySection("popStatic." + index) {{
            addSection(popWorkingStackIntoD());
            addLine("@" + table.labelForStaticIndex(index), "A = staticIndexlabel");
            addLine("M = D", "*staticIndexlabel = D");
        }};
    }

    private AssemblySection popReal(int address) {
        return new AssemblySection("popReal." + address) {{
            addSection(popWorkingStackIntoD());
            addLine("@" + address, null);
            addLine("M = D", null);
        }};
    }

    private AssemblySection popVirtual(Segment segment, int index) {
        return new AssemblySection("popVirtual" + segment + "." + index) {{
            addSection(popWorkingStackIntoD());
            addSection(setReferenceToD(segment, index));
        }};
    }

    private AssemblySection unaryArithmeticOperation(String operator) {
        return new AssemblySection("unaryArithmeticOperation" + " " + operator) {{
            addSection(popWorkingStackIntoD());
            addLine("D = " + operator + "D","D = 'operator'D");
            addSection(pushDontoWorkingStack());
        }};
    }

    private AssemblySection binaryArithmeticOperation(String operator) {
        return new AssemblySection("binaryArithmeticOperation" + " " + operator) {{
            addSection(popWorkingStackIntoD());
            addLine("@SP",                    "A = &SP");
            addLine("AM = M - 1",             "A = SP - 1; SP = SP - 1;");
            addLine("D = M" + operator + "D", "D = D 'operator' *SP");
            addSection(pushDontoWorkingStack());
        }};
    }

    private AssemblySection equalityOperation(Command command) {
        String label = table.labelForJumpCommand(command);
        return new AssemblySection("equalityOperation" + command) {{
            addLine("@R13","A = R13");
            addLine("M = -1","*R13 = true");
            addSection(binaryArithmeticOperation(Command.SUB.operator()));
            addSection(popWorkingStackIntoD());
            addLine("@" + label,"A = label");
            addLine("D;" + command.operator(),"if D == 0 then jump");
            addLine("@R13","A = R13");
            addLine("M = 0","*R13 = false");
            addLine("(" + label + ")", null);
            addLine("@R13","A = R13");
            addLine("D = M","D = *R13");
            addSection(pushDontoWorkingStack());
        }};
    }

    private AssemblySection setPointerToD(Segment segment) {
        return new AssemblySection("set" + segment + "toD") {{
            addLine("@" + segment.pointerSymbol(),"A = &pointer");
            addLine("M = D","pointer = D");
        }};
    }

    private AssemblySection setDtoPointer(Segment segment) {
        return new AssemblySection("setDto" + segment) {{
            addLine("@" + segment.pointerSymbol(), "A = &pointer");
            addLine("D = M",                       "D = pointer");
        }};
    }

    private AssemblySection setReferenceToD(Segment segment, int index) {
        return new AssemblySection("set" + segment + "." + index + "toD") {{
            addLine("@R15",                        "A = R15");
            addLine("M = D",                       "*R15 = originalD");
            addLine("@" + segment.pointerSymbol(), "A = &pointer");
            addLine("D = M",                       "D = pointer");
            addLine("@" + index,                   "A = index");
            addLine("D = D + A",                   "D = pointer + index");
            addLine("@R14",                        "A = R14");
            addLine("M = D",                       "*R14 = pointer + index");
            addLine("@R15",                        "A = R15");
            addLine("D = M",                       "D = originalD");
            addLine("@R14",                        "A = &(pointer + index)");
            addLine("A = M",                       "A = pointer + index");
            addLine("M = D",                       "*(pointer + index) = originalD");
        }};
    }

    private AssemblySection dereferencePointerIntoD(Segment segment, int index) {        
        return new AssemblySection("dereference" + segment + "." + index + "intoD") {{
            addLine("@" + segment.pointerSymbol(), "A = pointer");
            addLine("D = M",                       "D = pointer");
            addLine("@" + index,                   "A = index");
            addLine("A = D + A",                   "A = pointer + index");
            addLine("D = M",                       "D = *(pointer + index)");
        }};
    }

    private AssemblySection popWorkingStackIntoD() {  
        return new AssemblySection("popWorkingStackIntoD") {{
            addLine("@SP",        "A = &SP");
            addLine("AM = M - 1", "SP = SP - 1; A = SP - 1");
            addLine("D = M",      "D = *SP");
        }};
    }

    private AssemblySection pushDontoWorkingStack() {
        return new AssemblySection("pushDontoWorkingStack") {{
            addLine("@SP",       "A = &SP");
            addLine("A = M",     "A = SP");
            addLine("M = D",     "*SP = D");
            addLine("@SP",       "A = &SP");
            addLine("M = M + 1", "SP = SP + 1");
        }};
    }
}
