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

import org.modelsphere.jack.actions.AbstractActionsStore;
import org.modelsphere.jack.actions.AbstractTriStatesAction;
import org.modelsphere.jack.actions.SelectionActionListener;
import org.modelsphere.jack.baseDb.db.DbException;
import org.modelsphere.jack.graphic.Grid;
import org.modelsphere.jack.preference.PropertiesManager;
import org.modelsphere.jack.preference.PropertiesSet;
import org.modelsphere.jack.srtool.ApplicationContext;
import org.modelsphere.jack.srtool.international.LocaleMgr;

/**
 * @author nicolask
 * 
 *         TD-- To change the template for this generated type comment go to Window - Preferences -
 *         Java - Code Style - Code Templates
 */
public class ShowGridAction extends AbstractTriStatesAction implements SelectionActionListener {

    public void updateSelectionAction() throws DbException {
        PropertiesSet options = PropertiesManager.APPLICATION_PROPERTIES_SET;
        Boolean retVal = Boolean.FALSE;
        if (options != null)
            retVal = options.getPropertyBoolean(Grid.class, Grid.PROPERTY_HIDE_GRID, new Boolean(
                    Grid.PROPERTY_HIDE_GRID_DEFAULT));
        setState(!retVal.booleanValue() ? STATE_ON : STATE_OFF);
    }

    private static final String kGrid = LocaleMgr.action.getString("ShowGrid");

    public ShowGridAction() {
        super(kGrid);
        this.setMnemonic(LocaleMgr.action.getMnemonic("ShowGrid"));
        setEnabled(true);
        setDefaultToolBarVisibility(false);
    }

    protected final void doActionPerformed() {
        PropertiesSet options = PropertiesManager.APPLICATION_PROPERTIES_SET;
        Boolean retVal = Boolean.FALSE;
        if (options != null)
            retVal = options.getPropertyBoolean(Grid.class, Grid.PROPERTY_HIDE_GRID, new Boolean(
                    Grid.PROPERTY_HIDE_GRID_DEFAULT));
        options.setProperty(Grid.class, Grid.PROPERTY_HIDE_GRID, !retVal.booleanValue());
        setState(!retVal.booleanValue() ? STATE_ON : STATE_OFF);
        RefreshAllAction smsAction = (RefreshAllAction) ApplicationContext.getActionStore().get(
                AbstractActionsStore.REFRESH_ALL);
        smsAction.doActionPerformed();
    }
}
