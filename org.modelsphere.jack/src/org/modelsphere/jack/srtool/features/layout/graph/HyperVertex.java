/**
 * 
 */
package org.modelsphere.jack.srtool.features.layout.graph;

import java.awt.Point;

/**
 * An hyper vertex is placed at the crossing point of hyper edges.
 * 
 * @author David
 * 
 */
public class HyperVertex extends CrossingVertex {

    /**
     * The Enum HyperedgeType.
     */
    public enum HyperVertexType {

        /** The incoming. */
        incoming,
        /** The outgoing. */
        outgoing
    }

    /** The type. */
    private HyperVertexType type;

    /**
     * Instantiates a new hyper vertex.
     * 
     * @param replacingEdges
     *            the replacing edges
     * @param type
     *            the type
     */
    public HyperVertex(Edge[] replacingEdges, HyperVertexType type) {
        this(new Point(0, 0), replacingEdges, type);
    }

    /**
     * Instantiates a new hyper vertex.
     * 
     * @param position
     *            the position
     * @param replacingEdges
     *            the replacing edges
     * @param type
     *            the type
     */
    public HyperVertex(Point position, Edge[] replacingEdges, HyperVertexType type) {
        super(position, replacingEdges);
        this.type = type;
    }

    /**
     * Gets the type.
     * 
     * @return the type
     */
    public HyperVertexType getType() {
        return type;
    }

}
