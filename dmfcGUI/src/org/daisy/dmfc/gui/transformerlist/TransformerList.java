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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.daisy.dmfc.core.transformer.TransformerHandler;
import org.daisy.dmfc.qmanager.Job;

/**
 * Class that plays the role of the domain model 
 * This can be used with a persistent data store, i.e. database
 * to place items into a Vector, Array, whatever.
 * In this case, the List has already been created in the 
 * framework.
 * @author Laurie Sherve
 * 
 */

public class TransformerList {
	
	private Job job;
	
	//List of all transformers handlers for each script/job
	private List listTransformerInfo;
	private Set changeListeners = new HashSet();

	
	/**
	 * Constructor
	 */
	public TransformerList(Job job) {
		super();
		this.job = job;
		this.initData();
	}
	
	/*
	 * Initialize the table data.
	 */
	private void initData() {
		
		//each job, get the scriptHandler and list of transformers (transformerhandlers) for each
		listTransformerInfo = this.job.getScript().getTransformerInfoList();
	}

	
	/**
	 * Return the collection of tasks
	 */
	public List getTransformers() {
		return listTransformerInfo;
	}
	

	
	
	/**
	 * If want to add to the list...
	 */
	public void addTransformer() {
		//never added to this table
	}

	/**
	 *Transformer is never removed in this app...
	 * @param job
	 */
	public void removeTask(TransformerHandler th) {
		listTransformerInfo.remove(th);
		Iterator iterator = changeListeners.iterator();
		while (iterator.hasNext())
			((ITransformerListViewer) iterator.next()).removeTransformer(th);
	}

	/**
	 * Called when the transformer has completed.
	 * the table is updated
	 * @param Transformer
	 */
	public void transformerComplete(TransformerHandler th) {
		Iterator iterator = changeListeners.iterator();
		while (iterator.hasNext())
			((ITransformerListViewer) iterator.next()).updateTransformer(th);
	}

	/**
	 * @param viewer
	 */
	public void removeChangeListener(ITransformerListViewer viewer) {
		changeListeners.remove(viewer);
	}

	/**
	 * @param viewer
	 */
	public void addChangeListener(ITransformerListViewer viewer) {
		changeListeners.add(viewer);
	}

}
