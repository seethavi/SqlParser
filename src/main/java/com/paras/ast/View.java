package com.paras.ast;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.paras.ast.TableAccess.AccessType;

/**
 * View represents the corresponding SQL notion of a view. For the purposes of
 * the PL/SQL reverse engineering tool, a View is a table with the primary
 * difference that its columns are derived from another table through primarily a 
 * select on one or more tables. The view can also reference functions, whose
 * references will need to be resolved with the help of an AccessReferencer object
 * 
 * @see Table
 * @see AccessReferencer
 * 
 * @author seethavi
 */
public class View extends Table implements AccessReferencer {

    /** 
     * Access Referencer object used in resolving references to table and functions
     */
    private AccessReferencer referencerDelegate;

    /**
     * Default constructor
     * @param name Name of the View
     */
    public View(String name) {
        super(name);
        this.referencerDelegate = new DefaultAccessReferencer();
    }
    
    /**
     * utility method to add a referenced table
     * @param name Name of the table to be added
     */

    public void addTable(String name) {
        referencerDelegate.addTableAccessRef(name, AccessType.SELECT); // table references will be rationalised later
    }

    @Override
    public void addInvocationRef(String invocationName, Procedure p) {
        referencerDelegate.addInvocationRef(invocationName, p);
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

    /*
    public void buildModel(EAModelBuilder builder, org.sparx.Package viewPkg) {
    
    org.sparx.Element view = builder.findOrCreateView(viewPkg, getName());
    setID(view.GetElementID());
    for(Iterator<String> iterator = getColumnNames().iterator(); iterator.hasNext();) {
    String colName = iterator.next();
    builder.findOrCreateColumn(view, colName, "");
    }
    }
     */
    
 
    @Override
    public void write(Writer out, String indent) throws IOException {
        out.write(indent + "<view name='" + getName() + "'>\n");
        out.write(indent + "   <column-list>\n");
        for (Iterator<String> i = getColumnNames().iterator(); i.hasNext();) {
            out.write(indent + "   ");
            out.write("<column name='");
            out.write(i.next());
            out.write("'/>\n");
        }
        out.write(indent + "   </column-list>\n");
        referencerDelegate.write(out, indent + "   ");
        out.write(indent + "</view>\n\n");
    }

    @Override
    public void rationaliseReferences(Schema schema, SqlSource source) {
        referencerDelegate.rationaliseReferences(schema, source);

    }
}
