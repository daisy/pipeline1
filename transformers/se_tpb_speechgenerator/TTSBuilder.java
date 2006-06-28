package se_tpb_speechgenerator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
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

import org.daisy.util.xml.XPathUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TTSBuilder {
	
	public static final String CLASS = "class";
	public static final String BINARY = "binary";
	private boolean DEBUG = false;
	private Map parameters = new HashMap();
	private Document config;
	private File configFile;
	
	/**
	 * Constructor pointing out the TTSBuilder configuration file.
	 * @param configFile the configuration file.
	 */
	public TTSBuilder(File configFile) {
		this.configFile = configFile;
		this.config = readXML(configFile);
		keepFirstMatchingOS(config);
	}
	
	/**
	 * Sets a parameter. Parameters not used when creating the TTS are
	 * passed along to the TTS instance.
	 * @param paramName the parameter name
	 * @param paramValue the parameter value
	 */
	public void setParameter(String paramName, String paramValue) {
		parameters.put(paramName, paramValue);
	}
	
	
	/**
	 * Returns the parameter value, or <code>null</code> if no such
	 * value exsists.
	 * @param paramName the parameter name.
	 * @return the parameter value, or <code>null</code> if no such
	 * value exsists.
	 */
	public String getParameter(String paramName) {
		return (String) parameters.get(paramName);
	}
	
	/**
	 * Removes a parameter name and value.
	 * @param paramName the parameter name.
	 * @return the removed parameter value, or <code>null</code> if no such
	 * value exsists. 
	 */
	public String removeParameter(String paramName) {
		return (String) parameters.remove(paramName);
	}	
	
	/**
	 * Constructor pointing out the TTSBuilder configuration file and
	 * an additional map containing the names of parameters in the 
	 * configuration file together with their values.
	 * @param configFile the configuration file
	 * @param parameterSubst the parameter/value map.
	 */
	public TTSBuilder(File configFile, Map parameterSubst) {
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
				}
			}
		}
		DEBUG(config);
	}
	
	/**
	 * Returns a new TTS implementation for the language <code>lang</code>.
	 * If no such exists, what then? 
	 * @param lang the lower case two letter ISO 639 language code.
	 * @return a new TTS for language <code>lang</code> or
	 * a default if such exists. <code>null</code> otherwise.
	 *  
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws IOException 
	 */
	public TTS newTTS(String lang) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		parameters = new HashMap();
		String xpath = "/ttsbuilder/os/lang[@lang='" + lang + "']/tts";
		Element docElement = config.getDocumentElement();
		Element ttsElement = (Element) XPathUtils.selectSingleNode(docElement, xpath);
		
		if (null == ttsElement) {
			System.err.println("WARNING: No TTS found for language: " + 
					lang + ", an attempt to provide a default voice is made.");
			
			xpath = "/ttsbuilder/os/lang/tts[@default='true']";
			docElement = config.getDocumentElement();
			ttsElement = (Element) XPathUtils.selectSingleNode(docElement, xpath);;
			
			if (null == ttsElement) {
				System.err.println("WARNING: No default voice found.");
				return null;
			}
		}
		
		NodeList params = XPathUtils.selectNodes(ttsElement, "param");
		for (int i = 0; i < params.getLength(); i++) {
			Element elem = (Element) params.item(i);
			setParameter(elem.getAttribute("name"), elem.getAttribute("value"));
		}
		
		String fullClassName = getParameter(CLASS);
		String fullBinPath = getParameter(BINARY);
		if (null == fullClassName) {
			throw new IllegalArgumentException("The full class name must be privided for every TTS Java wrapper implementation, edit " + 
					configFile.getAbsolutePath() + 
					" in order to fix this problem. Tried to create tts for xml:lang= " + lang);
		}
		
		Class ttsClass = Class.forName(fullClassName);
		TTS tts = null;
		try {
			Class parameterList[] = new Class[]{Class.forName("java.util.Map")};
			Map constrParam[] = new Map[]{parameters};
			Constructor constructor = ttsClass.getConstructor(parameterList);			
			tts = (TTS) constructor.newInstance(constrParam);
		} catch (SecurityException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (InstantiationException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		if (null == tts) {
			tts = (TTS) ttsClass.newInstance();
			tts.setBinaryPath(new File(fullBinPath));
			tts.setParamMap(parameters);
		}
		
		DEBUG("Language=" + lang + ", parameters for TTS " + tts.getClass().getName() + ":");
		for (Iterator it = parameters.keySet().iterator(); it.hasNext();) {
			Object key = it.next();
			DEBUG("   " + key + " -->\t" + parameters.get(key));
		}
		DEBUG("");
		
		return tts;
	}
	
	private Document readXML(File file) {
		Document doc = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(file);
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
		return doc;
	}
	
	private void DEBUG(Document d) {
		if (DEBUG) {
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
	
	private void DEBUG(String msg) {
		if (DEBUG) {
			System.err.println("TTSBuilder: " + msg);
		}
	}
}
