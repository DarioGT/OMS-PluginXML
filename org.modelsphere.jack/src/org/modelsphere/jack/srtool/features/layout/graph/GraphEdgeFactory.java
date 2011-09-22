package org.modelsphere.jack.srtool.features.layout.graph;

import org.jgrapht.EdgeFactory;
import org.modelsphere.jack.srtool.features.layout.graph.Edge.OrientationConstraint;
import org.modelsphere.jack.srtool.features.layout.graph.Edge.RelationType;

/**
 * This class has been created to satisfy JGraphT's need for an EdgeFactory when you build a graph.
 * Although, the user should note that an edge created by this class won't have his Go properties
 * set properly. You can set them afterwards (not encouraged) of create the edge yourself before
 * adding it to the graph (better practice).
 * 
 * @author Gabriel
 */
public class GraphEdgeFactory implements EdgeFactory<Vertex, Edge> {

    /*
     * (non-Javadoc)
     * 
     * @see org.jgrapht.EdgeFactory#createEdge(java.lang.Object, java.lang.Object)
     */
    @Override
    public Edge createEdge(Vertex sourceVertex, Vertex targetVertex) {
        Edge edge = new Edge(OrientationConstraint.Free);
        edge.setRelationType(RelationType.Association);
        edge.setRightAngled(false);
        return edge;
    }
}
