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
package se_tpb_xmldetection;

import org.daisy.util.exception.BaseException;

/**
 * @author Linus Ericson
 */
public class UnsupportedDocumentTypeException extends BaseException {
	
	protected String publicId = null;
    protected String systemId = null;
    
    /**
     * @param message
     */
    public UnsupportedDocumentTypeException(String message) {
        super(message);
    }

    /**
     * @param message
     */
    public UnsupportedDocumentTypeException(String message, String pub, String sys) {
        super(message);
        publicId = pub;
        systemId = sys;
    }

    public String getMessage() {        
        return super.getMessage() + " PublicID: " + publicId + ", SystemID: " + systemId;
    }
    
    public String getPublicId() {
        return publicId;
    }
    
    public String getSystemId() {
        return systemId;
    }
    
    private static final long serialVersionUID = -7681816801264467561L;
}
