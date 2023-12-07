package com.paras.ast;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;


/**
 * Captures the notion of PL/SQL function. For the purposes of this reverse
 * engineering tool, a function is a kind of a procedure that can return results.
 * 
 * @see Procedure
 * @author seethavi
 */
public class Function extends Procedure {

    /**
     * Return type for results returned from a call to the function. 
     */
    private String returnType;

    /**
     * Default constructor
     * @param name Name of the function
     */
    public Function(String name) {
        super(name);
    }

    /**
     * Sets the return type
     * @param type Type of the value returned
     */
    public void setReturnType(String type) {
        this.returnType = type;
    }

    /**
     * Gets the return type
     * @return Return type associated with this function
     */
    public String getReturnType() {
        return this.returnType;
    }

    /**
     * Basic print method
     * @param out Output writer to write to
     * @param indent Controls the indentation associated with the output
     * @throws IOException When the output cannot be written by the Writer
     */
    @Override
    public void write(Writer out, String indent) throws IOException {
        out.write(indent);
        out.write("<function name='" + getName() + "' return='" + getReturnType() + "' >\n");
        out.write(indent + "   <parameter-list>\n");
        for(Parameter p : getParameterList()) {
            out.write(indent + "      <parameter name='" + p.getName() + "' type='" + p.getType() + "'/>\n");
        }
        out.write(indent + "   </parameter-list>\n");
        referencerDelegate.write(out, indent + "   ");
        out.write(indent);
        out.write("</function>\n\n");
    }

  
}
