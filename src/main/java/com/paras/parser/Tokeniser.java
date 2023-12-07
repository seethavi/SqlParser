/**
 * 
 */
package com.paras.parser;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.paras.io.OutputWriter;
import com.paras.parser.Token.TokenType;

/**
 * The Tokeniser converts the character input stream into meaningful tokens that
 * the parser can
 * understand and work with. This class encapsulates functionality that deals
 * with the complex
 * input handling
 * 
 * 
 * @see Parser
 * @see Token
 * @see StreamBuffer
 * 
 * @author seethavi
 * @since PLSQLRE v1.0
 */
public class Tokeniser {

	/**
	 * Regular expression for identifiers.
	 */
	private static final String ID_REGEX = "[a-zA-Z_][a-zA-Z0-9_$]*";

	/**
	 * Input stream from which the tokens will be identified
	 */
	private Reader is;
	/**
	 * Tokeniser output are written to this file
	 */
	private OutputWriter os;
	/**
	 * Maps string tokens to value tokens
	 */
	private Map<String, TokenType> tokenMap;
	/**
	 * Look-ahead buffer for storing prefetched tokens for future processing
	 */
	private List<Token> lookAheadBuf;
	/**
	 * Maximum number of prefetched tokens allowed
	 */
	private int lookAheadMax;
	/**
	 * Buffer that handles the reading of characters from a character stream
	 */
	private StreamBuffer streamBuf;
	/**
	 * Saves the current token being accessed
	 */
	private Token currentToken;
	/**
	 * A regular expression parser
	 */
	private Pattern idPattern;

	private boolean verbose;

	/**
	 * Creates a new Tokeniser and initialises the input and output streams
	 * 
	 * @param is The input stream from which characters are to be read
	 * @param os The output stream to send output to
	 * @throws Exception If either of the streams cannot be accessed then an
	 *                   IOException will be thrown
	 */

	public Tokeniser(Reader is, Writer os) throws Exception {
		this.is = is;
		this.os = new OutputWriter(os);
		initTokenMap();
		lookAheadMax = 1;
		lookAheadBuf = new ArrayList<Token>();
		streamBuf = new StreamBuffer(this.is);
		idPattern = Pattern.compile(ID_REGEX);
		this.verbose = true;
	}

	public void setVerbose(boolean val) {
		this.verbose = val;
	}

	/**
	 * Set the maximum value for te lookahead / prefetch
	 * 
	 * @param num Maximum prefetch value
	 */
	public void setLookAheadLimit(int num) {
		lookAheadMax = num;
	}

	/**
	 * Initialise the token map showing the mapping between string and token values
	 */
	private void initTokenMap() {
		tokenMap = new HashMap<>();
		tokenMap.put("on", TokenType.ON);
		tokenMap.put("alter", TokenType.ALTER);
		tokenMap.put("create", TokenType.CREATE);
		tokenMap.put("or", TokenType.OR);
		tokenMap.put("replace", TokenType.REPLACE);
		tokenMap.put("begin", TokenType.BEGIN);
		tokenMap.put("end", TokenType.END);
		tokenMap.put("package", TokenType.PACKAGE);
		tokenMap.put("body", TokenType.BODY);
		tokenMap.put("procedure", TokenType.PROCEDURE);
		tokenMap.put("function", TokenType.FUNCTION);
		tokenMap.put("select", TokenType.SELECT);
		tokenMap.put("update", TokenType.UPDATE);
		tokenMap.put("insert", TokenType.INSERT);
		tokenMap.put("delete", TokenType.DELETE);
		tokenMap.put("from", TokenType.FROM);
		tokenMap.put("where", TokenType.WHERE);
		tokenMap.put("into", TokenType.INTO);
		tokenMap.put("of", TokenType.OF);
		tokenMap.put(";", TokenType.SEMI);
		tokenMap.put("(", TokenType.OPEN_BRACK);
		tokenMap.put(")", TokenType.CLOSE_BRACK);
		tokenMap.put(",", TokenType.COMMA);
		tokenMap.put("is", TokenType.IS);
		tokenMap.put("loop", TokenType.LOOP);
		tokenMap.put("while", TokenType.WHILE);
		tokenMap.put("for", TokenType.FOR);
		tokenMap.put("if", TokenType.IF);
		tokenMap.put("in", TokenType.IN);
		tokenMap.put("out", TokenType.OUT);
		tokenMap.put("nocopy", TokenType.NOCOPY);
		tokenMap.put("'", TokenType.SINGLE_QUOTE);
		tokenMap.put("/*", TokenType.COMMENT_START);
		tokenMap.put("*/", TokenType.COMMENT_END);
		tokenMap.put("return", TokenType.RETURN);
		tokenMap.put("case", TokenType.CASE);
		tokenMap.put("grant", TokenType.GRANT);
		tokenMap.put("drop", TokenType.DROP);
		tokenMap.put("eof", TokenType.END);
		tokenMap.put("as", TokenType.AS);
		tokenMap.put("type", TokenType.TYPE);
		tokenMap.put("add", TokenType.ADD);
		tokenMap.put("table", TokenType.TABLE);
		tokenMap.put("view", TokenType.VIEW);
		tokenMap.put("trigger", TokenType.TRIGGER);
		tokenMap.put("values", TokenType.VALUES);
		tokenMap.put(":", TokenType.COLON);
		tokenMap.put("=", TokenType.EQUAL);
		tokenMap.put("union", TokenType.UNION);
		tokenMap.put("minus", TokenType.MINUS);
		tokenMap.put("intersect", TokenType.INTERSECT);
		tokenMap.put("<", TokenType.LESS);
		tokenMap.put(">", TokenType.GREAT);
		tokenMap.put("force", TokenType.FORCE);
		tokenMap.put("/", TokenType.SEPARATOR);
		tokenMap.put("commit", TokenType.COMMIT);
		tokenMap.put("foreign", TokenType.FOREIGN);
		tokenMap.put("primary", TokenType.PRIMARY);
		tokenMap.put("references", TokenType.REFERENCES);
		tokenMap.put("constraint", TokenType.CONSTRAINT);
		tokenMap.put("global", TokenType.GLOBAL);
		tokenMap.put("temporary", TokenType.TEMPORARY);
		tokenMap.put("using", TokenType.USING);
		tokenMap.put("merge", TokenType.MERGE);
	}

	/**
	 * Return the token from the lookahead buffer. Lookahead with no parameters
	 * defaults to the 1st lookahead
	 * 
	 * @return the look-ahead token
	 * @throws IOException If the input stream cannot be read then an IOException
	 *                     will be thrown
	 */

	public Token lookAhead() throws IOException { // lookahead with no argument defaults to the 1st lookahead
		return lookAhead(1);
	}

	/**
	 * Proactively fill the lookahead buffer
	 * 
	 * @throws IOException If the input stream cannot be read then an IOException
	 *                     will be thrown
	 */

	private void fillLookAhead() throws IOException {
		int currentSize = lookAheadBuf.size();
		int i = currentSize;
		while (i < lookAheadMax) {
			Token token = tokenise();
			if (!token.getStrVal().isEmpty()) {
				lookAheadBuf.add(token); // add to the end
				i++;
			}
		}
	}

	/**
	 * Get the lookahead token from the lookahead buffer
	 * 
	 * @param num The number of the lookahead
	 * @return Returns the lookahead token
	 * @throws IOException An exception is thrown if the input stream cannot be
	 *                     accessed correctly
	 */
	public Token lookAhead(int num) throws IOException {
		if (lookAheadBuf.size() < lookAheadMax) { // if lookAheadBuf is not filled to capacity then
			fillLookAhead();
		}
		if ((num - 1) <= lookAheadBuf.size()) { // check if the lookahead buffer has the required lookaheads
			Token lookAheadToken = lookAheadBuf.get((num - 1) % lookAheadMax); // fulfil the lookahead request
			return lookAheadToken;
		} else {
			if (num > lookAheadMax) {// if not throw an IOException
				throw new IOException("Lookahead access for " + num +
						" cannot be fulfilled as it exceeds max " + lookAheadMax);
			} else {
				throw new IOException("Lookahead access for " + num +
						" cannot be fulfilled as EOF has been reached");
			}
		}
	}

	/**
	 * Gets the next token from the input
	 * 
	 * @return The next token from the stream
	 * @throws Exception Throws an exception if the input stream cannot be accessed
	 *                   or read correctly
	 */

	public Token nextToken() throws Exception {
		if (!hasMoreTokens()) {
			return new Token("eof", TokenType.EOF);
		}

		if (lookAheadBuf.isEmpty()) { // if lookAheadBuf is empty, then fill it
			fillLookAhead();
		}
		Token token = lookAheadBuf.get(0); // get the token at the head of the list
		lookAheadBuf.remove(0); // remove the lookahead from the start of list.
								// This automatically left shifts the remaining tokens

		// if(!streamBuf.isEmpty() || !streamBuf.isEof()) { // if more tokens in stream
		// then
		// Token lookAheadToken = tokenise(); // get the next token from the stream
		// addLookAhead(lookAheadToken); // add it to the end of the look ahead buf
		// }
		currentToken = token;
		return token; // return the token
	}

	/**
	 * Returns the current token
	 * 
	 * @return current token
	 */

	public Token getCurrentToken() {
		return currentToken;
	}

	/**
	 * Checks if the input has more tokens or eof has been reached
	 * 
	 * @return boolean indicating if the input has more tokens or not
	 */

	public boolean hasMoreTokens() {
		boolean end = lookAheadBuf.isEmpty() && streamBuf.isEmpty() && streamBuf.isEof();
		return !end;
	}

	/**
	 * Get the token object corresponding to the input string
	 * 
	 * @param str The input string
	 * @return TokenType
	 */

	private TokenType getTokenVal(String str) {
		String val = str.trim().toLowerCase();
		TokenType tkn = tokenMap.get(val);
		if (tkn == null) {
			val = val.replace('.', '$');
			Matcher matcher = idPattern.matcher(val);
			if (matcher.matches()) {
				return TokenType.ID;
			} else {
				return TokenType.DEFAULT;
			}
		}
		return tkn;
	}

	/**
	 * Starts to tokenise the input character stream
	 * 
	 * @return the next token from the stream
	 * @throws IOException Throws an exception if the input cannot be read correctly
	 */

	private Token tokenise() throws IOException { // return the next token from the stream
		int ch;
		StringBuilder tbuf = new StringBuilder(); // token buffer onto which a given token is collected

		if (streamBuf.isEmpty()) {// if buffer is exhausted
			streamBuf.fillBuffer(); // fill up buffer from input stream. Tokeniser controls this as opposed to
									// StreamBuffer
			// as it allows it to reset the position and re-read etc.
		}

		while (!streamBuf.isEmpty()) {
			char c = streamBuf.nextChar();

			switch (c) {
				case '\r':
					if (verbose) {
						os.write(c);
					}
					break;
				case '\n': {
					if (verbose) {
						os.write(c);
					}
					String currentTokenStr = tbuf.toString().trim();
					if (!currentTokenStr.isEmpty()) { // if token buffer has some valid characters then return it
						// on seeing a new line. Whatever has been accumulated so far, should constitute
						// a token
						// as a new line is seen as a token separator
						currentTokenStr = tbuf.toString().trim();
						return new Token(currentTokenStr, getTokenVal(currentTokenStr));
					}
					break;
					// otherwise just consume it
				}
				case ',':
					// case ':' :
				case ';':
				case '(':
				case ')':
				case '=':
				case '|':
				case '+':
				case '*':
				case '&':
				case '^':
				case '~':
				case '!': {
					String str = String.valueOf(c);
					String currentTokenStr = tbuf.toString().trim();
					// The characters in these cases are usually token separators and also tokens in
					// their own rights
					// Check if the tokeniser has accumulated characters in the buffer first
					if (!currentTokenStr.isEmpty()) { // if tokeniser has accumulated characters in the buffer
						streamBuf.rewind(1); // rewind streamPos to pick up the same character again
						return new Token(currentTokenStr, getTokenVal(currentTokenStr)); // return the token collected
																							// so far
					} else { // if no token has been collected
						if (verbose) {
							os.write(c);
						}
						return new Token(str, getTokenVal(str)); // then return this character as a token
					}
				}

				case '\t':
				case ' ': {
					if (verbose) {
						os.write(c);
					}
					String currentTokenStr = tbuf.toString().trim();
					if (!currentTokenStr.isEmpty()) { // if buffer has valid characters then return them
						return new Token(currentTokenStr, getTokenVal(currentTokenStr));
					}
					break;
					// otherwise just consume it
				}

				case '-': { // eat line comment
					if (verbose) {
						os.write(c);
					}
					if (streamBuf.isEmpty()) {
						streamBuf.fillBuffer();
					}
					char la = streamBuf.nextChar(); // check if the next char is also a -
					if (verbose) {
						os.write(la);
					}
					if (la == '-') { // if it is a - then
						while (!streamBuf.isEof() || !streamBuf.isEmpty()) { // while EOF is not reached
							if (streamBuf.isEmpty()) { // check if the stream buffer is empty
								streamBuf.fillBuffer(); // if it is then refill buffer with new input from stream
							}
							ch = streamBuf.nextChar(); // read the next character from the stream buffer
							if (verbose) {
								os.write((char) ch);
							}
							if (ch == '\n') { // if it is new line then the line comment is finished
								break;
							}
						}
					} else { // if the character is not -
						tbuf.append(c); // append these characters in the token buffer
						tbuf.append(la);
					}

				}
					break;

				case '/': { // eat block comments
					if (verbose) {
						os.write(c);
					}
					if (streamBuf.isEmpty()) {
						streamBuf.fillBuffer();
					}
					char la = (char) streamBuf.charAt(streamBuf.getPos());

					if (la == '*') {
						boolean insideComment = true;
						char prevChar = ' ';
						char nextChar = '*';
						while (insideComment) {
							while (!streamBuf.isEof() || !streamBuf.isEmpty()) {
								if (streamBuf.isEmpty()) {
									streamBuf.fillBuffer();
								}
								prevChar = nextChar;
								nextChar = streamBuf.nextChar();
								if (verbose) {
									os.write(nextChar);
								}
								if (nextChar == '/') {
									break;
								}
							}
							// prevChar = (char) streamBuf.charAt(streamBuf.getPos() - 2); // getPos() - 1
							// is the current character
							if (prevChar == '*') {
								insideComment = false;
							}
							// if(streamBuf.isEof()) {
							// insideComment = false;
							// }
						}
					} else { // if the character is not -
						tbuf.append(c); // append these characters in the token buffer
					}
				}
					break;

				case '\'': { // read quoted input as one token
					if (verbose) {
						os.write(c);
					}
					tbuf.append(c);
					boolean insideQuote = true;
					while (insideQuote) {
						while (!streamBuf.isEof() || !streamBuf.isEmpty()) {
							if (streamBuf.isEmpty()) {
								streamBuf.fillBuffer();
							}
							char nextChar = streamBuf.nextChar();
							if (verbose) {
								os.write(nextChar);
							}
							tbuf.append(nextChar);

							if (nextChar == '\'') {
								// tbuf.append(nextChar);
								insideQuote = false;
								break;
							}
						}
						if (streamBuf.isEof()) {
							insideQuote = false;
						}
					}
				}
					break;

				default: { // any other character just append it to the token buffer and accumulate
					if (verbose) {
						os.write(c);
					}
					tbuf.append(c);
				}
			}
		}

		String str = tbuf.toString().trim();
		return new Token(str, getTokenVal(str));
	}

	public static void main(String[] args) throws Exception {
		Tokeniser tk = new Tokeniser(new java.io.FileReader("C:\\temp\\MDB\\moischedulesummary.pkb"),
				new java.io.FileWriter(new File("C:\\temp\\V_MktDispatch.sql.out")));
		while (tk.hasMoreTokens()) {
			Token tkn = tk.nextToken();
			System.out.println("<" + tkn.getStrVal() + ">" + " : " + tkn.getTknVal());
		}
	}

}
