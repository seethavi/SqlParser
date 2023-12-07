package com.paras.io;

import java.io.IOException;
import java.io.Writer;

/**
 * This is a utility class that provides some convenience methods to writeln to 
 * an IO Writer object
 * 
 * @See Writer
 * @author seethavi
 */
public class OutputWriter {

    /**
     * The output writer object to writeln output to
     */
    private Writer out;

    /**
     * Default constructor
     * @param writer Writer object to writeln output to
    
     */
    public OutputWriter(Writer writer) {
        out = writer;
    }

    /**
     * Getter
     * @return Writer object 
     */
    public Writer getWriter() {
        return out;
    }

    /**
     * Writes a string output
     * @param output String to be written
     * @throws IOException When output cannot be written
     */
    public OutputWriter writeln(String output) throws IOException {
        out.write(output == null ? "" : output);
        out.write('\n');
        out.flush();
        return this;
    }
    
    public OutputWriter write(String output) throws IOException {
        out.write(output == null ? "" : output);
        out.flush();
        return this;
    }

    /**
     * Writes a character output
     * @param c Character to be written
     * @throws IOException When output cannot be written
     */
    public OutputWriter write(char c) throws IOException {
        out.write(c);
        out.flush();
        return this;
    }
    
    public void close() throws IOException {
        out.flush();
        out.close();
    }
}
