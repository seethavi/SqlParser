package com.paras.ast;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.paras.ast.TableAccess.AccessType;

/**
 * Procedure class that models the PL/SQL notion of a procedure. This could be
 * either defined inside a package or in a SQL source directly without any
 * associated package namespace.
 * 
 * @see Parameter
 * @see Function
 * @see Container
 * @see AccessReferencer
 * @author seethavi
 */
public class Procedure extends Container implements AccessReferencer {

    /**
     * List of parameters for this procedure
     */
    protected List<Parameter> paramList;
    /**
     * AccessReferencer object that will be used to resolve references to other
     * procedures/functions and table accesses
     */
    protected AccessReferencer referencerDelegate;

    /**
     * Default constructor
     * @param name Name of this procedure
     */
    public Procedure(String name) {
        super(name);
        this.referencerDelegate = new DefaultAccessReferencer();
    }

    /**
     * Auto-generated code to implement the AccessReferencer interface using the
     * referencerDelegate object
     * 
     */
    @Override
    public void addInvocationRef(String invocationName, Procedure p) {
        referencerDelegate.addInvocationRef(invocationName, p);
    }

    @Override
    public void removeInvocationRef(String invocationName) {
        referencerDelegate.removeInvocationRef(invocationName);
    }

    @Override
    public void addInvocationRefs(Map<String, Procedure> refs) {
        referencerDelegate.addInvocationRefs(refs);
    }

    @Override
    public Set<String> getInvocations() {
        return referencerDelegate.getInvocations();
    }

    @Override
    public Collection<Procedure> getInvocationRefs() {
        return referencerDelegate.getInvocationRefs();
    }

    @Override
    public boolean containsInvocation(String invocationName) {
        return referencerDelegate.containsInvocation(invocationName);
    }

    @Override
    public void addTableAccessRef(String tableName, AccessType type) {
        referencerDelegate.addTableAccessRef(tableName, type);
    }

    @Override
    public void removeTableAccessRef(String tableName, AccessType type) {
        referencerDelegate.removeTableAccessRef(tableName, type);
    }

    @Override
    public Set<String> getTableAccesses() {
        return referencerDelegate.getTableAccesses();
    }

    @Override
    public Collection<TableAccess> getTableAccessRefs() {
        return referencerDelegate.getTableAccessRefs();
    }

    @Override
    public void rationaliseReferences(Schema schema, SqlSource source) {
        referencerDelegate.rationaliseReferences(schema, source);
        super.rationaliseReferences(schema, source);
        
    }
    
    @Override
    public Collection<Package> getPackageRefs() {
        return referencerDelegate.getPackageRefs();
    }
    
    @Override
    public void addPackageRef(Package p) {
        referencerDelegate.addPackageRef(p);
    }


    /**
     * Utility method to add a parameter to the procedure
     * @param param Parameter to be added
     */
    public void addParameter(Parameter param) {
        if (paramList == null) {
            paramList = new ArrayList<>();
        }
        paramList.add(param);

    }

    /**
     * Utility method to get the list of parameters for this procedure
     * @return List of parameters
     */
    public List<Parameter> getParameterList() {
        if (paramList == null) {
            paramList = new ArrayList<>();
        }
        return paramList;
    }
    
    /**
     * Returns the number of parameters that this procedure has
     * 
     * @return Number of Parameters
     */
    public int getNumParams() {
        return getParameterList().size();
    }

    /**
     * Utility method to initialise the parameter list for this procedure
     * @param paramList ParameterList to initialise with
     */
    public void setParameterList(List<Parameter> paramList) {
        this.paramList = paramList;
    }

    /**
     * Utility method to add a list of parameters to this procedure. This method
     * will add to the given list and not overwrite it.
     * @param paramList List of parameters to be copied into the current list
     */
    public void addParameterList(List<Parameter> paramList) {
        if (paramList == null) {
            paramList = new ArrayList<>();
        }
        this.paramList.addAll(paramList);
    }

    /**
     * Prints basic information about this procedure
     * @param out Output to write the information to
     * @param indent Format space to use
     * @throws IOException When output cannot be written
     */
    @Override
    public void write(Writer out, String indent) throws IOException {
        out.write(indent);
        out.write("<procedure name='" + getName());
        out.write("' >\n");
        out.write(indent + "   <parameter-list>\n");
        for (Parameter p : getParameterList()) {
            out.write(indent + "      <parameter name='" + p.getName() + "' type='" + p.getType() + "'/>\n");
        }
        out.write(indent + "   </parameter-list>\n");
        referencerDelegate.write(out, indent + "   ");
        super.write(out, indent + "   ");
        out.write(indent);
        out.write("</procedure>\n\n");
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("(");
        int i = 1;
        for(Parameter p : getParameterList()) {
            sb.append(p.getName()).append(" ").append(p.getType());
            if(i != getNumParams()) {
                sb.append(", ");
                i++;
            }
        }
        sb.append(")");
        return sb.toString();
    }

  
}
