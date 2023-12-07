package com.paras.ast;

import java.io.IOException;
import java.io.Writer;


/**
 * Class describing a table column in the abstract syntax tree of a SQL DDL
 * 
 * @see Table
 * @see View
 * @author seethavi
 * @since PLSQLRE r1
 */

public class Column {
	
	/**
	 * Column name
	 */
	private String name;
	/**
	 * Column type
	 */
	private String type;
	/**
	 * Captures the nullability aspects of the column
	 */
	private boolean nullable;
	/**
	 * Captures the default value of the column
	 */
	private String defaultVal;
	
	/**
	 * Initialises the column with a name and type
	 * @param name Name of the column
	 * @param type The SQL type of the column
	 */
	public Column(String name, String type) {
		this.name = name;
		this.type = type;
	}
	
	/**
	 * Following methods are auto-generated getters and setters 
	 *
	 */

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public String getDefaultVal() {
		return defaultVal;
	}

	public void setDefaultVal(String defaultVal) {
		this.defaultVal = defaultVal;
	}

	public void print(Writer out, String indent) throws IOException {
		out.write(indent);
		out.write(name);
		out.write(" ");
		out.write(type);
		out.write("\n");
	}

}
