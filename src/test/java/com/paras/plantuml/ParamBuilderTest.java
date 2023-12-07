package com.paras.plantuml;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ParamBuilderTest {

    @BeforeAll
    static void initAll() {
    }

    @BeforeEach
    void init() {
    }

    @Test
    void paramBuilderTest() {
        String expected = """
            type1 Param1, type2 Param2, type3 Param3""";
        UmlMethodParamBuilder builder = new UmlMethodParamBuilder();
        builder
            .addParam("type1", "Param1")
            .addParam("type2", "Param2")
            .addParam("type3", "Param3");

        assertEquals(expected, builder.toString());
    }


    @Test
    @Disabled("for demonstration purposes")
    void skippedTest() {
        // not executed
    }

    @AfterEach
    void tearDown() {
    }

    @AfterAll
    static void tearDownAll() {
    }
}