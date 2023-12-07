/**
 * 
 */
package com.paras.ast;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.paras.ast.TableAccess.AccessType;

/**
 * Default implementation of the AccessReferencer interface. An instance of this
 * class can be used as a delegate to manage the creation, tracking and
 * resolution of
 * references - procedure / function invocation and table accesses. This class
 * simplifies
 * the management of the reference functionality
 * 
 * @see Procedure
 * @see Function
 * @see Package
 * @see Trigger
 * @see View
 * @see SqlSource
 * @author seethavi
 *
 */
public class DefaultAccessReferencer implements AccessReferencer {

	/**
	 * Tables accessed by this object. In some situations, executable PL/SQL code
	 * can be directly
	 * defined at a file level outside of procedures, functions etc.
	 */
	protected Map<String, TableAccess> tableAccessRefs;
	/**
	 * Procedures and functions referenced by this object
	 * 
	 */
	protected Map<String, Procedure> invocationRefs;

	/**
	 * Uses the invocation references to also establish dependencies between
	 * packages
	 */

	protected Set<Package> packageRefs;

	/**
	 * Default constructor
	 */

	public DefaultAccessReferencer() {
		this.tableAccessRefs = new HashMap<>();
		this.invocationRefs = new HashMap<>();
		this.packageRefs = new HashSet<>();
	}

	/**
	 * Add a deduced package reference. As the referencer is resolving
	 * known references, it also establishes package level dependencies. This is
	 * basically
	 * a side effect of the reference resolution mechanism.
	 * 
	 * @param p package to add
	 */
	@Override
	public void addPackageRef(Package p) {
		packageRefs.add(p);
	}

	@Override
	public Collection<Package> getPackageRefs() {
		return packageRefs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.paras.ast.AccessReferencer#addInvocationRef(java.lang.String)
	 */
	@Override
	public void addInvocationRef(String invocationName, Procedure p) {
		invocationRefs.put(invocationName, p);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.paras.ast.AccessReferencer#addInvocationRefs(java.util.Map)
	 */
	@Override
	public void addInvocationRefs(Map<String, Procedure> refs) {
		invocationRefs.putAll(refs);

	}

	/**
	 * @see com.paras.ast.AccessReferencer#removeInvocationRef(String)
	 * @param invocationName
	 */
	@Override
	public void removeInvocationRef(String invocationName) {
		invocationRefs.remove(invocationName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.paras.ast.AccessReferencer#addTableAccessRef(java.lang.String,
	 * com.paras.ast.TableAccess.AccessType)
	 */
	@Override
	public void addTableAccessRef(String tableName, AccessType type) {
		TableAccess access = new TableAccess(tableName, type);
		String key = tableName.toLowerCase() + "_" + type.name();
		tableAccessRefs.put(key, access);

	}

	/**
	 * @see com.paras.ast.AccessReferencer#addTableAccessRef(String tableName,
	 *      AccessType type)
	 * @param tableName
	 * @param type
	 */

	@Override
	public void removeTableAccessRef(String tableName, AccessType type) {
		String key = tableName + "_" + type.name();
		tableAccessRefs.remove(key);
	}

	/**
	 * @see com.paras.ast.AccessReferencer#getInvocationRefs()
	 * @return
	 */
	@Override
	public Collection<Procedure> getInvocationRefs() {
		return invocationRefs.values();
	}

	/**
	 * @see com.paras.ast.AccessReferencer#getTableAccessRefs()
	 *
	 */
	@Override
	public Collection<TableAccess> getTableAccessRefs() {
		return tableAccessRefs.values();
	}

	/**
	 * @see com.paras.ast.AccessReferencer#containsInvocation(String)
	 *
	 */

	@Override
	public boolean containsInvocation(String invocationName) {
		return invocationRefs.containsKey(invocationName);
	}

	/**
	 * @see com.paras.ast.AccessReferencer#getInvocations()
	 *
	 */

	@Override
	public Set<String> getInvocations() {
		return invocationRefs.keySet();
	}

	/**
	 * @see com.paras.ast.AccessReferencer#getTableAccesses()
	 *
	 */
	@Override
	public Set<String> getTableAccesses() {
		return tableAccessRefs.keySet();
	}

	/**
	 * @see com.paras.ast.AccessReferencer#rationaliseReferences()
	 *
	 */
	@Override
	public void rationaliseReferences(Schema schema, SqlSource source) {
		rationaliseInvocations(schema, source);
		rationaliseTableAccess(schema);
	}

	@Override
	public void write(Writer out, String indent) throws IOException {
		out.write(indent);
		out.write("<table-references>\n");
		for (Iterator<TableAccess> iterator = getTableAccessRefs().iterator(); iterator.hasNext();) { // iterate
			TableAccess ta = iterator.next();
			out.write(indent + "   ");
			out.write("<table-reference name='");
			out.write(ta.getTableName());
			out.write("' access-type='");
			out.write(ta.getAccessTypeAsString());
			out.write("'/>\n");
		}
		out.write(indent);
		out.write("</table-references>\n\n");

		out.write(indent);
		out.write("<invocation-references>\n");
		for (Iterator<String> iterator = getInvocations().iterator(); iterator.hasNext();) { // iterate
			// for(Procedure proc: getInvocationRefs()) {
			String invocationName = iterator.next();
			Procedure proc = invocationRefs.get(invocationName);
			out.write(indent + "   ");
			out.write("<invocation-reference symbol-ref='");
			out.write(invocationName);
			out.write("'");
			if (proc != null) {
				out.write(" proc-ref='");
				out.write(proc.toString());
				out.write("'");
			}
			out.write("/>\n");
		}
		out.write(indent);
		out.write("</invocation-references>\n\n");

		out.write(indent);
		out.write("<package-references>\n");
		for (Package pkg : getPackageRefs()) {
			out.write(indent + "   ");
			out.write("<package-reference pkg='");
			out.write(pkg.getName());
			out.write("'/>\n");
		}
		out.write(indent);
		out.write("</package-references>\n\n");
	}

	/**
	 * Handles the symbol resolution of references to tables
	 * 
	 * @param schema The parse context containing all the parsed information
	 */

	private void rationaliseTableAccess(Schema schema) {

		for (Iterator<String> iterator = getTableAccesses().iterator(); iterator.hasNext();) { // iterate
			// through the list of table access references. Contains the name of the table
			// only
			Table table = null;
			String key = iterator.next();
			TableAccess ta = tableAccessRefs.get(key); // get the table access object.
			// The table access object contains the table name and a pointer to a table
			// object.
			// The table object is always null until resolved. This method, as part of the
			// symbol
			// resolution initialises the table object to point to the appropriate table
			// object
			// from the parse context
			String tableName = ta.getTableName(); // get the table name from the table access
			// object to ensure that the table name is as defined in the source

			for (SqlSource s : schema.getDBObjects()) {
				table = s.findTableOrView(tableName.toLowerCase()); // search through all the objects in the parse
																	// context
				// looking for the table name
				if (table != null) { // if found then resolve the reference
					ta.setTable(table);
					break;
				}
			}
			if (table == null) { // if the reference cannot be resolved then this is not a valid
				// table.
				iterator.remove(); // Hence remove the element from the list
			}
		}
	}

	/**
	 * Analagous to the rationaliseTableAccesses method, this method resolves
	 * references to functions and procedures
	 * 
	 * @param schema      The parse context containing all the parsed objects
	 * @param topLevelDBO The current source file
	 */

	private void rationaliseInvocations(Schema schema, SqlSource topLevelDBO) {
		Set<String> names = schema.getDBObjectNames();
		Iterator<String> i = getInvocations().iterator();
		while (i.hasNext()) { // go through all the objects in the parse context
			String invocationName = i.next();
			int index = invocationName.lastIndexOf('.'); // check if symbol is a qualified name
			if (index > 0) { // if yes
				String pkgName = invocationName.substring(0, index); // extract package name only
				String procName = invocationName.substring(index + 1); // extract till the end of the string
				SqlSource dbo = schema.getDBObject(pkgName); // get the SqlSource with the pkg name
				if (dbo == null) {
					i.remove();
				} else {
					Procedure p = dbo.resolveInvocation(procName.toLowerCase());
					// If invocation has a '.', then the procedure must exist in the package .
					if (p == null) {
						i.remove();
					} else {
						addInvocationRef(invocationName, p); // resolve the reference to p
						Package targetPkg = dbo.getMainPackage();
						if (targetPkg != null) { // add a reference to the package in which the procedure
							Package sourcePkg = topLevelDBO.getMainPackage();
							if (sourcePkg != null) {
								sourcePkg.addPackageRef(targetPkg);
							}
							addPackageRef(targetPkg); // is defined
						}
					}
				}
			} else {
				// if procedure/function not present within that db object then resolve this
				// reference
				// from the top-level package or module where this procedure is defined. In
				// other words,
				// search for the procedure name / function name in the same .pkg or .sql file
				// in which this
				// procedure is defined
				Procedure p = topLevelDBO.resolveInvocation(invocationName.toLowerCase());
				if (p == null) {
					i.remove(); // if cannot be resolved, then remove from the list
				} else {
					addInvocationRef(invocationName, p); // resolve the reference to p.
				}
			}
		}

	}

}
