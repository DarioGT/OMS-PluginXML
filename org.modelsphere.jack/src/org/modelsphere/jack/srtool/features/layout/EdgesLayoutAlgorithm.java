package org.modelsphere.jack.srtool.features.layout;

import java.util.List;

import org.modelsphere.jack.srtool.features.layout.graph.Graph;
import org.modelsphere.jack.srtool.features.layout.graph.Node;

/**
 * The Interface EdgesLayoutAlgorithm.
 */
public interface EdgesLayoutAlgorithm extends LayoutAlgorithm {

    /**
     * Layout cluster edges.
     * 
     * @param graph
     *            the graph
     * @param clusterEdges
     *            the cluster edges
     * @throws Exception
     *             the exception
     */
    public void layoutClusterEdges(Graph graph, List<Node> clusterEdges) throws LayoutException;
}