package org.daisy.pipeline.util;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.file.EFile;
import org.daisy.util.file.EFolder;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.interfaces.FilesetErrorHandler;
import org.daisy.util.xml.peek.PeekResult;
import org.daisy.util.xml.peek.Peeker;
import org.daisy.util.xml.peek.PeekerPool;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.xml.sax.SAXParseException;

/**
 * Util class (with main) to check the transformer and script documentation packages within a Pipeline 
 * distribution and report inexistance, invalidity, broken links, etc.
 * <p>This class is typically run at build stage.<p>  
 * <p>This class assumes that documentation resources are not jarred.</p>
 * @author Markus Gylling
 */
public class DocChecker implements FilesetErrorHandler {
					
	private Map<String, Boolean> mXifProperties = null;
	
	/**
	 * @param pipelineRootDir a string representing the root directory of the pipeline.
	 */	
	public DocChecker(String pipelineRootDir) throws Exception {
		
		mXifProperties = new HashMap<String, Boolean>();
		mXifProperties.put(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
		mXifProperties.put(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
		mXifProperties.put(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.TRUE);
		mXifProperties.put(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.TRUE);
		mXifProperties.put(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
		
		EFolder  rootDir = new EFolder(pipelineRootDir);
		assert(rootDir.exists());
		
		EFolder scriptsDir = new EFolder(rootDir,"scripts");
		EFolder transformersDir = new EFolder(rootDir,"transformers");
				
		Map<File,String> scriptAndTransformerFiles = new HashMap<File,String>();
		
		scriptAndTransformerFiles.putAll(getDocuments(transformersDir,"transformer","tdf"));
		scriptAndTransformerFiles.putAll(getDocuments(scriptsDir,"taskScript","taskScript"));
				
		//get all existing transformer and script documentation files
		Collection<URI> documentationFiles = parseScriptAndTransformerFiles(scriptAndTransformerFiles);
		
		//since the documentation files are XHTML, we run a Fileset instance on each and validate that way.
		for (URI uri : documentationFiles) {
			try{
				new FilesetImpl(uri,this,true,false);
			}catch (FilesetFatalException ffe) {
				System.err.println("[DocChecker Warning] FilesetFatalError exception: " 
						+ ffe.getMessage()
						+ ffe.getRootCauseMessagesAsString());
			}
		}
		System.err.println("DocChecker done.");
	}

	private Set<URI> parseScriptAndTransformerFiles(Map<File, String> scriptAndTransformerFiles) throws Exception {
		//incoming is <transformer> and <taskScript> documents
		//both have an optional element <documentation uri="../relative/document.html">
		//warn if this element is not present
		//if element is present, resolve the URI and check if the destination exists
		//if it exists, add to return set
		//if it doesnt exist, warn
		Set<URI> existingDocumentationFiles = new HashSet<URI>();
		
		for (Iterator iter = scriptAndTransformerFiles.keySet().iterator(); iter.hasNext();) {
			File file = (File) iter.next();
			String docURI = parseForDocURI(file);
			if(docURI==null){
				System.err.println("[DocChecker Warning] File " + file.getParentFile().getName()+"/"+file.getName() + " has no inline documentation URI");
			}else{
				URI resolvedURI = file.toURI().resolve(docURI);
				File test = new File(resolvedURI);
				if(test.exists()) {
					existingDocumentationFiles.add(resolvedURI);
				}else{
					System.err.println("[DocChecker Warning] Documentation URI in " + file.getParentFile().getName()+"/"+file.getName() + " does not resolve. URI is: " + resolvedURI );
				}
			}
			
		}
		System.err.println("[DocChecker info] Found " + existingDocumentationFiles.size() + " existing documentation files"); 								
		return existingDocumentationFiles;
		
	}

	private String parseForDocURI(File file) throws Exception {
		XMLInputFactory xif = StAXInputFactoryPool.getInstance().acquire(mXifProperties);
		XMLEventReader reader = xif.createXMLEventReader(file.toURI().toURL().openStream());
		
		String ret = null;		
		while (reader.hasNext() && ret == null) {
			XMLEvent event = reader.nextEvent();			
			if (event.isStartElement()) {
				StartElement se = event.asStartElement();
				if(se.getName().getLocalPart().equals("documentation")) {
					Attribute attr = se.getAttributeByName(new QName("uri"));
					if(attr!=null) ret = attr.getValue();											
				}
			}
		}	
		
		reader.close();
		StAXInputFactoryPool.getInstance().release(xif, mXifProperties);
		return ret;
	}

	private Map<File,String> getDocuments(EFolder rootDir, String rootElemLocalName, String extension) throws Exception {
		 		
		Collection<File> all = rootDir.getFiles(true);
		Map<File,String> ret = new HashMap<File,String>();
				
		Peeker peeker = PeekerPool.getInstance().acquire();
		for (File file : all) {
			EFile ef = new EFile(file);
			if(ef.getExtension()!=null && ef.getExtension().equals(extension)) {
				try {
					PeekResult result = peeker.peek(file);	
					if(result.getRootElementLocalName().equals(rootElemLocalName)){
						ret.put(file, rootElemLocalName);
					}
				}catch (Exception e) {
					//System.err.println("[DocChecker Warning] Peeker exception in " + file.getName());
				}
			}
		}
		PeekerPool.getInstance().release(peeker);		
		System.err.println("[DocChecker info] Found " + ret.size() + " files of type " + rootElemLocalName);
		return ret;
	}

	public void error(FilesetFileException ffe) throws FilesetFileException {
		String line = "";
		if(ffe.getCause() instanceof SAXParseException) {
			SAXParseException spe = (SAXParseException) ffe.getCause();
			line = Integer.toString(spe.getLineNumber());
		}
		System.err.println("[DocChecker Warning] FilesetFileException in: " 
				+ ffe.getOrigin().getFile().getParentFile().getName() + "/" 
				+ ffe.getOrigin().getName() + "[" + line +  "]: "
				+ ffe.getCause().getMessage()
				);		
	}
	
	public static void main(String[] args) throws Exception {
		DocChecker dc = new DocChecker(args[0]);
	}

}
