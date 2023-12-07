/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paras.eadiff.tool;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.paras.io.OutputWriter;
import com.paras.model.TAttribute;
import com.paras.model.TAttributetag;
import com.paras.model.TConnector;
import com.paras.model.TConnectortag;
import com.paras.model.TObject;
import com.paras.model.TObjectconstraint;
import com.paras.model.TObjectproperties;
import com.paras.model.TOperation;
import com.paras.model.TOperationparams;
import com.paras.model.TOperationtag;
import com.paras.model.TPackage;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 *
 * @author seethavi
 */
public class ModelDifferencer {
    // Avoiding the use of singleton to implement the factory
    // ideally the daFactory variable has to be dependency injected. Because we 
    // are not using a dependency injection
    // framework this is force-initialised as part of the tool initialisation

    private final String sourceFileName;
    private final String targetFileName;
    private final SqlSessionFactory srcSqlFactory;
    private final SqlSessionFactory tgtSqlFactory;
    private final SqlSession sourceSession;
    private final SqlSession targetSession;
    private OutputWriter out;
    private int refCounter;
    private int objectsProcessed;

    public ModelDifferencer(String sourceFile, String targetFile) throws Exception {
        refCounter = 1;
        objectsProcessed = 0;
        sourceFileName = sourceFile;
        targetFileName = targetFile;
        srcSqlFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream(sourceFile));
        tgtSqlFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream(targetFile));
        sourceSession = srcSqlFactory.openSession();
        targetSession = tgtSqlFactory.openSession();
        Integer srcCount = (Integer) sourceSession.selectOne("com.paras.dbmapper.TObjectMapper.countObjects");
        Integer tgtCount = (Integer) targetSession.selectOne("com.paras.dbmapper.TObjectMapper.countObjects");
        System.out.println("Src Objects = " + srcCount);
        System.out.println("Tgt Objects = " + tgtCount);
    }

    private void print(String indent, String message) {
      System.out.print(indent);
      System.out.println(message);
    }
    
    public void createDifferenceFile(String fileName) throws Exception {
        FileWriter writer = new FileWriter(new File(fileName));
        this.out = new OutputWriter(writer);
        try {
            TPackage startPkgSrc = (TPackage) sourceSession.selectOne("com.paras.dbmapper.TPackageMapper.selectModelPackage");
            TPackage startPkgTgt = (TPackage) targetSession.selectOne("com.paras.dbmapper.TPackageMapper.selectModelPackage");
            this.out.writeln("<difference-list source='" + sourceFileName + "' target='" + targetFileName + "'>");
            this.out.writeln("   <packages>");
            print("", "Comparing packages: " + startPkgSrc.getName() + " - " + startPkgTgt.getName());
            comparePackages(startPkgSrc, startPkgTgt, "      ");
            this.out.writeln("   </packages>");
            this.out.writeln("</difference-list>");
        } finally {
            System.out.println("Objects Processed = " + objectsProcessed);
            sourceSession.close();
            targetSession.close();
            this.out.close();
        }
    }

    public void comparePackages(TPackage sourcePkg, TPackage targetPkg, String indent) throws Exception {
        List<TPackage> sourceBackLog = new ArrayList<TPackage>();
        List<TPackage> targetBackLog = new ArrayList<TPackage>();
        List<TPackage> srcIntersectList = new ArrayList<TPackage>(); // create an intersect list for src as the package IDs are specific to the given model
        List<TPackage> tgtIntersectList = new ArrayList<TPackage>(); // create an intersect list for the target 

        List<TPackage> sourcePkgs = sourceSession.selectList("com.paras.dbmapper.TPackageMapper.selectNestedPackages", sourcePkg);
        List<TPackage> targetPkgs = targetSession.selectList("com.paras.dbmapper.TPackageMapper.selectNestedPackages", targetPkg);

        // do set operations
        Set<TPackage> difference = (sourcePkgs != null ? new HashSet<TPackage>(sourcePkgs) : new HashSet<TPackage>());
        if (targetPkgs != null) {
            difference.removeAll(targetPkgs);
        }

        Set<TPackage> reverseDifference = (targetPkgs != null ? new HashSet<TPackage>(targetPkgs) : new HashSet<TPackage>());
        if (sourcePkgs != null) {
            reverseDifference.removeAll(sourcePkgs);
        }

        Set<TPackage> intersection = (sourcePkgs != null ? new HashSet<TPackage>(sourcePkgs) : new HashSet<TPackage>());
        if (targetPkgs != null) {
            intersection.retainAll(targetPkgs);
        }
        if (intersection != null) {
            srcIntersectList.addAll(0, intersection);
        }

        Set<TPackage> reverseIntersection = (targetPkgs != null ? new HashSet<TPackage>(targetPkgs) : new HashSet<TPackage>());
        if (targetPkgs != null) {
            intersection.retainAll(targetPkgs);
        }
        if (reverseIntersection != null) {
            tgtIntersectList.addAll(0, reverseIntersection);
        }

        while (!srcIntersectList.isEmpty()) {
            TPackage srcPkg = srcIntersectList.remove(0); // remove the first package in the list
            TPackage tgtPkg = tgtIntersectList.remove(0);
            print(indent, "Comparing packages: " + srcPkg.getName() + " - " + tgtPkg.getName());

            this.out.write(indent).writeln("<!-- Packages common to both source and target -->");
 
            this.out.write(indent).writeln("      <package name='" + srcPkg.getName() + "' ref='REF" + refCounter++ + "' guid='" + srcPkg.getEa_guid() + "'>");
            this.out.write(indent).writeln("         <objects>");
            compareObjects(srcPkg, tgtPkg, indent + "         ");
            this.out.write(indent).writeln("         </objects>");            
            comparePackages(srcPkg, tgtPkg, indent + "   ");       
            this.out.write(indent).writeln("      </package>");
        }

        while (!sourceBackLog.isEmpty()) {
            this.out.writeln("   <!-- Packages and objects in source but not in target --> ");
            TPackage srcPkg = sourceBackLog.remove(0);
            this.out.writeln("      <package name='" + srcPkg.getName() + "' ref='REF" + refCounter++ + "' guid='" + srcPkg.getEa_guid() + "'>");
            this.out.writeln("         <diff source='exists' target=''/>");
            processPackage(sourceSession, srcPkg, "         ", true);
            this.out.writeln("      </package>");
            List<TPackage> srcPkgs = sourceSession.selectList("com.paras.dbmapper.TPackageMapper.selectNestedPackages", srcPkg); // retrieve nested packages
            if (srcPkgs != null) {
                sourceBackLog.addAll(srcPkgs); // more packages to recursively traverse down
            }
        }
        while (!targetBackLog.isEmpty()) {
            this.out.writeln("   <!-- Packages and objects in target but not in source  --> ");
            TPackage tgtPkg = targetBackLog.remove(0);
            this.out.writeln("      <package name='" + tgtPkg.getName() + "' ref='REF" + refCounter++ + "' guid='" + tgtPkg.getEa_guid() + "'>");
            this.out.writeln("         <diff source='' target='exists'/>");
            processPackage(targetSession, tgtPkg, "         ", false);
            this.out.writeln("      </package>");
            List<TPackage> tgtPkgs = targetSession.selectList("com.paras.dbmapper.TPackageMapper.selectNestedPackages", tgtPkg);
            if (targetPkgs != null) {
                targetBackLog.addAll(tgtPkgs); // more packages to recursively traverse down
            }
        }
    }
    /*
    public void comparePackages(TPackage sourcePkg, TPackage targetPkg) throws Exception {
    List<TPackage> sourceBackLog = new ArrayList<TPackage>();
    List<TPackage> targetBackLog = new ArrayList<TPackage>();
    List<TPackage> srcIntersectList = new ArrayList<TPackage>(); // create an intersect list for src as the package IDs are specific to the given model
    List<TPackage> tgtIntersectList = new ArrayList<TPackage>(); // create an intersect list for the target 
    
    srcIntersectList.add(sourcePkg);
    tgtIntersectList.add(targetPkg);
    TPackage srcPkg = null;
    TPackage tgtPkg = null;
    
    // process the intersect packages
    while (!srcIntersectList.isEmpty()) { // the two intersect lists should always be the same size
    // retrieve subpackages first for each item in the intersect list
    srcPkg = srcIntersectList.remove(0);
    tgtPkg = tgtIntersectList.remove(0);
    List<TPackage> sourcePkgs = sourceSession.selectList("com.paras.dbmapper.TPackageMapper.selectNestedPackages", srcPkg);
    List<TPackage> targetPkgs = targetSession.selectList("com.paras.dbmapper.TPackageMapper.selectNestedPackages", tgtPkg);
    
    // do set operations
    Set<TPackage> difference = (sourcePkgs != null ? new HashSet<TPackage>(sourcePkgs) : new HashSet<TPackage>());
    if (targetPkgs != null) {
    difference.removeAll(targetPkgs);
    }
    
    Set<TPackage> reverseDifference = (targetPkgs != null ? new HashSet<TPackage>(targetPkgs) : new HashSet<TPackage>());
    if (sourcePkgs != null) {
    reverseDifference.removeAll(sourcePkgs);
    }
    
    Set<TPackage> intersection = (sourcePkgs != null ? new HashSet<TPackage>(sourcePkgs) : new HashSet<TPackage>());
    if (targetPkgs != null) {
    intersection.retainAll(targetPkgs);
    }
    if (intersection != null) {
    srcIntersectList.addAll(0, intersection);
    }
    
    Set<TPackage> reverseIntersection = (targetPkgs != null ? new HashSet<TPackage>(targetPkgs) : new HashSet<TPackage>());
    if (targetPkgs != null) {
    intersection.retainAll(targetPkgs);
    }
    if (reverseIntersection != null) {
    tgtIntersectList.addAll(0, reverseIntersection);
    }
    
    if (!srcIntersectList.isEmpty()) {
    //srcPkg = srcIntersectList.remove(0); // remove the first package in the list
    //tgtPkg = tgtIntersectList.remove(0);
    this.out.writeln("<!-- Packages common to both source and target -->");
    this.out.writeln("      <package name='" + srcPkg.getName() + "' guid='" + srcPkg.getEa_guid() + "'>");
    this.out.writeln("         <objects>");
    compareObjects(srcPkg, tgtPkg, "         ");
    this.out.writeln("         </objects>");
    this.out.writeln("      </package>");
    }
    }
    int i = 1;
    while (!sourceBackLog.isEmpty()) {
    this.out.writeln("   <!-- Packages and objects in source but not in target --> ");
    srcPkg = sourceBackLog.remove(0);
    this.out.writeln("      <package name='" + srcPkg.getName() + "' ref='REF" + refCounter++ + "' guid='" + srcPkg.getEa_guid() + "'>");
    this.out.writeln("         <diff source='exists' target=''/>");
    processPackage(sourceSession, srcPkg, "         ", true);
    this.out.writeln("      </package>");
    List<TPackage> sourcePkgs = sourceSession.selectList("com.paras.dbmapper.TPackageMapper.selectNestedPackages", srcPkg); // retrieve nested packages
    if (sourcePkgs != null) {
    sourceBackLog.addAll(sourcePkgs); // more packages to recursively traverse down
    }
    i++;
    }
    while (!targetBackLog.isEmpty()) {
    this.out.writeln("   <!-- Packages and objects in target but not in source  --> ");
    tgtPkg = targetBackLog.remove(0);
    this.out.writeln("      <package name='" + tgtPkg.getName() + "' ref='REF" + refCounter++ + "' guid='" + tgtPkg.getEa_guid() + "'>");
    this.out.writeln("         <diff source='' target='exists'/>");
    processPackage(targetSession, tgtPkg, "         ", false);
    this.out.writeln("      </package>");
    List<TPackage> targetPkgs = targetSession.selectList("com.paras.dbmapper.TPackageMapper.selectNestedPackages", tgtPkg);
    if (targetPkgs != null) {
    targetBackLog.addAll(targetPkgs); // more packages to recursively traverse down
    }
    i++;
    }
    }
     */

    public void processPackage(SqlSession session, TPackage pkg, String indent, boolean isSrc) throws Exception {

        List<TObject> objects = session.selectList("com.paras.dbmapper.TObjectMapper.selectObjects", pkg);
        List<TObject> backlog = new ArrayList<TObject>();

        backlog.addAll(objects);

        while (!backlog.isEmpty()) {
            TObject obj = backlog.remove(0);
            this.out.write(indent).writeln("   <object name='" + obj.getName() + "' ref='REF" + refCounter++ + "' stereotype='" + (obj.getStereotype() == null ? "" : obj.getStereotype()) + "' guid='" + obj.getEa_guid() + "'>");
            if (isSrc) {
                this.out.write(indent).writeln("      <diff source='exists' target=''/>");
            } else {
                this.out.write(indent).writeln("      <diff source='' target='exists'/>");
            }
            this.out.write(indent).writeln("   </object>");
            objects = session.selectList("com.paras.dbmapper.TObjectMapper.selectNestedObjects", obj);
            if (objects != null && !objects.isEmpty()) {
                backlog.addAll(0, objects);
            }
        }
    }

    private boolean equal(String src, String tgt) {
        if (src == null && tgt == null) {
            return true;
        } else if (src != null) {
            return src.equals(tgt);
        } else {
            return tgt.equals(src);
        }
    }
    
    private boolean equalsIgnoreCase(String src, String tgt) {
        if (src == null && tgt == null) {
            return true;
        } else if (src != null) {
            return src.equalsIgnoreCase(tgt);
        } else {
            return tgt.equalsIgnoreCase(src);
        }
    }

    private void compareAttributes(TObject src, TObject tgt, String indent) throws Exception {
        List<TAttribute> sourceAttributes = sourceSession.selectList("com.paras.dbmapper.TAttributeMapper.selectAttributeList", src);
        List<TAttribute> targetAttributes = targetSession.selectList("com.paras.dbmapper.TAttributeMapper.selectAttributeList", tgt);

        Set<TAttribute> difference = new HashSet<TAttribute>(sourceAttributes);
        difference.removeAll(targetAttributes);

        for (TAttribute attr : difference) {
            this.out.write(indent).writeln("<diff attribute='" + attr.getName() + "' source='exists' target=''/>");
        }

        Set<TAttribute> reverseDifference = new HashSet<TAttribute>(targetAttributes);
        reverseDifference.removeAll(sourceAttributes);

        for (TAttribute attr : reverseDifference) {
            this.out.write(indent).writeln("<diff attribute='" + attr.getName() + "' source='' target='exists'/>");
        }

        Set<TAttribute> intersection = new HashSet<TAttribute>(sourceAttributes);
        intersection.retainAll(targetAttributes);

        for (TAttribute attr : intersection) {
            TAttribute srcAttr = sourceAttributes.get(sourceAttributes.indexOf(attr));
            TAttribute tgtAttr = targetAttributes.get(targetAttributes.indexOf(attr));
            if (!equal(srcAttr.getType(), tgtAttr.getType())) {
                this.out.write(indent).writeln("<diff attribute='type' source='" + srcAttr.getType() + "' target='" + tgtAttr.getType() + "'/>");
            }
            TAttributetag attrTag = new TAttributetag();
            attrTag.setElementID(srcAttr.getID());
            attrTag.setProperty("UpdateID");
            TAttributetag srcAttrTag = (TAttributetag) sourceSession.selectOne("com.paras.dbmapper.TAttributetagMapper.select", attrTag);
            attrTag.setElementID(tgtAttr.getID());
            TAttributetag tgtAttrTag = (TAttributetag) targetSession.selectOne("com.paras.dbmapper.TAttributetagMapper.select", attrTag);
            String srcTag = (srcAttrTag != null) ? srcAttrTag.getVALUE() : "";
            String tgtTag = (tgtAttrTag != null) ? tgtAttrTag.getVALUE() : "";
            if (!equal(srcTag, tgtTag)) {
                this.out.write(indent).writeln("<diff tag='UpdateID' source='" + srcTag + "' target='" + tgtTag + "'/>");
            }
        }
    }

    private void compareTaggedValues(TObject src, TObject tgt, String indent) throws Exception {
        TObjectproperties objTag = new TObjectproperties();
        objTag.setObject_ID(src.getObject_ID());
        objTag.setProperty("UpdateID");
        TObjectproperties srcObjTag = (TObjectproperties) sourceSession.selectOne("com.paras.dbmapper.TObjectpropertiesMapper.select", objTag);
        objTag.setObject_ID(tgt.getObject_ID());
        TObjectproperties tgtObjTag = (TObjectproperties) targetSession.selectOne("com.paras.dbmapper.TObjectpropertiesMapper.select", objTag);
        String srcTag = (srcObjTag != null) ? srcObjTag.getValue() : "";
        String tgtTag = (tgtObjTag != null) ? tgtObjTag.getValue() : "";
        if (!equal(srcTag, tgtTag)) {
            this.out.write(indent).writeln("<diff tag='UpdateID' source='" + srcTag + "' target='" + tgtTag + "'/>");
        }
    }

    private void compareConnectors(TObject src, TObject tgt, String indent) throws Exception {
        List<TConnector> sourceConnectors = sourceSession.selectList("com.paras.dbmapper.TConnectorMapper.selectAll", src);
        List<TConnector> targetConnectors = targetSession.selectList("com.paras.dbmapper.TConnectorMapper.selectAll", tgt);

        Set<TConnector> difference = new HashSet<TConnector>(sourceConnectors);
        difference.removeAll(targetConnectors);

        for (TConnector conn : difference) {
            TObject startObject = (TObject) sourceSession.selectOne("com.paras.dbmapper.TObjectMapper.selectObjectByID", conn.getStart_Object_ID());
            TObject endObject = (TObject) sourceSession.selectOne("com.paras.dbmapper.TObjectMapper.selectObjectByID", conn.getEnd_Object_ID());
            this.out.write(indent).writeln("<connector startObj='" + startObject.getName() + "' endObject='" + endObject.getName() + "' name='" + conn.getName() + "' stereotype='" + conn.getStereotype() + "' guid='" + conn.getEa_guid() + "'>");
            this.out.write(indent).writeln("   <diff source='exists' target=''/>");
            this.out.write(indent).writeln("</connector>");
        }

        Set<TConnector> reverseDifference = new HashSet<TConnector>(targetConnectors);
        reverseDifference.removeAll(sourceConnectors);

        for (TConnector conn : reverseDifference) {
            TObject startObject = (TObject) targetSession.selectOne("com.paras.dbmapper.TObjectMapper.selectObjectByID", conn.getStart_Object_ID());
            TObject endObject = (TObject) targetSession.selectOne("com.paras.dbmapper.TObjectMapper.selectObjectByID", conn.getEnd_Object_ID());
            this.out.write(indent).writeln("<connector startObj='" + startObject.getName() + "' endObject='" + endObject.getName() + "' name='" + conn.getName() + "' stereotype='" + conn.getStereotype() + "' guid='" + conn.getEa_guid() + "'>");
            this.out.write(indent).writeln("   <diff source='' target='exists'/>");
            this.out.write(indent).writeln("</connector>");
        }

        Set<TConnector> intersection = new HashSet<TConnector>(sourceConnectors);
        intersection.retainAll(targetConnectors);

        for (TConnector conn : intersection) {
            TConnector srcConn = sourceConnectors.get(sourceConnectors.indexOf(conn));
            TConnector tgtConn = targetConnectors.get(targetConnectors.indexOf(conn));
            TObject srcStartObject = (TObject) sourceSession.selectOne("com.paras.dbmapper.TObjectMapper.selectObjectByID", srcConn.getStart_Object_ID());
            TObject srcEndObject = (TObject) sourceSession.selectOne("com.paras.dbmapper.TObjectMapper.selectObjectByID", srcConn.getEnd_Object_ID());

            TObject tgtStartObject = (TObject) targetSession.selectOne("com.paras.dbmapper.TObjectMapper.selectObjectByID", tgtConn.getStart_Object_ID());
            TObject tgtEndObject = (TObject) targetSession.selectOne("com.paras.dbmapper.TObjectMapper.selectObjectByID", tgtConn.getEnd_Object_ID());

            if (equalsIgnoreCase(srcEndObject.getName(), tgtEndObject.getName())) {
                this.out.write(indent).writeln("<connector startObj='" + srcStartObject.getName() + "' endObject='" + srcEndObject.getName() + "' name='" + conn.getName() + "' stereotype='" + conn.getStereotype() + "' guid='" + conn.getEa_guid() + "'>");
            } else {
                this.out.write(indent).writeln("<connector name='" + conn.getName() + "' stereotype='" + conn.getStereotype() + "' guid='" + conn.getEa_guid() + "'>");
                this.out.write(indent).writeln("   <diff property='endobject' source='" + srcEndObject.getName() + "' target='" + tgtEndObject.getName() + "'/>");
            }
            if (!equal(srcConn.getConnector_Type(), tgtConn.getConnector_Type())) {
                this.out.write(indent).writeln("   <diff property='type' source='" + srcConn.getConnector_Type() + "' target='" + tgtConn.getConnector_Type() + "'/>");
            }
            if (!equal(srcConn.getStereotype(), tgtConn.getStereotype())) {
                this.out.write(indent).writeln("   <diff property='stereotype' source='" + srcConn.getStereotype() + "' target='" + tgtConn.getStereotype() + "'/>");
            }
            TConnectortag connTag = new TConnectortag();
            connTag.setElementID(srcConn.getConnector_ID());
            connTag.setProperty("UpdateID");
            TConnectortag srcConnTag = (TConnectortag) sourceSession.selectOne("com.paras.dbmapper.TConnectortagMapper.select", connTag);
            connTag.setElementID(tgtConn.getConnector_ID());
            TConnectortag tgtConnTag = (TConnectortag) targetSession.selectOne("com.paras.dbmapper.TConnectortagMapper.select", connTag);
            String srcTag = (srcConnTag != null) ? srcConnTag.getVALUE() : "";
            String tgtTag = (tgtConnTag != null) ? tgtConnTag.getVALUE() : "";
            if (!equal(srcTag, tgtTag)) {
                this.out.write(indent).writeln("   <diff tag='UpdateID' source='" + srcTag + "' target='" + tgtTag + "'/>");
            }

            this.out.write(indent).writeln("</connector>");
        }
    }

    private void compareOperations(TObject src, TObject tgt, String indent) throws Exception {
        List<TOperation> sourceOperations = sourceSession.selectList("com.paras.dbmapper.TOperationMapper.select", src);
        List<TOperation> targetOperations = targetSession.selectList("com.paras.dbmapper.TOperationMapper.select", tgt);

        Set<TOperation> difference = new HashSet<TOperation>(sourceOperations);
        difference.removeAll(targetOperations);

        for (TOperation oper : difference) {
            this.out.write(indent).writeln("<operation name='" + oper.getName() + "' stereotype='" + oper.getStereotype() + "' guid='" + oper.getEa_guid() + "'>");
            this.out.write(indent).writeln("   <diff source='exists' target=''/>");
            this.out.write(indent).writeln("</operation>");
        }

        Set<TOperation> reverseDifference = new HashSet<TOperation>(targetOperations);
        reverseDifference.removeAll(sourceOperations);

        for (TOperation oper : reverseDifference) {
            this.out.write(indent).writeln("<operation name='" + oper.getName() + "' stereotype='" + oper.getStereotype() + "' guid='" + oper.getEa_guid() + "'>");
            this.out.write(indent).writeln("   <diff source='' target='exists'/>");
            this.out.write(indent).writeln("</operation>");
        }

        Set<TOperation> intersection = new HashSet<TOperation>(sourceOperations);
        intersection.retainAll(targetOperations);

        for (TOperation oper : intersection) {
            TOperation srcOper = sourceOperations.get(sourceOperations.indexOf(oper));
            TOperation tgtOper = targetOperations.get(targetOperations.indexOf(oper));
            this.out.write(indent).writeln("<operation name='" + oper.getName() + "' stereotype='" + oper.getStereotype() + "' guid='" + oper.getEa_guid() + "'>");
            if (!equal(srcOper.getType(), srcOper.getType())) {
                this.out.write(indent).writeln("<diff property='type' source='" + srcOper.getType() + "' target='" + tgtOper.getType() + "'/>");
            }
            TOperationtag operTag = new TOperationtag();
            operTag.setElementID(srcOper.getOperationID());
            operTag.setProperty("UpdateID");
            TOperationtag srcOperTag = (TOperationtag) sourceSession.selectOne("com.paras.dbmapper.TOperationtagMapper.select", operTag);
            operTag.setElementID(tgtOper.getOperationID());
            TOperationtag tgtOperTag = (TOperationtag) targetSession.selectOne("com.paras.dbmapper.TOperationtagMapper.select", operTag);
            String srcTag = (srcOperTag != null) ? srcOperTag.getVALUE() : "";
            String tgtTag = (tgtOperTag != null) ? tgtOperTag.getVALUE() : "";
            if (!equal(srcTag, tgtTag)) {
                this.out.write(indent).writeln("<diff tag='UpdateID' source='" + srcTag + "' target='" + tgtTag + "'/>");
            }
            compareOperationParams(srcOper, tgtOper, indent);
            this.out.write(indent).writeln("</operation>");
        }
    }

    private void compareOperationParams(TOperation src, TOperation tgt, String indent) throws Exception {
        List<TOperationparams> sourceParams = sourceSession.selectList("com.paras.dbmapper.TOperationparamsMapper.select", src);
        List<TOperationparams> targetParams = targetSession.selectList("com.paras.dbmapper.TOperationparamsMapper.select", tgt);

        Set<TOperationparams> difference = new HashSet<TOperationparams>(sourceParams);
        difference.removeAll(targetParams);

        for (TOperationparams param : difference) {
            this.out.write(indent).writeln("<diff param='" + param.getName() + "' source='exists' target=''/>");
        }

        Set<TOperationparams> reverseDifference = new HashSet<TOperationparams>(targetParams);
        reverseDifference.removeAll(sourceParams);

        for (TOperationparams param : reverseDifference) {
            this.out.write(indent).writeln("<diff param='" + param.getName() + "' source='' target='exists'/>");
        }

        Set<TOperationparams> intersection = new HashSet<TOperationparams>(sourceParams);
        intersection.retainAll(targetParams);

        for (TOperationparams param : intersection) {
            TOperationparams srcParam = sourceParams.get(sourceParams.indexOf(param));
            TOperationparams tgtParam = targetParams.get(targetParams.indexOf(param));
            if (!equal(srcParam.getType(), tgtParam.getType())) {
                this.out.write(indent).writeln("<diff param='type' source='" + srcParam.getType() + "' target='" + tgtParam.getType() + "'/>");
            }
            if (!equal(srcParam.getKind(), tgtParam.getKind())) {
                this.out.write(indent).writeln("<diff param='kind' source='" + srcParam.getKind() + "' target='" + tgtParam.getKind() + "'/>");
            }
        }
    }

    private void compareConstraints(TObject src, TObject tgt, String indent) throws Exception {
        List<TObjectconstraint> sourceConstraints = sourceSession.selectList("com.paras.dbmapper.TObjectconstraintMapper.selectAll", src);
        List<TObjectconstraint> targetConstraints = targetSession.selectList("com.paras.dbmapper.TObjectconstraintMapper.selectAll", tgt);

        Set<TObjectconstraint> difference = new HashSet<TObjectconstraint>(sourceConstraints);
        difference.removeAll(targetConstraints);

        for (TObjectconstraint constraint : difference) {
            this.out.write(indent).writeln("<diff constraint='" + constraint.getConstraint() + "' source='exists' target=''/>");
        }

        Set<TObjectconstraint> reverseDifference = new HashSet<TObjectconstraint>(targetConstraints);
        reverseDifference.removeAll(sourceConstraints);

        for (TObjectconstraint constraint : reverseDifference) {
            this.out.write(indent).writeln("<diff constraint='" + constraint.getConstraint() + "' source='' target='exists'/>");
        }

        Set<TObjectconstraint> intersection = new HashSet<TObjectconstraint>(sourceConstraints);
        intersection.retainAll(targetConstraints);

        for (TObjectconstraint constraint : intersection) {
            TObjectconstraint srcConstraint = sourceConstraints.get(sourceConstraints.indexOf(constraint));
            TObjectconstraint tgtConstraint = targetConstraints.get(targetConstraints.indexOf(constraint));
            if (!equal(srcConstraint.getConstraint(), tgtConstraint.getConstraint())) {
                this.out.write(indent).writeln("<diff property='constraint' source='" + srcConstraint.getConstraint() + "' target='" + tgtConstraint.getConstraint() + "'/>");
            }
            if (!equal(srcConstraint.getConstraintType(), tgtConstraint.getConstraintType())) {
                this.out.write(indent).writeln("<diff property='type' source='" + srcConstraint.getConstraintType() + "' target='" + tgtConstraint.getConstraintType() + "'/>");
            }
            if (!equal(srcConstraint.getNotes(), tgtConstraint.getNotes())) {
                this.out.write(indent).writeln("<diff property='note'>");
                this.out.write(indent).writeln("   <source>");
                this.out.write(indent).write("      <!CDATA[").write(srcConstraint.getNotes()).writeln("]]>");
                this.out.write(indent).writeln("   </source>");
                this.out.write(indent).writeln("   <target>");
                this.out.write(indent).write("      <!CDATA[").write(tgtConstraint.getNotes()).writeln("]]>");
                this.out.write(indent).writeln("   </target>");
                this.out.write(indent).writeln("</diff>");


            }
        }
    }

    private void compareObjectDetail(TObject src, TObject tgt, String indent) throws Exception {
        if (!equal(src.getStereotype(), tgt.getStereotype())) {
            this.out.write(indent).writeln("<diff property='stereotype' source='" + src.getStereotype() + "' target='" + tgt.getStereotype() + "'/>");
        }
        if (!equal(src.getNote(), tgt.getNote())) {
            //this.out.write(indent).writeln("<diff property='note' source=\"" + src.getNote() + "\" target=\"" + tgt.getNote() + "\"/>");
            this.out.write(indent).writeln("<diff property='note'>");
            this.out.write(indent).writeln("   <source>");
            this.out.write(indent).write("      <!CDATA[").write(src.getNote()).writeln("]]>");
            this.out.write(indent).writeln("   </source>");
            this.out.write(indent).writeln("   <target>");
            this.out.write(indent).write("      <!CDATA[").write(tgt.getNote()).writeln("]]>");
            this.out.write(indent).writeln("   </target>");
            this.out.write(indent).writeln("</diff>");
        }
        if (!equal(src.getGenFile(), tgt.getGenFile())) {
            this.out.write(indent).writeln("<diff property='genfile' source='" + src.getGenFile() + "' target='" + tgt.getGenFile() + "'/>");
        }
        compareAttributes(src, tgt, indent);
        compareTaggedValues(src, tgt, indent);
        compareConnectors(src, tgt, indent);
        compareOperations(src, tgt, indent);
        compareConstraints(src, tgt, indent);

    }

    public void compareNestedObjects(TObject src, TObject tgt, String indent) throws Exception {
        compareObjectDetail(src, tgt, indent);

        List<TObject> sourceObjects = sourceSession.selectList("com.paras.dbmapper.TObjectMapper.selectNestedObjects", src);
        List<TObject> targetObjects = targetSession.selectList("com.paras.dbmapper.TObjectMapper.selectNestedObjects", tgt);

        List<TObject> sourceBackLog = new ArrayList<TObject>();
        List<TObject> targetBackLog = new ArrayList<TObject>();
        List<TObject> srcIntersectList = new ArrayList<TObject>();
        List<TObject> tgtIntersectList = new ArrayList<TObject>();

        Set<TObject> difference = (sourceObjects != null ? new HashSet<TObject>(sourceObjects) : new HashSet<TObject>());
        if (targetObjects != null) {
            difference.removeAll(targetObjects);
        }

        Set<TObject> reverseDifference = (targetObjects != null ? new HashSet<TObject>(targetObjects) : new HashSet<TObject>());
        if (sourceObjects != null) {
            reverseDifference.removeAll(sourceObjects);
        }

        Set<TObject> intersection = (sourceObjects != null ? new HashSet<TObject>(sourceObjects) : new HashSet<TObject>());
        if (targetObjects != null) {
            intersection.retainAll(targetObjects);
        }

        Set<TObject> reverseIntersection = (targetObjects != null ? new HashSet<TObject>(targetObjects) : new HashSet<TObject>());
        if (sourceObjects != null) {
            intersection.retainAll(sourceObjects);
        }
        if (difference != null) {
            sourceBackLog.addAll(difference);
        }
        if (reverseDifference != null) {
            targetBackLog.addAll(reverseDifference);
        }
        if (intersection != null) {
            srcIntersectList.addAll(0, intersection);
        }
        if (reverseIntersection != null) {
            tgtIntersectList.addAll(0, reverseIntersection);
        }

        while (!srcIntersectList.isEmpty()) {
            TObject obj = srcIntersectList.remove(0);
            TObject srcObj = obj;
            TObject tgtObj = targetObjects.get(targetObjects.indexOf(obj));
            if(!equal(srcObj.getStereotype(), tgtObj.getStereotype())) {
                print(indent, "*** WARNING: Potentially invalid object comparison ***");
            }
            print(indent, "Comparing objects: " + srcObj.getName() + ":" + srcObj.getStereotype() + " - " + tgtObj.getName() + ":" + tgtObj.getStereotype());
            this.out.write(indent).writeln("   <object name='" + srcObj.getName() + "' ref='REF" + refCounter++ + "' stereotype='" + (srcObj.getStereotype() == null ? "" : srcObj.getStereotype()) + "' guid='" + srcObj.getEa_guid() + "'>");
            compareNestedObjects(srcObj, tgtObj, indent + "      ");
            this.out.write(indent).writeln("   </object>");
        }

        while (!sourceBackLog.isEmpty()) {
            this.out.write(indent).writeln("<!-- Objects in source but not in target for package: " + src.getName() + "-->");
            TObject obj = sourceBackLog.remove(0);
            this.out.write(indent).writeln("   <object name='" + obj.getName() + "' ref='REF" + refCounter++ + "' stereotype='" + (obj.getStereotype() == null ? "" : obj.getStereotype()) + "' guid='" + obj.getEa_guid() + "'>");
            this.out.write(indent).writeln("      <diff source='exists' target=''/>");
            this.out.write(indent).writeln("   </object>");

        }

        while (!targetBackLog.isEmpty()) {
            this.out.write(indent).writeln("<!-- Objects not in source but in target for package: " + tgt.getName() + "-->");
            TObject obj = targetBackLog.remove(0);
            this.out.write(indent).writeln("   <object name='" + obj.getName() + "' ref='REF" + refCounter++ + "' stereotype='" + (obj.getStereotype() == null ? "" : obj.getStereotype()) + "' guid='" + obj.getEa_guid() + "'>");
            this.out.write(indent).writeln("      <diff source='' target='exists'/>");
            this.out.write(indent).writeln("   </object>");

        }
    }

    private void printPkgContents(TPackage pkg, List<TObject> objects) {
        System.out.println("Objects in package: " + pkg.getName());
        for (TObject obj : objects) {
            System.out.println("Name: " + obj.getName() + " Stereotype: " + obj.getStereotype());
        }
        objectsProcessed += objects.size();
    }

    public void compareObjects(TPackage srcPkg, TPackage tgtPkg, String indent) throws Exception {
        List<TObject> sourceObjects = sourceSession.selectList("com.paras.dbmapper.TObjectMapper.selectObjects", srcPkg);
        List<TObject> targetObjects = targetSession.selectList("com.paras.dbmapper.TObjectMapper.selectObjects", tgtPkg);

        //System.out.println("----------------------------------");
        //print(tgtPkg, targetObjects);
        //System.out.println("----------------------------------");
        List<TObject> sourceBackLog = new ArrayList<TObject>();
        List<TObject> targetBackLog = new ArrayList<TObject>();
        List<TObject> srcIntersectList = new ArrayList<TObject>(); // create an intersect list for src as the package IDs are specific to the given model
        List<TObject> tgtIntersectList = new ArrayList<TObject>(); // create an intersect list for the target 

        Set<TObject> difference = (sourceObjects != null ? new HashSet<TObject>(sourceObjects) : new HashSet<TObject>());
        if (targetObjects != null) {
            difference.removeAll(targetObjects);
        }

        Set<TObject> reverseDifference = (targetObjects != null ? new HashSet<TObject>(targetObjects) : new HashSet<TObject>());
        if (sourceObjects != null) {
            reverseDifference.removeAll(sourceObjects);
        }

        Set<TObject> intersection = (sourceObjects != null ? new HashSet<TObject>(sourceObjects) : new HashSet<TObject>());
        if (targetObjects != null) {
            intersection.retainAll(targetObjects);
        }

        Set<TObject> reverseIntersection = (targetObjects != null ? new HashSet<TObject>(targetObjects) : new HashSet<TObject>());
        if (sourceObjects != null) {
            intersection.retainAll(sourceObjects);
        }

        sourceBackLog.addAll(difference);
        targetBackLog.addAll(reverseDifference);
        srcIntersectList.addAll(intersection);
        tgtIntersectList.addAll(reverseIntersection);

        this.out.write(indent).writeln("<!-- Objects in both source and target in package: " + srcPkg.getName() + " -->");
        int i = 1;

        while (!srcIntersectList.isEmpty()) {
            TObject srcObj = srcIntersectList.remove(0);
            TObject tgtObj = targetObjects.get(targetObjects.indexOf(srcObj));
            if(!equal(srcObj.getStereotype(), tgtObj.getStereotype())) {
                print(indent, "*** WARNING: Potentially invalid object comparison ***");
            }
            print(indent, "Comparing objects: " + srcObj.getName() + ":" + srcObj.getStereotype() + " - " + tgtObj.getName() + ":" + tgtObj.getStereotype());
            this.out.write(indent).writeln("   <object name='" + srcObj.getName() + "' ref='REF" + refCounter++ + "' stereotype='" + (srcObj.getStereotype() == null ? "" : srcObj.getStereotype()) + "' guid='" + srcObj.getEa_guid() + "'>");
            compareNestedObjects(srcObj, tgtObj, indent + "      ");
            this.out.write(indent).writeln("   </object>");

        }

        while (!sourceBackLog.isEmpty()) {
            this.out.write(indent).writeln("<!-- Objects in source but not in target for package: " + srcPkg.getName() + "-->");
            TObject obj = sourceBackLog.remove(0);
            this.out.write(indent).writeln("   <object name='" + obj.getName() + "' ref='REF" + refCounter++ + "' stereotype='" + (obj.getStereotype() == null ? "" : obj.getStereotype()) + "' guid='" + obj.getEa_guid() + "'>");
            this.out.write(indent).writeln("      <diff source='exists' target=''/>");
            this.out.write(indent).writeln("   </object>");
            i++;
        }

        while (!targetBackLog.isEmpty()) {
            this.out.write(indent).writeln("<!-- Objects not in source but in target for package: " + srcPkg.getName() + "-->");
            TObject obj = targetBackLog.remove(0);
            this.out.write(indent).writeln("   <object name='" + obj.getName() + "' ref='REF" + refCounter++ + "' stereotype='" + (obj.getStereotype() == null ? "" : obj.getStereotype()) + "' guid='" + obj.getEa_guid() + "'>");
            this.out.write(indent).writeln("      <diff source='' target='exists'/>");
            this.out.write(indent).writeln("   </object>");
            i++;
        }
    }

    public static void main(String[] args) throws Exception {
        ModelDifferencer md = new ModelDifferencer("source.xml", "target.xml");
        md.createDifferenceFile("C:/Users/seethavi/Documents/NetBeansProjects/RE/model-diff.xml");
    }
}
