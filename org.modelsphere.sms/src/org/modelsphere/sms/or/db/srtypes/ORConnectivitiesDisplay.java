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

// Possible Values :
// NONE
// MIN
// MAX
// MIN_MAX
package org.modelsphere.sms.or.db.srtypes;

import org.modelsphere.jack.baseDb.db.srtypes.*;
import org.modelsphere.sms.or.international.LocaleMgr;

public class ORConnectivitiesDisplay extends IntDomain {

    static final long serialVersionUID = 0;

    public static final int NONE = 0;
    public static final int MIN = 0x01;
    public static final int MAX = 0x02;
    public static final int MIN_MAX = MIN | MAX;

    public static final ORConnectivitiesDisplay[] objectPossibleValues = new ORConnectivitiesDisplay[] {
            new ORConnectivitiesDisplay(NONE), new ORConnectivitiesDisplay(MIN),
            new ORConnectivitiesDisplay(MAX), new ORConnectivitiesDisplay(MIN_MAX) };

    public static final String[] stringPossibleValues = new String[] {
            LocaleMgr.misc.getString("none"), LocaleMgr.misc.getString("minimum"),
            LocaleMgr.misc.getString("maximum"), LocaleMgr.misc.getString("both") };

    public static ORConnectivitiesDisplay getInstance(int value) {
        return objectPossibleValues[objectPossibleValues[0].indexOf(value)];
    }

    protected ORConnectivitiesDisplay(int value) {
        super(value);
    }

    //Parameterless constructor
    public ORConnectivitiesDisplay() {
    }

    public final DbtAbstract duplicate() {
        return new ORConnectivitiesDisplay(value);
    }

    public final Domain[] getObjectPossibleValues() {
        return objectPossibleValues;
    }

    public final String[] getStringPossibleValues() {
        return stringPossibleValues;
    }
}
