/*************************************************************************
Copyright (C) 2011  CERTAE  Universite Laval  
Dario Gomez 
 **********************************************************************/

package org.certae.plugins.export.mrd.wrappers;

import org.modelsphere.jack.baseDb.db.DbException;
import org.modelsphere.sms.or.db.DbORAssociation;

public class DbORAssociationWrapper {
    private DbORAssociation m_aso;
    private DbORAssociationEndWrapper wBase;
    private DbORAssociationEndWrapper wRefe;
    
    public DbORAssociationWrapper(DbORAssociation aso) throws DbException {
        m_aso = aso;
        wBase = new DbORAssociationEndWrapper ( aso.getFrontEnd()) ; 
        wRefe = new DbORAssociationEndWrapper ( aso.getBackEnd()) ; 
    }

    public String getName() throws DbException {
        String s = m_aso.getName();
        s = (s==null )? "": s; 
        return s;
    }

    public StringWrapper getPhysicalName() throws DbException {
        StringWrapper sw = new StringWrapper(m_aso.getPhysicalName());
        return sw;
    }

	public String getPairName() throws DbException {
		return wBase.getTableName() + "-" + wRefe.getTableName();
	}

    public DbORAssociationEndWrapper getBase() throws DbException {
        return wBase;
    }

    public DbORAssociationEndWrapper getRefe() throws DbException {
        return wRefe;
    }

    
}
