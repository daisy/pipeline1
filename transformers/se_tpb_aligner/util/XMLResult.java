package se_tpb_aligner.util;

import java.io.File;

import org.daisy.util.file.EFolder;

/**
 *
 * @author Markus Gylling
 */
public class XMLResult extends Result {
	
	public XMLResult(File file) {
		super(file);
	}

	public XMLResult(EFolder dir, String string) {
		super(dir,string);
	}

	private static final long serialVersionUID = 1541411257100412523L;

}
