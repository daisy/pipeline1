/*
 * DMFC - The DAISY Multi Format Converter
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

package int_daisy_xmlValidator;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.parsers.SAXParser;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.dmfc.exception.TransformerAbortException;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.file.EFile;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.exception.FilesetFileErrorException;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.exception.FilesetFileFatalErrorException;
import org.daisy.util.fileset.impl.FilesetFileFactory;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetErrorHandler;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.interfaces.ManifestFile;
import org.daisy.util.fileset.util.FilesetRegex;
import org.daisy.util.xml.Peeker;
import org.daisy.util.xml.PeekerImpl;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.pool.SAXParserPool;
import org.daisy.util.xml.sax.SAXConstants;
import org.daisy.util.xml.XMLUtils;
import org.daisy.util.xml.validation.SchemaLanguageConstants;
import org.daisy.util.xml.validation.SchematronMessage;
import org.daisy.util.xml.validation.ValidationException;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Validates an XML document against one or several schemas (DTD, RelaxNG, XSD, Schematron).
 * Simultaneous validation using both inlined and inparam schemas is supported.
 * <p>This transformer extends (and effectively makes redundant) se_tpb_xmlValidator.</p>
 * @author Markus Gylling
 * @deprecated use int_daisy_validator instead
 */
public class XmlValidator extends Transformer implements ErrorHandler, FilesetErrorHandler, ContentHandler {
	
	// whether to validate using inline schemas
	private boolean useInlineSchemas = true;
		
    //whether to attempt to check local URI resolvement using an instance of org.daisy.util.Fileset
	private boolean useFileset = true;
	
	// the input XML document
	private EFile inFile = null;
	
	
    //Collects all schemas that the input document should be validated against;
	//the schemas may occur inlined in input document, or in the schemas inparam
	private Map schemaSources = null;  //<Source>,<SchemaNSURI>
	  
	//Handle transformer abort by tracking severity level 
	private boolean abortOnValidationWarning = false;	
	private boolean abortOnValidationError = true; 	
	private boolean abortOnValidationFatal = true;
	private boolean hadValidationWarning=false;
	private boolean hadValidationError=false;
	private boolean hadValidationFatal=false;
	
	/**
	 * SAX ContentHandler specific
	 */
	private boolean isRootElementReported = false;	
	
	/**
	 * Convenience instance of FilesetRegex
	 */
	private FilesetRegex regex = null;

	/**
	 * Instantiator.
	 * @param inListener
	 * @param eventListeners
	 * @param isInteractive
	 */
    public XmlValidator(InputListener inListener, Set eventListeners, Boolean isInteractive) {
        super(inListener, eventListeners, isInteractive);
        schemaSources = new HashMap(); 
        regex = FilesetRegex.getInstance();
        checkSystemProperties();
    }
    
	protected boolean execute(Map parameters) throws TransformerRunException {
    	
    	
     	//Schema language neutral jaxp.validation driver     	 
    	SchemaFactory anySchemaFactory = null;
    	
    	this.progress(0);
    	
    	try {    		
    		//set the infile
    		inFile = new EFile(FilenameOrFileURI.toFile((String)parameters.remove("inXML")));
    		
    		//set the inline schemas bool
    		useInlineSchemas = ((String)parameters.remove("useInlineSchemas")).equals("true");

    		//set the fileset tests bool
    		useFileset = ((String)parameters.remove("useFileset")).equals("true");
    		
    		//determine abort level
    		setAbortLevel((String)parameters.remove("abortOn"));
    		
    		//run a saxparse; validate if doc has DTD, collect inline schema references in this.schemaSources
    		//if not wellformed, an exception is thrown from this.ErrorHandler.fatalError
        	Map features = new HashMap();
        	features.put(SAXConstants.SAX_FEATURE_NAMESPACES, Boolean.TRUE);
        	if(useInlineSchemas) features.put(SAXConstants.SAX_FEATURE_VALIDATION, Boolean.TRUE);        	
        	SAXParser saxParser = SAXParserPool.getInstance().acquire(features,null);
        	saxParser.getXMLReader().setErrorHandler(this);
        	saxParser.getXMLReader().setContentHandler(this);
        	saxParser.getXMLReader().setEntityResolver(CatalogEntityResolver.getInstance());
        	saxParser.getXMLReader().parse(inFile.asInputSource());
        	SAXParserPool.getInstance().release(saxParser,features,null);	
        	
        	this.progress(33.33);
        	this.checkLocalAbort();
        	
    		//complete population of the schemaSources map (<Source>,<SchemaNSURI>)
        	//with the entries in the schemas inparam
			String schemas = (String)parameters.remove("schemas");
			if(schemas!=null && schemas.length()>0) {
				String[] array = schemas.split(",");
				for (int i = 0; i < array.length; i++) {
					try{
					Map aSchema = toSchemaSource(array[i],null);
						if(aSchema!=null) {
							schemaSources.putAll(aSchema);
						}else{
							this.sendMessage(Level.WARNING,i18n("SCHEMA_INSTANTIATION_FAILURE", array[i]));							
						}
					}catch (Exception e) {
						this.sendMessage(Level.WARNING, i18n("SCHEMA_INSTANTIATION_FAILURE", array[i]) + e.getMessage());
					}
				}						
			}//if(schemas!=null && schemas.length()>0)
			
        	//validate using all schemas in schemaSources map
			HashMap factoryMap = new HashMap();
			for (Iterator iter = schemaSources.keySet().iterator(); iter.hasNext();) {
				Source source = null;
				try{
					source = (Source)iter.next();
					String schemaNsURI = (String)schemaSources.get(source);
					if(!factoryMap.containsKey(schemaNsURI)) {
						factoryMap.put(schemaNsURI,SchemaFactory.newInstance(schemaNsURI));
					}
					anySchemaFactory = (SchemaFactory)factoryMap.get(schemaNsURI);
					anySchemaFactory.setErrorHandler(this);
					anySchemaFactory.setResourceResolver(CatalogEntityResolver.getInstance());
					Schema schema = anySchemaFactory.newSchema(source);					
					Validator validator = schema.newValidator();
					validator.validate(new StreamSource(inFile.toURI().toURL().openStream()));
				}catch (Exception e) {
					this.sendMessage(Level.WARNING,i18n("VALIDATION_FAILURE",source.getSystemId()) + e.getMessage());
				}
	        	this.checkLocalAbort();
			}
			
			this.progress(66.66);
			
        	//run a fileset to test URIs
			if(useFileset){
				try{
					FilesetFile filesetFile = FilesetFileFactory.newInstance().newFilesetFile(inFile);
					if(filesetFile instanceof ManifestFile) {
						Fileset fileset = new FilesetImpl(filesetFile.getFile().toURI(), this, false, false);
					}else{
						this.sendMessage(Level.WARNING,i18n("FILESET_FAILURE") + i18n("UNKNOWN_GRAMMAR"));
					}
				}catch (Exception e) {
					this.sendMessage(Level.WARNING,i18n("FILESET_FAILURE") + e.getMessage());
				}
			}//if(useFileset)
        	this.checkLocalAbort();
			//done.
			
    	}catch (Exception e) {
    		sendMessage(Level.SEVERE, i18n("ABORTING", inFile));
    		throw new TransformerRunException(e.getMessage(), e);
		}

    	this.progress(99.99);
    	
    	return true;
    }
    
	private void checkLocalAbort() throws TransformerAbortException {
    	this.checkAbort();
		if ((this.hadValidationFatal && this.abortOnValidationFatal)||
				(this.hadValidationError && this.abortOnValidationError) ||
				(this.hadValidationWarning && this.abortOnValidationWarning)) {			
			throw new TransformerAbortException("Aborting because of validation report severity level breach");
		}		
	}

	private void setAbortLevel(String string) {
		//tweak the default values if needed
		if(string.equals("WARNING")) {
			this.abortOnValidationWarning = true;		
		}else if(string.equals("NONE")) {
			this.abortOnValidationFatal = false;		
			this.abortOnValidationError = false;
		}
	}
	
	/**
     * SAX ErrorHandler impl
     */
    public void warning(SAXParseException e) throws SAXException {
    	  this.hadValidationWarning = true;
          sendMessage(Level.WARNING, i18n("SAX_WARNING", this.getParams(e)));
    }

    /**
     * SAX ErrorHandler impl
     */
    public void error(SAXParseException e) throws SAXException {
    	//TODO an sch message may be a warning
    	this.hadValidationError = true;
    	//if this is a SchematronMessage String, reformat before sending    	
    	if(SchematronMessage.isMessage(e.getMessage())){
    		String schMessage = getSchematronMessage(e.getMessage());
    		sendMessage(Level.WARNING, i18n("SAX_ERROR", this.getParams(e,schMessage)));
    	}else{
    		sendMessage(Level.WARNING, i18n("SAX_ERROR", this.getParams(e)));
    	}
    }
        
    private String getSchematronMessage(String message) {
		try {
			SchematronMessage sm = new SchematronMessage(message);
			//since SchematronMessages are only syntax and not naming static, we need to
			//check for known implementations (such as zedmaps)
			//TODO when we have ZedVal jar available etc
			
		} catch (ValidationException ve) {
			return message;
		}    		  
		return "TODO: " + message;
	}

	/**
     * SAX ErrorHandler impl
     */
    public void fatalError(SAXParseException e) throws SAXException {
    	this.hadValidationFatal = true;
        sendMessage(Level.SEVERE, i18n("SAX_FATAL", this.getParams(e)));        
    	throw e;
    }

    /**
     * FilesetErrorHandler impl
     */
    public void error(FilesetFileException ffe) throws FilesetFileException {
		if(ffe instanceof FilesetFileFatalErrorException) {
			this.hadValidationFatal = true;
			sendMessage(Level.SEVERE, i18n("FILESET_FATAL", this.getParams(ffe)));
		}else if (ffe instanceof FilesetFileErrorException) {
			this.hadValidationError = true;
			sendMessage(Level.WARNING, i18n("FILESET_ERROR", this.getParams(ffe)));
		}else{
			this.hadValidationWarning = true;
			sendMessage(Level.WARNING, i18n("FILESET_WARNING", this.getParams(ffe)));
		}
				
	}
    	
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {		
    	if(!isRootElementReported){
    		isRootElementReported = true;
    		if(useInlineSchemas){
	    		//check for inline (non-prolog) schema references
    			try {
	    			Set xsis = XMLUtils.getXSISchemaLocationURIs(uri, localName, qName, atts);
		    		for (Iterator iter = xsis.iterator(); iter.hasNext();) {
						String str = (String) iter.next();						
						Map map = toSchemaSource(str,SchemaLanguageConstants.W3C_XML_SCHEMA_NS_URI);
						if (map!=null){
							schemaSources.putAll(map);
						}else{
							this.sendMessage(Level.WARNING,i18n("SCHEMA_INSTANTIATION_FAILURE", str));
						}																	
					}//for
    			} catch (Exception e) {
					throw new SAXException(e.getMessage(),e);
				}
    		}
    	} //if(!isRootElementReported)		
	}

	/**
	 * Converts an identifier string into one or several Source objects
	 * @param 
	 * 		identifier a resource identifier consisting of an absolute or relative filespec, or a prolog Public or System Id.
	 * 		schemaLanguageConstant the schema NS URI identifier, if null, this method will attempt to set the value 
	 * @return a Map (Source, NSURI) representing the input resource, null if identification failed
	 * @throws IOException 
	 * @throws SAXException 
	 */
	private Map toSchemaSource(String identifier, String schemaLanguageConstant) throws IOException, SAXException {
		/*
		 * examples of inparams here:
		 *   http://www.example.com/example.dtd
		 *   http://www.example.com/example.rng
		 *   http://www.example.com/example.xsd
		 *   example.dtd 
		 *   example.rng 
		 *   ../stuff/example.rng
		 *   ./stuff/example.rng
		 *   D:/example.sch
		 *   file://D:/example.sch
		 *   -//NISO//DTD dtbook 2005-1//EN
		 */
		 
		Map map = new HashMap();		
		File localSchemaFile = null; 
		URL schemaURL = null;
		identifier = identifier.trim();
		
		//first try to resolve a physical file		
		boolean isRemote = regex.matches(regex.URI_REMOTE, identifier);
		try{			
			if(!isRemote){
				localSchemaFile = FilenameOrFileURI.toFile(identifier);
				if(localSchemaFile==null||!localSchemaFile.exists()) {
						//we couldnt find an absolute file, try relative to input document					    
						URI u = inFile.getParentFolder().toURI().resolve(identifier);
					    localSchemaFile = new File(u);
					    if(localSchemaFile.exists()) {
						    schemaURL = localSchemaFile.toURI().toURL();
					    }					    
				} //if(!localSchemaFile.exists()) 
				else{
					schemaURL = localSchemaFile.toURI().toURL();
				}
			}
		}catch (Exception e) {
			//carry on
		}
		
		//if physical file resolve didnt work, or isRemote, try catalog
		if(schemaURL == null) {
			//file resolve didnt work above, or its remote try catalog
			URL url = CatalogEntityResolver.getInstance().resolveEntityToURL(identifier);
			if(url!=null){
				schemaURL = url;											
			}
		}
		
		//if catalog didnt work
		if(schemaURL == null) {
			if(isRemote){
				//TODO warning
			}	
			try{
				schemaURL = new URL(identifier);
			}catch (Exception e) {
				// TODO: report problem
				return null;
			}	
		}
		
		if(schemaURL != null) {
			//prepare return
		    //set Source
			StreamSource ss = new StreamSource(schemaURL.openStream());
		    ss.setSystemId(schemaURL.toExternalForm());
		    
		    //set schematype
		    String nsuri; 
		    if(schemaLanguageConstant==null) {
		    	//it didnt come as inparam
		    	nsuri = getSchemaType(schemaURL);
		    }else{
		    	//it came as inparam
		    	nsuri = schemaLanguageConstant;
		    }
		    	    
		    if(nsuri!=null) {
		    	if(nsuri.equals(SchemaLanguageConstants.RELAXNG_NS_URI)) {
		    		//need to check for schematron islands FIXME may occur in XSD as well
		    		//FIXME check first, or make sure this doesnt break when no sch in rng
		    		StreamSource schss = new StreamSource(schemaURL.openStream());
		    	    schss.setSystemId(schemaURL.toExternalForm());
		    	    //FIXME may be ISO schematron, have to check first
		    	    map.put(schss,SchemaLanguageConstants.SCHEMATRON_NS_URI);	    		
		    	}	    		    	
		    	map.put(ss,nsuri);
		    	return map;
		    }
		}
	    return null;
	}
	
	/**
	 * @return a SchemaLanguageConstant NS URI, or null if schema type was not detected
	 * @throws IOException 
	 * @throws SAXException 
	 */
	private String getSchemaType(URL url) throws SAXException, IOException {
		Peeker peeker = new PeekerImpl();
		//peeker.peek(url);
		String rootName = peeker.getRootElementLocalName();		
		if(rootName == "schema") {
			if(peeker.getRootElementNsUri().equals(SchemaLanguageConstants.SCHEMATRON_NS_URI)){
				return SchemaLanguageConstants.SCHEMATRON_NS_URI;
			}else if(peeker.getRootElementNsUri().equals(SchemaLanguageConstants.ISO_SCHEMATRON_NS_URI)){
				return SchemaLanguageConstants.ISO_SCHEMATRON_NS_URI;
			}else if(peeker.getRootElementNsUri().equals(SchemaLanguageConstants.W3C_XML_SCHEMA_NS_URI)){
				return SchemaLanguageConstants.W3C_XML_SCHEMA_NS_URI;
			}							
		}else if(rootName== "grammar" && peeker.getRootElementNsUri().equals(SchemaLanguageConstants.RELAXNG_NS_URI)) {
			return SchemaLanguageConstants.RELAXNG_NS_URI;
		}else{
			//... it may be a DTD or something completey other...
			//TODO report problem
		}
		return null;
	}
	
	private Object[] getParams(SAXParseException e) {
        return new Object[]{e.getMessage(), Integer.valueOf(e.getLineNumber()), Integer.valueOf(e.getColumnNumber()), e.getSystemId()};
    }

	private Object[] getParams(SAXParseException e, String replacementMessage) {
        return new Object[]{replacementMessage, Integer.valueOf(e.getLineNumber()), Integer.valueOf(e.getColumnNumber()), e.getSystemId()};
    }
	
	private Object[] getParams(FilesetFileException e) {
        return new Object[]{e.getOrigin().getName(), e.getCause().getMessage()};
    }
	
    private void checkSystemProperties() {
    	//only set if the system doesnt carry values already
    	String test = System.getProperty(
    			"javax.xml.validation.SchemaFactory:http://relaxng.org/ns/structure/1.0");
    	if(test==null){
    		System.setProperty(
				"javax.xml.validation.SchemaFactory:http://relaxng.org/ns/structure/1.0",
				"org.daisy.util.xml.validation.jaxp.RelaxNGSchemaFactory");
    	}
    	test = System.getProperty(
    			"javax.xml.validation.SchemaFactory:http://www.ascc.net/xml/schematron");
    	if(test==null){
    	System.setProperty(
				"javax.xml.validation.SchemaFactory:http://www.ascc.net/xml/schematron",
				"org.daisy.util.xml.validation.jaxp.SchematronSchemaFactory");
    	}
	}
	
	/*
	 *  (non-Javadoc)
	 *  Unused ContentHandler methods
	 */
	public void setDocumentLocator(Locator arg0) {}
	public void startDocument() throws SAXException {}
	public void endDocument() throws SAXException {}
	public void startPrefixMapping(String arg0, String arg1) throws SAXException {}
	public void endPrefixMapping(String arg0) throws SAXException {}
	public void endElement(String arg0, String arg1, String arg2) throws SAXException {}
	public void characters(char[] arg0, int arg1, int arg2) throws SAXException {}
	public void ignorableWhitespace(char[] arg0, int arg1, int arg2) throws SAXException {}
	public void processingInstruction(String arg0, String arg1) throws SAXException {}
	public void skippedEntity(String arg0) throws SAXException {}
}