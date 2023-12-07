package com.paras.ast;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.paras.ast.TableAccess.AccessType;

/**
 * SqlSource is the core of the syntax tree. This class captures the essence of the executable PL / SQL
 * objects. In other words, this addresses those PL SQL types that can have executable code in them such
 * procedure, function, view, trigger etc and also acts as a container for data definition aspects such 
 * as tables, views etc. The parser creates this object and manages it on the parse stack
 * so that as other elements are recognised on the input stream, they then can be attached to this object.
 * 
 * @see Function
 * @see Procedure
 * @see Trigger
 * @see View
 * @author seethavi
 * @since PLSQLRE v1
 */
public class SqlSource extends Container implements AccessReferencer {

   
    /**
     * The set of packages defined in this SqlSource. In reality, there may be only one package per file
     */
    private Map<String, Package> packages;
    /**
     * The set of all constraints
     */
    protected Map<String, Constraint> constraints;
    /**
     * The set of table definitions that could be located in the SqlSource
     */
    protected Map<String, Table> tableDefinitions;
    /**
     * The set of all view definitions
     */
    protected Map<String, View> viewDefinitions;
    /**
     * The set of all triggers
     */
    protected Map<String, Trigger> triggers;
    /**
     * Delegate that manages all the access references, including table access and procedure accesses
     */
    private AccessReferencer referencerDelegate;

    /**
     * Create a new SqlSource with the name 
     * @param name Name of the SqlSource to be created
     */
    public SqlSource(String name) {
        super(name);
        this.packages = new HashMap<>();
        this.constraints = new HashMap<>();
        this.viewDefinitions = new HashMap<>();
        this.tableDefinitions = new HashMap<>();
        this.triggers = new HashMap<>();
        this.referencerDelegate = new DefaultAccessReferencer();

    }


    /**
     * Adds a package definition
     * @param pkg PL SQL package definition to be added
     */
    public void addPackage(Package pkg) {
        pkg.setSourceFile(getSourceFile());
        packages.put(pkg.getName().toLowerCase(), pkg);
    }

    /**
     * Returns the packages defined in this source file
     * @return collection of packages
     */
    public Collection<Package> getPackages() {
        return packages.values();
    }
    
    public Package getMainPackage() {
        Iterator<Package> i = getPackages().iterator();
        if(i.hasNext()) {
            return i.next();
        }
        else {
            return null;
        }
    }
    /**
     * Adds a constraint definition
     * @param constraint SQL constraint definition to be added
     */
    public void addConstraint(Constraint constraint) {
        constraint.setSourceFile(getSourceFile());
        constraints.put(constraint.getName().toLowerCase(), constraint);
    }
    
    public Collection<Constraint> getConstraints() {
        return constraints.values();
    }

    /**
     * Adds a table definition
     * @param table SQL table definition to be added
     */
    public void addTable(Table table) {
        table.setSourceFile(getSourceFile());
        tableDefinitions.put(table.getName().toLowerCase(), table);
    }

    public Table findTableOrView(String name) {
        Table t = tableDefinitions.get(name.toLowerCase());
        if (t == null) {
            t = viewDefinitions.get(name.toLowerCase());
        }
        return t;
    }
    
    public Collection<Table> getTables() {
        return tableDefinitions.values();
    }

    /**
     * Adds a view definition
     * @param view SQL view definition to be added
     */
    public void addView(View view) {
        view.setSourceFile(getSourceFile());
        viewDefinitions.put(view.getName().toLowerCase(), view);
    }
    
    public Collection<View> getViews() {
        return viewDefinitions.values();
    }

    /**
     * Adds a trigger definition
     * @param trigger SQL trigger definition to be added
     */
    public void addTrigger(Trigger trigger) {
        trigger.setSourceFile(getSourceFile());
        triggers.put(trigger.getName().toLowerCase(), trigger);
    }
    
    public Collection<Trigger> getTriggers() {
        return triggers.values();
    }

    /**
     * Adds a table access reference. Delegates to referencer.
     * @param tableName Name of the table that is accessed by code defined in this DB object
     * @param type Type of access <blockquote><pre>INSERT, DELETE, UPDATE, SELECT</pre></blockquote>
     */
    @Override
    public void addTableAccessRef(String tableName, TableAccess.AccessType type) {
        referencerDelegate.addTableAccessRef(tableName, type);

    }

    @Override
    public void addInvocationRef(String procName, Procedure p) {
        referencerDelegate.addInvocationRef(procName, p); // reference needs to be resolved to a real function later on
    }

    /**
     * Add a reference to a procedure or function call from the current parse context
     * @param procName Name of the procedure or function to be referenced.
     */
    public void addInvocationRef(String procName) {
        referencerDelegate.addInvocationRef(procName, null); // reference needs to be resolved to a real function later on
    }

    /**
     * Add a map of references to procedures / functions. This is a convenience method
     * @param refs Map of references to add
     */
    @Override
    public void addInvocationRefs(Map<String, Procedure> refs) {
        referencerDelegate.addInvocationRefs(refs);
    }

    /**
     * Checks if an invocation is present in the map of invocations
     * @param procName the name of the procedure
     * @return true if an invocation to the given procedure exists
     */
    @Override
    public boolean containsInvocation(String procName) {
        return referencerDelegate.containsInvocation(procName);
    }

    @Override
    public void removeInvocationRef(String invocationName) {
        referencerDelegate.removeInvocationRef(invocationName);

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
    public Collection<Package> getPackageRefs() {
        return referencerDelegate.getPackageRefs();
    }
        
    @Override
    public void addPackageRef(Package pkg) {
        referencerDelegate.addPackageRef(pkg);
    }
    
    /**
     * This method resolves the invocation reference to a procedure from the name to the actual
     * Procedure or Function object
     * @param invocation name of the procedure or function being referenced
     * @return the Procedure / Function with the specific name.
     */
    @Override
    public Procedure resolveInvocation(String invocation) {
        Procedure proc = super.resolveInvocation(invocation);
        if (proc != null) {
            return proc;
        }
        for (Package p : packages.values()) {
            proc = p.resolveInvocation(invocation);
            if (proc != null) {
                return proc;
            }
        }
        return proc;
    }

    /**
     * Rationalises the references to objects within the schema. For example, if a constraint references
     * a Table, then the table name is resolved to the actual object. 
     * @param schema The schema object containing the entire set of objects
     * @param topLevelDBO The top level DB Object containing other objects from where the rationalisation
     * 						process needs to start
     */
    @Override
    public void rationaliseReferences(Schema schema, SqlSource source) {
        referencerDelegate.rationaliseReferences(schema, source);
        // cascade this to the nested elements
        for (Package p : packages.values()) {
            p.rationaliseReferences(schema, source);
        }
        super.rationaliseReferences(schema, source);
        for (Trigger t : triggers.values()) {
            t.rationaliseReferences(schema, source);
        }
        for (View v : viewDefinitions.values()) {
            v.rationaliseReferences(schema, source);
        }
        for (Constraint c: constraints.values()) {
            c.rationaliseReferences(schema, source);
        }

    }

    /**
     * Utility method to get the number of procedures in the DB object
     * @return count of procedures
     */
    @Override
    protected int getNumProcs() {
        int num = getProcedures().size(); // initialise it to the number of procedures attached to the compilation unit

        for (Package pkg : getPackages()) { // get numbers from packages attached to it
            num += pkg.getNumProcs();
        }

        for (Procedure p : getProcedures()) { // get numbers from procedures directly attached to it
            num += p.getNumProcs();
        }
        return num;
    }

    /**
     * Utility method to get the number of functions in the DB Object
     * @return count of functions
     */
    @Override
    protected int getNumFuncs() {
        int num = functions.size(); // initialise it to the number of functions attached to the compilation unit

        for (Package pkg : getPackages()) { // get numbers from packages attached to it
            num += pkg.getNumFuncs();
        }

        for (Function f : getFunctions()) { // get numbers from functions directly attached to it
            num += f.getNumFuncs();
        }
        return num;
    }

    /**
     * Utility method to print stats information about the DB Object
     * @param out output stream to write to
     * @throws IOException if output cannot be written to
     */
    public void printStatistics(Writer out) throws IOException {
        int numProcs = getNumProcs();
        int numFuncs = getNumFuncs();
        out.write("Number of Procedures (including nested): " + numProcs);
        out.write("\n");
        out.write("Number of Functions (including nested): " + numFuncs);
        out.write("\n");
    }

    /**
     * Print references to tables
     * @param out output stream to print to
     * @param indent indentation to use
     * @throws IOException if output stream cannot be written to
     
     * /*
    protected void printTableRefs(Writer out, String indent) throws IOException {
        out.write("\n----------Table access references for -------------\n");
        out.write(getName());
        out.write("\n");
        for (TableAccess access : referencerDelegate.getTableAccessRefs()) {
            out.write(indent + "   ");
            out.write(access.getTableName() + " " + access.getAccessTypeAsString());
            out.write("\n");
        }
        out.write("\n***************************************************\n");
    }
     * */

    /**
     * Print invocation references to procedures / functions
     * @param out output stream to print to
     * @param indent indentation to use
     * @throws IOException if output stream cannot be written to
     */
    /*
    protected void printInvocationRefs(Writer out, String indent) throws IOException {
    out.write("\n----------Invocation References for -------------\n");
    out.write(getName());
    out.write("\n");
    
    for(Iterator<String> i = referencerDelegate.getInvocations().iterator(); i.hasNext();) {
    out.write(indent + "   ");
    out.write(i.next());
    out.write("\n");
    }
    for(Package p: packages.values()) {
    p.printInvocationRefs(out, indent + "   ");
    }
    for(Procedure p: procedures.values()) {
    p.printInvocationRefs(out, indent + "   ");
    }
    for(Function f: functions.values()) {
    f.printInvocationRefs(out, indent + "   ");
    }
    for(Trigger t: triggers.values()) {
    t.print(out, indent);
    }
    
    out.write("\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\n");
    }
     */
    
    protected void printPackageDefinitions(Writer out, String indent) throws IOException {
        out.write(indent);
        out.write("<package-definitions>\n");
        for (Package p : packages.values()) {
            p.write(out, indent + "   ");
        }
        out.write(indent);
        out.write("</package-definitions>\n\n");
    }
 
    protected void printTriggerDefinitions(Writer out, String indent) throws IOException {
        out.write(indent);
        out.write("<trigger-definitions>\n");
        for (Trigger t : triggers.values()) {
            t.write(out, indent + "   ");
        }
        out.write(indent);
        out.write("</trigger-definitions>\n\n");
    }

    /**
     * Print constraint definitions
     * @param out output stream to print to
     * @param indent indentation to use
     * @throws IOException if output stream cannot be written to
     */
    protected void printConstraintDefinitions(Writer out, String indent) throws IOException {
        out.write(indent);
        out.write("<constraint-definitions>\n");
        for (Constraint c : constraints.values()) {
            c.write(out, indent + "   ");
        }
        out.write(indent);
        out.write("</constraint-definitions>\n\n");
    }

    /**
     * Print table definitions
     * @param out output stream to print to
     * @param indent indentation to use
     * @throws IOException if output stream cannot be written to
     */
    protected void printTableDefinitions(Writer out, String indent) throws IOException {
        out.write(indent);
        out.write("<table-definitions>\n");
        for (Table t : tableDefinitions.values()) {
            t.write(out, indent + "   ");
        }
        out.write(indent);
        out.write("</table-definitions>\n\n");
    }

    /**
     * Print view definitions
     * @param out output stream to print to
     * @param indent indentation to use
     * @throws IOException if output stream cannot be written to
     */
    protected void printViewDefinitions(Writer out, String indent) throws IOException {
        out.write(indent);
        out.write("<view-definitions>\n");
        for (View v : viewDefinitions.values()) {
            v.write(out, indent + "   ");
        }
        out.write(indent);
        out.write("</view-definitions>\n\n");
    }


    /**
     * Print all information pertaining to this DB Object. Recurse into nested DB Objects as necessary
     * @param out output stream to print to
     * @param indent indentation to use
     * @throws IOException if output stream cannot be written to
     */
    @Override
    public void write(Writer out, String indent) throws IOException {
        out.write(indent + "<sql-source name='" + getName() + "' file='" + getSourceFileQN() + "' >\n");
        printPackageDefinitions(out, indent + "   ");
        printTableDefinitions(out, indent + "   ");
        printViewDefinitions(out, indent + "   ");
        printConstraintDefinitions(out, indent + "   ");
        printTriggerDefinitions(out, indent + "   ");
        super.write(out, indent + "   ");
        referencerDelegate.write(out, indent + "   ");
        out.write(indent + "</sql-source>");
    }



    
}
