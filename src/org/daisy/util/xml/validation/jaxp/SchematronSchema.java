/*
 * org.daisy.util (C) 2005-2008 Daisy Consortium
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
package org.daisy.util.xml.validation.jaxp;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.validation.ValidatorHandler;

import org.daisy.util.exception.ExceptionTransformer;
import org.daisy.util.file.TempFile;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.peek.PeekResult;
import org.daisy.util.xml.peek.Peeker;
import org.daisy.util.xml.peek.PeekerPool;
import org.daisy.util.xml.pool.PoolException;
import org.daisy.util.xml.validation.SchemaLanguageConstants;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.XSLTException;
import org.daisy.util.xml.xslt.stylesheets.Stylesheets;
import org.xml.sax.SAXException;

/**
 * 
 * @author Markus Gylling
 */
public class SchematronSchema extends AbstractSchema implements ErrorListener {
    private TransformerFactory transformerFactory = null;
    private Transformer mRngTransformer = null;
    private Transformer mAnyTransformer = null;

    SchematronSchema(URL schema, SchemaFactory originator) throws IOException,
            TransformerConfigurationException, XSLTException, PoolException,
            SAXException {
        super(schema, originator);
        // clean to a single namespace doc and replace the super attributes with
        // result
        StreamSource ss = new StreamSource(super.schemaURL.openStream(),
                super.schemaURL.toExternalForm());
        super.sources = transform(new Source[] { ss });
        super.schemaURL = null;
    }

    SchematronSchema(Source[] sources, SchemaFactory originator)
            throws IOException, TransformerConfigurationException,
            XSLTException, PoolException, SAXException {
        super(sources, originator);
        // clean to a single namespace doc and replace the super attributes with
        // result
        super.sources = transform(super.sources);
    }

    public Validator newValidator() {
        SchematronValidator validator = new SchematronValidator(this);
        validator.propagateHandlers(this.originator);
        validator.initialize();
        return validator;
    }

    public ValidatorHandler newValidatorHandler() {
        return null;
    }

    /**
     * Strips possible foreign namespaces from input using XSLT.
     * 
     * @throws IOException
     * @throws TransformerConfigurationException
     * @throws XSLTException
     * @throws PoolException
     * @throws SAXException
     */
    private Source[] transform(Source[] inSources)
            throws TransformerConfigurationException, IOException,
            XSLTException, PoolException, SAXException {
        PeekerPool pool = PeekerPool.getInstance();

        if (null == transformerFactory) {
            transformerFactory = TransformerFactory.newInstance();
            try {
                transformerFactory.setAttribute(
                        "http://saxon.sf.net/feature/version-warning",
                        Boolean.FALSE);
            } catch (IllegalArgumentException iae) {
                // silent
            }
            transformerFactory.setErrorListener(this);
            // TODO fix below method in CatalogEntityResolver by incorporating
            // linus code as fallback
            transformerFactory.setURIResolver(CatalogEntityResolver
                    .getInstance());
            mRngTransformer = transformerFactory
                    .newTransformer(new StreamSource(Stylesheets.get(
                            "RNG2Schtrn.xsl").openStream()));
            mAnyTransformer = transformerFactory
                    .newTransformer(new StreamSource(Stylesheets.get(
                            "Any2Schtrn.xsl").openStream()));
        }

        Source[] outSources = new Source[inSources.length];
        for (int i = 0; i < inSources.length; i++) {
            File fileResult = TempFile.create();
            StreamResult streamResult = new StreamResult(fileResult);
            Peeker peeker = pool.acquire();
            PeekResult peekResult = peeker.peek(new URL(inSources[i]
                    .getSystemId()));
            if (peekResult.getRootElementNsUri().equals(
                    SchemaLanguageConstants.RELAXNG_NS_URI)) {
                Stylesheet.apply(inSources[i], mRngTransformer, streamResult,
                        null);
                outSources[i] = new StreamSource(fileResult);
            } else if (peekResult.getRootElementNsUri().equals(
                    SchemaLanguageConstants.SCHEMATRON_NS_URI)) {
                outSources[i] = inSources[i];
            } else {
                Stylesheet.apply(inSources[i], mAnyTransformer, streamResult,
                        null);
                outSources[i] = new StreamSource(fileResult);
            }

            pool.release(peeker);
        }
        return outSources;
    }

    @SuppressWarnings("unused")
    public void warning(TransformerException exception) throws TransformerException {
        if (null != this.originator.getErrorHandler()) {
            try {
                this.originator.getErrorHandler().warning(
                        ExceptionTransformer.newSAXParseException(exception));
                return;
            } catch (SAXException e) {
                System.err.println(e.getMessage());
            }
        }
        System.err.println(exception.getMessage());
    }

    @SuppressWarnings("unused")
    public void error(TransformerException exception) throws TransformerException {
        if (null != this.originator.getErrorHandler()) {
            try {
                this.originator.getErrorHandler().error(
                        ExceptionTransformer.newSAXParseException(exception));
                return;
            } catch (SAXException e) {
                System.err.println(e.getMessage());
            }
        }
        System.err.println(exception.getMessage());
    }

    @SuppressWarnings("unused")
    public void fatalError(TransformerException exception) throws TransformerException {
        if (null != this.originator.getErrorHandler()) {
            try {
                this.originator.getErrorHandler().fatalError(
                        ExceptionTransformer.newSAXParseException(exception));
                return;
            } catch (SAXException e) {
                System.err.println(e.getMessage());
            }
        }
        System.err.println(exception.getMessage());
    }

}