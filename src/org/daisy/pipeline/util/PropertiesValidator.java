package org.daisy.pipeline.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamException;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.validation.XMLValidationException;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidationSchemaFactory;
import org.daisy.util.file.EFolder;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.stax.StaxEntityResolver;


/**
 * Validates all messages.properties files found under inparam directory (recursive)
 * @author Markus Gylling
 */
public class PropertiesValidator implements XMLReporter {

	public PropertiesValidator(String path) throws IOException, CatalogExceptionNotRecoverable {
		EFolder baseDir = new EFolder(path);		
		Collection<File> files = baseDir.getFiles(true, ".+\\.properties|.+\\.messages");				
        XMLValidationSchemaFactory sf = XMLValidationSchemaFactory.newInstance(XMLValidationSchema.SCHEMA_ID_DTD);
        URL schemaFile = CatalogEntityResolver.getInstance().resolveEntityToURL("http://java.sun.com/dtd/properties.dtd");
        XMLValidationSchema dtd = null;
        try {
            dtd = sf.createSchema(schemaFile);
        } catch (XMLStreamException xe) {
            System.err.println("Failed to process the DTD file ('"+schemaFile+"'): "+xe);
            System.exit(1);
        }
        
		for (File inputFile : files) {
	        try {
	            XMLInputFactory2 ifact = (XMLInputFactory2)XMLInputFactory.newInstance();
	            ifact.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
	            XMLStreamReader2 sr = ifact.createXMLStreamReader(inputFile);	
	            try {
	                sr.validateAgainst(dtd);
	                while (sr.hasNext()) {
	                    sr.next();
	                }
	            } catch (XMLValidationException vex) {
	                System.err.println(inputFile + "failed validation: "+ vex);
	            }
	        } catch (XMLStreamException xse) {
	            System.err.println(inputFile +" malformed: "+ xse);
	        }	        
		}    		
	}

	/**
	 * @param args first argument contains pathspec of dir to recursively search
	 * @throws IOException 
	 * @throws XMLStreamException 
	 * @throws CatalogExceptionNotRecoverable 
	 */
	public static void main(String[] args) throws IOException, CatalogExceptionNotRecoverable, XMLStreamException {		
		new PropertiesValidator(args[0]);
		System.err.println("Validation done.");
	}

	public void report(String arg0, String arg1, Object arg2, Location arg3) throws XMLStreamException {
		System.err.println(arg0 + arg1 +arg3.getLineNumber());
	}

}
