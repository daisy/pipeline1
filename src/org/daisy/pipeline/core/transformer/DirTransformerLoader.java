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
import java.util.Collection;
import java.util.Iterator;

import org.daisy.pipeline.core.DirClassLoader;
import org.daisy.pipeline.core.InputListener;

/**
 * Transformer loader that loads transformers packaged in directories.
 * 
 * @author Romain Deltour
 */
public class DirTransformerLoader extends AbstractTransformerLoader {

	private File tdfFile;
	private File transformersDir;

	/**
	 * @param inputListener
	 */
	public DirTransformerLoader(File tdfFile, File transformersDir,
			InputListener inputListener) {
		super(inputListener);
		this.tdfFile = tdfFile;
		this.transformersDir = transformersDir;
	}

	@Override
	protected ClassLoader getClassLoader(Collection<String> jars) {

		File dir = tdfFile.getAbsoluteFile().getParentFile();
		DirClassLoader classLoader = new DirClassLoader(transformersDir,
				transformersDir);

		for (Iterator<String> it = jars.iterator(); it.hasNext();) {
			String jar = it.next();
			classLoader.addJar(new File(dir, jar));
		}
		return classLoader;
	}

	@Override
	public URL getTdfUrl() throws MalformedURLException {
		return tdfFile.toURI().toURL();
	}

	@Override
	public File getTransformerDir() {
		return tdfFile.getParentFile();
	}

	@Override
	protected boolean isLoadedFromJar() {
		return false;
	}

}
