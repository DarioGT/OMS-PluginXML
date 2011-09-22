package org.modelsphere.jack.srtool.features.layout;

import java.awt.event.ActionEvent;
import java.util.List;

import org.modelsphere.jack.plugins.Plugin;
import org.modelsphere.jack.plugins.PluginSignature;
import org.modelsphere.jack.srtool.DefaultMainFrame;
import org.modelsphere.jack.srtool.MainFrameMenu;

/**
 * The Class LayoutPlugin.
 */
public abstract class LayoutPlugin implements Plugin {

    /*
     * (non-Javadoc)
     * 
     * @see org.modelsphere.jack.plugins.Plugin#doListenSelection()
     */
    @Override
    public final boolean doListenSelection() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modelsphere.jack.plugins.Plugin#execute(java.awt.event.ActionEvent)
     */
    @Override
    public final void execute(ActionEvent ev) throws Exception {
    }

    /**
     * Gets the layout algorithms.
     * 
     * @return the layout algorithms
     */
    public abstract List<LayoutAlgorithm> getLayoutAlgorithms();

    /**
     * Gets the default layout algorithms.
     * 
     * @return the default layout algorithms
     */
    public abstract List<LayoutAlgorithm> getDefaultLayoutAlgorithms();

    /*
     * (non-Javadoc)
     * 
     * @see org.modelsphere.jack.plugins.Plugin#getSignature()
     */
    @Override
    public final PluginSignature getSignature() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modelsphere.jack.plugins.Plugin#getSupportedClasses()
     */
    @Override
    public final Class<? extends Object>[] getSupportedClasses() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modelsphere.jack.plugins.Plugin#installAction(org.modelsphere.jack
     * .srtool.DefaultMainFrame , org.modelsphere.jack.srtool.MainFrameMenu)
     */
    @Override
    public final String installAction(DefaultMainFrame frame, MainFrameMenu menuManager) {
        return null;
    }

}
