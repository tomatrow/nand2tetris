package edu.miracosta.cs220.project07and08;

public class Translator {
    private static int eqCounter = 0;

    public static String pushConstant(Integer constant) {
        /*
            @constant
            D = A     // D = constant
            @SP       // A = &SP
            A = M     // A = SP
            M = D     // *SP = constant 
            @SP       // A = &SP
            M = M + 1 // SP = SP + 1
        */
        return String.format("@%d\nD = A\n@SP\nA = M\nM = D\n@SP\nM = M + 1\n", constant); 
    }

    // Public interface 
    public static String add() {
        return binaryArithmeticOperation(Command.ADD);
    }

    public static String sub() {
        return binaryArithmeticOperation(Command.SUB);
    }
    
    public static String or() {
        return binaryArithmeticOperation(Command.OR);
    }
    public static String and() {
        return binaryArithmeticOperation(Command.AND);
    }

    public static String not() {
        return unaryOperation(Command.NOT);
    }
    public static String neg() {
        return unaryOperation(Command.NEG);   
    }

    public static String eq() {
        return equalityOperation(Command.EQ);
    }
    public static String lt() {
        return equalityOperation(Command.LT);
    }
    public static String gt() {
        return equalityOperation(Command.GT);
    }

    // Private Helper Strings
    private static String equalityOperation(Command command) {
        String label = "IS_SATISFIED." + eqCounter;
        eqCounter++;

        String formatString = "@%s\nD;%s // if D == 0 then jump\n" + 
                                      setRegisterToBoolean(13, false) +
                                      "(%s)\n";
        
        /*
            setRegisterToBoolean(13, true)
            sub()
            popStackIntoD() // D = 0 || !0

            @label
            D;JEQ // if D == 0 then jump 
            
            setRegisterToBoolean(13, false)
            
            (label)
            
            setDtoRegister(13)
            pushDontoStack()
        */

        return setRegisterToBoolean(13, true) + 
               sub() + 
               popStackIntoD() +
               String.format(formatString, label, command.operator(), label) +
               setDtoRegister(13) +
               pushDontoStack();
    }

    private static String binaryLogicalOperation(Command command) { 
        return binaryArithmeticOperation(command) + 
               pushConstant(-1) + 
               eq();
    }

    private static String setDtoRegister(int register) {
        return String.format("@R%d\nD = M\n", register);
    }
    private static String setRegisterToBoolean(int regester, boolean value) {
        return String.format("@R%d\nM = %d // M[R%d] = true\n", regester, ((value)?-1:0), regester);
    }
    private static String binaryArithmeticOperation(Command command) {
        return popStackIntoD()+ useBinaryArithmeticOperatorOnDandTopOfStack(command) + pushDontoStack();
    }

    private static String unaryOperation(Command command) {
        return popStackIntoD() + useUnaryOperatorOnD(command) + pushDontoStack();
    }

    private static String useUnaryOperatorOnD(Command command) {
        String operator = command.operator();
        /*
            D = OPERATORD // D = OPERATORD
        */
        String formatString = "D = %sD // D = %sD\n";
        return String.format(formatString, operator, operator);
    }

    private static String useBinaryArithmeticOperatorOnDandTopOfStack(Command command) {
        String operator = command.operator();
        /*            
            // OPERATOR on D and top of stack 
            @SP // A = &SP
            AM = M - 1 // A = SP - 1; SP = SP - 1;
            D = D OPERATOR M // D = D OPERATOR *SP
        */
        String formatString = "// %s on D and top of stack\n@SP // A = &SP\nAM = M - 1 // A = SP - 1; SP = SP - 1;\nD = M %s D // D = D %s *SP\n";
        return String.format(formatString, operator, operator, operator);
    }

    private static String popStackIntoD() {
        /*
            // pop top into D
            @SP // A = &SP
            AM = M - 1 // SP = SP - 1; A = SP - 1
            D = M // D = *SP
        */
        
        return "// pop top into D\n@SP // A = &SP\nAM = M - 1 // SP = SP - 1; A = SP - 1\nD = M // D = *SP\n";
    }
    private static String pushDontoStack() {
        /*
            // push D onto stack 
            @SP // A = &SP
            A = M // A = SP
            M = D // *SP = D
            @SP // A = &SP
            M = M + 1 // SP = SP + 1
        */

        return "// push D onto stack \n@SP // A = &SP\nA = M // A = SP\nM = D // *SP = D\n@SP // A = &SP\nM = M + 1 // SP = SP + 1\n";
    }
}



