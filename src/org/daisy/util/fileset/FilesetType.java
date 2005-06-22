package org.daisy.util.fileset;

/**
 * Gathers constants that represent those types of filesets that this package claims to support
 * @author Markus Gylling
 */
public final class FilesetType {
	private FilesetType() {}          
    public static final FilesetType DAISY_202 = new FilesetType();
    public static final FilesetType Z3986 = new FilesetType();
    public static final FilesetType OTHER = new FilesetType();
    
    public static FilesetType parse(String type) throws IllegalArgumentException {
        if ("DAISY_202".equals(type)) {
            return DAISY_202;
        }
        if ("Z3986".equals(type)) {
            return Z3986;
        }
        if ("OTHER".equals(type)) {
            return OTHER;
        }
        throw new IllegalArgumentException("FileSet type must be one of DAISY_202, Z3986 or OTHER");
    }
	
}
