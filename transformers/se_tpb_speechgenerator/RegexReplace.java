package se_tpb_speechgenerator;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.daisy.util.xml.XPathUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Martin Blomberg
 *
 */
public class RegexReplace {
	
	private Vector patterns = new Vector();
	private boolean DEBUG = false;
	
	/**
	 * @param rulesXML
	 */
	public RegexReplace(File rulesXML) {
		initResources(rulesXML);
	}
	
	
	/**
	 * Reads the config file containing regexes and replace strings.
	 * @param rulesXML the config file.
	 */
	private void initResources(File rulesXML) {	
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			String xpath = "//rule";
			
			Document config = db.parse(rulesXML);
			NodeList rules = XPathUtils.selectNodes(config.getDocumentElement(), xpath);
			for (int i = 0; i < rules.getLength(); i++) {
				Element rule = (Element) rules.item(i);
				try {
					Pattern p = Pattern.compile(rule.getAttribute("match"));
					PatternReplace pr = new PatternReplace(p, rule.getAttribute("replace"));
					patterns.add(pr);
					DEBUG(pr);
				} catch(PatternSyntaxException pse) {
					System.out.println("There is a problem with the regular expression!");
					System.out.println("The pattern in question is: "+pse.getPattern());
					System.out.println("The description is: "+pse.getDescription());
					System.out.println("The message is: "+pse.getMessage());
					System.out.println("The index is: "+pse.getIndex());
					System.exit(0);
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Filters a string using the regexes.
	 * @param text the text to filter.
	 * @return the filtered text.
	 */
	public String filter(String text) {
		for (Iterator it = patterns.iterator(); it.hasNext(); ) {
			PatternReplace pr = (PatternReplace) it.next();
			text = processTest(text, pr.getPattern(), pr.getReplace());
		}
		return text;
	}
	
	/**
	 * Performes a regex-replace.
	 * @param input the input text.
	 * @param pattern the pattern to match.
	 * @param replace the replacement string.
	 * @return the filtered text.
	 */
	private String processTest(String input, Pattern pattern, String replace) {
		try {
			Matcher matcher = pattern.matcher(input);
			return matcher.replaceAll(replace);
		} catch (Exception e) {
			System.err.println("RegexReplace:");
			System.err.println("     pattern: " + pattern.toString());
			System.err.println("       input: " + input);
			System.err.println("     replace: " + replace);
			
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	
	private void DEBUG(PatternReplace pr) {
		DEBUG(pr.getPattern().toString() + "\t" + pr.getReplace());
	}
	
	
	private void DEBUG(String msg) {
		if (DEBUG) {
			System.err.println("RegexReplace: " + msg);
		}
	}
}

/**
 * A class combining a pattern and a replacement string.
 * @author Martin Blomberg
 *
 */
class PatternReplace {
	public Pattern pattern;
	public String replace;
	
	public PatternReplace(Pattern regex, String replace) {
		this.pattern = regex;
		this.replace = replace;
	}

	/**
	 * @return Returns the pattern.
	 */
	public Pattern getPattern() {
		return pattern;
	}

	/**
	 * @return Returns the prefix.
	 */
	public String getReplace() {
		return replace;
	}
}