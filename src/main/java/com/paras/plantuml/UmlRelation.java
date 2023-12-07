package com.paras.plantuml;

import static com.paras.util.StringUtil.insertArgInString;

public class UmlRelation {
    
    public enum Cardinality {
        ONE_ONE("--"),
        ONE_MANY( "\"1\" -- \"n\""),
        MANY_ONE( "\"n\" -- \"1\""),
        MANY_MANY( "\"n\" -- \"n\"")
        ;
    
        private String value;
    
        private Cardinality(String value) {
            this.value = value;
        }
    
        public String getValue() {
            return this.value;
        } 
    }

    public enum Type {
        AGGREGATES("o--"),
        COMPOSES("*--"),
        ASSOCIATES("--"),
        REALISES("..>"),
        EXTENDS("--|>");

        private String value;

        Type(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }
    private StringBuilder builder;
    private static final String TEMPLATE = "{0} {1} {2} {3} {4}";

    public UmlRelation() {
        this.builder = new StringBuilder(TEMPLATE);
    }

    public UmlRelation(String source, String target, Type type) {
        this.builder = new StringBuilder(TEMPLATE);
        this.source(source)
            .target(target)
            .type(type);
    }

    private StringBuilder getBuilder() {
        return this.builder;
    }

    public UmlRelation source(String source) {
        insertArg(0, source);
        return this;
    }

    public UmlRelation target(String target) {
        insertArg(4, target);
        return this;
    }

    public UmlRelation type(Type type) {
        insertArg(2, type.getValue());
        return this;
    }

    public UmlRelation name(String name) {
        getBuilder().append(" : "). append(name);
        return this;
    }

    public UmlRelation sourceCardinality(String cardinality) {
        insertArg(1, new StringBuilder().append('"').append(cardinality).append('"').toString());
        return this;
    }

    public UmlRelation targetCardinality(String cardinality) {
        insertArg(3, new StringBuilder().append('"').append(cardinality).append('"').toString());
        return this;
    }

    // validates the uml generated
    public boolean isValidUml() {
        // return false if the template is not fully replaced with args
        return getBuilder().lastIndexOf("{") < 0;
    }

    // alternate method to toString and is used in the UML generation context
    public String toUml() {
        return toString();
    }

    public String toString() {
        insertArg(1, "");
        insertArg(3, "");
        return getBuilder().toString();
    }

    private void insertArg(int index, String arg) {
        insertArgInString(getBuilder(), index, arg);
    }
    
}
