/**
 * 
 */
package org.daisy.util.dtb.build;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
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
	private ArrayList<String> metadataNames;		// Names of all metadata items, with prefixes (for testing later)
	private HashMap<String, ManifestItem> myManifest;	// Items keyed to absolute path
	private LinkedHashMap<String, ManifestItem> mySpine;	// Items keyed to absolute path (same as in manifest)
	
	/**
	 * Generic constructor
	 * @param type The type of OPF to create (from enum)
	 */
	public OpfBuilder(OpfType type) {
		// TODO Add support for OPF 2.0
		if (type == OpfType.OPF_20_10) {
			throw new UnsupportedOperationException("OPF 2.0 files not yet supported");
		}
		
		mOutputType = type;
		
		// Initialize the collections
		dcMetadata = new MetadataList();
		xMetadata = new MetadataList();
		metadataNames = new ArrayList<String>();
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
	 * @throws FilesetFatalException, BuildException
	 */
	public OpfBuilder(OpfType type, MetadataList metadata, Set<URL> manifest, LinkedHashSet<URL> spine) throws FilesetFatalException, BuildException {
		this(type);
		
		// Use object methods to set everything
		this.setMetadata(metadata);
		this.setManifest(manifest);
		this.setSpine(spine);
	}
	
	/**
	 * Set all metadata at once
	 * @param metadata The complete list of metadata items to use
	 * @throws BuildException
	 */
	public void setMetadata(MetadataList metadata) throws BuildException {
		// These are added one at a time to do validation and cleanup
		for (Iterator<MetadataItem> i = metadata.iterator(); i.hasNext(); ) {
			this.addMetadataItem(i.next());
		}
	}
	
	/**
	 * Set all manifest files at once
	 * @param manifest The complete list of files (URLs) to include in the manifest
	 * @throws FilesetFatalException
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
	 * @throws FilesetFatalException
	 */
	public void setSpine(LinkedHashSet<URL> spine) throws FilesetFatalException {
		// Add spine items for each URL in the set
		for (Iterator<URL> i = spine.iterator(); i.hasNext(); ) {
			this.addSpineItem(i.next());
		}
	}
	
	/**
	 * Add a single metadata item to the OPF
	 * This method does some sorting and cleanup of the item as presented to insure validity
	 * Note:  Ths ignores dc:Format items, since this is auto-generated at rendering time
	 * @param item The MetadataItem to add
	 * @throws BuildException
	 */
	public void addMetadataItem(MetadataItem item) throws BuildException {
		String name = item.getQName().getLocalPart();
		
		// Metadata declared as DC
		if (item.getQName().getPrefix().equals("dc")) {
			// Add the @id to dc:Identifier
			if (name.equals("Identifier")) {
				item.addAttribute("id", "opf_UID");
				dcMetadata.add(item);
				metadataNames.add("dc:" + name);
			}
			// Real DC items go into dcMetadata (except "Format", which is ignored)
			else if (name.equals("Title") ||
					 name.equals("Language") ||
					 name.equals("Contributor") ||
					 name.equals("Coverage") ||
					 name.equals("Creator") ||
					 name.equals("Date") ||
					 name.equals("Description") ||
					 name.equals("Publisher") ||
					 name.equals("Relation") ||
					 name.equals("Rights") ||
					 name.equals("Source") ||
					 name.equals("Subject") ||
					 name.equals("Type")) {
				dcMetadata.add(item);
				metadataNames.add("dc:" + name);
			}
			// Bogus ones go into xMetadata
			else if (!name.equals("Format")){
				this.addMetadataItem(name, item.getValue());		// This will add it properly as meta/@name
			}
		}
		
		// All other metadata are X
		// If it's passed to us as meta, be sure that you have a @name & @content, and a namespace
		// Also, delete any attributes that aren't in the DTD (@id, @xml:lang, @scheme)
		else if (item.getQName().getLocalPart().equals("meta")) {
			boolean hasName = false;
			boolean hasContent = false;
			String metadataName = null;
			
			// Look for @name and @content and delete anything funky
			for (ListIterator<Attribute>i = item.getAttributes(); i.hasNext(); ) {
				Attribute a = i.next();
				if (a.getName().getLocalPart().equals("name")) { hasName = true; metadataName = a.getValue(); }
				else if (a.getName().getLocalPart().equals("content")) { hasContent = true; }
				else if (!a.getName().getLocalPart().equals("id") &&
						 !a.getName().getLocalPart().equals("xml:lang") &&
						 !a.getName().getLocalPart().equals("scheme")) {
					i.remove();
				}
			}
			
			// If it's OK, add it
			if (hasName && hasContent) {
				if (item.getQName().getNamespaceURI().equals(opfNamespace.getNamespaceURI())) {
					xMetadata.add(item);
				}
				// If this isn't namespaced, make a new copy that is and add that
				else {
					MetadataItem newItem = new MetadataItem(new QName(opfNamespace.getNamespaceURI(),"meta"));
					for (ListIterator<Attribute>i = item.getAttributes(); i.hasNext(); ) {
						newItem.addAttribute(i.next());
					}
					xMetadata.add(newItem);
				}
				metadataNames.add(metadataName);
			}
			else {
				throw new BuildException("meta element missing @name or @content");
			}
		}
		
		
		// If it's anything else at all, add via name & value strings
		else {
			this.addMetadataItem(name, item.getValue());
		}
	}
	
	/**
	 * Add a single metadata item to the OPF
	 * @param name The name of the metadata item
	 * @param value The value of the metadata item
	 * @return The created MetadataItem (so that additional attributes can be added)
	 * @throws BuildException
	 */
	public MetadataItem addMetadataItem(String name, String value) throws BuildException {

	// First create the item
		MetadataItem item;
		
		// If it's a DC item, it gets named as such
		if (name.equals("dc:Format")) {
			return null;
		}
		else if (name.equals("dc:Title") ||
			name.equals("dc:Identifier") ||
			name.equals("dc:Language") ||
			name.equals("dc:Contributor") ||
			name.equals("dc:Coverage") ||
			name.equals("dc:Creator") ||
			name.equals("dc:Date") ||
			name.equals("dc:Description") ||
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
		return item;
	}
	
	/**
	 * Add a single resource to the manifest
	 * @param resource URL of resource to add
	 * @throws FilesetFatalException
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
	 * @throws FilesetFatalException
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
	 * This does checking to insure that output file is valid to DTD and to referenced spec.
	 * @param destination Output file URL
	 * @throws IOException, XMLStreamException, BuildException
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
		if (!metadataNames.contains("dc:Identifier")) {
			throw new BuildException("Missing dc:Identifier metadata (required by DTD)");
		}
		
		// Otherwise, add @unique-identifier (fixed value set in setMetadataItem() above)
		writer.add(xef.createAttribute("unique-identifier", "opf_UID"));
		writer.add(nl);
		
	// Metadata items (DC: and X)
		// Check to be sure DTD-required metadata items are present
		if (!metadataNames.contains("dc:Title")) {
			throw new BuildException("Missing dc:Title metadata (required by DTD)");
		}
		if (!metadataNames.contains("dc:Language")) {
			throw new BuildException("Missing dc:Language metadata (required by DTD)");
		}
		
		// Check for Z39.86-required DC metadata items (these are also required by NIMAS)
		if (this.mOutputType == OpfType.Z3986_2005 || 
			this.mOutputType == OpfType.NIMAS_11) {
			if (!metadataNames.contains("dc:Publisher")) {
				throw new BuildException("Missing dc:Publisher metadata (required for Z39.86 and NIMAS)");
			}
			if (!metadataNames.contains("dc:Date")) {
				throw new BuildException("Missing dc:Date metadata (required for Z39.86 and NIMAS)");
			}
		}
		
		// Check for Z39.86-required X metadata items
		if (this.mOutputType == OpfBuilder.OpfType.Z3986_2005) {
			if (!metadataNames.contains("dtb:multimediaType")) {
				throw new BuildException("Missing dtb:multimediaType metadata (required for Z39.86)");
			}
			if (!metadataNames.contains("dtb:multimediaContent")) {
				throw new BuildException("Missing dtb:multimediaContent metadata (required for Z39.86)");
			}
			if (!metadataNames.contains("dtb:totalTime")) {
				throw new BuildException("Missing dtb:totalTime metadata (required for Z39.86)");
			}
		}
		
		// Check for NIMAS-required metadata
		if (this.mOutputType == OpfType.NIMAS_11) {
			if (!metadataNames.contains("dc:Rights")) {
				throw new BuildException("Missing dc:Rights (required for NIMAS)");
			}
			if (!metadataNames.contains("dc:Source")) {
				throw new BuildException("Missing dc:Source (required for NIMAS)");
			}
			if (!metadataNames.contains("nimas-SourceEdition")) {
				throw new BuildException("Missing nimas-SourceEdition metadata (required for NIMAS 1.1)");
			}
			if (!metadataNames.contains("nimas-SourceDate")) {
				throw new BuildException("Missing nimas-SourceDate metadata (required for NIMAS 1.1)");
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
		
		// We need to know the name of the OPF destination folder so that we can strip it from file paths
		File destinationFolder = outputOPF.getParentFile();
		String destinationFolderPath = "";
		if (destinationFolder != null) {
			destinationFolderPath =  destinationFolder.getAbsolutePath();
		}

		writeEventPlusNewline(writer, xef.createStartElement(manifest,null,null), 0);

		for (Iterator<ManifestItem> i = myManifest.values().iterator(); i.hasNext(); ) {
			ManifestItem mi = i.next();
			writer.add(tab); writer.add(tab);
			writer.add(xef.createStartElement(item,null,null));
			writer.add(xef.createAttribute("id", mi.getId()));

			// If the file lives in the same place as the OPF, no path is needed, just name
			if (mi.getParent().getAbsolutePath().equals(destinationFolderPath)) {
				writer.add(xef.createAttribute("href", mi.getFilename()));
			}
			// If the file lives in a subfolder of the OPF parent, just give the subfolder tree
			else if (mi.getAbsolutePath().startsWith(destinationFolderPath)) {
				writer.add(xef.createAttribute("href", mi.getAbsolutePath().substring(destinationFolderPath.length()+1).replace("\\", "/")));
			}
			// If the file lives somewhere altogether different, put the full path
			else {
				writer.add(xef.createAttribute("href", mi.getAbsolutePath()));
			}
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
		
		public File getParent() {
			return f.getFile().getParentFile();
		}
		
		public String getMimeTypeString() {
			return f.getMimeType().getString();
		}
		
	}
}
