/*************************************************************************

Copyright (C) 2008 Grandite

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

You can reach Grandite at: 

20-1220 Lebourgneuf Blvd.
Quebec, QC
Canada  G2K 2G4

or

open-modelsphere@grandite.com

 **********************************************************************/
package org.modelsphere.sms.oo.java.db.srtypes;

import org.modelsphere.jack.baseDb.db.srtypes.DbtAbstract;
import org.modelsphere.jack.baseDb.db.srtypes.Domain;
import org.modelsphere.jack.baseDb.db.srtypes.IntDomain;
import org.modelsphere.sms.oo.java.international.LocaleMgr;

public final class JVClassCategory extends IntDomain {

    static final long serialVersionUID = 0;

    public static final int CLASS = 1;
    public static final int INTERFACE = 2;
    public static final int EXCEPTION = 3;

    public static final JVClassCategory[] objectPossibleValues = new JVClassCategory[] {
            new JVClassCategory(CLASS), new JVClassCategory(INTERFACE),
            new JVClassCategory(EXCEPTION) };

    public static final String[] stringPossibleValues = new String[] {
            LocaleMgr.misc.getString("class"), LocaleMgr.misc.getString("interface"),
            LocaleMgr.misc.getString("exception") };

    public static JVClassCategory getInstance(int value) {
        return objectPossibleValues[objectPossibleValues[0].indexOf(value)];
    }

    //Constructors
    protected JVClassCategory(int value) {
        super(value);
    }

    //Parameterless constructor
    public JVClassCategory() {
    }

    public DbtAbstract duplicate() {
        return new JVClassCategory(value);
    }

    public Domain[] getObjectPossibleValues() {
        return objectPossibleValues;
    }

    public String[] getStringPossibleValues() {
        return stringPossibleValues;
    }
}
