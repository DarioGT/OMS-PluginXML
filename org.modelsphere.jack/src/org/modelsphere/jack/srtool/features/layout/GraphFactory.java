package org.modelsphere.jack.srtool.features.layout;

import java.util.List;

import org.modelsphere.jack.baseDb.db.DbException;
import org.modelsphere.jack.baseDb.db.DbGraphicalObjectI;
import org.modelsphere.jack.baseDb.db.DbObject;
import org.modelsphere.jack.graphic.Diagram;
import org.modelsphere.jack.graphic.GraphicComponent;
import org.modelsphere.jack.srtool.features.layout.graph.Graph;

/**
 * A factory for creating Graph objects.
 */
public class GraphFactory {

    /** The default factory. */
    private static GraphFactory defaultFactory = new GraphFactory();

    /**
     * Gets the default factory.
     * 
     * @return the default factory
     */
    public static GraphFactory getDefaultFactory() {
        return defaultFactory;
    }

    /**
     * Sets the default.
     * 
     * @param graphFactory
     *            the new default
     */
    public static void setDefault(GraphFactory graphFactory) {
        if (graphFactory != null) {
            defaultFactory = graphFactory;
        }
    }

    /**
     * Creates a new Graph object.
     * 
     * @param diagramGo
     *            the diagram go
     * @param diagram
     *            the diagram
     * @return the graph
     * @throws DbException
     *             the db exception
     */
    public Graph createGraph(DbObject diagramGo, Diagram diagram, List<DbGraphicalObjectI> scope) throws DbException {
        return null;
    }
}
