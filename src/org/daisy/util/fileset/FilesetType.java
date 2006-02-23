package org.daisy.util.fileset;

/**
 * Gathers constants that represent those types of filesets that this package claims to support
 * @author Markus Gylling
 */
public final class FilesetType {
	private FilesetType() {}          
    public static final FilesetType DAISY_202 = new FilesetType();
    public static final FilesetType Z3986 = new FilesetType();
    public static final FilesetType Z3986_RESOURCEFILE = new FilesetType();
    public static final FilesetType XHTML_DOCUMENT = new FilesetType();
    public static final FilesetType DTBOOK_DOCUMENT = new FilesetType();
    public static final FilesetType CSS = new FilesetType();
    public static final FilesetType OTHER = new FilesetType();
    
    public static FilesetType parse(String type) throws IllegalArgumentException {
        if ("DAISY_202".equals(type)) {
            return DAISY_202;
        }
        if ("Z3986".equals(type)) {
            return Z3986;
        }
        if ("Z3986_RESOURCEFILE".equals(type)) {
            return Z3986_RESOURCEFILE;
        }
        if ("XHTML_DOCUMENT".equals(type)) {
            return XHTML_DOCUMENT;
        }
        if ("DTBOOK_DOCUMENT".equals(type)) {
            return DTBOOK_DOCUMENT;
        }
        if ("CSS".equals(type)) {
            return CSS;
        }

        if ("OTHER".equals(type)) {
            return OTHER;
        }
        throw new IllegalArgumentException("FileSet type must be one of DAISY_202, Z3986, Z3986_RESOURCEFILE, XHTML_DOCUMENT, DTBOOK_DOCUMENT, CSS or OTHER");
    }
	
}
