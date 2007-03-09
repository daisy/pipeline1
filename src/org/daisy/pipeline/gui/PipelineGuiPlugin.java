package org.daisy.pipeline.gui;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class PipelineGuiPlugin extends AbstractUIPlugin {

    // The shared instance.
    private static PipelineGuiPlugin plugin;
    public static final String PLUGIN_ID = "org.daisy.pipeline.gui";

    /**
     * The constructor.
     */
    public PipelineGuiPlugin() {
        if (plugin != null) {
            throw new IllegalStateException("Plug-in class already exists");
        }
        plugin = this;
    }

    /**
     * This method is called upon plug-in activation.
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
    }

    /**
     * This method is called when the plug-in is stopped.
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
        plugin = null;
    }

    /**
     * Returns the shared instance.
     */
    public static PipelineGuiPlugin getDefault() {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in
     * relative path.
     * 
     * @param path
     *            the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    public static ImageDescriptor getIcon(String key) {
        return getImageDescriptor(IIconsKeys.SIZE_DEFAULT_DIR + key);
    }

    public static ImageDescriptor getIcon(String key, int size) {
        String sizeDir;
        switch (size) {
        case IIconsKeys.SIZE_16:
            sizeDir = IIconsKeys.SIZE_16_DIR;
            break;
        case IIconsKeys.SIZE_22:
            sizeDir = IIconsKeys.SIZE_22_DIR;
            break;
        default:
            return null;
        }
        return getImageDescriptor(sizeDir + key);
    }
    
    public static URL getResourceURL(String name) {
        try {
            return FileLocator.resolve(getDefault().getBundle().getEntry(name));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    public static File getResourceFile(String name) {
        try {
            URL url = FileLocator.toFileURL(getDefault().getBundle().getEntry(name));
            return new File(url.toURI());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
