package com.paras.plantuml;

public class UmlSequenceAutoNum {
    private static final String AUTONUM = "autonumber";
    private StringBuilder builder;

    public UmlSequenceAutoNum () {
        builder = new StringBuilder();
        builder
            .append(AUTONUM)
            .append(' ');
    }

    public UmlSequenceAutoNum start(int start) {
        builder
            .append(start)
            .append(' ');
        return this;
    }

    public UmlSequenceAutoNum increment(int increment) {
        builder
            .append(increment)
            .append(' ');
        return this;
    }

    public UmlSequenceAutoNum format(String format) {
        builder
            .append(format)
            .append(' ');
        return this;
    }

    public String get() {
        return builder.toString().trim();
    }
  
}
