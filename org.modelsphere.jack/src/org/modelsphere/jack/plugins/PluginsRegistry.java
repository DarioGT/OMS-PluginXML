package org.modelsphere.jack.plugins;

import java.util.*;

import org.modelsphere.jack.plugins.io.PluginContext;
import org.modelsphere.jack.plugins.io.PluginLoader;
import org.modelsphere.jack.plugins.xml.XmlPluginDescriptor;

public final class PluginsRegistry implements Iterable<PluginDescriptor> {
    ArrayList<PluginDescriptor> pluginDescriptors = new ArrayList<PluginDescriptor>();
    ArrayList<PluginDescriptor> removedDescriptors = new ArrayList<PluginDescriptor>();

    private ArrayList<PluginListener> pluginListeners = new ArrayList<PluginListener>();

    PluginsRegistry() {

    }

    public final <T extends Plugin> ArrayList<T> getActivePluginInstances(Class<T> filterClass) {
        if (filterClass == null)
            return null;
        PluginMgr pluginMgr = PluginMgr.getSingleInstance();
        ArrayList<T> instances = new ArrayList<T>();
        Iterator<PluginDescriptor> iter = pluginDescriptors.iterator();
        String filterClassName = filterClass.getName();

        while (iter.hasNext()) {
            PluginDescriptor pluginDescriptor = iter.next();
            Class<? extends Plugin> pluginClass = pluginDescriptor.getPluginClass();
            String pluginClassName = pluginDescriptor.getClassName();

            boolean isInstance = filterClassName.equals(pluginClassName)
                    || (pluginClass != null && filterClass.isAssignableFrom(pluginClass));
            if (!isInstance)
                continue;

            if (pluginMgr.isActive(pluginDescriptor)) {
                PluginContext context = pluginDescriptor.getContext();
                Plugin plugin = context.getInstance();
                instances.add((T) plugin);
            }
        }
        return instances;
    }

    public final void addPluginListener(PluginListener l) {
        if (pluginListeners.indexOf(l) == -1) {
            pluginListeners.add(l);
        }
    }

    public final void removePluginListener(PluginListener l) {
        pluginListeners.remove(l);
    }

    private void firePluginListener(PluginDescriptor pluginDescriptor, boolean added) {
        for (int i = pluginListeners.size(); --i >= 0;) {
            PluginListener listener = pluginListeners.get(i);
            if (added)
                listener.pluginAdded(pluginDescriptor);
            else
                listener.pluginRemoved(pluginDescriptor);
        }
    }

    public void add(PluginDescriptor descriptor) {
    	
    	if (descriptor instanceof XmlPluginDescriptor) {
    		XmlPluginDescriptor newDescriptor = (XmlPluginDescriptor)descriptor; 
    		String name = newDescriptor.getName(); 
    		PluginDescriptor oldDescriptor = findDescriptorByName(pluginDescriptors, name);
    		pluginDescriptors.remove(oldDescriptor);
    		pluginDescriptors.add(descriptor);
    		removedDescriptors.remove(oldDescriptor);
    		firePluginListener(descriptor, true);
    		
    	} else {
    		pluginDescriptors.remove(descriptor);
            pluginDescriptors.add(descriptor);
            removedDescriptors.remove(descriptor);
            firePluginListener(descriptor, true);
    	} //end if
    	
       
    }

    private PluginDescriptor findDescriptorByName(List<PluginDescriptor> pluginDescriptorList, String nameToFind) {
    	PluginDescriptor foundDescriptor = null;
    	
		for (PluginDescriptor descriptor : pluginDescriptorList) {
			String name = descriptor.getName(); 
			if (nameToFind.equals(name)) {
				foundDescriptor = descriptor;
				break;
			}
		} //end for
		
		return foundDescriptor;
	}

	boolean remove(PluginDescriptor descriptor) {
        boolean removed = pluginDescriptors.remove(descriptor);
        if (removed) {
            removedDescriptors.add(descriptor);
            firePluginListener(descriptor, false);
        }
        return removed;
    }

    public boolean contains(PluginDescriptor descriptor) {
        return pluginDescriptors.contains(descriptor);
    }

    List<PluginDescriptor> getPluginDescriptors() {
        return Collections.unmodifiableList(pluginDescriptors);
    }

    public PluginDescriptor getPluginInfo(Class<? extends Plugin> pluginClass) {
        if (pluginClass == null)
            return null;
        Iterator<PluginDescriptor> pluginsIterator = this.pluginDescriptors.iterator();
        PluginDescriptor result = null;
        String pluginClassName = pluginClass.getName();
        while (pluginsIterator.hasNext()) {
            PluginDescriptor pluginDescriptor = pluginsIterator.next();
            if (pluginDescriptor.getClassName().equals(pluginClassName)) {
                result = pluginDescriptor;
                break;
            }
        }
        return result;
    }

    public PluginDescriptor getPluginInfo(String pluginClassName) {
        if (pluginClassName == null)
            return null;
        Iterator<PluginDescriptor> pluginsIterator = this.pluginDescriptors.iterator();
        PluginDescriptor result = null;
        while (pluginsIterator.hasNext()) {
            PluginDescriptor pluginDescriptor = pluginsIterator.next();
            if (pluginDescriptor.getClassName().equals(pluginClassName)) {
                result = pluginDescriptor;
                break;
            }
        }
        return result;
    }

    public PluginDescriptor getPluginInfo(Plugin plugin) {
        if (plugin == null)
            return null;
        PluginDescriptor result = getPluginInfo(plugin.getClass());
        if (result == null) {
            System.out
                    .println("WARNING:  getPluginInfo(Plugin) - A process is attempting access to a unknown plugin:  "
                            + plugin.getClass().getName());
        }
        return result;
    }

    /**
     * @param pluginClassName
     * @return the class or null if no plugin matches the specified class name. Note: The method
     *         returns null if the plugins are disabled using the global option.
     */
    public Class<? extends Plugin> getPluginClass(String pluginClassName) {
        if (pluginClassName == null)
            return null;

        PluginDescriptor pluginDescriptor = getPluginInfo(pluginClassName);
        PluginMgr pluginMgr = PluginMgr.getSingleInstance();

        if (pluginDescriptor == null || !pluginMgr.isActive(pluginDescriptor))
            return null;
        return pluginDescriptor.getPluginClass();
    }

    public List<Class<? extends Plugin>> getPluginClasses(Class<? extends Plugin> pluginclass) {
        if (pluginclass == null)
            return null;
        ArrayList<Class<? extends Plugin>> classes = new ArrayList<Class<? extends Plugin>>();

        PluginMgr pluginMgr = PluginMgr.getSingleInstance();
        Iterator<PluginDescriptor> iter = pluginDescriptors.iterator();
        while (iter.hasNext()) {
            PluginDescriptor pluginDescriptor = iter.next();
            if (!pluginMgr.isActive(pluginDescriptor))
                continue;
            Class<? extends Plugin> pluginClass = pluginDescriptor.getPluginClass();
            if (pluginclass.isAssignableFrom(pluginDescriptor.getClass())) {
                classes.add(pluginClass);
            }
        }
        return classes;
    }

    public List<PluginDescriptor> getActivePlugins() {
        PluginMgr pluginMgr = PluginMgr.getSingleInstance();
        ArrayList<PluginDescriptor> activePlugins = new ArrayList<PluginDescriptor>();
        Iterator<PluginDescriptor> pluginsIterator = this.pluginDescriptors.iterator();
        while (pluginsIterator.hasNext()) {
            PluginDescriptor pluginDescriptor = pluginsIterator.next();
            if (pluginMgr.isActive(pluginDescriptor)) {
                activePlugins.add(pluginDescriptor);
            }
        }
        return activePlugins;
    }

    public List<PluginDescriptor> getValidPlugins() {
        ArrayList<PluginDescriptor> validPlugins = new ArrayList<PluginDescriptor>();
        Iterator<PluginDescriptor> pluginsIterator = this.pluginDescriptors.iterator();
        while (pluginsIterator.hasNext()) {
            PluginDescriptor pluginDescriptor = pluginsIterator.next();
            if (PluginLoader.isValid(pluginDescriptor)) {
                validPlugins.add(pluginDescriptor);
            }
        }
        // include removed as valid
        pluginsIterator = this.removedDescriptors.iterator();
        while (pluginsIterator.hasNext()) {
            PluginDescriptor pluginDescriptor = pluginsIterator.next();
            if (PluginLoader.isValid(pluginDescriptor)) {
                validPlugins.add(pluginDescriptor);
            }
        }
        return validPlugins;
    }
    
    //return all plugins, including invalid, inactive or removed ones
    public List<PluginDescriptor> getAllPlugins() {
    	List<PluginDescriptor> allPlugins = new ArrayList<PluginDescriptor>();
    	allPlugins.addAll(this.pluginDescriptors); 
    	allPlugins.addAll(this.removedDescriptors); 
    	
        return allPlugins;
    }

    public final List<Plugin> getPluginInstances() {
        return getActivePluginInstances(Plugin.class);
    }

    @Override
    public Iterator<PluginDescriptor> iterator() {
        return pluginDescriptors.iterator();
    }

}
