package se_tpb_wordml2dtbook;

import java.io.File;
import java.io.FileOutputStream;

import org.daisy.dmfc.core.transformer.Transformer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import com.sun.imageio.plugins.common.I18N;

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
	private StringBuffer buffer;
	private int imgcount;
	private File dir;
	private String filename;
	private MessageInterface msg;
	
	/**
	 * 
	 * @param dir
	 */
	public ImageDecodeHandler(File dir, MessageInterface msg) {
		this.dir = dir;
		this.msg = msg;
		this.imgcount = 0;
		this.output = null;
		this.openPict = false;
		//this.decoder = new sun.misc.BASE64Decoder();
	}
	
	
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if (localName.equals("binData")) {
			imgcount++;
			openPict = true;
			String name = atts.getValue(uri, "name");
			String post = name.substring(name.lastIndexOf("."));
			if (!(post.equals(".png") || post.equals(".jpg") || post.equals(".jpeg"))) {
				msg.sendMessage(java.util.logging.Level.WARNING, "IMAGE_FORMAT_WARNING", new Object[]{post});
			}
			filename = "image";
			if (imgcount<10) filename += "00";
			else if (imgcount<100) filename += "0";
			filename += imgcount + post;
			buffer = new StringBuffer();
		}
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (openPict) {
			openPict = false;
			try {
				output = new FileOutputStream(new File(dir, filename));
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

}
