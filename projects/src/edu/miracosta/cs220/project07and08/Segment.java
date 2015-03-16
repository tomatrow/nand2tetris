package edu.miracosta.cs220.project07and08;

import java.util.HashMap;
import java.util.Map;

public enum Segment {
    ARGUMENT, LOCAL, STATIC, CONSTANT, THIS, THAT, POINTER, TEMP;

     public static final Map<String, Segment> symbolMap = new HashMap<String, Segment>() {{
        put("argument", ARGUMENT);
        put("local", LOCAL);
        put("static", STATIC);
        put("constant", CONSTANT);
        put("this", THIS);
        put("that", THAT);
        put("pointer", POINTER);
        put("temp", TEMP);
    }};

    /*
    Special:
        SP CONSTANT √
        STATIC √
    Pointers:
        LCL LOCAL
        ARG ARGUMENT
        THIS THIS
        THAT THAT
    Real: 
        3-4 POINTER √
        5-12 TEMP √
    */

    public boolean isSpecial() {
        switch (this) {
            case CONSTANT:
            case STATIC:
                return true;
            default:
                return false;
        }
    }

    public boolean isPointer() {
        switch (this) {
            case ARGUMENT:
            case LOCAL:
            case THIS:
            case THAT:
                return true;
            default:
                return false;
        }
    }

    public boolean isReal() {
        switch (this) {
            case POINTER:
            case TEMP:
                return true;
            default:
                return false;
        }
    }

    public int baseAddress() {
        switch (this) {
            case POINTER:
                return 3;
            case TEMP:
                return 5;
            default:
                throw new IllegalStateException();
        }
    }


}
