/*
 * DAISY Pipeline GUI Copyright (C) 2006 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.daisy.pipeline.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.UUID;

import org.daisy.pipeline.core.DMFCCore;
import org.daisy.pipeline.exception.DMFCConfigurationException;
import org.daisy.pipeline.gui.model.MessageManager;
import org.daisy.pipeline.gui.model.ScriptManager;
import org.daisy.pipeline.gui.model.StateManager;
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
import org.osgi.framework.BundleContext;

public class GuiPlugin extends AbstractUIPlugin {

    public static final String ID = "org.daisy.pipeline.gui"; //$NON-NLS-1$
    public static final String CORE_ID = "org.daisy.pipeline"; //$NON-NLS-1$

    // The shared instance.
    private static GuiPlugin plugin;

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
     * Returns an image descriptor for the image file at the given plug-in
     * relative path.
     * 
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor createDescriptor(String path) {
        return AbstractUIPlugin.imageDescriptorFromPlugin(ID, path);
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

    public static File getResourceFile(String name) {
        try {
            URL url = FileLocator.toFileURL(get().getBundle().getEntry(name));
            return new File(url.toURI());
        } catch (Exception e) {
            plugin.error("Couldn't fetch resource " + name, e); //$NON-NLS-1$
        }
        return null;
    }

    public static URL getResourceURL(String name) {
        try {
            return FileLocator.resolve(get().getBundle().getEntry(name));
        } catch (Exception e) {
            plugin.error("Couldn't fetch resource " + name, e); //$NON-NLS-1$
        }
        return null;
    }

    public static ImageDescriptor getSharedDescriptor(String key) {
        return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
                key);
    }

    public static Image getSharedImage(String key) {
        return PlatformUI.getWorkbench().getSharedImages().getImage(key);
    }

    public void error(String message, Throwable t) {
        getLog().log(new Status(IStatus.ERROR, ID, 0, message, t));
    }

    public DMFCCore getCore() {
        return core;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void info(String message, Throwable t) {
        getLog().log(new Status(IStatus.INFO, ID, 0, message, t));
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
            logSystemInfo();
        } catch (Exception e) {
            error("an error ocurred", e); //$NON-NLS-1$
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

    public void warn(String message, Throwable t) {
        getLog().log(new Status(IStatus.WARNING, ID, 0, message, t));
    }

    private void initCore() throws IOException, DMFCConfigurationException {
        File homeDir = PipelineUtil.getDir(PipelineUtil.HOME_DIR);
        Properties userProps = PipelineUtil.convPrefToProperties();
        core = new DMFCCore(null, homeDir, userProps);
    }

    private void initLog() {
        IPath logPath = Platform.getLocation().append(
                new Path("./.metadata/.log")); //$NON-NLS-1$
        File logFile = logPath.toFile();
        if (logFile != null && logFile.exists()) {
            logFile.delete();
        }
    }

    private void logSystemInfo() {
        // Log location info
        info("Plugin State Location: " //$NON-NLS-1$
                + Platform.getStateLocation(getBundle()).toString(), null);
        info("User Location: " + Platform.getUserLocation().getURL(), null); //$NON-NLS-1$
        info("Instance Location: " + Platform.getInstanceLocation().getURL(), //$NON-NLS-1$
                null);
        info("Install Location: " + Platform.getInstallLocation().getURL(), //$NON-NLS-1$
                null);
        info("Configuration Location: " //$NON-NLS-1$
                + Platform.getConfigurationLocation().getURL(), null);
        // Log system properties
        Properties properties = System.getProperties();
        for (Object key : properties.keySet()) {
            info(key + "= " + properties.getProperty((String) key), null); //$NON-NLS-1$
        }
    }

    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);
        reg.put(IIconsKeys.ACTION_STOP,
                createDescriptor(IIconsKeys.ACTION_STOP));
        reg.put(IIconsKeys.HELP_TOC_ITEM,
                createDescriptor(IIconsKeys.HELP_TOC_ITEM));
        reg.put(IIconsKeys.HELP_TOC_SECTION,
                createDescriptor(IIconsKeys.HELP_TOC_SECTION));
        reg.put(IIconsKeys.HELP_TOC_SUBSECTION,
                createDescriptor(IIconsKeys.HELP_TOC_SUBSECTION));
        reg.put(IIconsKeys.MESSAGE_DEBUG,
                createDescriptor(IIconsKeys.MESSAGE_DEBUG));
        reg.put(IIconsKeys.MESSAGE_ERROR,
                createDescriptor(IIconsKeys.MESSAGE_ERROR));
        reg.put(IIconsKeys.MESSAGE_INFO,
                createDescriptor(IIconsKeys.MESSAGE_INFO));
        reg.put(IIconsKeys.MESSAGE_WARNING,
                createDescriptor(IIconsKeys.MESSAGE_WARNING));
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
        reg.put(IIconsKeys.TREE_CATEGORY,
                createDescriptor(IIconsKeys.TREE_CATEGORY));
    }

}
