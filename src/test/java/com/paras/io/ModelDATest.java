/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paras.io;

import java.util.Calendar;
import java.util.List;
import com.paras.model.TAttribute;
import com.paras.model.TConnector;
import com.paras.model.TObject;
import com.paras.model.TObjectconstraint;


import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 *
 * @author seethavi
 */
public class ModelDATest {

    public ModelDATest() {
    }

    @BeforeAll
    public static void setUpClass() throws Exception {
    }

    @AfterAll
    public static void tearDownClass() throws Exception {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of openRepository method, of class ModelDA.
     */
    @Test
    public void testOpenRepository() throws Exception {
        System.out.println("openRepository");
        ModelDA modelDA = new ModelDA();
        boolean expResult = true;
        boolean result = modelDA.openRepository();
        assertEquals(expResult, result);
        modelDA.closeRepository();
    }

    /**
     * Test of closeRepository method, of class ModelDA.
     */
    @Test
    public void testCloseRepository() throws Exception {
        System.out.println("closeRepository");
        ModelDA modelDA = new ModelDA();
        modelDA.openRepository();
        boolean expResult = true;
        boolean result = modelDA.closeRepository();
        assertEquals(expResult, result);
    }

    /**
     * Test of createObject method, of class ModelDA.
     */
    @Test
    public void testCreateObject() throws Exception {
        ModelDA modelDA = new ModelDA();
        TObject obj = new TObject(true); // initialise guid
        try {
            System.out.println("createObject");
            obj.setName("CLU-1");
            obj.setStereotype("Oracle-Package");
            obj.setObject_Type("Class");
            obj.setPackage_ID(1);
            obj.setNote("The CLU package");
            modelDA.openRepository();
            int result = modelDA.createObject(obj);
            assertEquals(1, result);
        } finally {
            // clean up
            modelDA.deleteObject(obj);
            modelDA.closeRepository();
        }
    }

    /**
     * Test of retrieveObject method, of class ModelDA.
     */
    @Test
    public void testRetrieveObject() throws Exception {
        // create an object first
        ModelDA modelDA = new ModelDA();
        TObject obj = new TObject(true); // initialise guid
        try {
            obj.setName("CLU-2");
            obj.setStereotype("Oracle-Package");
            obj.setObject_Type("Class");
            obj.setPackage_ID(1);
            obj.setNote("The CLU package");
            modelDA.openRepository();
            modelDA.createObject(obj);
            System.out.println("retrieveObject");
            String expGuid = obj.getEa_guid();
            TObject result = modelDA.retrieveObject(obj);
            assertEquals(result.getEa_guid(), expGuid);
        } finally {
            modelDA.deleteObject(obj);
            modelDA.closeRepository();
        }
    }

    /**
     * Test of retrieveNestedObjectListByName method, of class ModelDA.
     */
    @Test
    public void testRetrieveNestedMethodListByName() throws Exception {
        // create a PL/SQL package first
        ModelDA modelDA = new ModelDA();
        TObject obj = new TObject(true); // initialise guid
        TObject method1 = new TObject(true);
        TObject method2 = new TObject(true);

        try {
            obj.setName("CLU-3");
            obj.setStereotype("Oracle-Package");
            obj.setObject_Type("Class");
            obj.setPackage_ID(1);
            obj.setNote("The CLU package");
            modelDA.openRepository();
            modelDA.createObject(obj);

            //create a couple of methods
            method1.setName("Method1");
            method1.setStereotype("Oracle-Procedure");
            method1.setParentID(obj.getObject_ID());
            method1.setPackage_ID(obj.getPackage_ID());
            modelDA.createObject(method1);

            method2.setName("Method2");
            method2.setStereotype("Oracle-Function");
            method2.setParentID(obj.getObject_ID());
            method2.setPackage_ID(obj.getPackage_ID());
            modelDA.createObject(method2);

            // retrieve the created methods
            TObject proc = new TObject();
            proc.setParentID(obj.getObject_ID());
            proc.setName("Method1");
            List<TObject> objList = modelDA.retrieveNestedMethodsByName(proc);
            System.out.println(objList);
            assertNotNull(objList);
            assertEquals(1, objList.size());
            assertEquals(objList.get(0).getStereotype(), "Oracle-Procedure");
            assertEquals(objList.get(0).getEa_guid(), method1.getEa_guid());
        } finally {
            //clean up
            modelDA.deleteObject(obj);
            modelDA.deleteObject(method1);
            modelDA.deleteObject(method2);
            modelDA.closeRepository();
        }
    }

    /**
     * Test of retrieveObjectByID method, of class ModelDA.
     */
    @Test
    public void testRetrieveObjectByID() throws Exception {
        ModelDA modelDA = new ModelDA();
        TObject obj = new TObject(true); // initialise guid 
        try {
            // create an object first
            obj.setName("CLU-4");
            obj.setStereotype("Oracle-Package");
            obj.setObject_Type("Class");
            obj.setPackage_ID(1);
            obj.setNote("The CLU package");

            modelDA.openRepository();
            modelDA.createObject(obj);

            System.out.println("retrieveObjectByID");
            TObject result = modelDA.retrieveObjectByID(obj);
            assertNotNull(result);
            assertEquals("CLU-4", result.getName());
        } finally {
            modelDA.deleteObject(obj);
            modelDA.closeRepository();
        }

    }

    /**
     * Test of updateObject method, of class ModelDA.
     */
    @Test
    public void testUpdateObject() throws Exception {

        ModelDA modelDA = new ModelDA();
        TObject obj = new TObject(true); // initialise guid 
        try {
            // create an object first
            obj.setName("CLU-5");
            obj.setStereotype("Oracle-Package");
            obj.setObject_Type("Class");
            obj.setPackage_ID(1);
            obj.setNote("The CLU package");

            modelDA.openRepository();
            modelDA.createObject(obj);

            System.out.println("updateObject");
            // retrieve the object next
            TObject result = modelDA.retrieveObjectByID(obj);
            assertNotNull(result);

            // update the object
            result.setName("CLU-5-Updated");
            result.setStereotype("Oracle-Package-Updated");
            result.setNote(result.getNote() + "-Updated");
            modelDA.updateObject(result);

            //retrieve the updated object and verify results
            TObject updatedObj = modelDA.retrieveObjectByGUID(result);

            assertEquals("CLU-5-Updated", updatedObj.getName());
            assertEquals("Oracle-Package-Updated", updatedObj.getStereotype());
            System.out.println(updatedObj.getNote());
        } finally {
            modelDA.deleteObject(obj);
            modelDA.closeRepository();
        }
    }

    /**
     * Test of createAttribute method, of class ModelDA.
     */
    @Test
    public void testCreateAttribute() throws Exception {
        ModelDA modelDA = new ModelDA();
        TObject obj = new TObject(true); // create a new object first
        TAttribute attrib = new TAttribute(true); // initialise with a guid
        try {
            obj.setName("Obj1");
            obj.setStereotype("Table");
            obj.setObject_Type("Class");
            obj.setPackage_ID(1);
            obj.setNote("The CLU table");
            modelDA.openRepository();
            modelDA.createObject(obj);
            System.out.println("createAttribute");
            int objID = obj.getObject_ID();

            attrib.setObject_ID(objID);
            attrib.setName("Attrib1");
            attrib.setType("String");
            attrib.setStereotype("Column");
            int result = modelDA.createAttribute(attrib);
            assertEquals(1, result);
        } finally {
            modelDA.deleteObject(obj);
            modelDA.deleteAttribute(attrib);
            modelDA.closeRepository();
        }

    }

    /**
     * Test of retrieveAttribute method, of class ModelDA.
     */
    @Test
    public void testRetrieveAttribute() throws Exception {
        ModelDA modelDA = new ModelDA();
        TObject obj = new TObject(true); // create object first
         TAttribute attrib = new TAttribute(true); // Then an attribute
        try {
            obj.setName("Obj2");
            obj.setStereotype("Table");
            obj.setObject_Type("Class");
            obj.setPackage_ID(1);
            obj.setNote("The CLU Table");

            modelDA.openRepository();
            modelDA.createObject(obj);
            int objID = obj.getObject_ID();
            attrib.setObject_ID(objID);
            attrib.setName("Attrib2");
            attrib.setType("String");
            attrib.setStereotype("Column");
            modelDA.createAttribute(attrib);
            
            System.out.println("retrieveAttribute");
            TAttribute attr = new TAttribute();
            attr.setObject_ID(objID);
            attr.setName("Attrib2");
            
            TAttribute result = modelDA.retrieveAttribute(attr);
            assertEquals("Column", result.getStereotype());
            assertEquals("String", result.getType());
            
        } finally {
            modelDA.deleteObject(obj);
            modelDA.deleteAttribute(attrib);
            modelDA.closeRepository();
        }
    }

    /**
     * Test of retrieveAttributeList method, of class ModelDA.
     */
    @Test
    public void testRetrieveAttributeList() throws Exception {
        ModelDA modelDA = new ModelDA();
        TObject obj = new TObject(true); // create object first
         TAttribute attrib1 = new TAttribute(true); // Then an attribute
         TAttribute attrib2 = new TAttribute(true); // Then another
        try {
            obj.setName("Obj3");
            obj.setStereotype("Table");
            obj.setObject_Type("Class");
            obj.setPackage_ID(1);
            obj.setNote("The CLU Table");

            modelDA.openRepository();
            modelDA.createObject(obj);
            int objID = obj.getObject_ID();
            attrib1.setObject_ID(objID);
            attrib1.setName("Attrib1");
            attrib1.setType("String");
            attrib1.setStereotype("Column");
            modelDA.createAttribute(attrib1);
            
            attrib2.setObject_ID(objID);
            attrib2.setName("Attrib2");
            attrib2.setType("String");
            attrib2.setStereotype("Column");
            modelDA.createAttribute(attrib2);
            
            System.out.println("retrieveAttributeList");
            
            List<TAttribute> result = modelDA.retrieveAttributeList(obj);
            assertEquals(2, result.size());
            
            assertTrue("Attrib1".equals(result.get(0).getName()) || "Attrib1".equals(result.get(1).getName()));
            assertTrue("Attrib2".equals(result.get(0).getName()) || "Attrib2".equals(result.get(1).getName()));
            
 
            
        } finally {
            modelDA.deleteObject(obj);
            modelDA.deleteAttribute(attrib1);
            modelDA.deleteAttribute(attrib2);
            modelDA.closeRepository();
        }
    }

    /**
     * Test of retrieveAttributeCount method, of class ModelDA.
     */
    @Test
    public void testRetrieveAttributeCount() throws Exception {
        ModelDA modelDA = new ModelDA();
        TObject obj = new TObject(true); // create object first
         TAttribute attrib1 = new TAttribute(true); // Then an attribute
         TAttribute attrib2 = new TAttribute(true); // Then another
        try {
            obj.setName("Obj4");
            obj.setStereotype("Table");
            obj.setObject_Type("Class");
            obj.setPackage_ID(1);
            obj.setNote("The CLU Table");

            modelDA.openRepository();
            modelDA.createObject(obj);
            int objID = obj.getObject_ID();
            attrib1.setObject_ID(objID);
            attrib1.setName("Attrib1");
            attrib1.setType("String");
            attrib1.setStereotype("Column");
            modelDA.createAttribute(attrib1);
            
            attrib2.setObject_ID(objID);
            attrib2.setName("Attrib2");
            attrib2.setType("String");
            attrib2.setStereotype("Column");
            modelDA.createAttribute(attrib2);
            
            System.out.println("retrieveAttributeList");
            
            int result = modelDA.retrieveAttributeCount(obj);
            assertEquals(2, result);
            
            
        } finally {
            modelDA.deleteObject(obj);
            modelDA.deleteAttribute(attrib1);
            modelDA.deleteAttribute(attrib2);
            modelDA.closeRepository();
        }
    }

    /**
     * Test of updateAttribute method, of class ModelDA.
     */
    @Test
    public void testUpdateAttribute() throws Exception {
        ModelDA modelDA = new ModelDA();
        TObject obj = new TObject(true); // create a new object first
        TAttribute attrib = new TAttribute(true); // create an attribute. initialise with a guid
        try {
            obj.setName("Obj5");
            obj.setStereotype("Table");
            obj.setObject_Type("Class");
            obj.setPackage_ID(1);
            obj.setNote("The CLU table");
            modelDA.openRepository();
            modelDA.createObject(obj);

            int objID = obj.getObject_ID();

            attrib.setObject_ID(objID);
            attrib.setName("Attrib1");
            attrib.setType("String");
            attrib.setStereotype("Column");
            modelDA.createAttribute(attrib);
            
            System.out.println("updateAttribute");
            
            TAttribute resultAttr = modelDA.retrieveAttribute(attrib); 
            resultAttr.setName(resultAttr.getName() + "-changed");
            resultAttr.setType(resultAttr.getType() + "-changed");
            resultAttr.setStereotype(resultAttr.getStereotype() + "-changed");
            
            int result = modelDA.updateAttribute(resultAttr);
            assertEquals(1, result);
            
            TAttribute updatedAttr = modelDA.retrieveAttribute(resultAttr); 
            assertEquals("Attrib1-changed", updatedAttr.getName());
            assertEquals("String-changed", updatedAttr.getType());
            assertEquals("Column-changed", updatedAttr.getStereotype());
        } 
        catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            modelDA.deleteObject(obj);
            modelDA.deleteAttribute(attrib);
            modelDA.closeRepository();
        }
    }

       /**
     * Test of createConnector method, of class ModelDA.
     */
    @Test
    public void testCreateConnector() throws Exception {
        ModelDA modelDA = new ModelDA();
        TObject source = new TObject(true); // create the source object first
        TObject target = new TObject(true); // create the target object next
        TConnector con = new TConnector(true); // initialise with a guid
        try {
            modelDA.openRepository();
            
            source.setName("sourceObj");
            source.setStereotype("Oracle-Procedure");
            source.setObject_Type("Class");
            source.setPackage_ID(1);
            source.setNote("A procedure");
            
            target.setName("targetObj");
            target.setStereotype("Oracle-Procedure");
            target.setObject_Type("Class");
            target.setPackage_ID(1);
            target.setNote("A procedure");
                        
            modelDA.createObject(source);
            modelDA.createObject(target);
            
            System.out.println("createConnector");

            con.setStart_Object_ID(source.getObject_ID());
            con.setEnd_Object_ID(target.getObject_ID());
            con.setName("test-connector");
            con.setDirection("source->destination");
            con.setConnector_Type("Association");
            
            int result = modelDA.createConnector(con);
            assertEquals(1, result);
        } finally {
            modelDA.deleteObject(source);
            modelDA.deleteObject(target);
            modelDA.deleteConnector(con);
            modelDA.closeRepository();
        }

    }
    
    /**
     * Test of retrieveConnectorByName method, of class ModelDA.
     */
    @Test
    public void testRetrieveConnectorByName() throws Exception {
        ModelDA modelDA = new ModelDA();
        TObject source = new TObject(true); // create the source object first
        TObject target = new TObject(true); // create the target object next
        TConnector con = new TConnector(true); // initialise with a guid
        try {
            modelDA.openRepository();
            
            source.setName("sourceObj");
            source.setStereotype("Oracle-Procedure");
            source.setObject_Type("Class");
            source.setPackage_ID(1);
            source.setNote("A procedure");
            
            target.setName("targetObj");
            target.setStereotype("Oracle-Procedure");
            target.setObject_Type("Class");
            target.setPackage_ID(1);
            target.setNote("A procedure");
                        
            modelDA.createObject(source);
            modelDA.createObject(target);
            
            System.out.println("createConnector");

            con.setStart_Object_ID(source.getObject_ID());
            con.setEnd_Object_ID(target.getObject_ID());
            con.setName("test-connector");
            con.setDirection("source->destination");
            con.setConnector_Type("Association");
            
            int result = modelDA.createConnector(con);
            
            TConnector connector = modelDA.retrieveConnectorByName(con);
            assertEquals(1, result);
        } finally {
            modelDA.deleteObject(source);
            modelDA.deleteObject(target);
            modelDA.deleteConnector(con);
            modelDA.closeRepository();
        }
    }

 

    /**
     * Test of updateConnector method, of class ModelDA.
     */
    @Test
    public void testUpdateConnector() throws Exception {
        ModelDA modelDA = new ModelDA();
        TObject source = new TObject(true); // create the source object first
        TObject target = new TObject(true); // create the target object next
        TConnector con = new TConnector(true); // initialise with a guid
        try {
            modelDA.openRepository();
            
            source.setName("sourceObj");
            source.setStereotype("Oracle-Procedure");
            source.setObject_Type("Class");
            source.setPackage_ID(1);
            source.setNote("A procedure");
            
            target.setName("targetObj");
            target.setStereotype("Oracle-Procedure");
            target.setObject_Type("Class");
            target.setPackage_ID(1);
            target.setNote("A procedure");
                        
            modelDA.createObject(source);
            modelDA.createObject(target);
            
            System.out.println("createConnector");

            con.setStart_Object_ID(source.getObject_ID());
            con.setEnd_Object_ID(target.getObject_ID());
            con.setName("test-connector");
            con.setDirection("source->destination");
            con.setConnector_Type("Association");
            
            modelDA.createConnector(con);
            
            TConnector connector = modelDA.retrieveConnectorByName(con);
            connector.setName(connector.getName() + "-changed");
            int result = modelDA.updateConnector(connector);
            assertEquals(1, result);
            
        } finally {
            modelDA.deleteObject(source);
            modelDA.deleteObject(target);
            modelDA.deleteConnector(con);
            modelDA.closeRepository();
        }

    }
    
    /**
     * Test of createObjectConstraint method, of class ModelDA.
     */
    @Test
    public void testCreateObjectConstraint() throws Exception {
        ModelDA modelDA = new ModelDA();
        TObject obj = new TObject(true); // create a new object first
        TObjectconstraint constraint = new TObjectconstraint(); // initialise with a guid
        try {
            obj.setName("Obj1");
            obj.setStereotype("Table");
            obj.setObject_Type("Class");
            obj.setPackage_ID(1);
            obj.setNote("The CLU table");
            modelDA.openRepository();
            modelDA.createObject(obj);
            System.out.println("createObjectConstraint");
            int objID = obj.getObject_ID();

            constraint.setObject_ID(objID);
            constraint.setConstraint("Constraint");
            constraint.setConstraintType("Primary-Key");
            constraint.setNotes("Created: " + Calendar.getInstance().getTime());
            int result = modelDA.createObjectConstraint(constraint);
            assertEquals(1, result);
        } finally {
            modelDA.deleteObject(obj);
            modelDA.deleteObjectConstraint(constraint);
            modelDA.closeRepository();
        }

    }
    /**
     * Test of retrieveObjectConstraintByName method, of class ModelDA.
     */
    @Test
    public void testRetrieveObjectConstraintByName() throws Exception {
        ModelDA modelDA = new ModelDA();
        TObject obj = new TObject(true); // create a new object first
        TObjectconstraint constraint = new TObjectconstraint(); // initialise with a guid
        try {
            obj.setName("Obj1");
            obj.setStereotype("Table");
            obj.setObject_Type("Class");
            obj.setPackage_ID(1);
            obj.setNote("The CLU table");
            modelDA.openRepository();
            modelDA.createObject(obj);
            System.out.println("createObjectConstraint");
            int objID = obj.getObject_ID();

            constraint.setObject_ID(objID);
            constraint.setConstraint("Constraint");
            constraint.setConstraintType("Primary-Key");
            constraint.setNotes("Created: " + Calendar.getInstance().getTime());
            modelDA.createObjectConstraint(constraint);
            
            TObjectconstraint constr = new TObjectconstraint();
            constr.setConstraint("Constraint");
            constr.setObject_ID(objID);
            TObjectconstraint result = modelDA.retrieveObjectConstraintByName(constr);
            
            assertEquals("Primary-Key", result.getConstraintType());
        } finally {
            modelDA.deleteObject(obj);
            modelDA.deleteObjectConstraint(constraint);
            modelDA.closeRepository();
        }
    }

    /**
     * Test of updateObjectConstraint method, of class ModelDA.
     */
    @Test
    public void testUpdateObjectConstraint() throws Exception {
        ModelDA modelDA = new ModelDA();
        TObject obj = new TObject(true); // create a new object first
        TObjectconstraint constraint = new TObjectconstraint(); // initialise with a guid
        try {
            obj.setName("Obj1");
            obj.setStereotype("Table");
            obj.setObject_Type("Class");
            obj.setPackage_ID(1);
            obj.setNote("The CLU table");
            modelDA.openRepository();
            modelDA.createObject(obj);
            System.out.println("createObjectConstraint");
            int objID = obj.getObject_ID();

            constraint.setObject_ID(objID);
            constraint.setConstraint("Constraint");
            constraint.setConstraintType("Primary-Key");
            constraint.setNotes("Created: " + Calendar.getInstance().getTime());
            modelDA.createObjectConstraint(constraint);
            
            TObjectconstraint constr = new TObjectconstraint();
            constr.setConstraint("Constraint");
            constr.setObject_ID(objID);
            TObjectconstraint result = modelDA.retrieveObjectConstraintByName(constr);
            result.setConstraintType("Foreign-Key");
            result.appendNotes("Updated: " + Calendar.getInstance().getTime());
        } finally {
            modelDA.deleteObject(obj);
            modelDA.deleteObjectConstraint(constraint);
            modelDA.closeRepository();
        }
    }
}
