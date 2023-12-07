package com.paras.parser;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

import com.paras.io.DirectoryScanner;

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

public class DirectoryScannerTest {

	@BeforeEach
	public void setUp() throws Exception {
	}

	@AfterEach
	public void tearDown() throws Exception {
	}

	@Test
	public void testScan() {
		DirectoryScanner scanner = new DirectoryScanner("C:\\temp\\MDB");
		Set<File> inputSet = scanner.scan();
		assertFalse(inputSet.isEmpty());
		Iterator<File> it = inputSet.iterator();
		while(it.hasNext()) {
			File f = it.next();
			//System.out.println(f.getAbsolutePath());
		}
		
	}
	
	@Test
	public void testScanFilter() {
		DirectoryScanner scanner = new DirectoryScanner("C:\\temp\\MDB");
		scanner.setIncludeFilterSet(new String[] {".*moi\\\\.*pkb"});
		Set<File> inputSet = scanner.scan();
		assertFalse(inputSet.isEmpty());
		Iterator<File> it = inputSet.iterator();
		while(it.hasNext()) {
			File f = it.next();
			System.out.println(f.getAbsolutePath());
		}
		
	}

}
