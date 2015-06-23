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
package int_daisy_recorder2dtb.read;

import int_daisy_recorder2dtb.InputType;

import java.net.URL;

import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.dtb.ncxonly.model.Model;

/**
 * Abstract base for concrete readers of playlists or marker files.
 * @author Markus Gylling
 */
public abstract class Reader {

	protected URL mInputURL = null;
	protected TransformerDelegateListener mTransformer = null;
		
	/**
	 * Constructor used during factory discovery.
	 */
	public Reader() {
		
	}
	
	/**
	 * Primary constructor.
	 * @param file
	 * @param tdl
	 */
	public Reader(URL file, TransformerDelegateListener tdl) {
		mInputURL = file;
		mTransformer = tdl;
	}
	
	/**
	 * Parse the input resource that was set in constructor, create and return a Model instance. 
	 */
	public abstract Model createModel() throws TransformerRunException;
		
	/**
	 * Factory qeury: is inparam resource of a type that this Reader supports?
	 */
	public abstract boolean supports(URL u);
	
	/**
	 * Get the type of playlist/marker file that this Reader supports.
	 */
	public abstract InputType getSupportedInputType();
	
	
}
