/**
 * 
 */
package org.daisy.util.dtb.build;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.dtb.meta.MetadataItem;
import org.daisy.util.dtb.meta.MetadataList;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.impl.FilesetFileFactory;
import org.daisy.util.xml.IDGenerator;
import org.daisy.util.xml.Namespaces;
import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.daisy.util.xml.pool.StAXOutputFactoryPool;

/**
 * Builds and renders an OPF file
 * @author James Pritchett (jpritchett@rfbd.org)
 *
 */

public class OpfBuilder {
	OpfType mOutputType;
	private MetadataList dcMetadata, xMetadata;
	private HashMap<String, ManifestItem> myManifest;	// Items keyed to absolute path
	private LinkedHashMap<String, ManifestItem> mySpine;	// Items keyed to absolute path (same as in manifest)
	
	/**
	 * Generic constructor
	 * @param type The type of OPF to create (from enum)
	 */
	public OpfBuilder(OpfType type) {
		if (type == OpfType.OPF_20_10) {
			throw new UnsupportedOperationException("OPF 2.0 files not yet supported");
		}
		
		mOutputType = type;
		
		// Initialize the collections
		dcMetadata = new MetadataList();
		xMetadata = new MetadataList();
		myManifest = new HashMap<String, ManifestItem>();
		mySpine = new LinkedHashMap<String, ManifestItem>();

		// Some conveniences and helpers that are used in various places
		xef = StAXEventFactoryPool.getInstance().acquire();
		nl = xef.createCharacters(LINEBREAK);
		tab = xef.createCharacters(TAB);

		opfNamespace = xef.createNamespace(Namespaces.OPF_10_NS_URI);
		dcNamespace = xef.createNamespace(Namespaces.DUBLIN_CORE_NS_URI);
	}
	
	/**
	 * Construct an OpfBuilder with full dataset
	 * @param type The type of OPF to create (from enum)
	 * @param metadata The metadata items to use
	 * @param manifest URLs of files to include in the manifest
	 * @param spine URLs of files to include in the spine, in order
	 */
	public OpfBuilder(OpfType type, MetadataList metadata, Set<URL> manifest, LinkedHashSet<URL> spine) throws FilesetFatalException {
		this(type);
		
		// Use object methods to set everything
		this.setMetadata(metadata);
		this.setManifest(manifest);
		this.setSpine(spine);
	}
	
	/**
	 * Set all metadata at once
	 * @param metadata The complete list of metadata items to use
	 */
	public void setMetadata(MetadataList metadata) {
		for (Iterator<MetadataItem> i = metadata.iterator(); i.hasNext(); ) {
			this.addMetadataItem(i.next());
		}
	}
	
	/**
	 * Set all manifest files at once
	 * @param manifest The complete list of files (URLs) to include in the manifest
	 */
	public void setManifest(Set<URL> manifest) throws FilesetFatalException {
		// Add manifest items for each URL in the set
		for (Iterator<URL> i = manifest.iterator(); i.hasNext(); ) {
			this.addManifestItem(i.next());
		}
	}
	
	/**
	 * Set all spine items at once
	 * @param spine The complete list of files (URLs) to include in the spine
	 */
	public void setSpine(LinkedHashSet<URL> spine) throws FilesetFatalException {
		// Add spine items for each URL in the set
		for (Iterator<URL> i = spine.iterator(); i.hasNext(); ) {
			this.addSpineItem(i.next());
		}
	}
	
	/**
	 * Add a single metadata item to the OPF
	 * @param item The MetadataItem to add
	 */
	public void addMetadataItem(MetadataItem item) {
		String name = item.getQName().getLocalPart();
		
		// Metadata declared as DC
		if (item.getQName().getPrefix().equals("dc")) {
			// Add the @id to dc:Identifier
			if (name.equals("Identifier")) {
				item.addAttribute("id", "opf_UID");
				dcMetadata.add(item);
			}
			// Real DC items go into dcMetadata
			else if (name.equals("Title") ||
					 name.equals("Language") ||
					 name.equals("Contributor") ||
					 name.equals("Coverage") ||
					 name.equals("Creator") ||
					 name.equals("Date") ||
					 name.equals("Description") ||
//					 name.equals("Format") ||		// Ignore Format; we add this automagically
					 name.equals("Publisher") ||
					 name.equals("Relation") ||
					 name.equals("Rights") ||
					 name.equals("Source") ||
					 name.equals("Subject") ||
					 name.equals("Type")) {
				dcMetadata.add(item);
			}
			// Bogus ones go into xMetadata
			else {
				xMetadata.add(item);
			}
		}
		// All other metadata are X
		else {
			xMetadata.add(item);
		}
	}
	
	/**
	 * Add a single metadata item to the OPF
	 * @param name The name of the metadata item
	 * @param value The value of the metadata item
	 */
	public void addMetadataItem(String name, String value) {

	// First create the item
		MetadataItem item;
		
		// If it's a DC item, it gets named as such
		if (name.equals("dc:Format")) {
			return;
		}
		else if (name.equals("dc:Title") ||
			name.equals("dc:Identifier") ||
			name.equals("dc:Language") ||
			name.equals("dc:Contributor") ||
			name.equals("dc:Coverage") ||
			name.equals("dc:Creator") ||
			name.equals("dc:Date") ||
			name.equals("dc:Description") ||
//			name.equals("dc:Format") ||
			name.equals("dc:Publisher") ||
			name.equals("dc:Relation") ||
			name.equals("dc:Rights") ||
			name.equals("dc:Source") ||
			name.equals("dc:Subject") ||
			name.equals("dc:Type")) {
			item = new MetadataItem(new QName(dcNamespace.getNamespaceURI(),name.split(":")[1],"dc"));
			item.setValue(value);
		}
		
		// If it's anything else, it's a meta element with @name and @content
		else {
			item = new MetadataItem(new QName(opfNamespace.getNamespaceURI(),"meta"));
			item.addAttribute("name",name);
			item.addAttribute("content",value);
		}
		
	// Then add it to the list
		this.addMetadataItem(item);
	}
	
	/**
	 * Add a single resource to the manifest
	 * @param resource URL of resource to add
	 */
	public void addManifestItem(URL resource) throws FilesetFatalException {		
		// Get the FilesetFile (if possible) and add to manifest
		FilesetFile ff = getFilesetFile(resource.getPath());
		
		this.addManifestItem(ff);
	}
	
	/**
	 * Add a single file to the manifest
	 * @param file The FilesetFile to add
	 */
	public void addManifestItem(FilesetFile file) {
		
		ManifestItem mi = new ManifestItem(file);
		
		// Add item as is, using the absolute path as a key
		this.myManifest.put(mi.getAbsolutePath(),mi);
	}
	
	/**
	 * Add a single resource to the end of the spine; add to manifest if not already there
	 * @param resource URL of resource to add
	 */
	public void addSpineItem(URL resource) throws FilesetFatalException {
		// Get the FilesetFile (if possible), then add to Spine
		// Get the FilesetFile (if possible) and add to manifest
		FilesetFile ff = getFilesetFile(resource.getPath());

		this.addSpineItem(ff);
	}
	
	/**
	 * Add a single file to the end of the spine; add to manifest if not already there
	 * @param file The FilesetFile to add
	 */
	public void addSpineItem(FilesetFile file) {
		// Look up the item in the manifest; if not there, add it to manifest
		if (!myManifest.containsKey(file.getFile().getAbsolutePath())) {
			this.addManifestItem(file);
		}
		
		// Fetch from manifest and add to spine
		ManifestItem mi = myManifest.get(file.getFile().getAbsolutePath());
		mySpine.put(mi.getAbsolutePath(), mi);
	}
	
	/**
	 * Render the OPF as an XML document
	 * @param destination Output file URL
	 * @throws IOException
	 * 
	 * Much of this code shamelessly stolen from Markus' OPFBuilder
	 */
	public void render(URL destination) throws IOException, XMLStreamException, BuildException {

	// Set up output writer
		Map<String,Object> properties = StAXOutputFactoryPool.getInstance().getDefaultPropertyMap();
		File outputOPF = new File(destination.getPath());
		XMLEventWriter writer = StAXOutputFactoryPool.getInstance().acquire(properties).createXMLEventWriter(new FileOutputStream(outputOPF));

	// Render the prolog
		QName pkg = new QName(opfNamespace.getNamespaceURI(),"package");
		
		writeEventPlusNewline(writer, xef.createStartDocument("utf-8", "1.0"), 0);
		writeEventPlusNewline(writer, xef.createDTD(OPF_DOCTYPE), 0);		
		
	// Root element
		writer.add(xef.createStartElement("",pkg.getNamespaceURI(),pkg.getLocalPart()));
		
		// Look for a dc:Identifier; this is an error if not found
		if (dcMetadata.get(new QName(dcNamespace.getNamespaceURI(),"Identifier","dc")) == null) {
			throw new BuildException("Missing dc:Identifier metadata (required by DTD)");
		}
		
		// Otherwise, add @unique-identifier (fixed value set in setMetadataItem() above)
		writer.add(xef.createAttribute("unique-identifier", "opf_UID"));
		writer.add(nl);
		
	// Metadata items (DC: and X)
		// Check to be sure DTD-required metadata items are present
		if (dcMetadata.get(new QName(dcNamespace.getNamespaceURI(),"Title","dc")) == null) {
			throw new BuildException("Missing dc:Title metadata (required by DTD)");
		}
		if (dcMetadata.get(new QName(dcNamespace.getNamespaceURI(),"Language","dc")) == null) {
			throw new BuildException("Missing dc:Language metadata (required by DTD)");
		}
		
		// Check for Z39.86-required DC metadata items (these are also required by NIMAS)
		if (this.mOutputType == OpfType.Z3986_2005 || 
			this.mOutputType == OpfType.NIMAS_11) {
			if (dcMetadata.get(new QName(dcNamespace.getNamespaceURI(), "Publisher", "dc")) == null) {
				throw new BuildException("Missing dc:Publisher metadata (required for Z39.86 and NIMAS)");
			}
			if (dcMetadata.get(new QName(dcNamespace.getNamespaceURI(), "Date", "dc")) == null) {
				throw new BuildException("Missing dc:Date metadata (required for Z39.86 and NIMAS)");
			}
		}
		
		// TODO Rethink x-metadata so that this can be implemented more cleanly
//		// Check for Z39.86-required X metadata items
//		if (this.mOutputType == OpfBuilder.OpfType.Z3986_2005) {
//			if (xMetadata.get(new QName(opfNamespace.getNamespaceURI(), "dtb:multimediaType")) == null) {
//				throw new BuildException("Missing dtb:multimediaType metadata (required for Z39.86)");
//			}
//			if (xMetadata.get(new QName(opfNamespace.getNamespaceURI(), "dtb:multimediaContent")) == null) {
//				throw new BuildException("Missing dtb:multimediaContent metadata (required for Z39.86)");
//			}
//			if (xMetadata.get(new QName(opfNamespace.getNamespaceURI(), "dtb:totalTime")) == null) {
//				throw new BuildException("Missing dtb:totalTime metadata (required for Z39.86)");
//			}
//		}
		
		// Check for NIMAS-required metadata
		if (this.mOutputType == OpfType.NIMAS_11) {
			if (dcMetadata.get(new QName(dcNamespace.getNamespaceURI(), "Rights", "dc")) == null) {
				throw new BuildException("Missing dc:Rights (required for NIMAS)");
			}
			if (dcMetadata.get(new QName(dcNamespace.getNamespaceURI(), "Source", "dc")) == null) {
				throw new BuildException("Missing dc:Source (required for NIMAS)");
			}			
		}

		// Add the appropriate dc:Format item
		if (this.mOutputType == OpfType.Z3986_2005) {
			this.dcMetadata.add(new MetadataItem(new QName(dcNamespace.getNamespaceURI(), "Format", "dc"), "ANSI/NISO Z39.86-2005"));
		}
		else if (this.mOutputType == OpfType.NIMAS_11) {
			this.dcMetadata.add(new MetadataItem(new QName(dcNamespace.getNamespaceURI(), "Format", "dc"), "NIMAS 1.1"));
		}
		
		QName metadata = new QName(opfNamespace.getNamespaceURI(),"metadata");		
		QName dcMetadata = new QName(opfNamespace.getNamespaceURI(),"dc-metadata");
		QName xMetadata = new QName(opfNamespace.getNamespaceURI(),"x-metadata");
		
		// First, the DC metadata
		writeEventPlusNewline(writer, xef.createStartElement(metadata,null,null), 0);
		writer.add(tab);
		writer.add(xef.createStartElement(dcMetadata,null,null));
		writer.add(xef.createNamespace("dc", dcNamespace.getNamespaceURI()));
		writer.add(xef.createNamespace("oebpackage", opfNamespace.getNamespaceURI()));
		writer.add(nl);

		for (Iterator<MetadataItem> i = this.dcMetadata.iterator(); i.hasNext(); ) {
			MetadataItem item = i.next();
			writer.add(tab); writer.add(tab);
			item.asXMLEvents(writer);
			writer.add(nl);
		}
		writeEventPlusNewline(writer, xef.createEndElement(dcMetadata,null), 1);
		
		// Then, the X metadata
		if (this.xMetadata.size() > 0) {
			writeEventPlusNewline(writer, xef.createStartElement(xMetadata,null,null), 1);
			for (Iterator<MetadataItem> i = this.xMetadata.iterator(); i.hasNext(); ) {
				MetadataItem item = i.next();
				writer.add(tab); writer.add(tab);
				item.asXMLEvents(writer);
				writer.add(nl);
			}
			writeEventPlusNewline(writer, xef.createEndElement(xMetadata,null), 1);
		}
		writeEventPlusNewline(writer, xef.createEndElement(metadata,null), 0);

		
	// Manifest
		// No files in manifest is an error
		if (myManifest == null || myManifest.size() == 0) {
			throw new BuildException("No files in manifest!");
		}
		QName manifest = new QName(opfNamespace.getNamespaceURI(),"manifest");
		QName item = new QName(opfNamespace.getNamespaceURI(),"item");

		writeEventPlusNewline(writer, xef.createStartElement(manifest,null,null), 0);

		for (Iterator<ManifestItem> i = myManifest.values().iterator(); i.hasNext(); ) {
			ManifestItem mi = i.next();
			writer.add(tab); writer.add(tab);
			writer.add(xef.createStartElement(item,null,null));
			writer.add(xef.createAttribute("id", mi.getId()));
			// TODO Known bug here:  @href should use path relative to OPF destination
			writer.add(xef.createAttribute("href", mi.getFilename()));
			writer.add(xef.createAttribute("media-type", mi.getMimeTypeString()));
			writeEventPlusNewline(writer, xef.createEndElement(item,null), 0);
		}
		
		writeEventPlusNewline(writer, xef.createEndElement(manifest,null), 0);

	// Spine
		// No files in spine is an error
		if (mySpine == null || mySpine.size() == 0) {
			throw new BuildException("No files in spine!");
		}
		for (Iterator<ManifestItem> i = mySpine.values().iterator(); i.hasNext(); ) {
			ManifestItem mi = i.next();
			
		// Check to be sure that all files are in the manifest, and that they are of the correct media-type
			if (myManifest.get(mi.getAbsolutePath()) == null) {
				throw new BuildException("Spine file " + mi.getFilename() + " not listed in manifest");
			}
			
			if (this.mOutputType == OpfType.Z3986_2005 &&
				mi.getMimeTypeString() != "application/smil") {
				throw new BuildException("Spine file " + mi.getFilename() + " is not a SMIL file (required by Z39.86)");
			}
			if (this.mOutputType == OpfType.NIMAS_11 &&
				mi.getMimeTypeString() != "application/x-dtbook+xml") {
				throw new BuildException("Spine file " + mi.getFilename() + " is not a dtbook file (required by NIMAS 1.1)");
			}
		}
		
		QName spine = new QName(opfNamespace.getNamespaceURI(),"spine");
		QName itemref = new QName(opfNamespace.getNamespaceURI(),"itemref");

		writeEventPlusNewline(writer, xef.createStartElement(spine,null,null), 0);
		
		for (Iterator<ManifestItem> i = mySpine.values().iterator(); i.hasNext(); ) {
			ManifestItem mi = i.next();
			writer.add(tab); writer.add(tab);
			writer.add(xef.createStartElement(itemref,null,null));
			writer.add(xef.createAttribute("idref", mi.getId()));
			writeEventPlusNewline(writer, xef.createEndElement(itemref,null), 0);
		}
		
		writeEventPlusNewline(writer, xef.createEndElement(spine,null), 0);
	
	// Close everything up and exit
		writer.add(xef.createEndElement("",pkg.getNamespaceURI(),pkg.getLocalPart()));						
		writer.add(xef.createEndDocument());
		writer.close();
	}
	
	public enum OpfType {
		OPF_20_10, 		//http://www.openebook.org/2007/opf/OPF_2.0_final_spec.html
		Z3986_2005,		//http://www.daisy.org/z3986/2005/Z3986-2005.html#OPF
		NIMAS_11;			//http://nimas.cast.org/about/proposal/spec-v1_1_anno.html
	}
	
	// Just a refactored convenience
	private void writeEventPlusNewline(XMLEventWriter ew, XMLEvent event, int indents) throws XMLStreamException {
		for (int i = 0; i < indents; i++) {
			ew.add(tab);
		}
		ew.add(event);
		ew.add(nl);
	}
	
	// Take a path and return a FilesetFile
	// This assumes that the file extensions are correct, and uses FilesetFileFactory logic to detect file type
	private FilesetFile getFilesetFile(String fname) throws FilesetFatalException {
	 	return fac.newFilesetFile(new File(fname));
	}
	
	// Constants and other items useful in multiple contexts
	private static final String OPF_DOCTYPE = "<!DOCTYPE package PUBLIC \"+//ISBN 0-9673008-1-9//DTD OEB 1.2 Package//EN\" \"http://openebook.org/dtds/oeb-1.2/oebpkg12.dtd\" >"; 
	private static final String LINEBREAK = "\n";
	private static final String TAB = "\t";
	private XMLEventFactory xef = null;
	private XMLEvent nl = null;
	private XMLEvent tab = null;
	private Namespace opfNamespace = null;
	private Namespace dcNamespace = null;
	private final IDGenerator idg = new IDGenerator("opf_");
	private final FilesetFileFactory fac = FilesetFileFactory.newInstance();

	// Convenience class to hold information about items for the manifest
	private class ManifestItem {
		private FilesetFile f;
		private String id;

		public ManifestItem(FilesetFile file) {
			f = file;
			id = idg.generateId();
		}
		
		public String getId() {
			return id;
		}
		
		public String getFilename() {
			return f.getName();		
		}
		
		public String getAbsolutePath() {
			return f.getFile().getAbsolutePath();
		}
		
		public String getMimeTypeString() {
			return f.getMimeType().getString();
		}
		
	}
}
