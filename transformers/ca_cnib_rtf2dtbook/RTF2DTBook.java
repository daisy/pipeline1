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
package ca_cnib_rtf2dtbook;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.file.TempFile;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.XSLTException;
import org.xml.sax.EntityResolver;

import com.tetrasix.majix.rtf.RtfStyleMap;
import com.tetrasix.majix.templates.ConversionTemplateFactory;
import com.tetrasix.majix.xml.ConversionTemplate;
import com.tetrasix.majix.xml.Converter;
import com.tetrasix.majix.xml.XmlGeneratorParam;
import com.tetrasix.util.Configuration;

/**
 * @author Linus Ericson
 */
public class RTF2DTBook extends Transformer {

    public RTF2DTBook(InputListener inListener, Set eventListeners, Boolean isInteractive) {
        super(inListener, eventListeners, isInteractive);        
    }

    protected boolean execute(Map parameters) throws TransformerRunException {
        // Read parameters
        String rtfFile = (String)parameters.remove("rtf");
        String dtbookFile = (String)parameters.remove("dtbook");
        String styleMapFile = (String)parameters.remove("stylemap");
        String tagMapFile = (String)parameters.remove("tagmap");
        String templateName = (String)parameters.remove("template");
        String stylesheet = (String)parameters.remove("stylesheet");
        String xsltFactory = (String)parameters.remove("factory"); 

        Configuration.init(this.getTransformerDirectory() + File.separator + "majix", false);

        // Setup conversion template
        ConversionTemplateFactory factory = new ConversionTemplateFactory();
        ConversionTemplate template = factory.load(templateName);        
        if (template == null) {
            throw new TransformerRunException(i18n("CONVERSION_TEMPLATE_ERROR"));
        }

        // Setup stylemap
        if (styleMapFile != null) {
			RtfStyleMap stylemap = new RtfStyleMap(styleMapFile);
			template.initRtfAbstractStyleSheet(stylemap.getRtfAbstractStylesheet());
			template.setStyleMap(stylemap);
		}		

        // Setup tag map        
        if (tagMapFile != null) {
			template.setGeneratorParam(new XmlGeneratorParam(template, tagMapFile));
		}
		
        // Output the XML into a temporary file
        TempFile xmlFile;
        try {
            xmlFile = new TempFile();
        } catch (IOException e) {
            throw new TransformerRunException(i18n("CANNOT_CREATE_TEMP_FILE"), e);
        }

        // Perform Majix conversion
        sendMessage(Level.FINER, i18n("RUNNING_MAJIX"));
        boolean result = Converter.Convert(template, rtfFile, xmlFile.getFile().getAbsolutePath(), false, false, null);
        if (!result) {
            return false;
        }
                
        // Finish up with some XSLT
        sendMessage(Level.FINER, i18n("APPLYING_XSLT"));
        try {
            EntityResolver resolver = CatalogEntityResolver.getInstance();
            Stylesheet.apply(xmlFile.getFile().getAbsolutePath(), stylesheet, dtbookFile, xsltFactory, null, resolver);
        } catch (XSLTException e) {
            throw new TransformerRunException(i18n("ERROR_APPLYING_XSLT"), e);
        } catch (CatalogExceptionNotRecoverable e) {
            throw new TransformerRunException(i18n("ENTITY_RESOLVER_ERROR"), e);
        }
        
        return result;
    }

}
