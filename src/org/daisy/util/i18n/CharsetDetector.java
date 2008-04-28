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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;

import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;
import org.mozilla.intl.chardet.nsPSMDetector;

/**
 * <p>A utility wrapper around the jchardet port of the Mozilla character set (encoding) detection library (http://jchardet.sourceforge.net).<br/>
 * Usage:</p>
 * <pre><code>
 * 	CharsetDetector det = new CharsetDetector();
 *	String charset = det.detect(url);
 * </code></pre>
 * <p>If the detect method returns null, it is possible to retrieve an array of probable charsets:</p>
 * <pre><code>
 * 	CharsetDetector det = new CharsetDetector();
 *	String charset = det.detect(url);
 *	if(null==charset) {
 *		String[] probable = det.getProbableCharsets();
 *	}
 *	//...
 * </code></pre>
 * <p>...or an entry from the probable charsets which matches the charset of the current locale:</p>
 * <pre><code>
 * 	CharsetDetector det = new CharsetDetector();
 *	String charset = det.detect(url);
 *	if(null==charset) {
 *		charset = det.getProbableCharsetUsingLocale();
 *	}
 *	//...
 * </code></pre>
 * @author Markus Gylling
 */
public class CharsetDetector implements nsICharsetDetectionObserver {

	private String detectedCharset;
	private String[] probableCharsets;
	private boolean isAscii = true ;
	
	public CharsetDetector() {
		
	}

	/**
	 * Default call method.
	 * @param url
	 * 		URL of resource to detect charset for.
	 * @return the charset string if a charset was detected, else null.
	 * @throws IOException 
	 */
	public String detect(URL url) throws IOException {
		return detect(url,nsPSMDetector.ALL);		
	}

	/**
	 * Extended call method.
	 * @param url
	 * 		URL of resource to detect charset for
	 * @param lang
	 * 		A static constant from {@link org.mozilla.intl.chardet.nsPSMDetector}
	 * @return the charset string if a charset was detected, else null.
	 * @throws IOException 
	 */

	public String detect(URL url, int lang) throws IOException {
		
		detectedCharset = null;
		probableCharsets = null;
		
		nsDetector det = new nsDetector(lang);
		det.Init(this);
		BufferedInputStream imp = new BufferedInputStream(url.openStream());
		byte[] buf = new byte[1024] ;
        int len;
        boolean done = false;        

        while((len=imp.read(buf,0,buf.length)) != -1) {
                // Check if the stream is only ascii.
                if (isAscii) {
                    isAscii = det.isAscii(buf,len);
                }    

                // DoIt if not done yet.
                if (!done) {
                    done = det.DoIt(buf,len, false);
                }
                
                if(done) break;
        }
        det.DataEnd();

        probableCharsets = det.getProbableCharsets();
        imp.close();
		return detectedCharset;
	}

	/**
	 * The observer interface implementation used for callbacks from jchardet.
	 */
	public void Notify(String charset) {
		detectedCharset = charset;		
	}

	/**
	 * @return a string representing detected charset if a charset was detected, else null.
	 */
	public String getDetectedCharset() {
			return detectedCharset;
	}
	
	/**
	 * Use this method if getDetectedCharset() returns null.
	 * @return an array of probable charset strings if such were detected, else null.
	 */

	public String[] getProbableCharsets() {
		return probableCharsets;
	}
	
	/**
	 * Use this method if getDetectedCharset() returns null and you want to know
	 * if only ascii characters were detected in the document.
	 * @return true if only ascii characters were detected, false otherwise
	 */
	public boolean isAscii() {
		return isAscii;
	}
	
	/**
	 * Use this utility method to retrieve the possibly existing match between
	 * one entry in the probableCharsets array, and the default charset of the current locale.
	 * @return the charset string if a match between locale and probable was found, null otherwise.
	 */
	public String getProbableCharsetUsingLocale() throws IllegalCharsetNameException, UnsupportedCharsetException {
		String localeCs = Charset.defaultCharset().toString();		
		if(probableCharsets!=null) {
			//first match on the bare strings case insensitive
			for (int i = 0; i < probableCharsets.length; i++) {
				if(localeCs.equalsIgnoreCase(probableCharsets[i])){
					return probableCharsets[i];					
				}
			}//for	
			
			//then match using Charset
			Charset loc = Charset.forName(localeCs);
			for (int i = 0; i < probableCharsets.length; i++) {	
				if(Charset.isSupported(probableCharsets[i])) {
					Charset test = Charset.forName(probableCharsets[i]);					
					if ((loc.compareTo(test)==0)||(test.aliases().contains(loc))){
						return test.toString();
					}					
				}//if(Charset.isSupported
			}//for				
		}//if(probableCharsets!=null)
		
		return null;
	}
	
}
