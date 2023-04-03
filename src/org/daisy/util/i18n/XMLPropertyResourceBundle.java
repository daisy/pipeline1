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
package org.daisy.util.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * A reshrouding of <code>java.util.ResourceBundle</code>.
 * <p>The following properties characterize this implementation:</p>
 * <ol>
 * 	<li>Supporting <code>Properties</code> in both the JDK 1.5 XML and plain text formats.</li>
 * 	<li>An override and a new overload of <code>getBundle</code> to circumvent problems when using 
 * 			classLoaders on jars that are not on the classpath.</li>
 * </ol>
 * <p>To enjoy the features above, users need to create ResourceBundle instances using the <code>getBundle(String, 
 * Locale, ClassLoader)</code> or <code>getBundle(URL, Locale)</code> methods.</p>
 * @author Markus Gylling
 */

public class XMLPropertyResourceBundle extends ResourceBundle {
	
	private Locale mLocale = null;							//user provided locale
	private XMLProperties mProperties = null; 				//inner carrier of the read data
	
	/**
	 * Creational method, overriding the only non final <code>getBundle</code> 
	 * method in <code>ResourceBundle</code>.
	 */
	public static ResourceBundle getBundle(String baseName, Locale locale, ClassLoader loader) {	
		try {			
			return initialize(baseName, locale, loader);
		} catch (IOException e) {
			throw new MissingResourceException(baseName,null,baseName);
		}	
	}
	
	/**
	 * Creational method, extending the <code>ResourceBundle</code> API.
	 */
	public static ResourceBundle getBundle(URL baseBundle, Locale locale) {	
		try {			
			return initialize(baseBundle, locale);
		} catch (IOException e) {
			throw new MissingResourceException(baseBundle.toExternalForm(),null,baseBundle.toExternalForm());
		}	
	}
	
	private static XMLPropertyResourceBundle initialize(String baseName, Locale locale, ClassLoader loader) throws IOException {
		if(baseName==null) throw new NullPointerException("baseName");
		if(locale==null) throw new NullPointerException("locale");
		if(loader==null) throw new NullPointerException("loader");
		
		//generate a list of candidate bundle names
		List<String> candidateNames = generateCandidateNameList(baseName, locale);
		
		//generate a list of actually existing bundles
		List<URL> existingBundles = generateExistingBundleList(candidateNames, loader);
		
		//if we have no existing bundles, bail out
		if(existingBundles==null) throw new MissingResourceException(baseName,null,baseName);
	
		//return the bundle
		return new XMLPropertyResourceBundle(existingBundles, 0, locale);
	}

	private static XMLPropertyResourceBundle initialize(URL baseBundle, Locale locale) throws IOException {
		if(baseBundle==null) throw new NullPointerException("baseName");
		if(locale==null) throw new NullPointerException("locale");
		
		//generate a list of candidate bundle names
		List<String> candidateURLs = generateCandidateNameList(baseBundle.toExternalForm(), locale);
		
		//generate a list of actually existing bundles
		List<URL> existingBundles = generateExistingBundleList(candidateURLs);
		
		//if we have no existing bundles, bail out
		if(existingBundles==null) throw new MissingResourceException(baseBundle.toExternalForm(),null,baseBundle.toExternalForm());
	
		//return the bundle
		return new XMLPropertyResourceBundle(existingBundles, 0, locale);
	}
	
	/**
	 * Constructor.
	 * @param existingBundles a List of properties files confirmed to exist
	 * @param bundle The propertiesfile to load, expressed as a position in the existingBundles list 
	 * @param locale The user requested locale
	 * @throws IOException  
	 */
	private XMLPropertyResourceBundle(List<URL> existingBundles, int bundle, Locale locale) throws IOException {
		super();
		assert(existingBundles.size() > bundle);
		
		mLocale = locale;
		mProperties = new XMLProperties();

		URL url = existingBundles.get(bundle);
		
		try {
			//try the xml load
			mProperties.loadFromXML(url.openStream());
		} catch (Exception e) {
			//try the non-XML load
			mProperties.load(url.openStream());
		}
				
		if(existingBundles.size()>(bundle+1)) {
			setParent(new XMLPropertyResourceBundle(existingBundles, bundle+1, locale));
		}		
	}
		
	@Override
	public Enumeration<String> getKeys() {        		
        return new ResourceBundleEnumeration(mProperties.keySet(),
                (this.parent != null) ? this.parent.getKeys() : null);
	}

	@Override
	protected Object handleGetObject(String key) {
		return mProperties.get(key);		
	}
	
	@Override
	public Locale getLocale() {		
		return mLocale;	
	}
		
	/**
	 * Generate an ordered list of existing bundles
	 * @return null if zero existing bundles were found, else the list
	 */
	private static List<URL> generateExistingBundleList(List<String> candidateNames, ClassLoader loader) {
		List<URL> list = null;		
		for (String name : candidateNames) {						
			URL url = loader.getResource(name);
			if(url!=null) {		
				if(null==list)list=new LinkedList<URL>();
				list.add(url);
			}	
		}				
		return list;
	}

	/**
	 * Generate an ordered list of existing bundles
	 * @return null if zero existing bundles were found, else the list
	 */
	private static List<URL> generateExistingBundleList(List<String> candidateURLs)  {
		List<URL> list = null;		
		for (String name : candidateURLs) {						
			URL url;				
			try{
				url = new URL(name);
				InputStream is = url.openStream();	
				is.close();
			}catch (Exception e) {
				continue;
			}						
			if(null==list)list=new LinkedList<URL>();
			list.add(url);
		}				
		return list;
	}
	
	/**
	 * Generate an ordered list of candidate bundle names
	 */
	private static List<String> generateCandidateNameList(String baseName, Locale locale) {
		List<String> list = new LinkedList<String>();		
		String ext = getExtension(baseName);		
		String path = baseName.subSequence(0, baseName.length()-ext.length()).toString();
				
		addToCandidateList(locale, list, path, ext);
		
		if(!locale.equals(Locale.getDefault())) {
			addToCandidateList(Locale.getDefault(), list, path, ext);
		}
					
		list.add(baseName);
	
		return list;
	}

	private static List<String> addToCandidateList(Locale locale, List<String> list, String path, String ext) {
		String language = locale.getLanguage();
		String country = locale.getCountry();
		String variant = locale.getVariant();
		
		StringBuilder sb = new StringBuilder();
		
		if(language.length()>0 && country.length()>0 && variant.length()>0) {			
			list.add((sb.append(path).append('_').append(language).append('_').append(country).append('_').append(variant).append(ext)).toString());
			sb.delete(0, sb.length());
		}

		if(language.length()>0 && country.length()>0) {
			list.add((sb.append(path).append('_').append(language).append('_').append(country).append(ext)).toString());
			sb.delete(0, sb.length());
		}

		if(language.length()>0) {			
			list.add((sb.append(path).append('_').append(language).append(ext)).toString());
		}
		
		return list;
	}

	/**
	 * @return the substring from the last occurence of a period character inclusive. If
	 * no period char in inparam string, return the empty string
	 */
	private static String getExtension(String name) {
		StringBuilder sb = new StringBuilder();		
		int start = name.lastIndexOf('.');
		if(start > 0) {
			for (int i = 0; i < name.length(); i++) {
				if(i>=start){
					sb.append(name.charAt(i));
				}
			}
			return sb.toString();
		}
		return "";
	}
	
	class ResourceBundleEnumeration implements Enumeration<String> {

	    Set<String> set;
	    Iterator<String> iterator;
	    Enumeration<String> enumeration; // may remain null

	    /**
	     * Constructs a resource bundle enumeration.
	     * @param set an set providing some elements of the enumeration
	     * @param enumeration an enumeration providing more elements of the enumeration.
	     *        enumeration may be null.
	     */
	    public ResourceBundleEnumeration(Set set, Enumeration<String> enumeration) {
	        this.set = set;
	        this.iterator = set.iterator();
	        this.enumeration = enumeration;
	    }

	    String next = null;
	            
	    public boolean hasMoreElements() {
	        if (next == null) {
	            if (iterator.hasNext()) {
	                next = iterator.next();
	            } else if (enumeration != null) {
	                while (next == null && enumeration.hasMoreElements()) {
	                    next = enumeration.nextElement();
	                    if (set.contains(next)) {
	                        next = null;
	                    }
	                }
	            }
	        }
	        return next != null;
	    }

	    public String nextElement() {
	        if (hasMoreElements()) {
	            String result = next;
	            next = null;
	            return result;
	        } 
	        throw new NoSuchElementException();
	        
	    }
	}
}