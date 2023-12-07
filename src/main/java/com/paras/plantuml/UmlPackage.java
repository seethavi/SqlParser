package com.paras.plantuml;

import static com.paras.util.StringUtil.insertArgInString;

public class UmlPackage {
    private StringBuilder builder;
    private static final String TEMPLATE = "package {0} {1}";

    public UmlPackage(String name) {
        this.builder = new StringBuilder(TEMPLATE);
        insertArgInString(builder, 0, name);
        this.builder.append(" {\n").append("}\n");
    }

    private StringBuilder getBuilder() {
        return this.builder;
    }

    public  UmlPackage defineClass(UmlClass clazz) {
        getBuilder().insert(getBuilder().lastIndexOf("}"), clazz.toUml());
        return this;
    }

    public UmlPackage defineType(UmlType type) {
        insertArgInString(getBuilder(), 1, 
            new StringBuilder().append("<<").append(type.getValue()).append(">>").toString());
        return this;
    }

    public UmlPackage addNote(String note) {
        getBuilder().append("note: ").append(note).append('\n');
        return this;
    }


    public String toString() {
        // If no stereotype has been set, then replace it with empty string
        insertArgInString(getBuilder(), 1, ""); 
        return getBuilder().toString();
    }

    public String toUml() {
        return toString();
    }
    
}
