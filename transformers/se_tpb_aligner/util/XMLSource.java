package se_tpb_aligner.util;

import java.io.File;

import org.daisy.util.file.EFolder;

/**
 *
 * @author Markus Gylling
 */
public class XMLSource extends Source {

	public XMLSource(File file) {
		super(file);
	}

	public XMLSource(EFolder dir, String string) {
		super(dir,string);
	}

	private static final long serialVersionUID = -3472856551841536790L;

}
