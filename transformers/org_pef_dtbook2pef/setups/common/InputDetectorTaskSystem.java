package org_pef_dtbook2pef.setups.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;

import org.daisy.pipeline.core.event.EventBus;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.util.xml.peek.PeekResult;
import org.daisy.util.xml.peek.Peeker;
import org.daisy.util.xml.peek.PeekerPool;
import org.xml.sax.SAXException;
import org_pef_dtbook2pef.DTBook2PEF.OutputFormat;
import org_pef_dtbook2pef.system.InternalTask;
import org_pef_dtbook2pef.system.TaskSystem;
import org_pef_dtbook2pef.system.TaskSystemException;
import org_pef_dtbook2pef.system.tasks.ValidatorTask;
import org_pef_dtbook2pef.system.tasks.XsltTask;

/**
 * The InputDetectorTaskSystem is a TaskSystem that tries to determine the
 * input format and load the appropriate settings based on the detected 
 * input format.
 * 
 * It can be used as a first step in a TaskSystem, if input format detection
 * is desired.
 * 
 * Resources are located in the following order: 
 * resourceBase/localBase/[output format]/[input format].properties
 * resourceBase/localBase/[output format]/xml.properties
 * resourceBase/commonBase/[output format]/[input format].properties
 * resourceBase/commonBase/[output format]/xml.properties
 * 
 * Currently supported formats are: DTBook and xml (heuristic block detection, no layout).
 * 
 * @author joha
 *
 */
public class InputDetectorTaskSystem implements TaskSystem {
	private final URL resourceBase;
	private final String localBase;
	private final String commonBase;

	/**
	 * Create a new InputDetectorTaskSystem. 
	 * @param resourceBase the resource root URL 
	 * @param localBase a path relative the resource root to the local resources
	 * @param commonBase a path relative the resource root to the common resources
	 */
	public InputDetectorTaskSystem(URL resourceBase, String localBase, String commonBase) {
		this.resourceBase = resourceBase;
		this.localBase = localBase;
		this.commonBase = commonBase;
	}

	public ArrayList<InternalTask> compile(Map<String, String> parameters)
			throws TaskSystemException {
		
		ArrayList<InternalTask> setup = new ArrayList<InternalTask>();
		
		String input = parameters.get("input");
		String inputformat = null;
		Peeker peeker = null;
		try{
			PeekResult peekResult;
			peeker = PeekerPool.getInstance().acquire();
			FileInputStream is = new FileInputStream(new File(input));
			peekResult = peeker.peek(is);
			String rootNS = peekResult.getRootElementNsUri();
			String rootElement = peekResult.getRootElementLocalName();
			if (rootNS!=null) {
				if (rootNS.equals("http://www.daisy.org/z3986/2005/dtbook/") && rootElement.equals("dtbook")) {
					inputformat = "dtbook.properties";
				} // else if {
					// Add more input formats here...
				// }
			}
			is.close();
		} catch (SAXException e) {
			throw new TaskSystemException("SAXException while reading input", e);
		} catch (IOException e) {
			throw new TaskSystemException("IOException while reading input", e);
		}  finally {
			if (peeker!=null) {
				PeekerPool.getInstance().release(peeker);
			}
		}
		String xmlformat = "xml.properties";
		String outputformat = OutputFormat.valueOf(parameters.get("outputFormat").toUpperCase()).toString().toLowerCase();
		try {
			URL localBaseURL = new URL(new URL(resourceBase, localBase), outputformat + "/");
			URL commonBaseURL = new URL(new URL(resourceBase, commonBase), outputformat + "/");
			ArrayList<URL> urls = new ArrayList<URL>();
			if (inputformat!=null) {
				urls.add(new URL(localBaseURL, inputformat));
			}
			urls.add(new URL(localBaseURL, xmlformat));
			if (inputformat!=null) {
				urls.add(new URL(commonBaseURL, inputformat));
			}
			urls.add(new URL(commonBaseURL, xmlformat));
			InputStream propsStream = null;
			for (URL t : urls) {
				try {
					propsStream = t.openStream();
					EventBus.getInstance().publish(
							new MessageEvent(this, "Opening stream: " + t.getFile(), MessageEvent.Type.DEBUG, MessageEvent.Cause.SYSTEM, null)
						);
					break;
				} catch (IOException e) {
					EventBus.getInstance().publish(
							new MessageEvent(this, "Cannot open stream: " + t.getFile(), MessageEvent.Type.DEBUG, MessageEvent.Cause.SYSTEM, null)
						);
				}
			}
			if (propsStream != null) {
				Properties p = new Properties();
				p.loadFromXML(propsStream);
				propsStream.close();

				HashMap h = new HashMap();
				h.putAll(p);
				HashMap xsltProps = new HashMap();
				xsltProps.putAll(parameters);
				for (Object key : p.keySet()) {
					String[] schemas = p.get(key).toString().split("\\s*,\\s*");
					if ("validation".equals(key.toString())) {
						for (String s : schemas) {
							if (s!=null && s!="") {
								setup.add(new ValidatorTask("Conformance checker: " + s, new URL(resourceBase, s)));
							}
						}
					} else if ("transformation".equals(key.toString())) {
						for (String s : schemas) {
							if (s!=null && s!="") {
								setup.add(new XsltTask("DTBook to FLOW converter: " + s, new URL(resourceBase, s), null, xsltProps));
							}
						}
					} else {
						EventBus.getInstance().publish(
							new MessageEvent(this, "Unrecognized key: " + key, MessageEvent.Type.INFO, MessageEvent.Cause.SYSTEM, null)
						);
					}
				}
			} else {
				throw new TaskSystemException("Unable to open a configuration stream for the format.");
			}

		} catch (MalformedURLException e) {
			throw new TaskSystemException(e);
		} catch (InvalidPropertiesFormatException e) {
			throw new TaskSystemException("Unable to read settings file.", e);
		} catch (IOException e) {
			throw new TaskSystemException("Unable to open settings file.", e);
		}

		return setup;
	}

}
