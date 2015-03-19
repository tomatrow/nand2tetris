package edu.miracosta.cs220.project07and08;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
    Enumeraton of all the VM commands. 
*/
public enum Command {
    PUSH, POP, AND, OR, NOT, ADD, SUB, NEG, EQ, GT, LT, LABEL, GOTO, IF, FUNCTION, RETURN, CALL; 

    public static final Map<String, Command> symbolMap = new HashMap<String, Command>() {{
        put("push", PUSH);
        put("pop", POP);
        put("and", AND);
        put("or", OR);
        put("not", NOT);
        put("add", ADD);
        put("sub", SUB);
        put("neg", NEG);
        put("eq", EQ);
        put("gt", GT);
        put("lt", LT);
        put("label", LABEL);
        put("goto", GOTO);
        put("if-goto", IF);
        put("function", FUNCTION);
        put("return", RETURN);
        put("call", CALL);
    }};

    public int argumentCount() {
        switch (this) {
            case AND:
            case OR:
            case NOT:
            case ADD:
            case SUB:
            case NEG:
            case EQ:
            case GT:
            case LT:
            case RETURN:
                return 0;
            case LABEL:
            case GOTO:
            case IF:
                return 1;
            case PUSH:
            case POP:
            case FUNCTION:
            case CALL:
                return 2;
            default: 
                throw new RuntimeException();
        }
    }

    public Argument firstArgument() {
        switch (this) {
            case PUSH:
            case POP:
                return Argument.SEGMENT;
            case LABEL:
            case GOTO:
            case IF:
                return Argument.SYMBOL;
            case FUNCTION:
            case CALL:
                return Argument.FUNCTION_NAME;
            default: 
                return Argument.NULL;
        }
    }

     public Argument secondArgument() {
        switch (this) {
            case PUSH:
            case POP:
            case FUNCTION:
            case CALL:
                return Argument.INTEGER;
            default: 
                return Argument.NULL;
        }
    }

    public boolean isArithmeticOperation() {
        switch (this) {
            case AND:
            case OR:
            case NOT:
            case ADD:
            case SUB:
            case NEG:
            case EQ:
            case GT:
            case LT:
                return true;
            default: 
                return false; 
        }
    }    

    public boolean isJumpCommand() {
        switch (this) {
            case EQ:
            case GT:
            case LT:
                return true;
            default: 
                return false; 
        }
    }

    public boolean isMemoryCommand() {
        switch (this) {
            case PUSH:
            case POP:
                return true;
            default:
                return false;
        }
    }

    public boolean isFlowCommand() {
        switch (this) {
            case LABEL:
            case GOTO:
            case IF:
                return true;
            default:
                return false;
        }
    }

    public boolean isFunctionCommand() {
        switch (this) {
            case RETURN:
            case FUNCTION:
            case CALL:
                return true;
            default:
                return false;
        }
    }

    public boolean isUnaryStackOperation() {
        switch (this) {
            case NOT:
            case NEG:
                return true;
            default:
                return false;
        }
    }

    public boolean isBinaryStackOperation() {
        switch (this) {
            case AND:
            case OR:
            case ADD:
            case SUB:
            case EQ:
            case GT:
            case LT:
                return true;
            default:
                return false;
        }
    }

    public String operator() {
        switch (this) {
            case AND:
                return "&";
            case OR:
                return "|";
            case ADD:
                return "+";
            case SUB:
            case NEG:
                return "-";
            case NOT:
                return "!";
            case EQ:
                return "JEQ";
            case GT:
                return "JGT";
            case LT:
                return "JLT";
            default: 
                throw new RuntimeException("Invalid operator " + "\"" + this.toString() + "\"");
        }
    }

    private boolean isValidInteger(Object object) {
        if (object == null || !object.getClass().equals(Integer.class)) {
            return false;
        }
        return true; 
    }
}
