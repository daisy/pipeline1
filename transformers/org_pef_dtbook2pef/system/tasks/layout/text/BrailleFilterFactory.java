package org_pef_dtbook2pef.system.tasks.layout.text;

import java.net.URL;
import java.util.HashMap;

import org_pef_dtbook2pef.system.tasks.layout.text.brailleFilters.sv_SE.CapitalizationMarkers;

/**
 * BrailleFilterFactory is a factory for braille StringFilters. It can return different StringFilters
 * depending on the requested locale. 
 * @author joha
 */
public class BrailleFilterFactory implements FilterFactory {
	private static HashMap<String, FilterLocale> locales = null;
	private StringFilter def;

	protected BrailleFilterFactory() {
		if (locales == null) {
			initTable();
		}
		def = new CombinationFilter();
	}
	
	public static BrailleFilterFactory newInstance() {
		return new BrailleFilterFactory();
	}
	
	private void initTable() {
		locales = new HashMap<String, FilterLocale>();
		putLocale("sv");
		putLocale("sv-SE");
	}
	
	private void putLocale(String str) {
		FilterLocale loc = FilterLocale.parse(str);
		locales.put(loc.toString(), loc);
	}
	
	public StringFilter getDefault() {
		return def;
	}

	/**
	 * Attempt to retrieve a StringFilter for the given locale. If none is found
	 * the default StringFilter is returned.
	 * @param target target locale
	 * @return returns a StringFilter for the given locale
	 */
	public StringFilter newStringFilter(FilterLocale target) {
		if (target.isA(locales.get("sv"))) {
			CombinationFilter filters = new CombinationFilter();
			// Remove redundant whitespace
			filters.add(new RegexFilter("(\\s+)", " "));
			// Remove zero width space
			filters.add(new RegexFilter("\\u200B", ""));
			// One or more digit followed by zero or more digits, commas or periods
			filters.add(new RegexFilter("([\\d]+[\\d,\\.]*)", "\u283c$1"));
			// Insert a "reset character" between a digit and lower case a-j
			filters.add(new RegexFilter("([\\d])([a-j])", "$1\u2831$2"));
			// Add upper case marker to the beginning of any upper case sequence
			//filters.add(new RegexFilter("(\\p{Lu}[\\p{Lu}\\u00ad]*)", "\u2820$1"));
			// Add another upper case marker if the upper case sequence contains more than one character
			//filters.add(new RegexFilter("(\\u2820\\p{Lu}\\u00ad*\\p{Lu}[\\p{Lu}\\u00ad]*)", "\u2820$1"));
			filters.add(new CapitalizationMarkers());
			// Change case to lower case
			filters.add(new CaseFilter(CaseFilter.Mode.LOWER_CASE));
			if (target.isA(locales.get("sv-SE"))) {
				// Transcode characters
				filters.add(new CharFilter(getResource("tables/sv_SE.xml")));
			}
			return filters;
		}
		// use default
		return def;
	}
	
	public void setDefault(FilterLocale locale) {
		def = newStringFilter(locale);
	}
	
	public void setDefault(StringFilter filter) {
		def = filter;
	}

	/**
	 * Retrieve a URL of a resource associated with this transformer.
	 * <p>This method is preferred to {@link #getTransformerDirectory()} since
	 * it supports jarness.</p>
	 */
	final protected URL getResource(String subPath) throws IllegalArgumentException {
		//TODO check the viability of this method
		URL url;
	    url = this.getClass().getResource(subPath);
	    if(null==url) {
	    	String qualifiedPath = this.getClass().getPackage().getName().replace('.','/') + "/";	    	
	    	url = this.getClass().getClassLoader().getResource(qualifiedPath+subPath);
	    }
	    if(url==null) throw new IllegalArgumentException(subPath);
	    return url;
	}

}
