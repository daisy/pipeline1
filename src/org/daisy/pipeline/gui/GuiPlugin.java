package org.daisy.pipeline.gui;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

import org.daisy.dmfc.core.DMFCCore;
import org.daisy.dmfc.core.script.Job;
import org.daisy.dmfc.core.script.Script;
import org.daisy.dmfc.exception.DMFCConfigurationException;
import org.daisy.pipeline.gui.jobs.StateManager;
import org.daisy.pipeline.gui.jobs.model.JobManager;
import org.daisy.pipeline.gui.messages.MessageManager;
import org.daisy.pipeline.gui.scripts.ScriptManager;
import org.daisy.util.file.EFolder;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class GuiPlugin extends AbstractUIPlugin {

    // The shared instance.
    private static GuiPlugin plugin;

    public static final String ID = "org.daisy.pipeline.gui";

    public static final String CORE_ID = "org.daisy.pipeline";

    private DMFCCore core;
    private UUID uuid;

    /**
     * The constructor.
     */
    public GuiPlugin() {
        super();
        plugin = this;
        uuid = UUID.randomUUID();
    }

    /**
     * This method is called upon plug-in activation.
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        try {
            initCore();
            MessageManager.getDefault().init();
            ScriptManager.getDefault().init();
            StateManager.getDefault().init();
            populateTestJobs();
        } catch (Exception e) {
            error("an error ocurred", e);
        }
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

    public UUID getUUID() {
        return uuid;
    }
    
    /**
     * Returns the shared instance.
     */
    public static GuiPlugin get() {
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
            return FileLocator.resolve(get().getBundle().getEntry(name));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static File getResourceFile(String name) {
        try {
            URL url = FileLocator.toFileURL(get().getBundle().getEntry(name));
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

    private void initCore() throws IOException, DMFCConfigurationException {
        Bundle coreBundle = Platform.getBundle(GuiPlugin.CORE_ID);
        URL url = FileLocator.toFileURL(coreBundle.getEntry("/"));
        File homeDir = new File(url.getPath());
        if (!DMFCCore.testHomeDirectory(homeDir)) {
            throw new DMFCConfigurationException(
                    "Cannot locate the Daisy Pipeline home directory");
        }
        core = new DMFCCore(null, homeDir);
    }

    public void error(String message, Throwable t) {
        getLog().log(new Status(IStatus.ERROR, ID, 0, message, t));
    }

    public void info(String message, Throwable t) {
        getLog().log(new Status(IStatus.INFO, ID, 0, message, t));
    }

    public void warn(String message, Throwable t) {
        getLog().log(new Status(IStatus.WARNING, ID, 0, message, t));
    }

    public static void populateTestJobs() {
        try {
            URL url = FileLocator.toFileURL(Platform.getBundle(
                    GuiPlugin.CORE_ID).getEntry("/scripts/_dev"));
            EFolder devDir = new EFolder(url.toURI());
            Collection devScripts = devDir.getFiles(true, ".+\\.taskScript");
            for (Iterator iter = devScripts.iterator(); iter.hasNext();) {
                File file = (File) iter.next();
                ScriptManager scriptMan = ScriptManager.getDefault();
                Script script = scriptMan.getScript(file.toURI());
                Job job = new Job(script);
                JobManager.getDefault().add(job);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
