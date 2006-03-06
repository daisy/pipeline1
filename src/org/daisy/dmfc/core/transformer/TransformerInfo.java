/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2005  Daisy Consortium
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
package org.daisy.dmfc.core.transformer;

import java.io.File;
import java.net.URL;
import java.util.Collection;

/**
 * Interface containing Transformer information functions.
 * @author Linus Ericson
 */
public interface TransformerInfo {
    
	/**
	 * Gets the name of the Transformer.
	 * @return the name of the Transformer
	 */
	public String getName();

	/**
	 * Gets the description of the Transformer.
	 * @return a description of the Transformer
	 */
	public String getDescription();
	
	/**
	 * Gets the colleaction of paramters that the Transfomer accepts as input
	 * @return a collection of <code>Parameter</code>s.
	 */
	public Collection getParameters();	
	
	/**
	 * Gets the directory of the transformer.
	 * @return the transformer directory
	 */
	public File getTransformerDir();
	
	/**
	 * Gets the documentation of the transformer, or <code>null</code> if no
	 * documentation exists.
	 * @return a url to the documentation.
	 */
	public URL getDocumentation();
}
