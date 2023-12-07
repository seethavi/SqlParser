package com.paras.ast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Schema is a collection of database objects, each uniquely identifiable by name.
 * The schema object also establishes the parse context for resolving the various
 * procedure/function and table references.
 * @see DBObject
 * @author seethavi
 */
public class Schema {
    
    /**
     * Directory to produce output corresponding to the various parsed objects
     */
    private String outputDir;

    /**
     * Map of all DBObjects defined
     */
    private Map<String, SqlSource> dbObjects; //all objects

    /**
     * Default constructor
     */
    public Schema() {
        dbObjects = new HashMap<>();
    }
    
    /**
     * Set the output directory
     * @param dir fully qualified pathname
     */
    public void setOutputDir(String dir) {
        outputDir = dir;
    }

    /**
     * Utility method to add a DB Object. Schema object maintains a collection
     * of SqlSource objects, which in turn contain the various db object definitions
     * such as procedures, functions, triggers, constraints etc.
     * 
     * @param source The SqlSource
     */
    public void addDBObject(SqlSource source) {
        dbObjects.put(source.getName().toLowerCase(), source);
    }
    
    /**
     * Returns the SQLSource object with a given name
     * @param name Name of the SqlSource object
     * @return The SqlSource object
     */

    public SqlSource getDBObject(String name) {
        return dbObjects.get(name.toLowerCase());
    }
    
    /**
     * Utility method that retrieves all the SqlSource objects
     * @return List of SqlSource objects
     */

    public Collection<SqlSource> getDBObjects() {
        return dbObjects.values();
    }
    
    /**
     * Utility method that retrieves the names of all the SqlSource objects
     * @return 
     */

    public Set<String> getDBObjectNames() {
        return dbObjects.keySet();
    }
    
    /**
     * Method that rationalises references to procedures / functions and tables and
     * resolves the references. This method invocation is passed along to all
     * the DB objects within the parse context
     */

    public void rationaliseReferences() {
        for (SqlSource dbo : dbObjects.values()) {
            dbo.rationaliseReferences(this, dbo);
        }
    }
    
    
    
    /**
     * Utility print method
     * @throws IOException When information cannot be printed
     */
    public void writeToXML() throws IOException {
        int size = dbObjects.size();
        int i = 1;
        File outDir = new File(outputDir);
        if(!outDir.exists()) {
            outDir.mkdir();
        }
        for (SqlSource dbo : dbObjects.values()) {
            Writer fw = new FileWriter(outputDir + File.separator + dbo.getSourceFileName() + ".xml");
            dbo.write(fw, "");
            //dbo.printStatistics(fw);
            fw.flush();
            fw.close();
            i++;
        }
    }
}
