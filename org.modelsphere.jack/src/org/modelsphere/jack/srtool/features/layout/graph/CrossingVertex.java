/**
 * 
 */
package org.modelsphere.jack.srtool.features.layout.graph;

import java.awt.Dimension;
import java.awt.Point;

/**
 * The Class CrossingVertex.
 * 
 * @author David
 */
public abstract class CrossingVertex extends Vertex {

    /** The location. */
    protected Point location;

    /** The replaced edges. */
    protected Edge[] replacedEdges;

    /**
     * Instantiates a new crossing vertex.
     * 
     * @param location
     *            the location
     * @param crossingEdges
     *            the crossing edges
     */
    protected CrossingVertex(Point location, Edge[] crossingEdges) {
        this.location = location;
        replacedEdges = crossingEdges;
    }

    /**
     * Gets the location.
     * 
     * @return the location
     * @see org.modelsphere.jack.srtool.features.layout.graph.Vertex#getLocation()
     */
    @Override
    public Point getLocation() {
        return location;
    }

    /**
     * Sets the location.
     * 
     * @param point
     *            the new location
     * @see org.modelsphere.jack.srtool.features.layout.graph.Vertex#setLocation(java.awt.Point)
     */
    @Override
    public void setLocation(Point point) {
        location = point;
    }

    /**
     * Gets the dimension.
     * 
     * @return the dimension
     * @see org.modelsphere.jack.srtool.features.layout.graph.Vertex#getDimension()
     */
    @Override
    public Dimension getDimension() {
        return new Dimension(0, 0);
    }

    /**
     * Sets the dimension.
     * 
     * @param size
     *            the new dimension
     * @see org.modelsphere.jack.srtool.features.layout.graph.Vertex#setDimension(java.awt.Dimension)
     */
    @Override
    public void setDimension(Dimension size) {
        return;
    }

    /**
     * Gets the replaced edges.
     * 
     * @return the replaced edges
     */
    public Edge[] getReplacedEdges() {
        return replacedEdges;
    }

}
