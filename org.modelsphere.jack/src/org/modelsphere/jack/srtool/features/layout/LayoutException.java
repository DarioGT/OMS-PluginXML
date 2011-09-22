package org.modelsphere.jack.srtool.features.layout;

/**
 * The Class LayoutException.
 */
@SuppressWarnings("serial")
public class LayoutException extends Exception {
    private LayoutAlgorithm source;
    
    /**
     * Instantiates a new layout exception.
     */
    public LayoutException(LayoutAlgorithm source) {
        super();
//        super("Could not complete layout operation");
    }

    /**
     * Instantiates a new layout exception.
     * 
     * @param message
     *            An error message.
     */
    public LayoutException(LayoutAlgorithm source, String message) {
        super(message);
    }

    public LayoutAlgorithm getSource() {
        return source;
    }
}
