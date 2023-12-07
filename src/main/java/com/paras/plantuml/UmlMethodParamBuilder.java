package com.paras.plantuml;

public class UmlMethodParamBuilder {
    private StringBuilder builder;

    public UmlMethodParamBuilder() {
        builder = new StringBuilder();
    }

    public UmlMethodParamBuilder addParam(String type, String name) {
        if(builder.length() > 0) { // other parameters exist and this should be appended to the list by prepending a ,
            builder.append(", ").append(type).append(' ').append(name);
        }
        else {
            builder.append(type).append(' ').append(name);
        }
        return this;
    }

    public String toString() {
        return builder.toString();
    }
    
}
