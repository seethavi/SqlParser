package com.paras.ast;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.paras.ast.TableAccess.AccessType;

/**
 * Represents the SQL trigger. This class extends the ParseableObject as it can
 * contain code that needs to be parsed. Also, it implements the AccessReferencer
 * interface as the PL/SQL code contained in a trigger can reference procedures/
 * functions and access tables.
 * 
 * @See ParseableObject
 * @See AccessReferencer
 * @See DefaultAccessReferencer
 * @See Table
 * 
 * @author seethavi
 */
public class Trigger extends Container implements AccessReferencer {
    /*
     * String describing the condition for the trigger. This field is currently
     * not in use
     */

    private String triggerCondition;
    /*
     * Table on which this trigger is defined
     */
    private Reference<Table> sourceTable;
    /*
     * A delegate that implements the AccessReferencer implementation
     */
    private AccessReferencer referencerDelegate;

    /**
     * Default constructor
     * @param name Name of the trigger
     * @param tableOrViewName Name of the table or view on which the trigger is defined
     */
    public Trigger(String name, String tableOrViewName) {
        super(name);
        sourceTable = new Reference<Table>(tableOrViewName, null);
        referencerDelegate = new DefaultAccessReferencer();
    }

    /**
     * Getter for the trigger condition
     * @return TriggerCondition as String
     */
    public String getTriggerCondition() {
        return triggerCondition;
    }

    /**
     * Setter for the trigger condition
     * @param triggerCondition Trigger condition to set (String)
     */
    public void setTriggerCondition(String triggerCondition) {
        this.triggerCondition = triggerCondition;
    }

    /**
     * Getter for source table
     * @return Table object corresponding to the source table
     */
    public Table getSourceTable() {
        return sourceTable.getReferencedObject();
    }

    /**
     * Setter for the source table
     * @param t Source table object
     */
    public void setSourceTable(Table t) {
        sourceTable.setReferencedObject(t);
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
     * See AccessReferencer#addInvocationRefs(Map<String, Procedure> refs)
     */
    @Override
    public void addInvocationRefs(Map<String, Procedure> refs) {
        referencerDelegate.addInvocationRefs(refs);
    }

    /*
     * See AccessReferencer#getInvocations()
     */
    @Override
    public Set<String> getInvocations() {
        return referencerDelegate.getInvocations();
    }

    /*
     * See AccessReferencer#getInvocationRefs()
     */
    @Override
    public Collection<Procedure> getInvocationRefs() {
        return referencerDelegate.getInvocationRefs();
    }

    /*
     * See AccessReferencer#containsInvocation(String)
     */
    @Override
    public boolean containsInvocation(String invocationName) {
        return referencerDelegate.containsInvocation(invocationName);
    }

    /*
     * See AccessReferencer#addTableAccessRef(String, AccessType)
     */
    @Override
    public void addTableAccessRef(String tableName, AccessType type) {
        referencerDelegate.addTableAccessRef(tableName, type);
    }

    /*
     * See AccessReferencer#removeTableAccessRef(String, AccessType)
     */
    @Override
    public void removeTableAccessRef(String tableName, AccessType type) {
        referencerDelegate.removeTableAccessRef(tableName, type);
    }

    /*
     * See AccessReferencer#getTableAccesses()
     */
    @Override
    public Set<String> getTableAccesses() {
        return referencerDelegate.getTableAccesses();
    }

    /*
     * See AccessReferencer#getTableAccessRefs()
     */
    @Override
    public Collection<TableAccess> getTableAccessRefs() {
        return referencerDelegate.getTableAccessRefs();
    }

    /*
     * See AccessReferencer#addInvocationRef(String, Procedure)
     */
    @Override
    public void addInvocationRef(String invocationName, Procedure p) {
        referencerDelegate.addInvocationRef(invocationName, p);
    }

    /*
     * See AccessReferencer#removeInvocationRef(String)
     */
    @Override
    public void removeInvocationRef(String invocationName) {
        referencerDelegate.removeInvocationRef(invocationName);
    }

    /*
     * See AccessReferencer#rationaliseReferences(Schema, SqlSource)
     */
    @Override
    public void rationaliseReferences(Schema schema, SqlSource source) {
        Table table = null;
        String tableName = sourceTable.getReferencedName(); // get the name 
        // of table/view on which the trigger is defined
        for (SqlSource s : schema.getDBObjects()) {
            table = s.findTableOrView(tableName); // search through all the objects in the parse context
            // looking for the table/view name
            if (table != null) { // if found then resolve the reference
                sourceTable.setReferencedObject(table);
                break;
            }
        }
        referencerDelegate.rationaliseReferences(schema, source); // resolve other
        // references using the referencer delegate
    }

    /*
     * See AccessReferencer#write(Writer, String)
     */
    @Override
    public void write(Writer out, String indent) throws IOException {
        out.write(indent);
        out.write("<trigger name='");
        out.write(getName());
        out.write("' defined-on='");
        out.write(sourceTable.getReferencedName());
        out.write("'>\n");
        super.write(out, indent + "   ");
        referencerDelegate.write(out, indent + "   ");
        out.write(indent);
        out.write("</trigger>\n\n");

    }
}
