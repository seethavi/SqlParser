package com.paras.io;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.paras.ast.Package;
import com.paras.ast.Constraint;
import com.paras.ast.Function;
import com.paras.ast.Parameter;
import com.paras.ast.Procedure;
import com.paras.ast.Schema;
import com.paras.ast.SqlSource;
import com.paras.ast.Table;
import com.paras.plantuml.UmlPlant;
import com.paras.plantuml.UmlRelation;
import com.paras.plantuml.UmlClassDiagram;
import com.paras.plantuml.UmlMethodParamBuilder;
import com.paras.plantuml.UmlPackage;
import com.paras.plantuml.UmlClass;

/**
 * The UmlModelBuilder class is responsible for building the model in PlantUml
 * from the
 * Abstract Syntax Tree representation of the parsed output. This class provides
 * methods that traverse the object graph corresponding to the parsed output and
 * create the Uml model elements. It also establishes the relationships between
 * the model elements.
 * 
 * @See Package
 * @See Column
 * @See Container
 * @See Function
 * @See Procedure
 * @See Schema
 * @See Table
 * @See View
 * @See Trigger
 * @See Constraint
 * 
 * @author seethavi
 */
public class UmlModelBuilder {

    private static Logger logger = Logger.getLogger("co.nz.transpower.io.UmlModelBuilder");
    private static final String RE = "Reverse engineered on %t";

    private UmlPlant plant;
    private UmlClassDiagram classDiagram;

    public UmlModelBuilder() {
        this.plant = new UmlPlant();
        this.classDiagram = getPlant().createClassDiagram();
    }

    public String toUml() {
        return getPlant().toUml();
    }

    private UmlPlant getPlant() {
        return this.plant;
    }

    private UmlClassDiagram getClassDiagram() {
        return this.classDiagram;
    }

    /**
     * Build the model for the given schema object. The schema object is a
     * collection
     * of all parsed elements from the input source files.
     * 
     * @param schema Schema object containing all the parsed representations of the
     *               input source
     */
    public void buildModel(Schema schema) {
        java.util.Collection<SqlSource> dbObjects = schema.getDBObjects();
        int size = schema.getDBObjects().size();

        for (SqlSource dbo : dbObjects) {
            long start = System.currentTimeMillis();
            if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "Building model elements. Processing file {0} of {1}==>{2}",
                        new Object[] { dbo.getSourceFileName(), size, dbo.getSourceFile() });
            }
            buildModel(schema, dbo);
            long end = System.currentTimeMillis();
            if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, " in {0}s", (end - start) / 1000);
            }
        }
    }

    public void buildReferences(Schema schema) {
        java.util.Collection<SqlSource> dbObjects = schema.getDBObjects();
        int size = schema.getDBObjects().size();

        for (SqlSource dbo : dbObjects) {
            long start = System.currentTimeMillis();
            if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "Building model references. Processing file {0} of {1}==>{2}",
                        new Object[] { dbo.getSourceFileName(), size, dbo.getSourceFile() });
            }
            try {
                buildReferences(schema, dbo);
            } catch (Exception e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, " in {0}s", (end - start) / 1000);
            }

        }
    }

    public void buildModel(Schema schema, SqlSource dbo) {
        dbo.getPackages().stream()
                .forEach(pkg -> {
                     // resolve any reference types, so uml methods can be created with the right parameter types
                    pkg.getProcedures().stream()
                        .forEach(proc -> proc.getParameterList().stream().forEach(param -> param.resolveReference(schema)));
                    pkg.getFunctions().stream()
                        .forEach(func -> func.getParameterList().stream().forEach(param -> param.resolveReference(schema)));
                    createPackage(pkg);
                });

        dbo.getTriggers().stream().forEach(trigger -> getClassDiagram()
                .defineClass(new UmlClass(trigger.getName())
                        .defineStereotype("trigger")));
        dbo.getTables().stream().forEach(table -> {
            UmlClass c = new UmlClass(table.getName())
                    .defineStereotype("table");

            table.getColumns().stream().forEach(column -> c.defineField(column.getName(), column.getType()));

            getClassDiagram()
                    .defineClass(c);

        });
        dbo.getViews().stream().forEach(view -> getClassDiagram()
                .defineClass(new UmlClass(view.getName())
                        .defineStereotype("view")));
    }

    /**
     * Builds the model in EA
     * 
     * @param builder  The ModelBuilder that builds the model in EA
     * @param plSqlPkg The UML package within EA to root this package in
     */
    public void createPackage(Package thePackage) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Processing PL/SQL Package: {0}\n", thePackage.getName());
        }
        UmlPackage pkg = new UmlPackage(thePackage.getSourceFileQN()); // find or create the root package
        UmlClass uclass = new UmlClass(thePackage.getName());

        Collection<Procedure> procs = thePackage.getProcedures();
        procs.stream().forEach(proc -> {
            List<Parameter> params = proc.getParameterList();
            UmlMethodParamBuilder builder = new UmlMethodParamBuilder();
            params.iterator().forEachRemaining(param -> builder.addParam(param.getName(), param.getType()));
            uclass.defineMethod(proc.getName(), "void", builder);
        });

        Collection<Function> funcs = thePackage.getFunctions();
        funcs.stream().forEach(func -> {
            List<Parameter> params = func.getParameterList();
            UmlMethodParamBuilder builder = new UmlMethodParamBuilder();
            params.iterator().forEachRemaining(param -> builder.addParam(param.getName(), param.getType()));
            uclass.defineMethod(func.getName(), func.getReturnType(), builder);
        });

        pkg.defineClass(uclass);
        getClassDiagram().definePackage(pkg);
    }

    /**
     * Creates the model references for the abstract syntax tree representation of
     * the
     * parsed input.
     * 
     * @param dbo The sql source containing db objects
     */
    public void buildReferences(Schema schema, SqlSource dbo) {
        // buildReferences((Container) dbo); // build references for procedures /
        // functions defined in the current sql source
        dbo.getPackages().stream().forEach(pkg -> {
            pkg.getPackageRefs().stream().forEach(ref -> getClassDiagram()
                    .defineRelation(
                            new UmlRelation(pkg.getSourceFileQN(), ref.getSourceFileQN(),
                                    UmlRelation.Type.ASSOCIATES)));
            pkg.getProcedures().stream().forEach(proc ->
                proc.getTableAccessRefs().stream().forEach(tableAccess -> 
                    getClassDiagram().defineRelation(
                        new UmlRelation(pkg.getName(), tableAccess.getTableName(), 
                            UmlRelation.Type.ASSOCIATES).name(tableAccess.getAccessTypeAsString())
                    )
                )
            );
        });

        dbo.getTriggers().stream().forEach(trigger -> getClassDiagram()
                .defineRelation(
                        new UmlRelation()
                                .source(trigger.getName())
                                .target(trigger.getSourceTable().getName())
                                .type(UmlRelation.Type.ASSOCIATES)));

        dbo.getConstraints().stream().forEach(this::buildReference);

    }

    /**
     * Build the constraints and the references created by them in the model
     * 
     * @param c Constraint object to build
     */
    public void buildReference(Constraint c) {
        switch (c.getConstraintType()) {
            case FK: {
                Optional<Table> sourceTable = c.getSourceTable();
                sourceTable.ifPresent(table -> {
                    UmlRelation rel = new UmlRelation()
                            .target(table.getName())
                            .source(c.getName())
                            .type(UmlRelation.Type.ASSOCIATES)
                            .name("foreign-key");
                    getClassDiagram()
                            .defineClass(new UmlClass(c.getName()))
                            .defineRelation(rel);

                });
            }
                break;

            case PK: {
                Optional<Table> sourceTable = c.getSourceTable();
                sourceTable.ifPresent(table -> {
                    UmlRelation rel = new UmlRelation()
                            .target(table.getName())
                            .source(c.getName())
                            .type(UmlRelation.Type.ASSOCIATES)
                            .name("primary-key");
                    getClassDiagram()
                            .defineClass(new UmlClass(c.getName()))
                            .defineRelation(rel);
                });
            }
                break;


            default:
                break;
        }

    }
}