package com.paras.ast;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Access Referencer interface that is responsible for managing references from
 * procedures, functions, triggers, views to other procedures, functions and tables.
 * 
 * The interface describes a basic contract for creating and resolving references
 * between parsed objects
 * 
 * @author seethavi
 * @see Procedure
 * @see Function
 * @see Trigger
 * @see View
 * @see DefaultAccessReferencer
 */
public interface AccessReferencer {
    
    /**
     * Get all the package references from the access referencer
     * @return Collection of package references
     */
    public Collection<Package> getPackageRefs();

    /**
     * Adds a reference to the package where the symbol is defined
     * @param pkg package reference to be added
     */
    public void addPackageRef(Package pkg);

    /**
     * Add a reference to an invocation to a procedure or a function
     * @param invocationName Name of the procedure/function to be added
     * @param p 
     */
    public void addInvocationRef(String invocationName, Procedure p);

    /**
     * Remove the invocation reference
     * @param invocationName Name of the procedure/function to be removed
     */
    public void removeInvocationRef(String invocationName);

    /**
     * Utility method that deals with bulk-add of references
     * @param refs Collection of invocation references to be added
     */
    public void addInvocationRefs(Map<String, Procedure> refs);

    /**
     * Return the set of invocations
     * @return Set of procedure / function names
     */
    public Set<String> getInvocations();

    /**
     * Returns a collection of procedure objects
     * @return Collection of procedures
     */
    public Collection<Procedure> getInvocationRefs();

    /**
     * Validates if an invocation is present in the set of referenced invocations
     * @param invocationName Name of the invocation to verify
     * @return true if present
     */
    public boolean containsInvocation(String invocationName);

    /**
     * Add reference to a table
     * @param tableName Name of the table to be referenced
     * @param type Access type (INSERT, DELETE, UPDATE, SELECT)
     */
    public void addTableAccessRef(String tableName, TableAccess.AccessType type);

    /**
     * Remove reference to a table
     * @param tableName Name of the table to be removed from the list of references
     * @param type Type of access to be removed from the list of references
     */
    public void removeTableAccessRef(String tableName, TableAccess.AccessType type);

    /**
     * Get a list of table access references
     * @return Set of table access names
     */
    public Set<String> getTableAccesses();

    /**
     * Get a list of table access objects
     * @return Set of table access objects
     */
    public Collection<TableAccess> getTableAccessRefs();

    /**
     * Resolve all symbols to point to appropriate objects from the parser output
     * @param schema Parse context containing all the objects from the parse
     * @param source Current file where the definition of the object implementing this interface was found
     */
    public void rationaliseReferences(Schema schema, SqlSource source);

    /**
     * Prints table access references in XML     
     * @param writer output writer to use
     * @param indent for formatting
     * @throws IOException when output cannot be written to
     */
    public void write(Writer writer, String indent) throws IOException;
}
