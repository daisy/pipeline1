package int_daisy_mathAltCreator.com.dessci.mathflow;

import int_daisy_mathAltCreator.IMathAltCreator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.XMLEvent;

import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.util.execution.Command;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.file.TempFile;
import org.daisy.util.xml.Namespaces;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.pool.StAXOutputFactoryPool;
import org.daisy.util.xml.stax.StaxEntityResolver;
import org.w3c.dom.DOMError;
import org.w3c.dom.DOMErrorHandler;

/**
 * An implementation of IMathMLAltCreator using the MathFlow SDK.
 * @author Design Science Inc.
 */
public class MathFlowAltCreator implements IMathAltCreator, DOMErrorHandler {
	private File mInputDoc;
	private File mOutputDoc;
	private File mComposerPath;
	private File mLicensePath;
	private String mImagesPath;
	private String mOptsPath;
	
	private static final String REG_VALUE_COMPOSER = "TODO_COMPOSER";
	private static final String REG_VALUE_LICENSE = "TODO_LICENSE";
	
	Set<TransformerDelegateListener> listeners = null;
	
	/**
	 * Default constructor, also used by factory initializer.
	 */
	public MathFlowAltCreator() {
		listeners = new HashSet<TransformerDelegateListener>();  
	}
	
	/*
	 * (non-Javadoc)
	 * @see int_daisy_mathAltCreator.IMathAltCreator#configure(java.io.File, java.io.File, java.util.Map)
	 */
	public void configure(File input, File output, @SuppressWarnings("unused")Map<String, Object> parameters) {
    	mInputDoc = input;
    	mOutputDoc = output;
    	mImagesPath = "images";
		mOptsPath = "opts.xml";		
		mComposerPath = FilenameOrFileURI.toFile(getRegistryValue(REG_VALUE_COMPOSER));
		mLicensePath = FilenameOrFileURI.toFile(getRegistryValue(REG_VALUE_COMPOSER));
    	
		if(mInputDoc==null||mOutputDoc==null||mComposerPath==null||mLicensePath==null) {
			throw new IllegalStateException();
		}
	}

	private String getRegistryValue(String key) {
		//TODO
		if(key.equals(REG_VALUE_COMPOSER)) {
			return "C:\\Program Files\\MathFlow SDK\\1.0\\windows\\bin\\DocumentComposer.exe";
		}
		return "C:\\Program Files\\MathFlow SDK\\1.0\\windows\\samples\\dessci.lic";
	}

	/*
	 * (non-Javadoc)
	 * @see int_daisy_mathAltCreator.IMathAltCreator#execute()
	 */
	public void execute() throws Exception {
//		> > > > "../bin/DocumentComposer" -license "dessci.lic"
//		> > -outputtype mathml
//		> > > > -imagetype gif -fontmetrics true -alttext "true"
//		> > > > -inputdoc "input.html"
//		> > > > -outputdoc "output2.html" -imagefolder "images" -saveoptions
//		> > > > "opts.xml
		
       ArrayList<String> arr = new ArrayList<String>();		        
       char q = '"';
       arr.add(q + mComposerPath.getAbsolutePath() + q);        
       arr.add("-license");
       arr.add(q + mLicensePath.getAbsolutePath() + q);
       arr.add("-outputtype");        
       arr.add("mathml");               
       arr.add("-imagetype");
       arr.add("png");
       arr.add("-fontmetrics");
       arr.add("true");
       arr.add("-alttext");
       arr.add("true");
       arr.add("-inputdoc");
       arr.add(q + mInputDoc.getAbsolutePath() + q);
       arr.add("-outputdoc");
       arr.add(q + mOutputDoc.getAbsolutePath() + q);
       arr.add("-imagefolder");
       arr.add(q + mImagesPath + q);
       arr.add("-saveoptions");
       arr.add(q + mOptsPath + q);	        
       
       /*
        * Execute
        */
       int ret;
       //System.err.println("Pipeline calling MathFlow: " + arr.toString());        	
       ret = Command.execute((arr.toArray(new String[arr.size()])));
       //System.err.println("ret");
       if(ret == -1) {
       	//TODO 
    	   System.err.println("WARNING DocumentComposer returned -1");
       }
       
       tempFix();
		
	}
		
	private void tempFix() throws IOException {
		//TEMP: some post-xslt namespace and attribute cleanup.
		
		Map<String, Object> xifProperties = null;
		Map<String, Object> xofProperties = null;
		XMLInputFactory xif = null;
		XMLOutputFactory xof = null;
		XMLEventFactory xef = null;
		FileInputStream fis = null;
		FileOutputStream fos = null;
		File tempOut = TempFile.create();
		try {
			xifProperties = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(false);
			xofProperties = StAXOutputFactoryPool.getInstance().getDefaultPropertyMap();
			xofProperties.put(XMLOutputFactory.IS_REPAIRING_NAMESPACES, Boolean.FALSE);
			xif = StAXInputFactoryPool.getInstance().acquire(xifProperties);
			xof = StAXOutputFactoryPool.getInstance().acquire(xofProperties);
			xif.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
			fis = new FileInputStream(mOutputDoc);			
			XMLEventReader reader = xif.createXMLEventReader(fis);
			fos = new FileOutputStream(tempOut);
			XMLEventWriter writer = xof.createXMLEventWriter(fos);			
			xef = StAXEventFactoryPool.getInstance().acquire();
			
			
			while(reader.hasNext()) {
				XMLEvent xe = reader.nextEvent();
				if(xe.getEventType()==XMLEvent.START_ELEMENT && xe.asStartElement().getName().getLocalPart() == "math") {
					QName math = new QName(Namespaces.MATHML_NS_URI,"math","m");
					Set<Attribute> attributes = new HashSet<Attribute>();
					Iterator<?> i = xe.asStartElement().getAttributes();
					while(i.hasNext()) {
						Attribute a = (Attribute)i.next();
						if(!a.getName().getPrefix().equals("dsi")) {
							if(a.getName().getLocalPart().equals("alttext")) {
								attributes.add(xef.createAttribute(new QName("", "alttext" ), a.getValue().replace('\\', '/')));
							}else if(a.getName().getLocalPart().equals("altimg")) {
								attributes.add(xef.createAttribute(new QName("", "altimg" ), a.getValue().replace('\\', '/')));
							}else{
								attributes.add(a);
							}	
						}
					}
					
					Set<Namespace> namespaces = new HashSet<Namespace>();
					i = xe.asStartElement().getNamespaces();
					while(i.hasNext()) {
						Namespace n = (Namespace)i.next();
						if(!n.getPrefix().equals("dsi")) {
							namespaces.add(n);
						}
					}
					
					writer.add(xef.createStartElement(math, attributes.iterator(), namespaces.iterator()));
				}else{
					writer.add(xe);
				}
			}			
			
			if(reader!=null)reader.close();
			if(writer!=null)writer.flush();writer.close();
						
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}finally{			
			if(fis!=null) try {fis.close();} catch (IOException e) {}
			if(fos!=null) try {fos.close();} catch (IOException e) {}
			StAXInputFactoryPool.getInstance().release(xif, xifProperties);
			StAXOutputFactoryPool.getInstance().release(xof, xofProperties);
		}					

		FileUtils.copy(tempOut, mOutputDoc);
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see int_daisy_mathAltCreator.IMathAltCreator#getNiceName()
	 */
	public String getNiceName() {		
		return "DesignScience MathFlow";
	}

	public void addListener(TransformerDelegateListener listener) {
		listeners.add(listener);
		
	}

	public void removeListener(TransformerDelegateListener listener) {
		listeners.remove(listener);		
	}

	public boolean handleError(DOMError error) {
		System.err.println(error.getMessage());
		return true;
	}

}
