package edu.miracosta.cs220.project07and08;

public class Translator {
    private static int eqCounter = 0;

    public static String memoryCommand(Command command, Segment segment, Integer index) {
        if (command == null || !command.isMemoryCommand()) {
            throw new IllegalArgumentException("Non memory command: " + command);
        }

        switch (command) { 
            case PUSH:
                if (segment != Segment.CONSTANT) {
                    throw new RuntimeException("Unimplemented PUSH segment: " + segment);
                }
                return pushConstant(index);
            default:
                throw new RuntimeException("Unimplemented Command: " + command.toString());
        }
    }

    public static String arithmeticCommand(Command command) {
        if (command == null || !command.isArithmeticOperation()) {
            throw new IllegalArgumentException("Non arithmetic command: " + command);
        }

        switch (command) {
            case NOT:
            case NEG:
                return unaryArithmeticOperation(command);
            case ADD:
            case SUB:
            case OR:
            case AND:
                return binaryArithmeticOperation(command);
            case EQ:
            case LT:
            case GT:
                return equalityOperation(command);
            default: 
                throw new RuntimeException("Unimplemented Command: " + command.toString());
        }
    }

    // Private Helper Strings
    /*
        @constant
        D = A     // D = constant
        @SP       // A = &SP
        A = M     // A = SP
        M = D     // *SP = constant 
        @SP       // A = &SP
        M = M + 1 // SP = SP + 1
    */
    private static String pushConstant(Integer constant) {
        return String.format("@%d\nD = A\n@SP\nA = M\nM = D\n@SP\nM = M + 1\n", constant); 
    }

    /*
        setRegisterToBoolean(13, true)
        sub()
        popStackIntoD() // D = 0 || !0

        |@label
        |D;OPERATOR // if D == 0 then 

        |setRegisterToBoolean(13, false) // set ansert to false 

        |(label)
        
        setDtoRegister(13)
        pushDontoStack()
    */
    private static String equalityOperation(Command command) {
        String label = "IS_SATISFIED." + eqCounter;
        eqCounter++;

        String formatString = "@%s\nD;%s // if D == 0 then jump\n" +  
                                      setRegisterToBoolean(13, false) +
                                      "(%s)\n";

        return setRegisterToBoolean(13, true) + 
               arithmeticCommand(Command.SUB) + 
               popStackIntoD() +
               String.format(formatString, label, command.operator(), label) +
               setDtoRegister(13) +
               pushDontoStack();
    }

    private static String binaryArithmeticOperation(Command command) {
        return popStackIntoD()+ useBinaryArithmeticOperatorOnDandTopOfStack(command) + pushDontoStack();
    }

    private static String unaryArithmeticOperation(Command command) {
        return popStackIntoD() + useUnaryOperatorOnD(command) + pushDontoStack();
    }

    /*
        D = 'OPERATOR'D // D = 'OPERATOR'D
    */
    private static String useUnaryOperatorOnD(Command command) {
        String operator = command.operator();
        String formatString = "D = %sD // D = %sD\n";
        return String.format(formatString, operator, operator);
    }

    /*            
        // 'OPERATOR' on D and top of stack 
        @SP // A = &SP
        AM = M - 1 // A = SP - 1; SP = SP - 1;
        D = D 'OPERATOR' M // D = D 'OPERATOR' *SP
    */
    private static String useBinaryArithmeticOperatorOnDandTopOfStack(Command command) {
        String operator = command.operator();
        String formatString = "// %s on D and top of stack\n@SP // A = &SP\nAM = M - 1 // A = SP - 1; SP = SP - 1;\nD = M %s D // D = D %s *SP\n";
        return String.format(formatString, operator, operator, operator);
    }

    // D operations 
    private static String setDtoRegister(int register) {
        return String.format("@R%d\nD = M\n", register);
    }

    private static String setRegisterToBoolean(int regester, boolean value) {
        return String.format("@R%d\nM = %d // M[R%d] = true\n", regester, ((value)?-1:0), regester);
    }

    /*
        // pop top into D
        @SP // A = &SP
        AM = M - 1 // SP = SP - 1; A = SP - 1
        D = M // D = *SP
    */
    private static String popStackIntoD() {  
        return "// pop top into D\n@SP // A = &SP\nAM = M - 1 // SP = SP - 1; A = SP - 1\nD = M // D = *SP\n";
    }

    /*
        // push D onto stack 
        @SP // A = &SP
        A = M // A = SP
        M = D // *SP = D
        @SP // A = &SP
        M = M + 1 // SP = SP + 1
    */
    private static String pushDontoStack() {
        return "// push D onto stack \n@SP // A = &SP\nA = M // A = SP\nM = D // *SP = D\n@SP // A = &SP\nM = M + 1 // SP = SP + 1\n";
    }
}



