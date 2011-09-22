/*************************************************************************

Copyright (C) 2009 Grandite

This file is part of Open ModelSphere.

Open ModelSphere is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA 
or see http://www.gnu.org/licenses/.

You can redistribute and/or modify this particular file even under the
terms of the GNU Lesser General Public License (LGPL) as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

You should have received a copy of the GNU Lesser General Public License 
(LGPL) along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA 
or see http://www.gnu.org/licenses/.

You can reach Grandite at: 

20-1220 Lebourgneuf Blvd.
Quebec, QC
Canada  G2K 2G4

or

open-modelsphere@grandite.com

 **********************************************************************/

package org.modelsphere.jack.srtool.actions;

import org.modelsphere.jack.actions.AbstractApplicationAction;
import org.modelsphere.jack.baseDb.db.*;
import org.modelsphere.jack.srtool.ApplicationContext;
import org.modelsphere.jack.srtool.international.LocaleMgr;
import org.modelsphere.jack.util.ExceptionHandler;

public class CloseAllAction extends AbstractApplicationAction {

    public CloseAllAction() {
        super(LocaleMgr.action.getString("closeAll"));
    }

    protected final void doActionPerformed() {

        // //
        // next two lines to address a transaction error when editing
        // an attribute in design panel at the action is invoked

        try {
            ApplicationContext.getDefaultMainFrame().getExplorerPanel().getExplorer().refresh();
        } catch (DbException dbe) {
        }

        // //
        // proceed wiht close all actions

        Db[] dbs = Db.getDbs();
        if (dbs != null) {
            for (int i = 0; i < dbs.length; i++) {
                if (!(dbs[i] instanceof DbRAM))
                    continue;
                DbProject project = null;
                try {
                    dbs[i].beginReadTrans();
                    DbEnumeration dbEnum = dbs[i].getRoot().getComponents().elements(
                            DbProject.metaClass);
                    if (dbEnum.hasMoreElements())
                        project = (DbProject) dbEnum.nextElement();
                    dbEnum.close();
                    dbs[i].commitTrans();
                } catch (DbException e) {
                    ExceptionHandler.processUncatchedException(ApplicationContext
                            .getDefaultMainFrame(), e);
                    return;
                }
                if (project != null)
                    ApplicationContext.getDefaultMainFrame().closeProject(project);
            }
        }

    }
}
