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
