package com.paras.ast;

import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;

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

public class DBObjectTest {

	@BeforeEach
	public void setUp() throws Exception {
	}

	@AfterEach
	public void tearDown() throws Exception {
	}

	@Test
	public void testRationaliseReferencesDBObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddAccessedTable() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindProcOrFuncWithName() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetProcWithName() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFuncWithName() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddInvocationRef() {
		SqlSource dbo = new SqlSource("dbo");
		// positive test
		dbo.addInvocationRef("proc1");
		dbo.addInvocationRef("proc2");
		dbo.addInvocationRef("proc3");
		
		//negative test - should not be able to add two elements with the same name
		dbo.addInvocationRef("proc1");
		dbo.addInvocationRef("proc1");
		//dbo.printInvocationRefs(new FileWriter(System.out), "");
		
		Set<String> strSet = new HashSet<String>();
		//assertTrue(strSet.add("proc1"));
		//assertFalse(strSet.add("proc1"));
		
	}

	@Test
	public void testRationaliseReferencesSchemaDBObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testRationaliseTables() {
		fail("Not yet implemented");
	}

	@Test
	public void testRationaliseInvocations() {
		fail("Not yet implemented");
	}

	@Test
	public void testPrintStatistics() {
		fail("Not yet implemented");
	}

	@Test
	public void testPrintTableRefs() {
		fail("Not yet implemented");
	}

	@Test
	public void testPrintInvocationRefs() {
		fail("Not yet implemented");
	}

	@Test
	public void testPrintNestings() {
		fail("Not yet implemented");
	}

	@Test
	public void testPrintBasic() {
		fail("Not yet implemented");
	}

	@Test
	public void testPrint() {
		fail("Not yet implemented");
	}

}
