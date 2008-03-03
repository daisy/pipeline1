package int_daisy_dtbMigrator;

import java.util.Map;

import javax.xml.stream.Location;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent.Cause;
import org.daisy.pipeline.core.event.MessageEvent.Type;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.EFile;
import org.daisy.util.file.EFolder;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetErrorHandler;

/**
 * Main Transformer class.
 * @author Markus Gylling
 */
public class DtbMigrator extends Transformer implements FilesetErrorHandler, TransformerDelegateListener {

	public DtbMigrator(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}
	
	@Override
	protected boolean execute(Map parameters) throws TransformerRunException {
		
		try{

			/*
			 * Get handles to input and output.
			 */
			EFile inputFile = new EFile(FilenameOrFileURI.toFile((String)parameters.remove("input")));
			EFolder destination = new EFolder(FileUtils.createDirectory(FilenameOrFileURI.toFile((String)parameters.remove("destination"))));
			Fileset inputFileset = new FilesetImpl(inputFile.toURI(),this,false,false);
			
			/*
			 * Create DtbDescriptors for input and output. 
			 */			
			DtbDescriptor inputDescriptor = getInputDescriptor(inputFileset);
			DtbDescriptor outputDescriptor = getOutputDescriptor(parameters,inputDescriptor);
			
			/*
			 * Get a handle to a migrator impl that supports input and output
			 */			
			MigratorFactory factory = MigratorFactory.newInstance(this);	
			Migrator migrator = factory.newMigrator(inputDescriptor, inputFileset, outputDescriptor, parameters);
			
			/*
			 * Execute.
			 */	
			migrator.setListener(this);
			migrator.migrate(inputDescriptor, outputDescriptor, parameters, inputFileset, destination);
			
			
		} catch (Exception e) {
			if(e instanceof TransformerRunException) throw (TransformerRunException)e;
			throw new TransformerRunException(e.getLocalizedMessage(),e);
		}
		return true;
	}

	private DtbDescriptor getInputDescriptor(Fileset inputFileset) {
		DtbVersion inputVersion = DtbDescriptor.getVersion(inputFileset);
		DtbType inputType = DtbDescriptor.getType(inputFileset);			
		DtbDescriptor inputDescriptor = new DtbDescriptor(inputVersion, inputType);
		return inputDescriptor;
	}
	
	private DtbDescriptor getOutputDescriptor(Map parameters, DtbDescriptor inputDescriptor) {
		String param = (String)parameters.remove("outputVersion");
		DtbVersion version = null;
		if(param.equals("Z2005")) {
			version = DtbVersion.Z2005;
		}else if(param.equals("D202")) {
			version = DtbVersion.D202;
		}
		
		DtbType type = inputDescriptor.getType();
		
		return new DtbDescriptor(version,type);
	}

	public void error(FilesetFileException ffe) throws FilesetFileException {
		this.sendMessage(ffe);		
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.pipeline.core.transformer.TransformerDelegateListener#delegateCheckAbort()
	 */
	public boolean delegateCheckAbort() {		
		return this.isAborted();
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.pipeline.core.transformer.TransformerDelegateListener#delegateLocalize(java.lang.String, java.lang.Object)
	 */
	public String delegateLocalize(String key, Object[] params) {		
		if(key==null) return i18n(key);
		return i18n(key,params);
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.pipeline.core.transformer.TransformerDelegateListener#delegateMessage(java.lang.Object, java.lang.String, org.daisy.pipeline.core.event.MessageEvent.Type, org.daisy.pipeline.core.event.MessageEvent.Cause, javax.xml.stream.Location)
	 */
	public void delegateMessage(Object delegate, String message, Type type, Cause cause, Location location) {
		this.sendMessage(message, type, cause,location);		
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.pipeline.core.transformer.TransformerDelegateListener#delegateProgress(java.lang.Object, double)
	 */
	public void delegateProgress(Object delegate, double progress) {
		sendMessage(progress);		
	}

	

}
