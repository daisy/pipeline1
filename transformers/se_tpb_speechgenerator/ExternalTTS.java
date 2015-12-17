/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2006  Daisy Consortium
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

package se_tpb_speechgenerator;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
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
import org.daisy.util.xml.SmilClock;
import org.daisy.util.xml.XPathUtils;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.TransformerCache;
import org.daisy.util.xml.xslt.XSLTException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * Abstract base class for TTS implementations. 
 * @author Martin Blomberg
 *
 */
public abstract class ExternalTTS implements TTS {
	
	protected Map<?,?> parameters;									// custom parameters
	protected RegexReplace specificReplace;						// regexes: optional, applied before general regexes
	protected RegexReplace generalReplace;						// regexes: optional, applied after specific regexes
	protected UCharReplacer charReplacer;						// replacement of characters
	protected String xsltFilename;								// xsl transformation of each sync point
	protected TransformerCache cache = new TransformerCache();	// for fast loading of the xslt
	private static final String XSLT_FACTORY = "net.sf.saxon.TransformerFactoryImpl";	// choose the right transformer factory
	protected YearAnnouncer yearAnnouncer;						//  speaking years properly
	
	/**
	 * Constructor taking a map of parameters/properties as parameter. 
	 * @param params A java.util.Map containing parameters.
	 */
	public ExternalTTS(Map<?,?> params) {
		this.setParamMap(params);
		//if (null != params.get(TTSBuilder.BINARY)) {
		//	this.setBinaryPath(new File((String) params.get(TTSBuilder.BINARY)));
		//}
	}
	
	
	/**
	 * Generates audio for the contents of <tt>doc</tt>.
	 * @param doc A document holding some xml content.
	 * @param file a file representing the destination for the generated audio.
	 * @return the duration of the audio in ms.
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 * @throws TransformerRunException
	 */
	protected abstract SmilClock sayImpl(Document doc, File file) throws IOException, UnsupportedAudioFileException, TransformerRunException;
	
	/**
	 * Generates audio for the string <tt>str</tt>.
	 * @param str a text string.
	 * @param file a file representing the destination for the generated audio.
	 * @return the duration of the audio in ms.
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 * @throws TransformerRunException
	 */
	protected abstract SmilClock sayImpl(String str, File file) throws IOException, UnsupportedAudioFileException, TransformerRunException;
	
	/**
	 * Makes an announcement, introduction, for each start element in the list
	 * to the file <code>outputFile</code>. 
	 * @param startElements List of <code>StartElement</code>s to announce prior to
	 * the content of the same elements.
	 * @param attributeQName the QName for the attribute containing the introduction text.
	 * @param outputFile the audio file to generate.
	 * @return the time of the speach generated in milli seconds.
	 * @throws UnsupportedAudioFileException 
	 * @throws TransformerRunException 
	 */
	public SmilClock introduceStruct(List<StartElement> startElements, QName attributeQName, File outputFile) throws IOException, UnsupportedAudioFileException, TransformerRunException {
		String sayBefore = concatAttributes(startElements, attributeQName);
		return sayImpl(sayBefore, outputFile);
	}
	
	/**
	 * Makes an announcement, termination, for each start element in the list
	 * to the file <code>outputFile</code>. 
	 * @param startElements List of <code>StartElement</code>s to announce after 
	 * the content of the same elements.
	 * @param attributeQName the QName for the attribute containing the termination text.
	 * @param outputFile the audio file to generate.
	 * @return the time of the speach generated in milliseconds.
	 * @throws UnsupportedAudioFileException 
	 * @throws TransformerRunException 
	 */
	public SmilClock terminateStruct(List<StartElement> startElements, QName attributeQName, File outputFile) throws IOException, UnsupportedAudioFileException, TransformerRunException {
		String sayAfter = concatAttributes(startElements, attributeQName);
		return sayImpl(sayAfter, outputFile);
	}
	
	
	/**
	 * A DOM containing text to read.
	 * @param synchPoint A part of the manuscript identified as a point of synchronization.
	 * @param outputFile the audio file to generate.
	 * @return the time of the speach generated in milliseconds.
	 * @throws UnsupportedAudioFileException 
	 * @throws TransformerRunException 
	 */
	public SmilClock say(Document synchPoint, File outputFile) throws IOException, UnsupportedAudioFileException, TransformerRunException {
		NodeList abbrs = XPathUtils.selectNodes(synchPoint.getDocumentElement(), "//*[@exp]");
		// if supplied, use exp attributes instead of text nodes
		for (int i = 0; i < abbrs.getLength(); i++) {	
			Element elem = (Element) abbrs.item(i);
			Node textNode = synchPoint.createTextNode(elem.getAttribute("exp"));
			elem.getParentNode().insertBefore(textNode, elem);
			elem.getParentNode().removeChild(elem);
		}
		
		// parse all roman numerals
		NodeList romans = XPathUtils.selectNodes(synchPoint.getDocumentElement(), "//pagenum[@page='front']");
		for (int i = 0; i < romans.getLength(); i++) {	
			Element elem = (Element) romans.item(i);
			String str = elem.getTextContent().trim();
			str = String.valueOf(RomanNumeral.parseInt(str));
			elem.setTextContent(str);
		}
		
		return sayImpl(synchPoint, outputFile);
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
	private String concatAttributes(List<StartElement> startElements, QName attrName) {
		String announcements = "";
		for (int i = 0; i< startElements.size(); i++) {
			StartElement se = startElements.get(i);
// jpritchett@rfbd.org:  For some reason, the following doesn't work all the time.
//			Attribute at = se.getAttributeByName(attrName);
//			if (at != null) {
//				announcements += at.getValue() + " ";
//			}
// Replaced with the following do-it-yourself iteration, 14 Aug 2006
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
	
	
	/**
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
	 * However, there may be other values here that are read by the
	 * TTS implementation, such as <code>ttsProperties</code>, which
	 * should be a filename for a TTS specific properties file.
	 * @param params the parameters.
	 * @see se_tpb_speechgenerator.TTS#setParamMap(java.util.Map)
	 */
	/* (non-Javadoc)
	 * @see se_tpb_speechgenerator.TTS#setParamMap(java.util.Map)
	 */
	public void setParamMap(Map<?,?> params) {
		
		parameters = params;
		if (null != params.get("generalRegexFilename")) {
			generalReplace = 
				new RegexReplace(new File((String) params.get("generalRegexFilename")));
		}
		
		if (null != params.get("specificRegexFilename")) {
			specificReplace = 
				new RegexReplace(new File((String) params.get("specificRegexFilename")));
		}
		
		
		if (null != params.get("characterSubstitutionTables")) {
			charReplacer = new UCharReplacer();
			
			// Load all substitution tables 
			String substitutionTables = (String) params.get("characterSubstitutionTables");			
			String[] tables = substitutionTables.split(",");
			for (int i = 0; i < tables.length; i++) {
				File t = new File(tables[i].trim());
				if (t.exists()) {
					try {
						charReplacer.addSubstitutionTable(t.toURI().toURL());
					} catch (Exception e) {
						String msg = "Translation table " + t.getPath() + " exception: " + e.getMessage();
						throw new IllegalArgumentException(msg);
					}	
				} else {
					String msg = "Translation table " + t.getPath() + " could not be found";
					throw new IllegalArgumentException(msg);
				}
			}


			// Set optional exclusion repertoire
			String excludeCharset = (String) params.get("characterExcludeFromSubstitution");
			if (excludeCharset != null) {
				try {
					charReplacer.setExclusionRepertoire(Charset.forName(excludeCharset));
				} catch (UnsupportedOperationException e) {
					throw new IllegalArgumentException(e.getMessage(), e);
				}
			}
			
			// Set optional character fallback state(s)
			if (null != params.get("characterFallbackStates")) {
				String characterFallbacks = (String) params.get("characterFallbackStates");
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
		
		if (null != params.get("yearFilename")) {
			try {
				yearAnnouncer = 
					new YearAnnouncer(new File((String) params.get("yearFilename")).toURI().toURL());
			} catch (MalformedURLException e) {
				String msg = "Exception occurred during creation of " + 
					params.get(TTSBuilder.CLASS) + "\n";
				msg += e.getMessage();
				throw new IllegalArgumentException(msg, e);
			} catch (ParserConfigurationException e) {
				String msg = "Exception occurred during creation of " + 
					params.get(TTSBuilder.CLASS) + "\n";
				msg += e.getMessage();
				throw new IllegalArgumentException(msg, e);
			} catch (SAXException e) {
				String msg = "Exception occurred during creation of " + 
					params.get(TTSBuilder.CLASS) + "\n";
				msg += e.getMessage();
				throw new IllegalArgumentException(msg, e);
			} catch (IOException e) {
				String msg = "Exception occurred during creation of " + 
					params.get(TTSBuilder.CLASS) + "\n";
				msg += e.getMessage();
				throw new IllegalArgumentException(msg, e);
			}
		}
		
		if (null != params.get("xsltFilename")) {
			try {
				xsltFilename = (String) params.get("xsltFilename");
				// load the transformer
				cache.get(xsltFilename, XSLT_FACTORY);
			} catch (XSLTException e) {
				System.err.println("Could not parse " + params.get("xsltFilename"));
				System.err.println("Continuing without XSLT, ");
				e.printStackTrace();
				xsltFilename = null;
			}
		}
	}
	
	
	/**
	 * Applies an XSLT on the synchronization point DOM.
	 * @param document the synchronization point DOM.
	 * @return the text remaining from <code>document</code> after the XSLT.
	 * @throws XSLTException
	 * @throws IOException 
	 * @throws SAXException 
	 */
	protected String xsltFilter(Document document) throws XSLTException, SAXException, IOException {
		if (xsltFilename != null) {
			StringBuffer buff = new StringBuffer();
			DOMSource source = new DOMSource(document.getDocumentElement());
			Stylesheet.apply(source, cache.get(xsltFilename, XSLT_FACTORY), buff, null, null);
			return buff.toString().trim();
		}
		return document.getDocumentElement().getTextContent().trim();
		
	}
	
	
	/**
	 * Uses regexes to manipulate the text <code>text</text>.
	 * First, the regexes from the specificRegexFilename are applied, 
	 * then the general ones.
	 * @param text the text to manipulate.
	 * @return the manipulated text.
	 */
	protected String regexFilter(String text) {		
		if (specificReplace != null) {
			text = specificReplace.filter(text);
		}
		
		if (generalReplace != null) {
			text = generalReplace.filter(text);
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
	protected String yearFilter(String str) {
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
	protected String normalizeWhitespace(String str) {
		StringTokenizer tok = new StringTokenizer(str);
		str = "";
		while (tok.hasMoreTokens()) {
			str += tok.nextToken() + " ";
		}
		return str.trim();
	}
	
	
	
	protected String replaceUChars(String line) {
		if (charReplacer != null) {
			CharSequence cs = charReplacer.replace(line);
			return cs.toString();
		}
		return line;
	}
	
	/**
	 * Character substitution in textnodes. Recursive.
	 * @param node the node
	 */
	protected void replaceUChars(Node node) {
		if (node instanceof Document) {
			// just make a recursive call using the document element as parameter
			Document doc = (Document) node;
			replaceUChars(doc.getDocumentElement());
		
		} else if (node instanceof Element) {
			// line up the children, make a call for each one of them
			Element elem = (Element) node;
			NodeList kids = elem.getChildNodes();
			for (int i = 0; i < kids.getLength(); i++) {
				replaceUChars(kids.item(i));
			}

		} else if (node instanceof Text) {
			// base case
			// bingo! make the substitutions
			Text text = (Text) node;
			String original = text.getWholeText();
			String replacement = replaceUChars(original);
			if (!replacement.equals(original)) {
				try {
					text.replaceWholeText(replacement);
				} catch (DOMException e) {
					// text node is read only.
					DEBUG("ExternalTTS#replaceUChars(Node): unable to replace: " + original + " to " + replacement);
				}
			}
		}
	}
	
	private void DEBUG(String msg) {
		if (System.getProperty("org.daisy.debug") != null) {
			System.out.println("DEBUG: " + msg);
		}
	}
	
	@SuppressWarnings("unused")
	private void DEBUG(Document d) {
		if (System.getProperty("org.daisy.debug") != null) {
			DEBUG("ExternalTTS#DEBUG(Document):");
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
	}
}
