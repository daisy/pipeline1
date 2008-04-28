package se_tpb_aligner.util;

import java.io.File;

import org.daisy.util.file.EFile;
import org.daisy.util.file.Directory;

/**
 *
 * @author Markus Gylling
 */
public class Result extends EFile {
	
	public Result(File file) {
		super(file);
	}

	public Result(Directory dir, String string) {
		super(dir,string);
	}

	private static final long serialVersionUID = -709458276385138804L;

}
