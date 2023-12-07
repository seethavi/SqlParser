package com.paras.plantuml;

import static com.paras.util.Assertions.assertTokensEqual;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UmlClassTest {

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
    void defineClassifier_Test() {
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
                    .defineMethod("methodB", "void"));

        assertTokensEqual(expected, plant.toUml());
        
    }

    @Test
    void defineClassifier_With_Stereotype_Test() {
        String expected = """
                @startUml
                class ClassA <<Table>>
                {
                    int methodA()
                }

                class ClassB
                {
                    void methodB()
                }

                @endUml
                """;

        UmlPlant plant = new UmlPlant();
        plant
        .createClassDiagram()
            .defineClass(
                new UmlClass("ClassA")
                    .defineMethod("methodA", "int")
                    .defineStereotype("Table"))
            .defineClass(
                new UmlClass("ClassB")
                    .defineMethod("methodB", "void"));

        assertTokensEqual(expected, plant.toUml());
        
    }
}