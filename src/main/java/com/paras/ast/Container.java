package com.paras.ast;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The Container class, models a grouping of executable code namely procedures and functions.
 * Two primary kinds of containers exist, namely PL SQL Package and any SQL file where
 * procedures / functions can be defined without any name-space bindings. 
 * 
 * @author seethavi
 * @see com.paras.ast.Package
 * @see SQLSource 
 */
public class Container extends ParseableObject {

    /**
     * The set of all procedures defined in the container
     */
    protected List<Procedure> procedures;
    /**
     * The set of all functions defined in this SqlSource
     */
    protected List<Function> functions;

    /**
     * Default constructor
     * @param name Name of the container
     */
    public Container(String name) {
        super(name);
        this.procedures = new ArrayList<Procedure>();
        this.functions = new ArrayList<Function>();

    }

    /**
     * Adds a procedure definition
     * @param proc PL SQL procedure definition to be added
     */
    public void addProcedure(Procedure proc) {
        proc.setSourceFile(getSourceFileQN());
        procedures.add(proc);
    }

    public Collection<Procedure> getProcedures() {
        return procedures;
    }

    /**
     * Adds a function definition
     * @param func PL SQL function definition to be added
     */
    public void addFunction(Function func) {
        func.setSourceFile(getSourceFileQN());
        functions.add(func);
    }

    public Collection<Function> getFunctions() {
        return functions;
    }

    /**
     * Finds a procedure or a function with a given name. This is part of resolving references to 
     * functions and procedures that a given piece of code within this DB object references
     * @param name 
     * @return
     */
    public Procedure findProcOrFuncWithName(String name) {
        Procedure p = getProcWithName(name);
        if (p == null) {
            p = getFuncWithName(name);
        }
        return p;
    }

    private Procedure searchProcedure(Collection<Procedure> procedureList, String name, int paramCount) {
        Procedure match = null;
        List<Procedure> procShortList = new ArrayList<Procedure>();
        for (Procedure proc : procedureList) { // go through the list and create a short-list of procs that would match this call
            if (proc.getName().equalsIgnoreCase(name) && proc.getNumParams() >= paramCount) {
                procShortList.add(proc);
            }
        }
        if (procShortList.isEmpty()) { // no procedures match by name hence return null
            return null;
        } else { // matches exist, so we need to find the closest match by number of parameters
            match = procShortList.get(0);
            for (Procedure proc : procShortList) {
                if (proc.getNumParams() == paramCount) {
                    match = proc;
                } else if (proc.getNumParams() > paramCount) {
                    if (proc.getNumParams() < match.getNumParams()) {
                        match = proc;
                    }
                }
            }
            return match;
        }
    }

    private Function searchFunction(Collection<Function> funcList, String name, int paramCount) {
        Function match = null;
        List<Function> funcShortList = new ArrayList<Function>();
        for (Function func : funcList) { // go through the list and create a short-list of procs that would match this call
            if (func.getName().equalsIgnoreCase(name) && func.getNumParams() >= paramCount) {
                funcShortList.add(func);
            }
        }
        if (funcShortList.isEmpty()) { // no procedures match by name hence return null
            return null;
        } else { // matches exist, so we need to find the closest match by number of parameters
            match = funcShortList.get(0);
            for (Function func : funcShortList) {
                if (func.getNumParams() == paramCount) {
                    match = func;
                } else if (func.getNumParams() > paramCount) {
                    if (func.getNumParams() < match.getNumParams()) {
                        match = func;
                    }
                }
            }
            return match;
        }
    }

        /**
         * Get the procedure defined in this DB Object by name 
         * @param name Name of the procedure
         * @return Procedure with the name
         */
    

    

    public Procedure getProcWithName(String name) {
        int index = name.lastIndexOf("#");
        String procName = name.substring(0, index);
        int paramCount = Integer.valueOf(name.substring(index + 1));
        Procedure proc = searchProcedure(getProcedures(), procName, paramCount);
        if (proc == null) { // check for nested procedures
            for (Procedure p : getProcedures()) {
                proc = p.getProcWithName(name);
                if (proc != null) {
                    return proc;
                }
            }
        }
        return proc;

    }

    /**
     * Get the function defined in this DB Object by name
     * @param name Name of the function
     * @return Function with the name
     */
    public Function getFuncWithName(String name) {
        int index = name.lastIndexOf("#");
        String funcName = name.substring(0, index);
        int paramCount = Integer.valueOf(name.substring(index + 1));
        Function func = searchFunction(getFunctions(), funcName, paramCount);
        if (func == null) { // check for nested procedures
            for (Procedure p : getProcedures()) {
                func = p.getFuncWithName(name);
                if (func != null) {
                    return func;
                }
            }
        }
        return func;

    }

    /**
     * Utility function that returns the number of procedures defined.
     * Since procedures can have other nested procedures, this method
     * recursively gets the details
     * 
     * @return Total number of procedures defined in the container
     */
    protected int getNumProcs() {
        int num = procedures.size(); // initialise it to the number of procedures attached to the compilation unit

        for (Procedure p : getProcedures()) { // get numbers from procedures directly attached to it
            num += p.getNumProcs();
        }
        for (Function f : getFunctions()) { // get nested procedures defined within functions
            num += f.getNumProcs();
        }
        return num;
    }

    /**
     * Utility method to get the number of functions in the container. Since
     * functions can have other nested functions, this method
     * @return count of functions
     */
    protected int getNumFuncs() {
        int num = functions.size(); // initialise it to the number of functions attached to the compilation unit

        for (Procedure p : getProcedures()) { // get nested functions defined within procedures
            num += p.getNumFuncs();
        }

        for (Function f : getFunctions()) { // get numbers from functions directly attached to it
            num += f.getNumFuncs();
        }
        return num;
    }

    /**
     * A utility method that is used to locate a reference to a function. This searches
     * through the list of procedures and functions recursively in an attempt to locate
     * the procedure or function with the exact name
     * 
     * @param invocation Name of the procedure or function to search
     * @return  Procedure or Function that matches the name. Where two procedures are of the same name, the first 
     * identified function will be returned. 
     * 
     * TODO: resolve the case where two functions or procedures may have the same name (FIXED).
     */
    public Procedure resolveInvocation(String invocation) {
        int index = invocation.lastIndexOf("#");
        String procName = invocation.substring(0, index);
        int paramCount = Integer.valueOf(invocation.substring(index + 1));
        Procedure proc = searchProcedure(getProcedures(), procName, paramCount);
        if (proc != null) {
            return proc;
        }

        index = invocation.lastIndexOf("#");
        procName = invocation.substring(0, index);
        paramCount = Integer.valueOf(invocation.substring(index + 1));

        Function func = searchFunction(getFunctions(), procName, paramCount);
        if (func != null) {
            return func;
        }

        for (Procedure p : getProcedures()) {
            proc = p.resolveInvocation(invocation); // else check for nested procedures within this
            // procedure
            if (proc != null) {
                return proc;
            }
        }

        for (Function f : getFunctions()) {
            proc = f.resolveInvocation(invocation); // else check for nested functions
            if (proc != null) {
                return proc;
            }
        }
        return null;
    }

    public void rationaliseReferences(Schema schema, SqlSource source) {
        for (Procedure p : getProcedures()) { // for nested procedures
            p.rationaliseReferences(schema, source);
        }
        for (Function f : getFunctions()) { // for nested functions
            f.rationaliseReferences(schema, source);
        }
    }

    public void write(Writer out, String indent) throws IOException {
        out.write(indent);
        out.write("<procedures>\n");
        for (Procedure p : getProcedures()) {
            p.write(out, indent + "   ");
        }
        out.write(indent);
        out.write("</procedures>\n\n");

        out.write(indent);
        out.write("<functions>\n");
        for (Function f : getFunctions()) {
            f.write(out, indent + "   ");
        }
        out.write(indent);
        out.write("</functions>\n\n");
    }
}
