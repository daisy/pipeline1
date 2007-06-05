package pipeline_system_deleter;

import java.io.File;
import java.util.Map;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.EFile;
import org.daisy.util.file.EFolder;

/**
 * Transformer that deletes resources on the filesystem.
 * @author Markus Gylling
 */
public class Deleter extends Transformer {
	private String toDelete = null;
	
	public Deleter(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	@Override
	protected boolean execute(Map parameters) throws TransformerRunException {		
		try{
			// le20070531: If we don't call System.gc() there might be some old trailing 
			// file locks and the delete will fail.
			System.gc();
			
			String active = (String)parameters.remove("active");
			if(active.equals("true")){
				String toDelete = (String)parameters.remove("delete");
				EFile resource = new EFile(toDelete);				
				if(!resource.exists()) return true;
				
				if(resource.isFile() && !resource.isSymLink()) {
					boolean result = resource.delete();
					if(!result) {
						this.sendMessage(i18n("DELETER_FAILURE", resource.getAbsolutePath()), MessageEvent.Type.WARNING, MessageEvent.Cause.SYSTEM);
					}
				}else if (resource.isDirectory() && !resource.isSymLink()) {
					EFolder folder = new EFolder(resource);
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
