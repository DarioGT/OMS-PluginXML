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
import org.modelsphere.sms.db.DbSMSProject;
import org.modelsphere.sms.or.db.DbORAssociation;
import org.modelsphere.sms.or.db.DbORDataModel;
import org.modelsphere.sms.or.db.DbORTable;

//import org.certae.plugins.export.mrd.wrappers.DbTableWrapper;;

public class DbDataModelWrapper implements Comparable<DbDataModelWrapper> {

    private DbProjectWrapper m_parent;
    private DbORDataModel m_dataModel;

	private List<DbDataModelWrapper> m_subModels = new ArrayList<DbDataModelWrapper>();
    private Map<DbORDataModel, DbDataModelWrapper> m_subModelMap = new HashMap<DbORDataModel, DbDataModelWrapper>();

    public DbDataModelWrapper(DbProjectWrapper parent, DbORDataModel dbDataModel) {
        m_parent = parent;
        m_dataModel = dbDataModel;
    }

	public String getUdfValue(String udfName) {
		String value;

		try {
			Object o = m_dataModel.getUDF(udfName);
			value = (o == null) ? "" : o.toString();
		} catch (DbException ex) {
			value = "?";
		}

		return value;
	}

    
    public DbProjectWrapper getParent() {
        return m_parent;
    }

    public String getName() {
        String name;
        try {
            name = m_dataModel.getName();
        } catch (DbException ex) {
            name = super.toString();
        } //end try

        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    private Map<DbORTable, DbTableWrapper> m_tables = new HashMap<DbORTable, DbTableWrapper>();

    public DbTableWrapper getTable(DbORTable t) {
        DbTableWrapper table = m_tables.get(t);
        if (table == null) {
            table = new DbTableWrapper(this, t);
            m_tables.put(t, table);
        }

        return table;
    }

    @Override
    public int compareTo(DbDataModelWrapper that) {
        String s1 = this.getName();
        String s2 = that.getName();
        int comparaison = s1.compareTo(s2);
        return comparaison;
    }

    public List<DbDataModelWrapper> getSubModels() {

        try {
            DbRelationN relN = m_dataModel.getComponents();
            DbEnumeration enu = relN.elements(DbORDataModel.metaClass);
            while (enu.hasMoreElements()) {
                DbORDataModel dbModel = (DbORDataModel) enu.nextElement();
                DbDataModelWrapper subModel = m_subModelMap.get(dbModel);

                if (subModel == null) {
                    subModel = m_parent.getDataModel(dbModel);
                    m_subModelMap.put(dbModel, subModel);
                    m_subModels.add(subModel);
                }

            } //end while
            enu.close();
        } catch (DbException ex) {
            m_subModelMap.clear();
            m_subModels.clear();
        } //end try 

        return m_subModels;
    } //end getSubModels()

    public boolean isProjectChild() {
        boolean projectChild;

        try {
            DbObject composite = m_dataModel.getComposite();
            projectChild = (composite instanceof DbSMSProject);
        } catch (DbException ex) {
            projectChild = false;
        } //end try 

        return projectChild;
    } //end isProjectChild()

    
    public List<DbORAssociationWrapper> getAssociations() {

    	List <DbORAssociationWrapper> m_associations = new ArrayList<DbORAssociationWrapper>();
        try {
            DbRelationN relN = m_dataModel.getComponents();
            DbEnumeration enu = relN.elements(DbORAssociation.metaClass);
            while (enu.hasMoreElements()) {
            	DbORAssociation dbAssociation = (DbORAssociation) enu.nextElement();
            	DbORAssociationWrapper wAssociation = new DbORAssociationWrapper(dbAssociation);

                m_associations.add(wAssociation);

            } //end while
            enu.close();
        } catch (DbException ex) {
            m_associations.clear();
        } //end try 

        return m_associations;
    } //end 


}
