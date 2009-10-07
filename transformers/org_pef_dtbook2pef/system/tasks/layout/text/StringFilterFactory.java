package org_pef_dtbook2pef.system.tasks.layout.text;

import java.net.URL;
import java.util.HashMap;

/**
 * StringFilterFactory is a factory for StringFilters. It can return different StringFilters
 * depending on the requested locale. 
 * @author joha
 *
 */
public class StringFilterFactory {
	private static HashMap<String, Locale2> locales = null;
	private StringFilter def;

	protected StringFilterFactory() {
		if (locales == null) {
			initTable();
		}
		def = new CombinationFilter();
	}
	
	public static StringFilterFactory newInstance() {
		return new StringFilterFactory();
	}
	
	private void initTable() {
		locales = new HashMap<String, Locale2>();
		putLocale("sv");
		putLocale("sv-SE");
	}
	
	private void putLocale(String str) {
		Locale2 loc = Locale2.parse(str);
		locales.put(loc.toString(), loc);
	}
	
	/**
	 * Get the default StringFilter
	 * @return
	 */
	public StringFilter newStringFilter() {
		return def;
	}

	/**
	 * Attempt to retrieve a StringFilter for the given locale. If none is found
	 * the default StringFilter is returned.
	 * @param target target locale
	 * @return returns a StringFilter for the given locale
	 */
	public StringFilter newStringFilter(Locale2 target) {
		if (target.isA(locales.get("sv"))) {
			CombinationFilter filters = new CombinationFilter();
			// Remove redundant whitespace
			filters.add(new RegexFilter("(\\s+)", " "));
			// Remove zero width space
			filters.add(new RegexFilter("\\u200B", ""));
			// One or more digit followed by zero or more digits, commas or periods
			filters.add(new RegexFilter("([\\d]+[\\d,\\.]*)", "\u283c$1"));
			// Add upper case marker to the beginning of any upper case sequence
			filters.add(new RegexFilter("(\\p{Lu}[\\p{Lu}\\u00ad]*)", "\u2820$1"));
			// Add another upper case marker if the upper case sequence contains more than one character
			filters.add(new RegexFilter("(\\u2820\\p{Lu}\\u00ad*\\p{Lu}[\\p{Lu}\\u00ad]*)", "\u2820$1"));
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
	
	public void setDefault(Locale2 locale) {
		def = newStringFilter(locale);
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
	
	/*
	// test 
	public static void main(String[] args) {
		Locale2 sv = Locale2.parse("sv");
		Locale2 sv_se = Locale2.parse("sv-se");
		Locale2 sv_fi = Locale2.parse("sv-FI");
		Locale2 l = Locale2.parse("sv-SE-test");
		System.out.println(l + " " + l.getLanguage() + " " +l.getCountry() + " " + l.getVariant());
		testIsa(l, sv);
		testIsa(l, sv_se);
		testIsa(l, l);
		testIsa(l, sv_fi);
		StringFilterFactory.newInstance().newStringFilter(sv);
		System.out.println(
		StringFilterFactory.newInstance().newStringFilter(sv_se).filter("test"));
	}
	
	public static void testIsa(Locale2 l1, Locale2 l2) {
		System.out.println(l1 + (l1.isA(l2) ? " is a " : " is not a ")  + l2);
	}
	*/

}
