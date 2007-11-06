package se_tpb_dtbookFix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.Location;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;

import net.sf.saxon.event.MessageEmitter;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.event.MessageEvent.Cause;
import org.daisy.pipeline.core.event.MessageEvent.Type;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.exception.FilesetFileFatalErrorException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetErrorHandler;
import org.daisy.util.fileset.validation.Validator;
import org.daisy.util.fileset.validation.ValidatorFactory;
import org.daisy.util.fileset.validation.ValidatorListener;
import org.daisy.util.fileset.validation.exception.ValidatorException;
import org.daisy.util.fileset.validation.exception.ValidatorNotSupportedException;
import org.daisy.util.fileset.validation.message.ValidatorMessage;
import org.daisy.util.fileset.validation.message.ValidatorWarningMessage;
import org.daisy.util.xml.LocusTransformer;
import org.daisy.util.xml.peek.PeekResult;
import org.daisy.util.xml.peek.Peeker;
import org.daisy.util.xml.peek.PeekerPool;

/**
 * Main Transformer class. See ../doc/transformers/se_tpb_dtbookFix for details.
 * @author Joel Håkansson, Markus Gylling 
 */
public class DTBookFix2 extends Transformer implements URIResolver, TransformerDelegateListener, ValidatorListener, FilesetErrorHandler {

	private boolean mHadValidationErrors = false;
	
	public DTBookFix2(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	@Override
	protected boolean execute(Map parameters) throws TransformerRunException {
		progress(0);
		String factory = (String)parameters.remove("factory");
		File input = FilenameOrFileURI.toFile((String)parameters.get("input"));
		File output = FilenameOrFileURI.toFile((String)parameters.get("output"));
		boolean forceRepair = ((String)parameters.get("forceRepair")).contentEquals("true");
		String tidy = (String)parameters.get("tidy");
		String indent = (String)parameters.get("indent");
		
		try{
			
	    	/*
	    	 * Peek on input and get version
	    	 */			
	    	Peeker peeker = PeekerPool.getInstance().acquire();
	    	PeekResult result = peeker.peek(input);
	    	String inputDTBookVersion = result.getRootElementAttributes().getValue("version");
	    	PeekerPool.getInstance().release(peeker);
	    		    		    		    	
	    	/*
	    	 * Create wanted categories and call DTBookFixExecutor.execute
	    	 */
	    	if(forceRepair || !isValid(input)) {
		    	List<DTBookFixExecutor> repairCategory = 
		    		createCategory(ExecutorCategory.REPAIR, parameters, inputDTBookVersion);	    		
		    	for(DTBookFixExecutor exec : repairCategory) {
		    		//exec.execute(source, result);
		    	}
	    	}

	    	List<DTBookFixExecutor> tidyCategory = 
	    		createCategory(ExecutorCategory.TIDY, parameters, inputDTBookVersion);	    	
	    	for(DTBookFixExecutor exec : tidyCategory) {
	    		//exec.execute(source, result);
	    	}
	    		
		}catch (Exception e) {
			throw new TransformerRunException(e.getMessage(),e);
		}
    	
		return true;
	}
	
	private List<DTBookFixExecutor> createCategory(ExecutorCategory execCategory, Map parameters, String inputDTBookVersion) {
		final String[] v2005_1 = {"2005-1"};		
		final String[] v2005_2 = {"2005-2"};
		final String[] v2005_1_2 = {"2005-1","2005-2"};
						
		List<DTBookFixExecutor> category = new LinkedList<DTBookFixExecutor>();
		
		/*
		 * Instantiate a message emitter to listen to messages from Saxon
		 */
    	MessageEmitter emitter = new MessageEmitter();
    	emitter.setWriter(new MessageEmitterWriter(this));
    	
    	/*
    	 * Add each executor
    	 */
    	if(execCategory==ExecutorCategory.TIDY) {    		    		 		
    		category.add(new DTBookFixExecutorXSLT(parameters,this.getClass().getResource("./xslt/tidy-level-cleaner.xsl"),v2005_1_2,"Level cleaner",this,this,emitter));
    		//etc
    	}else if (execCategory==ExecutorCategory.REPAIR) {
    		//etc
    	}
    	
    	for (int i = 0; i < category.size(); i++) {
    		DTBookFixExecutor dbfe = category.get(i);
       		if(!dbfe.supportsVersion(inputDTBookVersion)) {
    			category.remove(i);
    		}

		}
    	    	
		return category;
	}

	private boolean isValid(File f) throws TransformerRunException {
		return isValid(f,null);
	}
	
	private boolean isValid(File f, Set<URL> extraSchemas) throws TransformerRunException {
		ValidatorFactory vfac = ValidatorFactory.newInstance();		
		mHadValidationErrors = false;
		try{
			Fileset dtbookFileset = new FilesetImpl(f.toURI(),this,true,false);			
			Validator validator = vfac.newValidator(FilesetType.DTBOOK_DOCUMENT);
			validator.setListener(this);			
			/*
			 * If adding extra schemas beyond the canonical ones, do:
			 * URL url = CatalogEntityResolver.getInstance().resolveEntityToURL(catalogID);
			 * String type = "org.daisy.util.fileset.impl.Z3986DtbookFileImpl";
			 * validator.setSchema(url,type);
			 */
			validator.validate(dtbookFileset);
		}catch (ValidatorNotSupportedException  e) {
			// TODO: warn inform
		} catch (ValidatorException e) {
			// TODO: warn inform
			
		} catch (FilesetFatalException e) {
			throw new TransformerRunException(e.getMessage(),e);			
		}	
		return !mHadValidationErrors;
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.xml.transform.URIResolver#resolve(java.lang.String, java.lang.String)
	 */
	public Source resolve(String href, String base) throws TransformerException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Get and forward XSLT messages.
	 * @author Markus Gylling
	 */
	class MessageEmitterWriter extends Writer {
		Transformer mT = null;
		MessageEmitterWriter(Transformer t) {
			mT = t;
		}
		
		@Override
		public void close() throws IOException {
			
		}

		@Override
		public void flush() throws IOException {
			
		}

		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {
			String s = new String(cbuf, off, len).trim();
			mT.sendMessage("XSLT message: " + s,MessageEvent.Type.INFO,MessageEvent.Cause.SYSTEM,null);			
		}
		 
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.pipeline.core.transformer.TransformerDelegateListener#delegateLocalize(java.lang.String, java.lang.Object)
	 */
	public String delegateLocalize(String message, Object param) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.pipeline.core.transformer.TransformerDelegateListener#delegateMessage(java.lang.Object, java.lang.String, org.daisy.pipeline.core.event.MessageEvent.Type, org.daisy.pipeline.core.event.MessageEvent.Cause, javax.xml.stream.Location)
	 */
	public void delegateMessage(Object delegate, String message, Type type,Cause cause, Location location) {
		// TODO Auto-generated method stub		
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.pipeline.core.transformer.TransformerDelegateListener#delegateProgress(java.lang.Object, double)
	 */
	public void delegateProgress(Object delegate,double progress) {
		// TODO Auto-generated method stub		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorListener#exception(org.daisy.util.fileset.validation.Validator, java.lang.Exception)
	 */
	public void exception(Validator validator, Exception e) {		
		mHadValidationErrors = true;
		Location loc = LocusTransformer.newLocation(e);
		this.sendMessage(e.getMessage(), MessageEvent.Type.ERROR, MessageEvent.Cause.INPUT,loc);						
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorListener#report(org.daisy.util.fileset.validation.Validator, org.daisy.util.fileset.validation.message.ValidatorMessage)
	 */
	public void report(Validator validator, ValidatorMessage message) {		
		MessageEvent.Type type = null;
		if(message instanceof ValidatorWarningMessage) {			
			type = MessageEvent.Type.WARNING;		
		}else {
			mHadValidationErrors = true;
			type = MessageEvent.Type.ERROR;
		}
		Location loc = LocusTransformer.newLocation(message);
		this.sendMessage(message.getMessage(), type, MessageEvent.Cause.INPUT,loc);		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorListener#inform(org.daisy.util.fileset.validation.Validator, java.lang.String)
	 */
	public void inform(Validator validator, String information) {
		this.sendMessage(information, MessageEvent.Type.INFO, MessageEvent.Cause.SYSTEM,null);		
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorListener#progress(org.daisy.util.fileset.validation.Validator, double)
	 */
	public void progress(Validator validator, double progress) {
		// TODO Auto-generated method stub		
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.interfaces.FilesetErrorHandler#error(org.daisy.util.fileset.exception.FilesetFileException)
	 */
	public void error(FilesetFileException ffe) throws FilesetFileException {		
		Throwable root =ffe.getRootCause();
		if(root==null) root = ffe.getCause();		
		/*
		 * Because we are using tempdirs, have to filter out all
		 * exceptions about missing referenced files.
		 */
		if (ffe instanceof FilesetFileFatalErrorException && !(ffe.getCause() instanceof FileNotFoundException)) {
			mHadValidationErrors = true;
			this.sendMessage(root.getMessage(), MessageEvent.Type.ERROR, MessageEvent.Cause.INPUT, null);
		} 		
	}
}
