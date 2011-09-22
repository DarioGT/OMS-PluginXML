package org.modelsphere.jack.srtool.features.layout;

import javax.swing.JComponent;

/**
 * The Interface LayoutAlgorithm.
 */
public interface LayoutAlgorithm {

    /**
     * Creates the option component.
     * 
     * @return the j component
     */
    public JComponent createOptionComponent();

    /**
     * Gets the text.
     * 
     * @return the text
     */
    public String getText();

}
