/*************************************************************************
Copyright (C) 2011  CERTAE  Universite Laval  
Dario Gomez 
 **********************************************************************/

package org.certae.plugins.export.mrd.wrappers;

import java.util.ArrayList;
import java.util.List;

import org.modelsphere.jack.baseDb.db.DbEnumeration;
import org.modelsphere.jack.baseDb.db.DbException;
import org.modelsphere.sms.db.srtypes.SMSMultiplicity;
import org.modelsphere.sms.or.db.DbORAbsTable;
import org.modelsphere.sms.or.db.DbORAssociationEnd;
import org.modelsphere.sms.or.db.DbORColumn;
import org.modelsphere.sms.or.db.DbORFKeyColumn;
import org.modelsphere.sms.or.db.DbORForeign;

public class DbORAssociationEndWrapper {
    private DbORAssociationEnd m_end;

    public DbORAssociationEndWrapper(DbORAssociationEnd end) {
        m_end = end;
    }

    public List<DbColumnWrapper> getColumns() throws DbException {
        List<DbColumnWrapper> columnList = new ArrayList<DbColumnWrapper>();
        DbORForeign f = m_end.getMember();
        if (f != null) {
            DbEnumeration enu = f.getComponents().elements(DbORFKeyColumn.metaClass);

            while (enu.hasMoreElements()) {
                DbORFKeyColumn fColumn = (DbORFKeyColumn) enu.nextElement();
                DbORColumn c = fColumn.getColumn();
                DbColumnWrapper col = new DbColumnWrapper(c);
                columnList.add(col);
            }
            enu.close();
        } //end if

        return columnList;
    }

    public StringWrapper getName() throws DbException {
//      String s = m_end.getName();
    	DbORAbsTable table = m_end.getClassifier();
    	String s = table.getName();
        s = ((s==null || s==""))? m_end.getPhysicalName(): s; 
        return new StringWrapper(s);
    }

    public StringWrapper getPhysicalName() throws DbException {
        StringWrapper sw = new StringWrapper(m_end.getPhysicalName());
        return sw;
    }

    private  DbORAssociationEndWrapper getOpposite() throws DbException {
        DbORAssociationEnd oppEnd = m_end.getOppositeEnd();
        DbORAssociationEndWrapper opposite = new DbORAssociationEndWrapper(oppEnd);
        return opposite;
    }

    public String getTableName() throws DbException {
    	DbORAbsTable table = m_end.getClassifier();
    	String s = table.getName();
    	s = ((s==null || s==""))? table.getPhysicalName(): s; 
    	s = (s==null)? "": s; 
        return s;
    }
    
    public String getTableRef() throws DbException {
    	DbORAssociationEndWrapper tabRef = getOpposite(); 
        return tabRef.getTableName();
    }
    
    public String getMin() throws DbException {
        SMSMultiplicity mult = m_end.getMultiplicity();
        String min = mult.getDatarunMinimumMultiplicityLabel();
        return min;
    }

    public String getMax() throws DbException {
        SMSMultiplicity mult = m_end.getMultiplicity();
        String max = mult.getDatarunMaximumMultiplicityLabel();
        return max;
    }

    public String getMultiplicity() throws DbException {

        int mult = m_end.getMultiplicity().getValue();
        String s = ""; 

    	switch (mult) {
    	   case SMSMultiplicity.UNDEFINED	:	s = "UNDEFINED"; 	break;
    	   case SMSMultiplicity.MANY 		:	s = "MANY";      	break;
    	   case SMSMultiplicity.ONE_OR_MORE	:	s = "ONE_OR_MORE"; 	break;
    	   case SMSMultiplicity.EXACTLY_ONE	:	s = "EXACTLY_ONE";	break;
    	   case SMSMultiplicity.OPTIONAL 	:	s = "OPTIONAL";		break;
    	   case SMSMultiplicity.SPECIFIC 	:	s = "SPECIFIC";		break;
    	}
    	
    	return s; 
    }

    public String getCardinality() throws DbException {
        return getMin() + ".." + getMax();
    }

    
    public boolean isNavigable() throws DbException {
        boolean navigable = m_end.isNavigable();
        return navigable;
    }

    public boolean isFrontEnd() throws DbException {
        boolean b1 = m_end.isFrontEnd(); 
        return b1;
    }

    
//    
//    
//    	  DbORTable t1;
//        DbORTable parent = null;
//        DbRelationN relN = t1.getAssociationEnds();
//        DbEnumeration enu = relN.elements(DbORAssociationEnd.metaClass);
//        while (enu.hasMoreElements()) {
//            DbORAssociationEnd end = (DbORAssociationEnd) enu.nextElement();
//            DbORAssociationEnd oppEnd = end.getOppositeEnd();
//            DbORAbsTable t = oppEnd.getClassifier();
//                if (t instanceof DbORTable) {
//                    parent = (DbORTable) t;
//                    break;
    
//			-------------------------------------------------------------
//                DbORAbsTable oppTable = oppEnd.getClassifier();
//                if (oppTable.equals(table)) {
//                    DbORAssociation assoc = (DbORAssociation) oppEnd.getCompositeOfType(DbORAssociation.metaClass);
//
//                    DbORAssociationEnd frontEnd = assoc.getFrontEnd();
//                    DbORAssociationEnd backEnd = assoc.getBackEnd();
//
//                    DbORAbsTable frontTable = frontEnd.getClassifier();
//                    DbORAbsTable backTable = backEnd.getClassifier();
//

    
}
