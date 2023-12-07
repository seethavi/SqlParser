package com.paras.plantuml;

public class UmlClassDiagram extends UmlDiagram {

    public UmlClassDiagram defineClass(UmlClass clazz) {
        getBuilder()
            .append(clazz.toUml())
            .append('\n');
        return this;
    }

    
    public UmlClassDiagram defineRelation(UmlRelation rel) {
        getBuilder()
            .append(rel.toUml())
            .append('\n');
        return this;
    }
    
    public UmlClassDiagram definePackage(UmlPackage pkg) {
        getBuilder()
            .append(pkg.toUml())
            .append('\n');
        return this;
    }
}
