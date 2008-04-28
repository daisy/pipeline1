package se_tpb_dtbookFix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.HashSet;
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
import org.daisy.util.file.EFolder;
import org.daisy.util.file.FileJuggler;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetErrorHandler;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.exception.FilesetFileWarningException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.validation.Validator;
import org.daisy.util.fileset.validation.ValidatorFactory;
import org.daisy.util.fileset.validation.ValidatorListener;
import org.daisy.util.fileset.validation.exception.ValidatorException;
import org.daisy.util.fileset.validation.message.ValidatorMessage;
import org.daisy.util.xml.LocusTransformer;
import org.daisy.util.xml.Namespaces;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogURIResolver;
import org.daisy.util.xml.peek.PeekResult;
import org.daisy.util.xml.peek.Peeker;
import org.daisy.util.xml.peek.PeekerPool;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * Main Transformer class. 
 * <p>See ../doc/transformers/se_tpb_dtbookFix.html for further details, 
 * and inline documentation of individual Executors (XSLTs, classes).</p>
 * @author Joel Håkansson, Markus Gylling 
 */
public class DTBookFix extends Transformer implements EntityResolver, URIResolver, TransformerDelegateListener, ValidatorListener, FilesetErrorHandler {

	private boolean mHadValidationErrors = false;
	private CatalogURIResolver mCatalogURIResolver = null;
	
	public DTBookFix(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	/*
	 * TODO ideas (mg20071121)
	 *  optional add GUID or better inparam UID to meta
	 *  optional, docauthor doctitle/dc:title values as inparams?
	 *  
	 * (jh20071204)
	 * before repair-idref there should be an repair-add-id (pagenum, note, annotation). 
	 */
	
	@Override
	protected boolean execute(Map parameters) throws TransformerRunException {
		
		File input = FilenameOrFileURI.toFile((String)parameters.get("input"));
		File output = FilenameOrFileURI.toFile((String)parameters.get("output"));
		boolean force = ((String)parameters.get("forceRun")).contentEquals("true");						
		SystemPropertyHandler propertyHandler = new SystemPropertyHandler(parameters);
		propertyHandler.set();
						
		try {
			
			if(input.getCanonicalPath().equals(output.getCanonicalPath())) {
				throw new TransformerRunException(i18n("INPUT_OUTPUT_SAME"));
			}
			
			mCatalogURIResolver = new CatalogURIResolver();
			
			/*
			 * Confirm that input prerequisites are met (see javadoc of #confirmPrerequisites).
	    	 * Populate the parameters Map with document meta info.
	    	 */
			confirmPrerequisites(input,parameters);
			
			
			/*
			 * Create an ordered List with the user activated category names
			 */
			List<Category.Name> nameList = getActiveCategories((String)parameters.get("runCategories"));
			
			
			/*
			 * Create Category instances, populated with executors
			 */
			List<Category> categories = new LinkedList<Category>(); 			
			for (Category.Name name : nameList) {
				categories.add(createCategory(name, parameters, (String)parameters.get("DTBookVersion")));				
			}
			
			/* 
			 * Run the indenter last in the chain, this is harmless so always active
			 */
			categories.add(createCategory(Category.Name.INDENT, parameters, (String)parameters.get("DTBookVersion")));
			
			/*
			 * Execute the Executors.
			 */
			FileJuggler juggler = new FileJuggler(input, output);
			int progressLen = getActiveExecutorCount(categories);
			double progress = 0;
			for(Category category : categories) {				
				InputState state = getInputState(juggler.getInput());
				if(force||category.supportsInputState(state)) {
					this.sendMessage(i18n("RUNNING_CATEGORY",i18n(category.getName().toString())),MessageEvent.Type.INFO_FINER);
			    	for(Executor exec : category) {
			    		this.checkAbort();
			    		this.sendMessage(i18n("RUNNING_EXECUTOR", exec.getNiceName()),MessageEvent.Type.INFO_FINER);
			    		exec.execute(new StreamSource(juggler.getInput()), new StreamResult(juggler.getOutput()));
			    		juggler.swap();
			    		progress++;
			    		this.sendMessage(progress/progressLen);
			    	}
				}else{
					this.sendMessage(i18n("CATEGORY_SKIPPED", i18n(category.getName().name()), i18n(state.name())), MessageEvent.Type.INFO_FINER);
					progress += category.size();
					this.sendMessage(progress/progressLen);
				}
			}
			
			
			/*
			 * Get the result to final output dir,
			 * including aux files
			 */
			finalize(input, output, juggler);
							    		    	
		} catch (Exception e) {
			if(e instanceof TransformerRunException) throw (TransformerRunException)e;
			throw new TransformerRunException(e.getMessage(),e);
		}finally{			
			propertyHandler.reset();
		}
		return true;
	}
	
	private int getActiveExecutorCount(List<Category> categories) {
		int i = 0;
		for(Category category : categories) {
			i+= category.size();
		}
		return i;
	}

	/**
	 * Activate categories based on the runCategories user inparam.
	 * <p>The returned set contains ExecutorCategory instances in
	 * the same order as their names appear in the inparam, so category
	 * execution order is defined by the inparam string.</p>
	 * 
	 */
	private List<Category.Name> getActiveCategories(String param) {
		List<Category.Name> list = new LinkedList<Category.Name>();
		if(param.contentEquals("NOTHING")) return list;
		String[] wantedCategories = param.split("_");
		for (int i = 0; i < wantedCategories.length; i++) {			
			list.add(Category.Name.valueOf(wantedCategories[i]));			
		}				
		return list;
	}

	
	/**
	 * Copy aux files over to dest dir if not same as input.
	 * Delete the manifest file since it will be replaced
	 * with the output from the Juggler.
	 * Close the juggler.
	 * @throws IOException If juggle close fails. 
	 */
	private void finalize(File input, File output, FileJuggler files) throws IOException  {
		if(!input.getParentFile().equals(output.getParentFile())) {
			try{
				Fileset toCopy = new FilesetImpl(input.toURI(),this,false,false);
				EFolder dest = new EFolder(output.getParentFile());
				dest.addFileset(toCopy, true);
				File manifest = new File(dest,input.getName());
				manifest.delete();
			}catch (Exception e) {
				this.sendMessage(i18n("AUX_COPY_ERROR",e.getMessage()), MessageEvent.Type.ERROR);
			}							
		}
		files.close();
	}
	
	/**
	 * Check input prerequisites for running the Executors:
	 * <ul>	
	 * 	<li>DTBook namespace present</li>
	 * 	<li>DOCTYPE-@version inambiguity</li>
	 * </ul>
	 * <p>Populate the parameters inparam with document info</p> 
	 * @throws TransformerRunException if prerequisites are not met
	 */
	@SuppressWarnings("unchecked")
	private void confirmPrerequisites(File input, Map parameters) throws Exception {
    	Peeker peeker = PeekerPool.getInstance().acquire();
    	PeekResult result;
		
    	try {
			result = peeker.peek(input);			
			//check that we are namespaced as expected
			// jpritchett@rfbd.org, 31 Jan 2008:  Added test for null (Bug #1879846)
			if(result.getRootElementNsUri() == null ||
			   !result.getRootElementNsUri().equals(Namespaces.Z2005_DTBOOK_NS_URI)) {
				throw new TransformerRunException(i18n("INPUT_ERROR_NAMESPACE", Namespaces.Z2005_DTBOOK_NS_URI));
			}
			
			String publicId = result.getPrologPublicId();
			String systemId = result.getPrologSystemId();
			String version = result.getRootElementAttributes().getValue("version");
	    				
			//check doctype/@version match			
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
			
			//populate parameters
			parameters.put("DTBookVersion", version);
						
		} finally {      	
			PeekerPool.getInstance().release(peeker);
		}				    	
	}

	@SuppressWarnings("unchecked")	
	private Category createCategory(Category.Name name, Map parameters, String inputDTBookVersion) {
		final String[] v2005_1 = {"2005-1"};		
		final String[] v2005_2 = {"2005-2"};
		final String[] v2005_1_2 = {"2005-1","2005-2"};
		final String[] v2005_1_2_3 = {"2005-1","2005-2","2005-3"};
						
		List<Executor> executors = new LinkedList<Executor>();
		Set<InputState> supportedStates = new HashSet<InputState>();
		
		/*
		 * Instantiate a message emitter to listen to messages from Saxon
		 */
    	MessageEmitter emitter = new MessageEmitter();
    	emitter.setWriter(new MessageEmitterWriter(this));
    	    	
    	if(name==Category.Name.TIDY) {
    		
    		/*
    		 * Populate the executors of the TIDY category 
    		 */    		
    		    		
    		if(((String)parameters.get("simplifyHeadingLayout")).contentEquals("true")) {
    			//tidy-level-cleaner.xsl is optional
    			executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/tidy-level-cleaner.xsl"),v2005_1_2_3,i18n("LEVEL_CLEANER"),this,this,this,emitter));
    		}    		
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/tidy-move-pagenum.xsl"),v2005_1_2_3,i18n("MOVE_PAGENUM"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/tidy-pagenum-type.xsl"),v2005_1_2_3,i18n("TIDY_PAGENUM_TYPE"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/tidy-change-inline-pagenum-to-block.xsl"),v2005_1_2_3,i18n("CHANGE_INLINE_PAGENUM"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/tidy-remove-empty-elements.xsl"),v2005_1_2_3,i18n("TIDY_REMOVE_EMPTY_ELEMENTS"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/tidy-add-author-title.xsl"),v2005_1_2_3,i18n("ADD_AUTHOR_AND_TITLE"),this,this,this,emitter));
    		executors.add(new LangExecutor(parameters, this.getClass().getResource("./xslt/tidy-add-lang.xsl"), i18n("ADD_LANG"), this, this, this, emitter));
    		
    		if(((String)parameters.get("externalizeWhitespace")).contentEquals("true")) {
    			//tidy-externalize-whitespace.xsl is optional
    			executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/tidy-externalize-whitespace.xsl"),v2005_1_2,i18n("EXTERNALIZE_WHITESPACE"),this,this,this,emitter));
    		} 
    		
    		//run the indenter last in the chain, this is harmless so always active
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/tidy-indent.xsl"),v2005_1_2,i18n("INDENT"),this,this,this,emitter));

    		/*
    		 * Populate the supported states of the TIDY category 
    		 */    		    		
    		supportedStates.add(InputState.VALID);
    		
    	}else if (name==Category.Name.REPAIR) {
    		
    		/*
    		 * Populate the executors of the REPAIR category 
    		 */    		
    		    		
    		//optional charset recoder
    		//this should always be run first in the repair category
    		if(((String)parameters.get("fixCharset")).contentEquals("true")) {
    			executors.add(new CharsetExecutor(parameters,i18n("CHARSET_FIXER"),this));
    		}
    		//all level repair needs to be added in sequential order:
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-levelnormalizer.xsl"),v2005_1_2_3,i18n("LEVEL_NORMALIZER"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-levelsplitter.xsl"),v2005_1_2_3,i18n("LEVEL_SPLITTER"),this,this,this,emitter));
    		if(((String)parameters.get("repairLevelImpl")).contentEquals("xslt 1.0")) {
	    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-level1.xsl"),v2005_1_2_3,i18n("REPAIR_LEVEL_1"),this,this,this,emitter));
	    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-level2.xsl"),v2005_1_2_3,i18n("REPAIR_LEVEL_2"),this,this,this,emitter));
	    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-level3.xsl"),v2005_1_2_3,i18n("REPAIR_LEVEL_3"),this,this,this,emitter));
	    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-level4.xsl"),v2005_1_2_3,i18n("REPAIR_LEVEL_4"),this,this,this,emitter));
	    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-level5.xsl"),v2005_1_2_3,i18n("REPAIR_LEVEL_5"),this,this,this,emitter));
	    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-level6.xsl"),v2005_1_2_3,i18n("REPAIR_LEVEL_6"),this,this,this,emitter));
    		} else if(((String)parameters.get("repairLevelImpl")).contentEquals("xslt 2.0")) {
    			executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-add-levels.xsl"),v2005_1_2_3,i18n("REPAIR_LEVELS"),this,this,this,emitter));
    		}

    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-remove-illegal-headings.xsl"),v2005_1_2_3,i18n("REMOVE_ILLEGAL_HEADINGS"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-flatten-redundant-nesting.xsl"),v2005_1_2_3,i18n("FLATTEN_REDUNDANT_NESTING"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-complete-structure.xsl"),v2005_1_2_3,i18n("COMPLETE_STRUCTURE"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-lists.xsl"),v2005_1_2_3,i18n("REPAIR_LISTS"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-idref.xsl"),v2005_1_2_3,i18n("REPAIR_IDREF"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-remove-empty-elements.xsl"),v2005_1_2_3,i18n("REPAIR_REMOVE_EMPTY_ELEMENTS"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-pagenum-type.xsl"),v2005_1_2_3,i18n("REPAIR_PAGENUM_TYPE"),this,this,this,emitter));    		
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-metadata.xsl"),v2005_1_2_3,i18n("REPAIR_METADATA"),this,this,this,emitter));

    		/*
    		 * Populate the supported states of the REPAIR category 
    		 */
    		supportedStates.add(InputState.INVALID);
    		
    	}else if (name==Category.Name.NARRATOR) {
    		
    		/*
    		 * The narrator category is intendede to have a predictable set of executors run regardless of whether input is valid or invalid.
    		 * Therefore, we duplicate references to executors that appear in the REPAIR and TIDY categories above.
    		 */
    		
    		/*
    		 * executors from the tidy category
    		 */
    		if(((String)parameters.get("simplifyHeadingLayout")).contentEquals("true")) {
    			//tidy-level-cleaner.xsl is optional
    			executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/tidy-level-cleaner.xsl"),v2005_1_2_3,i18n("LEVEL_CLEANER"),this,this,this,emitter));
    		}    		
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/tidy-move-pagenum.xsl"),v2005_1_2_3,i18n("MOVE_PAGENUM"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/tidy-pagenum-type.xsl"),v2005_1_2_3,i18n("TIDY_PAGENUM_TYPE"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/tidy-change-inline-pagenum-to-block.xsl"),v2005_1_2_3,i18n("CHANGE_INLINE_PAGENUM"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/tidy-remove-empty-elements.xsl"),v2005_1_2_3,i18n("TIDY_REMOVE_EMPTY_ELEMENTS"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/tidy-add-author-title.xsl"),v2005_1_2_3,i18n("ADD_AUTHOR_AND_TITLE"),this,this,this,emitter));
    		executors.add(new LangExecutor(parameters, this.getClass().getResource("./xslt/tidy-add-lang.xsl"), i18n("ADD_LANG"), this, this, this, emitter));

    		/*
    		 * executors from the repair category
    		 */
    		//all level repair needs to be added in sequential order:
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-levelnormalizer.xsl"),v2005_1_2_3,i18n("LEVEL_NORMALIZER"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-levelsplitter.xsl"),v2005_1_2_3,i18n("LEVEL_SPLITTER"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-level1.xsl"),v2005_1_2_3,i18n("REPAIR_LEVEL_1"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-level2.xsl"),v2005_1_2_3,i18n("REPAIR_LEVEL_2"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-level3.xsl"),v2005_1_2_3,i18n("REPAIR_LEVEL_3"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-level4.xsl"),v2005_1_2_3,i18n("REPAIR_LEVEL_4"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-level5.xsl"),v2005_1_2_3,i18n("REPAIR_LEVEL_5"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-level6.xsl"),v2005_1_2_3,i18n("REPAIR_LEVEL_6"),this,this,this,emitter));
    		
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-remove-illegal-headings.xsl"),v2005_1_2_3,i18n("REMOVE_ILLEGAL_HEADINGS"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-flatten-redundant-nesting.xsl"),v2005_1_2_3,i18n("FLATTEN_REDUNDANT_NESTING"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-complete-structure.xsl"),v2005_1_2_3,i18n("COMPLETE_STRUCTURE"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-lists.xsl"),v2005_1_2_3,i18n("REPAIR_LISTS"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-idref.xsl"),v2005_1_2_3,i18n("REPAIR_IDREF"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-remove-empty-elements.xsl"),v2005_1_2_3,i18n("REPAIR_REMOVE_EMPTY_ELEMENTS"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-pagenum-type.xsl"),v2005_1_2_3,i18n("REPAIR_PAGENUM_TYPE"),this,this,this,emitter));    		
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-metadata.xsl"),v2005_1_2_3,i18n("REPAIR_METADATA"),this,this,this,emitter));

    		
    		/*
    		 * Populate the executors of the NARRATOR category 
    		 */
    		executors.add(new NarratorMetadataExecutor(parameters, this.getClass().getResource("./xslt/narrator-metadata.xsl"), i18n("NARRATOR_METADATA"), this, this, this, emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/narrator-headings.xsl"),v2005_1_2_3,i18n("NARRATOR_EMPTY_HEADINGS"),this,this,this,emitter));
    		executors.add(new NarratorMetadataExecutor(parameters, this.getClass().getResource("./xslt/narrator-title.xsl"), i18n("NARRATOR_TITLE"), this, this, this, emitter));
    		executors.add(new NarratorMetadataExecutor(parameters, this.getClass().getResource("./xslt/narrator-lists.xsl"), i18n("NARRATOR_LISTS"), this, this, this, emitter));
    		
    		
    		/*
    		 * Populate the supported states of the NARRATOR category 
    		 */
    		supportedStates.add(InputState.VALID);
    		supportedStates.add(InputState.INVALID);
    		
    	}else if (name==Category.Name.INDENT) {
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/tidy-indent.xsl"),v2005_1_2_3,i18n("INDENT"),this,this,this,emitter));
    		
    		supportedStates.add(InputState.INVALID);
    		supportedStates.add(InputState.VALID);
    	}
		
    	
    	for (int i = 0; i < executors.size(); i++) {
    		Executor executor = executors.get(i);
       		if(!executor.supportsVersion(inputDTBookVersion)) {
    			executors.remove(i);
        		String message = i18n("REMOVING", executor.getNiceName(), inputDTBookVersion);
        		this.sendMessage(message,Type.WARNING,Cause.INPUT);
    		}
		}
    	
    	if(executors.isEmpty()) {
    		String message = i18n("EMPTY_CATEGORY", executors.toString());
    		this.sendMessage(message,Type.WARNING,Cause.INPUT);
    	}
    	
		return new Category(name,supportedStates,executors);
	}
	
	private InputState getInputState(File input) throws ValidatorException {		
		return getInputState(input, null);
	}
	
	private InputState getInputState(File input, Set<URL> extraSchemas) throws ValidatorException {	
		
    	this.sendMessage(i18n("VALIDATING", input),MessageEvent.Type.INFO_FINER);
    			
		ValidatorFactory vfac = ValidatorFactory.newInstance();		
		mHadValidationErrors = false;
		try{			
			Fileset dtbookFileset = new FilesetImpl(input.toURI(),this,true,false);						
			Validator validator = vfac.newValidator(FilesetType.DTBOOK_DOCUMENT);
			validator.setListener(this);			
			
			if(extraSchemas!=null) {
				String type = "org.daisy.util.fileset.impl.Z3986DtbookFileImpl";
				for(URL url : extraSchemas) {
					validator.setSchema(url,type);
				}
			}			
			validator.validate(dtbookFileset);		
		} catch (FilesetFatalException e) {
			this.sendMessage(i18n("WAS_MALFORMED"),MessageEvent.Type.INFO);
			return InputState.MALFORMED;			
		}	
		
		if (mHadValidationErrors) {
			this.sendMessage(i18n("WAS_INVALID"),MessageEvent.Type.INFO);
			return InputState.INVALID; 
		}
		
		this.sendMessage(i18n("WAS_VALID"),MessageEvent.Type.INFO_FINER);
		return InputState.VALID;
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.xml.transform.URIResolver#resolve(java.lang.String, java.lang.String)
	 */
	public Source resolve(String href, String base) throws TransformerException {		
		System.err.println("dtbookfix URIResolver resolve, href=" + href + " , base=" + base);
		return mCatalogURIResolver.resolve(href, base);
	}

	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String)
	 */
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		//System.err.println("dtbookfix resolveEntity, publicId=" + publicId + " , systemId=" + systemId);
		return CatalogEntityResolver.getInstance().resolveEntity(publicId, systemId);				
	}
	
	/**
	 * Get and forward XSLT messages.
	 */
	@SuppressWarnings("unused")
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
			if (s.length()>0 && !s.startsWith("<?xml")) mT.sendMessage("XSLT message: " + s,MessageEvent.Type.INFO,MessageEvent.Cause.SYSTEM,null);			
		}
		 
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.pipeline.core.transformer.TransformerDelegateListener#delegateLocalize(java.lang.String, java.lang.Object)
	 */
	public String delegateLocalize(String key, Object[] params) {
		if(params==null) {
			return i18n(key);
		}
		return i18n(key,params);
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.pipeline.core.transformer.TransformerDelegateListener#delegateMessage(java.lang.Object, java.lang.String, org.daisy.pipeline.core.event.MessageEvent.Type, org.daisy.pipeline.core.event.MessageEvent.Cause, javax.xml.stream.Location)
	 */
	@SuppressWarnings("unused")
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
	@SuppressWarnings("unused")
	public void delegateProgress(Object delegate,double progress) {
		//ignore				
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.pipeline.core.transformer.TransformerDelegateListener#delegateCheckAbort()
	 */
	public boolean delegateCheckAbort() {
		return super.isAborted();		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorListener#exception(org.daisy.util.fileset.validation.Validator, java.lang.Exception)
	 */
	@SuppressWarnings("unused")
	public void exception(Validator validator, Exception e) {		
		mHadValidationErrors = true;
		Location loc = LocusTransformer.newLocation(e);
		this.sendMessage(e.getMessage(), MessageEvent.Type.ERROR, MessageEvent.Cause.INPUT,loc);						
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorListener#report(org.daisy.util.fileset.validation.Validator, org.daisy.util.fileset.validation.message.ValidatorMessage)
	 */
	@SuppressWarnings("unused")
	public void report(Validator validator, ValidatorMessage message) {
		mHadValidationErrors = true;
		Location loc = LocusTransformer.newLocation(message);
		this.sendMessage(message.getMessage(), MessageEvent.Type.DEBUG, MessageEvent.Cause.INPUT,loc);		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorListener#inform(org.daisy.util.fileset.validation.Validator, java.lang.String)
	 */
	@SuppressWarnings("unused")
	public void inform(Validator validator, String information) {
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorListener#progress(org.daisy.util.fileset.validation.Validator, double)
	 */
	@SuppressWarnings("unused")
	public void progress(Validator validator, double progress) {
		//ignore
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.interfaces.FilesetErrorHandler#error(org.daisy.util.fileset.exception.FilesetFileException)
	 */
	@SuppressWarnings("unused")
	public void error(FilesetFileException ffe) throws FilesetFileException {		
		Throwable root =ffe.getRootCause();		
		if(root==null) root = ffe.getCause();	
		
		Location loc = null;
		if(root instanceof SAXParseException) {
			loc=LocusTransformer.newLocation((SAXParseException)root);
		}
		
		if(!(ffe instanceof FilesetFileWarningException)) {
			/*
			 * Because we are using tempdirs, have to filter out all
			 * exceptions about missing referenced files.
			 */	
			if (!(root instanceof FileNotFoundException)) {
				mHadValidationErrors = true;				
				this.sendMessage(root.getMessage(), MessageEvent.Type.DEBUG, MessageEvent.Cause.INPUT, loc);
			}			
		}else{
			this.sendMessage(root.getMessage(), MessageEvent.Type.DEBUG, MessageEvent.Cause.INPUT, loc);
		}		
	}
	
	private class SystemPropertyHandler {
		private final static String DTBOOK_VALIDATOR_IMPL = "org.daisy.util.fileset.validation:http://www.daisy.org/fileset/DTBOOK_DOCUMENT";
		private final static String JAVAX_FACTORY_KEY = "javax.xml.transform.TransformerFactory";
		private String factory = null;
		private String initXsltFactoryProp = null;
		private String initDtbookValidatorFactoryProp = null;
		
		private SystemPropertyHandler(Map parameters) {
			factory = (String)parameters.get("factory");	
		}
						
		private void set() {

    	    //Set a sysprop for the XSLT factory. 
			//This needs to be a 1.0 and 2.0 processor, so we are expecting the tdf to namedrop the Saxon8 identifier.
			initXsltFactoryProp = System.getProperty(JAVAX_FACTORY_KEY);		
			System.setProperty(JAVAX_FACTORY_KEY, factory);
							
			 //Set a sysprop to help the dtbook validator factory discovery
			initDtbookValidatorFactoryProp = System.getProperty(DTBOOK_VALIDATOR_IMPL);
			if(initDtbookValidatorFactoryProp==null){
				System.setProperty(
						DTBOOK_VALIDATOR_IMPL,
				"org.daisy.util.fileset.validation.ValidatorImplDtbook");
			}
		}
		
		private void reset() {
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
		
	}

	
}
