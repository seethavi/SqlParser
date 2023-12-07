package com.paras.ast;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Class that models the SQL Table construct.
 * 
 * @see View
 * @see DBObject
 * 
 * @author seethavi
 */
public class Table extends DBObject {

    /**
     * Column definitions
     */
    private Map<String, Column> columnMap;

    /**
     * Default constructor
     * @param name Name of the table
     */
    public Table(String name) {
        super(name);
        columnMap = new HashMap<String, Column>();
    }

    /**
     * Utility method to add a columnName to the list. 
     * @param columnName Name of column to be added
     */
    public void addColumn(String columnName) {
       addColumn(new Column(columnName, ""));
    }

    /**
     * Utility method to add a column to the list
     * @param col Column object to be added to the column list
     */
    public void addColumn(Column col) {
        columnMap.put(col.getName(), col);
    }
    
    /**
     * Utility method to replace the column list
     * @param columnMap column map to replace with
     */

    public void setColumnMap(Map<String, Column> columnMap) {
        this.columnMap = columnMap;
    }
    
    /**
     * Utility method that checks the presence of a column with a given name
     * @param name Name of the column to check
     * @return True if the column is present, false otherwise.
     */

    public boolean hasColumn(String name) {
        return columnMap != null ? columnMap.containsKey(name) : false;
    }
    
    /**
     * Retrieve the column with a given name
     * @param name Name of the column
     * @return Column object that matches the name
     */

    public Column getColumn(String name) {
        return columnMap != null ? columnMap.get(name) : null;
    }
    
    /**
     * Retrieve all the columns
     * @return Collection of columns
     */

    public Collection<Column> getColumns() {
        return columnMap.values();
    }
    
    /**
     * Retrieve all the column names
     * @return Set of column names
     */

    public Set<String> getColumnNames() {
        return columnMap.keySet();
    }

    /**
     * Utility method that outputs information about this table
     * @param out Output to send the information to
     * @param indent Formatting space to use
     * @throws IOException When output cannot be written to
     */
    public void write(Writer out, String indent) throws IOException {
        out.write(indent + "<table name='" + getName() + "'>\n");
        out.write(indent + "   <columns>\n");
        for (Column col : columnMap.values()) {
            out.write(indent + "      <column name='" + col.getName() + "' type='" + col.getType() + "'/>\n");
        }
        out.write(indent + "   </columns>\n");
        out.write(indent + "</table>\n\n");
    }
    
 
}
