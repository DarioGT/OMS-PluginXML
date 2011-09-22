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

package org.modelsphere.jack.graphic;

import org.modelsphere.jack.util.SrVector;

public final class GraphLayoutLink implements Comparable {

    GraphLayoutNode parentNode;
    GraphLayoutNode childNode;
    boolean isHierar = false;
    boolean isMoveCloser = false;
    int prio = 0; /* priority if hierarchical */
    SrVector userLinks = new SrVector();

    /*
     * The notion of parent and child is considered only in the case <isHierar>.
     */
    GraphLayoutLink(GraphLayoutNode parentNode, GraphLayoutNode childNode, Object userLink) {
        this.parentNode = parentNode;
        this.childNode = childNode;
        userLinks.addElement(userLink);
        parentNode.links.addElement(this);
        childNode.links.addElement(this);
    }

    public final void setHierar(GraphLayoutNode parentNode, GraphLayoutNode childNode, int prio) {
        if (isHierar && this.prio >= prio)
            return; /* if already hierarchical with a higher priority, do not change the setting. */
        isHierar = true;
        this.parentNode = parentNode; /* insure that parentNode and childNode are in the good order */
        this.childNode = childNode;
        this.prio = prio;
    }

    public final SrVector getUserLinks() {
        return userLinks;
    }

    public final int compareTo(Object obj) {
        GraphLayoutLink that = (GraphLayoutLink) obj;
        if (prio < that.prio)
            return -1;
        if (prio == that.prio)
            return 0;
        return 1;
    }
}
