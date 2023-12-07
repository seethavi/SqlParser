package com.paras.parser;

import java.util.Stack;

import com.paras.ast.ParseableObject;

/**
 * A straight-forward extension of the default Stack implementation constraining
 * the stackable objects to be of type ParseableObject. In future, there could 
 * be customisation of the functionality implemented by the ParseStack.
 * 
 * @author seethavi
 */
public class ParseStack extends Stack<ParseableObject> {
	
}
