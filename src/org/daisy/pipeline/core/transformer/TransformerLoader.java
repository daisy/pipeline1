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
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import org.daisy.pipeline.core.script.Task;
import org.daisy.pipeline.exception.TransformerDisabledException;

/**
 * @author Romain Deltour
 * 
 */
public interface TransformerLoader {

    /**
     * Creates an instance object of the Transformer class.
     * 
     * @param interactive
     * @return a <code>Transformer</code> object
     * @throws IllegalArgumentException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public Transformer createTransformer(boolean interactive, Task task,
	    TransformerInfo info) throws IllegalArgumentException,
	    InstantiationException, IllegalAccessException,
	    InvocationTargetException;

    /**
     * @return
     * @throws MalformedURLException 
     */
    public URL getTdfUrl() throws MalformedURLException;

    /**
     * @return
     */
    public File getTransformerDir();

    /**
     * 
     * @param classname
     * @param jars
     * @param nicename
     * @throws TransformerDisabledException
     */
    public void init(String classname, Collection<String> jars, String nicename)
	    throws TransformerDisabledException;
}