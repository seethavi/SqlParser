/**
 * 
 */
package com.paras.parser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

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

/**
 * @author seethavi
 *
 */
public class TokeniserTest {
	
	private Tokeniser tk;
	

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.paras.parser.Tokeniser#Tokeniser(java.io.File, com.paras.io.OutputWriter)}.
	 */
	@Test
	public void testTokeniser()  {
		try {
			Tokeniser tk = new Tokeniser(new FileReader("C:\\temp\\auditautomation.sql"), 
				new FileWriter(new File("C:\\temp\\auditautomation.out.txt")));
		}
		catch(Exception e) {
			fail("Exception not expected");
		}
		
	}
	
	@Test
	public void testTokeniser2() throws Exception {
		Tokeniser tk = new Tokeniser(new FileReader("C:\\temp\\wrongfile.sql"), 
				new FileWriter(new File("C:\\temp\\auditautomation.out.txt")));
	}

	/**
	 * Test method for {@link com.paras.parser.Tokeniser#lookAhead()}.
	 */
	@Test
	public void testLookAhead() {
		try {
			String str = "token1 token2 token3";
			Writer writer = new StringWriter();
			Tokeniser tk = new Tokeniser(new StringReader(str), 
					writer);
			tk.setLookAheadLimit(2);
			Token la = tk.lookAhead(1);
			assertEquals(la.getStrVal(), "token1");
			la = tk.lookAhead(2);
			assertEquals(la.getStrVal(), "token2");
			System.out.println(writer.toString());
		}
		catch(Exception e) {
			fail("Exception not expected");
		}
	}

	/**
	 * Test method for {@link com.paras.parser.Tokeniser#nextToken()}.
	 */
	@Test
	public void testNextToken() {
		try {
			String str = "token1:token2, token3() ";
			Writer writer = new StringWriter();
			Tokeniser tk = new Tokeniser(new StringReader(str), 
					writer);
			Token tkn = tk.nextToken();
			//assertEquals(tkn.getVal(), "token1");
			System.out.println(writer.toString());
			tkn = tk.nextToken();
			//assertEquals(tkn.getVal(), ":");
			System.out.println(writer.toString());
			tkn = tk.nextToken();
			System.out.println(tkn.getStrVal());
			//assertEquals(tkn.getVal(), "token2");
			
			tkn = tk.nextToken();
			System.out.println(tkn.getStrVal());
			//assertEquals(tkn.getVal(), ",");
			tkn = tk.nextToken();
			System.out.println(tkn.getStrVal());
			//assertEquals(tkn.getVal(), "token3");
			tkn = tk.nextToken();
			System.out.println(tkn.getStrVal());
			//assertEquals(tkn.getVal(), "(");
			tkn = tk.nextToken();
			System.out.println(tkn.getStrVal());
			//assertEquals(tkn.getVal(), ")");
		}
		catch(Exception e) {
			fail("Exception not expected");
		}
	}

	/**
	 * Test method for {@link com.paras.parser.Tokeniser#hasMoreTokens()}.
	 */
	@Test
	public void testHasMoreTokens() {
		try {
			String str = "token1 token2 token3";
			Writer writer = new StringWriter();
			Tokeniser tk = new Tokeniser(new StringReader(str), 
					writer);
			assertTrue(tk.hasMoreTokens());
			Token tkn = tk.nextToken();
			assertTrue(tk.hasMoreTokens());
			tkn = tk.nextToken();
			assertTrue(tk.hasMoreTokens());
			tkn = tk.nextToken();
			assertFalse(tk.hasMoreTokens());
		}
		catch(Exception e) {
			fail("Exception not expected");
		}
	}

	/**
	 * Test method for {@link com.paras.parser.Tokeniser#tokenise()}.
	 */
	@Test
	public void testTokenise() {
		try {
			String str = "CREATE OR REPLACE PACKAGE BODY MDBOWN.SDVUTIL is" +
			 "/*******************************************************************************" +
			  "* PURPOSE:" + 
			  "* Procedures for SDV processing." +
			  "*" +
			  "*/" +
			  
"PROCEDURE	AddToMsgTable (" +
		  "iModuleName	in varchar2" +
		", iMsgCode		in varchar2" +
		", iMsgText		in varchar2" +
		", iMsgType		in varchar2 default kMsgAdvice" +
	") is" +

		"msgRowCount		binary_integer;" +
	"BEGIN" +

		"--	Add the described message to the message table.\n" +

		"msgRowCount	:= NVL(gMsgTable.COUNT,0) + 1;" +

		"gMsgTable(msgRowCount).moduleName	:= SUBSTR(iModuleName,1,100);" +
		"gMsgTable(msgRowCount).msgCode		:= SUBSTR(iMsgCode,1,20);" +
		"gMsgTable(msgRowCount).msgText		:= SUBSTR(iMsgText,1,256);" +
		"gMsgTable(msgRowCount).msgType		:= SUBSTR(iMsgType,1,4);" +

	"END AddToMsgTable;";
			
			Writer writer = new StringWriter();
			Tokeniser tk = new Tokeniser(new StringReader(str), 
					writer);
			while(tk.hasMoreTokens()) {
				System.out.println(tk.nextToken());
			}
			/*
			Token tkn = tk.nextToken();
			assertEquals(tkn.getVal(), "CREATE");
			tkn = tk.nextToken();
			tkn = tk.nextToken();
			assertEquals(tkn.getVal(), "REPLACE"); */
		}
		catch(Exception e) {
			fail("Exception not expected");
		}
	}

}
