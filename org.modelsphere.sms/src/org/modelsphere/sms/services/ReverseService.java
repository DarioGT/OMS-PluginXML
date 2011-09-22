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

package org.modelsphere.sms.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Vector;

/**
 * 
 * A reverse service. Read occurrences produced by client until the last one is met, and then closes
 * the connection
 * 
 */
public final class ReverseService implements org.modelsphere.jack.services.Service {
    private Vector vec_reply = new Vector();

    public void serve(InputStream i, OutputStream o) throws IOException {
        ServiceList.getSingleInstance();
        ObjectInputStream in = new ObjectInputStream(i);
        ObjectOutputStream out = new ObjectOutputStream(o);
        boolean isDone = false;

        while (!isDone) {
            try {
                Occurrence occ = (Occurrence) in.readObject();
                if (occ.isLast())
                    isDone = true;
                else
                    occ.create();

            } catch (ClassNotFoundException e) {
            }
        } /* end while */

        out.close();
    } /* end serve() */
}
