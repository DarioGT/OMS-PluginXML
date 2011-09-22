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
package org.modelsphere.sms.oo.db.srtypes;

import org.modelsphere.jack.baseDb.db.srtypes.DbtAbstract;
import org.modelsphere.jack.baseDb.db.srtypes.Domain;
import org.modelsphere.jack.baseDb.db.srtypes.IntDomain;
import org.modelsphere.jack.util.DiscreteValuesComparator;
import org.modelsphere.sms.oo.international.LocaleMgr;

public class OOVisibility extends IntDomain {

    static final long serialVersionUID = -5740069237441550283L;

    public static final int PRIVATE = 1;
    public static final int PUBLIC = 2;
    public static final int PROTECTED = 3;
    public static final int LAST_OO_VISIBILITY = PROTECTED;

    public static final OOVisibility[] objectPossibleValues = new OOVisibility[] {
            new OOVisibility(PUBLIC), new OOVisibility(PRIVATE), new OOVisibility(PROTECTED) };

    public static final String[] stringPossibleValues = new String[] {
            LocaleMgr.misc.getString("public"), LocaleMgr.misc.getString("private"),
            LocaleMgr.misc.getString("protected") };

    private static DiscreteValuesComparator comparator = null;

    /*
     * public static DiscreteValuesComparator getComparator() { if( comparator == null ) {
     * comparator = new DiscreteValuesComparator(new Object[] {getInstance(PRIVATE),
     * getInstance(PROTECTED), getInstance(PUBLIC)} ); } return comparator; }
     */

    /*
     * public static OOVisibility getInstance(int value) { return
     * objectPossibleValues[objectPossibleValues[0].indexOf(value)]; }
     */

    //Parameterless constructor
    public OOVisibility() {
    }

    protected OOVisibility(int value) {
        super(value);
    }

    public DbtAbstract duplicate() {
        return new OOVisibility(value);
    }

    public Domain[] getObjectPossibleValues() {
        return objectPossibleValues;
    }

    public String[] getStringPossibleValues() {
        return stringPossibleValues;
    }
}
