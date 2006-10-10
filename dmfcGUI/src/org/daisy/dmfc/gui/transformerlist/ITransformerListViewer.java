/**
 *DAISY Multi-Format Converter, or DAISY Pipeline Graphical User Interface.
Copyright (C) 2006 DAISY Consortium

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.daisy.dmfc.gui.transformerlist;


import org.daisy.dmfc.core.transformer.TransformerHandler;

/**
 * Functionality for list of transformers.  Jface
 * @author Laurie
 *
 */


public interface ITransformerListViewer {

	/**
	 * Update the view to show that a transformer was added 
	 * to the list.  Not used in this app.
	 * 
	 * @param TransformerHandler
	 */
	public void addTransformer(TransformerHandler th);
	
	/**
	 * Update the view to show that a transformer was removed 
	 * from the list.  Not done in this application.
	 * 
	 * @param transformerHandler
	 */
	public void removeTransformer(TransformerHandler th);
	
	/**
	 * Update the view to reflect the fact that a transformer has finished running.
	 * 
	 * @param task
	 */
	public void updateTransformer(TransformerHandler th);
}
