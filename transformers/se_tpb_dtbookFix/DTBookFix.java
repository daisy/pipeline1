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
package se_tpb_dtbookFix;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.Location;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.XMLEvent;
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
import org.daisy.util.file.FileJuggler;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetErrorHandler;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.Z3986DtbookFile;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.exception.FilesetFileWarningException;
import org.daisy.util.fileset.impl.FilesetFileFactory;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.validation.Validator;
import org.daisy.util.fileset.validation.ValidatorFactory;
import org.daisy.util.fileset.validation.ValidatorListener;
import org.daisy.util.fileset.validation.exception.ValidatorException;
import org.daisy.util.fileset.validation.message.ValidatorMessage;
import org.daisy.util.text.URIUtils;
import org.daisy.util.xml.LocusTransformer;
import org.daisy.util.xml.NamespaceReporter;
import org.daisy.util.xml.Namespaces;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogURIResolver;
import org.daisy.util.xml.peek.PeekResult;
import org.daisy.util.xml.peek.Peeker;
import org.daisy.util.xml.peek.PeekerPool;
import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.pool.StAXOutputFactoryPool;
import org.daisy.util.xml.stax.DoctypeParser;
import org.daisy.util.xml.stax.StaxEntityResolver;
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
	 * ideas (mg20071121)
	 *  optional add GUID or better inparam UID to meta
	 *  optional, docauthor doctitle/dc:title values as inparams?
	 *  
	 * (jh20071204)
	 * before repair-idref there should be an repair-add-id (pagenum, note, annotation). 
	 */
	
	@Override
	protected boolean execute(Map<String,String> parameters) throws TransformerRunException {
		
		File input = FilenameOrFileURI.toFile(parameters.get("input"));
		File output = FilenameOrFileURI.toFile(parameters.get("output"));
		boolean force = (parameters.get("forceRun")).contentEquals("true");						
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
			List<Category.Name> nameList = getActiveCategories(parameters.get("runCategories"));
			
			
			/*
			 * Create Category instances, populated with executors
			 */
			List<Category> categories = new LinkedList<Category>(); 			
			for (Category.Name name : nameList) {
				categories.add(createCategory(name, parameters, parameters.get("DTBookVersion")));				
			}
			
			/* 
			 * Run the indenter last in the chain, this is harmless so always active
			 */
			categories.add(createCategory(Category.Name.INDENT, parameters, parameters.get("DTBookVersion")));
			
			/*
			 * Store the DTD declaration (inc internal subset) for re-insertion at the end of the process.
			 * (It may be a desirable alternate approach to make all XSLTs handle internal subsets etc gracefully)
			 */			
			parameters.put("DTDdeclaration", getDTDdecl(input));
			
			/*
			 * Execute the Executors.
			 */
			FileJuggler juggler = new FileJuggler(input, output);
			int progressLen = getActiveExecutorCount(categories);
			double progress = 0;
			for(Category category : categories) {				
				InputState state = getInputState(juggler, parameters);
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
			 * Reinsert the DTD declaration
			 */
			setDTDdecl(juggler,parameters);
			
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
		if(!input.getAbsoluteFile().getParentFile().equals(output.getAbsoluteFile().getParentFile())) {
			try{
			    // We cannot just instantiate a fileset with the input file as manifest, since
			    // the input file may contain broken links.
			    
			    // Get the URIs from the *fixed* file
			    FilesetFileFactory factory = FilesetFileFactory.newInstance();
			    FilesetFile filesetFile = factory.newFilesetFile("Z3986DtbookFile", files.getInput().toURI());
			    Z3986DtbookFile zed = (Z3986DtbookFile)filesetFile;
			    zed.parse();
			    Collection<String> uris = zed.getUriStrings();
			    
			    for (String uri : uris) {			    	
			    	URI resolvedFrom = URIUtils.resolve(input.toURI(), uri);
			    	URI resolvedTo = URIUtils.resolve(output.toURI(), uri);
			        if ("file".equals(resolvedFrom.getScheme()) && "file".equals(resolvedTo.getScheme())) {
        		        File from = new File(resolvedFrom.getPath());
        		        File to = new File(resolvedTo.getPath());
        		        if (from.exists() && from.isFile()) {
        		            // We only try to copy the file if it actually exists. If a file
        		            // has been renamed by an executor it will not reside in the input
        		            // directory. In those cases the executors themselves are responsible
        		            // for copying the file to the output dir.
        		            FileUtils.copyFile(from, to);
        		        }
			        }
			    }			    
			}catch (Exception e) {			    
			    e.printStackTrace();			    
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
	
	private Category createCategory(Category.Name name, Map<String,String> parameters, String inputDTBookVersion) {
		@SuppressWarnings("unused")
		final String[] v2005_1 = {"2005-1"};		
		@SuppressWarnings("unused")
		final String[] v2005_2 = {"2005-2"};
		@SuppressWarnings("unused")
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
    		    		
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/tidy-remove-empty-elements.xsl"),v2005_1_2_3,i18n("TIDY_REMOVE_EMPTY_ELEMENTS"),this,this,this,emitter));
    		if((parameters.get("simplifyHeadingLayout")).contentEquals("true")) {
    			//tidy-level-cleaner.xsl is optional
    			executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/tidy-level-cleaner.xsl"),v2005_1_2_3,i18n("LEVEL_CLEANER"),this,this,this,emitter));
    		}    		
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/tidy-move-pagenum.xsl"),v2005_1_2_3,i18n("MOVE_PAGENUM"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/tidy-pagenum-type.xsl"),v2005_1_2_3,i18n("TIDY_PAGENUM_TYPE"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/tidy-change-inline-pagenum-to-block.xsl"),v2005_1_2_3,i18n("CHANGE_INLINE_PAGENUM"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/tidy-add-author-title.xsl"),v2005_1_2_3,i18n("ADD_AUTHOR_AND_TITLE"),this,this,this,emitter));
    		executors.add(new LangExecutor(parameters, this.getClass().getResource("./xslt/tidy-add-lang.xsl"), i18n("ADD_LANG"), this, this, this, emitter));
    		
    		if(parameters.get("externalizeWhitespace").contentEquals("true")) {
    			//tidy-externalize-whitespace.xsl is optional
    			executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/tidy-externalize-whitespace.xsl"),v2005_1_2_3,i18n("EXTERNALIZE_WHITESPACE"),this,this,this,emitter));
    		} 
    		
    		//run the indenter last in the chain, this is harmless so always active
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/tidy-indent.xsl"),v2005_1_2_3,i18n("INDENT"),this,this,this,emitter));

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
    		if(parameters.get("fixCharset").contentEquals("true")) {
    			executors.add(new CharsetExecutor(parameters,i18n("CHARSET_FIXER"),this));
    		}
    		//all level repair needs to be added in sequential order:
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-levelnormalizer.xsl"),v2005_1_2_3,i18n("LEVEL_NORMALIZER"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-levelsplitter.xsl"),v2005_1_2_3,i18n("LEVEL_SPLITTER"),this,this,this,emitter));
   			executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-add-levels.xsl"),v2005_1_2_3,i18n("REPAIR_LEVELS"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-remove-illegal-headings.xsl"),v2005_1_2_3,i18n("REMOVE_ILLEGAL_HEADINGS"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-flatten-redundant-nesting.xsl"),v2005_1_2_3,i18n("FLATTEN_REDUNDANT_NESTING"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-complete-structure.xsl"),v2005_1_2_3,i18n("COMPLETE_STRUCTURE"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-lists.xsl"),v2005_1_2_3,i18n("REPAIR_LISTS"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-idref.xsl"),v2005_1_2_3,i18n("REPAIR_IDREF"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-remove-empty-elements.xsl"),v2005_1_2_3,i18n("REPAIR_REMOVE_EMPTY_ELEMENTS"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-pagenum-type.xsl"),v2005_1_2_3,i18n("REPAIR_PAGENUM_TYPE"),this,this,this,emitter));    		
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/repair-metadata.xsl"),v2005_1_2_3,i18n("REPAIR_METADATA"),this,this,this,emitter));
    		executors.add(new EmptyMathMLStripExecutor(parameters, i18n("NARRATOR_MATHML_STRIP"), this));
    		executors.add(new InvalidURIExecutor(parameters, i18n("REPAIR_URI"), this));
    		
    		/*
    		 * Populate the supported states of the REPAIR category 
    		 */
    		supportedStates.add(InputState.INVALID);
    		supportedStates.add(InputState.VALID);
    		
    	}else if (name==Category.Name.NARRATOR) {
    		
    		/*
    		 * The narrator category is intended to have a predictable set of executors run regardless of whether input is valid or invalid.
    		 * Therefore, it should *always* be called in conjunction with at least the REPAIR (and if possible the TIDY) category above.
    		 */    		
    		/*
    		 * Populate the executors of the NARRATOR category 
    		 */    	    
    		executors.add(new NarratorMetadataExecutor(parameters, this.getClass().getResource("./xslt/narrator-metadata.xsl"), i18n("NARRATOR_METADATA"), this, this, this, emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/narrator-headings-r14.xsl"),v2005_1_2_3, i18n("NARRATOR_HEADINGS_R14"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters,this.getClass().getResource("./xslt/narrator-headings-r100.xsl"),v2005_1_2_3, i18n("NARRATOR_HEADINGS_R100"),this,this,this,emitter));
    		executors.add(new XSLTExecutor(parameters, this.getClass().getResource("./xslt/narrator-title.xsl"), v2005_1_2_3, i18n("NARRATOR_TITLE"), this, this, this, emitter));
    		executors.add(new XSLTExecutor(parameters, this.getClass().getResource("./xslt/narrator-lists.xsl"), v2005_1_2_3, i18n("NARRATOR_LISTS"), this, this, this, emitter));
//    		executors.add(new XSLTExecutor(parameters, this.getClass().getResource("./xslt/narrator-empty-cells.xsl"), v2005_1_2_3, i18n("NARRATOR_EMPTY_CELLS"), this, this, this, emitter));
//    		if(parameters.get("renameJpeg").contentEquals("true")) {
//    			executors.add(new JpegRenameExecutor(parameters, i18n("NARRATOR_JPEG_RENAMER"), this));
//    		}
//    		if(parameters.get("renameIllegalFilenames").contentEquals("true")) {
//    		    executors.add(new IllegalFilenameExecutor(parameters, i18n("NARRATOR_ILLEGAL_FILENAME"), this));
//    		}
    		
    		
    		/*
    		 * Populate the supported states of the NARRATOR category 
    		 */
    		supportedStates.add(InputState.VALID);
    		
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
	
	private InputState getInputState(FileJuggler juggler, Map<String,String> parameters) throws ValidatorException {
	    // Reinsert the doctype declaration each time the document is validated.
	    setDTDdecl(juggler, parameters);
	    return getInputState(juggler.getInput());
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
	
	/**
	 * Insert a DTD declaration, stored in the parameters map.
	 */
	private void setDTDdecl(FileJuggler juggler,Map<String, String> parameters) {
		String DTDdecl = parameters.get("DTDdeclaration");
		if(DTDdecl == null) return;  //input didnt have a DTD decl
		
		Map<String, Object> xifProperties = null;
		Map<String, Object> xofProperties = null;
		XMLInputFactory xif = null;
		XMLOutputFactory xof = null;
		XMLEventFactory xef = null;
		FileInputStream fis = null;
		FileOutputStream fos = null;
		
		try {
		    // Is there any math in here?
	        NamespaceReporter namespaceReporter = new NamespaceReporter(juggler.getInput().toURI().toURL());
	        Set<String> namespaces = namespaceReporter.getNamespaceURIs();
	        boolean containsMath =  namespaces.contains(Namespaces.MATHML_NS_URI);
	        if (!containsMath) {
	            DTDdecl = stripInternalSubset(DTDdecl);
	        }
	        
			xifProperties = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(false);
			xofProperties = StAXOutputFactoryPool.getInstance().getDefaultPropertyMap();
			xofProperties.put(XMLOutputFactory.IS_REPAIRING_NAMESPACES, Boolean.FALSE);
			xif = StAXInputFactoryPool.getInstance().acquire(xifProperties);
			xof = StAXOutputFactoryPool.getInstance().acquire(xofProperties);
			xif.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
			fis = new FileInputStream(juggler.getInput());			
			XMLEventReader reader = xif.createXMLEventReader(fis);
			fos = new FileOutputStream(juggler.getOutput());
			XMLEventWriter writer = xof.createXMLEventWriter(fos);			
			xef = StAXEventFactoryPool.getInstance().acquire();
			
			boolean DTDadded = false;
			while(reader.hasNext()) {
				XMLEvent xe = reader.nextEvent();
				if(xe.getEventType()==XMLEvent.DTD) {
					writer.add(xef.createDTD(DTDdecl));
					DTDadded = true;
				}else if(xe.isStartElement() && !DTDadded) {
					writer.add(xef.createDTD(DTDdecl));
					writer.add(xe);
					DTDadded = true;
				}else{
					writer.add(xe);
				}
			}			
			
			if(reader!=null)reader.close();
			if(writer!=null)writer.flush();writer.close();
			
		} catch (Exception e) {
			sendMessage(i18n("ERROR",e.getMessage()),MessageEvent.Type.ERROR, MessageEvent.Cause.SYSTEM, null);			
		}finally{			
			if(fis!=null) try {fis.close();} catch (IOException e) {}
			if(fos!=null) try {fos.close();} catch (IOException e) {}
			if(xif!=null) StAXInputFactoryPool.getInstance().release(xif, xifProperties);
			if(xof!=null) StAXOutputFactoryPool.getInstance().release(xof, xofProperties);
			try {juggler.swap();} catch (IOException e) {}
		}
		
	}

	/**
	 * Strip out the internal subset from a doctype declaration.
	 * @param doctype a doctype declaration
	 * @return the doctype declaration without internal subset
	 */
	private String stripInternalSubset(String doctype) {
	    DoctypeParser doctypeParser = new DoctypeParser(doctype);
        StringBuilder builder = new StringBuilder("<!DOCTYPE ");
        builder.append(doctypeParser.getRootElem());
        boolean hasPublicID = false;
        String publicID = doctypeParser.getPublicId();
        if ((publicID != null) && (publicID.length() > 0)) {
            builder.append(" PUBLIC \"");
            builder.append(publicID);
            builder.append("\"");
            hasPublicID = true;
        }
        String systemID = doctypeParser.getSystemId();
        if ((systemID != null) && (systemID.length() > 0)) {
            if (!hasPublicID) {
                builder.append(" SYSTEM");
            }
            builder.append(" \"");
            builder.append(systemID);
            builder.append("\"");
        }
        // No internal subset added here...            
        builder.append(">");
        return builder.toString();
	}
	
	/**
	 * Get the entire input file DTD declaration, or null if the input file has no such 
	 * declaration.
	 */
	private String getDTDdecl(File input) {		
		Map<String, Object> properties = null;
		XMLInputFactory xif = null;
		FileInputStream fis = null;
		try {
			properties = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(false);
			xif = StAXInputFactoryPool.getInstance().acquire(properties);
			xif.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
			fis = new FileInputStream(input);
			XMLEventReader reader = xif.createXMLEventReader(fis);
			while(reader.hasNext()) {
				XMLEvent xe = reader.nextEvent();
				if(xe.getEventType() == XMLEvent.DTD) {
					DTD dtd = (DTD) xe;	
					return dtd.getDocumentTypeDeclaration();
				}else if(xe.isStartElement()) {
					return null;
				}
			}
		} catch (Exception e) {
			sendMessage(i18n("ERROR",e.getMessage()),MessageEvent.Type.ERROR, MessageEvent.Cause.SYSTEM, null);			
		}finally{
			if(fis!=null) try {fis.close();} catch (IOException e) {}
			StAXInputFactoryPool.getInstance().release(xif, properties);
		}
		return null;
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
		
		private SystemPropertyHandler(Map<String,String> parameters) {
			factory = parameters.get("factory");	
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
