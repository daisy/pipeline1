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
import java.net.URL;
import java.util.Properties;
import java.util.UUID;

import org.daisy.pipeline.core.PipelineCore;
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
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * 
 * The Pipeline GUI plugin activator. See the super javadoc for detailed
 * information on the role of this class.
 * 
 * <p>
 * Additionally, contains convenient methods for logging and resource access.
 * </p>
 * 
 * @author Romain Deltour
 * 
 */
public class GuiPlugin extends AbstractUIPlugin {
	/** The ID of the Pipeline GUI plugin, as specified in plugin.xml */
	public static final String ID = "org.daisy.pipeline.gui"; //$NON-NLS-1$
	/** The ID of the Pipeline Core plugin as specified in its plugin.xml */
	public static final String CORE_ID = "org.daisy.pipeline"; //$NON-NLS-1$

	// The shared instance.
	private static GuiPlugin plugin;

	private PipelineCore core;
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
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor createDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(ID, path);
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return the shared instance of this <code>GuiPlugin</code>
	 */
	public static GuiPlugin get() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the given icon key as listed in
	 * {@link IIconsKeys}
	 * 
	 * @param key
	 *            an icon key.
	 * @return an image descriptor for the given icon key.
	 * @see IIconsKeys
	 */
	public static ImageDescriptor getDescriptor(String key) {
		return get().getImageRegistry().getDescriptor(key);
	}

	/**
	 * Returns an image for the given icon key as listed in {@link IIconsKeys}
	 * 
	 * @param key
	 *            an icon key.
	 * @return an image for the given icon key.
	 * @see IIconsKeys
	 */
	public static Image getImage(String key) {
		return get().getImageRegistry().get(key);
	}

	/**
	 * Returns a file representing the given resource name (as a Bundle entry).
	 * 
	 * <p>
	 * The resource may be extracted into a cache on the file-system in order to
	 * get a file.
	 * </p>
	 * 
	 * @param name
	 *            the name of the resource to access
	 * @return a file representing the given resource.
	 * @see Bundle#getEntry(String)
	 * @see FileLocator#toFileURL(URL)
	 */
	public static File getResourceFile(String name) {
		try {
			URL url = FileLocator.toFileURL(get().getBundle().getEntry(name));
			return new File(url.toURI());
		} catch (Exception e) {
			plugin.error("Couldn't fetch resource " + name, e); //$NON-NLS-1$
		}
		return null;
	}

	/**
	 * Returns a file URL representing the given resource name (as a Bundle
	 * entry).
	 * 
	 * <p>
	 * The resource may be extracted into a cache on the file-system in order to
	 * get a file URL.
	 * </p>
	 * 
	 * @param name
	 *            the name of the resource to access
	 * @return a file URL representing the given resource.
	 * @see Bundle#getEntry(String)
	 * @see FileLocator#toFileURL(URL)
	 */
	public static URL getResourceURL(String name) {
		try {
			return FileLocator.resolve(get().getBundle().getEntry(name));
		} catch (Exception e) {
			plugin.error("Couldn't fetch resource " + name, e); //$NON-NLS-1$
		}
		return null;
	}

	/**
	 * Returns an image descriptor for the given shared image key. See
	 * {@link IWorkbench#getSharedImages()}.
	 * 
	 * @param key
	 *            a key to a shared image.
	 * @return an image descriptor for the given shared image key.
	 * @see IWorkbench#getSharedImages()
	 */
	public static ImageDescriptor getSharedDescriptor(String key) {
		return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
				key);
	}

	/**
	 * Returns an image for the given shared image key. See
	 * {@link IWorkbench#getSharedImages()}.
	 * 
	 * @param key
	 *            a key to a shared image.
	 * @return an image for the given shared image key.
	 * @see IWorkbench#getSharedImages()
	 */
	public static Image getSharedImage(String key) {
		return PlatformUI.getWorkbench().getSharedImages().getImage(key);
	}

	/**
	 * Returns the internal instance of the Pipeline Core.
	 * 
	 * @return the internal instance of the Pipeline Core.
	 */
	public PipelineCore getCore() {
		return core;
	}

	/**
	 * Returns a UUID to identify this Pipeline GUI instance session.
	 * 
	 * @return a UUID to identify this Pipeline GUI instance session.
	 */
	public UUID getUUID() {
		return uuid;
	}

	/**
	 * Re-initialize and reload the Pipeline Core. Re-populates the
	 * {@link ScriptManager}.
	 */
	public void reloadCore() {
		initCore();
		ScriptManager.getDefault().clear();
		ScriptManager.getDefault().init();
	}

	/**
	 * This method is called upon plug-in activation.
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		initLog();
		initCore();
		MessageManager.getDefault().init();
		ScriptManager.getDefault().init();
		StateManager.getDefault().init();
		logSystemInfo();
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
	 * Sends an error message to the plugin logger.
	 * 
	 * @param message
	 *            the message to send
	 * @param t
	 *            a low-level exception, or null if not applicable
	 */
	public void error(String message, Throwable t) {
		getLog().log(new Status(IStatus.ERROR, ID, 0, message, t));
	}

	/**
	 * Sends an info message to the plugin logger.
	 * 
	 * @param message
	 *            the message to send
	 * @param t
	 *            a low-level exception, or null if not applicable
	 */
	public void info(String message, Throwable t) {
		getLog().log(new Status(IStatus.INFO, ID, 0, message, t));
	}

	/**
	 * Sends an warning message to the plugin logger.
	 * 
	 * @param message
	 *            the message to send
	 * @param t
	 *            a low-level exception, or null if not applicable
	 */
	public void warn(String message, Throwable t) {
		getLog().log(new Status(IStatus.WARNING, ID, 0, message, t));
	}

	private void initCore() {
		File homeDir = PipelineUtil.getDir(PipelineUtil.HOME_DIR);
		Properties userProps = PipelineUtil.convPrefToProperties();
		try {
			core = new PipelineCore(null, homeDir, userProps);
		} catch (Exception e) {
			error("Error while intializing the Pipeline core", e); //$NON-NLS-1$
		}
	}

	private void initLog() {
		IPath logPath = Platform.getLocation().append(
				new Path("./.metadata/.log")); //$NON-NLS-1$
		File logFile = logPath.toFile();
		if ((logFile != null) && logFile.exists()) {
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
		reg.put(IIconsKeys.MESSAGE_INFO_FINER,
				createDescriptor(IIconsKeys.MESSAGE_INFO_FINER));
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
		reg.put(IIconsKeys.STATE_WAITING,
				createDescriptor(IIconsKeys.STATE_WAITING));
		reg.put(IIconsKeys.TREE_CATEGORY,
				createDescriptor(IIconsKeys.TREE_CATEGORY));
	}

}
