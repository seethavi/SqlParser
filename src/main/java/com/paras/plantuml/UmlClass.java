package com.paras.plantuml;


import java.util.Optional;
import javax.annotation.Nullable;

import static com.paras.util.StringUtil.insertArgInString;

public class UmlClass {
    private StringBuilder builder;
    private static final String TEMPLATE = "class {0} {1}";

    public UmlClass(String name) {
        this.builder = new StringBuilder(TEMPLATE);
        insertArgInString(builder, 0, name);
        this.builder.append(" {\n").append("}\n");
    }

    private StringBuilder getBuilder() {
        return this.builder;
    }

    public UmlClass defineStereotype(String stereotype) {
        insertArgInString(getBuilder(), 1, 
            new StringBuilder().append("<<").append(stereotype).append(">>").toString());
        return this;
    }

    // inserts the fields into the nearest class definition. That is define the class an immediately define the fields and
    // methods for the class
    public UmlClass defineField(String type, String name) {
        StringBuilder fieldDefn = new StringBuilder();
        // indent field and define field of given type
        fieldDefn.append('\t').append(type).append(' ').append(name).append('\n');
        getBuilder().insert(getBuilder().lastIndexOf("}"), fieldDefn);
        return this;
    }

    public UmlClass defineMethod(String name, String ret) {
        return defineMethod(name, ret, null);
    }

    public UmlClass defineMethod(String name, String ret, @Nullable UmlMethodParamBuilder params) {
        StringBuilder methodDefn = new StringBuilder();
        // indent field and define field of given type
        methodDefn.append('\t').append(ret).append(' ').append(name).append("(");
        Optional.ofNullable(params).ifPresent(p -> methodDefn.append(p.toString()));
        methodDefn.append(")").append('\n');
        getBuilder().insert(getBuilder().lastIndexOf("}"), methodDefn);
        return this;
    }

    public UmlClass addNote(String note) {
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
