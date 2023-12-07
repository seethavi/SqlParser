/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paras.eadiff.tool;

import java.util.List;
import com.paras.model.TPackage;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.util.UUID;
import com.paras.model.TObject;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
public class ModelDifferencerTest {

    public ModelDifferencerTest() {
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
     * Test of createDifferenceFile method, of class ModelDifferencer.
     */
    @Test
    public void testCreateDifferenceFile() throws Exception {
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream("source.xml"));
        SqlSession session = sqlSessionFactory.openSession();

        try {
            List<TPackage> sourcePkgs = session.selectList("com.paras.dbmapper.TPackageMapper.selectPackages");
            Integer maxPkgId = (Integer) session.selectOne("com.paras.dbmapper.TPackageMapper.selectMaxPkgID");
            TPackage sourcePkg = sourcePkgs.get(0);
            System.out.println("Inserting into " + sourcePkg.getName());
            TPackage pkg = new TPackage();
            pkg.setParent_ID(sourcePkg.getPackage_ID());
            pkg.setPackage_ID(maxPkgId + 1);
            pkg.setName("CREATED_PROGRAMMATICALLY");
            pkg.setEa_guid("{" + UUID.randomUUID() + "}");
            pkg.setVersion("3.0-RE");
            
            
            TObject obj = new TObject();
            //obj.setObject_ID(maxObjId);
            obj.setObject_Type("Package");
            obj.setName(pkg.getName());
            obj.setPackage_ID(pkg.getParent_ID());
            obj.setEa_guid(pkg.getEa_guid());
            obj.setVersion(pkg.getVersion());
            session.insert("com.paras.dbmapper.TPackageMapper.insertSelective", pkg);
            session.insert("com.paras.dbmapper.TObjectMapper.insertSelective", obj);
            session.commit();
            TPackage insPkg = (TPackage) session.selectOne("com.paras.dbmapper.TPackageMapper.selectPackage", pkg.getEa_guid());
            System.out.println("Version number is + " + insPkg.getVersion());

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception not expected");
        } finally {
            session.close();
        }

    }
}
