package com.paras.parser;

/**
 * Class defining the parse tokens returned by the tokeniser/scanner. Each token
 * has a token type and the actual value corresponding to it.
 * 
 * 
 * @See Tokeniser
 * @author seethavi
 */

public final class Token {

    /*
     * Enum defining the different types of tokens
     */
    public enum TokenType {

        PACKAGE, BODY, PROCEDURE, FUNCTION, SELECT,
        INSERT, UPDATE, DELETE, WHERE, FROM, INTO, OF,
        OPEN_BRACK, CLOSE_BRACK, SEMI, COMMA, IS, BEGIN,
        END, WHILE, FOR, IF, IN, OUT, NOCOPY, SINGLE_QUOTE,
        LOOP, COMMENT_START, COMMENT_END, RETURN, CASE, DROP,
        GRANT, DEFAULT, EOF, AS, CREATE, REPLACE, OR, ID, TYPE,
        TABLE, VIEW, TRIGGER, CONSTRAINT, ADD, ALTER, ON,
        VALUES, EQUAL, COLON, UNION, INTERSECT, MINUS, GREAT, LESS,
        FORCE, SEPARATOR, COMMIT, FOREIGN, PRIMARY, REFERENCES, GLOBAL,
        TEMPORARY, USING, MERGE
    };
    
    /*
     * value of the token
     */
    private String val;
    /*
     * Type of the token
     */
    private TokenType tkn;

    /**
     * Default no-arg constructur
     */
    public Token() {
    }

    /* constructor taking the value and the type of the token
     * 
     */
    public Token(String val, TokenType tkn) {
        this.val = val;
        this.tkn = tkn;
    }
    
    
    /**
     * Getter returning the string value of the token
     * @return String value corresponding to the token
     */

    public String getStrVal() {
        return val;
    }

    /**
     * Getter returning the token value of the token
     * @return Token value
     */
    public TokenType getTknVal() {
        return tkn;
    }

    /**
     * 
     * @return String representation of the token object 
     */
    @Override
    public String toString() {
        return val;
    }
}
