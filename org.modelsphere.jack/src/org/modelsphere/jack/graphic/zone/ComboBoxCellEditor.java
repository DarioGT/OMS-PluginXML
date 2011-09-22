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

package org.modelsphere.jack.graphic.zone;

import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComponent;
import javax.swing.JComboBox;

import org.modelsphere.jack.graphic.*;

public abstract class ComboBoxCellEditor implements CellEditor {

    protected Diagram diagram;
    protected ZoneBox box;
    protected CellID cellID;
    protected ZoneCell value;
    protected JComboBox combo;

    public final JComponent getComponent(ZoneBox box, CellID cellID, ZoneCell value,
            DiagramView view, Rectangle cellRect) {
        this.box = box;
        this.cellID = cellID;
        this.value = value;
        diagram = box.getDiagram();
        combo = new JComboBox();
        populateCombo();
        combo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                diagram.removeEditor(CellEditor.NORMAL_ENDING);
            }
        });
        return combo;
    }

    public boolean isEmpty() {

        int count = combo.getItemCount();
        return (count == 0);
    }

    public void showPopup() {
        combo.showPopup();
    }

    public abstract void populateCombo();

    public abstract void stopEditing(int endCode);
}
