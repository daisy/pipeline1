/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2005  Daisy Consortium
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
package se_tpb_speechgen2.tts.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
 * A class providing simple regex search and replace.
 * A rule-file is parsed and regexes and their replacement
 * string are stored in an ordered collection.
 * @author Martin Blomberg
 *
 */
public class RegexReplace {
	
	private Vector<PatternReplace> patterns = new Vector<PatternReplace>();
	private boolean DEBUG = false;
	
	/**
	 * @param rulesXML
	 */
	public RegexReplace(URL rulesXML) {
		initResources(rulesXML);
	}
	
	
	/**
	 * Reads the config file containing regexes and replace strings.
	 * @param rulesXML the config file.
	 */
	private void initResources(URL rulesXML) {	
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			String xpath = "//rule";
			
			Document config = db.parse(rulesXML.openStream());
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
				
					throw new IllegalArgumentException(pse.getMessage());
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
		for (Iterator<PatternReplace> it = patterns.iterator(); it.hasNext(); ) {
			PatternReplace pr = it.next();
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
	public Pattern mPattern; 	// the regex pattern to match
	public String mReplace;		// the replacement string
	
	public PatternReplace(Pattern regex, String replace) {
		this.mPattern = regex;
		this.mReplace = replace;
	}

	/**
	 * Returns the pattern.
	 * @return the pattern.
	 */
	public Pattern getPattern() {
		return mPattern;
	}

	/**
	 * Returns the prefix.
	 * @return the prefix.
	 */
	public String getReplace() {
		return mReplace;
	}
}