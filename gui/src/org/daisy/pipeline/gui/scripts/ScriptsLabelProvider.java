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
package org.daisy.pipeline.gui.scripts;

import java.io.File;

import org.daisy.pipeline.core.script.Script;
import org.daisy.pipeline.gui.model.ScriptManager;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;

public class ScriptsLabelProvider extends LabelProvider implements
		ILabelProvider {

	private ScriptManager scriptMan;

	public ScriptsLabelProvider() {
		scriptMan = ScriptManager.getDefault();
	}

	/**
	 * Returns the text for the label of the given script file.
	 * 
	 * There is a hierarchy in the file structure that organises the.
	 * transformers by type:
	 * <ul>
	 * <li>Directories does not have associated script handler objects, they
	 * must be labelled by their name.</li>
	 * <li>Script file does have an associated script handler, they are
	 * labelled with teh script name retrieved from the handler. </li>
	 * </ul>
	 * 
	 * @param file
	 *            A File in the script file tree.
	 */
	@Override
	public String getText(Object obj) {
		String text;
		File file = (File) obj;
		if (file.isDirectory()) {
			text = Messages.getName(file);
		} else {
			Script script = scriptMan.getScript((file).toURI());
			text = (script != null) ? script.getNicename() : file.getName();
		}
		return text;
	}
}
