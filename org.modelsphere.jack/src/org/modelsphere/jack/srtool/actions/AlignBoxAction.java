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

import java.awt.Rectangle;

import org.modelsphere.jack.actions.AbstractDomainAction;
import org.modelsphere.jack.actions.ActionInformation;
import org.modelsphere.jack.actions.SelectionActionListener;
import org.modelsphere.jack.actions.util.DbMultiTrans;
import org.modelsphere.jack.baseDb.db.*;
import org.modelsphere.jack.baseDb.db.srtypes.*;
import org.modelsphere.jack.graphic.ZoneBox;
import org.modelsphere.jack.international.LocaleMgr;
import org.modelsphere.jack.srtool.ApplicationContext;
import org.modelsphere.jack.srtool.graphic.ApplicationDiagram;
import org.modelsphere.jack.srtool.graphic.DbGraphic;

public final class AlignBoxAction extends AbstractDomainAction implements SelectionActionListener {
    private static final String kAlignBoxAction = LocaleMgr.action.getString("AlignBox");

    public AlignBoxAction() {
        super(kAlignBoxAction, false);
        setDomainValues(AlignBoxDomain.stringPossibleValues);
        setVisible(true);
        setEnabled(false);
    }

    protected final void doActionPerformed() {
        int selvalue = getSelectedIndex();
        AlignBoxDomain value = AlignBoxDomain.objectPossibleValues[selvalue];
        selvalue = value.getValue();
        Object[] objects = ApplicationContext.getFocusManager().getSelectedObjects();
        try {
            DbMultiTrans.beginTrans(Db.WRITE_TRANS, objects, kAlignBoxAction);
            int intValue = -1;
            for (int i = 0; i < objects.length; i++) {
                ZoneBox box = (ZoneBox) objects[i];
                Rectangle boxRec = box.getRectangle();
                if (selvalue == AlignBoxDomain.TOP) {
                    if (intValue == -1)
                        intValue = boxRec.y;
                    else if (boxRec.y < intValue)
                        intValue = boxRec.y;
                } else if (selvalue == AlignBoxDomain.RIGHT) {
                    if (intValue == -1)
                        intValue = boxRec.x + boxRec.width;
                    else if ((boxRec.x + boxRec.width) > intValue)
                        intValue = boxRec.x + boxRec.width;
                } else if (selvalue == AlignBoxDomain.BOTTOM) {
                    if (intValue == -1)
                        intValue = boxRec.y + boxRec.height;
                    else if ((boxRec.y + boxRec.height) > intValue)
                        intValue = boxRec.y + boxRec.height;
                } else if (selvalue == AlignBoxDomain.LEFT) {
                    if (intValue == -1)
                        intValue = boxRec.x;
                    else if (boxRec.x < intValue)
                        intValue = boxRec.x;
                }
            }
            // change rectangle
            if (intValue == -1)
                intValue = 0;
            for (int i = 0; i < objects.length; i++) {
                ZoneBox box = (ZoneBox) objects[i];
                DbObject boxGo = ((ActionInformation) box).getGraphicalObject();
                Rectangle boxGoRec = (Rectangle) boxGo.get(DbGraphic.fGraphicalObjectRectangle);
                Rectangle boxRec = box.getRectangle();
                if (selvalue == AlignBoxDomain.TOP) {
                    boxRec.translate(0, intValue - boxRec.y);
                } else if (selvalue == AlignBoxDomain.RIGHT) {
                    boxRec.translate((intValue - boxRec.width) - boxRec.x, 0);
                } else if (selvalue == AlignBoxDomain.BOTTOM) {
                    boxRec.translate(0, (intValue - boxRec.height) - boxRec.y);
                } else if (selvalue == AlignBoxDomain.LEFT) {
                    boxRec.translate(intValue - boxRec.x, 0);
                }
                boxGo.set(DbGraphic.fGraphicalObjectRectangle, boxRec);
            }
            DbMultiTrans.commitTrans(objects);
        } catch (Exception e) {
            org.modelsphere.jack.util.ExceptionHandler.processUncatchedException(ApplicationContext
                    .getDefaultMainFrame(), e);
        }
    }

    public final void updateSelectionAction() throws DbException {
        if (!(ApplicationContext.getFocusManager().getFocusObject() instanceof ApplicationDiagram)) {
            setEnabled(false);
            return;
        }

        Object[] objects = ApplicationContext.getFocusManager().getSelectedObjects();
        if (objects.length < 2) {
            setEnabled(false);
            return;
        }

        for (int i = 0; i < objects.length; i++) {
            if (!(objects[i] instanceof ZoneBox && objects[i] instanceof ActionInformation)) {
                setEnabled(false);
                setVisible(false);
                return;
            }
        }
        setEnabled(true);
        setVisible(true);
    }

    // inner class domain
    static class AlignBoxDomain extends IntDomain {
        public static final int TOP = 0;
        public static final int LEFT = 1;
        public static final int BOTTOM = 2;
        public static final int RIGHT = 3;

        public static final AlignBoxDomain[] objectPossibleValues = new AlignBoxDomain[] {
                new AlignBoxDomain(AlignBoxDomain.TOP), new AlignBoxDomain(AlignBoxDomain.LEFT),
                new AlignBoxDomain(AlignBoxDomain.BOTTOM), new AlignBoxDomain(AlignBoxDomain.RIGHT) };

        public static final String[] stringPossibleValues = new String[] {
                LocaleMgr.action.getString("AlignTop"), LocaleMgr.action.getString("AlignLeft"),
                LocaleMgr.action.getString("AlignBottom"), LocaleMgr.action.getString("AlignRight") };

        public static AlignBoxDomain getInstance(int value) {
            return objectPossibleValues[objectPossibleValues[0].indexOf(value)];
        }

        protected AlignBoxDomain(int value) {
            super(value);
        }

        public final DbtAbstract duplicate() {
            return new AlignBoxDomain(value);
        }

        public final Domain[] getObjectPossibleValues() {
            return objectPossibleValues;
        }

        public final String[] getStringPossibleValues() {
            return stringPossibleValues;
        }
    }

}
