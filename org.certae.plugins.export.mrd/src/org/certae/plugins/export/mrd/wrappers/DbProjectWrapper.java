/*************************************************************************
Copyright (C) 2011  CERTAE  Universite Laval  
Dario Gomez 
 **********************************************************************/

package org.certae.plugins.export.mrd.wrappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelsphere.jack.baseDb.db.DbEnumeration;
import org.modelsphere.jack.baseDb.db.DbException;
import org.modelsphere.jack.baseDb.db.DbObject;
import org.modelsphere.jack.baseDb.db.DbRelationN;
import org.modelsphere.jack.baseDb.db.DbUDF;
import org.modelsphere.jack.baseDb.meta.MetaClass;
import org.modelsphere.sms.db.DbSMSLinkModel;
import org.modelsphere.sms.db.DbSMSProject;
import org.modelsphere.sms.or.db.DbORColumn;
import org.modelsphere.sms.or.db.DbORDataModel;

public class DbProjectWrapper {
	
	static DbSMSProject m_proj; 
	
    private List<DbLinkModelWrapper> m_linkModels = null;
    private Map<DbORDataModel, DbDataModelWrapper> m_dataModels = new HashMap<DbORDataModel, DbDataModelWrapper>();

    public DbProjectWrapper( DbSMSProject proj) {
        m_proj = proj;
        m_linkModels = new ArrayList<DbLinkModelWrapper>();
        
        try {
			addProject();
		} catch (DbException e) {
			//TD-- Auto-generated catch block
			e.printStackTrace();
		} 
    }
    
    public static DbSMSProject getSmsProj() {
        return m_proj;
    }
    
    public StringWrapper getName() throws DbException {
        String s = m_proj.getName();
        return new StringWrapper(s);
    }
    
    public void clear() {
        m_dataModels.clear();
        m_linkModels.clear();
    }

    private void addProject() throws DbException {

        //for each project component
        DbRelationN relN = m_proj.getComponents();
        DbEnumeration enu = relN.elements();
        
        while (enu.hasMoreElements()) {
            DbObject o = enu.nextElement();
            if (o instanceof DbSMSLinkModel) {
                DbSMSLinkModel lm = (DbSMSLinkModel) o;
                DbLinkModelWrapper linkModel = new DbLinkModelWrapper(this, lm);
                m_linkModels.add(linkModel);
            } else if (o instanceof DbORDataModel) {
                DbORDataModel dataModel = (DbORDataModel) o;
                addDataModel(dataModel);
            } //end if
        } //end while
        
        enu.close();
    }

    private void addDataModel(DbORDataModel dataModel) throws DbException {
//      DbSMSProject project = (DbSMSProject) dataModel.getCompositeOfType(DbSMSProject.metaClass);

        DbDataModelWrapper wDataModel = new DbDataModelWrapper(this,  dataModel);
        m_dataModels.put( dataModel, wDataModel );

        //for each project component
        DbRelationN relN = dataModel.getComponents();
        DbEnumeration enu = relN.elements();
        while (enu.hasMoreElements()) {
            DbObject o = enu.nextElement();
            if (o instanceof DbSMSLinkModel) {
                DbSMSLinkModel lm = (DbSMSLinkModel) o;
                addLinkModel(lm);
            } else if (o instanceof DbORDataModel) {
                DbORDataModel subDataModel = (DbORDataModel) o;
                addDataModel(subDataModel);
            } //end if
        } //end while
        enu.close();
    }

    private void addLinkModel(DbSMSLinkModel lm) throws DbException {
//      DbSMSProject project = (DbSMSProject) lm.getCompositeOfType(DbSMSProject.metaClass);

    	DbLinkModelWrapper linkModel = new DbLinkModelWrapper(this, lm);
        m_linkModels.add(linkModel);
    }

    
    public List<DbLinkModelWrapper> getLinkModels() {
        return m_linkModels;
    } //end getLinkModels()

    
    public DbDataModelWrapper getDataModel(DbORDataModel dm) {
        DbDataModelWrapper dataModel = m_dataModels.get(dm);
        if (dataModel == null) {
            dataModel = new DbDataModelWrapper( this, dm);
            m_dataModels.put(dm, dataModel);
        }

        return dataModel;
    }

    public Map<DbORDataModel, DbDataModelWrapper> getDataModels() {

    	return m_dataModels;
    }

    private List<DbUdfWrapper> m_udfs = null;
    public List<DbUdfWrapper> getUdfs() {
        if (m_udfs == null) {
            m_udfs = new ArrayList<DbUdfWrapper>();
            List<DbUDF> udfs = getUDFs();
            for (DbUDF udf : udfs) {
                DbUdfWrapper udf1 = new DbUdfWrapper(udf);
                m_udfs.add(udf1);
            } //end for
        } //end if

        return m_udfs;
    }

    private List<DbUDF> getUDFs() {
        List<DbUDF> udfs = new ArrayList<DbUDF>();

        try {

        	DbRelationN relN = m_proj.getComponents();
            DbEnumeration enu = relN.elements(DbUDF.metaClass);
            while (enu.hasMoreElements()) {
                DbUDF udf = (DbUDF) enu.nextElement();
                MetaClass mc = udf.getUDFMetaClass();

//                DGT: lo asignaba solo si era asignable a las Cols 
//                if (DbORColumn.metaClass.isAssignableFrom(mc)) {
                udfs.add(udf);
//                }
            } //end while
            enu.close();

        } catch (DbException ex) {

        }

        return udfs;
    }

    public int getNbLinks() {
        int nbLinks = 0;

        try {
            for (DbLinkModelWrapper linkModel : m_linkModels) {
                nbLinks += linkModel.getLinks().size();
            }
        } catch (DbException ex) {
            nbLinks = -1;
        }

        return nbLinks;
    }

} //end ProjectWrapper
