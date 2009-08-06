package int_daisy_metadataEditor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.daisy.util.fileset.FilesetType;

/**
 * A factory and a cache for {@link MetadataEditor} implementations given a
 * {@link FilesetType}.
 * 
 * <p>
 * This factory currently supports the following types:
 * </p>
 * <ul>
 * <li>{@link FilesetType#DTBOOK_DOCUMENT}</li>
 * <li>{@link FilesetType#DAISY_202}</li>
 * <li>{@link FilesetType#XHTML_DOCUMENT}</li>
 * <li>{@link FilesetType#Z3986}</li>
 * </ul>
 * 
 * @author Romain Deltour
 * 
 */
public class MetadataEditorFactory {

	private ConcurrentMap<FilesetType, MetadataEditor> cache = new ConcurrentHashMap<FilesetType, MetadataEditor>();

	/**
	 * Creates a new metadata editor able to edit metadata in a fileset of the
	 * given type.
	 * 
	 * @param type
	 *            the fileset type for which to create the metadata editor.
	 * @return a new metadata editor
	 */
	public MetadataEditor newEditor(FilesetType type) {
		switch (type) {
		case DTBOOK_DOCUMENT:
			return new XSLTMetadataEditor(getClass().getResource(
					"xslt/dtbook-metadata-editor.xsl"));
		case DAISY_202:
			return new XSLTMetadataEditor(getClass().getResource(
					"xslt/html-metadata-editor.xsl"));
		case XHTML_DOCUMENT:
			return new XSLTMetadataEditor(getClass().getResource(
					"xslt/html-metadata-editor.xsl"));
		case Z3986:
			return new XSLTMetadataEditor(getClass().getResource(
					"xslt/opf-metadata-editor.xsl"));
		default:
			return null;
		}
	}

	/**
	 * Returns a cached instance of a metadata editor able to edit metadata in a
	 * fileset of the given type.
	 * 
	 * @param type
	 *            the fileset type for which to create the metadata editor.
	 * @return a cached metadata editor
	 */
	public MetadataEditor getEditor(FilesetType type) {
		if (!cache.containsKey(type)) {
			cache.putIfAbsent(type, newEditor(type));
		}
		return cache.get(type);
	}
}
