/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paras.io;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.paras.model.TAttribute;
import com.paras.model.TAttributetag;
import com.paras.model.TConnector;
import com.paras.model.TConnectortag;
import com.paras.model.TObject;
import com.paras.model.TObjectconstraint;
import com.paras.model.TObjectproperties;
import com.paras.model.TPackage;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 *
 * @author seethavi
 */
public class ModelDA {

    private SqlSessionFactory sqlSessionFactory;
    private SqlSession session;
    private Properties properties;
    private static Logger logger = Logger.getLogger("co.nz.transpower.io.EAModelBuilder");

    public ModelDA() throws IOException {
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream("/target.xml"));
        properties = sqlSessionFactory.getConfiguration().getVariables();
    }

    /**
     * Opens the EA repository object and initialises the EA packages.
     * @param repName name of the repository
     * @return true if successfully opened false otherwise
     * @throws Exception
     */
    public boolean openRepository() throws Exception {
        session = sqlSessionFactory.openSession();
        // Attempt to open the provided file
        if (logger.isLoggable(Level.INFO)) {
            logger.log(Level.INFO, "Successfully opened model: {0}", session.getConfiguration().getEnvironment().toString());
        }

        return true;
    }

    /**
     * Closes the repository
     * @return true if successfully closed false otherwise
     */
    public boolean closeRepository() {
        if (session != null) {
            session.close();
            return true;
        }
        return false;
    }
    
    public TPackage retrievePackage(String pkgName) {
        TPackage param = new TPackage();
        param.setName(pkgName);
        TPackage pkg = (TPackage) session.selectOne("com.paras.dbmapper.TPackageMapper.select", param);
        return pkg;
    }

    public TObject retrieveObject(TObject obj) {
        List<TObject> result = session.selectList("com.paras.dbmapper.TObjectMapper.selectObjectByNameAndStereotype", obj);
        if(result != null && result.size() >= 1) {
            return result.get(0);
        }
        return null;
    }

    public List<TObject> retrieveNestedObjectListByName(TObject obj) {
        return session.selectList("com.paras.dbmapper.TObjectMapper.selectNestedObjectListByName", obj);
    }
    
    public List<TObject> retrieveNestedMethodsByName(TObject obj) {
        return session.selectList("com.paras.dbmapper.TObjectMapper.selectNestedMethodsByName", obj);
    }

    public TObject retrieveObjectByID(TObject obj) {
        return (TObject) session.selectOne("com.paras.dbmapper.TObjectMapper.selectObjectByID", obj);
    }
    
    public TObject retrieveObjectByGUID(TObject obj) {
        return (TObject) session.selectOne("com.paras.dbmapper.TObjectMapper.selectObjectByGUID", obj);
    }

    private int deleteObjectTag(TObject obj) {
        TObjectproperties objTag = new TObjectproperties(true);
        objTag.setObject_ID(obj.getObject_ID());
        objTag.setProperty("UpdateID");
        return session.delete("com.paras.dbmapper.TObjectpropertiesMapper.delete", objTag);
    }
    
    private int createObjectTag(TObject obj) {
        TObjectproperties objTag = new TObjectproperties(true);
        objTag.setObject_ID(obj.getObject_ID());
        objTag.setProperty("UpdateID");
        objTag.setValue("0");
        return session.insert("com.paras.dbmapper.TObjectpropertiesMapper.insertSelective", objTag);

    }

    private int updateObjectTag(TObject obj) {
        TObjectproperties objTag = new TObjectproperties();
        objTag.setObject_ID(obj.getObject_ID());
        objTag.setProperty("UpdateID");
        TObjectproperties prop = (TObjectproperties) session.selectOne("com.paras.dbmapper.TObjectpropertiesMapper.select", objTag);
        if (prop == null) {
            return 0;
        }
        int updateID = Integer.valueOf(prop.getValue());
        updateID++; // increment the update id
        prop.setValue(String.valueOf(updateID));
        return session.update("com.paras.dbmapper.TObjectpropertiesMapper.updateSelective", prop);
    }

    public int createObject(TObject obj) {
        int result = session.insert("com.paras.dbmapper.TObjectMapper.insertSelective", obj);
        if(result == 1) {
            Integer id = (Integer) session.selectOne("com.paras.dbmapper.TObjectMapper.selectID", obj);
            obj.setObject_ID(id);
        }
        createObjectTag(obj);
        session.commit();
        return result;
    }
    
    public int deleteObject(TObject obj) {
        int result = session.delete("com.paras.dbmapper.TObjectMapper.delete", obj);
        deleteObjectTag(obj);
        session.commit();
        return result;
    }

    public int updateObject(TObject obj) {
        int result = session.update("com.paras.dbmapper.TObjectMapper.updateSelective", obj);
        if (updateObjectTag(obj) == 0) { // if tag does not exist, then create one
            createObjectTag(obj);
        }
        session.commit();
        return result;
    }

    public TAttribute retrieveAttribute(TAttribute attr) {
        return (TAttribute) session.selectOne("com.paras.dbmapper.TAttributeMapper.selectAttributeByName", attr);
    }

    public List<TAttribute> retrieveAttributeList(TObject object) {
        return session.selectList("com.paras.dbmapper.TAttributeMapper.selectAttributeList", object);
    }

    public Integer retrieveAttributeCount(TObject object) {
        return (Integer) session.selectOne("com.paras.dbmapper.TAttributeMapper.selectAttributeCount", object);
    }

    private int createAttributeTag(TAttribute attr) {
        TAttributetag attrTag = new TAttributetag(true);
        attrTag.setElementID(attr.getID());
        attrTag.setProperty("UpdateID");
        attrTag.setVALUE("0");
        return session.insert("com.paras.dbmapper.TAttributetagMapper.insertSelective", attrTag);

    }
    
    private int deleteAttributeTag(TAttribute attr) {
        TAttributetag attrTag = new TAttributetag();
        attrTag.setElementID(attr.getID());
        return session.delete("com.paras.dbmapper.TAttributetagMapper.delete", attrTag);

    }

    private int updateAttributeTag(TAttribute attr) {
        TAttributetag param = new TAttributetag();
        param.setElementID(attr.getID());
        param.setProperty("UpdateID");
        TAttributetag attrTag = (TAttributetag) session.selectOne("com.paras.dbmapper.TAttributetagMapper.select", param);
        if (attrTag == null) {
            return 0;
        }
        int updateID = Integer.valueOf(attrTag.getVALUE());
        updateID++; // increment the update id
        attrTag.setVALUE(String.valueOf(updateID));
        return session.update("com.paras.dbmapper.TAttributetagMapper.updateSelective", attrTag);
    }

    public int createAttribute(TAttribute attr) {
        int result = session.insert("com.paras.dbmapper.TAttributeMapper.insertSelective", attr);
        if(result == 1) {
            Integer id = (Integer) session.selectOne("com.paras.dbmapper.TAttributeMapper.selectID", attr);
            attr.setID(id);
        }
        createAttributeTag(attr);
        session.commit();
        return result;
    }
    
    public int deleteAttribute(TAttribute attr) {
        int result = session.delete("com.paras.dbmapper.TAttributeMapper.delete", attr);
        deleteAttributeTag(attr);
        session.commit();
        return result;
    }

    public int updateAttribute(TAttribute attr) {
        int result = session.update("com.paras.dbmapper.TAttributeMapper.updateSelective", attr);
        if (updateAttributeTag(attr) == 0) { // if tag does not exist, then create one
            createAttributeTag(attr);
        }
        session.commit();
        return result;
    }

    public TConnector retrieveConnectorByName(TConnector con) {
        return (TConnector) session.selectOne("com.paras.dbmapper.TConnectorMapper.select", con);
    }

    private int createConnectorTag(TConnector con) {
        TConnectortag conTag = new TConnectortag(true);
        conTag.setElementID(con.getConnector_ID());
        conTag.setProperty("UpdateID");
        conTag.setVALUE("0");
        return session.insert("com.paras.dbmapper.TConnectortagMapper.insertSelective", conTag);

    }
    
    private int deleteConnectorTag(TConnector con) {
        TConnectortag conTag = new TConnectortag();
        conTag.setElementID(con.getConnector_ID());
        return session.delete("com.paras.dbmapper.TConnectortagMapper.delete", conTag);

    }


    private int updateConnectorTag(TConnector con) {
        TConnectortag param = new TConnectortag();
        param.setElementID(con.getConnector_ID());
        param.setProperty("UpdateID");
        TConnectortag conTag = (TConnectortag) session.selectOne("com.paras.dbmapper.TConnectortagMapper.select", param);
        if (conTag == null) {
            return 0;
        }
        int updateID = Integer.valueOf(conTag.getVALUE());
        updateID++; // increment the update id
        conTag.setVALUE(String.valueOf(updateID));
        return session.update("com.paras.dbmapper.TConnectortagMapper.updateSelective", conTag);
    }

    public int createConnector(TConnector con) {
        int result = session.insert("com.paras.dbmapper.TConnectorMapper.insertSelective", con);
        createConnectorTag(con);
        session.commit();
        return result;
    }

    public int updateConnector(TConnector con) {
        int result = session.update("com.paras.dbmapper.TConnectorMapper.updateSelective", con);
        if (updateConnectorTag(con) == 0) { // if tag does not exist, then create one
            createConnectorTag(con);
        }
        session.commit();
        return result;
    }
    
    public int deleteConnector(TConnector con) {
        int result = session.delete("com.paras.dbmapper.TConnectorMapper.delete", con);
        deleteConnectorTag(con);
        session.commit();
        return result;
    }


    public TObjectconstraint retrieveObjectConstraintByName(TObjectconstraint con) {
        return (TObjectconstraint) session.selectOne("com.paras.dbmapper.TObjectconstraintMapper.select", con);
    }

    public int createObjectConstraint(TObjectconstraint con) {
        int result = session.insert("com.paras.dbmapper.TObjectconstraintMapper.insertSelective", con);
        session.commit();
        return result;
    }
    
    public int deleteObjectConstraint(TObjectconstraint con) {
        int result = session.insert("com.paras.dbmapper.TObjectconstraintMapper.delete", con);
        session.commit();
        return result;
    }

    public int updateObjectConstraint(TObjectconstraint con) {
        int result = session.update("com.paras.dbmapper.TObjectconstraintMapper.updateSelective", con);
        session.commit();
        return result;
    }
}
