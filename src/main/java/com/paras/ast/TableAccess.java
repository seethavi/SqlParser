package com.paras.ast;

/**
 * Class that captures information relating to table accesses. Table accesses
 * are generated when a Procedure, Function, View, Trigger, SQL Source etc,
 * reference a table by issuing a SQL DML command (insert, delete etc). 
 * 
 * @see AccessReferencer
 * @see DefaultAccessReferencer
 * @author seethavi
 */
public class TableAccess {

    /**
     * Enum capturing different types of access.
     */
    public enum AccessType {

        INSERT("Insert"),
        DELETE("Delete"),
        SELECT("Select"),
        UPDATE("Update");
        private String access;

        private AccessType(String str) {
            access = str;
        }

        private String getAccess() {
            return access;
        }
    };
    /**
     * The able being referenced. This includes the name of the table and a reference
     * to the actual table itself
     */
    private Reference<Table> table;
    /**
     * Type of access as defined in the Enum above
     */
    private AccessType accessType;

    /**
     * Default constructor
     * @param tableName Name of the table
     * @param accessType Type of the access
     */
    public TableAccess(String tableName, AccessType accessType) {
        table = new Reference<Table>(tableName, null);
        setTableAccess(accessType);
    }
    
    /**
     * Standard getter/setters are shown below.
     */

    private void setTableAccess(AccessType accessType) {
        this.accessType = accessType;
    }

    public void setTable(Table table) {
        this.table.setReferencedObject(table);
    }

    public Table getTable() {
        return table.getReferencedObject();
    }

    public String getTableName() {
        return table.getReferencedName();
    }

    public AccessType getAccessType() {
        return accessType;
    }

    public String getAccessTypeAsString() {
        return accessType.getAccess();
    }

    public boolean equals(TableAccess ta) {
        return this.accessType == ta.accessType && this.getTableName().equals(ta.getTableName());
    }
}
