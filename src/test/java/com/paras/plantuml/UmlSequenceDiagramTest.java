package com.paras.plantuml;

import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class UmlSequenceDiagramTest {

    @BeforeAll
    static void initAll() {
    }

    @BeforeEach
    void init() {
    }

    @Test
    void autoNumTest() {
        String expected = """
            @startUml
            actor Bob as Bob
            actor Loa as Loa
            autonumber 10 #red
            Bob -> Loa : Hello there
            @endUml""";
        
        UmlPlant plant = new UmlPlant();
        UmlSequenceAutoNum auto = new UmlSequenceAutoNum();

        auto.start(10);
        auto.format("#red");

        plant
            .createSequenceDiagram()
            .declareType(UmlType.ACTOR, "Bob").as("Bob")
            .declareType(UmlType.ACTOR, "Loa").as("Loa")
            .autoNumber(auto)
            .message("Bob", "Loa", "Hello there");

        assertEquals(expected, plant.toUml());
    }

    @Test
    void messageTest() {
        String expected = """
            @startUml
            actor Bob as Bob
            actor Loa as Loa
            Bob -> Loa : Hello there
            @endUml""";
        UmlPlant plant = new UmlPlant();

        plant
            .createSequenceDiagram()
            .declareType(UmlType.ACTOR, "Bob").as("Bob")
            .declareType(UmlType.ACTOR, "Loa").as("Loa")
            .message("Bob", "Loa", "Hello there");

        assertEquals(plant.toUml(), expected);
    }

    @Test
    void failingTest() {
        //fail("a failing test");
    }

    @Test
    @Disabled("for demonstration purposes")
    void skippedTest() {
        // not executed
    }

    @Test
    void abortedTest() {
        assumeTrue("abc".contains("Z"));
       // fail("test should have been aborted");
    }

    @AfterEach
    void tearDown() {
    }

    @AfterAll
    static void tearDownAll() {
    }

}