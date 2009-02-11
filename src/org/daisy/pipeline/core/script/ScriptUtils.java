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
package org.daisy.pipeline.core.script;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.daisy.pipeline.core.event.CoreMessageEvent;
import org.daisy.pipeline.core.event.EventBus;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Parameter;
import org.daisy.pipeline.core.transformer.TransformerHandler;
import org.daisy.util.file.TempFile;
import org.daisy.util.i18n.I18n;
import org.daisy.util.text.URIUtils;
import org.daisy.util.xml.peek.PeekResult;
import org.daisy.util.xml.peek.Peeker;
import org.daisy.util.xml.peek.PeekerPool;
import org.daisy.util.xml.pool.PoolException;
import org.daisy.util.xml.validation.SimpleValidator;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.XSLTException;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Contains static utility methods used for script manipulation and validation.
 * 
 * @author Romain Deltour
 * 
 */
public final class ScriptUtils {

	private static final I18n i18n = new I18n();

	// prevent instantiation of this static utility
	private ScriptUtils() {}

	private static class InternalErrorHandler implements ErrorHandler {

		private boolean receivedError = false;

		public boolean hasReceivedError() {
			return receivedError;
		}

		public void error(SAXParseException e) throws SAXException {
			saxWarn(e);
			receivedError = true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
		 */
		public void fatalError(SAXParseException e) throws SAXException {
			saxWarn(e);
			receivedError = true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
		 */
		public void warning(SAXParseException e) throws SAXException {
			if (!e.getMessage().contains("XSLT 1.0")) {
				// temp hack to avoid saxon 8 version warning messages
				saxWarn(e);
			}
		}

		private void saxWarn(SAXParseException e) {
			EventBus.getInstance().publish(
					new MessageEvent(this, i18n.format("ERROR_SYSID",
							new Object[] { e.getSystemId(), e.getLineNumber(),
									e }), MessageEvent.Type.WARNING,
							MessageEvent.Cause.INPUT));
		}

	}

	private static class InternalEntityResolver implements EntityResolver {

		private URL scriptURL;

		public InternalEntityResolver(URL scriptURL) {
			this.scriptURL = scriptURL;
		}

		public InputSource resolveEntity(String systemId, String publicId)
				throws SAXException, IOException {
			try {
				String id = systemId;
				if (id == null)
					id = publicId;
				URL url = URIUtils.resolve(scriptURL.toURI(), id).toURL();
				return new InputSource(url.openStream());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	/**
	 * Check if this is a valid version script file
	 * 
	 * @param url
	 *            and URL to the script file to check
	 * @param schemaName
	 *            name of the schema resource
	 * @return true if the script file is valid, false otherwise
	 * @throws ScriptValidationException
	 */
	public static boolean isXMLValid(URL url, String schemaName)
			throws ScriptValidationException {
		try {
			InternalErrorHandler handler = new InternalErrorHandler();
			SimpleValidator validator = new SimpleValidator(ScriptUtils.class
					.getResource(schemaName), handler);
			validator.setResolver(new InternalEntityResolver(url));
			return validator.validate(url) && !handler.hasReceivedError();
		} catch (Exception e) {
			throw new ScriptValidationException(e.getMessage(), e);
		}
	}

	/**
	 * Perform some validation on the script object after it has been built.
	 * 
	 * @param script
	 *            the Script object to validate
	 * @return true if the script object is valid, false otherwise
	 */
	public static boolean isValid(Script script) {
		boolean result = true;
		result &= testUniquePropertyNames(script);
		result &= testAllRequiredTaskParametersExist(script);
		result &= testAllTaskParametersDefinedByTDF(script);
		return result;
	}

	/**
	 * Upgrade the script file.
	 * 
	 * @param url
	 *            the URL of the script file
	 * @return a temporary file containing the upgraded script, or null if the
	 *         script didn't need an upgrade
	 * @throws ScriptValidationException
	 */
	public static File upgrade(URL url) throws ScriptValidationException {
		Peeker peeker = null;
		try {
			peeker = PeekerPool.getInstance().acquire();
			PeekResult result = peeker.peek(url);
			Attributes attrs = result.getRootElementAttributes();
			// Check for a version="1.0" attribute on the root element
			if ("1.0".equals(attrs.getValue("", "version"))) {

				// Make sure this is a valid version 1.0 script
				if (!isXMLValid(url, "script-1.0.rng")) {
					throw new ScriptValidationException(
							"Invalid version 1.0 script");
				}

				// Upgrade the script to version 2.0
				EventBus.getInstance().publish(
						new CoreMessageEvent(ScriptUtils.class, i18n.format(
								"VERSION_UP", "1.0", "2.0"),
								MessageEvent.Type.INFO));
				TempFile temp = new TempFile();
				Source input = new StreamSource(url.openStream());
				Source sheet = new StreamSource(ScriptUtils.class
						.getResourceAsStream("script10to20.xsl"));
				Result res = new StreamResult(temp.getFile());

				Stylesheet.apply(input, sheet, res);
				return temp.getFile();
			}

		} catch (IOException e) {
			throw new ScriptValidationException(e.getMessage(), e);
		} catch (SAXException e) {
			throw new ScriptValidationException(e.getMessage(), e);
		} catch (PoolException e) {
			throw new ScriptValidationException(e.getMessage(), e);
		} catch (XSLTException e) {
			throw new ScriptValidationException(e.getMessage(), e);
		} finally {
			try {
				PeekerPool.getInstance().release(peeker);
			} catch (PoolException e) {}
		}

		// The script wasn't detected as a version 1.0 script
		return null;
	}

	/**
	 * Checks if all properties defined in the script have unique names, i.e.
	 * there are no duplicates.
	 * 
	 * @param script
	 * @return
	 */
	private static boolean testUniquePropertyNames(Script script) {
		boolean result = true;
		Set<String> names = new HashSet<String>();
		for (AbstractProperty property : script.getProperties().values()) {
			String name = property.getName();
			if (names.contains(name)) {
				EventBus.getInstance().publish(
						new CoreMessageEvent(ScriptUtils.class, i18n.format(
								"UNIQUE_PROP", name),
								MessageEvent.Type.WARNING,
								MessageEvent.Cause.INPUT));
				result = false;
			}
			names.add(name);
		}
		return result;
	}

	/**
	 * Checks if all parameters required by each transformer (as defined in the
	 * respective transformer description file) are defined in each task
	 * 
	 * @param script
	 * @return
	 */
	private static boolean testAllRequiredTaskParametersExist(Script script) {
		boolean result = true;
		// Loop over all tasks
		for (Task task : script.getTasks()) {
			// Find TransformerHandler
			TransformerHandler handler = task.getTransformerHandler();
			if (handler != null) {
				Collection<Parameter> parameters = handler.getParameters();
				// Loop over all transformer parameters
				for (Parameter param : parameters) {
					if (param.isRequired()
							&& !task.getParameters().containsKey(
									param.getName())) {
						EventBus.getInstance().publish(
								new CoreMessageEvent(ScriptUtils.class, i18n
										.format("MISSING_PARAM", param
												.getName()),
										MessageEvent.Type.WARNING,
										MessageEvent.Cause.INPUT));
						result = false;
					}
				}
			}
		}
		return result;
	}

	/**
	 * Checks if all parameters defined in the tasks are defined by the TDF.
	 * 
	 * @param script
	 * @return
	 */
	private static boolean testAllTaskParametersDefinedByTDF(Script script) {
		boolean result = true;
		// Loop over all tasks
		for (Task task : script.getTasks()) {
			// Find TransformerHandler
			TransformerHandler handler = task.getTransformerHandler();
			if (handler != null) {
				// Loop over task parameters
				for (TaskParameter taskParam : task.getParameters().values()) {
					String taskParamName = taskParam.getName();
					// Find the matching handler param
					boolean matchingParam = false;
					Collection<Parameter> parameters = handler.getParameters();
					for (Parameter param : parameters) {
						if (taskParamName.equals(param.getName())) {
							matchingParam = true;
							// Is this a hard-coded transformer param?
							if (param.getValue() != null) {
								EventBus.getInstance().publish(
										new CoreMessageEvent(ScriptUtils.class,
												i18n.format("HARD_CODED_PARAM",
														param.getName()),
												MessageEvent.Type.WARNING,
												MessageEvent.Cause.INPUT));
								result = false;
							}
							break;
						}
					}
					if (!matchingParam) {
						EventBus.getInstance().publish(
								new CoreMessageEvent(ScriptUtils.class, i18n
										.format("UNDEFINED_PARAM",
												taskParamName),
										MessageEvent.Type.WARNING,
										MessageEvent.Cause.INPUT));
						result = false;
					}
				}
			}
		}
		return result;
	}
}
