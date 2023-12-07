package com.paras.plantuml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.paras.util.Assertions.assertIteratorEquals;
import java.util.StringTokenizer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UmlClassDiagramTest {

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
    void createClassDiagram_with_ClassA_Test() {
        String expected = """
                @startUml
                class ClassA {
                }
                @endUml""";

        UmlPlant plant = new UmlPlant();
        plant
            .createClassDiagram()
            .defineClass(new UmlClass("ClassA"));

        assertEquals(expected, plant.toUml());
        
    }

    @Test
    void createClassDiagram_with_ClassA_and_FieldA_Test() {
        String expected = """
                @startUml
                class ClassA 
                {
                    int fieldA
                }
                @endUml
                """;

        UmlPlant plant = new UmlPlant();
        plant
        .createClassDiagram()
            .defineClass(
                new UmlClass("ClassA")
                    .defineField("int", "fieldA"));

        assertIteratorEquals(new StringTokenizer(expected).asIterator(), new StringTokenizer(plant.toUml()).asIterator());
        
    }

    @Test
    void createClassDiagram_with_ClassA_and_MethodA_Test() {
        String expected = """
                @startUml
                class ClassA 
                {
                    int methodA()
                }
                @endUml
                """;

        UmlPlant plant = new UmlPlant();
        plant
        .createClassDiagram()
            .defineClass(
                new UmlClass("ClassA")
                    .defineMethod("methodA", "int"));

        assertIteratorEquals(new StringTokenizer(expected).asIterator(), new StringTokenizer(plant.toUml()).asIterator());
        
    }

}