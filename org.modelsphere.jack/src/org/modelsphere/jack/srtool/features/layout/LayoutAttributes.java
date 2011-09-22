package org.modelsphere.jack.srtool.features.layout;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;

/**
 * The Class LayoutAttributes.
 */
public final class LayoutAttributes {

    /** The Constant CANVAS_HORIZONTAL_RESIZE_INCREMENT. */
    public static final LayoutAttribute<Double> CANVAS_HORIZONTAL_RESIZE_INCREMENT = new LayoutAttribute<Double>(
            "CANVAS_HORIZONTAL_RESIZE", 0D);

    /** The Constant CANVAS_VERTICAL_RESIZE_INCREMENT. */
    public static final LayoutAttribute<Double> CANVAS_VERTICAL_RESIZE_INCREMENT = new LayoutAttribute<Double>(
            "CANVAS_VERTICAL_RESIZE", 0D);

    /** The Constant CANVAS_DIMENSION. */
    public static final LayoutAttribute<Dimension2D> CANVAS_DIMENSION = new LayoutAttribute<Dimension2D>(
            "CANVAS_DIMENSION", new Dimension());

    /** The Constant RANDOMIZE. */
    public static final LayoutAttribute<Boolean> RANDOMIZE = new LayoutAttribute<Boolean>(
            "RANDOMIZE", Boolean.FALSE);

    /**
     * Instantiates a new layout attributes.
     */
    private LayoutAttributes() {
    }
}
