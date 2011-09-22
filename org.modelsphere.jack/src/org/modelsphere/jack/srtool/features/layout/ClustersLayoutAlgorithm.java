package org.modelsphere.jack.srtool.features.layout;

import java.util.HashMap;

import org.modelsphere.jack.srtool.features.layout.graph.Graph;
import org.modelsphere.jack.srtool.features.layout.graph.Node;

/**
 * The Interface ClustersLayoutAlgorithm.
 */
public interface ClustersLayoutAlgorithm extends LayoutAlgorithm {

    /**
     * Layout clusters.
     * 
     * @param graph
     *            the graph
     * @param clusters
     *            the clusters
     * @param attributes
     *            the attributes
     * @throws Exception
     *             the exception
     */
    public void layoutClusters(Graph graph, HashMap<Node, Integer> clusters,
            HashMap<LayoutAttribute<?>, Object> attributes) throws LayoutException;
}
