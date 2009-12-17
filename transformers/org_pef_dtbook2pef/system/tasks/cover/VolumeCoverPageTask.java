package org_pef_dtbook2pef.system.tasks.cover;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.stax.StaxEntityResolver;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import org_pef_dtbook2pef.system.InternalTask;

/**
 * Add a Volume Cover to each volume
 * @author joha
 *
 */
public class VolumeCoverPageTask extends InternalTask {
	private VolumeCoverPage cover;

	public VolumeCoverPageTask(String name, VolumeCoverPage cover) {
		super(name);
		this.cover = cover;
	}

	@Override
	public void execute(File input, File output)
			throws TransformerRunException {

        XMLInputFactory inFactory = XMLInputFactory.newInstance();
		inFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);        
        inFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        inFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.TRUE);
        inFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.TRUE);
        
    	try {
			inFactory.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
		} catch (CatalogExceptionNotRecoverable e1) {
			e1.printStackTrace();
		}
		
		try {
			Document d2 = initDocumentBuilder().parse(input);
			XPath xp = XPathFactory.newInstance().newXPath();
			int volumeCount = ((Double)xp.evaluate("count(//volume)", d2, XPathConstants.NUMBER)).intValue();
			VolumeCoverPageFilter pf = new VolumeCoverPageFilter(
					inFactory.createXMLEventReader(new FileInputStream(input)), 
					new FileOutputStream(output),
					cover, volumeCount);
			pf.filter();
			pf.close();
		} catch (FileNotFoundException e) {
			throw new TransformerRunException("FileNotFoundException:", e);
		} catch (XMLStreamException e) {
			throw new TransformerRunException("XMLStreamException:", e);
		} catch (IOException e) {
			throw new TransformerRunException("IOException:", e);
		} catch (SAXException e) {
			throw new TransformerRunException("SAXException:", e);
		} catch (ParserConfigurationException e) {
			throw new TransformerRunException("ParserConfigurationException:", e);
		} catch (XPathExpressionException e) {
			throw new TransformerRunException("XPathExpressionException:", e);
		}
	}
	
	protected DocumentBuilder initDocumentBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		try {
			db.setEntityResolver(CatalogEntityResolver.getInstance());
		} catch (CatalogExceptionNotRecoverable e) {
			ParserConfigurationException pce = new ParserConfigurationException("Unable to set CatalogEntityResolver");
			pce.initCause(e);
			throw pce;
		}
		return db;
	}

}
