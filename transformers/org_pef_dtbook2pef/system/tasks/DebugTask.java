package org_pef_dtbook2pef.system.tasks;

import java.io.File;
import java.io.IOException;

import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.FileUtils;
import org_pef_dtbook2pef.system.InternalTask;

public class DebugTask extends InternalTask {
	private File debug;
	
	public DebugTask(String name, File debug) {
		super(name);
		this.debug = debug;
	}

	@Override
	public void execute(File input, File output) throws TransformerRunException {
		try {
			FileUtils.copy(input, debug);
			FileUtils.copy(input, output);
		} catch (IOException e) {
			throw new TransformerRunException("Exception while copying file.", e);
		}
	}

}
