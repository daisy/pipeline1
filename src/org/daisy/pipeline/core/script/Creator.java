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

import javax.xml.stream.XMLStreamException;

import org.daisy.pipeline.core.transformer.TransformerHandler;
import org.daisy.pipeline.core.transformer.TransformerHandlerLoader;
import org.daisy.pipeline.exception.TransformerDisabledException;

/**
 * A class responsible for for building and validating Script objects.
 * 
 * @author Linus Ericson
 */
public class Creator {

	private ScriptParser parser = new ScriptParser();

    /**
     * Constructor.
     *
     */
    public Creator() {
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
        try {
            // Peek and convert from old script format if needed
            File upgraded = ScriptUtils.upgrade(url);
            if (upgraded != null) {
                url = upgraded.toURI().toURL();
            }

            // Validate XML document
            if (!ScriptUtils.isXMLValid(url, "script-2.0.rng")) {
                throw new ScriptValidationException(
                        "System error: Chain not XML valid");
            }

            // Parse document
            script = parser.newScript(url);

            // Delete temporary file
            if (upgraded != null) {
                upgraded.delete();
            }

            // Set TransformerHandlers
            this.setTransformerHandlers(script);

            // Validate parsed content
            if (!ScriptUtils.isValid(script)) {
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
     * Set the TransformerHandler for each task in the script.
     * 
     * @param script the script
     * @throws ScriptValidationException
     */
    private void setTransformerHandlers(Script script)
            throws ScriptValidationException {
        for (Task task : script.getTasks()) {
            try {
                TransformerHandler handler = getTransformerHandler(task.getName());
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

    protected TransformerHandler getTransformerHandler(String name)
            throws TransformerDisabledException {
        return TransformerHandlerLoader.INSTANCE.getTransformerHandler(name);
    }
}
