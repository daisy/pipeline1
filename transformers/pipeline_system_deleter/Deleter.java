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
package pipeline_system_deleter;

import java.util.Map;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.EFile;
import org.daisy.util.file.Directory;

/**
 * Transformer that deletes resources on the filesystem.
 * @author Markus Gylling
 */
public class Deleter extends Transformer {
	private String toDelete = null;
	
	public Deleter(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	@SuppressWarnings("unused")
	@Override
	protected boolean execute(Map<String,String> parameters) throws TransformerRunException {		
		try{
			// le20070531: If we don't call System.gc() there might be some old trailing 
			// file locks and the delete will fail.
			System.gc();
			
			String active = parameters.remove("active");
			if(active.equals("true")){
				String toDelete = parameters.remove("delete");
				EFile resource = new EFile(toDelete);				
				if(!resource.exists()) return true;
				
				if(resource.isFile() && !resource.isSymLink()) {
					boolean result = resource.delete();
					if(!result) {
						this.sendMessage(i18n("DELETER_FAILURE", resource.getAbsolutePath()), MessageEvent.Type.WARNING, MessageEvent.Cause.SYSTEM);
					}
				}else if (resource.isDirectory() && !resource.isSymLink()) {
					Directory folder = new Directory(resource);
					boolean result = folder.deleteContents(true);
					if(result) {
						result = folder.delete();
					}	
					if(!result) {
						this.sendMessage(i18n("DELETER_FAILURE", folder.getAbsolutePath()), MessageEvent.Type.WARNING, MessageEvent.Cause.SYSTEM);
					}	
				}
			}
		}catch (Exception e) {
			this.sendMessage(i18n("DELETER_MAIN_EXCEPTION", toDelete, e.getMessage()), MessageEvent.Type.WARNING, MessageEvent.Cause.SYSTEM);			
		}		
		return true;
	}

}
