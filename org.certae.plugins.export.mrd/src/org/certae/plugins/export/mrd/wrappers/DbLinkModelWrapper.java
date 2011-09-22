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
import org.modelsphere.jack.baseDb.db.DbObject;
import org.modelsphere.jack.baseDb.db.DbRelationN;
import org.certae.plugins.export.mrd.international.LocaleMgr;
import org.modelsphere.sms.db.DbSMSLink;
import org.modelsphere.sms.db.DbSMSLinkModel;

public class DbLinkModelWrapper {
    private static final String NO_MODEL = LocaleMgr.misc.getString("NoModel");
    private static final String X_MODELS = LocaleMgr.misc.getString("0Models");

    private DbProjectWrapper m_parent;
    private DbSMSLinkModel m_linkModel;

    public DbLinkModelWrapper(DbProjectWrapper parent, DbSMSLinkModel linkModel) {
        m_parent = parent;
        m_linkModel = linkModel;
    }

    public DbProjectWrapper getParent() {
        return m_parent;
    }

    public String getSource() throws DbException {
        List<String> models = new ArrayList<String>();

        List<DbLinkWrapper> links = getLinks();
        for (DbLinkWrapper link : links) {
            List<DbColumnWrapper> sources = link.getSourceColumns();
            for (DbColumnWrapper column : sources) {
                String modelName = column.getModelName();
                if (!models.contains(modelName)) {
                    models.add(modelName);
                }
            }
        } //end for

        String source = toText(models);
        return source;
    }

    public String getDestination() throws DbException {
        List<String> models = new ArrayList<String>();

        List<DbLinkWrapper> links = getLinks();
        for (DbLinkWrapper link : links) {
            List<DbColumnWrapper> sources = link.getDestinationColumns();
            for (DbColumnWrapper column : sources) {
                String modelName = column.getModelName();
                if (!models.contains(modelName)) {
                    models.add(modelName);
                }
            }
        } //end for

        String destination = toText(models);
        return destination;
    }

    private String toText(List<String> models) {
        String text;
        if (models.size() == 0) {
            text = NO_MODEL;
        } else if (models.size() == 1) {
            text = models.get(0);
        } else {
            text = MessageFormat.format(X_MODELS, models.size());
        } //end if

        return text;
    }

    public String getName() throws DbException {
        String name = m_linkModel.getName();
        return name;
    }

    private List<DbLinkWrapper> m_links = null;

    public List<DbLinkWrapper> getLinks() throws DbException {
        if (m_links == null) {
            m_links = new ArrayList<DbLinkWrapper>();

            //for each project component
            DbRelationN relN = m_linkModel.getComponents();
            DbEnumeration enu = relN.elements();
            while (enu.hasMoreElements()) {
                DbObject o = enu.nextElement();
                if (o instanceof DbSMSLink) {
                    DbSMSLink l = (DbSMSLink) o;
                    DbLinkWrapper link = new DbLinkWrapper(this, l);
                    m_links.add(link);
                } //end if
            } //end while
            enu.close();
        }

        return m_links;
    }

}
