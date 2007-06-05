/*
 * Daisy Pipeline Copyright (C) 2007 Daisy Consortium
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

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.daisy.pipeline.core.event.CoreMessageEvent;
import org.daisy.pipeline.core.event.EventBus;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Parameter;
import org.daisy.pipeline.core.transformer.TransformerHandler;
import org.daisy.pipeline.core.transformer.TransformerHandlerLoader;
import org.daisy.pipeline.exception.TransformerDisabledException;
import org.daisy.util.file.TempFile;
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
 * A class responsible for for building and validating Script objects.
 * 
 * @author Linus Ericson
 */
public class Creator implements ErrorHandler, EntityResolver {

    private TransformerHandlerLoader mHandlerLoader;
    private boolean mValidationError = false;
    private URL mCurrentScriptURL = null;

    /**
     * Constructor.
     * 
     * @param loader a TransformerHandlerLoader
     */
    public Creator(TransformerHandlerLoader loader) {
        mHandlerLoader = loader;
    }

    /**
     * Create a new Script from a URL to a script file. A version 1.0 script
     * file will automatically be transformed into version 2.0 before it is
     * being processed.
     * 
     * @param url the script file
     * @return a Script object
     * @throws ScriptValidationException
     */
    public Script newScript(URL url) throws ScriptValidationException {
        Script script = null;
        mCurrentScriptURL = url;
        try {
            // Peek and convert from old script format if needed
            TempFile upgraded = this.upgrade(url);
            if (upgraded != null) {
                url = upgraded.getFile().toURI().toURL();
            }

            // Validate XML document
            if (!this.isXMLValid(url, "script-2.0.rng")) {
                throw new ScriptValidationException(
                        "System error: Chain not XML valid");
            }

            // Parse document
            Parser parser = Parser.getInstance();
            script = parser.newScript(url);

            // Delete temporary file
            if (upgraded != null) {
                upgraded.delete();
            }

            // Set TransformerHandlers
            this.setTransformerHandlers(script);

            // Validate parsed content
            if (!this.isValid(script)) {
                throw new ScriptValidationException(
                        "System error: Chain not valid");
            }
        } catch (IOException e) {
            throw new ScriptValidationException(e.getMessage(), e);
        } catch (XMLStreamException e) {
            throw new ScriptValidationException(e.getMessage(), e);
        }

        return script;
    }

    /**
     * Upgrade the script file.
     * 
     * @param url the URL of the script file
     * @return a temporary file containing the upgraded script, or null if the
     *         script didn't need an upgrade
     * @throws ScriptValidationException
     */
    private TempFile upgrade(URL url) throws ScriptValidationException {
        Peeker peeker = null;
        try {
            peeker = PeekerPool.getInstance().acquire();
            PeekResult result = peeker.peek(url);
            Attributes attrs = result.getRootElementAttributes();
            // Check for a version="1.0" attribute on the root element
            if ("1.0".equals(attrs.getValue("", "version"))) {

                // Make sure this is a valid version 1.0 script
                if (!this.isXMLValid(url, "script-1.0.rng")) {
                    throw new ScriptValidationException(
                            "Invalid version 1.0 script");
                }

                // Upgrade the script to version 2.0
                EventBus.getInstance().publish(
                        new CoreMessageEvent(this,
                                "Upgrading version 1.0 script file to 2.0",
                                MessageEvent.Type.INFO));
                TempFile temp = new TempFile();
                Source input = new StreamSource(url.openStream());
                Source sheet = new StreamSource(this.getClass()
                        .getResourceAsStream("script10to20.xsl"));
                Result res = new StreamResult(temp.getFile());

                Stylesheet.apply(input, sheet, res);
                return temp;
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
            } catch (PoolException e) {
            }
        }

        // The script wasn't detected as a version 1.0 script
        return null;
    }

    /**
     * Set the TransformerHandler for each task in the script.
     * 
     * @param script the script
     * @throws ScriptValidationException
     */
    private void setTransformerHandlers(Script script)
            throws ScriptValidationException {
        for (Task task : script.getTasks()) {
            try {
                TransformerHandler handler = mHandlerLoader
                        .getTransformerHandler(task.getName());
                if (handler != null) {
                    task.setTransformerHandler(handler);
                } else {
                    throw new ScriptValidationException(
                            "System error: No transformer found for task "
                                    + task.getName());
                }
            } catch (TransformerDisabledException e) {
                throw new ScriptValidationException(
                        "System error: Transformer " + task.getName()
                                + " is disabled", e);
            }
        }
    }

    /**
     * Check if this is a valid version script file
     * 
     * @param url and URL to the script file to check
     * @param schemaName name of the schema resource
     * @return true if the script file is valid, false otherwise
     * @throws ScriptValidationException
     */
    private boolean isXMLValid(URL url, String schemaName)
            throws ScriptValidationException {
        try {
            SimpleValidator validator = new SimpleValidator(
            		getClass().getResource(schemaName), this);
            validator.setResolver(this);
            return validator.validate(url) && !mValidationError;
        } catch (Exception e) {
            throw new ScriptValidationException(e.getMessage(), e);
        } 
    }

    /**
     * Perform some validation on the script object after it has been built.
     * 
     * @param script the Script object to validate
     * @return true if the script object is valid, false otherwise
     */
    private boolean isValid(Script script) {
        boolean result = true;
        result &= this.testUniquePropertyNames(script);
        result &= this.testAllRequiredTaskParametersExist(script);
        result &= this.testAllTaskParametersDefinedByTDF(script);
        return result;
    }

    /**
     * Checks if all properties defined in the script have unique names, i.e.
     * there are no duplicates.
     * 
     * @param script
     * @return
     */
    private boolean testUniquePropertyNames(Script script) {
        boolean result = true;
        Set<String> names = new HashSet<String>();
        for (AbstractProperty property : script.getProperties().values()) {
            String name = property.getName();
            if (names.contains(name)) {
                EventBus.getInstance().publish(
                        new CoreMessageEvent(this,
                                "System error: Property name " + name
                                        + " is not unique.",
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
    private boolean testAllRequiredTaskParametersExist(Script script) {
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
                                new CoreMessageEvent(this,
                                        "System error: Required parameter "
                                                + param.getName()
                                                + " not specified by chain",
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
    private boolean testAllTaskParametersDefinedByTDF(Script script) {
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
                                EventBus
                                        .getInstance()
                                        .publish(
                                                new CoreMessageEvent(
                                                        this,
                                                        "System error: Parameter "
                                                                + param
                                                                        .getName()
                                                                + " is hard-coded in the TDF and may not be specified in a script.",
                                                        MessageEvent.Type.WARNING,
                                                        MessageEvent.Cause.INPUT));
                                result = false;
                            }
                            break;
                        }
                    }
                    if (!matchingParam) {
                        EventBus
                                .getInstance()
                                .publish(
                                        new CoreMessageEvent(
                                                this,
                                                "System error: Parameter "
                                                        + taskParamName
                                                        + " is not defined in the TDF.",
                                                MessageEvent.Type.WARNING,
                                                MessageEvent.Cause.INPUT));
                        result = false;
                    }
                }
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
     */
    public void error(SAXParseException e) throws SAXException {
        saxWarn(e);
        mValidationError = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
     */
    public void fatalError(SAXParseException e) throws SAXException {
        saxWarn(e);
        mValidationError = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
     */
    public void warning(SAXParseException e) throws SAXException {
        saxWarn(e);
        // mValidationError = true;
    }

    private void saxWarn(SAXParseException e) {
        EventBus.getInstance().publish(
                new MessageEvent(this, "User error in " + e.getSystemId()
                        + " at line " + e.getLineNumber() + ": " + e,
                        MessageEvent.Type.WARNING, MessageEvent.Cause.INPUT));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String,
     *      java.lang.String)
     */
    public InputSource resolveEntity(String systemId, String publicId)
            throws SAXException, IOException {
        try {
            String id = systemId;
            if (id == null)
                id = publicId;
            URL url = mCurrentScriptURL.toURI().resolve(id).toURL();
            return new InputSource(url.openStream());
        } catch (Exception e) {

        }

        return null;
    }

}
