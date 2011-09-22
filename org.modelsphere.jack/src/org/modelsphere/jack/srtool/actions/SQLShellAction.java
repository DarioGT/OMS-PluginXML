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
import org.modelsphere.jack.srtool.ApplicationContext;
import org.modelsphere.jack.srtool.DefaultMainFrame;
import org.modelsphere.jack.srtool.reverse.jdbc.*;
import org.modelsphere.jack.srtool.services.ConnectionMessage;

/**
 * 
 * Must be used in debug only
 * 
 */
public final class SQLShellAction extends AbstractApplicationAction implements
        ActiveConnectionListener {
    private static SQLShellAction singleton;
    private SQLShell sqlShell;

    static {
        singleton = new SQLShellAction();
    }

    public static SQLShellAction getSingleton() {
        return singleton;
    }

    public SQLShell getShell() {
        return sqlShell;
    }

    private SQLShellAction() {
        super("Start SQL Shell"); // NOT LOCALIZABLE, hidden feature
        setEnabled(true);
        this.setHelpText("Start SQL Shell"); // NOT LOCALIZABLE, hidden feature
        ActiveConnectionManager.addActiveConnectionListener(this);
    }

    protected final void doActionPerformed() {
        sqlShell = new SQLShell();
        ApplicationContext.getDefaultMainFrame().getJDesktopPane().add(sqlShell,
                DefaultMainFrame.PROPERTY_LAYER);
        sqlShell.setVisible(true);
        sqlShell.setSize(600, 400);
    }

    public void activeConnectionChanged(ConnectionMessage cm) {
        setEnabled(cm != null);
        if (cm != null && sqlShell != null) {
            sqlShell.setConnection(cm);
        }

    }
}
