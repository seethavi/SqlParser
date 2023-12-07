package com.paras.parser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ParserTest {

    @Test
    public void testParse_CaseInProcArg() throws Exception {
        String fileName = "C:\\Users\\seethavi\\Documents\\NetBeansProjects\\RE\\test\\TestData\\CaseInProcArg.pkb";
        File file = new File(fileName);
        Parser parser = new Parser(file, 
                "C:\\Users\\seethavi\\Documents\\NetBeansProjects\\RE\\resources", 
                "C:\\Temp\\Output");
        
        try {
            parser.parse();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception should not occur");
        }
        Writer fw = new FileWriter(fileName + ".xml");
        parser.getParseTree().write(fw, "");
    }
}
