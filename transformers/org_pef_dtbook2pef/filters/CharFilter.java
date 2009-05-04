package org_pef_dtbook2pef.filters;

import java.net.URL;

import org.daisy.util.i18n.UCharReplacer;

/**
 * Implements StringFilter using UCharReplacer.
 * 
 * @author  Joel Hakansson
 * @version 4 maj 2009
 * @since 1.0
 */
public class CharFilter implements StringFilter {
	private UCharReplacer ucr;
	
	/**
	 * Create a new CharFilter
	 * @param table relative path to replacement table, see UCharReplacement for more information
	 */
	public CharFilter(String table) {
		this.ucr = new UCharReplacer();
		try {
			this.ucr.addSubstitutionTable(getTransformerDirectoryResource(table));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String replace(String str) {
		return ucr.replace(str).toString();
	}

	final protected URL getTransformerDirectoryResource(String subPath) throws IllegalArgumentException {
		//TODO check the viability of this method
		URL url;
	    url = this.getClass().getResource(subPath);
	    if(null==url) {
	    	String qualifiedPath = this.getClass().getPackage().getName().replace('.','/') + "/";	    	
	    	url = this.getClass().getClassLoader().getResource(qualifiedPath+subPath);
	    }
	    if(url==null) throw new IllegalArgumentException(subPath + " in CharFilter");
	    return url;
	}
}
