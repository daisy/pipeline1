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
import java.io.FileFilter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.exception.TransformerDisabledException;

/**
 * @author Romain Deltour
 * 
 */
public enum TransformerHandlerLoader {
    INSTANCE;

    private final ConcurrentMap<String, TransformerHandler> handlersMap = new ConcurrentHashMap<String, TransformerHandler>();
    private InputListener inputListener;
    private File transformersDir;
    
	/**
	 * Get the TransformerHandler for the transformer with the given name.
	 * @param transformerName the name of the transformer
	 * @return a TransfomerHandler, or null if a handler cannot be created.
	 * @throws TransformerDisabledException
	 */
    public TransformerHandler getTransformerHandler(String name)
	    throws TransformerDisabledException {
	TransformerHandler current = handlersMap.get(name);

	if (current != null)
	    return current;

	TransformerHandler candidate = createHandler(name);
	current = handlersMap.putIfAbsent(name, candidate);
	return (current == null) ? candidate : current;
    }

    private TransformerHandler createHandler(String transformerName)
	    throws TransformerDisabledException {
	// mg20070520: if subdir (such as se_tpb_dtbSplitterMerger.split)
	transformerName = transformerName.replace('.', '/');

	// Try to load TDF from directory
	File[] files = new File(transformersDir, transformerName)
		.listFiles(new FileFilter() {
		    public boolean accept(File file) {
			return file.getName().endsWith(".tdf");
		    }
		});

	if (files != null && files.length > 0) {
	    return new TransformerHandler(new DirTransformerLoader(files[0],
		    transformersDir, inputListener));
	} else {
	    // Try to load from JAR if no TDF was found,
	    File jarFile = new File(transformersDir, transformerName + ".jar");
	    if (jarFile.exists()) {
		return new TransformerHandler(new JarTransformerLoader(jarFile,
			transformerName, inputListener));
	    }
	}
	return null;
    }

    public void setInputListener(InputListener inputListener) {
	this.inputListener = inputListener;
    }

    public void setTransformersDirectory(File transformersDir) {
	this.transformersDir = transformersDir;
    }

}
