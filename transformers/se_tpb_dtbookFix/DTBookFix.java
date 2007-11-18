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
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

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
import org.daisy.util.xml.Namespaces;
import org.daisy.util.xml.catalog.CatalogURIResolver;
import org.daisy.util.xml.peek.PeekResult;
import org.daisy.util.xml.peek.Peeker;
import org.daisy.util.xml.peek.PeekerPool;


/**
 * Main Transformer class. See ../doc/transformers/se_tpb_dtbookFix for further details.
 * /**
 * 
 * Fixes dtbook conversion errors/problems
 * 
 * Features:
 * 	- Adds levels where missing
 *  - Adds list items around lists in lists
 *  - Simplifies structure if possible 
 *  
 * Steps: 
 *  1. Removes levelx if it has descendant headings of x-1
 *  2. Splits a level into several levels on every additional heading on the same level
 *  3. Adds headings where needed
 *  4. Changes hx to p where parent isn't a levely
 *  
 *  5. Add li around lists that has a list as parent
 *   
 *  6. Moves structure upwards where possible
 *  
 *  (7. Tidy description todo)
 *  
 *  Limitations:
 *   - Cannot handle reversed structure: e.g. level3/level2/level1
 *   - Does not structure frontmatter
 *   - Validation not implemented yet!
 *   
 *  Note:
 *   - The current version of post-pagenum-fix.xsl contains one repairing operation
 *     that will never be used since the program will stop if the file is invalid at
 *     that point. If needed, it should be moved to the repairing section of dtbook-fix.
 *     
 *  Further development:
 *   - Tidy:
 *   	<levelx><h1>Heading</h1><p/></levelx><levelx><p>text</p>...
 *      to
 *      <levelx><h1>Heading</h1><p>text</p>...
 *   - Implement input and intermediate validation
 *   - Option to skip repairing and fail if input is invalid
 *   - Add documentation
 *   
 * @author Joel Håkansson, Markus Gylling 
 */
public class DTBookFix extends Transformer implements URIResolver, TransformerDelegateListener, ValidatorListener, FilesetErrorHandler {

	private boolean mHadValidationErrors = false;
	private CatalogURIResolver mCatalogURIResolver = null;
	private final static String DTBOOK_VALIDATOR_IMPL = "org.daisy.util.fileset.validation:http://www.daisy.org/fileset/DTBOOK_DOCUMENT";
	private final static String JAVAX_FACTORY_KEY = "javax.xml.transform.TransformerFactory";
	
	public DTBookFix(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	@Override
	protected boolean execute(Map parameters) throws TransformerRunException {
		progress(0);		
		String factory = (String)parameters.remove("factory");						
		File input = FilenameOrFileURI.toFile((String)parameters.get("input"));
		File output = FilenameOrFileURI.toFile((String)parameters.get("output"));
		boolean forceRepair = ((String)parameters.get("forceRepair")).contentEquals("true");
		boolean abortOnError = ((String)parameters.get("abortOnError")).contentEquals("true");
		String tidy = (String)parameters.get("tidy");
		
		FileJuggler files = null;
		
		/*
		 * Set a sysprop for the XSLT factory. This needs to be a 1.0 and 2.0 processor,
		 * so we are expecting the tdf to namedrop the Saxon8 identifier.
		 * Save any preexisting prop, and reset in finally
		 */
		String initXsltFactoryProp = System.getProperty(JAVAX_FACTORY_KEY);		
		System.setProperty(JAVAX_FACTORY_KEY, factory);
						
		/*
		 * Set a sysprop to help the dtbook validator factory discovery
		 */
		String initDtbookValidatorFactoryProp = System.getProperty(DTBOOK_VALIDATOR_IMPL);
		if(initDtbookValidatorFactoryProp==null){
			System.setProperty(
					DTBOOK_VALIDATOR_IMPL,
			"org.daisy.util.fileset.validation.ValidatorImplDtbook");
		}
		
		try {
						
			files = new FileJuggler(input, output);
			mCatalogURIResolver = new CatalogURIResolver();
			
			/*
			 * Confirm that input prerequisites are met (see javadoc of #confirmPrerequisites).
	    	 * At the same time, get a PeekResult to retrieve version information et al.
	    	 */
			PeekResult result = confirmPrerequisites(input);
			
			//use @version to determine version since doctype contents may or may not be present
	    	String inputDTBookVersion = result.getRootElementAttributes().getValue("version");	    	

	    	//add version to parameters so that XSLTs may enjoy it if needed
	    	parameters.put("DTBookVersion", inputDTBookVersion);
	    		    		    	    		    	
	    	/*
	    	 * Create the executor categories
	    	 */
	    	List<Executor> repairCategory = 
	    		createCategory(ExecutorCategory.REPAIR, parameters, inputDTBookVersion);	    	
	    	
	    	List<Executor> tidyCategory = 
	    		createCategory(ExecutorCategory.TIDY, parameters, inputDTBookVersion);	    
	    	
	    	int progressLen = repairCategory.size() + tidyCategory.size();	    	
	    	double progress = 0;
	    	
	    	/*
	    	 * Validate input
	    	 */
	    	this.sendMessage(i18n("VALIDATING_INPUT", input.getName()));
	    	boolean inputValid = isValid(input);
	    	if(inputValid) this.sendMessage(i18n("WAS_VALID"));
	    	
	    	/*
	    	 * Run the executors. First repair if active. 
	    	 */
	    	if(forceRepair || !inputValid) {	    		
	    		this.sendMessage(i18n("REPAIRING"),MessageEvent.Type.INFO);
		    	for(Executor exec : repairCategory) {
		    		this.sendMessage(i18n("RUNNING_EXECUTOR", exec.getNiceName()),MessageEvent.Type.INFO);
		    		exec.execute(new StreamSource(files.getInput()), new StreamResult(files.getOutput()));
		    		files.swap();
		    		progress++;
		    		progress(progress/progressLen);
		    	}
	    	} else {
	    		progress = repairCategory.size();
	    		progress(progress/progressLen);
	    	}
	    			    		    		    	
	    	this.sendMessage(i18n("TIDYING"),MessageEvent.Type.INFO);
	    	for(Executor exec : tidyCategory) {
	    		this.sendMessage(i18n("RUNNING_EXECUTOR", exec.getNiceName()),MessageEvent.Type.INFO);
	    		exec.execute(new StreamSource(files.getInput()), new StreamResult(files.getOutput()));
	    		files.swap();
	    		progress++;
	    		progress(progress/progressLen);
	    	}
	    	
	    	files.close();
	    	
	    	/*
	    	 * Validate the output
	    	 */
	    	this.sendMessage(i18n("VALIDATING_OUTPUT", output));
	    	if(!isValid(output)) {
	    		if(abortOnError) {	    	
	    			throw new TransformerRunException(i18n("ABORTING_INVALID"));
	    		}	
	    	}else{
	    		this.sendMessage(i18n("WAS_VALID"));
	    	}
	    	
	    	progress(1);
	    	
		} catch (Exception e) {
			throw new TransformerRunException(e.getMessage(),e);
		}finally{			
			 //Reset the validator factory to initial value			
			if(initDtbookValidatorFactoryProp!=null) {
				System.setProperty(DTBOOK_VALIDATOR_IMPL,initDtbookValidatorFactoryProp);
			}else{
				System.clearProperty(DTBOOK_VALIDATOR_IMPL);
			}
			//Reset the xslt factory to initial value
			if(initXsltFactoryProp!=null) {
				System.setProperty(JAVAX_FACTORY_KEY,initXsltFactoryProp);
			}else{
				System.clearProperty(JAVAX_FACTORY_KEY);
			}
		}
		return true;
	}
	
	/**
	 * This method implements parts of the contract with subclasses of Executor:
	 * <p>Before calling the first executor, the transformer will assure that the following properties hold true for the input document:</p>
	 * <ul>	
	 * 	<li>DTBook namespace present</li>
	 * 	<li>DOCTYPE-@version match</li>
	 * </ul>
	 * @throws TransformerRunException if prerequisites are not met
	 * @return a PeekResult on the inparam file 
	 */
	private PeekResult confirmPrerequisites(File input) throws TransformerRunException {
    	Peeker peeker = PeekerPool.getInstance().acquire();
    	PeekResult result;
		try {
			result = peeker.peek(input);			
			//check that we are namespaced as expected
			if(!result.getRootElementNsUri().equals(Namespaces.Z2005_DTBOOK_NS_URI)) {
				throw new TransformerRunException(i18n("INPUT_ERROR_NAMESPACE", Namespaces.Z2005_DTBOOK_NS_URI));
			}
			
			//check doctype/@version match
			String publicId = result.getPrologPublicId();
			String systemId = result.getPrologSystemId();
			String version = result.getRootElementAttributes().getValue("version");
			if(publicId!=null && version!=null) {
				if(!publicId.contains(version)) {
					throw new TransformerRunException(i18n("INPUT_ERROR_VERSION_AMBIVALENCE", publicId, version));
				}
			} else if(systemId!=null && version!=null) {
				if(!systemId.contains(version)) {
					throw new TransformerRunException(i18n("INPUT_ERROR_VERSION_AMBIVALENCE", systemId, version));
				} 	
			} else if (version!=null){
				// the spec does not really require that public or system IDs are present
				String message = i18n("INPUT_WARNING_NO_DOCTYPE");
				this.sendMessage(message, MessageEvent.Type.WARNING, MessageEvent.Cause.INPUT);				
			}else{
				//no doctype, no @version
				throw new TransformerRunException(i18n("INPUT_ERROR_NO_DOCTYPE_NO_VERSION"));
			}
							
		} catch (Exception e) {
			throw new TransformerRunException(e.getMessage(),e);
		} finally {      	
			PeekerPool.getInstance().release(peeker);
		}		
		
    	return result;
	}

	@SuppressWarnings("unchecked")
	private List<Executor> createCategory(ExecutorCategory execCategory, Map parameters, String inputDTBookVersion) {
		final String[] v2005_1 = {"2005-1"};		
		final String[] v2005_2 = {"2005-2"};
		final String[] v2005_1_2 = {"2005-1","2005-2"};
						
		List<Executor> category = new LinkedList<Executor>();
		
		/*
		 * Instantiate a message emitter to listen to messages from Saxon
		 */
    	MessageEmitter emitter = new MessageEmitter();
    	emitter.setWriter(new MessageEmitterWriter(this));
    	
    	/*
    	 * Add each executor
    	 */
    	if(execCategory==ExecutorCategory.TIDY) {
    		//tidy-level-cleaner.xsl should be optional
    		if(((String)parameters.get("simplifyHeadingLayout")).contentEquals("true")) {
    			category.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/tidy-level-cleaner.xsl"),v2005_1_2,i18n("LEVEL_CLEANER"),this,this,emitter));
    		}
    		
    		category.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/tidy-pagenum-fix.xsl"),v2005_1_2,i18n("PAGENUM_FIX"),this,this,emitter));
    		category.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/tidy-add-author-title.xsl"),v2005_1_2,i18n("ADD_AUTHOR_AND_TITLE"),this,this,emitter));
    		
    		//run the indenter last in the chain, this is harmless so always active
    		category.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/tidy-indent.xsl"),v2005_1_2,i18n("INDENT"),this,this,emitter));
    		
    	}else if (execCategory==ExecutorCategory.REPAIR) {
    		//optional charset recoder
    		//this should always be run first in the repair category
    		if(((String)parameters.get("fixCharset")).contentEquals("true")) {
    			category.add(new CharsetExecutor(parameters,i18n("CHARSET_FIXER"),this));
    		}
    		category.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-levelnormalizer.xsl"),v2005_1_2,i18n("LEVEL_NORMALIZER"),this,this,emitter));
    		category.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-levelsplitter.xsl"),v2005_1_2,i18n("LEVEL_SPLITTER"),this,this,emitter));
    		category.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-level1.xsl"),v2005_1_2,i18n("REPAIR_LEVEL_1"),this,this,emitter));
    		category.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-level2.xsl"),v2005_1_2,i18n("REPAIR_LEVEL_2"),this,this,emitter));
    		category.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-level3.xsl"),v2005_1_2,i18n("REPAIR_LEVEL_3"),this,this,emitter));
    		category.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-level4.xsl"),v2005_1_2,i18n("REPAIR_LEVEL_4"),this,this,emitter));
    		category.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-level5.xsl"),v2005_1_2,i18n("REPAIR_LEVEL_5"),this,this,emitter));
    		category.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-level6.xsl"),v2005_1_2,i18n("REPAIR_LEVEL_6"),this,this,emitter));
    		category.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-remove-illegal-headings.xsl"),v2005_1_2,i18n("REMOVE_ILLEGAL_HEADINGS"),this,this,emitter));
    		category.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-lists.xsl"),v2005_1_2,i18n("REPAIR_LISTS"),this,this,emitter));
    	}
    	
    	for (int i = 0; i < category.size(); i++) {
    		Executor dbfe = category.get(i);
       		if(!dbfe.supportsVersion(inputDTBookVersion)) {
    			category.remove(i);
        		String message = i18n("REMOVING", dbfe.getNiceName(), inputDTBookVersion);
        		this.sendMessage(message,Type.WARNING,Cause.INPUT);
    		}
		}
    	
    	if(category.isEmpty()) {
    		String message = i18n("EMPTY_CATEGORY", category.toString());
    		this.sendMessage(message,Type.WARNING,Cause.INPUT);
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
			
			if(extraSchemas!=null) {
				String type = "org.daisy.util.fileset.impl.Z3986DtbookFileImpl";
				for(URL url : extraSchemas) {
					validator.setSchema(url,type);
				}
			}			
			validator.validate(dtbookFileset);
		}catch (ValidatorNotSupportedException  e) {
			throw new TransformerRunException(e.getMessage(),e);
		} catch (ValidatorException e) {
			throw new TransformerRunException(e.getMessage(),e);			
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
		System.err.println("dtbookfix resolve, href=" + href + " , base=" + base);
		return mCatalogURIResolver.resolve(href, base);
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
		if(param==null) {
			return i18n(message);
		}
		return i18n(message,param);
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.pipeline.core.transformer.TransformerDelegateListener#delegateMessage(java.lang.Object, java.lang.String, org.daisy.pipeline.core.event.MessageEvent.Type, org.daisy.pipeline.core.event.MessageEvent.Cause, javax.xml.stream.Location)
	 */
	public void delegateMessage(Object delegate, String message, Type type,Cause cause, Location location) {
		if(location!=null) {
			this.sendMessage(message,type,cause,location);
		}	
		this.sendMessage(message,type,cause);
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.pipeline.core.transformer.TransformerDelegateListener#delegateProgress(java.lang.Object, double)
	 */
	public void delegateProgress(Object delegate,double progress) {
		//ignore				
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
		//ignore
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
