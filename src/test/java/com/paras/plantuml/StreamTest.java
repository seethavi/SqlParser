package com.paras.plantuml;

import static com.paras.util.Assertions.assertListsEqual;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamTest {

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
    void stream_drop_while_test() {

        // create a stream of names
        Stream<String> stream = Stream.of("aman", "amar", "suraj", "sivam", "zara", "azad");
        List<String> expected = List.of("suraj", "sivam", "zara", "azad");

        // apply dropWhile to drop all the names
        // matches passed predicate
        List<String> actual = stream.dropWhile(name -> (name.charAt(0) == 'a'))
                .collect(Collectors.toList());

        assertListsEqual(expected, actual);
    }
}