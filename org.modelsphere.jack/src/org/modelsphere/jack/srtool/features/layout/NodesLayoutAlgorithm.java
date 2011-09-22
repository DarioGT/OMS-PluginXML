package org.modelsphere.jack.srtool.features.layout;

import java.util.List;

import org.modelsphere.jack.srtool.features.layout.graph.Graph;
import org.modelsphere.jack.srtool.features.layout.graph.Node;

/**
 * The Interface NodesLayoutAlgorithm.
 */
public interface NodesLayoutAlgorithm extends LayoutAlgorithm {

    /**
     * Layout cluster nodes.
     * 
     * @param graph
     *            the graph
     * @param clusterNodes
     *            the cluster nodes
     * @throws Exception
     *             the exception
     */
    public void layoutClusterNodes(Graph graph, List<Node> clusterNodes) throws LayoutException;

}
