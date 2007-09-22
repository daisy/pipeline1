package org.daisy.pipeline.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.InvalidPropertiesFormatException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.daisy.pipeline.core.DMFCCore;
import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.RequestEvent;
import org.daisy.pipeline.core.event.UserReplyEvent;
import org.daisy.pipeline.core.script.Script;
import org.daisy.pipeline.core.transformer.TransformerHandler;
import org.daisy.pipeline.core.transformer.TransformerInfo;
import org.daisy.pipeline.exception.DMFCConfigurationException;
import org.daisy.pipeline.exception.TransformerDisabledException;
import org.daisy.util.file.EFolder;
import org.daisy.util.i18n.XMLProperties;
import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.daisy.util.xml.pool.StAXOutputFactoryPool;

/**
 * Generate an XHTML doc with summaries of functionality, using TDFs and Script introductions
 * @author Markus Gylling
 */
public class SummaryGenerator implements InputListener {
	
	private static final String xhtmlNS = "http://www.w3.org/1999/xhtml"; 
	private static final String br = "\n";
	
	public SummaryGenerator(EFolder pipelineMainDir, File destination) throws IOException, XMLStreamException, DMFCConfigurationException {
		XMLEventFactory xef = null;
		XMLOutputFactory xof = null;
		Map outMap = StAXOutputFactoryPool.getInstance().getDefaultPropertyMap();
		outMap.put(XMLOutputFactory.IS_REPAIRING_NAMESPACES, Boolean.FALSE);
		checkSystemProperties(pipelineMainDir);
		try{
			List<XMLEvent> doc = new LinkedList<XMLEvent>();
			EFolder dir = pipelineMainDir;
			
			xef = StAXEventFactoryPool.getInstance().acquire();
			
			generateHeader(doc,xef);			
			generateScriptSummary(doc,dir,xef,destination);
			generateTransformerSummary(doc,dir,xef,destination);		
			generateFooter(doc,xef);
			
			xof =  StAXOutputFactoryPool.getInstance().acquire(outMap);
			
			XMLEventWriter xew = xof.createXMLEventWriter(new FileOutputStream(destination));
			
			for(XMLEvent x : doc) {
//				if(x.getEventType() == XMLEvent.START_DOCUMENT) System.err.println("[START]");
//				if(x.getEventType() == XMLEvent.END_DOCUMENT) System.err.println("[END]");
//				if(x.getEventType() == XMLEvent.CHARACTERS) System.err.println(x.asCharacters().getData());
//				if(x.getEventType() == XMLEvent.START_ELEMENT) System.err.println(x.asStartElement().getName().getLocalPart());
//				if(x.getEventType() == XMLEvent.END_ELEMENT) System.err.println("/"+x.asEndElement().getName().getLocalPart());
//				if(x.getEventType() == XMLEvent.ATTRIBUTE) System.err.println(((Attribute)x).getValue() + ((Attribute)x).getName().toString());
				xew.add(x);				
			}
			xew.flush();
			xew.close();
			
		}finally{
			StAXEventFactoryPool.getInstance().release(xef);
			StAXOutputFactoryPool.getInstance().release(xof, outMap);
		}
				
	}


	private void print(List<XMLEvent> list) {
		for (XMLEvent x : list) {
		if(x.getEventType() == XMLEvent.START_DOCUMENT) System.err.println("[START]");
		if(x.getEventType() == XMLEvent.END_DOCUMENT) System.err.println("[END]");
		if(x.getEventType() == XMLEvent.CHARACTERS) System.err.println(x.asCharacters().getData());
		if(x.getEventType() == XMLEvent.START_ELEMENT) System.err.println(x.asStartElement().getName().getLocalPart());
		if(x.getEventType() == XMLEvent.END_ELEMENT) System.err.println("/"+x.asEndElement().getName().getLocalPart());
		}
	}

	private void generateTransformerSummary(List<XMLEvent> doc, EFolder dir, XMLEventFactory xef, File destination) throws IOException {		
		
		URI dest = destination.getParentFile().toURI();
		
		doc.add(xef.createStartElement(new QName(xhtmlNS, "h2"),null,null));
		doc.add(xef.createAttribute(new QName(xhtmlNS, "id"), "transformers"));
		doc.add(xef.createCharacters(br));
		
		doc.add(xef.createCharacters("Transformer Summary"));
		doc.add(xef.createEndElement(new QName(xhtmlNS, "h2"),null));
		
		doc.add(xef.createStartElement(new QName(xhtmlNS, "dl"),null,null));
		doc.add(xef.createCharacters(br));
		
		EFolder transformersDir = new EFolder(dir, "transformers");
		Collection<File> c = transformersDir.getFiles(true, ".+\\.[Tt][Dd][Ff]$");
		List<File> tdfs = new LinkedList<File>(c);
		Collections.sort(tdfs);
						
		for(File tdf : tdfs) {
			try {
				TransformerHandler th = new TransformerHandler(tdf,transformersDir,this);
				TransformerInfo info = th; 

				doc.add(xef.createStartElement(new QName(xhtmlNS, "dt"),null,null));
				doc.add(xef.createAttribute(new QName(xhtmlNS, "id"), info.getPackageName()));
				doc.add(xef.createCharacters(info.getNiceName()));
				doc.add(xef.createEndElement(new QName(xhtmlNS, "dt"),null));
				doc.add(xef.createCharacters(br));
				
				doc.add(xef.createStartElement(new QName(xhtmlNS, "dd"),null,null));
				doc.add(xef.createCharacters(br));
				
				doc.add(xef.createStartElement(new QName(xhtmlNS, "p"),null,null));
				doc.add(xef.createCharacters(info.getDescription()));
				doc.add(xef.createEndElement(new QName(xhtmlNS, "p"),null));
				doc.add(xef.createCharacters(br));
				
				doc.add(xef.createStartElement(new QName(xhtmlNS, "p"),null,null));
				doc.add(xef.createCharacters("Documentation: "));
				if(info.getDocumentation()!=null){
					dest.normalize();
					info.getDocumentation().normalize();
					URI relative = dest.relativize(info.getDocumentation());															
					doc.add(xef.createStartElement(new QName(xhtmlNS, "a"),null,null));
					doc.add(xef.createAttribute(new QName(xhtmlNS, "href"), relative.toString()));
					doc.add(xef.createCharacters(relative.toString()));
					doc.add(xef.createEndElement(new QName(xhtmlNS, "a"),null));
				}else{
					doc.add(xef.createCharacters("N/A"));
				}
				doc.add(xef.createEndElement(new QName(xhtmlNS, "p"),null));
				doc.add(xef.createCharacters(br));
				
				doc.add(xef.createEndElement(new QName(xhtmlNS, "dd"),null));
				doc.add(xef.createCharacters(br));
				
			} catch (TransformerDisabledException e) {
				e.printStackTrace();
			}					
		}//for
		
		doc.add(xef.createEndElement(new QName(xhtmlNS, "dl"),null));		
		
	}

	private void generateScriptSummary(List<XMLEvent> doc, EFolder dir, XMLEventFactory xef, File destination) throws IOException, DMFCConfigurationException {
		
		URI dest = destination.getParentFile().toURI();
		
		doc.add(xef.createStartElement(new QName(xhtmlNS, "h2"),null,null));
		doc.add(xef.createAttribute(new QName(xhtmlNS, "id"), "scripts"));
		doc.add(xef.createCharacters("Script Summary"));
		doc.add(xef.createEndElement(new QName(xhtmlNS, "h2"),null));
		doc.add(xef.createCharacters(br));
		
		doc.add(xef.createStartElement(new QName(xhtmlNS, "dl"),null,null));
		doc.add(xef.createCharacters(br));
		
		EFolder scriptsDir = new EFolder(dir, "scripts");
		Collection<File> c = scriptsDir.getFiles(true, ".+\\.[Tt][Aa][Ss][Kk][Ss][Cc][Rr][Ii][Pp][Tt]$");
		List<File> scripts = new LinkedList<File>(c);
		Collections.sort(scripts);
		
		URL uurl = new File(dir,"/src/pipeline.user.properties").toURI().toURL();
        XMLProperties properties = new XMLProperties(System.getProperties());        
        properties.loadFromXML(uurl.openStream());
		DMFCCore dmfc = new DMFCCore(null,dir,properties);
						
		for(File s : scripts) {
			try {
				Script script = dmfc.newScript(s.toURI().toURL());
																 
				doc.add(xef.createStartElement(new QName(xhtmlNS, "dt"),null,null));
				doc.add(xef.createAttribute(new QName(xhtmlNS, "id"), script.getName()));
				doc.add(xef.createCharacters(script.getNicename()));
				doc.add(xef.createEndElement(new QName(xhtmlNS, "dt"),null));
				doc.add(xef.createCharacters(br));
				
				doc.add(xef.createStartElement(new QName(xhtmlNS, "dd"),null,null));
				doc.add(xef.createCharacters(br));
				
				doc.add(xef.createStartElement(new QName(xhtmlNS, "p"),null,null));
				doc.add(xef.createCharacters(script.getDescription()));
				doc.add(xef.createEndElement(new QName(xhtmlNS, "p"),null));
				doc.add(xef.createCharacters(br));
				
				doc.add(xef.createStartElement(new QName(xhtmlNS, "p"),null,null));
				doc.add(xef.createCharacters("Documentation: "));
				if(script.getDocumentation()!=null){					
					dest.normalize();
					script.getDocumentation().normalize();
					URI relative = dest.relativize(script.getDocumentation());
					doc.add(xef.createStartElement(new QName(xhtmlNS, "a"),null,null));
					doc.add(xef.createAttribute(new QName(xhtmlNS, "href"), relative.toString()));
					doc.add(xef.createCharacters(relative.toString()));
					doc.add(xef.createEndElement(new QName(xhtmlNS, "a"),null));
				}else{
					doc.add(xef.createCharacters("N/A"));
				}
				doc.add(xef.createEndElement(new QName(xhtmlNS, "p"),null));
				doc.add(xef.createCharacters(br));
				
				doc.add(xef.createEndElement(new QName(xhtmlNS, "dd"),null));
				doc.add(xef.createCharacters(br));
				
			} catch (Exception e) {
				e.printStackTrace();
			}					
		}//for
		
		doc.add(xef.createEndElement(new QName(xhtmlNS, "dl"),null));		
		
	}

	
	
	private void generateHeader(List<XMLEvent> doc, XMLEventFactory xef) {		
	
		doc.add(xef.createStartDocument("utf-8"));
		doc.add(xef.createCharacters(br));
		doc.add(xef.createStartElement(new QName(xhtmlNS, "html"),null,null));
		doc.add(xef.createCharacters(br));
		doc.add(xef.createStartElement(new QName(xhtmlNS, "head"),null,null));
		doc.add(xef.createCharacters(br));
		doc.add(xef.createStartElement(new QName(xhtmlNS, "title"),null,null));
		doc.add(xef.createCharacters("DAISY Pipeline Script and Transformer Summary"));
		doc.add(xef.createEndElement(new QName(xhtmlNS, "title"),null));
		doc.add(xef.createCharacters(br));
		
		doc.add(xef.createStartElement(new QName(xhtmlNS, "link"),null,null));
		doc.add(xef.createAttribute(new QName(xhtmlNS, "rel"), "stylesheet"));
		doc.add(xef.createAttribute(new QName(xhtmlNS, "type"), "text/css"));
		doc.add(xef.createAttribute(new QName(xhtmlNS, "href"), "pipeline.css"));
		doc.add(xef.createCharacters(br));
		doc.add(xef.createEndElement(new QName(xhtmlNS, "link"),null));
		
		doc.add(xef.createEndElement(new QName(xhtmlNS, "head"),null));
		doc.add(xef.createCharacters(br));
		doc.add(xef.createStartElement(new QName(xhtmlNS, "body"),null,null));
		doc.add(xef.createCharacters(br));	
		doc.add(xef.createStartElement(new QName(xhtmlNS, "h1"),null,null));
		doc.add(xef.createCharacters("DAISY Pipeline Script and Transformer Summary"));
		doc.add(xef.createEndElement(new QName(xhtmlNS, "h1"),null));
		doc.add(xef.createStartElement(new QName(xhtmlNS, "p"),null,null));
		doc.add(xef.createAttribute(new QName(xhtmlNS, "class"), "summary"));
		doc.add(xef.createCharacters("This summary is autogenerated and contains information extracted from the Pipeline documentation suite. Some information here pertain to functionality that is still in development and not ready for production use. For more information, follow the provided hyperlinks."));
		doc.add(xef.createEndElement(new QName(xhtmlNS, "p"),null));
		doc.add(xef.createCharacters(br));
		doc.add(xef.createStartElement(new QName(xhtmlNS, "p"),null,null));
		doc.add(xef.createCharacters("Document generated: " + getDate()));
		doc.add(xef.createEndElement(new QName(xhtmlNS, "p"),null));
		doc.add(xef.createCharacters(br));
		
		doc.add(xef.createStartElement(new QName(xhtmlNS, "div"),null,null));
		doc.add(xef.createAttribute(new QName(xhtmlNS, "class"), "toc"));
		
		doc.add(xef.createStartElement(new QName(xhtmlNS, "a"),null,null));
		doc.add(xef.createAttribute(new QName(xhtmlNS, "href"), "#scripts"));
		doc.add(xef.createCharacters("Script Summary"));		
		doc.add(xef.createEndElement(new QName(xhtmlNS, "a"),null));

		doc.add(xef.createStartElement(new QName(xhtmlNS, "br"),null,null));
		doc.add(xef.createEndElement(new QName(xhtmlNS, "br"),null));
		
		doc.add(xef.createStartElement(new QName(xhtmlNS, "a"),null,null));
		doc.add(xef.createAttribute(new QName(xhtmlNS, "href"), "#transformers"));
		doc.add(xef.createCharacters("Transformer Summary"));		
		doc.add(xef.createEndElement(new QName(xhtmlNS, "a"),null));
		
		doc.add(xef.createEndElement(new QName(xhtmlNS, "div"),null));
		
	}
	
	private void generateFooter(List<XMLEvent> doc, XMLEventFactory xef) {
		doc.add(xef.createEndElement(new QName(xhtmlNS, "body"),null));
		doc.add(xef.createCharacters(br));
		doc.add(xef.createEndElement(new QName(xhtmlNS, "html"),null));
		doc.add(xef.createEndDocument());
		
	}


	/*
	 * (non-Javadoc)
	 * @see org.daisy.pipeline.core.InputListener#getUserReply(org.daisy.pipeline.core.event.RequestEvent)
	 */
	public UserReplyEvent getUserReply(
			RequestEvent event) {
		// TODO Auto-generated method stub
		return null;
	}	
	
	/**
	 * Main class
	 * @param args first argument contains absolute path to the pipeline main dir
	 */
	public static void main(String[] args) {		
		System.err.println("Running SummaryGenerator...");
		try {
			EFolder mainDir = new EFolder(args[0]);
			if(!mainDir.exists()) {
				throw new IOException(mainDir.toString());
			}
		
			File dest = new File(mainDir,"doc/summary.html");
			
			new SummaryGenerator(mainDir,dest); 
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.err.println("SummaryGenerator done.");
	}
	
	private void checkSystemProperties(EFolder pipelineBaseDir) throws InvalidPropertiesFormatException, IOException {
		
		URL url = new File(pipelineBaseDir,"/src/pipeline.properties").toURI().toURL();
		URL uurl = new File(pipelineBaseDir,"/src/pipeline.user.properties").toURI().toURL();
		Collection<URL> c = new HashSet<URL>();
		c.add(url);
		c.add(uurl);
		        
        for(URL u : c) {
            XMLProperties properties = new XMLProperties(System.getProperties());        
            properties.loadFromXML(u.openStream());        
            System.setProperties(properties);        	
        }
        

        
		//only set if the system doesnt carry values already

		String test = System.getProperty(
		"javax.xml.validation.SchemaFactory:http://relaxng.org/ns/structure/1.0");
		if(test==null){
			System.setProperty(
					"javax.xml.validation.SchemaFactory:http://relaxng.org/ns/structure/1.0",
			"org.daisy.util.xml.validation.jaxp.RelaxNGSchemaFactory");
		}

		test = System.getProperty(
		"javax.xml.validation.SchemaFactory:http://www.ascc.net/xml/schematron");
		if(test==null){
			System.setProperty(
					"javax.xml.validation.SchemaFactory:http://www.ascc.net/xml/schematron",
			"org.daisy.util.xml.validation.jaxp.SchematronSchemaFactory");
		}

		test = System.getProperty(
		"javax.xml.validation.SchemaFactory:http://purl.oclc.org/dsdl/schematron");
		if(test==null){
			System.setProperty(
					"javax.xml.validation.SchemaFactory:http://purl.oclc.org/dsdl/schematron",
			"org.daisy.util.xml.validation.jaxp.ISOSchematronSchemaFactory");
		}
		
		test = System.getProperty(
			"org.daisy.util.fileset.validation:http://www.daisy.org/fileset/Z3986");
		if(test==null){
			System.setProperty(
					"org.daisy.util.fileset.validation:http://www.daisy.org/fileset/Z3986",
			"org.daisy.util.fileset.validation.ValidatorImplZedVal");
			/*
			 * The basic (non-ZedVal) validator would be
			 * System.setProperty(
			 * "org.daisy.util.fileset.validation:http://www.daisy.org/fileset/Z3986",
			 * "org.daisy.util.fileset.validation.impl.ValidatorImplZedBasic");
			 */
		}

		test = System.getProperty(
		"org.daisy.util.fileset.validation:http://www.daisy.org/fileset/DAISY_202");
		if(test==null){
			System.setProperty(
					"org.daisy.util.fileset.validation:http://www.daisy.org/fileset/DAISY_202",
			"org.daisy.util.fileset.validation.ValidatorImplD202");
		}

		test = System.getProperty(
		"org.daisy.util.fileset.validation:http://www.daisy.org/fileset/DTBOOK_DOCUMENT");
		if(test==null){
			System.setProperty(
					"org.daisy.util.fileset.validation:http://www.daisy.org/fileset/DTBOOK_DOCUMENT",
			"org.daisy.util.fileset.validation.ValidatorImplDtbook");
		}

		test = System.getProperty(
		"org.daisy.util.fileset.validation:http://www.daisy.org/fileset/OPS_20");
		if(test==null){
			System.setProperty(
					"org.daisy.util.fileset.validation:http://www.daisy.org/fileset/OPS_20",
			"org.daisy.util.fileset.validation.ValidatorImplOPS2x");
		}
		

	}

	private String getDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(new java.util.Date());
	}
}
