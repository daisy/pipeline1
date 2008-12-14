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
package org.daisy.pipeline.scripts.ui;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.stax.StaxEntityResolver;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;

/**
 * 
 * JFac label provider for the script tree.
 * <p>
 * Provides labels for
 * </p>
 * <ul>
 * <li>Scripts files</li>
 * <li>Scripts directories used as categorization</li>
 * </ul>
 * 
 * @author Romain Deltour
 * 
 */
public class ScriptsLabelProvider extends LabelProvider implements
		ILabelProvider {

	/**
	 * Creates a new instance of this label provider.
	 */
	public ScriptsLabelProvider() {
	}

	/**
	 * Returns the text for the label of the given script file.
	 * 
	 * There is a hierarchy in the file structure that organizes the.
	 * transformers by type:
	 * <ul>
	 * <li>Directories does not have associated script handler objects, they
	 * must be labeled by their name.</li>
	 * <li>Script file does have an associated script handler, they are labeled
	 * with the script name retrieved from the handler.</li>
	 * </ul>
	 * 
	 * @param obj
	 *            An element in the script file tree.
	 */
	@Override
	public String getText(Object obj) {
		String text = null;
		File file = (File) obj;
		if (file.isDirectory()) {
			text = file.getName();
		} else {
			// Script script = scriptMan.getScript((file).toURI());
			// text = (script != null) ? script.getNicename() : file.getName();
			try {
				text = getNiceName(file.toURI());
			} catch (Exception e) {
				// Do nothing
			}
			if (text == null) {
				text = file.getName();
			}
		}
		return text;
	}

	private String getNiceName(URI scriptURI) throws IOException,
			XMLStreamException {
		Map<String, Object> properties = StAXInputFactoryPool.getInstance()
				.getDefaultPropertyMap(false);
		XMLInputFactory xif = null;
		try {
			xif = StAXInputFactoryPool.getInstance().acquire(properties);
			xif.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver
					.getInstance()));
			XMLStreamReader xsr = xif.createXMLStreamReader(scriptURI.toURL()
					.openStream());
			while (xsr.hasNext()) {
				xsr.next();
				if (xsr.isStartElement()) {
					if (xsr.getLocalName().equals("nicename")) {
						return xsr.getElementText();
					}
				}
			}
			xsr.close();
		} catch (CatalogExceptionNotRecoverable e) {
			// TODO log exception
			e.printStackTrace();
		} finally {
			StAXInputFactoryPool.getInstance().release(xif, properties);
		}

		return null;
	}
}
