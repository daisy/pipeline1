package int_daisy_opsCreator;

import int_daisy_opsCreator.metadata.MetadataItem;
import int_daisy_opsCreator.metadata.MetadataList;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.EFolder;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetErrorHandler;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.interfaces.ManifestFile;
import org.daisy.util.fileset.interfaces.xml.XmlFile;
import org.daisy.util.fileset.manipulation.FilesetFileManipulator;
import org.daisy.util.fileset.manipulation.FilesetManipulationException;
import org.daisy.util.fileset.manipulation.FilesetManipulator;
import org.daisy.util.fileset.manipulation.FilesetManipulatorListener;
import org.daisy.util.fileset.manipulation.manipulators.XMLEventConsumer;
import org.daisy.util.fileset.manipulation.manipulators.XMLEventExposer;
import org.daisy.util.location.LocationUtils;
import org.daisy.util.xml.IDGenerator;
import org.daisy.util.xml.pool.PoolException;
import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.daisy.util.xml.stax.ContextStack;

/**
 * Main transformer class. Create an OPS 2.0 publication from DTBook or XHTML input.
 * @author Markus Gylling
 */
public class OpsCreator extends Transformer implements FilesetErrorHandler, FilesetManipulatorListener, XMLEventConsumer {
	private List<URL> mInputDocuments = null;
	private List<Fileset> mInputFilesets = null;
	private List<Fileset> mOutputFilesets = null;
	private EFolder mOutputDir = null;
	private NcxBuilderConfiguration mNcxConfiguration = null;
	private IDGenerator mIdGenerator = null;
	private XMLEventFactory mXMLEventFactory = null;
	private Set<StartElement> mInputMetadata = null;
	private MetadataList mOpsMetaData = null;	
	private String mFirstXmlLangValue = null;
	private QName mXmlLangQName = null;
	
	private boolean mIteratorIsFirstManifest = false;
	private boolean mIteratorIsFirstFileset = true;
	
	static public final String OPF_NS = "http://www.idpf.org/2007/opf";
	static public final String DC_NS = "http://purl.org/dc/elements/1.1/"; 
	static public final String DC_PFX = "dc";
	public static final String APP_NAME = "Daisy Pipeline OPS Creator";
			
	public OpsCreator(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
		mIdGenerator = new IDGenerator("ops");
		mInputMetadata = new HashSet<StartElement>();
		mXmlLangQName = new QName("xml:lang");
	}

	@Override
	protected boolean execute(Map parameters) throws TransformerRunException {
		try{						
			mInputDocuments = getInputDocuments((String)parameters.remove("input"));
			mInputFilesets = getInputFilesets(mInputDocuments);		
			  
			URL ncxConfigURL = getNcxConfigURL(parameters);
			mNcxConfiguration = new NcxBuilderConfiguration(ncxConfigURL,this);
					
			mOutputDir = (EFolder)FileUtils.createDirectory(new EFolder(FilenameOrFileURI.toFile((String)parameters.remove("output"))));
			mOutputFilesets = new LinkedList<Fileset>();
			
			/*
			 * Copy over filesets to output dir, ensure
			 * that all content docs have IDs on NCX target positions
			 */
			try{
				mXMLEventFactory = StAXEventFactoryPool.getInstance().acquire();				
				for (Fileset fileset : mInputFilesets) {
					FilesetManipulator fm = new FilesetManipulator();
					fm.setInputFileset(fileset);
					fm.setOutputFolder(mOutputDir);
					fm.setFileTypeRestriction(XmlFile.class);
					fm.setListener(this);
					fm.iterate();
					mIteratorIsFirstFileset = false;
					URI uri = (new File(fm.getOutputFolder(),fm.getInputFileset().getManifestMember().getFile().getName())).toURI();
					mOutputFilesets.add(new FilesetImpl(uri,this,false,false));
				}
			}finally{
				StAXEventFactoryPool.getInstance().release(mXMLEventFactory);				
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

	private URL getNcxConfigURL(Map parameters) {
		String ncxConfigParam = (String)parameters.remove("ncxConfig");	
		if(ncxConfigParam!=null && ncxConfigParam.length()>0) {
			//user provided custom ncx config file
			URL url = LocationUtils.identifierToURL(ncxConfigParam);
			this.sendMessage(i18n("USING_NCX_CONFIG", url.getPath()), MessageEvent.Type.INFO, MessageEvent.Cause.SYSTEM);
			return url;
		}
		//use default
		this.sendMessage(i18n("USING_DEFAULT_NCX_CONFIG"), MessageEvent.Type.INFO, MessageEvent.Cause.SYSTEM);
		return this.getClass().getResource("ncx-config-default.xml");
	}

	/**
	 * Create the metadata object using input fileset(s) and/or inparams. 
	 * <p>If dupe, inparam wins.</p>
	 * @throws PoolException 
	 */
	private MetadataList getMetadata(Map parameters) throws PoolException {
		
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
				for (Iterator iter = se.getAttributes(); iter.hasNext();) {
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
			String test = (String)parameters.get("dc:title");
			if(test != null && test.length()> 0 ) {
				dcTitleValue = test;
			}						
			mOpsMetaData.add(new QName(DC_NS,"title",DC_PFX), dcTitleValue);
			
			//dc:creator
			test = (String)parameters.get("dc:creator");
			if(test != null && test.length()> 0 ) {
				dcCreatorValue = test;
			}						
			mOpsMetaData.add(new QName(DC_NS,"creator",DC_PFX), dcCreatorValue);
			
			//dc:publisher
			test = (String)parameters.get("dc:publisher");
			if(test != null && test.length()> 0 ) {
				dcPublisherValue = test;
			}						
			mOpsMetaData.add(new QName(DC_NS,"publisher",DC_PFX), dcPublisherValue);

			//dc:date event=publication
			test = (String)parameters.get("dc:date");
			if(test != null && test.length()> 0 ) {
				dcDateValue = test;
			}						
			QName q = new QName(DC_NS,"date",DC_PFX);
			MetadataItem m = new MetadataItem(q,dcDateValue);
			m.addAttribute(OPF_NS, "event","publication");
			mOpsMetaData.add(m);
			
			//dc:date event=creation (today)
			QName n = new QName(DC_NS,"date",DC_PFX);
			MetadataItem d = new MetadataItem(n,getCurrentDate());
			d.addAttribute(OPF_NS, "event","creation");
			mOpsMetaData.add(d);

			//dc:identifier
			test = (String)parameters.get("dc:identifier");
			if(test != null && test.length()> 0 ) {
				dcIdentifierValue = test;
			}					
			QName i = new QName(DC_NS,"identifier",DC_PFX);
			MetadataItem im = new MetadataItem(i,dcIdentifierValue);
			im.addAttribute("id","uid");
			mOpsMetaData.add(im);

			//dc:langauge
			test = (String)parameters.get("dc:language");
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
				throw new FilesetFatalException("Fileset " + fileset.getFilesetType().toNiceNameString() + " is not an allowed type.");
			}
			retList.add(fileset);
		}		
		return retList;
	}

	/**
	 * Build an ordered list of input documents, make sure all exist.
	 * @throws MalformedURLException
	 * @throws IOException  
	 */
	private List<URL> getInputDocuments(String inputParam) throws IOException, MalformedURLException {
		List<URL> retList = new LinkedList<URL>();
		String[] docs = inputParam.split(File.pathSeparator);
		for (String doc : docs) {
			URL url = LocationUtils.identifierToURL(doc);
			InputStream is = url.openStream();
			is.close();
			retList.add(url);
		}		
		return retList;
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.interfaces.FilesetErrorHandler#error(org.daisy.util.fileset.exception.FilesetFileException)
	 */
	public void error(FilesetFileException ffe) throws FilesetFileException {		
		this.sendMessage(ffe);
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.manipulation.FilesetManipulatorListener#nextFile(org.daisy.util.fileset.interfaces.FilesetFile)
	 */
	public FilesetFileManipulator nextFile(FilesetFile file) throws FilesetManipulationException {
		
		mIteratorIsFirstManifest = false;
		if(mIteratorIsFirstFileset && file instanceof ManifestFile) {
			mIteratorIsFirstManifest = true;
		}
		
		//restriction is set to only recieve XmlFile here 		
		try {
			XMLEventExposer xee = new XMLEventExposer (this,null,null,false,false,false);
			xee.setEventTypeRestriction(XMLEvent.START_ELEMENT);
			xee.setEventTypeRestriction(XMLEvent.ATTRIBUTE);
			return xee;
		} catch (Exception e) {
			throw new FilesetManipulationException(e.getMessage(),e);
		}					

	}

	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.manipulation.manipulators.XMLEventConsumer#nextEvent(javax.xml.stream.events.XMLEvent, org.daisy.util.xml.stax.ContextStack)
	 */
	public List<XMLEvent> nextEvent(XMLEvent event, ContextStack context) {
		/*
		 * If missing, add id attr to elements that match the ncx config.
		 * We need to deal with attributes included in the StartElement event from the exposer
		 * and discard the separate Attribute events
		 */			
		if(event.isAttribute()) return null;
		
		List<XMLEvent> list = new LinkedList<XMLEvent>();
		list.add(event);
		
		if(event.isStartElement()) {
			StartElement se = event.asStartElement();
			//check if this element needs an id attribute
			if(mNcxConfiguration.matchesNavMapFilter(se)||mNcxConfiguration.matchesNavListFilter(se)) {				
				if(!hasIdAttribute(se)) {
					list.add(mXMLEventFactory.createAttribute(new QName(se.getName().getNamespaceURI(),"id"), mIdGenerator.generateId()));
				}
			}
			if(mIteratorIsFirstManifest) {
				//collect meta elements
				String n = se.getName().getLocalPart(); 
				if(n.equals("meta")) {
					mInputMetadata.add(se);
				}
			}
			
			if(mFirstXmlLangValue==null){
				Attribute a = se.getAttributeByName(mXmlLangQName);
				if(a!=null){
					mFirstXmlLangValue = a.getValue();
				}
			}
		}
		
		return list;
		
	}

	private boolean hasIdAttribute(StartElement se) {
		for (Iterator iter = se.getAttributes(); iter.hasNext();) {
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

}
