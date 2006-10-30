package org.daisy.util.fileset.validation.delegate.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.interfaces.xml.XmlFile;
import org.daisy.util.fileset.validation.delegate.ValidatorDelegateImplAbstract;
import org.daisy.util.fileset.validation.exception.ValidatorException;
import org.daisy.util.fileset.validation.exception.ValidatorNotSupportedException;
import org.daisy.util.fileset.validation.message.ValidatorErrorMessage;
import org.daisy.util.i18n.CharsetDetector;

/**
 * Makes sure all XML files in a file set use a specific
 * character encoding. If no encoding is specified in the
 * constructor, the validation delegate looks for utf-8 by
 * default.
 * @author Linus Ericson
 */
public class XMLEncodingDelegate extends ValidatorDelegateImplAbstract {
	
	private String mEncoding;
	
	public XMLEncodingDelegate() {
		this("utf-8");
	}
	
	public XMLEncodingDelegate(String encoding) {
		if (encoding == null) {
			encoding = "utf-8";
		}
		mEncoding = encoding;
	}
	
	public boolean isFilesetTypeSupported(FilesetType type) {	
		return true;		
	}

	public void execute(Fileset fileset) throws ValidatorNotSupportedException, ValidatorException {		
		super.execute(fileset);
		
		Collection members = fileset.getLocalMembers();
		for (Iterator it = members.iterator(); it.hasNext(); ) {
			FilesetFile filesetFile = (FilesetFile)it.next();
			if (filesetFile instanceof XmlFile) {
				try {
					this.checkEncoding(filesetFile.getFile().toURL());
				} catch (MalformedURLException e) {
					throw new ValidatorException(e.getMessage(), e);
				} catch (IOException e) {
					throw new ValidatorException(e.getMessage(), e);
				} catch (URISyntaxException e) {
					throw new ValidatorException(e.getMessage(), e);
				}
			} else {
				//System.err.println("Skipping: " + filesetFile.toString());
			}
		}		
	}
	
	private void checkEncoding(URL url) throws IOException, URISyntaxException {
		CharsetDetector detector = new CharsetDetector();		
		detector.detect(url);
		String[] charsets = detector.getProbableCharsets();
		boolean found = false;
		for (int i = 0; i < charsets.length; ++i) {
			if (charsets[i].equalsIgnoreCase(mEncoding)) {
				found = true;
			}
		}
		if (!found) {
			report(new ValidatorErrorMessage(url.toURI(), "File is not encoded in '" + 
					mEncoding + "'."));
		}
	}

}
