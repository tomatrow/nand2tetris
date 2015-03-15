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
}
