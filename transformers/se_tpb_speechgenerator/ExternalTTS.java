package se_tpb_speechgenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
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

import org.daisy.dmfc.exception.TransformerRunException;
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

/**
 * @author Martin Blomberg
 *
 */
public abstract class ExternalTTS implements TTS {
	
	protected boolean _DEBUG = false;
	protected Map parameters;
	protected RegexReplace generalReplace;
	protected RegexReplace specificReplace;
	protected UCharReplacer charReplacer;
	protected String xsltFilename;
	protected TransformerCache cache = new TransformerCache();
	private static final String XSLT_FACTORY = "net.sf.saxon.TransformerFactoryImpl";
	YearAnnouncer yearAnnouncer;
	
	/**
	 * Constructor taking a map of parameters/properties as parameter. 
	 * @param params A java.util.Map containing parameters.
	 * @throws IOException
	 */
	public ExternalTTS(Map params) throws IOException {
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
	protected abstract long sayImpl(Document doc, File file) throws IOException, UnsupportedAudioFileException, TransformerRunException;
	
	/**
	 * Generates audio for the string <tt>str</tt>.
	 * @param str a text string.
	 * @param file a file representing the destination for the generated audio.
	 * @return the duration of the audio in ms.
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 * @throws TransformerRunException
	 */
	protected abstract long sayImpl(String str, File file) throws IOException, UnsupportedAudioFileException, TransformerRunException;
	
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
	public long introduceStruct(List startElements, QName attributeQName, File outputFile) throws IOException, UnsupportedAudioFileException, TransformerRunException {
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
	public long terminateStruct(List startElements, QName attributeQName, File outputFile) throws IOException, UnsupportedAudioFileException, TransformerRunException {
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
	public long say(Document synchPoint, File outputFile) throws IOException, UnsupportedAudioFileException, TransformerRunException {
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
	private String concatAttributes(List startElements, QName attrName) {
		String announcements = "";
		for (int i = 0; i< startElements.size(); i++) {
			StartElement se = (StartElement) startElements.get(i);
			Attribute at = se.getAttributeByName(attrName);
			if (at != null) {
				announcements += at.getValue() + " ";
			}
		}
		
		return announcements;
	}
	
	
	/**
	 * Receives a Map with parameters. In this implementation,
	 * the values are all filenames and the keys are:
	 * <ol>
	 * <li>generalRegexFilename</li>
	 * <li>specificRegexFilename</li>
	 * <li>yearFilename</li>
	 * <li>xsltFilename</li>
	 * <li>characterTranslationTable</li>
	 * </ol>
	 * However, there may be other values here that are read by the
	 * TTS implementation, such as <code>ttsProperties</code>, which
	 * should be a filename for a TTS specific properties file.
	 * @param params the parameters.
	 * @see se_tpb_speechgenerator.TTS#setParamMap(java.util.Map)
	 */
	public void setParamMap(Map params) {
		
		parameters = params;
		if (null != params.get("generalRegexFilename")) {
			generalReplace = 
				new RegexReplace(new File((String) params.get("generalRegexFilename")));
		}
		
		if (null != params.get("specificRegexFilename")) {
			specificReplace = 
				new RegexReplace(new File((String) params.get("specificRegexFilename")));
		}
		
		if (null != params.get("characterTranslationTable")) {
			String tableEncoding = (String) params.get("translationTableEncoding");
			String translationTable = (String)params.get("characterTranslationTable");
			
			charReplacer = new UCharReplacer();
			try {
				File file = new File((String) translationTable);
				charReplacer.addTranslationTable(file.toURL(), tableEncoding);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (null != params.get("yearFilename")) {
			try {
				yearAnnouncer = 
					new YearAnnouncer(new File((String) params.get("yearFilename")).toURL());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (null != params.get("xsltFilename")) {
			try {
				xsltFilename = (String) params.get("xsltFilename");
				// load the transformer
				/*Transformer t = */cache.get(xsltFilename, XSLT_FACTORY);
			} catch (IOException e) {
				System.err.println("Could not parse " + params.get("xsltFilename"));
				System.err.println("Continuing without XSLT, ");
				e.printStackTrace();
				xsltFilename = null;
			} catch (SAXException e) {
				System.err.println("Could not parse " + params.get("xsltFilename"));
				System.err.println("Continuing without XSLT, ");
				e.printStackTrace();
				xsltFilename = null;
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
	 * @throws CatalogExceptionNotRecoverable
	 * @throws XSLTException
	 * @throws FileNotFoundException
	 */
	protected String xsltFilter(Document document) throws CatalogExceptionNotRecoverable, XSLTException, FileNotFoundException {
		int len = document.getDocumentElement().getTextContent().trim().length();
		DEBUG(document);
		DEBUG(xsltFilename);
		if (xsltFilename != null) {
			StringBuffer buff = new StringBuffer();
			DOMSource source = new DOMSource(document.getDocumentElement());
			Stylesheet.apply(source, cache.get(xsltFilename, XSLT_FACTORY), buff, null, null);
			String result = buff.toString().trim();
			if (len != result.length()) {
				DEBUG(result);
			} else {
				DEBUG(result);
			}
			return result;
		} else {
			return document.getDocumentElement().getTextContent().trim();
		}
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
			return charReplacer.toReplacementString(line);
		}
		return line;
	}
	
	protected void DEBUG(String msg) {
		if (_DEBUG) {
			System.err.println("ExternalTTS: " + msg);
		}
	}
	
	protected void DEBUG(Document d) {
		if (_DEBUG) {
			DEBUG("DEBUG(Document):");
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
