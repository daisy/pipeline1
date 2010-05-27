package us_rfbd_textOnlyDtbCreator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.dtb.build.BuildException;
import org.daisy.util.dtb.build.NavigationItem;
import org.daisy.util.dtb.build.NavigationItemPage;
import org.daisy.util.dtb.build.NavigationItemPoint;
import org.daisy.util.dtb.build.NavigationLabel;
import org.daisy.util.dtb.build.NcxBuilder;
import org.daisy.util.dtb.build.OpfBuilder;
import org.daisy.util.dtb.build.SmilBuilder;
import org.daisy.util.dtb.build.SmilStructure;
import org.daisy.util.dtb.build.SmilStructureContainer;
import org.daisy.util.dtb.build.SmilStructureText;
import org.daisy.util.dtb.meta.MetadataItem;
import org.daisy.util.dtb.meta.MetadataList;
import org.daisy.util.file.EFile;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.ImageFile;
import org.daisy.util.fileset.SmilFile;
import org.daisy.util.fileset.TextualContentFile;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.util.DefaultFilesetErrorHandlerImpl;
import org.daisy.util.xml.IDGenerator;
import org.daisy.util.xml.Namespaces;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.stax.StaxEntityResolver;
import org.daisy.util.xml.stax.StaxFilter;

/**
 * TextOnlyDtbCreator:  Creates a Z39.86-2005 text-only fileset from an input dtbook file
 * @author jpritchett@rfbd.org
 * @version 0.9 (beta)
 * 27 May 2010
 *   - Can now identify synch points via an attribute (e.g., as provided by int_daisy_mixedContentNormalizer)
 * 24 December 2009 (Ho Ho Ho!)
 * 
 * This transformer just collects data for the "Builder" classes in
 * org.daisy.util.dtb.build, allowing them to do all the actual XML rendering.
 * 
 * All the work of this transformer is done in two Stax parses of the dtbook source.
 * It does one parse using a StaxFilter to collect all the SMIL synchronization
 * points, navigation points, navigation labels, and to add id and smilRef attributes
 * as needed in the dtbook file.  The second parse is done to handle any SMIL
 * links that are required (since links can be backward-pointing).
 */
// TODO:  Add MathML support
public class TextOnlyDtbCreator extends Transformer {

	private static final String version = "0.9 (beta)";
	
	private File inputFile;
	
	// Stuff needed to create SMIL files
    // The number of events at which we start a new SMIL file
    private long smilEventThreshold;
    
    // The list of SmilBuilders, one per file
	private ArrayList<SmilBuilder> smilBuilders;
	
	// These are lists of the elements that can be synchronizable, skippable, escapable, 
	// and represented as containers
	private ArrayList<QName> synchItems;
	private QName synchAttr;		// Alternatively, we can name an attribute that identifies synch items
	private ArrayList<QName> skippableItems;
	private ArrayList<QName> escapableItems;
    private ArrayList<QName> containerItems;
    
    // These maps are used to keep track of links
    private HashMap<String,List<SmilStructure>> smilLinks;
    private HashMap<String,String> linkFiles;
    

	// Stuff needed to create NCX
    
    // A list of element names that create navigation items
	private ArrayList<QName> navItems;
	
	// The navMap and pageList and its depth
    private ArrayList<NavigationItemPoint> ncxMap;
    private int ncxMapDepth;
    
    // The pageList and the maximum page value it contains
    private ArrayList<NavigationItemPage> ncxPages;
    private long maxPageValue;
    
    // Required labels for title and author(s)
    private NavigationLabel ncxTitle;
    private ArrayList<NavigationLabel> ncxAuthors;
    
    // Stuff needed to create OPF
    private MetadataList opfMetadata;
    private String uid;
    
	public TextOnlyDtbCreator(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
		
		// Instantiate and initialize everything
		smilEventThreshold = 500;		// Default value
		ncxMap = new ArrayList<NavigationItemPoint>();
		ncxMapDepth = 0;
		maxPageValue = 0;
		ncxPages = new ArrayList<NavigationItemPage>();
		ncxAuthors = new ArrayList<NavigationLabel>();
		smilBuilders = new ArrayList<SmilBuilder>();
		opfMetadata = new MetadataList();
		smilLinks = new HashMap<String,List<SmilStructure>>();
		linkFiles = new HashMap<String,String>();
		
		// Set up the list of elements that generate SMIL structures
		// These must contain the linking elements a, noteref, and annoref
		// Rest will come from config file
		synchItems = new ArrayList<QName>();
		synchItems.add(new QName(Namespaces.Z2005_DTBOOK_NS_URI, "a"));
		synchItems.add(new QName(Namespaces.Z2005_DTBOOK_NS_URI, "noteref"));
		synchItems.add(new QName(Namespaces.Z2005_DTBOOK_NS_URI, "annoref"));

		// Set up the list of elements that must be represented as SMIL containers
		containerItems = new ArrayList<QName>();
		containerItems.add(new QName(Namespaces.Z2005_DTBOOK_NS_URI, "table"));
		containerItems.add(new QName(Namespaces.Z2005_DTBOOK_NS_URI, "list"));
		containerItems.add(new QName(Namespaces.Z2005_DTBOOK_NS_URI, "note"));
		containerItems.add(new QName(Namespaces.Z2005_DTBOOK_NS_URI, "annotation"));
		containerItems.add(new QName(Namespaces.Z2005_DTBOOK_NS_URI, "sidebar"));
		containerItems.add(new QName(Namespaces.Z2005_DTBOOK_NS_URI, "prodnote"));

		// Set up list of standard navigation items (headings and page numbers)
		// TODO Add custom items for navList/navTarget
		navItems = new ArrayList<QName>();
		navItems.add(new QName(Namespaces.Z2005_DTBOOK_NS_URI, "pagenum"));
		navItems.add(new QName(Namespaces.Z2005_DTBOOK_NS_URI, "h1"));
		navItems.add(new QName(Namespaces.Z2005_DTBOOK_NS_URI, "h2"));
		navItems.add(new QName(Namespaces.Z2005_DTBOOK_NS_URI, "h3"));
		navItems.add(new QName(Namespaces.Z2005_DTBOOK_NS_URI, "h4"));
		navItems.add(new QName(Namespaces.Z2005_DTBOOK_NS_URI, "h5"));
		navItems.add(new QName(Namespaces.Z2005_DTBOOK_NS_URI, "h6"));
		
		// Set up list of standard skippable items (from Z39.86-2005 spec)
		// Additional entries can be added via config file
		skippableItems = new ArrayList<QName>();
		skippableItems.add(new QName(Namespaces.Z2005_DTBOOK_NS_URI, "pagenum"));
		skippableItems.add(new QName(Namespaces.Z2005_DTBOOK_NS_URI, "noteref"));
		skippableItems.add(new QName(Namespaces.Z2005_DTBOOK_NS_URI, "linenum"));
		skippableItems.add(new QName(Namespaces.Z2005_DTBOOK_NS_URI, "note"));
		skippableItems.add(new QName(Namespaces.Z2005_DTBOOK_NS_URI, "annotation"));
		skippableItems.add(new QName(Namespaces.Z2005_DTBOOK_NS_URI, "sidebar"));
		skippableItems.add(new QName(Namespaces.Z2005_DTBOOK_NS_URI, "prodnote"));

		// Set up list of standard escapable items (from Z39.86-2005 spec)
		// Additional entries can be added via config file
		escapableItems = new ArrayList<QName>();
		escapableItems.add(new QName(Namespaces.Z2005_DTBOOK_NS_URI, "table"));
		escapableItems.add(new QName(Namespaces.Z2005_DTBOOK_NS_URI, "list"));
		escapableItems.add(new QName(Namespaces.Z2005_DTBOOK_NS_URI, "note"));
		escapableItems.add(new QName(Namespaces.Z2005_DTBOOK_NS_URI, "annotation"));
		escapableItems.add(new QName(Namespaces.Z2005_DTBOOK_NS_URI, "prodnote"));
		escapableItems.add(new QName(Namespaces.Z2005_DTBOOK_NS_URI, "sidebar"));
}

	@Override
	protected boolean execute(Map<String, String> parameters)
			throws TransformerRunException {
		
		// Set up the Stax input factory
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
        factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.TRUE);
        try {
			factory.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
		} catch (CatalogExceptionNotRecoverable e2) {
			throw new TransformerRunException("Error setting XMLResolver", e2);
		}
        factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.FALSE);        
        
	// Read all the parameters and configurations
		// input = Input dtbook file path
		String inputPath = parameters.get("input");
		inputFile = new File(inputPath);
		this.sendMessage("Input dtbook file = " + inputPath);
		String nameroot = new EFile(inputFile).getNameMinusExtension();
		
		// output = Path where output goes
		String outputPath = parameters.get("output");
		File outputDir = new File(outputPath);
		if (!outputDir.exists() && !outputDir.mkdir()) {
			throw new TransformerRunException("Can't create output directory");
		}
		this.sendMessage("Output path = " + outputPath);
		
		// resourceFile = The Z39.86 resource file to use (default one is in transformer directory)
		String resourceFilePath = parameters.get("resourceFile");
		this.sendMessage("Resource file = " + resourceFilePath);
		
        // configFile = The extended configuration file (default one is in transformer directory)
		String configFilename = parameters.get("configFile");
		File configFile = new File(configFilename);
		this.sendMessage("Configuration file = " + configFilename);
		
		// Open the config file and parse it
		this.sendMessage("Reading config file");
        XMLEventReader xer = null;
        try {
			xer = factory.createXMLEventReader(new FileInputStream(configFile));
		} catch (FileNotFoundException e) {
			throw new TransformerRunException("Config file not found:  " + inputFile.getAbsolutePath());
		} catch (XMLStreamException e) {
			throw new TransformerRunException("Error opening config stream", e);
		}
		while (xer.hasNext()) {
			XMLEvent e;
			try {
				e = xer.nextEvent();
			} catch (XMLStreamException e1) {
				throw new TransformerRunException("Exception reading config stream", e1);
			}
			if (e.isStartElement()) {
				StartElement se = e.asStartElement();
				String name = se.getName().getLocalPart();
				// <synchElement name="foo" ns="http://www.example.com/ns/" />
				// Element name and (optional) namespace that generates a synch event (SMIL structure)
				if (name.equals("synchElement")) {
					parseConfigElement(se, synchItems);
				}
				// <synchAttribute name="foo" ns="http://www.example.com/ns/" />
				// Attribute name and (optional) namespace that generates a synch event (SMIL structure)
				else if (name.equals("synchAttribute")) {
					synchAttr = parseConfigElement(se, null);
				}
				// <skippableElement name="foo" ns="http://www.example.com/ns/" />
				// Element name and (optional) namespace that generates a skippable structure in SMIL
				else if (name.equals("skippableElement")) {
					QName itemName = parseConfigElement(se, skippableItems);
					// Skippable items not usually synched will be treated as containers
					if (!synchItems.contains(itemName)) {
						containerItems.add(itemName);
					}
				}
				// <escapableElement name="foo" ns="http://www.example.com/ns/" />
				// Element name and (optional) namespace that generates a skippable structure in SMIL
				else if (name.equals("escapableElement")) {
					QName itemName = parseConfigElement(se, escapableItems);
					// Escapable items not usually synched will be treated as containers
					if (!synchItems.contains(itemName)) {
						containerItems.add(itemName);
					}
				}
				// <smilEventThreshold value="500" />
				// Number of events per SMIL file before looking for an exit (defaults to 500)
				else if (name.equals("smilEventThreshold")) {
					Attribute valueAtt = se.getAttributeByName(new QName("value"));
					if (valueAtt != null) {
						smilEventThreshold = Long.parseLong(valueAtt.getValue());
					}
				}
			}
		}
		try {
			xer.close();
		} catch (XMLStreamException e4) {
			throw new TransformerRunException("Error closing config file", e4);
		}
		
	// Pass the input through a StaxFilter to collect data and add @smilRef's and @id's where needed
		this.sendMessage("Filtering input file to collect data, and add @id's and @smilRef's");
        // Open input text in reader
        try {
			xer = factory.createXMLEventReader(new FileInputStream(inputFile));
		} catch (FileNotFoundException e) {
			throw new TransformerRunException("Input file not found:  " + inputFile.getAbsolutePath());
		} catch (XMLStreamException e) {
			throw new TransformerRunException("Error opening input stream", e);
		}

		// Create the output text file
		OutputStream outStream = null;
		try {
			outStream = new FileOutputStream(outputDir.getAbsolutePath() + File.separator + inputFile.getName());
		} catch (FileNotFoundException e) {
			throw new TransformerRunException("Can't create output file: " + outputDir.getAbsolutePath() + File.separator + inputFile.getName());
		}
		
		// Parse text using the SmilrefAdder (see below).  This does all the heavy lifting of the transformer
		SmilrefAdder sra = null;
		try {
			sra = new SmilrefAdder(xer,outStream);
			sra.filter();
			sra.close();
		} catch (XMLStreamException e) {
			throw new TransformerRunException("Can't parse input", e);
		} catch (IOException e) {
			throw new TransformerRunException("IO exception parsing input", e);
		}
		
		// If we found anything that needs SMIL links, we have to do another pass:
		// Parse the output dtbook file and use the @smilref's to fill in all the link targets
		if (smilLinks.values().size() > 0) {
			this.sendMessage("Handling SMIL links");
	        try {
				xer = factory.createXMLEventReader(new FileInputStream(outputDir.getAbsolutePath() + File.separator + inputFile.getName()));
			} catch (FileNotFoundException e) {
				throw new TransformerRunException("Modified dtbook file not found:  " + inputFile.getAbsolutePath());
			} catch (XMLStreamException e) {
				throw new TransformerRunException("Error opening modified dtbook stream", e);
			}
			
			String targetNeedingSmilref = null;
			
			while (xer.hasNext()) {
				XMLEvent e;
				try {
					e = xer.nextEvent();
				} catch (XMLStreamException e1) {
					throw new TransformerRunException("Exception reading modified dtbook stream", e1);
				}
				if (e.isStartElement()) {
					
					// If this has an @id, see if it is the target of a link
					// If so, build the target URI and remember it (since this element itself may not have a @smilref)
					Attribute idAtt = e.asStartElement().getAttributeByName(new QName("id"));
					if (idAtt != null) {
						if (smilLinks.containsKey("#" + idAtt.getValue())) {
							targetNeedingSmilref = "#" + idAtt.getValue();
						}
					}
					
					// If this has a @smilref, and if we're looking to resolve a link, use @smilref as the 
					// target of the SMIL link(s).  Note that multiple links can point to the same target.
					Attribute smilrefAtt = e.asStartElement().getAttributeByName(new QName("smilref"));
					if (smilrefAtt != null && targetNeedingSmilref != null) {
						String smilref = smilrefAtt.getValue();
						List<SmilStructure> ssList = smilLinks.get(targetNeedingSmilref);
						for (SmilStructure ss : ssList) {
							String smilrefFile = smilref.substring(0, smilref.indexOf("#"));
							// If the SMIL file of the target is same as of the link, all you need is the fragment ID
							if (smilrefFile.equals(linkFiles.get(ss.getSmilID()))) {
								ss.setLinkTarget(smilref.substring(smilref.indexOf("#")));
							}
							// Otherwise, you need the whole thing
							else {
								ss.setLinkTarget(smilref);
							}
						}
						smilLinks.remove(targetNeedingSmilref);
						targetNeedingSmilref = null;
					}
				}
			}
			try {
				xer.close();
			} catch (XMLStreamException e) {
				throw new TransformerRunException("Error closing modified dtbook file", e);
			}
			
			// smilLinks should be empty now; otherwise an error
			if (smilLinks.values().size() != 0) {
				throw new TransformerRunException("Unresolvable links found");
			}
		}
		
		// Now we have all the data we need to build the fileset
		
	// SMIL generation
		this.sendMessage("Generating SMIL files");
		// One metadata list to rule them all ...
		MetadataList smilMetadata = new MetadataList();
		smilMetadata.add(makeMetaMeta(Namespaces.SMIL_20_NS_URI, "dtb:uid", uid));
		smilMetadata.add(makeMetaMeta(Namespaces.SMIL_20_NS_URI, "dtb:generator", "us_rfbd_TextOnlyCreator, v" + version));
		smilMetadata.add(makeMetaMeta(Namespaces.SMIL_20_NS_URI, "dtb:totalElapsedTime", "0:00:00.000"));

		HashSet<String> smilCustomTests = new HashSet<String>();
		
		for (int i = 0; i < smilBuilders.size(); i++) {
			SmilBuilder sb = smilBuilders.get(i);
			smilCustomTests.addAll(sb.getCustomTests());	// Collect all the customTests for the NCX
			sb.setMetadata(smilMetadata);
			try {
				sb.render(new File(outputDir.getAbsolutePath() + File.separator + 
						           "smilFile" + String.valueOf(i+1) + ".smil").toURI().toURL());
			} catch (MalformedURLException e3) {
				throw new TransformerRunException("URL error rendering SMIL file", e3);
			} catch (IOException e3) {
				throw new TransformerRunException("IO error rendering SMIL file", e3);
			} catch (XMLStreamException e3) {
				throw new TransformerRunException("XML error rendering SMIL file", e3);
			}
		}

	// NCX generation
		this.sendMessage("Generating NCX");
		// Create the metadata
		MetadataList ncxMetadata = new MetadataList();
		ncxMetadata.add(makeMetaMeta(Namespaces.Z2005_NCX_NS_URI, "dtb:uid", uid));
		ncxMetadata.add(makeMetaMeta(Namespaces.Z2005_NCX_NS_URI, "dtb:generator", "us_rfbd_TextOnlyCreator, v" + version));
		ncxMetadata.add(makeMetaMeta(Namespaces.Z2005_NCX_NS_URI, "dtb:depth", String.valueOf(ncxMapDepth)));
		ncxMetadata.add(makeMetaMeta(Namespaces.Z2005_NCX_NS_URI, "dtb:totalPageCount", String.valueOf(ncxPages.size())));		
		ncxMetadata.add(makeMetaMeta(Namespaces.Z2005_NCX_NS_URI, "dtb:maxPageNumber", String.valueOf(maxPageValue)));		

		NcxBuilder nb = new NcxBuilder(ncxMetadata, ncxMap, ncxPages, ncxTitle, ncxAuthors, smilCustomTests);
		
		URL ncxURL = null;
		try {
			ncxURL = new File(outputDir.getAbsolutePath() + File.separator + nameroot + ".ncx").toURI().toURL();
		} catch (MalformedURLException e2) {
			throw new TransformerRunException("Error in NCX URL", e2);
		}
		
		try {
			nb.render(ncxURL);
		} catch (IOException e1) {
			throw new TransformerRunException("IO error rendering NCX", e1);
		} catch (XMLStreamException e1) {
			throw new TransformerRunException("XML error rendering NCX", e1);
		}
		
	// Copy files to output (if necessary)
		FilesetImpl fs = null;
		DefaultFilesetErrorHandlerImpl fsErrHandler = new DefaultFilesetErrorHandlerImpl();
		FilesetFile baseFile;
		
		if (!inputFile.getParent().equals(outputDir.getPath())) {
			this.sendMessage("Copying files to output");
			
			// Open text file as fileset
			try {
				fs = new FilesetImpl(inputFile.toURI(), fsErrHandler);
			} catch (FilesetFatalException e) {
				throw new TransformerRunException("Can't create fileset from input document", e);
			}
			
			baseFile = fs.getLocalMember(inputFile.toURI());
			
			for (FilesetFile fsf : fs.getLocalMembers()) {
				if (!fsf.getName().equals(inputFile.getName())) {
					URI relURI = baseFile.getRelativeURI(fsf);
					try {
						FileUtils.copy(fsf.getFile(), new File(outputDir.getAbsolutePath() + File.separator + relURI.getPath()));
					} catch (IOException e) {
						throw new TransformerRunException("Error copying file", e);
					}
				}
			}
		}
		
	// OPF generation
		this.sendMessage("Generating OPF");
		// Add the fixed metadata to our list of collected items
		MetadataItem mi;
		mi = new MetadataItem(new QName("meta"));
		mi.addAttribute("name", "dtb:multimediaType");
		mi.addAttribute("content", "textNCX");
		opfMetadata.add(mi);
		
		mi = new MetadataItem(new QName("meta"));
		mi.addAttribute("name", "dtb:totalTime");
		mi.addAttribute("content", "0:00:00.000");
		opfMetadata.add(mi);

		// Build the manifest
		Set<URL> manifest = new HashSet<URL>();
		boolean hasImages = false;

		try {
			fs = new FilesetImpl(FilenameOrFileURI.toURI(outputDir.getAbsolutePath() + File.separator + inputFile.getName()), fsErrHandler);
		} catch (FilesetFatalException e) {
			throw new TransformerRunException("Can't create fileset from output document", e);
		}
		
		for(URI u : fs.getLocalMembersURIs()) {
			try {
				manifest.add(u.toURL());
			} catch (MalformedURLException e) {
				throw new TransformerRunException("Error forming package manifest", e);
			}
		}
		manifest.add(ncxURL); 		// Add the NCX, too
		
		// Build the spine
		LinkedHashSet<URL> spine = new LinkedHashSet<URL>();
		TextualContentFile mainFile = (TextualContentFile)fs.getLocalMember(FilenameOrFileURI.toURI(outputDir.getAbsolutePath() + File.separator + inputFile.getName()));
		
		for (FilesetFile f : mainFile.getReferencedLocalMembers()) {
			if (f instanceof SmilFile) {
				try {
					spine.add(f.getFile().toURI().toURL());
				} catch (MalformedURLException e) {
					throw new TransformerRunException("Bad URL encountered when creating package spine", e);
				}
			}
			else if (f instanceof ImageFile) {
				hasImages = true;
			}
		}
		
		// Add the dtb:multimediaContent metadata based on whether we found images or not
		mi = new MetadataItem(new QName("meta"));
		mi.addAttribute("name", "dtb:multimediaContent");
		if (hasImages) {
			mi.addAttribute("content", "text,image");
		}
		else {
			mi.addAttribute("content", "text");
		}
		opfMetadata.add(mi);			
		
		
		// Now deal with the resource file and its referred files
		try {
			fs = new FilesetImpl(FilenameOrFileURI.toURI(resourceFilePath), fsErrHandler);
		} catch (FilesetFatalException e) {
			throw new TransformerRunException("Can't create fileset from resource file", e);
		}
		baseFile = fs.getManifestMember();
		
		for(FilesetFile fsf : fs.getLocalMembers()) {
			try {
				manifest.add(FilenameOrFileURI.toURI(outputDir.getAbsolutePath() + File.separator + fsf.getName()).toURL());
			} catch (MalformedURLException e) {
				throw new TransformerRunException("Error adding resource file to package manifest", e);
			}
			
			// Copy the file, too
			URI relURI = baseFile.getRelativeURI(fsf);

			try {
				FileUtils.copy(fsf.getFile(), new File(outputDir.getAbsolutePath() + File.separator + relURI.getPath()));
			} catch (IOException e) {
				throw new TransformerRunException("Error copying resource file", e);
			}
		}

		// Now build the darned thing
		try {
			OpfBuilder ob = new OpfBuilder(OpfBuilder.OpfType.Z3986_2005, opfMetadata, manifest, spine);
			ob.render(FilenameOrFileURI.toURI(outputDir.getAbsolutePath() + File.separator + nameroot + ".opf").toURL());
		} catch (FilesetFatalException e) {
			throw new TransformerRunException("Fileset error rendering OPF", e);
		} catch (BuildException e) {
			throw new TransformerRunException("Build error rendering OPF", e);
		} catch (MalformedURLException e) {
			throw new TransformerRunException("URL error rendering OPF", e);
		} catch (IOException e) {
			throw new TransformerRunException("IO error rendering OPF", e);
		} catch (XMLStreamException e) {
			throw new TransformerRunException("XML error rendering OPF", e);
		}
		
		// All done!
		return true;
	}

	// A refactored convenience
	private QName parseConfigElement(StartElement se, ArrayList<QName>list) {
		String synchLocalName = se.getAttributeByName(new QName("name")).getValue();
		Attribute nsAtt = se.getAttributeByName(new QName("ns"));
		String synchNSURI;
		// If no namespace URI given, default to dtbook
		if (nsAtt == null) {
			synchNSURI = Namespaces.Z2005_DTBOOK_NS_URI;
		}
		else {
			synchNSURI = nsAtt.getValue();
		}
		// Add to the list for use when parsing
		QName itemName = new QName(synchNSURI, synchLocalName);
		if (list != null) {
			list.add(itemName);
		}
		return itemName;
	}
	
	// A refactored convenience
	private MetadataItem makeMetaMeta(String namespaceURI, String name, String value) {
		MetadataItem mi;
		
		mi = new MetadataItem(new QName(namespaceURI,"meta"));
		mi.addAttribute("name",name);
		mi.addAttribute("content",value);
		
		return mi;
	}
	
	/**
	 * A StaxFilter that adds id attributes to dtbook file (where needed) and collects data for SMIL, NCX, and OPF generation
	 * @author jpritchett@rfbd.org
	 * 
	 * This filter does the bulk of the work for this transformer:
	 * 	Builds the list of metadata for the OPF
	 *	Builds the labels for title and author(s) for the NCX
	 *	Builds the list of navigation points for the NCX navMap, including labels and playOrder
	 *  Builds the list of page targets for the NCX, including labels and playOrder
	 *  Builds the lists of SMIL structures for the SMIL files (seq's and text's), including:
	 *  	* customTests for skippable structures
	 *  	* escapable structures flagged
	 *  	* links noted for future resolution
	 *  	* breaks files at regular intervals
	 *  Adds @smilRef to all elements where needed
	 *  Adds @id to elements as needed
	 */
	private class SmilrefAdder extends StaxFilter {
		
		// A couple of QNames that we use a lot, defined here for convenience
		final QName smilrefQname = new QName(Namespaces.Z2005_DTBOOK_NS_URI,"smilref");
		final QName idQname = new QName("id");
		
		// Stuff to keep track of for NCX generation
		private long playOrder;
	    private NavigationItem curNavItem;
	    private QName curNavItemElementName;
	    private NavigationLabel curNavLabel;
		private Stack<NavigationItemPoint> navPointStack;
		
		// Stuff to keep track of for SMIL generation
		private Stack<SmilStructureContainer> smilStructureStack;
	    private IDGenerator idg_smil, idg_text;
	    private SmilBuilder currentSmil;
	    private long structPointCount;
		
		public SmilrefAdder(XMLEventReader xer, OutputStream outStream) throws XMLStreamException {
			super(xer, outStream);
			
			// Initialize everything
			playOrder = 1;
			navPointStack = new Stack<NavigationItemPoint>();
			smilStructureStack = new Stack<SmilStructureContainer>();
			curNavItem = null;
			curNavItemElementName = null;
			idg_smil = new IDGenerator("s");		// These will be the @id values for SMIL files
			idg_text = new IDGenerator("id4smil");	// These will be the added @id values for dtbook file (when needed)
			
			// Create the first SmilBuilder now
			currentSmil = new SmilBuilder();
			currentSmil.setSmilStructures(new ArrayList<SmilStructure>());
			smilBuilders.add(currentSmil);
			structPointCount = 0;
		}
		
		// StartElement does the bulk of the processing:
		//	* Collects all metadata for the OPF
		//	* Detects and sets up title and author structures
		//	* Detects and sets up new navigation items (pages and points)
		//	* Detects and sets up new SMIL containers
		//	* Detects and sets up new SMIL text items
		protected StartElement startElement(StartElement se) {
			String localName = se.getName().getLocalPart();
			
			// For dtbook, just strip out any foreign namespaces
			// TODO:  This is a workaround to remove the vnml namespace added by the sentence detector; fix that bug!
			if (localName.equals("dtbook")) {
				Collection<Namespace> elemNS = new ArrayList<Namespace>();
				elemNS.add(this.getEventFactory().createNamespace("http://www.daisy.org/z3986/2005/dtbook/"));
				
				Collection<Attribute> atts = new ArrayList<Attribute>();
				for (Iterator<Attribute> i = se.getAttributes(); i.hasNext(); ) {
					Attribute a = i.next();
					if (!a.getName().getPrefix().equals("xmlns")) {
						atts.add(a);
					}
				}
				return this.getEventFactory().createStartElement(se.getName(), atts.iterator(), elemNS.iterator());
			}
			
		// ==================================
		// PART 1:  metadata handling
		// ==================================

			if (localName.equals("meta")) {
				// Create an OPF metadata item for all dc: and dtb: items
				MetadataItem mi;
				String miName = se.getAttributeByName(new QName("name")).getValue();
				String miValue = se.getAttributeByName(new QName("content")).getValue();
				
				// Ignore dc:Identifier -- we only recognize dtb:uid
				if (miName.equals("dc:Identifier")) {
					return se;
				}
				
				// dtb:uid becomes dc:Identifier in the OPF
				if (miName.equals("dtb:uid")) {
					uid = miValue;
					miName = "dc:Identifier";
				}
				
				// Create the metadata item in the correct namespace and add to our opfMetadata list
				// Note:  non-dc:/dtb: metadata is just ignored
				if (miName.startsWith("dc:")) {
					mi = new MetadataItem(new QName(Namespaces.DUBLIN_CORE_NS_URI,miName.split(":")[1],"dc"));
					mi.setValue(miValue);
					opfMetadata.add(mi);
				}
				else if (miName.startsWith("dtb:")) {
					mi = new MetadataItem(new QName(Namespaces.OPF_10_NS_URI,"meta"));
					mi.addAttribute("name",miName);
					mi.addAttribute("content",miValue);
					opfMetadata.add(mi);
				}
				return se;	// metadata is never navigable or synchronizable, so bail now
			}

		// ==================================
		// PART 2:  Navigation point handling
		// ==================================
			
			// Create a NavigtionItem if this element is in the navItems list
			if (navItems.contains(se.getName())) {
			// Page numbers
				if (localName.equals("pagenum")) {
					NavigationItemPage page;
					
					// Get type
					Attribute typeAtt = se.getAttributeByName(new QName("page"));

					if (typeAtt == null || typeAtt.getValue().equals("normal")) {
						page = new NavigationItemPage(NavigationItemPage.PageType.NORMAL);
					}
					else if (typeAtt.getValue().equals("front")) {
						page = new NavigationItemPage(NavigationItemPage.PageType.FRONT);
					}
					else {
						// Don't you know?  Everybody's special!
						page = new NavigationItemPage(NavigationItemPage.PageType.SPECIAL);
					}						
					// Add the item to the page list and make the current item
					ncxPages.add(page);
					curNavItem = page;
				}
				
		  // Headings
				else if (localName.matches("h[1-6]")) {
					NavigationItemPoint point = new NavigationItemPoint();
					int depth = Integer.valueOf(localName.substring(1));
					
					// Pop the stack until we get to the level above this one
					while (navPointStack.size() >= depth) {
						navPointStack.pop();
					}
					
					// If the stack is empty, add this to the top-level NCX map list
					if (navPointStack.size() == 0) {
						ncxMap.add(point);
					}
					// If there's something on the stack, it's our parent, so add us to the subpoints
					else {
						NavigationItemPoint parent = navPointStack.peek();
						if (parent.getSubpoints() == null) {
							parent.setSubpoints(new ArrayList<NavigationItemPoint>());
						}
						parent.getSubpoints().add(point);
					}
					
					// Push this onto the stack, set as current point, and update the mapDepth (if needed)
					navPointStack.push(point);
					curNavItem = point;
					if (navPointStack.size() > ncxMapDepth) {
						ncxMapDepth = navPointStack.size();
					}
					
					// Also look to see if this should start a new SMIL file
					// (we break at the first heading following the smilEventThreshold)
					if (structPointCount >= smilEventThreshold) {
						currentSmil = new SmilBuilder();
						currentSmil.setSmilStructures(new ArrayList<SmilStructure>());
						smilBuilders.add(currentSmil);
						structPointCount = 0;		// reset counter for this new file
					}
				}
				
				// All navigation items set these things:
				//	* Remember the element name (we need this to tell when we close out this element in EndElement)
				//	* Set the play order
				//	* Set the pointer to the current item label (will be built via Characters as we continue the parse)
				curNavItemElementName = se.getName();
				if (curNavItem != null) {
					curNavItem.setPlayOrder(playOrder++);
					curNavLabel = curNavItem.getLabel();
				}
			}
			
		// ==================================
		// PART 3:  Title and Authors
		// ==================================
			
			// Make labels for title and authors
			if (localName.equals("doctitle") || localName.equals("docauthor")) {
				curNavLabel = new NavigationLabel("");		// We'll build the label as we go along via Characters()
				if (localName.equals("doctitle")) { ncxTitle = curNavLabel; }
				else { ncxAuthors.add(curNavLabel); }
				curNavItemElementName = se.getName();
			}
			
		// =====================================
		// PART 4:  Synchronization:  containers
		// =====================================
			
			// See if this is something that must be rendered as a container
			if (containerItems.contains(se.getName())) {
				// Create the container, add to the top-level list or to the children of the current container,
				// 	then push onto the stack
				SmilStructureContainer ssc = new SmilStructureContainer("seq", idg_smil.generateId(), localName);
				if (smilStructureStack.size() == 0) {
					currentSmil.getSmilStructures().add(ssc);
				}
				else {
					smilStructureStack.peek().getChildren().add(ssc);
				}
				smilStructureStack.push(ssc);
				
				// Create the SMIL reference for this structure
				String smilRef = "smilFile" + String.valueOf(smilBuilders.size()) + ".smil#" + ssc.getSmilID();

				// Create a new attribute collection that adds @smilref
                Collection<Attribute> coll = new ArrayList<Attribute>();
                coll.add(this.getEventFactory().createAttribute(smilrefQname, smilRef));
                for (Iterator<Attribute> ai = se.getAttributes(); ai.hasNext(); ) {
                	coll.add(ai.next());
                }
                
                // If we have a current navigation item, set the content if necessary
                // (if the item has no SMIL pointer yet, then this must be it)
                if (curNavItem != null && curNavItem.getSmilContent() == null) {
                	curNavItem.setSmilContent(smilRef);
                }
                
                // Add customTests for skippable items
                if (skippableItems.contains(se.getName())) {
                	// We need to inspect the render attribute for prodnotes & sidebars
                	if (localName.equals("prodnote") || localName.equals("sidebar")) {
                		if (se.getAttributeByName(new QName("render")).getValue().equals("optional")) {
                			ssc.setCustomTestName(localName);
                		}
                	}
                	else {
                		ssc.setCustomTestName(localName);
                	}
                }
                
                // Set escapable items as such
                if (escapableItems.contains(se.getName())) {
                	ssc.setEscapable(true);
                }
                
                // All done; return a new StartElement that uses our enhanced attribute collection
				return this.getEventFactory().createStartElement(se.getName(), coll.iterator(), se.getNamespaces());
			}

			// ========================================
			// PART 5:  Synchronization:  text elements
			// ========================================
			if (synchItems.contains(se.getName()) || se.getAttributeByName(synchAttr) != null) {
				// Collect or create the id of this element
				String textID;
				boolean needsID = false;
				if (se.getAttributeByName(idQname) == null) {
					textID = idg_text.generateId();
					needsID = true;
				}
				else {
					textID = se.getAttributeByName(idQname).getValue();
				}

				// Create a SmilStructureText with appropriate data and add to list (either
				// at top level or as child of current container)
				SmilStructureText sst = new SmilStructureText(idg_smil.generateId(), inputFile.getName() + "#" + textID);
				if (smilStructureStack.size() == 0) {
					currentSmil.getSmilStructures().add(sst);
				}
				else {
					smilStructureStack.peek().getChildren().add(sst);
				}
				structPointCount++;		// We keep a count of these to know when to break SMIL files
                
				// Create the SMIL reference for this structure
				String smilRef = "smilFile" + String.valueOf(smilBuilders.size()) + ".smil#" + sst.getSmilID();

                // Create a new attribute collection that adds @smilref (and @id, if necessary) 
				// to existing ones
				// AND removes the synchAttribute
                Collection<Attribute> coll = new ArrayList<Attribute>();
                coll.add(this.getEventFactory().createAttribute(smilrefQname, smilRef));
                if (needsID) {
                	coll.add(this.getEventFactory().createAttribute(idQname, textID));
                }
                for (Iterator<Attribute> ai = se.getAttributes(); ai.hasNext(); ) {
                	Attribute a = ai.next();
                	if (!a.getName().equals(synchAttr)) {
                		coll.add(a);
                	}
                }
                
                // If we have a current navigation item, set the content if necessary
                // (if the item doesn't have a SMIL pointer yet, then this must be it)
                if (curNavItem != null && curNavItem.getSmilContent() == null) {
                	curNavItem.setSmilContent(smilRef);
                }

                // If this should be skippable, set the customTest attribute, too
                if (skippableItems.contains(se.getName())) {
                	sst.setCustomTestName(localName);
                }
                
                // If this is escapable, set that, also
                if (escapableItems.contains(se.getName())) {
                	sst.setEscapable(true);
                }
                
                // If this is a link, save it in the map of linking structures
                // Linking structures are a, noteref, and annoref
                if (se.getName().equals(new QName(Namespaces.Z2005_DTBOOK_NS_URI, "a")) ||
                	se.getName().equals(new QName(Namespaces.Z2005_DTBOOK_NS_URI, "noteref")) ||
                	se.getName().equals(new QName(Namespaces.Z2005_DTBOOK_NS_URI, "annoref"))) {
                	
                	// For <a>, the attribute with the target is @href; everybody else uses @idref
                	Attribute targetAtt;
                	if (se.getName().equals(new QName(Namespaces.Z2005_DTBOOK_NS_URI, "a"))) {
                		targetAtt = se.getAttributeByName(new QName("href"));
                	}
                	else {
                		targetAtt = se.getAttributeByName(new QName("idref"));
                	}
                	
                	// External links:  just set and forget
                	Attribute externalAtt = se.getAttributeByName(new QName("external"));
                	if (externalAtt != null && externalAtt.getValue().equals("true")) {
	                	// Ignore mailto: links, which aren't really URIs
	                	if (!targetAtt.getValue().startsWith("mailto:")) {
	                		sst.setLinkExternal(true);
	                		sst.setLinkTarget(targetAtt.getValue());
	                	}
                	}
                	
                	// Internal links:  Add to the list of SMIL links for resolution later
                	else {
                		// We store these in lists keyed under the target string 
                		// (we use lists because a target can have multiple links to it)
                		List<SmilStructure> ssList;
                		
                		// If we've not already linked to this target, create a new list and add to the lookup
                		if (!smilLinks.containsKey(targetAtt.getValue())) {
                			ssList = new ArrayList<SmilStructure>();
                			smilLinks.put(targetAtt.getValue(), ssList);
                		}
                		// Otherwise, we'll add this to the existing list
                		else {
                			ssList = smilLinks.get(targetAtt.getValue());
                		}
                		ssList.add(sst);
                		
                		// Remember the SMIL file name for this structure, since we'll need it later 
                		// when we build the actual links
                		// This list is keyed under smilID
                		linkFiles.put(sst.getSmilID(), "smilFile" + String.valueOf(smilBuilders.size()) + ".smil");
                	}
                }
                
                // All done with text structures; return a new StartElement with our enhanced attribute collection
				return this.getEventFactory().createStartElement(se.getName(), coll.iterator(), se.getNamespaces());
			}

			// If we get here, it wasn't a synch item, so just pass it along
			return se;
		}
		
		// Characters() collects text for navigation labels (title, author, points, and pages)
		protected Characters characters(Characters c) {
			// If we are in a navigation item add these characters to the text label
			if (curNavLabel != null) {
				curNavLabel.setText(curNavLabel.getText().concat(c.getData()));
			}
			
			return c;
		}
		
		// EndElement() does cleanup when we end elements that are navigation items or SMIL containers
		protected EndElement endElement(EndElement ee) {
			// If this ends our current navigation item, clear everything out
			if (curNavItemElementName != null && curNavItemElementName.equals(ee.getName())) {
				// If this is a pagenum, and if we can parse the content as a number, set the value
				if (curNavItemElementName.equals(new QName(Namespaces.Z2005_DTBOOK_NS_URI, "pagenum"))) {
					try {
						long value = Long.valueOf(curNavLabel.getText().trim());
						((NavigationItemPage)curNavItem).setValue(value);
						if (value > maxPageValue) { maxPageValue = value; }
					} catch (NumberFormatException e) {
						// Just ignore all format errors
					}
				}
				curNavItem = null;
				curNavItemElementName = null;
				curNavLabel = null;
			}
			
			// If this ends something that needs a container, pop the SMIL structure stack
			if (containerItems.contains(ee.getName())) {
				smilStructureStack.pop();
			}
			return ee;
		}
	}
}


