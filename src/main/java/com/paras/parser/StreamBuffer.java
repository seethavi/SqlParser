package com.paras.parser;

import java.io.IOException;
import java.io.Reader;

/**
 * This class provides the ability to read from an input source and buffer the
 * input. This provides other features over buffered reader such as the ability
 * to rewind the stream position by many characters. This feature is not provided
 * by the standard BufferedInputStream, hence a custom implementation had to be
 * created.
 * 
 * @author seethavi
 */

public class StreamBuffer {

    /**
     * Static for the buffer size
     */
    public final static int BUF_SIZE = 1024;
    /**
     * Static to control the alignment of characters read at word boundaries
     */
    public final static int THRESHOLD = 50;
    /**
     * current size of the buffer
     */
    private int bufSize;
    /**
     * current value of the threshold. This can be controlled
     */
    private int threshold;
    /**
     * The input stream to read characters from
     */
    private Reader is;
    /**
     * String buffer to read characters into
     */
    private StringBuffer buffer;
    /**
     * Current character position within buffer to read character from
     */
    private int pos;
    /**
     * Boolean indicating end of file
     */
    private boolean eof;

    /**
     * Default constructor initialising the input stream
     * @param is Input stream to read characters from
     */
    public StreamBuffer(Reader is) {
        this.is = is;
        buffer = null;
        pos = 0;
        eof = false;
        bufSize = BUF_SIZE;
        threshold = THRESHOLD;
    }

    /**
     * Setter for buffer size
     * @param size 
     */
    public void setBufferSize(int size) {
        bufSize = size;
    }
    
    /**
     * Setter for threshold
     * @param size 
     */

    public void setThreshold(int size) {
        threshold = size;
    }
    
    /** 
     * Getter for buffer
     * @return The buffer containing the currently read characters
     */

    public StringBuffer getBuffer() {
        return buffer;
    }
    
    /**
     * Checks if EOF has been reached
     * @return true if eof is reached, false otherwise
     */

    public boolean isEof() {
        return eof;
    }
    
    /**
     * Checks if buffer is empty
     * @return true if empty, false otherwise
     */

    public boolean isEmpty() {
        return buffer == null || pos >= buffer.length();
    }

    /**
     * Fills the buffer, when the pre-read characters have already been consumed
     * @return a Buffer containing characters read from the input stream
     * @throws IOException When the stream cannot be read correctly
     */
    StringBuffer fillBuffer() throws IOException {
        char[] cbuf = new char[bufSize];
        buffer = new StringBuffer();
        int i = 0;
        boolean done = false;
        while (!done) {
            int ret = is.read();
            if (ret == -1) {
                eof = true;
                done = true;
            }
            buffer.append((char) ret);
            if (cbuf.length - i < threshold) {//try and align word boundaries in the buffer
                char c = (char) ret;
                if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                    done = true;
                }
            }
            i++;
        }
        pos = 0;
        return buffer;
    }

    /**
     * Getter for position
     * @return 
     */
    public int getPos() {
        return pos;
    }
    
    /**
     * Setter for position
     * @param index 
     */

    public void setPos(int index) {
        pos = index;
    }
    
    /**
     * Rewind the buffer position so that characters can be re-read from the
     * stream buffer
     * @param numChars Number of characters to rewind the stream position by
     */

    public void rewind(int numChars) {
        pos = pos - numChars;
    }

    /**
     * Get the next character from the stream
     * @return next character
     * @throws IOException When the stream cannot be read
     */
    public char nextChar() throws IOException {
        return (char) charAt(pos++);
    }

    /**
     * Get the character at a given position in the stream
     * @param index position to read the charaacter from
     * @return Character at a given position in the input stream
     * @throws IOException When the input stream cannot be read
     * @throws IndexOutOfBoundsException  When the index is outside of bounds
     */
    public int charAt(int index) throws IOException, IndexOutOfBoundsException {
        if (buffer == null) {
            fillBuffer(); // character buffer into which characters are read into from the file stream
        }
        if (index < buffer.length()) {
            return buffer.charAt(index);
        } else {
            throw new IndexOutOfBoundsException("Index out of bound when accessing streambuffer: Accessed="
                    + index + " buffer length=" + buffer.length());
        }
    }
}
