/*************************************************************************

Copyright (C) 2010 Grandite

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

package org.modelsphere.jack.graphic.shape;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;

import org.modelsphere.jack.graphic.DiagramView;
import org.modelsphere.jack.graphic.GraphicComponent;
import org.modelsphere.jack.graphic.GraphicUtil;

public class OvalUnderlinedShape implements GraphicShape {

    public static final OvalUnderlinedShape singleton = new OvalUnderlinedShape();
    private static final double SQRT_TWO = Math.sqrt(2);

    public final void paint(Graphics g, DiagramView diagView, GraphicComponent gc,
            int renderingFlags) {
        Rectangle rect = gc.getRectangle();
        if (diagView != null)
            rect = diagView.zoom(rect);
        Graphics2D g2D = (Graphics2D) g;
        Stroke oldStroke = g2D.getStroke();
        g2D.setStroke(gc.getLineStroke());
        g2D.setColor(gc.getLineColor());
        
        //draw the circle
        int radius = (int) (rect.height * 0.9);
        int x = rect.x + (rect.width / 2) - (radius / 2);
        int y = rect.y;
        g2D.drawOval(x, y, radius, radius);
        
        //draw the line under the circle
        int x1 = rect.x;
        int x2 = rect.x + rect.width;
        y = rect.y + (int) (rect.height * 0.9);
        g2D.drawLine(x1, y, x2, y);
        
        //restore
        g2D.setStroke(oldStroke);
    }

    // Upon entry, we already checked that <x,y> is inside the rectangle.
    public final boolean contains(GraphicComponent gc, int x, int y) {
        Rectangle rect = gc.getRectangle();
        x -= rect.x + rect.width / 2;
        y -= rect.y + rect.height / 2;
        double dx = (double) x / (rect.width / 2);
        double dy = (double) y / (rect.height / 2);
        return (dx * dx + dy * dy <= 1);
    }

    public final Rectangle getContentRect(GraphicComponent gc) {
        Rectangle rect = gc.getRectangle();
        int inset = (int) gc.getLineStroke().getLineWidth();
        int width = Math.max(0, (int) (rect.width / SQRT_TWO) - 2 * inset);
        int height = Math.max(0, (int) (rect.height / SQRT_TWO) - 2 * inset);
        return GraphicUtil.rectangleResize(rect, width, height);
    }

    public final Dimension getShapeSize(GraphicComponent gc, Dimension contentSize) {
        int inset = (int) gc.getLineStroke().getLineWidth();
        return new Dimension((int) ((contentSize.width + 2 * inset) * SQRT_TWO),
                (int) ((contentSize.height + 2 * inset) * SQRT_TWO));
    }
}
