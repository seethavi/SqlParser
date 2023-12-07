package com.paras.parser;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class StreamBufferTest {
	

	@BeforeEach
	public void setUp() throws Exception {
	}

	@AfterEach
	public void tearDown() throws Exception {
	}

	@Test
	public void testStreamBuffer() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsEof() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsEmpty() {
		fail("Not yet implemented");
	}

	@Test
	public void testFillBuffer() throws IOException {
		String str = "hello how are you? This is a test for word boundaries";
		StreamBuffer buffer = new StreamBuffer(new StringReader(str));
		buffer.setBufferSize(33);
		buffer.setThreshold(6);
		buffer.fillBuffer();
		System.out.println(buffer.getBuffer());

	}

	@Test
	public void testGetPos() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetPos() {
		fail("Not yet implemented");
	}

	@Test
	public void testRewind() {
		fail("Not yet implemented");
	}

	@Test
	public void testNextChar() {
		fail("Not yet implemented");
	}

	@Test
	public void testCharAt() {
		fail("Not yet implemented");
	}

}
