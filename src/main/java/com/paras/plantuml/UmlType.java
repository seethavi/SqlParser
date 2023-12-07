package com.paras.plantuml;

public enum UmlType {
    ACTOR("actor"),
    BOUNDARY("boundary"),
    CONTRL("control"),
    DATABASE("database"),
    ENTITY("entity"),
    COLLECTIONS("collections"),
    QUEUE("queue"),
    NODE("node"),
    RECTANGLE("rectangle"),
    FOLDER("folder"),
    FRAME("frame")

    ;

    private String value;

    private UmlType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}