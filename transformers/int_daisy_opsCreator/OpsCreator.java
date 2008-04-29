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
package int_daisy_opsCreator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.script.datatype.FilesDatatype;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.css.stylesheets.Css;
import org.daisy.util.dtb.meta.MetadataItem;
import org.daisy.util.dtb.meta.MetadataList;
import org.daisy.util.file.Directory;
import org.daisy.util.file.EFile;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.file.TempFile;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetErrorHandler;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.util.FilesetFileFilter;
import org.daisy.util.location.LocationUtils;
import org.daisy.util.xml.IDGenerator;
import org.daisy.util.xml.Namespaces;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.peek.PeekResult;
import org.daisy.util.xml.peek.Peeker;
import org.daisy.util.xml.peek.PeekerPool;
import org.daisy.util.xml.pool.PoolException;
import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.pool.StAXOutputFactoryPool;
import org.daisy.util.xml.stax.StaxEntityResolver;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.TransformerFactoryConstants;
import org.daisy.util.xml.xslt.XSLTException;
import org.daisy.util.xml.xslt.stylesheets.Stylesheets;
import org.xml.sax.SAXException;

/**
 * Main transformer class. Create an OPS 2.0 publication from DTBook or XHTML input.
 * @author Markus Gylling
 */
public class OpsCreator extends Transformer implements FilesetErrorHandler {
	private List<URL> mInputDocuments = null;
	private List<Fileset> mInputFilesets = null;
	private List<Fileset> mOutputFilesets = null;
	private Directory mOutputDir = null;
	private NcxBuilderConfiguration mNcxConfiguration = null;
	private IDGenerator mIdGenerator = null;
	private Set<StartElement> mInputMetadata = null;
	private MetadataList mOpsMetaData = null;	
	private String mFirstXmlLangValue = null;
	private QName mXmlLangQName = null;
		
	static public final String OPF_NS = "http://www.idpf.org/2007/opf";
	static public final String DC_NS = "http://purl.org/dc/elements/1.1/"; 
	static public final String DC_PFX = "dc";
	public static final String APP_NAME = "Daisy Pipeline EPUB Creator";
			
	public OpsCreator(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
		mIdGenerator = new IDGenerator("ops");
		mInputMetadata = new HashSet<StartElement>();
		mXmlLangQName = new QName("xml:lang");
	}
	
	@Override
	protected boolean execute(Map<String,String> parameters) throws TransformerRunException {
		try{
			
			/*
			 * Get a list of the plain unmodded input documents
			 */
			List<URL> mRawInputDocuments = parseInputParam(parameters.get("input"));
			
			/*
			 * Make sure input dir != output dir
			 */
			Directory outputDir = new Directory(FilenameOrFileURI.toFile(parameters.remove("output")));
			if(outputDir!=null && outputDir.exists()) {
				for(URL u : mRawInputDocuments ) {
					File f = new File(u.toURI());
					if(f.getParentFile().getCanonicalPath().equals(outputDir.getCanonicalPath())) {
						throw new TransformerRunException(i18n("INPUT_OUTPUT_SAME"));
					}
				}
			}
			
			/*
			 * Get a list of the final input documents; the return may have modded documenttypes,
			 * which means mInputDocuments URLs may be reffing user provided paths and/or temp paths.
			 */
			mInputDocuments = getInputDocuments(mRawInputDocuments);
			
			/*
			 * Get a list of the filesets of the unmodded input documents 
			 */
			mInputFilesets = getInputFilesets(mRawInputDocuments);		
			
			/*
			 * Prep output
			 */			
			mOutputDir = (Directory)FileUtils.createDirectory(outputDir);
			mOutputFilesets = new LinkedList<Fileset>();
			
			/*
			 * Get all satellite files of the input filesets over to output
			 */
			for(Fileset fileset: mInputFilesets) {
				final Fileset currentFileset = fileset;
				mOutputDir.addFileset(fileset, true, new FilesetFileFilter(){
					public short acceptFile(FilesetFile file) {						
						try {
							if(currentFileset.getManifestMember().getFile().getCanonicalPath()
									.equals(file.getFile().getCanonicalPath())) {
								return FilesetFileFilter.REJECT;
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						return FilesetFileFilter.ACCEPT;
					}					
				});
			}
			
			/*
			 * Set up the NCX configuration
			 */			
			URL ncxConfigURL = getNcxConfigURL(parameters);
			mNcxConfiguration = new NcxBuilderConfiguration(ncxConfigURL,this);
			
			/*
			 * Get the content docs (manifests) over to output,
			 * and create representations of the output filesets.
			 */
			boolean first = true;
			for(URL doc : mInputDocuments) {
				mOutputFilesets.add(pipe(doc,first));
				first = false;		
			}
																		
			/*
			 * Build the package file
			 */
			OpfBuilder opfBuilder = new OpfBuilder(mOutputFilesets,getMetadata(parameters));
			opfBuilder.build();
			opfBuilder.render(new File(mOutputDir,"package.opf"));
								
			/*
			 * build the ncx
			 */
			NcxBuilder ncxBuilder = new NcxBuilder(mOutputFilesets,getMetadata(parameters),mNcxConfiguration,this);
			ncxBuilder.build();
			ncxBuilder.render(new File(mOutputDir,"navigation.ncx"));
		
		}catch (Exception e) {
			this.sendMessage(i18n("ERROR_ABORTING", e.getMessage()), MessageEvent.Type.ERROR);
			throw new TransformerRunException(e.getMessage(),e);
		}
		return true;
	}

	private URL getNcxConfigURL(Map<String,String> parameters) {
		String ncxConfigParam = parameters.remove("ncxConfig");	
		if(ncxConfigParam!=null && ncxConfigParam.length()>0) {
			//user provided custom ncx config file
			URL url = LocationUtils.identifierToURL(ncxConfigParam);
			this.sendMessage(i18n("USING_NCX_CONFIG", url.getPath()), MessageEvent.Type.INFO, MessageEvent.Cause.SYSTEM);
			return url;
		}
		//use default
		this.sendMessage(i18n("USING_DEFAULT_NCX_CONFIG"), MessageEvent.Type.INFO_FINER, MessageEvent.Cause.SYSTEM);
		return this.getClass().getResource("ncx-config-default.xml");
	}

	/**
	 * Create a Fileset instance for each input document
	 * @throws URISyntaxException 
	 * @throws FilesetFatalException 
	 */
	private List<Fileset> getInputFilesets(List<URL> inputDocuments) throws FilesetFatalException, URISyntaxException {		
		List<Fileset> retList = new LinkedList<Fileset>();
		for (URL url : inputDocuments) {
			Fileset fileset = new FilesetImpl(url.toURI(),this);
			if(fileset.getFilesetType() != FilesetType.DTBOOK_DOCUMENT &&
					fileset.getFilesetType() != FilesetType.XHTML_DOCUMENT) {
				throw new FilesetFatalException(i18n("DISALLOWED_DOCUMENT_TYPE",fileset.getFilesetType().toNiceNameString()));
			}
			retList.add(fileset);
		}		
		return retList;
	}

	/**
	 * Build an ordered list of input documents using the given transformer inparameter, make sure all exist.
	 * @throws FileNotFoundException 
	 */
	private List<URL> parseInputParam(String inputParam) throws FileNotFoundException {
		List<URL> retList = new LinkedList<URL>();
		String[] docs = inputParam.split(FilesDatatype.SEPARATOR_STRING);
		for (String doc : docs) {
			URL u = LocationUtils.identifierToURL(doc);
			if(u!=null) {
				retList.add(LocationUtils.identifierToURL(doc));
			}else {
				throw new FileNotFoundException(doc);
			}
		}		
		return retList;
	}
	
	/**
	 * Build an ordered list of input documents, make sure all exist. Check input versions, and transform document type if necessary/expected
	 * @throws MalformedURLException
	 * @throws IOException  
	 * @throws SAXException 
	 * @throws TransformerRunException 
	 */
	private List<URL> getInputDocuments(List<URL> unModdedDocs) throws IOException, MalformedURLException, SAXException, TransformerRunException {
		List<URL> retList = new ArrayList<URL>();
		for (URL doc : unModdedDocs) {			
			URL url = checkValidType(doc);
			retList.add(url);
		}		
		return retList;
	}

	/**
	 * Check if the inparam URL is of a type/version allowed By OPS. If not, transform into a valid type/version and return. 
	 */
	private URL checkValidType(URL url) throws SAXException, IOException, TransformerRunException {
		Peeker peeker = null;
		try{
			peeker = PeekerPool.getInstance().acquire();
			PeekResult result = peeker.peek(url);		
			if(!result.getRootElementLocalName().matches("html|dtbook")) {
				throw new TransformerRunException(i18n("DISALLOWED_DOCUMENT_TYPE",result.getRootElementLocalName()));
			}
			
			String token = getVersionToken(result);
			
			if(result.getRootElementLocalName().equals("dtbook")) {								
				if(token!=null) {					
					if(!(token.contains("2005-2"))) {
						this.sendMessage(i18n("DISALLOWED_DOCUMENT_VERSION",result.getRootElementLocalName(),token),
								MessageEvent.Type.WARNING, MessageEvent.Cause.INPUT);
						//If !2005-2, do a dynamic transform to 1.1
						try{
							URL out = createXHTML11(url, result);
							if(out!=null) {
								this.sendMessage(i18n("CONVERTED_TO_XHTML11"), MessageEvent.Type.WARNING, MessageEvent.Cause.INPUT);
								url = out;
							}
						}catch (Exception e) {
							//failure, leave inparam URL
							this.sendMessage(i18n("ERROR",e.getMessage()), MessageEvent.Type.ERROR, MessageEvent.Cause.SYSTEM);
						}
					}
				}else{
					this.sendMessage(i18n("DISALLOWED_DOCUMENT_VERSION",result.getRootElementLocalName(),"unknown"), 
								MessageEvent.Type.WARNING, MessageEvent.Cause.INPUT);
				}
			}
			
			else if(result.getRootElementLocalName().equals("html")) {
				if(token!=null) {					
					if(!(token.contains("XHTML 1.1") || token.contains("xhtml11.dtd"))) {
						this.sendMessage(i18n("DISALLOWED_DOCUMENT_VERSION",result.getRootElementLocalName(),token), 
								MessageEvent.Type.WARNING, MessageEvent.Cause.INPUT);
						//If we have Xhtml 1.0, do a dynamic transform to 1.1
						try{
							URL out = createXHTML11(url, result);
							if(out!=null) {
								this.sendMessage(i18n("CONVERTED_TO_XHTML11"), MessageEvent.Type.WARNING, MessageEvent.Cause.INPUT);
								url = out;
							}
						}catch (Exception e) {
							//failure, leave inparam URL
							this.sendMessage(i18n("ERROR",e.getMessage()), MessageEvent.Type.ERROR, MessageEvent.Cause.SYSTEM);
						}
						
					}
				}else{
					this.sendMessage(i18n("DISALLOWED_DOCUMENT_VERSION",result.getRootElementLocalName(),"unknown"), 
								MessageEvent.Type.WARNING, MessageEvent.Cause.INPUT);
				}
			}
		}finally{
			PeekerPool.getInstance().release(peeker);
		}
		return url;
	}

	/**
	 * Transform an incoming DTBook 2005-x or XHTML 1.0 doc into XHTML 1.1
	 * @throws IOException 
	 * @throws XSLTException 
	 * @throws URISyntaxException 
	 */
	private URL createXHTML11(URL in, PeekResult result) throws IOException, XSLTException, URISyntaxException {
		
		URL url = in;
		if(result.getRootElementLocalName() == "dtbook") {
			//transform DTBook to XHTML 1.0		
			
			InputStream iis = in.openStream();
			StreamSource iss = new StreamSource(iis);
			iss.setSystemId(new File(in.toURI()).getAbsolutePath());
						
			URL xsl = Stylesheets.get("dtbook2xhtml.xsl");
			InputStream xslis = xsl.openStream();
			StreamSource xslss = new StreamSource(xslis);
			
			File doc = TempFile.create();
			
			try {
				Stylesheet.apply(iss,xslss,new StreamResult(doc),TransformerFactoryConstants.SAXON8,null,null);
			}finally{
				iis.close();
				xslis.close();
			}
			url = doc.toURI().toURL();
		}
		
		//transform XHTML 1.0 to 1.1
		
		InputStream iis = url.openStream();
		StreamSource iss = new StreamSource(iis);
		iss.setSystemId(new File(url.toURI()).getAbsolutePath());
		URL xsl = Stylesheets.get("xhtml10toXhtml11.xsl");
		InputStream xslis = xsl.openStream();
		StreamSource xslss = new StreamSource(xslis);		
		File out = TempFile.create();
		try{
			Stylesheet.apply(iss,xslss,new StreamResult(out),TransformerFactoryConstants.SAXON8,null,null);
		}finally{
			iis.close();
			xslis.close();
		}
		
		//give the result an appropriate name
		EFile efile = new EFile(in.toURI());
		Directory tempDir = new Directory(out.getParentFile());
		FileInputStream fis = new FileInputStream(out);
		File out2 = tempDir.writeToFile(efile.getNameMinusExtension()+".html", fis);
		fis.close();

		return out2.toURI().toURL();
	}

	private String getVersionToken(PeekResult result) {		 
		String token = result.getPrologPublicId();
		if(token==null) {
			token = result.getPrologSystemId();
			if(token==null) {
				String name = null;
				for (int i = 0; i < result.getRootElementAttributes().getLength(); i++) {
					name = result.getRootElementAttributes().getLocalName(i);
					if(name.equals("version")) {
						token = result.getRootElementAttributes().getValue(i);
					}
				}
			}
		}	
		return token;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.interfaces.FilesetErrorHandler#error(org.daisy.util.fileset.exception.FilesetFileException)
	 */
	public void error(FilesetFileException ffe) throws FilesetFileException {		
		this.sendMessage(ffe);
	}

	/**
	 * Copy over a doc to output dir, ensure
	 * that manifest member has IDs on NCX target positions. The copied docs
	 * satellite files are expected to already be in output dir
	 * Add CSS if missing.
	 * @param fileset Fileset to copy
	 * @param first Whether this is the first call to this method (in case of several input filesets)
	 * @return the output fileset
	 * @throws IOException 
	 * @throws FilesetFatalException 
	 * @throws XMLStreamException 
	 * @throws URISyntaxException 
	 */
	private Fileset pipe(URL inputManifestURL, boolean first) throws IOException, FilesetFatalException, XMLStreamException, URISyntaxException {		
		
		String fileName = new File(inputManifestURL.toURI()).getName();		
		File outputManifestFile = new File(mOutputDir,fileName);
				
		XMLEventFactory xef = null;
		XMLInputFactory xif = null;
		Map<String, Object> xifProperties = null;
		XMLOutputFactory xof = null;
		Map<String, Object> xofProperties = null;
		InputStream is = null;
		FileOutputStream fos = null;
		XMLEventWriter xew = null;
		
		try{
			xef = StAXEventFactoryPool.getInstance().acquire();
			xifProperties = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(false);
			xifProperties.put(XMLInputFactory.SUPPORT_DTD, Boolean.TRUE);
			xif = StAXInputFactoryPool.getInstance().acquire(xifProperties);
			try {
				xif.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
			} catch (CatalogExceptionNotRecoverable e1) {
				e1.printStackTrace();
			}
			xofProperties = StAXOutputFactoryPool.getInstance().getDefaultPropertyMap();			
			xof = StAXOutputFactoryPool.getInstance().acquire(xofProperties);
			is = inputManifestURL.openStream();			
			XMLEventReader xer = xif.createXMLEventReader(is);			
			fos = new FileOutputStream(outputManifestFile);
			boolean seenStylesheetInstruction = false;
			boolean passedFirstElement = false;
			boolean seenXhtmlCssLink = false;
			QName xhtHead = new QName(Namespaces.XHTML_10_NS_URI,"head");
			while(xer.hasNext()) {
				XMLEvent xe = xer.nextEvent();
				if(xe.getEventType() == XMLEvent.START_DOCUMENT) {
					StartDocument sd = (StartDocument) xe; 
					String enc = sd.getCharacterEncodingScheme();
					if(enc==null||enc.equals(""))enc="utf-8";
					xew = xof.createXMLEventWriter(fos,enc);
					xew.add(xe);
				}else if(xe.getEventType() == XMLEvent.PROCESSING_INSTRUCTION) {
					ProcessingInstruction pi = (ProcessingInstruction) xe;
					if(pi.getTarget().equals("xml-stylesheet")) {
						seenStylesheetInstruction = true;
					}
					xew.add(pi);
				}else if(xe.isEndElement()) {
					if(xe.asEndElement().getName().equals(xhtHead) && !seenXhtmlCssLink) {
						try{
							URL cssURL = Css.get(Css.DocumentType.D202_XHTML);
							String cssLocalName = cssURL.toString().substring(cssURL.toString().lastIndexOf('/')+1);
							mOutputDir.writeToFile(cssLocalName, cssURL.openStream());
							Set<Attribute> attrs = new HashSet<Attribute>();
							attrs.add(xef.createAttribute(new QName("type"), "text/css"));
							attrs.add(xef.createAttribute(new QName("rel"), "stylesheet"));
							attrs.add(xef.createAttribute(new QName("href"), cssLocalName));
							QName link = new QName(xe.asEndElement().getName().getNamespaceURI(),"link");
							xew.add(xef.createStartElement(link, attrs.iterator(), xe.asEndElement().getNamespaces()));
							xew.add(xef.createEndElement(link, xe.asEndElement().getNamespaces()));
							this.sendMessage(i18n("ADDED_CSS",  fileName), 
									MessageEvent.Type.INFO, MessageEvent.Cause.SYSTEM);
						}catch (Exception e) {
							e.printStackTrace();
						}
					}					
					xew.add(xe);
				}else if(xe.isStartElement()) {
					StartElement se = xe.asStartElement();
					if(!passedFirstElement) {
						xew.setDefaultNamespace(se.getName().getNamespaceURI());
					}
					if(se.getName().getLocalPart() == "dtbook" 
							&& !passedFirstElement 
								&& !seenStylesheetInstruction) {						
						try{
							URL cssURL = Css.get(Css.DocumentType.Z3986_DTBOOK);
							String cssLocalName = cssURL.toString().substring(cssURL.toString().lastIndexOf('/')+1);
							mOutputDir.writeToFile(cssLocalName, cssURL.openStream());
							xew.add(xef.createProcessingInstruction
									("xml-stylesheet", "href='" +cssLocalName +"' type='text/css'")
							);
							this.sendMessage(i18n("ADDED_CSS",  fileName), 
									MessageEvent.Type.INFO, MessageEvent.Cause.SYSTEM);
						}catch (Exception e) {
							e.printStackTrace();
						}
					} else if (se.getName().equals(new QName(Namespaces.XHTML_10_NS_URI,"link")) && !seenXhtmlCssLink) {
						seenXhtmlCssLink = isXhtmlCssLink(se);
					}
					
					xew.add(xe);
					
					//check if this element needs an id attribute
					if(mNcxConfiguration.matchesNavMapFilter(se)||mNcxConfiguration.matchesNavListFilter(se)) {				
						if(!hasIdAttribute(se)) {							
							xew.add(xef.createAttribute("id", mIdGenerator.generateId()));
						}
					}
					
					if(first) {
						//collect meta elements 
						if(se.getName().getLocalPart().equals("meta")) {
							mInputMetadata.add(se);
						}
					}
					
					if(mFirstXmlLangValue==null){
						Attribute a = se.getAttributeByName(mXmlLangQName);
						if(a!=null){
							mFirstXmlLangValue = a.getValue();
						}
					}
					passedFirstElement = true;					
				}else{
					xew.add(xe);					
				}

			}
		}finally{
			xew.flush();
			xew.close();
			if(is!=null)is.close();
			if(fos!=null)fos.close();
			StAXEventFactoryPool.getInstance().release(xef);	
			StAXInputFactoryPool.getInstance().release(xif,xifProperties);
			StAXOutputFactoryPool.getInstance().release(xof,xofProperties);
			
		}		
		return new FilesetImpl(outputManifestFile.toURI(),this,false,false);
	}
	

	private boolean isXhtmlCssLink(StartElement se) {
		Iterator<?> attrs = se.getAttributes();
		boolean css = false;
		while(attrs.hasNext()) {
			Attribute a = (Attribute) attrs.next();
			if(a.getName().getLocalPart()=="type" && a.getValue().toLowerCase().equals("text/css")) css = true;
			else if (a.getName().getLocalPart()=="rel" && a.getValue().toLowerCase().equals("stylesheet")) css = true;
		}
		return css;
	}

	private boolean hasIdAttribute(StartElement se) {
		for (Iterator<?> iter = se.getAttributes(); iter.hasNext();) {
			Attribute a = (Attribute)iter.next();		
			if(a.getName().getLocalPart().equals("id")) {
				return true;
			}
		}
		return false;
	}
	
	private String getCurrentDate() {	
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(new java.util.Date());
	}

	/**
	 * Create the metadata object using input fileset(s) and/or inparams. 
	 * <p>If dupe, inparam wins.</p>
	 * @throws PoolException 
	 */
	private MetadataList getMetadata(Map<String, String> parameters) throws PoolException {
		
		/*
		 * At the time of writing, supported metadata is 
		 * dc:title (OPS required)
		 * dc:creator
		 * dc:publisher
		 * dc:date event=publication
		 * dc:date event=creation (auto-generated)
		 * dc:identifier (OPS required)
		 * dc:language (OPS required)
		 * 
		 * Any occurences of these meta elements in the first input 
		 * document will be included in the OPF.
		 * 
		 * Values supplied as inparameters to the transformer take 
		 * precedence over any occurences in the document.   
		 */
	
		if(mOpsMetaData==null){						
			mOpsMetaData = new MetadataList();			
			String dcTitleValue = null;
			String dcCreatorValue = null;
			String dcPublisherValue = null;
			String dcDateValue = null;
			String dcIdentifierValue = null;
			String dcLanguageValue = null;
			
			
			for(StartElement se : mInputMetadata) {
				//a set of <meta/> elements				
				for (Iterator<?> iter = se.getAttributes(); iter.hasNext();) {
					Attribute a = (Attribute) iter.next();
					String name = a.getName().getLocalPart().toLowerCase();
					if(name.equals("name")){
						QName q = new QName(a.getName().getNamespaceURI(),"content");
						Attribute b = se.getAttributeByName(q);
						if(b!=null){
							String value = a.getValue().toLowerCase().intern();
							if(value == "dc:title") {
								dcTitleValue = b.getValue();
							}else if(value == "dc:creator") {
								dcCreatorValue = b.getValue();
							}else if(value == "dc:publisher") {
								dcPublisherValue = b.getValue();
							}else if(value == "dc:date") {
								dcDateValue = b.getValue();
							}else if(value == "dc:identifier" || value == "dtb:uid" ) {
								dcIdentifierValue = b.getValue();
							}else if(value == "dc:language") {
								dcLanguageValue = b.getValue();
							}	
						}
					}
				}				 
			}
			
			//dc:title
			String test = parameters.get("dc:title");
			if(test != null && test.length()> 0 ) {
				dcTitleValue = test;
			}						
			mOpsMetaData.add(new QName(DC_NS,"title",DC_PFX), dcTitleValue);
			
			//dc:creator
			test = parameters.get("dc:creator");
			if(test != null && test.length()> 0 ) {
				dcCreatorValue = test;
			}						
			mOpsMetaData.add(new QName(DC_NS,"creator",DC_PFX), dcCreatorValue);
			
			//dc:publisher
			test = parameters.get("dc:publisher");
			if(test != null && test.length()> 0 ) {
				dcPublisherValue = test;
			}						
			mOpsMetaData.add(new QName(DC_NS,"publisher",DC_PFX), dcPublisherValue);
	
			//dc:date event=publication
			QName event = new QName(OPF_NS,"event","opf");
			
			test = parameters.get("dc:date");
			if(test != null && test.length()> 0 ) {
				dcDateValue = test;
			}						
			QName q = new QName(DC_NS,"date",DC_PFX);			
			MetadataItem m = new MetadataItem(q,dcDateValue);
			//m.addAttribute(OPF_NS, "event","publication");
			m.addAttribute(event,"publication");
			mOpsMetaData.add(m);
			
			//dc:date event=creation (today)
			QName n = new QName(DC_NS,"date",DC_PFX);
			MetadataItem d = new MetadataItem(n,getCurrentDate());
			//d.addAttribute(OPF_NS, "event","creation");
			d.addAttribute(event,"creation");
			mOpsMetaData.add(d);
	
			//dc:identifier
			test = parameters.get("dc:identifier");
			if(test != null && test.length()> 0 ) {
				dcIdentifierValue = test;
			}					
			QName i = new QName(DC_NS,"identifier",DC_PFX);
			MetadataItem im = new MetadataItem(i,dcIdentifierValue);
			im.addAttribute("id","uid");
			mOpsMetaData.add(im);
	
			//dc:langauge
			test = parameters.get("dc:language");
			if(test != null && test.length()> 0 ) {
				dcLanguageValue = test;				
			}									
			if(dcLanguageValue == null || dcLanguageValue.length()==0 ) {
				dcLanguageValue = mFirstXmlLangValue;				
			}
			mOpsMetaData.add(new QName(DC_NS,"language",DC_PFX), dcLanguageValue);
			
			
		} // if mOpsMetaData == null
		return mOpsMetaData;
	}

}