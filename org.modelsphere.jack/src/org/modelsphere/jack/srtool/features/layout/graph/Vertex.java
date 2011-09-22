package org.modelsphere.jack.srtool.features.layout.graph;

import java.awt.Dimension;
import java.awt.Point;

/**
 * <p>
 * Base class that abstracts a vertex that can be inserted in a {@link Graph}.
 * 
 * @author Gabriel
 * 
 */
public abstract class Vertex {

    /**
     * Gets the location.
     * 
     * @return the location
     */
    public abstract Point getLocation();

    /**
     * Sets the location.
     * 
     * @param point
     *            the new location
     */
    public abstract void setLocation(Point point);

    /**
     * Gets the dimension.
     * 
     * @return the dimension
     */
    public abstract Dimension getDimension();

    /**
     * Sets the dimension.
     * 
     * @param size
     *            the new dimension
     */
    public abstract void setDimension(Dimension size);
}
