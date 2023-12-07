package com.paras.util;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Assertions {
    public static void assertIteratorEquals(Iterator<?> expected, Iterator<?> actual) {
        while(expected.hasNext() && actual.hasNext())
            assertEquals(expected.next(), actual.next());
        assert !expected.hasNext() && !actual.hasNext();
    }

    public static void assertTokensEqual(String expected, String actual) {
        assertIteratorEquals(
            new StringTokenizer(expected).asIterator(),
            new StringTokenizer(actual).asIterator()
        );
    }

    public static void assertListsEqual(List<?> expected, List<?> actual) {
        assertIteratorEquals(expected.iterator(), actual.iterator());
    }
    
}
