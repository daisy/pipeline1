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
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
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
            initLog();
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

    public static ImageDescriptor getDescriptor(String key) {
        return get().getImageRegistry().getDescriptor(key);
    }

    public static Image getImage(String key) {
        return get().getImageRegistry().get(key);
    }

    public static Image getSharedImage(String key) {
        return PlatformUI.getWorkbench().getSharedImages().getImage(key);
    }

    public static ImageDescriptor getSharedDescriptor(String key) {
        return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
                key);
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in
     * relative path.
     * 
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor createDescriptor(String path) {
        return AbstractUIPlugin.imageDescriptorFromPlugin(ID, path);
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

    private void initLog() {
        IPath logPath = Platform.getLocation().append(
                new Path("./.metadata/.log"));
        File logFile = logPath.toFile();
        if (logFile != null && logFile.exists()) {
            logFile.delete();
        }
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

    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);
        reg.put(IIconsKeys.MESSAGE_DEBUG, createDescriptor(IIconsKeys.MESSAGE_DEBUG));
        reg.put(IIconsKeys.MESSAGE_ERROR, createDescriptor(IIconsKeys.MESSAGE_ERROR));
        reg.put(IIconsKeys.MESSAGE_INFO, createDescriptor(IIconsKeys.MESSAGE_INFO));
        reg.put(IIconsKeys.MESSAGE_WARNING, createDescriptor(IIconsKeys.MESSAGE_WARNING));
        reg.put(IIconsKeys.STATE_CANCELED,
                createDescriptor(IIconsKeys.STATE_CANCELED));
        reg.put(IIconsKeys.STATE_FAILED,
                createDescriptor(IIconsKeys.STATE_FAILED));
        reg.put(IIconsKeys.STATE_FINISHED,
                createDescriptor(IIconsKeys.STATE_FINISHED));
        reg.put(IIconsKeys.STATE_IDLE, createDescriptor(IIconsKeys.STATE_IDLE));
        reg.put(IIconsKeys.STATE_RUNNING,
                createDescriptor(IIconsKeys.STATE_RUNNING));
        reg.put(IIconsKeys.STATE_RUNNING,
                createDescriptor(IIconsKeys.STATE_WAITING));
        reg.put(IIconsKeys.TREE_CATEGORY, createDescriptor(IIconsKeys.TREE_CATEGORY));
    }

}
