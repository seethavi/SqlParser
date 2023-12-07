package com.paras.ast;

import java.io.File;

/**
 * Base class representing all the database objects
 * 
 * @see Column
 * @see Constraint
 * @see Package
 * @see Parameter
 * @see Procedure
 * @see Function
 * @see Table
 * @see View
 * @see Trigger
 * @author seethavi
 */
public abstract class DBObject {

    /**
     * Name of the source file from where this object has been created
     */
    private File sourceFile;
    private String uuid;
    private int id;
    private String name;
    
    

    /**
     * Default constructor
     * @param name Name of the DB Object
     */
    public DBObject(String name) {
        this.name = name;
        id = 0;
    }

        /**
     * sets the source file from which this object definition is parsed
     * @param name Fully qualified path name of the source file
     */
    public void setSourceFile(String name) {
        this.sourceFile = new File(name);
    }
    
    public void setSourceFile(File file) {
        this.sourceFile = file;
    }

    /**
     * @return Name of the source file
     */
    public String getSourceFileName() {
        return this.sourceFile.getName();
    }
    
    public String getSourceFileQN() {
        return this.sourceFile.getAbsolutePath();
    }
    
    public File getSourceFile() {
        return this.sourceFile;
    }

    public void setUuid(String id) {
        uuid = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
