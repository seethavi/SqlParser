package com.paras.plantuml;

import java.util.StringTokenizer;
import static com.paras.util.Assertions.assertIteratorEquals;
import static com.paras.util.Assertions.assertTokensEqual;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RelationTest {

    @BeforeAll
    static void initAll() {
    }

    @BeforeEach
    void init() {
    }

    @AfterEach
    void tearDown() {
    }

    @AfterAll
    static void tearDownAll() {
    }

    @Test
    void isValidRelation_One_to_Many_Association_Test() {
        String expected = """
                ClassA \"1\" -- \"n\" ClassB
                """;

        UmlRelation relation = new UmlRelation();
        relation
            .source("ClassA")
            .target("ClassB")
            .sourceCardinality("1")
            .targetCardinality("n")
            .type(UmlRelation.Type.ASSOCIATES);

        assertTokensEqual(expected, relation.toString());
    }

    @Test
    void defineRelation_ClassA_ClassB_One_to_Many_Association_Test() {
        String expected = """
                @startUml
                class ClassA 
                {
                    int methodA()
                }

                class ClassB
                {
                    void methodB()
                }

                ClassA \"1\" -- \"n\" ClassB
                @endUml
                """;

        UmlPlant plant = new UmlPlant();
        plant
        .createClassDiagram()
            .defineClass(
                new UmlClass("ClassA")
                    .defineMethod("methodA", "int"))
            .defineClass(
                new UmlClass("ClassB")
                    .defineMethod("methodB", "void"))
            .defineRelation(
                new UmlRelation()
                    .source("ClassA")
                    .target("ClassB")
                    .type(UmlRelation.Type.ASSOCIATES)
                    .sourceCardinality("1")
                    .targetCardinality("n")
            );

        assertIteratorEquals(new StringTokenizer(expected).asIterator(), new StringTokenizer(plant.toUml()).asIterator());
        
    }

    @Test
    void defineRelation_ClassA_ClassB_Association_Many_to_Many_Test() {
        String expected = """
                @startUml
                class ClassA 
                {
                    int methodA()
                }

                class ClassB
                {
                    void methodB()
                }

                ClassA \"n\" -- \"n\" ClassB
                @endUml
                """;

        UmlPlant plant = new UmlPlant();
        plant
        .createClassDiagram()
            .defineClass(
                new UmlClass("ClassA")
                    .defineMethod("methodA", "int"))
            .defineClass(
                new UmlClass("ClassB")
                    .defineMethod("methodB", "void"))
            .defineRelation(
                new UmlRelation()
                    .source("ClassA")
                    .target("ClassB")
                    .type(UmlRelation.Type.ASSOCIATES)
                    .sourceCardinality("n")
                    .targetCardinality("n")
            );

        assertIteratorEquals(new StringTokenizer(expected).asIterator(), new StringTokenizer(plant.toUml()).asIterator());
        
    }

    @Test
    void defineRelation_ClassA_ClassB_Aggregation_One_Many_Test() {
        String expected = """
                @startUml
                class ClassA 
                {
                    int methodA()
                }

                class ClassB
                {
                    void methodB()
                }

                ClassA \"1\" o-- \"n\" ClassB
                @endUml
                """;

        UmlPlant plant = new UmlPlant();
        plant
        .createClassDiagram()
            .defineClass(
                new UmlClass("ClassA")
                    .defineMethod("methodA", "int")
            )
            .defineClass(
                new UmlClass("ClassB")
                    .defineMethod("methodB", "void")
            )                    
            .defineRelation(
                new UmlRelation()
                    .source("ClassA")
                    .target("ClassB")
                    .type(UmlRelation.Type.AGGREGATES)
                    .sourceCardinality("1")
                    .targetCardinality("n")
            );

        assertIteratorEquals(new StringTokenizer(expected).asIterator(), new StringTokenizer(plant.toUml()).asIterator());
        
    }
    
}
