package com.paras.plantuml;

public abstract class UmlDiagram {
    private StringBuilder builder;

    protected UmlDiagram() {
        this.builder = new StringBuilder();
    }

    protected StringBuilder getBuilder() {
        return this.builder;
    }
    
    public String toUml() {
        return getBuilder().toString();
    }
}
