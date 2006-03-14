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
package org.daisy.util.xml.validation;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.daisy.util.file.FileUtils;
import org.daisy.util.file.TempFile;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.thaiopensource.util.PropertyMapBuilder;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.ValidationDriver;

/**
 * Validate an XML document using a RELAXNG schema with embedded Schematron
 * rules.
 * 
 * @author Linus Ericson
 * @author Markus Gylling
 */
public class RelaxngSchematronValidator implements Validator, ErrorHandler {

    private ValidationDriver relaxngDriver = null;

    private ValidationDriver schematronDriver = null;

    /**
     * <p>
     * Creates a new RELAXNG/Schematron validator using the specified schema.
     * </p>
     * <p>
     * This constructor assumes that the schema is compound and contains tests
     * in RelaxNG and Schematron namespaces.
     * </p>
     * 
     * @param schemaFile
     *            a RELAXNG schema, possibly with embedded Schematron rules
     * @param errh
     *            an imlementation of the SAX ErrorHandler interface; if fed to
     *            constructor as null, validation errors are reported to
     *            system.err
     * @param useSchematron
     *            tells whether Schematron validation should be performed
     * @param useRelaxNG
     *            tells whether RelaxNG validation should be performed
     * @throws ValidationException
     */
    public RelaxngSchematronValidator(File schemaFile, ErrorHandler errh,
            boolean useRelaxNG, boolean useSchematron)
            throws ValidationException {

        try {
            InputSource is = new InputSource(new FileReader(schemaFile));
            is.setSystemId(schemaFile.toURL().toString());
            initialize(is, errh, useRelaxNG, useSchematron, true);
        } catch (ValidationException e) {
            throw e;
        } catch (IOException e) {
            throw new ValidationException(e.getMessage(), e);
        }
    }

    /**
     * <p>
     * Creates a new RELAXNG/Schematron validator using the specified schema.
     * </p>
     * <p>
     * This constructor assumes that the schema is compound and contains tests
     * in RelaxNG and Schematron namespaces.
     * </p>
     * 
     * @param schemaURL
     *            URL of a RELAXNG schema, possibly with embedded Schematron
     *            rules
     * @param errh
     *            an imlementation of the SAX ErrorHandler interface; if fed to
     *            constructor as null, validation errors are reported to
     *            system.err
     * @throws ValidationException
     */

    public RelaxngSchematronValidator(URL schemaUrl, ErrorHandler errh)
            throws ValidationException {
        try {
            InputSource is = new InputSource(schemaUrl.openConnection()
                    .getInputStream());
            is.setSystemId(schemaUrl.toString());
            initialize(is, errh, true, true, true);
        } catch (ValidationException e) {
            throw e;
        } catch (IOException e) {
            throw new ValidationException(e.getMessage(), e);
        }
    }

    /**
     * Creates a new RELAXNG/Schematron validator using the specified schema
     * <p>
     * This constructor assumes that the schema is compound and contains tests
     * in RelaxNG and Schematron namespaces.
     * </p>
     * @param schemaURL
     *            URL of a RELAXNG schema, possibly with embedded Schematron
     *            rules
     * @param errh
     *            an imlementation of the SAX ErrorHandler interface; if fed to
     *            constructor as null, validation errors are reported to
     *            system.err
     * @param useSchematron
     *            tells whether Schematron validation should be performed
     * @param useRelaxNG
     *            tells whether RelaxNG validation should be performed
     * @throws ValidationException
     */

    public RelaxngSchematronValidator(URL schemaUrl, ErrorHandler errh,
            boolean useRelaxNG, boolean useSchematron)
            throws ValidationException {
        try {
            InputSource is = new InputSource(schemaUrl.openConnection()
                    .getInputStream());
            is.setSystemId(schemaUrl.toString());
            initialize(is, errh, useRelaxNG, useSchematron, true);
        } catch (ValidationException e) {
            throw e;
        } catch (IOException e) {
            throw new ValidationException(e.getMessage(), e);
        }
    }

    /**
     * <p>
     * Creates a new RELAXNG/Schematron validator using the specified schema.
     * </p>
     * <p>
     * This constructor assumes that the schema is compound and contains tests
     * in RelaxNG and Schematron namespaces.
     * </p>
     * 
     * @param schemaPublicOrSystemId
     *            Public or System ID of a RELAXNG schema, possibly with
     *            embedded Schematron rules
     * @param errh
     *            an imlementation of the SAX ErrorHandler interface; if fed to
     *            constructor as null, validation errors are reported to
     *            system.err
     * @param useSchematron
     *            tells whether Schematron validation should be performed
     * @param useRelaxNG
     *            tells whether RelaxNG validation should be performed
     * @throws ValidationException
     */
    public RelaxngSchematronValidator(String schemaPublicOrSystemId,
            ErrorHandler errh) throws ValidationException {

        try {
            initialize(CatalogEntityResolver.getInstance().resolveEntity(
                    schemaPublicOrSystemId, schemaPublicOrSystemId), errh,
                    true, true, true);
        } catch (CatalogExceptionNotRecoverable e) {
            throw new ValidationException(e.getMessage(), e);
        } catch (IOException e) {
            throw new ValidationException(e.getMessage(), e);
        }
    }

    /**
     * <p>
     * Creates a new RELAXNG/Schematron validator using the specified schema.
     * </p>
     * <p>
     * This constructor assumes that the schema is compound and contains tests
     * in RelaxNG and Schematron namespaces.
     * </p>
     * 
     * @param schemaPublicId
     *            Public ID of a RELAXNG schema, possibly with embedded
     *            Schematron rules
     * @param schemaSystemId
     *            System ID of a RELAXNG schema, possibly with embedded
     *            Schematron rules
     * 
     * @param errh
     *            an imlementation of the SAX ErrorHandler interface; if fed to
     *            constructor as null, validation errors are reported to
     *            system.err
     * @throws ValidationException
     */
    public RelaxngSchematronValidator(String schemaPublicId,
            String schemaSystemId, ErrorHandler errh)
            throws ValidationException {

        try {
            initialize(CatalogEntityResolver.getInstance().resolveEntity(
                    schemaPublicId, schemaSystemId), errh, true, true, true);
        } catch (CatalogExceptionNotRecoverable e) {
            throw new ValidationException(e.getMessage(), e);
        } catch (IOException e) {
            throw new ValidationException(e.getMessage(), e);
        }
    }

    /**
     * <p>
     * Creates a new RELAXNG/Schematron validator using the specified schema.
     * </p>
     * <p>
     * This constructor assumes that the schema is compound and contains tests
     * in RelaxNG and Schematron namespaces.
     * </p>
     * 
     * @param schemaPublicId
     *            Public ID of a RELAXNG schema, possibly with embedded
     *            Schematron rules
     * @param schemaSystemId
     *            System ID of a RELAXNG schema, possibly with embedded
     *            Schematron rules
     * 
     * @param errh
     *            an imlementation of the SAX ErrorHandler interface; if fed to
     *            constructor as null, validation errors are reported to
     *            system.err
     * @param useSchematron
     *            tells whether Schematron validation should be performed
     * @param useRelaxNG
     *            tells whether RelaxNG validation should be performed
     * @throws ValidationException
     */
    public RelaxngSchematronValidator(String schemaPublicId,
            String schemaSystemId, ErrorHandler errh, boolean useRelaxNG,
            boolean useSchematron) throws ValidationException {

        try {
            initialize(CatalogEntityResolver.getInstance().resolveEntity(
                    schemaPublicId, schemaSystemId), errh, useRelaxNG,
                    useSchematron, true);
        } catch (CatalogExceptionNotRecoverable e) {
            throw new ValidationException(e.getMessage(), e);
        } catch (IOException e) {
            throw new ValidationException(e.getMessage(), e);
        }
    }
    
    /**
     * <p>Creates a new Schematron validator using the specified schema string.</p>
     * <p>Note - the string is not a pathspec but the schema document data.</p>
     * <p>This constructor assumes that the input string is non-compound</p>
    **/
    
    public RelaxngSchematronValidator(ErrorHandler errh, String schSchemaData) throws ValidationException {                        
        InputSource is;
        try {
            TempFile f = new TempFile();            
            FileUtils.writeStringToFile(f.getFile(),schSchemaData,"utf-8");            
            is = new InputSource(f.getFile().toURL().openConnection().getInputStream());
            is.setSystemId(f.getFile().toURL().toString());        
            initialize(is,errh,false,true,false);
            f.delete();
        } catch (IOException e) {
            throw new ValidationException(e.getMessage(), e);
        }        
    }

    private void initialize(InputSource schema, ErrorHandler errh,
            boolean useRelaxNG, boolean useSchematron, boolean isCompound)
            throws ValidationException {
        PropertyMapBuilder builder = new PropertyMapBuilder();

        // set the errorhandler
        if (null != errh) {
            builder.put(ValidateProperty.ERROR_HANDLER, errh);
        } else {
            // fall back on self if user did not supply an errorhandler
            builder.put(ValidateProperty.ERROR_HANDLER, this);
        }

        // mg: Implement XmlReaderCreator in order to hook up the DTD catalog
        // ValidateProperty.ENTITY_RESOLVER does not seem to work
        XmlReaderCreatorImpl xrc = new XmlReaderCreatorImpl(false);
        builder.put(ValidateProperty.XML_READER_CREATOR, xrc);

        try {
            if (useRelaxNG) {
                // Load RELAXNG schema
                relaxngDriver = new ValidationDriver(builder.toPropertyMap());
                try {
                    if (!relaxngDriver.loadSchema(schema)) {
                        throw new ValidationException(
                                "Cannot load RELAXNG schema "
                                        + schema.getSystemId());
                    }
                } catch (Exception e) {
                    throw new ValidationException("Cannot load RELAXNG schema "
                            + schema.getSystemId(), e);
                }
            }

            if (useSchematron) {

                InputSource schSource;
                TempFile schematronSchema = new TempFile();

                if (isCompound) {
                    // Use XSLT to strip out Schematron rules
                    if (schema.getSystemId() == null) {
                        System.err
                                .println("warning: schema.getSystemId()==null in RelaxngSchematronValidator");
                    }

                    Source xml = new StreamSource(new InputStreamReader(schema
                            .getByteStream()), schema.getSystemId());

                    Source xslt = new StreamSource(this.getClass()
                            .getResourceAsStream("RNG2Schtrn.xsl"));
                    TransformerFactory factory = TransformerFactory
                            .newInstance();

                    Transformer transformer = factory.newTransformer(xslt);
                    transformer.transform(xml, new StreamResult(
                            schematronSchema.getFile().getCanonicalPath()));
                    schSource = new InputSource(schematronSchema.getFile()
                            .toURL().toString());
                } else {
                    // just pass along the method inputparam InputSource
                    schSource = schema;
                }

                // Try to load Schematron schema
                schematronDriver = new ValidationDriver(builder.toPropertyMap());

                if (!schematronDriver.loadSchema(schSource)) {
                    throw new ValidationException(
                            "Cannot load Schematron schema "
                                    + schema.getSystemId());
                }

                // Delete temporary schematron file
                schematronSchema.delete();
            }
        } catch (SAXException e) {
            throw new ValidationException("Parsing: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new ValidationException("Parsing: " + e.getMessage(), e);
        } catch (TransformerConfigurationException e) {
            throw new ValidationException("Parsing: " + e.getMessage(), e);
        } catch (TransformerException e) {
            throw new ValidationException("Parsing: " + e.getMessage(), e);
        }

    }

    public boolean isValid(File xml) throws ValidationException {
        boolean rngResult = true;
        boolean schResult = true;

        if ((relaxngDriver == null) && (schematronDriver == null)) {
            throw new ValidationException("Validation: ",
                    new ValidationException("no driver loaded"));
        }

        try {
            if (relaxngDriver != null) {
                rngResult = relaxngDriver.validate(ValidationDriver
                        .fileInputSource(xml));
            }
            if (schematronDriver != null) {
                schResult = schematronDriver.validate(ValidationDriver
                        .fileInputSource(xml));
            }
        } catch (SAXException e) {
            throw new ValidationException("Validation: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new ValidationException("Validation: " + e.getMessage(), e);
        }

        return rngResult && schResult ? true : false;
    }

    public void warning(SAXParseException e) throws SAXException {
        printMessage("Warning", e);
    }

    public void error(SAXParseException e) throws SAXException {
        printMessage("Error", e);
    }

    public void fatalError(SAXParseException e) throws SAXException {
        printMessage("Fatal error", e);
    }

    private void printMessage(String type, SAXParseException spe) {
        StringBuffer sb = new StringBuffer();
        sb.append(type);
        sb.append(" in ");
        sb.append(spe.getSystemId());
        sb.append(": ");
        sb.append(spe.getMessage());
        sb.append(". Line:" + spe.getLineNumber());
        sb.append(" Column:" + spe.getColumnNumber());
        System.err.println(sb.toString());
    }
}