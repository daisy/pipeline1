/*
 * Daisy Pipeline
 * Copyright (C) 2007  Daisy Consortium
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

import org.daisy.dmfc.exception.TransformerDisabledException;

/**
 * Interface for the TransformerHandler loader. 
 * @author Linus Ericson
 */
public interface TransformerHandlerLoader {
	/**
	 * Get the TransformerHandler for the transformer with the given name.
	 * @param transformerName the name of the transformer
	 * @return a TransfomerHandler, or null if a handler cannot be created.
	 * @throws TransformerDisabledException
	 */
	public TransformerHandler getTransformerHandler(String transformerName) throws TransformerDisabledException;
}
