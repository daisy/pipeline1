package org.daisy.pipeline.gui;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.daisy.dmfc.core.DMFCCore;
import org.daisy.dmfc.core.FakeCore;
import org.daisy.dmfc.exception.DMFCConfigurationException;
import org.daisy.pipeline.gui.jobs.StateManager;
import org.daisy.pipeline.gui.messages.MessageManager;
import org.daisy.pipeline.gui.scripts.ScriptManager;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class PipelineGuiPlugin extends AbstractUIPlugin {

    // The shared instance.
    private static PipelineGuiPlugin plugin;
    public static final String ID = "org.daisy.pipeline.gui";
    public static final String CORE_ID = "org.daisy.pipeline";
    private DMFCCore core;

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
        initPipelineCore();
        MessageManager.getDefault().init();
        StateManager.getInstance().init();
        ScriptManager.getDefault();
        FakeCore.populateTestJobs();
    }

    /**
     * This method is called when the plug-in is stopped.
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
        plugin = null;
    }

    public DMFCCore getCore() {
        return core;
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
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return AbstractUIPlugin.imageDescriptorFromPlugin(ID, path);
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
            URL url = FileLocator.toFileURL(getDefault().getBundle().getEntry(
                    name));
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

    private void initPipelineCore() {

        Bundle coreBundle = Platform.getBundle(PipelineGuiPlugin.CORE_ID);
        try {
            URL url = FileLocator.toFileURL(coreBundle.getEntry("/"));
            File homeDir = new File(url.toURI());
            if (!DMFCCore.testHomeDirectory(homeDir)) {
                throw new DMFCConfigurationException(
                        "Cannot locate the Daisy Pipeline home directory");
            }
            core = new DMFCCore(null, homeDir);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DMFCConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
