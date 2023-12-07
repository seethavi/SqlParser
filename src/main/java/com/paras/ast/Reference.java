package com.paras.ast;

/**
 * A parameterised class that is used to capture a reference to any object. When
 * created it is initialised with a null reference to an object of type T. Then
 * during the resolution process, this value will be initialised to point to an
 * object denoted by the given name;
 * 
 * @author seethavi
 * @param <T> Type of object to create a reference to
 */
public class Reference<T> {

    /**
     * Name of the referenced object. This can only be initialised during object
     * creation time. This is to ensure that the reference name is immutable.
     */
    private final String referencedName;
    /**
     * The object reference as denoted by the referencedName
     */
    private T referencedObject;

    /**
     * Default constructor
     * @param name Name of the object
     * @param o Object reference. This can be null.
     */
    public Reference(String name, T o) {
        this.referencedName = name;
        this.referencedObject = o;
    }
    
    /**
     * Standard getters/setters
     * 
     */

    public String getReferencedName() {
        return referencedName;
    }

    public T getReferencedObject() {
        return referencedObject;
    }

    public void setReferencedObject(T referencedObject) {
        this.referencedObject = referencedObject;
    }

    /** 
     * String comparison and hash methods
     * 
     */
    @Override
    public String toString() {
        return getReferencedName();
    }

    @Override
    public boolean equals(Object o) {

        Reference<T> r = (Reference<T>) o;
        System.out.println("Comparing " + r.getReferencedName() + " with " + getReferencedName());
        boolean val = getReferencedName().equals(r.getReferencedName());
        return val;

    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.referencedName != null ? this.referencedName.hashCode() : 0);
        return hash;
    }
}
