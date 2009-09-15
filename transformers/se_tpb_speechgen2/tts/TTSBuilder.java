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
package se_tpb_speechgen2.tts;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.util.i18n.LocaleUtils;
import org.daisy.util.xml.XPathUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se_tpb_speechgen2.tts.concurrent.TTSAdapter;
import se_tpb_speechgen2.tts.concurrent.TTSGroup;
import se_tpb_speechgen2.tts.util.TTSUtils;


/**
 * Constructs tts instances given a certain configuration and environment.
 * 
 * @author Martin Blomberg
 *
 */
public class TTSBuilder implements TTSConstants {

	private File configFile;
	private Document configuration;

	/**
	 * Constructor pointing out the TTSBuilder configuration file.
	 * @param configFile the configuration file.
	 * @throws TTSBuilderException 
	 */
	public TTSBuilder(File configFile) throws TTSBuilderException {
		this.configFile = configFile;
		this.configuration = readXML(configFile);
		keepFirstMatchingOS(configuration);
	}

	public TTSBuilder(File configFile, Map<String,String> fileSubstitutions) throws TTSBuilderException {
		//System.setProperty("org.daisy.debug", "foo");
		this.configFile = configFile;
		this.configuration = readXML(configFile);
		XMLParameter xmlp = new XMLParameter(fileSubstitutions);
		xmlp.eval(configuration);
		keepFirstMatchingOS(configuration);
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


	public TTS newTTS(Locale lang, TransformerDelegateListener tdl) throws TTSBuilderException {

		String xpath = null;
		Element docElement = null;
		Element ttsElement = null;

		// if lang != null, someone wants a TTS for a specific xml:lang
		if (lang != null) {

			xpath = "/ttsbuilder/os/lang[@lang='" + lang.toString() + "']/tts";
			docElement = configuration.getDocumentElement();
			ttsElement = (Element) XPathUtils.selectSingleNode(docElement, xpath);

			if (null == ttsElement) {
				xpath = "/ttsbuilder/os/lang[@lang='" + lang.getLanguage() + "']/tts";
				docElement = configuration.getDocumentElement();
				ttsElement = (Element) XPathUtils.selectSingleNode(docElement, xpath);
				
				if (null ==ttsElement){
					xpath = "/ttsbuilder/os/lang[starts-with(@lang,'" + lang.getLanguage() + "_')]/tts";
					docElement = configuration.getDocumentElement();
					ttsElement = (Element) XPathUtils.selectSingleNode(docElement, xpath);
					
					if (null ==ttsElement){
						String msg = "No TTS found for language: " + lang;
						throw new TTSBuilderException(msg);
					}
				}
				
			}
		} else {
			// lang is null, a default voice is desired
			xpath = "/ttsbuilder/os/lang/tts[@default='true']";
			docElement = configuration.getDocumentElement();
			ttsElement = (Element) XPathUtils.selectSingleNode(docElement, xpath);;

			if (null == ttsElement) {
				String msg = "No default TTS found!";
				throw new TTSBuilderException(msg);
			}
		}


		// are we supposed to start several instances?
		int numTTSInstances = 1;
		String instancesValue = ttsElement.getAttribute("instances");
		if (instancesValue != null && instancesValue.trim().length() > 0) {
			int temp;
			try {
				temp = Integer.parseInt(instancesValue);
			} catch (NumberFormatException e) {
				temp = -1;
			}
			numTTSInstances = Math.max(numTTSInstances, temp);
		}

		// get the commands used to start (possibly) several instances of this tts.
		// if no commands are present, create a single empty map just to get by.
		Map<String, String>[] params = null;
		NodeList commands = XPathUtils.selectNodes(ttsElement, "param[@name='" + COMMAND + "']");
		if (commands.getLength() > 0) {
			params = new Map[commands.getLength()];
			for (int i = 0; i < params.length; i++) {
				params[i] = new HashMap<String, String>();
				Element elem = (Element) commands.item(i);
				params[i].put(COMMAND, elem.getAttribute("value"));
			}
		} else {
			params = new Map[]{new HashMap<String, String>()};
		}
		
		//System.err.println("#instances of " + lang + ": " + (numTTSInstances * params.length));

		// ttsElement represents a tts matching user requirements,
		// read the tts specific parameters from the configfile
		Map<String, String> parameters = new HashMap<String, String>();
		NodeList paramElems = XPathUtils.selectNodes(ttsElement, "param[@name!='" + COMMAND + "']");
		DEBUG("paramElems.getLength() = " + paramElems.getLength());
		for (int i = 0; i < paramElems.getLength(); i++) {
			Element elem = (Element) paramElems.item(i);
			parameters.put(elem.getAttribute("name"), elem.getAttribute("value"));
		}

		String fullClassName = parameters.get(CLASS);
		if (null == fullClassName) {
			throw new IllegalArgumentException("The full class name must be provided for every TTS Java wrapper implementation, edit " + 
					configFile.getAbsolutePath() + 
					" in order to fix this problem. Tried to create tts for xml:lang= " + lang);
		}

		Class<?> ttsClass = null; 
		Class<?> parameterList[] = null;
		Object constrParam[] = null;
		Constructor<?> constructor = null;
		TTSAdapter tts = null;

		try {
			DEBUG("fullClassName: " + fullClassName);
			ttsClass = Class.forName(fullClassName);
		} catch (ClassNotFoundException e) {
			throw new TTSBuilderException(e.getMessage(), e);
		}


		try {
			parameterList = new Class[]{
					Class.forName("se_tpb_speechgen2.tts.util.TTSUtils"), 
					Class.forName("java.util.Map")
				};
			
			constrParam = new Object[2];
			constructor = ttsClass.getConstructor(parameterList);
		} catch (Exception e) {
			//throw new TTSInstantiationException(e.getMessage(), e);
			// Ok, we didn't get the constructor we wanted. We continue
			// anyway, further down there is a possibility to try a
			// default no-parameters constructor.
		}

		
		List<TTSAdapter> ttsInstances = new ArrayList<TTSAdapter>();
		
		// for each command
		for (int i = 0; i < params.length; i++) {
			params[i].putAll(parameters);
			
			// for each "instance"
			for (int j = 0; j < numTTSInstances; j++) {
							
				// did we get hold of the desired constructor?
				if (constructor != null && constrParam != null) {

					// if so, create wished number of instances, if there are several
					// commands, one instance will be created for each one of them.
					try {
						Map<String, String> m = new HashMap<String, String>();
						m.putAll(params[i]);
						TTSUtils tu = new TTSUtils(m);
						
						constrParam[0] = tu;
						constrParam[1] = params[i];
						
						tts = (TTSAdapter) constructor.newInstance(constrParam);
					} catch (Exception e) {
						System.err.println(e.getMessage());
						e.printStackTrace();
						throw new TTSBuilderException(e.getMessage(), e);
					}
				} 
				// so there is no constructor accepting parametes. (as far as we know)
				// let's try the default approach.
				else {
					try {
						tts = (TTSAdapter) ttsClass.newInstance();
					} catch (InstantiationException e) {
						System.err.println(e.getMessage());
						e.printStackTrace();
						throw new TTSBuilderException(e.getMessage(), e);
					} catch (IllegalAccessException e) {
						System.err.println(e.getMessage());
						e.printStackTrace();
						throw new TTSBuilderException(e.getMessage(), e);
					}
				}
				// register the transformer delegate listener
				tts.setTransformerDelegateListener(tdl);
				// initialize the adapter
				tts.init();
				// add the tts to the group
				ttsInstances.add(tts);
			}
		}
		
		// create a TTSGroup with all the new Instances.
		TTSGroup group = new TTSGroup(ttsInstances,tdl);
		return group;
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
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
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
