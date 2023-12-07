package com.paras.util;

public class StringUtil {

    private StringUtil() {}

        // method  to insert the arguments in the template incrementally. MessageFormat class is an alternative option
    // but this implementation is fairly lightweight and relies on StringBuilder replace method
    public static void insertArgInString(StringBuilder builder, int index, String arg) {
        int argIndex = 
            builder
                .lastIndexOf(
                    new StringBuilder()
                        .append("{")
                        .append(index)
                        .append("}")
                        .toString()
                );
        if (argIndex >= 0) {
            builder.replace(argIndex, argIndex + 3, arg);
        }
    }
    
}
