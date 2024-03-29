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
package org.modelsphere.jack.baseDb.screen.plugins;

import java.awt.Component;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;

import org.modelsphere.jack.baseDb.db.DbException;
import org.modelsphere.jack.baseDb.db.DbObject;
import org.modelsphere.jack.baseDb.screen.Renderer;
import org.modelsphere.jack.baseDb.screen.ScreenView;
import org.modelsphere.jack.international.LocaleChangeManager;
import org.modelsphere.jack.srtool.ApplicationContext;

/**
 * @author nicolask
 * 
 *         TD-- To change the template for this generated type comment go to Window - Preferences -
 *         Java - Code Style - Code Templates
 */
public class TimestampRenderer extends DefaultTableCellRenderer implements Renderer {

    public static final TimestampRenderer singleton = new TimestampRenderer();
    String datetimeFormat = null;

    protected TimestampRenderer() {
        String path = ApplicationContext.getApplicationDirectory();
        if (LocaleChangeManager.getLocale().getLanguage().compareTo("fr") == 0)
            datetimeFormat = "dd/MM/yyyy HH:mm:ss";
        else
            datetimeFormat = "MM/dd/yyyy HH:mm:ss";
    }

    public Component getTableCellRendererComponent(ScreenView screenView, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (value != null) {
            DateFormat dateFormat = DateFormat.getInstance();
            DateFormat df = new SimpleDateFormat(datetimeFormat);
            df.setTimeZone(TimeZone.getDefault());
            setText(df.format((Long) value));
        } else
            setText("");

        setHorizontalAlignment(javax.swing.JLabel.LEFT);
        if (isSelected) {
            setBackground(screenView.getSelectionBackground());
            setForeground(screenView.getSelectionForeground());
        } else {
            setBackground(screenView.getBackground());
            setForeground(screenView.getForeground());
        }

        return this;

    }

    public Object wrapValue(DbObject dbo, Object value) throws DbException {
        return value;
    }

    public Object unwrapValue(Object value) {
        return value;
    }

    public int getDisplayWidth() {
        return 40;
    }

}
