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
import org.daisy.util.file.Directory;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.file.TempFile;
import org.daisy.util.runtime.RegistryQuery;
import org.daisy.util.xml.Namespaces;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
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
	private String mImagesPath;
	private String mOverwrite = "false";
		
	private Set<TransformerDelegateListener> listeners = null;
	
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
    	if (parameters != null) {
    	    Boolean over = (Boolean)parameters.get("overwrite");
    	    if (over != null && over.booleanValue()) {
    	        mOverwrite = "true";
    	    }
    	}
    	
    	// Make sure we're running on Windows
    	if (!System.getProperty("os.name").matches("Windows.*")) {
    	    throw new IllegalStateException(getNiceName() + " is only supported on the Windows platform.");
    	}
    	
    	String programDir = getRegistryValue("HKLM\\Software\\Design Science\\DSMD1", "ProgramDir");
    	if (programDir == null) {
    	    throw new IllegalStateException("Cannot locate the installation directory of " + getNiceName());
    	}    	
    	mComposerPath = new File(FilenameOrFileURI.toFile(programDir), "MathDAISY.exe");
    	
		if(mInputDoc==null||mOutputDoc==null||programDir==null) {
			throw new IllegalStateException();
		}
	}

	private String getRegistryValue(String key, String name) {
	    String value = RegistryQuery.readString(key, name);
	    return value;
	}

	/*
	 * (non-Javadoc)
	 * @see int_daisy_mathAltCreator.IMathAltCreator#execute()
	 */
	public void execute() throws Exception {
	    File input = mInputDoc;
	    File output = mOutputDoc;
	    boolean copyInput = false;
	    boolean moveOutput = false;
	    Directory safeDir = null;
	    if (!Command.isSafePath(mInputDoc)) {
	        // Input doc path is unsafe. Copy to temp dir
	        copyInput = true;
	        safeDir = new Directory(TempFile.createDir());
	        input = new File(new File(safeDir, "input"), mInputDoc.getName());
	        FileUtils.copyFile(mInputDoc, input);
	    }
	    if (!Command.isSafePath(mOutputDoc)) {
	        // Output doc path is unsafe. Write to temp dir instead
	        moveOutput = true;
	        if (safeDir == null) {
	            safeDir = new Directory(TempFile.createDir());
	        }
	        output = new File(new File(safeDir, "output"), mOutputDoc.getName());
	        FileUtils.createDirectory(output.getParentFile());
	    }
	    
	    ArrayList<String> commandArgs = new ArrayList<String>();
	    commandArgs.add(mComposerPath.getAbsolutePath());
	    
	    commandArgs.add("-inputdoc");
	    commandArgs.add(input.getAbsolutePath());
	    
	    commandArgs.add("-outputdoc");
	    commandArgs.add(output.getAbsolutePath());
	    
	    commandArgs.add("-imagefolder");
	    commandArgs.add(mImagesPath);
	    
	    commandArgs.add("-overwrite");
	    commandArgs.add(mOverwrite);
       
	    /*
	     * Execute
	     */
	    int ret;
	    ret = Command.execute((commandArgs.toArray(new String[commandArgs.size()])));
	    if(ret != 0) {
	        throw new IOException("MathDAISY failed. Exit status was " + ret);
	    }
	    
	    if (moveOutput) {
	        // Move the result back to the real output location
	        FileUtils.moveFile(output, mOutputDoc);
	        Directory tempImages = new Directory(new File(output.getParentFile(), mImagesPath).getCanonicalFile());
	        Directory destImages = new Directory(new File(mOutputDoc.getParentFile(), mImagesPath).getCanonicalFile());
	        tempImages.copyChildrenTo(destImages, true);
	    }
	    if (copyInput || moveOutput) {
	        // Delete temp dir
	        safeDir.deleteContents(true);
	        safeDir.delete();
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
				    String prefix = xe.asStartElement().getName().getPrefix();
					QName math = new QName(Namespaces.MATHML_NS_URI, "math", prefix);
					Set<Attribute> attributes = new HashSet<Attribute>();
					Iterator<?> i = xe.asStartElement().getAttributes();
					while(i.hasNext()) {
						Attribute a = (Attribute)i.next();
						if(!a.getName().getPrefix().equals("dsi")) {
							if(a.getName().getLocalPart().equals("alttext")) {
								attributes.add(xef.createAttribute(new QName("", "alttext" ), a.getValue()));
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
			
			if (reader!=null) {
			    reader.close();
			}
			if (writer!=null) {
			    writer.flush();
			    writer.close();
			}
						
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
		return "DesignScience MathDAISY";
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
