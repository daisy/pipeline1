/*
 * DAISY Pipeline GUI Copyright (C) 2006 Daisy Consortium
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
package org.daisy.pipeline.gui.model;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.daisy.pipeline.core.PipelineCore;
import org.daisy.pipeline.core.event.CoreMessageEvent;
import org.daisy.pipeline.core.event.EventBus;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.script.Script;
import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.PipelineUtil;
import org.daisy.pipeline.gui.scripts.ScriptFileFilter;
import org.eclipse.osgi.util.NLS;

/**
 * Manages the set of scripts available in the Pipeline.
 * 
 * @author Romain Deltour
 * 
 */
public class ScriptManager {
	private static ScriptManager _default = new ScriptManager();

	private Map<URI, Script> scriptMap;

	/**
	 * Creates a new instance of this script manager.
	 */
	public ScriptManager() {
		scriptMap = new HashMap<URI, Script>();
	}

	/**
	 * Returns a shared instance of a script manager.
	 * 
	 * @return a shared instance of a script manager
	 */
	public static ScriptManager getDefault() {
		return _default;
	}

	/**
	 * Initializes this script manager. Creates new scripts object for each
	 * script file in the script directory.
	 * 
	 * @see PipelineCore#newScript(java.net.URL)
	 */
	public void init() {
		populateScripts(PipelineUtil.getDir(PipelineUtil.SCRIPT_DIR));
	}

	/**
	 * Clear this scripts manager from its scripts.
	 */
	public void clear() {
		scriptMap = new HashMap<URI, Script>();
	}

	/**
	 * Default implementation does nothing
	 */
	public void dispose() {
		// Nothing
	}

	/**
	 * Returns the script represented by the given URI.
	 * 
	 * @param uri
	 *            a URI to a script file
	 * @return the script object for the script file at <code>uri</code>
	 */
	public Script getScript(URI uri) {
		return scriptMap.get(uri);
	}

	/**
	 * Returns the scripts contained in this manager. The collection is backed
	 * by the manager, so changes to the manager are reflected in the
	 * collection, and vice-versa.
	 * 
	 * @return the scripts contained in this manager.
	 */
	public Collection<Script> getScripts() {
		return scriptMap.values();
	}

	/**
	 * Returns the URI to the script file for the given script.
	 * 
	 * @param script
	 *            a Pipeline script
	 * @return the URI of the script file for this script
	 */
	public URI getURI(Script script) {
		if (script != null) {
			for (URI uri : scriptMap.keySet()) {
				if (script.equals(scriptMap.get(uri))) {
					return uri;
				}
			}
		}
		return null;
	}

	private void populateScripts(File dir) {
		PipelineCore core = GuiPlugin.get().getCore();
		for (File file : dir.listFiles(new ScriptFileFilter(true))) {
			if (file.isDirectory()) {
				populateScripts(file);
			} else {
				Script script = null;
				try {
					script = core.newScript(file.toURI().toURL());
					scriptMap.put(file.toURI(), script);
				} catch (Exception e) {
					EventBus.getInstance().publish(
							new CoreMessageEvent(this, NLS.bind(
									Messages.error_unableToLoadScript, file
											.getName()),
									MessageEvent.Type.WARNING));
					GuiPlugin.get().error(e.getLocalizedMessage(), e);
				}
			}
		}
	}
}
