/*
 * org.daisy.util (C) 2005-2008 Daisy Consortium
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
package org.daisy.util.fileset.encryption;

/**
 * Enumeration constant provider; referencing a wellknown encryption type/algorithm.
 * @author Markus Gylling
 */
public class EncryptionType {
	private EncryptionType() {}          
    public static final EncryptionType PDTB_1_0_0 = new EncryptionType();
    public static final EncryptionType PDTB_2_0_0 = new EncryptionType();
 
    /**
     * Returns the canonical name constant for this encryption type in string form.
     */
    public String toString(){
    	if (this.equals(EncryptionType.PDTB_1_0_0)) {
    		return "PDTB_1_0_0";
    	}else
    	if (this.equals(EncryptionType.PDTB_2_0_0)) {
        		return "PDTB_2_0_0";
        }
    	return null;
    }	
    
    public static EncryptionType parse(String type) throws IllegalArgumentException {
        if ("PDTB_1_0_0".equals(type)) {
            return PDTB_1_0_0;
        }else if ("PDTB_1_0_0".equals(type)) {
            return PDTB_1_0_0;
        }
        throw new IllegalArgumentException("FileSet type must be one of PDTB_1_0_0, PDTB_2_0_0");
    }
    
}
