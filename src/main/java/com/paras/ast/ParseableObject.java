package com.paras.ast;

/**
 * An abstract class that represents objects that require code parsing. This includes
 * database objects that have PL/SQL code defined or referenced in them, such as, Procedure, Function,
 * Package, SQLSource, Trigger, Views etc. Parsing of such objects require that
 * they be on the stack, or explicitly parsed by the parser.
 * 
 * @see Procedure
 * @see Function
 * @see Package
 * @see SqlSource
 * @see Trigger
 * @see View
 * @author seethavi
 */
public abstract class ParseableObject extends DBObject {
	/**
	 * A counter to keep track of the different levels of nesting within this object. For example,
	 * block statements (BEGIN, END) are considered as part of the nesting levels. Whenever a BEGIN is
	 * encountered the counter is incremented by 1. Whenever, a corresponding end is seen the counter is
	 * decremented. This way, an object such as procedure, function etc seen in the input definitions file
	 * can be correctly pushed and popped from the parse stack and information gathered during the parse
	 * can be attached to the objects currently in scope. 
	 */
	private int beginCounter;

        /** 
         * Default constructor
         * 
         * @param name Name of the object
         */
	public ParseableObject(String name) {
		super(name);
		beginCounter = 0;
	}
        
        /**
         * utility method to increment the counter
         */
	public void incBeginCount() {
		beginCounter++;

	}
        
        /**
         * utility method to decrement the counter
         */

	public void decBeginCount() {
		beginCounter--;

	}
        
        /**
         * utility method to get the value of the counter
         * @return Counter value
         */
	
	public int getBeginCount() {
		return beginCounter;
	}

}
