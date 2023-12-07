package com.paras.ast;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

/**
 * This class captures the SQL constraints. Three different types of constraints are addressed:
 * <blockquote><pre> 1. Primary Key 2. Foreign key 3. Uniqueness </pre></blockquote>
 * 
 * @author seethavi
 * @since PLSQLRE r1
 */
public class Constraint extends DBObject {

    /**
     * enum capturing the different types of constraints
     * 
     * @author seethavi
     *
     */
    public enum ConstraintType {

        PK("Primary Key"),
        FK("Foreign Key"),
        UNIQUENESS("Uniqueness");
        private String type;

        private ConstraintType(String str) {
            type = str;
        }

        private String getType() {
            return type;
        }
    };
    /**
     * Type of the constraint FK, PK or Uniqueness
     */
    private ConstraintType constraintType;
    /**
     * Source table on which the constraint is defined
     */
    private Reference<Table> sourceTable;
    /**
     * Source columns on which the constraint is defined
     */
    private Map<String, Column> sourceColumns;
    /**
     * If this is a foreign key then the target columns on which the constraint is defined
     */
    private Map<String, Column> targetColumns;
    /**
     * If this is a foreign key then the target table to which constraint relates to
     */
    private Reference<Table> targetTable;

    /**
     * Creates a constraint with the name
     * @param name Name of the constraint
     */
    public Constraint(String name) {
        super(name);
        sourceColumns = new HashMap<String, Column>();
        targetColumns = new HashMap<String, Column>();
        this.constraintType = ConstraintType.UNIQUENESS;
    }

    public ConstraintType getConstraintType() {
        return constraintType;
    }

    public void setConstraintType(ConstraintType constraintType) {
        this.constraintType = constraintType;
    }

    public String getSourceTableName() {
        return sourceTable != null ? sourceTable.getReferencedName() : "";
    }

    public Optional<Table> getSourceTable() {
        return Optional.ofNullable(sourceTable.getReferencedObject());
    }

    public void setSourceTable(String tableName) {
        sourceTable = new Reference<>(tableName, null);
    }

    public Map<String, Column> getSourceColumns() {
        return sourceColumns;
    }

    public void addSourceColumn(String columnName) {
        this.sourceColumns.put(columnName, null);
    }

    public Map<String, Column> getTargetColumns() {
        return targetColumns;
    }

    public void addTargetColumn(String columnName) {
        this.targetColumns.put(columnName, null);
    }

    public String getTargetTableName() {
        return targetTable != null ? targetTable.getReferencedName() : "";
    }

    public Table getTargetTable() {
        return targetTable != null ? targetTable.getReferencedObject() : null;
    }

    public void setTargetTable(String tableName) {
        targetTable = new Reference<Table>(tableName, null);
    }

    public void rationaliseReferences(Schema schema, SqlSource source) {
        Table srcTable = null;
        Table tgtTable = null;
        String sourceTableName = sourceTable.getReferencedName(); // get the source table name 
        String targetTableName = null;
        if (targetTable != null) {
            targetTableName = targetTable.getReferencedName();
        } // get the target table name

        // of table/view on which the constraint is defined
        for (SqlSource s : schema.getDBObjects()) {
            srcTable = s.findTableOrView(sourceTableName); // search through all the objects in the parse context
            // looking for the table/view name
            if (srcTable != null) { // if found then resolve the reference
                sourceTable.setReferencedObject(srcTable);
                break;
            }
        }

        if (targetTableName != null) {
            for (SqlSource s : schema.getDBObjects()) {
                tgtTable = s.findTableOrView(targetTableName);
                if (tgtTable != null) {
                    targetTable.setReferencedObject(tgtTable);
                    break;
                }
            }
        }

    }

    public void write(Writer writer, String indent) throws IOException {
        writer.write(indent + "<constraint name='" + getName()
                + "' type='" + getConstraintType().toString() + "' source='"
                + getSourceTableName() + "' target='"
                + getTargetTableName() + "'>\n");
        writer.write(indent + "   <source-columns>\n");
        for (Iterator<String> i = sourceColumns.keySet().iterator(); i.hasNext();) {
            writer.write(indent + "      <column name='" + i.next() + "'/>\n");
        }
        writer.write(indent + "   </source-columns>\n");
        writer.write(indent + "   <target-columns>\n");
        for (Iterator<String> i = targetColumns.keySet().iterator(); i.hasNext();) {
            writer.write(indent + "      <column name='" + i.next() + "'/>\n");
        }
        writer.write(indent + "   </target-columns>\n");
        writer.write(indent + "</constraint>\n\n");
    }
}
