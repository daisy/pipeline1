package se_tpb_wordml2dtbook;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URLDecoder;
import java.util.ArrayList;

import org.daisy.util.file.FileUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

/**
 * 
 * ... beskrivning ...
 * 
 * @author  Joel Hakansson, TPB
 * @version 2006-aug-28
 * @since 1.0
 */
public class ImageDecodeHandler extends DefaultHandler2 {
	private boolean openPict;
	//private sun.misc.BASE64Decoder decoder;
	private FileOutputStream output;
	private File outputFile;
	private StringBuffer buffer;
	private int imgcount;
	private File inputdir;
	private File outputdir;
	private ArrayList<File> filesToConvert;
	
	/**
	 * 
	 * @param outputdir
	 */
	public ImageDecodeHandler(File inputdir, File outputdir) {
		this.inputdir = inputdir;
		this.outputdir = outputdir;
		this.imgcount = 0;
		this.output = null;
		this.openPict = false;
		this.filesToConvert = new ArrayList<File>();
		//this.decoder = new sun.misc.BASE64Decoder();
	}
	
	
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if (localName.equals("binData")) {
			openPict = true;
			String name = atts.getValue(uri, "name");
			String post = name.substring(name.lastIndexOf(".")).toLowerCase();
			outputFile = new File(outputdir, buildFileName(name));
			buffer = new StringBuffer();
			// removed to harmonize with stylesheet...
			if (!(post.equals(".jpg"))) { //  || post.equals(".jpeg") || post.equals(".png")  
				filesToConvert.add(outputFile);
			}
		} else if (localName.equals("pict")) { // added to harmonize with stylesheet
			imgcount++;
		} else if (localName.equals("imagedata")) {
			String src = atts.getValue("src");
			if (src!=null && !src.startsWith("wordml://")) {
				try {
					File f = new File(inputdir, URLDecoder.decode(src, "utf-8"));
					FileUtils.copy(f, new File(outputdir, buildFileName(src)));
					String post = src.substring(src.lastIndexOf(".")).toLowerCase();
					if (!(post.equals(".jpg"))) { //  || post.equals(".jpeg") || post.equals(".png")  
						filesToConvert.add(outputFile);
					}					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private String buildFileName(String name) {
		String post = name.substring(name.lastIndexOf(".")).toLowerCase();
		String filename = "image";
		if (imgcount<10) filename += "00";
		else if (imgcount<100) filename += "0";
		filename += imgcount + post;
		return filename;
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (openPict) {
			openPict = false;
			try {
				output = new FileOutputStream(outputFile);
				//output.write(decoder.decodeBuffer(buffer.toString()));
				output.write(Base64.decode(buffer.toString()));
				output.close();
			} catch (Exception e) { e.printStackTrace(); }
		}
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (openPict) {
			buffer.append(ch, start, length);
		}
	}
	
	public File[] getFilesToConvert() {
		return (File[])filesToConvert.toArray(new File[filesToConvert.size()]);
	}

}
