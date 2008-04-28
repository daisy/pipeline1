package se_tpb_aligner.util;

import java.io.File;

import org.daisy.util.file.EFile;
import org.daisy.util.file.Directory;

/**
 *
 * @author Markus Gylling
 */
public class Source extends EFile {

	
	public Source(File file) {
		super(file);
	}

	public Source(Directory dir, String string) {
		super(dir,string);
	}

	private static final long serialVersionUID = 7909274987441151044L;

}
