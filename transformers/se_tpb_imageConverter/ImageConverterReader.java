package se_tpb_imageConverter;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.StartElement;

import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.util.execution.Command;
import org.daisy.util.execution.ExecutionException;
import org.daisy.util.file.FileUtils;
import org.daisy.util.xml.stax.StaxFilter;

public class ImageConverterReader extends StaxFilter {
	private String[] command;
	private String tag;
	private String att;
	private String ext;
	private int inputIndex;
	private int outputIndex;
	private File input;
	private File output;
	private ImageConverter t;
	private List<File> filesToCopy;
	private int len;

	public ImageConverterReader(ImageConverter t, XMLInputFactory inFactory, File input, File output, String command, String tag, String att, String ext, String placeholderInput, String placeholderOutput) throws XMLStreamException, FileNotFoundException {
		super(inFactory.createXMLEventReader(new FileInputStream(input)), new FileOutputStream(output));
		this.command = command.split("\\s+");
		this.tag = tag;
		this.att = att;
		this.ext = ext;
		this.input = input;
		this.output = output;
		this.t = t;
		for (int j=0;j<this.command.length;j++) {
			if (this.command[j].equals(placeholderInput)) {
				inputIndex=j;
			} else if (this.command[j].equals(placeholderOutput)) { 
				outputIndex=j;
			}
		}
		List<File> tmpList = Arrays.asList(input.getParentFile().listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return !pathname.isDirectory();
			}}));
		len = tmpList.size();
		filesToCopy = new ArrayList();
		for (File f : tmpList) {
			filesToCopy.add(f);
		}
		filesToCopy.remove(input);
		t.setProgress((double)(len-filesToCopy.size())/len);
	}
	
    protected StartElement startElement(StartElement event) {
    	if (ext==null) ext="";
    	if (event.getName().getLocalPart().equals(tag)) {
    		Iterator i = event.getAttributes();
    		ArrayList<Attribute> atts = new ArrayList<Attribute>();
    		while (i.hasNext()) {
    			Attribute a = (Attribute)i.next();
    			if (a.getName().getLocalPart().equals(att)) {
    				String filename = a.getValue();
    				if (filename.length()>0) {
	    				if (filename.indexOf('.')>-1) {
	    					filename = a.getValue().substring(0, a.getValue().lastIndexOf(".")).toLowerCase();
	    				}
	    				filename += ext;
	    				// convert [options ...] file [ [options ...] file ...] [options ...] file
	    				
	    				File img = new File(a.getValue());
	    				if (img.isAbsolute()) {
	    					t.sendMessage("Cannot convert an absolute path", MessageEvent.Type.WARNING, MessageEvent.Cause.SYSTEM, null);
	    				} else {
		    				// If image path is not absolute it should be relative to the input file
	    					img = new File(input.getParentFile(), a.getValue());
	    				}
	    				if (img.exists()) {
	    					File outImg = new File(output.getParentFile(), filename);
	    					outImg.getParentFile().mkdirs();
	    					command[inputIndex]=img.getAbsolutePath(); 
	    					command[outputIndex]= outImg.getAbsolutePath();
		    				try {
		    					t.sendMessage("Converting " + img, MessageEvent.Type.INFO, MessageEvent.Cause.SYSTEM, null);
								Command.execute(command);
			    				filesToCopy.remove(img);
			    				atts.add(getEventFactory().createAttribute(a.getName(), filename));
			    				t.setProgress((double)(len-filesToCopy.size())/len);
							} catch (ExecutionException e) {
								atts.add(a);
								t.sendMessage("Cannot convert file " + a.getValue(), MessageEvent.Type.ERROR, MessageEvent.Cause.SYSTEM, null);
							}
	    				} else {
	    					atts.add(a);
	    					t.sendMessage("Cannot find file " + a.getValue(), MessageEvent.Type.WARNING, MessageEvent.Cause.SYSTEM, null);
	    				}
    				}
    			} else {
    				atts.add(a);
    			}
    		}
    		return getEventFactory().createStartElement(event.getName(), atts.iterator(), event.getNamespaces());
    	} else return event;
    }

    protected EndDocument endDocument(EndDocument event) {
    	for (File f : filesToCopy) {
			try {
				FileUtils.copy(f, new File(output.getParentFile(), f.getName()));
				t.setProgress((double)(len-filesToCopy.size())/len);
			} catch (IOException e) {
				t.sendMessage("Unable to copy file " + f.getAbsolutePath(), MessageEvent.Type.WARNING, MessageEvent.Cause.SYSTEM, null);
			}    		
    	}
        return event;
    }
}
