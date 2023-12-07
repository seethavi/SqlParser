package com.paras.plantuml;

public class UmlSequenceDiagram extends UmlDiagram {

    public UmlSequenceDiagram declareType(UmlType type, String name) {
        getBuilder().append(type.getValue()).append(' ').append(name);
        return this;
    }

    public UmlSequenceDiagram as(String value) {
        getBuilder().append(" as ").append(value).append('\n');
        return this;
    }

    public UmlSequenceDiagram message(String src, String dst, String name) {
        getBuilder()
            .append(src)
            .append(" -> ")
            .append(dst)
            .append(" : ")
            .append(name)
            .append('\n');
        return this;
    }

    public UmlSequenceDiagram autoNumber(UmlSequenceAutoNum auto) {
        getBuilder().append(auto.get()).append('\n');
        return this;
    }
}