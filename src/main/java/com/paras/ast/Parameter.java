package com.paras.ast;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.paras.ast.TableAccess.AccessType;

/**
 * Class that models the procedure and function parameters
 * @author seethavi
 */
public class Parameter extends DBObject {

    /**
     * Name of the parameter is inherited from DBObject
     */

    /**
     * Type of the parameter
     */
    private String type;
    /**
     * SQL modifiability of the parameter. IN, OUT
     */
    private String modifiability;

    private boolean isRefType;

    private String tableName;
    
    private String columnName;

    /**
     * Default constructor
     * @param name Parameter Name
     * @param type Parameter Type
     * @param modifiability Denotes if the parameter can be modified by the function
     */
    public Parameter(String name, String type, String modifiability) {
        super(name);
        this.type = type;
        this.modifiability = modifiability;
        setRefType( (this.type != null) && type.toLowerCase().contains("%type") );
        this.tableName = null;
        this.columnName = null;
    }

    private boolean isRefType() {
        return this.isRefType;
    }

    private void setRefType(boolean typeStatus) {
        this.isRefType = typeStatus;
    }

    /**
     * Private method to extract the table name from the type
     * @param type
     * @return
     */

    private String getTableName() {
        // if tableName is not initialised and the parameter is a reference then
        if(this.tableName == null && isRefType()) {
            // infer the table name from the type
            this.tableName = getType().substring(0, type.indexOf('.'));
        }
        return this.tableName;
    }

    private String getColumnName() {
        // if ref type and columnName is not initialised then infer it from the parameter
        if(this.columnName == null && isRefType()) {
            this.columnName = getType().substring(type.indexOf('.') + 1, type.indexOf('%'));
        }
        return this.columnName;
    }

    /**
     * Resolves references to types based on table column types of the form cust.id%type where cust is the
     * table id is the column and %type denotes the type as defined the table definition
     * @param schema contains all the db objects including the table where the column is defined
     */
    public void resolveReference(Schema schema) {
        if(isRefType()) {
                // first SqlSource in the returned list contains the reference to the table
            for(SqlSource source : schema.getDBObjects()) {
                Table table = source.findTableOrView(getTableName()); // locate the table reference
                if(table != null) { // found the table definition
                    Column col = table.getColumn(getColumnName()); // get the column
                    setType(col.getType());
                    break; // break out of the for
                }
            }
        }
    }

    /**
     * Prints basic information to the output
     * @param out Output to write the information to
     * @param indent Formatting space
     * @throws IOException When information cannot be written to output
     */
    public void print(Writer out, String indent) throws IOException {
        out.write(indent);
        out.write(getName() + " " + type + " " + modifiability);
        out.write("\n");
    }

    /**
     * Auto generated getter/setter below
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        setRefType(type != null && type.toLowerCase().contains("%type"));
    }
}
