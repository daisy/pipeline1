/**
 * 
 */
package org.daisy.util.dtb.build;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import org.daisy.util.dtb.meta.MetadataItem;
import org.daisy.util.dtb.meta.MetadataList;
import org.daisy.util.fileset.FilesetFile;

/**
 * Builds and renders an OPF file
 * @author James Pritchett (jpritchett@rfbd.org)
 *
 */

public class OpfBuilder {
	OpfType mOutputType;
	private MetadataList myMetadata;
	private HashMap<String, FilesetFile> myManifest;	// Files keyed to id
	private LinkedHashMap<String, FilesetFile> mySpine;	// Files keyed to id (same as in manifest)
	
	/**
	 * Generic constructor
	 * @param type The type of OPF to create (from enum)
	 */
	public OpfBuilder(OpfType type) {
		mOutputType = type;
	}
	
	/**
	 * Construct an OpfBuilder will full dataset
	 * @param type The type of OPF to create (from enum)
	 * @param metadata The metadata items to use
	 * @param manifest URLs of files to include in the manifest
	 * @param spine URLs of files to include in the spine, in order
	 */
	public OpfBuilder(OpfType type, MetadataList metadata, Set<URL> manifest, LinkedHashSet<URL> spine) {
		mOutputType = type;
		
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
		myMetadata = metadata;
	}
	
	/**
	 * Set all manifest files at once
	 * @param manifest The complete list of files (URLs) to include in the manifest
	 */
	public void setManifest(Set<URL> manifest) {
		// TODO:  Code this
		throw new UnsupportedOperationException("Method not yet implemented");

		// For each URL given, convert to an appropriate FilesetFile (if possible) and add to
		// 	myManifest keyed to an id
	}
	
	/**
	 * Set all spine items at once
	 * @param spine The complete list of files (URLs) to include in the spine
	 */
	public void setSpine(LinkedHashSet<URL> spine) {
		// TODO:  Code this
		throw new UnsupportedOperationException("Method not yet implemented");

		// For each URL given, look up the file in myManifest, and if present, add to the spine.
		// If not in manifest, add it.
	}
	
	/**
	 * Add a single metadata item to the OPF
	 * @param item The MetadataItem to add
	 */
	public void addMetadataItem(MetadataItem item) {
		if (myMetadata == null) { myMetadata = new MetadataList(); }

		myMetadata.add(item);
	}
	
	/**
	 * Add a single metadata item to the OPF
	 * @param name The name of the metadata item
	 * @param value The value of the metadata item
	 */
	public void addMetadataItem(String name, String value) {
		// TODO:  Code this
		throw new UnsupportedOperationException("Method not yet implemented");

		// First create the item
		
			// If it's a DC item, it gets named as such
			// If it's anything else, it's a meta element with @name and @content

		// Then add it to the list
	}
	
	/**
	 * Add a single resource to the manifest
	 * @param resource URL of resource to add
	 */
	public void addManifestItem(URL resource) {
		// TODO:  Code this
		throw new UnsupportedOperationException("Method not yet implemented");
		
		//  MG:  can use org.daisy.util.file.detect to retrieve a mime type
		
		// Create the FilesetFile item
		
		// Detect MIME type and set accordingly
		
		// Then add it to the list
	}
	
	/**
	 * Add a single file to the manifest
	 * @param file The FilesetFile to add
	 */
	public void addManifestItem(FilesetFile file) {
		if (myManifest == null) { myManifest = new HashMap<String, FilesetFile>(); }
		
		// TODO:  Code this
		throw new UnsupportedOperationException("Method not yet implemented");

		// Add item as is, with an id key
	}
	
	/**
	 * Add a single resource to the end of the spine; add to manifest if not already there
	 * @param resource URL of resource to add
	 */
	public void addSpineItem(URL resource) {
		if (mySpine == null) { mySpine = new LinkedHashMap<String, FilesetFile>(); }
		
		// TODO:  Code this
		throw new UnsupportedOperationException("Method not yet implemented");

		// First, look up the URL in the manifest
		// If not there, create it as a manifest item
		// Add to spine, keyed to id
	}
	
	/**
	 * Add a single file to the end of the spine; add to manifest if not already there
	 * @param file The FilesetFile to add
	 */
	public void addSpineItem(FilesetFile file) {
		if (mySpine == null) { mySpine = new LinkedHashMap<String, FilesetFile>(); }

		// TODO:  Code this
		throw new UnsupportedOperationException("Method not yet implemented");

		// First look up the item in the manifest
		// If not there, add it to manifest
		// Add to spine, keyed to id
	}
	
	/**
	 * Render the OPF as an XML document
	 * @param destination Output file URL
	 * @throws IOException
	 */
	public void render(URL destination) throws IOException {
		// TODO:  code this
		throw new UnsupportedOperationException("Method not yet implemented");

		// Render the prolog
		
		// Root element:  needs uid
			// Exception if not found
		
		// Metadata items (DC: and X)
			// Exception if required items not found
		
		// Manifest
			// Exception if no files in manifest
		
		// Spine
			// Exception if no files in manifest
			// Exception if any files of wrong type
	}
	
	public enum OpfType {
		OPF_20_10, 		//http://www.openebook.org/2007/opf/OPF_2.0_final_spec.html
		Z3986_2005,
		NIMAS;
	}
}
