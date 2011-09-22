package org.modelsphere.jack.srtool.features.layout;

import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.modelsphere.jack.srtool.features.layout.graph.Node;

/**
 * The Class LayoutUtilities.
 */
public class LayoutUtilities {

    /**
     * Gets the polygon.
     * 
     * @param nodes
     *            the nodes
     * @return the polygon
     */
    public static Polygon getPolygon(List<Node> nodes) {
        Polygon polygon = new Polygon();
        for (Node node : nodes) {
            Rectangle2D rect = node.getBounds();
            if (polygon.contains(rect)) {
                continue;
            }
            int x = (int) rect.getX();
            int y = (int) rect.getY();
            int w = (int) rect.getWidth();
            int h = (int) rect.getHeight();
            polygon.addPoint(x, y);
            polygon.addPoint(x + w, y);
            polygon.addPoint(x, y + h);
            polygon.addPoint(x + w, y + h);
        }

        return polygon;
    }

    /**
     * Translate nodes.
     * 
     * @param nodes
     *            the nodes
     * @param dx
     *            the dx
     * @param dy
     *            the dy
     */
    public static void translateNodes(List<Node> nodes, int dx, int dy) {
        if (nodes == null) {
            return;
        }
        for (Node node : nodes) {
            node.translate(dx, dy);
        }
    }

    /**
     * Instantiates a new layout utilities.
     */
    private LayoutUtilities() {
    }

}
