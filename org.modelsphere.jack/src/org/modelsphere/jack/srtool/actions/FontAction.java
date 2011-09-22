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

import java.awt.Font;

import org.modelsphere.jack.actions.AbstractApplicationAction;
import org.modelsphere.jack.actions.util.DbMultiTrans;
import org.modelsphere.jack.awt.AwtUtil;
import org.modelsphere.jack.awt.FontChooserDialog;
import org.modelsphere.jack.srtool.ApplicationContext;
import org.modelsphere.jack.srtool.graphic.DbGraphic;
import org.modelsphere.jack.srtool.graphic.FreeText;
import org.modelsphere.jack.srtool.international.LocaleMgr;

public final class FontAction extends AbstractApplicationAction {

    public FontAction() {
        super(LocaleMgr.action.getString("font"));
    }

    protected final void doActionPerformed() {
        Object[] objects = ApplicationContext.getFocusManager().getSelectedObjects();

        FontChooserDialog fc = new FontChooserDialog(ApplicationContext.getDefaultMainFrame(),
                LocaleMgr.screen.getString("chooseFont"), ((FreeText) objects[0]).getFont());
        AwtUtil.centerWindow(fc);
        fc.setVisible(true);
        Font selectedFont = fc.getSelectedFont();
        if (selectedFont != null) {
            try {
                DbMultiTrans.beginTrans(org.modelsphere.jack.baseDb.db.Db.WRITE_TRANS, objects,
                        LocaleMgr.action.getString("font"));
                for (int i = 0; i < objects.length; i++) {
                    ((FreeText) objects[i]).getGraphicalObject().set(DbGraphic.fUserTextGoFont,
                            selectedFont);
                }
                DbMultiTrans.commitTrans(objects);
            } catch (Exception e) {
                org.modelsphere.jack.util.ExceptionHandler.processUncatchedException(
                        ApplicationContext.getDefaultMainFrame(), e);
            }
        }
    }
}
