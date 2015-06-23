package se_tpb_aligner.util;

import java.io.File;

import org.daisy.util.file.Directory;

/**
 *
 * @author Markus Gylling
 */
public class AudioSource extends Source {

	public AudioSource(File file) {
		super(file);
	}

	public AudioSource(Directory dir, String string) {
		super(dir,string);
	}

	private static final long serialVersionUID = 3697984666868313023L;	

}
