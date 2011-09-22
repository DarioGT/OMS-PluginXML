package org.modelsphere.jack.srtool.features.layout.graph;

import java.awt.Point;

/**
 * <p>
 * Represents a <i>dummy-vertex</i> (also referred to as, <i>null-node</i>) that can stand as a
 * vertex in a graph.
 * <p>
 * <i>Dummy-vertex</i> are mostly inserted to replace some edges by a path of new edges. The object
 * can store the reference of the replaced edge (or edges) that it at the origin of its insertion.
 * As they are not supposed to change over the life time of a <tt>DummyVertex</tt>, the replaced
 * edge(s) can only be set at creation time and not later.
 * <p>
 * It also can be applied a location (a coordinate in Z^2).
 * 
 * @author Gabriel
 * 
 */
public class DummyVertex extends CrossingVertex {

    /**
     * Instantiates a new dummy vertex.
     */
    public DummyVertex() {
        this(new Point(0, 0), new Edge[0]);
    }

    /**
     * Instantiates a new dummy vertex.
     * 
     * @param position
     *            An initial position.
     */
    public DummyVertex(Point position) {
        this(position, new Edge[0]);
    }

    /**
     * Instantiates a new dummy vertex.
     * 
     * @param replacingEdge
     *            An <tt>Edge</tt> replaced by the this <i>dummy-vertex</i>.
     */
    public DummyVertex(Edge replacingEdge) {
        this(new Edge[] { replacingEdge });
    }

    /**
     * Instantiates a new dummy vertex.
     * 
     * @param replacingEdges
     *            An array of each <tt>Edge</tt> that is replaced by this <i>dummy-vertex</i>.
     */
    public DummyVertex(Edge[] replacingEdges) {
        this(new Point(0, 0), replacingEdges);
    }

    /**
     * Instantiates a new dummy vertex.
     * 
     * @param position
     *            An initial position.
     * @param replacingEdges
     *            An array of each <tt>Edge</tt> that is replaced by this <i>dummy-vertex</i>.
     */
    public DummyVertex(Point position, Edge[] replacingEdges) {
        super(position, replacingEdges);
    }

    /**
     * Sets the location.
     * 
     * @param x
     *            the x
     * @param y
     *            the y
     */
    public void setLocation(int x, int y) {
        setLocation(new Point(x, y));
    }

    /**
     * Gets the x.
     * 
     * @return the x
     */
    public int getX() {
        return location.x;
    }

    /**
     * Gets the y.
     * 
     * @return the y
     */
    public int getY() {
        return location.y;
    }

}
