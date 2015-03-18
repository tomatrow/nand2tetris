package edu.miracosta.cs220.project07and08;

/**
  Translates one VM command and it's arguments into assembly.
*/
public class Translator {

    private SymbolTable table;

    public Translator(SymbolTable table) {
        this.table = table;
    }

    public String memoryCommand(Command command, Segment segment, Integer index) {
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

    public String arithmeticCommand(Command command) {
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

    public String flowCommand(Command command, String label) {
        if (command == null || !command.isFlowCommand()) {
            throw new IllegalArgumentException("Non flow command: " + command);
        }

        switch (command) {
            case LABEL:
                return "(" + label +  ")" + "\n";
            case GOTO:
                return "@" + label + "\n" +
                       "0;JMP" + "\n";
            case IF:
                return memoryCommand(Command.PUSH, Segment.CONSTANT, 0) +
                       arithmeticCommand(Command.EQ) + 
                       popWorkingStackIntoD() +
                       "@" + label + "\n" + 
                       "D;JEQ" + "\n"; // jump iff (stackTop != 0)
            default: 
                throw new RuntimeException("Unimplemented Command: " + command.toString());
        }
    }

    private String pushStatic(Integer index) {
        return "@" + table.labelForStaticIndex(index) + "\n" +  // A = staticIndexlabel
               "D = M\n" +  // D = *fileName.i
               pushDontoWorkingStack();
    }

    private String pushReal(int address) {
        return "@" + address + "\n" +
               "D = M\n" + 
               pushDontoWorkingStack(); 

    }

    private String pushVirtual(Segment segment, int index) {
        return dereferencePointerIntoD(segment, index) + 
               pushDontoWorkingStack();
    }

    private String pushConstant(Integer constant) {
        return "@" + constant + "\n" + // A = constant 
               "D = A\n" + // D = constant
               "@SP\n" + // A = &SP
               "A = M\n" + // A = SP
               "M = D\n" + // *SP = constant 
               "@SP\n" + // A = &SP
               "M = M + 1\n"; // SP = SP + 1
    }

    private String popStatic(int index) {
        return popWorkingStackIntoD() +
               "@" + table.labelForStaticIndex(index) + "\n" +  // A = staticIndexlabel
               "M = D\n"; // *staticIndexlabel = D
    }

    private String popReal(int address) {
        return popWorkingStackIntoD() + 
               "@" + address + "\n" +
               "M = D\n";
    }

    private String popVirtual(Segment segment, int index) {
        return popWorkingStackIntoD() + 
               setReferenceToD(segment, index);
    }

    private String unaryArithmeticOperation(String operator) {
        return popWorkingStackIntoD() + 
               "D = " + operator + "D\n" +  // D = 'operator'D
               pushDontoWorkingStack();
    }

    private String binaryArithmeticOperation(String operator) {
        return popWorkingStackIntoD() + 
               "@SP\n" + // A = &SP
               "AM = M - 1\n" + // A = SP - 1; SP = SP - 1;
               "D = M " + operator + " D\n" + // D = D 'operator' *SP
               pushDontoWorkingStack();
    }

    private String equalityOperation(Command command) {
        String label = table.labelForJumpCommand(command);

        return "@R13\n" + // A = R13
               "M = -1\n" + // *R13 = true 
               binaryArithmeticOperation(Command.SUB.operator()) + 
               popWorkingStackIntoD() +
               "@" + label + "\n" + // A = label
               "D;" + command.operator() + "\n" +  // if D == 0 then jump
               "@R13\n" + // A = R13
               "M = 0\n" + // *R13 = false
               "(" + label + ")\n" + 
               "@R13\n" + // A = R13
               "D = M\n" + // D = *R13
               pushDontoWorkingStack();
    }

    private String setReferenceToD(Segment segment, int index) {
        return "@R15\n" + // A = R15
               "M = D\n" + // *R15 = originalD 
               "@" + segment.pointerSymbol() + "\n" + // A = &pointer
               "D = M\n" + // D = pointer
               "@" + index + "\n" + // A = index
               "D = D + A\n" + // D = pointer + index
               "@R14\n" + // A = R14
               "M = D\n" + // *R14 = pointer + index 
               "@R15\n" + // A = R15
               "D = M\n" + // D = originalD
               "@R14\n" + // A = &(pointer + index)
               "A = M\n" + // A = pointer + index
               "M = D\n"; // *(pointer + index) = originalD
    }

    private String dereferencePointerIntoD(Segment segment, int index) {        
        return "@" + segment.pointerSymbol() + "\n" + // A = pointer
               "D = M\n" + // D = pointer
               "@" + index + "\n" + // A = index
               "A = D + A\n" + // A = pointer + index
               "D = M\n" ; // D = *(pointer + index)
    }

    private String popWorkingStackIntoD() {  
        return "@SP\n" + // A = &SP
               "AM = M - 1\n" + // SP = SP - 1; A = SP - 1
               "D = M\n"; // D = *SP
    }

    private String pushDontoWorkingStack() {
        return "@SP\n" +// A = &SP
               "A = M\n" +// A = SP
               "M = D\n" +// *SP = D
               "@SP\n" +// A = &SP
               "M = M + 1\n";// SP = SP + 1
    }
}
