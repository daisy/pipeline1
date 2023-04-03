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
package se_tpb_speechgen2.tts.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.i18n.UCharReplacer;
import org.daisy.util.xml.XPathUtils;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.TransformerCache;
import org.daisy.util.xml.xslt.XSLTException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import se_tpb_speechgen2.tts.TTSBuilder;
import se_tpb_speechgen2.tts.TTSConstants;
import se_tpb_speechgen2.tts.TTSInput;

/**
 * Utility functions for tts implementations.
 * 
 * @author Martin Blomberg
 *
 */
public class TTSUtils {

	private Map<String,String> parameters;									// custom parameters
	private RegexReplace[] specificReplace;					// regexes: optional, applied before general regexes
	private RegexReplace[] regexReplace;					// regexes: optional, applied after specific regexes
	private UCharReplacer charReplacer;						// replacement of characters
	private String xsltFilename;							// xsl transformation of each sync point
	private TransformerCache cache = new TransformerCache();// for fast loading of the xslt
	private static final String XSLT_FACTORY = "net.sf.saxon.TransformerFactoryImpl";	// choose the right transformer factory
	private YearAnnouncer yearAnnouncer;					//  speaking years properly


	/**
	 * Constructor taking a map of parameters/properties as parameter. 
	 * The parameters are parsed as follows:
	 * <dl>
	 * 	<dt>timeout</dt>
	 * 	<dd>the timeout value in millis.</dd>
	 * 
	 * 	<dt>command</dt>
	 * 	<dd>the external command (if any) used to initialize the tts system.</dd>
	 * 
	 * 	<dt>class</dt>
	 * 	<dd>the tts class to use</dd>
	 * 
	 * 	<dt>xslt</dt>
	 * 	<dd>a comma separated list of urls to xslt files</dd>
	 * 
	 * 	<dt>regex</dt>
	 * 	<dd>a comma separated list of urls to regex files</dd>
	 * 
	 * 	<dt>year</dt>
	 * 	<dd>a url to a file containing regex handling years</dd>
	 * 
	 * 	<dt>characterSubstitutionTables</dt>
	 * 	<dd>a comma separated list of urls to character translation tables.</dd>
	 * 
	 * </dl>
	 * Additional parameters may be present in the map.
	 * 
	 * @param params A java.util.Map containing parameters.
	 */
	public TTSUtils(Map<String,String> params) {
		parameters = params;
		init();
	}	

	/**
	 * A DOM containing text to read.
	 * @param doc A part of the manuscript identified as a point of synchronization.
	 */
	public void expandAbbrs(Document doc) {
		NodeList abbrs = XPathUtils.selectNodes(doc.getDocumentElement(), "//*[@exp]");
		// if supplied, use exp attributes instead of text nodes
		for (int i = 0; i < abbrs.getLength(); i++) {	
			Element elem = (Element) abbrs.item(i);
			Node textNode = doc.createTextNode(elem.getAttribute("exp"));
			elem.getParentNode().insertBefore(textNode, elem);
			elem.getParentNode().removeChild(elem);
		}
	}

	/**
	 * Parses roman numerals, that is, pagenum elements with attribute
	 * <code>page="front"</code>. The numerals are parsed and substituted
	 * for arabic numbers.
	 * @param doc the xml fragment to work on.
	 */
	public void parseRomanNumerals(Document doc) {
		// parse all roman numerals
		NodeList frontNums = XPathUtils.selectNodes(doc.getDocumentElement(), "//pagenum[@page='front' or @page='special']");
		for (int i = 0; i < frontNums.getLength(); i++) {	
			Element elem = (Element) frontNums.item(i);
			String str = elem.getTextContent().trim();
			// rd20080426: only parse the numeral as roman if it really is
			if (RomanNumeral.isRoman(str)){
				str = String.valueOf(RomanNumeral.parseInt(str));
				elem.setTextContent(str);
			}
		}
	}
	
	
	/**
	 * Concatenates the value of the attribute named <code>attrName</code>
	 * for all elements in <code>startElements</code>.
	 * @param startElements
	 * @param attrName the name of the attribute whoose values to 
	 * concatenate.
	 * @return the value of the attribute named <code>attrName</code>
	 * for all elements in <code>startElements</code>.
	 */
	public static String concatAttributes(List<StartElement> startElements, QName attrName) {
		String announcements = "";
		for (int i = 0; i< startElements.size(); i++) {
			StartElement se = startElements.get(i);
//			jpritchett@rfbd.org:  For some reason, the following doesn't work all the time.
//			Attribute at = se.getAttributeByName(attrName);
//			if (at != null) {
//			announcements += at.getValue() + " ";
//			}
//			Replaced with the following do-it-yourself iteration, 14 Aug 2006
			for (Iterator<?> atIt = se.getAttributes(); atIt.hasNext(); ) {
				Attribute at = (Attribute) atIt.next();
				if (attrName.equals(at.getName())) {
					announcements += at.getValue() + " ";
					break;
				}
			}
		}

		return announcements;
	}


	/*
	 * Receives a Map with parameters. In this implementation,
	 * the values are all but one - characterFallbackStates - 
	 * filenames and the keys are:
	 * <ol>
	 * <li>generalRegexFilename</li>
	 * <li>specificRegexFilename</li>
	 * <li>yearFilename</li>
	 * <li>xsltFilename</li>
	 * <li>characterSubstitutionTables</li>
	 * <li>characterExcludeFromSubstitution</li>
	 * <li>characterFallbackStates</li>
	 * </ol>
	 * characterFallbackStates is a comma separated list of fallbacks,
	 * the following are valid:
	 * <ol>
	 * <li>fallbackToNonSpacingMarkRemovalTransliteration</li>
	 * <li>fallbackToLatinTransliteration</li>
	 * <li>fallbackToUCD</li>
	 * </ol>
	 */
	
	/* (non-Javadoc)
	 * @see se_tpb_speechgenerator.TTS#setParamMap(java.util.Map)
	 */
	public void init() {

		String line = null;
		URL[] urls = null;

		//-------------------------------------------------------------------------------
		// regex
		//
		line = parameters.get(TTSConstants.REGEX_URLS);
		if (line != null) {
			urls = getURLs(line);
			regexReplace = new RegexReplace[urls.length];
			for (int i = 0; i < urls.length; i++) {
				regexReplace[i] = new RegexReplace(urls[i]);
			}
		}

		//-------------------------------------------------------------------------------
		// character substitution tables
		//
		line = parameters.get(TTSConstants.CHARACTER_SUBSTITUTION_TABLES);
		if (line != null) {
			urls = getURLs(line);
			charReplacer = new UCharReplacer();

			for (int i = 0; i < urls.length; i++) {
				try {
					charReplacer.addSubstitutionTable(urls[i]);
				} catch (IOException e) {
					e.printStackTrace();
					String msg = "Translation table " + urls[i] + " exception: " + e.getMessage();
					throw new IllegalArgumentException(msg);
				}
			}


			// Set optional exclusion repertoire
			String excludeCharset = parameters.get("characterExcludeFromSubstitution");
			if (excludeCharset != null) {
				try {
					charReplacer.setExclusionRepertoire(Charset.forName(excludeCharset));
				} catch (UnsupportedOperationException e) {
					throw new IllegalArgumentException(e.getMessage(), e);
				}
			}

			// Set optional character fallback state(s)
			if (null != parameters.get("characterFallbackStates")) {
				String characterFallbacks = parameters.get("characterFallbackStates");
				String[] fallbacks = characterFallbacks.split(",");
				for (int i = 0; i < fallbacks.length; i++) {
					if (fallbacks[i].trim().equals("fallbackToNonSpacingMarkRemovalTransliteration")) {
						charReplacer.setFallbackState(charReplacer.FALLBACK_TRANSLITERATE_REMOVE_NONSPACING_MARKS, true);
					}

					if (fallbacks[i].trim().equals("fallbackToLatinTransliteration")) {
						charReplacer.setFallbackState(charReplacer.FALLBACK_TRANSLITERATE_ANY_TO_LATIN, true);
					}

					if (fallbacks[i].trim().equals("fallbackToUCD")) {
						charReplacer.setFallbackState(charReplacer.FALLBACK_USE_UCD_NAMES, true);
					}
				}				
			}		
		}
		
		//-------------------------------------------------------------------------------
		// years
		//
		line = parameters.get(TTSConstants.YEAR_REGEX_URL);
		if (line != null) {
			urls = getURLs(line);

			try {
				yearAnnouncer = 
					new YearAnnouncer(urls[0]);
			} catch (Exception e) {
				String by = parameters.get(TTSBuilder.CLASS) == null ?
						":" : " by " + parameters.get(TTSBuilder.CLASS) + ":";
			
				String msg = "Exception occurred during creation of " +
				"YearAnnouncer for use in TTSUtils" + by + "\n";
				msg += e.getMessage();
				System.err.println(e.getMessage());
				e.printStackTrace();
				throw new IllegalArgumentException(msg, e);
			}
		}

		//-------------------------------------------------------------------------------
		// XSLT
		//
		line = parameters.get(TTSConstants.XSLT_URLS);
		if (line != null) {
			try {
				urls = getURLs(line);
				xsltFilename = urls[0].toURI().toString();
				// load the transformer
				cache.get(xsltFilename, XSLT_FACTORY);
			} catch (Exception e) {
				String by = parameters.get(TTSBuilder.CLASS) == null ?
						":" : " by " + parameters.get(TTSBuilder.CLASS) + ":";
			
				String msg = "Exception occurred during creation of " +
				"XSL Transform for use in TTSUtils" + by + "\n";
				msg += e.getMessage();
				System.err.println(e.getMessage());
				e.printStackTrace();
				throw new IllegalArgumentException(msg, e);
			}
		}
	}

	// a comma separated list of file urls.
	private URL[] getURLs(String line) {
		if (null == line) {
			throw new IllegalArgumentException("Error paring TTSUtils parameters. This is no URL: " + line);
		}

		String[] strs = line.split(",");
		URL[] urls = new URL[strs.length];

		for (int i = 0; i < strs.length; i++) {
			File t = new File(strs[i].trim());
			try {
				
				if (t.exists()) {
						urls[i] = t.toURI().toURL();
				} else {
					String msg = "File " + t.getAbsolutePath() + " could not be found";
					throw new IllegalArgumentException(msg);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return urls;
	}


	/**
	 * Applies an XSLT on the synchronization point DOM.
	 * @param document the synchronization point DOM.
	 * @return the text remaining from <code>document</code> after the XSLT.
	 * @throws XSLTException
	 */
	public String xsltFilter(Document document) throws XSLTException {
		if (xsltFilename != null) {
			StringBuffer buff = new StringBuffer();
			DOMSource source = new DOMSource(document.getDocumentElement());
			Stylesheet.apply(source, cache.get(xsltFilename, XSLT_FACTORY), buff, null, null);
			return buff.toString().trim();
		} 
		return document.getDocumentElement().getTextContent().trim();
		
	}


	/**
	 * Uses regexes to manipulate the text <code>text</code>.
	 * First, the regexes from the specificRegexFilename are applied, 
	 * then the general ones.
	 * @param text the text to manipulate.
	 * @return the manipulated text.
	 */
	public String regexFilter(String text) {
		if (specificReplace != null) {
			for (int i = 0; i < specificReplace.length; i++) {
				text = specificReplace[i].filter(text);
			}
		}

		if (regexReplace != null) {
			for (int i = 0; i < regexReplace.length; i++) {
				text = regexReplace[i].filter(text);	
			}
		}

		return text;
	}


	/**
	 * Applies regexes to read years as years, not as numbers. The 
	 * numbers in the interval defined in the year substitution xml file 
	 * will be spelled out with letters.
	 * @param str the unfiltered string.
	 * @return the filtered string, with years spelled out with letters.
	 */
	public String yearFilter(String str) {
		if (yearAnnouncer != null) {
			return yearAnnouncer.filter(str);
		}
		return str;
	}


	/**
	 * Normalizes all whitespace to a single "&nbsp;".
	 * @param str the text.
	 * @return the text with normalized whitespace.
	 */
	public String normalizeWhitespace(String str) {
		StringTokenizer tok = new StringTokenizer(str);
		StringBuffer sb = new StringBuffer();
		while (tok.hasMoreTokens()) {
			sb.append(tok.nextToken());
			if (tok.hasMoreElements()) {
				sb.append(' ');
			}
		}
		return sb.toString();
	}



	/**
	 * Replaces characters with replacement string as configured in
	 * external file for character replacement.
	 * @param line the string of text that may contain illegal characters.
	 * @return the input string with illegal characters substituted.
	 */
	public String replaceUChars(String line) {
		if (charReplacer != null) {
			CharSequence cs = charReplacer.replace(line);
			return cs.toString();
		}
		return line;
	}

	

	@SuppressWarnings("unused")
	private void DEBUG(String msg) {
		if (System.getProperty("org.daisy.debug") != null) {
			System.out.println("DEBUG: " + msg);
		}
	}

	
	// prints the document on stderr.
	public static void print(Document d) {
		try {
			TransformerFactory xformFactory = TransformerFactory.newInstance();  
			javax.xml.transform.Transformer idTransform = xformFactory.newTransformer();
			idTransform.setOutputProperty(OutputKeys.INDENT, "yes");
			Source input = new DOMSource(d);
			Result output = new StreamResult(System.err);
			idTransform.transform(input, output);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Returns <b>true</b> iff the string contains any digits or
	 * letters apart from the optional xml.
	 * @param input the input
	 * @return <b>true</b> iff the string contains any digits or
	 * letters apart from the optional xml, <b>false</b> otherwise.
	 */
	public static boolean containsLetterOrDigit(TTSInput input) {
		if (null == input) {
			return false;
		}
		
		if (input.isSyncPoint()) {
			return containsLetterOrDigit(input.getSyncPoint());
		}
		List<StartElement> anns = input.getAnnouncements();
		QName qn = input.getQName();
		String concat = concatAttributes(anns, qn);
		return containsLettersOrDigits(concat);
	}
	
	/*
	public static boolean containsLetterOrDigit(Document doc) {
		return containsLetterOrDigit(doc.getDocumentElement());
	}
	*/
	
	/**
	 * Returns true if the node's text content contains at least
	 * one letter or digit, false otherwise.
	 * @param node the node
	 * @return true if the node's text content contains at least
	 * one letter or digit, false otherwise.
	 */
	public static boolean containsLetterOrDigit(Node node) {
		if (null == node) {
			return false;
		}
		
		String tc = node.getTextContent();
		if (tc != null && tc.trim().length() > 0) {
			if (containsLettersOrDigits(tc)) {
				return true;
			}
		}

		NodeList kids = node.getChildNodes();
		for (int i = 0; i < kids.getLength(); i++) {
			if (containsLetterOrDigit(kids.item(i))) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Returns true if the input string contains at least
	 * one letter or digit, false otherwise.
	 * @param line the string of text
	 * @return true if the input string contains at least
	 * one letter or digit, false otherwise.
	 */
	public static boolean containsLettersOrDigits(String line) {
		if (null == line || line.trim().length() == 0) {
			return false;
		}
		
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (Character.isDigit(c) || Character.isLetter(c)) {
				return true;
			}
		}
		return false;
	}
	

	
	/**
	 * Converts the input Document (the xml scope) to a string of 
	 * speakable text.
	 * @param doc the xml fragment representing a syncpoint.
	 * @return speakable text.
	 * @throws XSLTException
	 * @throws IOException
	 * @throws SAXException 
	 */
	public String dom2input(Document doc) throws XSLTException, IOException, SAXException {
		expandAbbrs(doc);
		parseRomanNumerals(doc);
		prepareTextContent(doc);
		
		String str = xsltFilter(doc);
		return normalizeWhitespace(str);
	}
	
	
	
	/**
	 * @param node
	 * @throws CatalogExceptionNotRecoverable
	 * @throws XSLTException
	 * @throws IOException
	 */
	private void prepareTextContent(Node node) throws CatalogExceptionNotRecoverable, XSLTException, IOException {
		if (node.getNodeType() == Node.TEXT_NODE) {
			String s = node.getTextContent();
			node.setTextContent(str2input(s, false));
		} else {
			NodeList kids = node.getChildNodes();
			for (int i = 0; i < kids.getLength(); i++) {
				prepareTextContent(kids.item(i));
			}
		}
	}
	
	public String str2input(String str, boolean normalizeWhitespace) {
		str = regexFilter(str);
		str = yearFilter(str);
		str = replaceUChars(str);
		if (normalizeWhitespace) {
			str = normalizeWhitespace(str);
		}
		return str;
	}
	
	public String str2input(String str) {
		boolean normalizeWhitespace = true;
		return str2input(str, normalizeWhitespace);
	}
	
	/**
	 * Waits for bytes to become available at <b>is</b>, in
	 * at most <b>timeout</b> milliseconds.
	 * @param is the <b>InputStream</b>
	 * @param timeout the desired timeout in milliseconds.
	 * @return the number of milliseconds we had to wait for IO.
	 * @throws IOException
	 */
	public static long awaitIO(InputStream is, long timeout) throws IOException {
		return awaitIO(is, timeout, 200);
	}
	
	/**
	 * Waits for bytes to become available at <b>is</b>, in
	 * at most <b>timeout</b> milliseconds.
	 * @param is the <b>InputStream</b>.
	 * @param timeout the desired timeout in milliseconds.
	 * @param sleep the number of milliseconds to sleep after
	 * a negative io poll.
	 * @return the number of milliseconds we had to wait for IO.
	 * @throws IOException
	 */
	public static long awaitIO(InputStream is, long timeout, long sleep) throws IOException {
		if (sleep < 0) {
			String msg = "Time intervall may not be a negative number: " + sleep;
			throw new IllegalArgumentException(msg);
		}
		
		long startTime = System.currentTimeMillis();
				
		do {
			if (is.available() > 0) {
				return System.currentTimeMillis() - startTime;
			}
			
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
				break;
			}
		} while (timeout > (System.currentTimeMillis() - startTime));
		String msg = "Wait for IO timed out after: " + (System.currentTimeMillis() - startTime) + "ms," +
				" (max was " + timeout + "ms).";
		throw new IOException(msg);
	}
	
	
	/**
	 * Writes the given audio data to the given file
	 * @param data the audio data
	 * @param dst the destination file
	 * @throws IOException
	 */
	public static void writeAudio(byte[] data, File dst) throws IOException {
		if (null == data || data.length == 0) {
			return;
		}
		
		FileOutputStream fos = new FileOutputStream(dst);
		fos.write(data);
		fos.close();
	}
}
