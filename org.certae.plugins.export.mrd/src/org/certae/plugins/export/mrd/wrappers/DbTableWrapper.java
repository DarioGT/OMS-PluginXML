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
import org.modelsphere.jack.baseDb.db.DbRelationN;
import org.modelsphere.sms.or.db.DbORAssociationEnd;
import org.modelsphere.sms.or.db.DbORColumn;
import org.modelsphere.sms.or.db.DbORDataModel;
import org.modelsphere.sms.or.db.DbORFKeyColumn;
//import org.modelsphere.sms.or.db.DbORForeign;
import org.modelsphere.sms.or.db.DbORPrimaryUnique;
import org.modelsphere.sms.or.db.DbORTable;

public class DbTableWrapper {
//    private static final String RUBRIQUE_SUFFIX = "(RUBRIQUE)";
//    private static final String RUBRIQUE_STEREOTYPE = "rubrique";

	private DbDataModelWrapper m_dtModel;
    private DbORTable m_table;

    
    public DbTableWrapper(DbDataModelWrapper dtModel, DbORTable table) {
        m_dtModel = dtModel;
        m_table = table;
    }

    public DbDataModelWrapper getDtModel() {
        return m_dtModel;
    }

    public StringWrapper getPhysicalName() throws DbException {
        StringWrapper sw = new StringWrapper(m_table.getPhysicalName());
        return sw;
    }
    
    public StringWrapper getSuperTableName() throws DbException {

        String s = "";
    	DbORTable superTable = (DbORTable) m_table.getSuperCopy(); 

        if (superTable  != null) {
           s = superTable.getName(); 
        } 
        
    	StringWrapper sw = new StringWrapper(s);
        return sw;
    }
    
    @Override
    public String toString() {
    	String s = ""; 
        try {
			s = getName().toString();
		} catch (DbException e) {
			//TD-- Auto-generated catch block
		}
		return s; 
    }

    public StringWrapper getName() throws DbException {
        StringWrapper sw = new StringWrapper(m_table.getName());
        return sw;
    }

    public StringWrapper getAlias() throws DbException {
        StringWrapper sw = new StringWrapper(m_table.getAlias());
        return sw;
    }

    public StringWrapper getNamespace() throws DbException {
        DbORDataModel model = (DbORDataModel) m_table.getCompositeOfType(DbORDataModel.metaClass);
        String name = model.getName();
        StringWrapper sw = new StringWrapper(name);
        return sw;
    }


    //attribute: a column which is not a primary nor a foreign column
    public List<DbColumnWrapper> getAttributes() throws DbException {
        List<DbColumnWrapper> columns = new ArrayList<DbColumnWrapper>();

        //for each field
        DbRelationN relN = m_table.getComponents();
        DbEnumeration enu = relN.elements(DbORColumn.metaClass);
        while (enu.hasMoreElements()) {
            DbORColumn col = (DbORColumn) enu.nextElement();

            if (!isPrimary(col) && (!isForeign(col)) && (!isVersion(col))) {
                DbColumnWrapper cw = new DbColumnWrapper(this, col);
                columns.add(cw);
            }
        } //end while
        enu.close();

        return columns;
    } //end getAttributes()



    public List<DbORAssociationEndWrapper> getReferences() throws DbException {
        List<DbORAssociationEndWrapper> references = new ArrayList<DbORAssociationEndWrapper>();
        
        DbRelationN relN = m_table.getAssociationEnds();
        DbEnumeration enu = relN.elements(DbORAssociationEnd.metaClass);
        while (enu.hasMoreElements()) {

            DbORAssociationEnd end = (DbORAssociationEnd) enu.nextElement();
            DbORAssociationEndWrapper ref = new DbORAssociationEndWrapper(end);
        	
            references.add(ref);
            
        } //end while
        enu.close();

        return references;
        
//        List<Integer> fkOrderedList = new ArrayList<Integer>();
//        DbEnumeration fkEnum = column.getFKeyColumns().elements();
//        while (fkEnum.hasMoreElements()) {
//            DbORForeign foreignKey = (DbORForeign) fkEnum.nextElement().getComposite();
//            fkOrderedList.add(new Integer(calculateForeignConstraintID(foreignKey)));
//        }
//        fkEnum.close();

    } //end getReferences()

    
    
    // -----------------------------------------------------------
    // -----------------------------------------------------------
    // -----------------------------------------------------------

    
    private Map<DbORColumn, DbColumnWrapper> m_columns = new HashMap<DbORColumn, DbColumnWrapper>();
    public DbColumnWrapper getColumn(DbORColumn col) throws DbException {
        DbColumnWrapper column = m_columns.get(col);
        if (column == null) {
            column = new DbColumnWrapper(this, col);
            m_columns.put(col, column);
        }

        return column;
    }

    private Map<DbORTable, DbORPrimaryUnique> tablePK = new HashMap<DbORTable, DbORPrimaryUnique>();
    public boolean isPrimary(DbORColumn column) throws DbException {
        DbORTable table = (DbORTable) column.getCompositeOfType(DbORTable.metaClass);
        DbORPrimaryUnique pk = tablePK.get(table);
        if (pk == null) {
            DbEnumeration enu = table.getComponents().elements(DbORPrimaryUnique.metaClass);
            while (enu.hasMoreElements()) {
                DbORPrimaryUnique puk = (DbORPrimaryUnique) enu.nextElement();
                if (puk.isPrimary()) {
                    pk = puk;
                    break;
                }
            } //end while
            enu.close();

            tablePK.put(table, pk);
        } //end if

        boolean primary = false;
        if (pk != null) {
            DbEnumeration enu = pk.getColumns().elements(DbORColumn.metaClass);
            while (enu.hasMoreElements()) {
                DbORColumn c = (DbORColumn) enu.nextElement();
                if (column.equals(c)) {
                    primary = true;
                    break;
                }
            } //end while
            enu.close();
        }

        return primary;
    } //end isPrimary()

    
    
    private boolean isForeign(DbORColumn column) throws DbException {
        boolean isForeign = false;

        DbEnumeration enu = column.getFKeyColumns().elements(DbORFKeyColumn.metaClass);
        isForeign = enu.hasMoreElements();
        enu.close();

        return isForeign;
    }

    private boolean isVersion(DbORColumn col) throws DbException {
        String name = col.getPhysicalName();
        boolean version = "version".equals(name);
        return version;
    }

//    public final void getPKs() throws DbException {
//		TODO  Enumera las Pkey 
//    	  DbORAssociationEnd end = (DbORAssociationEnd) objects[0];
//        DbORPrimaryUnique selKey = end.getReferencedConstraint();
//
//        DbORAbsTable tableSo = (DbORAbsTable) end.getOppositeEnd().getClassifier();
//        DbEnumeration dbEnum = tableSo.getComponents().elements(DbORPrimaryUnique.metaClass);
//        while (dbEnum.hasMoreElements()) {
//            DbORPrimaryUnique key = (DbORPrimaryUnique) dbEnum.nextElement();
//            dbos.add(key);
//        }
//        dbEnum.close();
//
//        String tableName = tableSo.getName();
//        for (int i = 0; i < dbos.size(); i++) {
//            String name = ((DbORPrimaryUnique) dbos.get(i)).getName();
//            items[i + sizeadjustment] = MessageFormat.format(kKeyNamePatern, new Object[] {
//                    tableName, name });
//        }
//        setDomainValues(items);
//
//        if (selKey == null) setSelectedIndex(0);
//        else  setSelectedIndex(dbos.indexOf(selKey) + sizeadjustment);
//        setEnabled(true);
//    }

//  private static void reverseDependencies(DbORPrimaryUnique puKey) throws DbException {
//  // First get in a list the foreign columns present in the primary/unique
//  // key.
//  ArrayList foreignCols = new ArrayList();
//  DbRelationN cols = puKey.getColumns();
//  int i;
//  for (i = 0; i < cols.size(); i++) {
//      DbORColumn col = (DbORColumn) cols.elementAt(i);
//      if (col.getFKeyColumns().size() != 0)
//          foreignCols.add(col);
//  }
//
//  while (foreignCols.size() > 0) {
//      // For each foreign column, check if one of its foreign keys has all
//      // its columns present in the PU key;
//      // if one such foreign key is found, add it to the dependencies of
//      // the PU key.
//      DbORColumn foreignCol = (DbORColumn) foreignCols.get(0);
//      DbRelationN foreignKeys = foreignCol.getFKeyColumns();
//      for (i = 0; i < foreignKeys.size(); i++) {
//          DbORForeign foreignKey = (DbORForeign) foreignKeys.elementAt(i).getComposite();
//          if (isFKeyInPUKey(foreignKey, foreignCols, false)) {
//              puKey.addToAssociationDependencies(foreignKey.getAssociationEnd());
//              // Remove the foreign key columns from the list of columns
//              // to be processed
//              isFKeyInPUKey(foreignKey, foreignCols, true);
//              break;
//          }
//      }
//      // Just in case the column is still in the list (i.e. no foreign key
//      // was found for the dependency).
//      foreignCols.remove(foreignCol);
//  }
//}


//private static boolean isFKeyInPUKey(DbORForeign foreignKey, ArrayList puCols, boolean purge)
//      throws DbException {
//  // Return true if all the columns of the foreign key are present in the list
//  // of PU columns.
//  // If purge, remove from the list all the columns of the foreign key.
//  boolean isPresent = true;
//  DbRelationN foreignCols = foreignKey.getComponents();
//  for (int i = 0; i < foreignCols.size(); i++) {
//      DbORColumn foreignCol = ((DbORFKeyColumn) foreignCols.elementAt(i)).getColumn();
//      if (purge) {
//          puCols.remove(foreignCol);
//      } else if (!puCols.contains(foreignCol)) {
//          isPresent = false;
//          break;
//      }
//  }
//  return isPresent;
//}

    

}
