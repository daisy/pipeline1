/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
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
package org.daisy.pipeline.core.transformer;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

import org.daisy.pipeline.core.InputListener;

/**
 * Transformer loader that loads transformers packaged in jars.
 * 
 * @author Romain Deltour
 * 
 */
public class JarTransformerLoader extends AbstractTransformerLoader {

	private File jarFile;
	private String transformerName;

	/**
	 * @param inputListener
	 * @param transformerName
	 * @param jarFile
	 */
	public JarTransformerLoader(File jarFile, String transformerName,
			InputListener inputListener) {
		super(inputListener);
		this.jarFile = jarFile;
		this.transformerName = transformerName;
	}

	@Override
	protected ClassLoader getClassLoader(Collection<String> jars)
			throws MalformedURLException {
		return new URLClassLoader(new URL[] { jarFile.toURI().toURL() }, this
				.getClass().getClassLoader());
	}

	@Override
	public URL getTdfUrl() throws MalformedURLException {
		return new URL("jar:" + jarFile.toURI().toURL() + "!/"
				+ transformerName + "/transformer.tdf");
	}

	@Override
	public File getTransformerDir() {
		return null;
	}

	@Override
	protected boolean isLoadedFromJar() {
		return true;
	}

}
