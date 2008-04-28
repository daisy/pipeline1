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
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.daisy.util.i18n.LocaleUtils;
import org.daisy.util.xml.XPathUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * TTS builder/factory. Depends heavily on its configuration file.
 * 
 * @author Martin Blomberg
 *
 */
public class TTSBuilder {

	public static final String CLASS = "class";		// the qualified java class name of a tts
	public static final String BINARY = "binary";	// the path to binary used by a java tts wrapper					
	private File configFile;						// the TTSBuilder configuration file
	private Document config;						// the DOM representation of the TTSBuilder configuration file

	/**
	 * Constructor pointing out the TTSBuilder configuration file.
	 * @param configFile the configuration file.
	 * @throws TTSBuilderException 
	 */
	public TTSBuilder(File configFile) throws TTSBuilderException {
		this.configFile = configFile;
		this.config = readXML(configFile);
		keepFirstMatchingOS(config);
	}

	/**
	 * Constructor pointing out the TTSBuilder configuration file and
	 * an additional map containing the names of parameters in the 
	 * configuration file together with their values.
	 * @param configFile the configuration file
	 * @param parameterSubst the parameter/value map.
	 * @throws TTSBuilderException 
	 */
	public TTSBuilder(File configFile, Map<String,String> parameterSubst) throws TTSBuilderException {
		this.configFile = configFile;
		this.config = readXML(configFile);
		XMLParameter xmlp = new XMLParameter(parameterSubst);
		xmlp.eval(config);
		keepFirstMatchingOS(config);
	}


	/**
	 * Keeps the first os configuration from <code>config</code> that matches the current environment.
	 * @param config the configuration document 
	 */
	private void keepFirstMatchingOS(Document config) {
		Properties systemProps = System.getProperties();
		Element docElem = config.getDocumentElement();

		NodeList osList = XPathUtils.selectNodes(docElem, "/ttsbuilder/os");

		int numRemovedSystems = 0;
		// for each operating system currentOS
		for (int i = 0; i < osList.getLength(); i++) {
			Node currentOS = osList.item(i);
			NodeList propList = XPathUtils.selectNodes(currentOS, "property");

			// for each property required for currentOS
			boolean matching = true;
			for (int j = 0; j < propList.getLength(); j++) {
				Element prop = (Element) propList.item(j);

				String propertyName = prop.getAttribute("name");
				String propertyMatch = prop.getAttribute("match");
				String systemPropValue = systemProps.getProperty(propertyName);

				if (!systemPropValue.matches(propertyMatch)) {
					currentOS.getParentNode().removeChild(currentOS);
					numRemovedSystems++;
					matching = false;
					break;
				}
			}

			// is currentOS the one we should keep?
			if (matching) {
				i++;
				for ( ; i < osList.getLength(); i++) {
					// tmp is either redundant or incorrect
					Node tmp = osList.item(i);
					tmp.getParentNode().removeChild(tmp);
					numRemovedSystems++;
				}
			}
		}

		// what? no matching operating systems left in the configuration file?
		if (osList.getLength() == numRemovedSystems) {
			String osName = System.getProperty("os.name");
			String message = "TTSBuilder configuration file does not " +
			"contain any operating system entry matching the current environment: " + 
			osName + "\n" + "Add a proper " + osName + " matching section to " + 
			configFile + " to fix this problem.";
			throw new IllegalArgumentException(message);
		}

		DEBUG(config);
	}

	/**
	 * Returns a new TTS implementation for the language <code>lang</code>.
	 * 
	 * If <code>lang != null</code> and if no such TTS exists, a 
	 * <code>TTSNotFoundException</code> is thrown.
	 * 
	 * If <code>lang == null</code> an attempt is made to provide a
	 * default TTS. If no such exists, a 
	 * <code>TTSNotFoundException</code> is thrown.
	 * 
	 * @param lang the lower case two letter ISO 639 language code, or
	 * <code>null</code> if a default voice is desired.
	 * @return a new TTS for language <code>lang</code> or
	 * a default if <code>lang</code> is <code>null</code>.
	 *  
	 * @throws TTSBuilderException 
	 */
	public TTS newTTS(String lang) throws TTSBuilderException {
		Map<String, String> parameters = new HashMap<String, String>();

		String xpath = null;
		Element docElement = null;
		Element ttsElement = null;

		// if lang != null, someone wants a TTS for a specific xml:lang
		if (lang != null) {
			// is lang a valid locale?
			if (null == LocaleUtils.string2locale(lang)) {
				// what? throw InvalidLangException??
			}

			xpath = "/ttsbuilder/os/lang[@lang='" + lang + "']/tts";
			docElement = config.getDocumentElement();
			ttsElement = (Element) XPathUtils.selectSingleNode(docElement, xpath);

			if (null == ttsElement) {
				String msg = "No TTS found for language: " + lang;
				TTSBuilderException e = new TTSBuilderException(msg);
				throw e;
			}
		} else {
			// lang is null, a default voice is desired
			xpath = "/ttsbuilder/os/lang/tts[@default='true']";
			docElement = config.getDocumentElement();
			ttsElement = (Element) XPathUtils.selectSingleNode(docElement, xpath);;

			if (null == ttsElement) {
				String msg = "No default TTS found!";
				TTSBuilderException e = new TTSBuilderException(msg);
				throw e;
			}
		}


		// ttsElement represents a tts matching user requirements,
		// read the tts specific parameters from the configfile
		NodeList params = XPathUtils.selectNodes(ttsElement, "param");
		for (int i = 0; i < params.getLength(); i++) {
			Element elem = (Element) params.item(i);
			parameters.put(elem.getAttribute("name"), elem.getAttribute("value"));
		}

		String fullClassName = parameters.get(CLASS);
		if (null == fullClassName) {
			throw new IllegalArgumentException("The full class name must be privided for every TTS Java wrapper implementation, edit " + 
					configFile.getAbsolutePath() + 
					" in order to fix this problem. Tried to create tts for xml:lang= " + lang);
		}

		Class<?> ttsClass = null; 
		Class<?> parameterList[] = null;
		Map<?,?> constrParam[] = null;
		Constructor<?> constructor = null;
		TTS tts = null;

		try {
			ttsClass = Class.forName(fullClassName);
		} catch (ClassNotFoundException e) {
			throw new TTSBuilderException(e.getMessage(), e);
		}

		
		try {
			parameterList = new Class[]{Class.forName("java.util.Map")};
			constrParam = new Map[]{parameters};
			constructor = ttsClass.getConstructor(parameterList);
		} catch (Exception e) {
			//throw new TTSInstantiationException(e.getMessage(), e);
			// Ok, we didn't get the constructor we wanted. We continue
			// anyway, further down there is a possibility to try a
			// default no-parameters constructor.
		}

		// did we get hold of the desired constructor?
		if (constructor != null && constrParam != null) {
			try {
				tts = (TTS) constructor.newInstance(constrParam);
			} catch (Exception e) {
				//throw new TTSInstantiationException(e.getMessage(), e);
				// Do we really want to die here? Maybe, but for now: let's use
				// the default constructor as fallback.
			}
		}
		
		if (null == tts) {
			try {
				tts = (TTS) ttsClass.newInstance();
				tts.setParamMap(parameters);
			} catch (Exception e) {
				throw new TTSBuilderException(e.getMessage(), e);
			}
		}

		return tts;
	}
	

	/**
	 * Reads the configuration file into a DOM object.
	 * @param file the configuration file
	 * @return a DOM representation of the XML in the configuration file
	 * @throws TTSBuilderException
	 */
	private Document readXML(File file) throws TTSBuilderException {
		Document doc = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(file);
		} catch (ParserConfigurationException e) {
			throw new TTSBuilderException(e.getMessage(), e);
		} catch (SAXException e) {
			throw new TTSBuilderException(e.getMessage(), e);
		} catch (IOException e) {
			throw new TTSBuilderException(e.getMessage(), e);
		}
		return doc;
	}

	/**
	 * Optional debug messages.
	 * @param d the xml document to display.
	 */
	private void DEBUG(Document d) {
		if (System.getProperty("org.daisy.debug") != null) {
			DEBUG("DEBUG(Document):");
			try {
				TransformerFactory xformFactory = TransformerFactory.newInstance();  
				javax.xml.transform.Transformer idTransform = xformFactory.newTransformer();
				idTransform.setOutputProperty(OutputKeys.INDENT, "yes");
				Source input = new DOMSource(d);
				Result output = new StreamResult(System.out);
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

	/**
	 * Optional debug messages.
	 * @param msg the message to display.
	 */
	private void DEBUG(String msg) {
		if (System.getProperty("org.daisy.debug") != null) {
			System.out.println("DEBUG: TTSBuilder#" + msg);
		}
	}
}
