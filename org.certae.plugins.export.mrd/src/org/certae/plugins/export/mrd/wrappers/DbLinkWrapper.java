/*************************************************************************
Copyright (C) 2011  CERTAE  Universite Laval  
Dario Gomez 
 **********************************************************************/
package org.certae.plugins.export.mrd.wrappers;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.modelsphere.jack.baseDb.db.DbEnumeration;
import org.modelsphere.jack.baseDb.db.DbException;
import org.modelsphere.jack.baseDb.db.DbRelationN;
import org.certae.plugins.export.mrd.international.LocaleMgr;
import org.modelsphere.sms.db.DbSMSLink;
import org.modelsphere.sms.or.db.DbORColumn;
import org.modelsphere.sms.or.db.DbORDataModel;
import org.modelsphere.sms.or.db.DbORTable;

public class DbLinkWrapper {
    private static final String NO_DESTINATION = LocaleMgr.misc.getString("NoDestination");
    private static final String X_COLUMNS = LocaleMgr.misc.getString("0Columns");

    private DbLinkModelWrapper m_parent;
    private DbSMSLink m_link;

    public DbLinkWrapper(DbLinkModelWrapper parent, DbSMSLink link) {
        m_parent = parent;
        m_link = link;
    }

    public List<DbColumnWrapper> getSourceColumns() throws DbException {
        List<DbColumnWrapper> columns = new ArrayList<DbColumnWrapper>();
        DbProjectWrapper project = m_parent.getParent();

        DbRelationN relN = m_link.getSourceObjects();
        DbEnumeration enu = relN.elements(DbORColumn.metaClass);
        while (enu.hasMoreElements()) {
            DbORColumn col = (DbORColumn) enu.nextElement();
            DbORTable t = (DbORTable) col.getCompositeOfType(DbORTable.metaClass);
            DbORDataModel dm = (DbORDataModel) t.getCompositeOfType(DbORDataModel.metaClass);
            DbDataModelWrapper dataModel = project.getDataModel(dm);
            DbTableWrapper table = dataModel.getTable(t);
            DbColumnWrapper column = table.getColumn(col);
            columns.add(column);
        } //end while
        enu.close();

        return columns;
    }

    public List<DbColumnWrapper> getDestinationColumns() throws DbException {
        List<DbColumnWrapper> columns = new ArrayList<DbColumnWrapper>();
        DbProjectWrapper project = m_parent.getParent();

        DbRelationN relN = m_link.getTargetObjects();
        DbEnumeration enu = relN.elements(DbORColumn.metaClass);
        while (enu.hasMoreElements()) {
            DbORColumn col = (DbORColumn) enu.nextElement();
            DbORTable t = (DbORTable) col.getCompositeOfType(DbORTable.metaClass);
            DbORDataModel dm = (DbORDataModel) t.getCompositeOfType(DbORDataModel.metaClass);
            DbDataModelWrapper dataModel = project.getDataModel(dm);
            DbTableWrapper table = dataModel.getTable(t);
            DbColumnWrapper column = table.getColumn(col);
            columns.add(column);

        } //end while
        enu.close();

        return columns;
    }

    public String getDestinationText() throws DbException {
        List<DbColumnWrapper> destinations = new ArrayList<DbColumnWrapper>();
        DbProjectWrapper project = m_parent.getParent();

        DbRelationN relN = m_link.getTargetObjects();
        DbEnumeration enu = relN.elements(DbORColumn.metaClass);
        while (enu.hasMoreElements()) {
            DbORColumn col = (DbORColumn) enu.nextElement();
            DbORTable t = (DbORTable) col.getCompositeOfType(DbORTable.metaClass);
            DbORDataModel dm = (DbORDataModel) t.getCompositeOfType(DbORDataModel.metaClass);
            DbDataModelWrapper dataModel = project.getDataModel(dm);
            DbTableWrapper table = dataModel.getTable(t);
            DbColumnWrapper column = table.getColumn(col);
            destinations.add(column);
        } //end while
        enu.close();

        String destinationName;
        if (destinations.size() == 0) {
            destinationName = NO_DESTINATION;
        } else if (destinations.size() == 1) {
            DbColumnWrapper col = destinations.get(0);
            destinationName = col.getFullName();
        } else {
            destinationName = MessageFormat.format(X_COLUMNS, destinations.size());
        } //end if 

        return destinationName;
    }

    public String getName() throws DbException {
        return m_link.getName();
    }

    public String getAlias() throws DbException {
        return m_link.getAlias();
    }

}
