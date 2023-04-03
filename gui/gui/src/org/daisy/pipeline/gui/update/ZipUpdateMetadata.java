/*
 * DAISY Pipeline GUI
 * Copyright (C) 2006  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.daisy.pipeline.gui.update;

import java.io.IOException;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.util.i18n.XMLProperties;

/**
 * Represent the metadata of a Pipeline ZIP update patch.
 * 
 * @author Romain Deltour
 * 
 */
public class ZipUpdateMetadata {
	/** The key for property holding the description of the update patch */
	private static final String DESCRIPTION_KEY = "description";//$NON-NLS-1$
	/** The key for property holding the version of the update patch is for */
	private static final String VERSION_KEY = "version";//$NON-NLS-1$
	/** The path to the metadata properties file */
	private static final String PROP_PATH = "update.properties";//$NON-NLS-1$
	/** The ZipFile this is the metadata of */
	private ZipFile zipFile;
	/** The properties holding the metadata */
	private Properties properties;
	/** Whether the properties have been correctly loaded */
	private boolean isLoaded;

	/**
	 * Creates a new metadata instance for the given ZIP patch.
	 * 
	 * @param zipFile
	 *            a ZIP update patch.
	 */
	public ZipUpdateMetadata(ZipFile zipFile) {
		super();
		this.zipFile = zipFile;
		this.properties = new XMLProperties();
		this.isLoaded = loadProperties();
	}

	/**
	 * Returns the description of the update patch.
	 * 
	 * @return the description of the update patch.
	 */
	public String getDescription() {
		return properties.getProperty(DESCRIPTION_KEY,
				Messages.metadata_description_NA);
	}

	/**
	 * Returns the version of the Pipeline this update targets.
	 * 
	 * @return the version of the Pipeline this update targets.
	 */
	public String getVersion() {
		return properties.getProperty(VERSION_KEY,
				Messages.metadata_version_NA);
	}

	/**
	 * Whether the metadata has been found and properly loaded.
	 * 
	 * @return <code>true</code> if and only if the metadata properties file
	 *         has been found and properly loaded.
	 */
	public boolean isOK() {
		return isLoaded;
	}

	private boolean loadProperties() {
		ZipEntry propEntry = zipFile.getEntry(PROP_PATH);
		if (propEntry == null) {
			GuiPlugin.get().error("Unable to fetch the udpate patch metadata", //$NON-NLS-1$
					null);
			return false;
		}
		try {
			properties.loadFromXML(zipFile.getInputStream(propEntry));
			return true;
		} catch (IOException e) {
			GuiPlugin.get().error(
					"Unable to load properties from the metadata file", e);//$NON-NLS-1$
			return false;
		}
	}
}
