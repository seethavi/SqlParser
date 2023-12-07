package com.paras.ast;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.paras.ast.TableAccess.AccessType;

/**
 * Package represents a PL/SQL package. As a container, it can include procedure
 * and function definitions. For the purposes of the reverse engineering tool 
 * this class will contain information reverse engineered from a package body 
 * definition (*.pkb)
 * 
 * @see Container
 * @see Procedure
 * @see Function
 * @author seethavi
 */
public class Package extends Container implements AccessReferencer {

    private AccessReferencer referencerDelegate;

    /**
     * Default constructor
     * @param name Name of the package
     */
    public Package(String name) {
        super(name);
        referencerDelegate = new DefaultAccessReferencer();
        String fullPkgName = getName().replace("\"", ""); // remove any quote characters from the name
        String pkgName = fullPkgName;

        int index = fullPkgName.lastIndexOf('.'); // check if symbol is a qualified name. Sometimes the package name
        // has the schema name in front of it. This adds very little info from a model perspective. So may not make
        // much sense to retain it.
        if (index > 0) { // if yes
            pkgName = fullPkgName.substring(index + 1); // extract just the pkg name leaving out the schema name

        }
        setName(pkgName); //reset the package name
    }

    public String getCanonicalName() {
        String fullPkgName = getName().replace("\"", ""); // remove any quote characters from the name
        String pkgName = fullPkgName;

        int index = fullPkgName.lastIndexOf('.'); // check if symbol is a qualified name. Sometimes the package name
        // has the schema name in front of it. This adds very little info from a model perspective. So may not make
        // much sense to retain it.
        if (index > 0) { // if yes
            pkgName = fullPkgName.substring(index + 1); // extract just the pkg name leaving out the schema name

        }
        return pkgName;
    }
    
    @Override
    public void rationaliseReferences(Schema schema, SqlSource source) {
        for (Procedure p : getProcedures()) { // for procedures defined
            // in this package rationalise the references
            p.rationaliseReferences(schema, source);
        }
        for (Function f : getFunctions()) { // for functions defined 
            // in this package rationalise the references
            f.rationaliseReferences(schema, source);
        }
    }

    @Override
    /**
     * Utility method to get the number of procedures in the DB object
     * @return count of procedures
     */
    protected int getNumProcs() {
        int num = getProcedures().size(); // initialise it to the number of procedures attached to the compilation unit

        for (Procedure p : getProcedures()) { // get numbers from procedures directly attached to it
            num += p.getNumProcs();
        }
        return num;
    }

    @Override
    /**
     * Utility method to get the number of functions in the DB Object
     * @return count of functions
     */
    protected int getNumFuncs() {
        int num = functions.size(); // initialise it to the number of functions attached to the compilation unit

        for (Function f : getFunctions()) { // get numbers from functions directly attached to it
            num += f.getNumFuncs();
        }
        return num;
    }
    
    @Override
    public Collection<Package> getPackageRefs() {
        return referencerDelegate.getPackageRefs();
    }

    @Override
    public void addPackageRef(Package p) {
        referencerDelegate.addPackageRef(p);
    }

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

    /**
     * Prints XML information about the package
     * @param out Output to send the information to
     * @param indent To format the output
     * @throws IOException When output cannot be written
     */
    @Override
    public void write(Writer out, String indent) throws IOException {
        out.write(indent);
        out.write("<package name='");
        out.write(getName());
        out.write("'>\n");
        super.write(out, indent);
        referencerDelegate.write(out, indent + "   ");
        out.write(indent);
        out.write("</package>\n\n");
    }
}
