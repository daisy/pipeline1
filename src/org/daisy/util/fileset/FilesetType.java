/*
 * org.daisy.util - The DAISY java utility library
 * Copyright (C) 2005  Daisy Consortium
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

package org.daisy.util.fileset;

/**
 * Gathers constants that represent those types of filesets that this package claims to support
 * @author Markus Gylling
 */
public final class FilesetType {
	private FilesetType() {}          
    public static final FilesetType DAISY_202 = new FilesetType();
    public static final FilesetType Z3986 = new FilesetType();
    public static final FilesetType NIMAS = new FilesetType();
    public static final FilesetType Z3986_RESOURCEFILE = new FilesetType();
    public static final FilesetType XHTML_DOCUMENT = new FilesetType();
    public static final FilesetType HTML_DOCUMENT = new FilesetType();
    public static final FilesetType DTBOOK_DOCUMENT = new FilesetType();
    public static final FilesetType CSS = new FilesetType();
    public static final FilesetType PLAYLIST_M3U = new FilesetType();
    public static final FilesetType PLAYLIST_PLS = new FilesetType();
    public static final FilesetType UNKNOWN = new FilesetType();
    
    public String toString(){
    	if (this.equals(FilesetType.CSS)) {
    		return "CSS";
    	}else
    	if (this.equals(FilesetType.DAISY_202)) {
    		return "DAISY_202";
    	}else
        if (this.equals(FilesetType.DTBOOK_DOCUMENT)) {
        		return "DTBOOK_DOCUMENT";
       	}else
       	if (this.equals(FilesetType.HTML_DOCUMENT)) {
        		return "HTML_DOCUMENT";
       	}else
       	if (this.equals(FilesetType.NIMAS)) {
        		return "NIMAS";
       	}else
       	if (this.equals(FilesetType.PLAYLIST_M3U)) {
        		return "PLAYLIST_M3U";
       	}else
       	if (this.equals(FilesetType.PLAYLIST_PLS)) {
        		return "PLAYLIST_PLS";
       	}else
       	if (this.equals(FilesetType.XHTML_DOCUMENT)) {
        		return "XHTML_DOCUMENT";
       	}else
       	if (this.equals(FilesetType.Z3986)) {
        		return "Z3986";
       	}else 
       	if (this.equals(FilesetType.Z3986_RESOURCEFILE)) {
        		return "Z3986_RESOURCEFILE";
       	}else 
       	if (this.equals(FilesetType.UNKNOWN)) {
        		return "UNKNOWN";
       	}
	
    	return null;
    }
    
    public static FilesetType parse(String type) throws IllegalArgumentException {
        if ("DAISY_202".equals(type)) {
            return DAISY_202;
        }else
        if ("Z3986".equals(type)) {
            return Z3986;
        }else
        if ("NIMAS".equals(type)) {
            return NIMAS;
        }else
        if ("Z3986_RESOURCEFILE".equals(type)) {
            return Z3986_RESOURCEFILE;
        }else
        if ("XHTML_DOCUMENT".equals(type)) {
            return XHTML_DOCUMENT;
        }else
        if ("HTML_DOCUMENT".equals(type)) {
            return HTML_DOCUMENT;
        }else
        if ("DTBOOK_DOCUMENT".equals(type)) {
            return DTBOOK_DOCUMENT;
        }else
        if ("CSS".equals(type)) {
            return CSS;
        }else
        if ("PLAYLIST_M3U".equals(type)) {
            return PLAYLIST_M3U;
        }else
        if ("PLAYLIST_PLS".equals(type)) {
            return PLAYLIST_PLS;
        }
        else
            if ("UNKNOWN".equals(type)) {
                return UNKNOWN;
            }
        throw new IllegalArgumentException("FileSet type must be one of DAISY_202, Z3986, NIMAS, Z3986_RESOURCEFILE, HTML_DOCUMENT, XHTML_DOCUMENT, DTBOOK_DOCUMENT, CSS, PLAYLIST_M3U, PLAYLIST_PLS, UNKNOWN");
    }
	
}
