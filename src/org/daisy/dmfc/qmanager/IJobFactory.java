package org.daisy.dmfc.qmanager;

import java.io.File;

import org.daisy.dmfc.core.script.ScriptHandler;

/**
 * Defines functionality needed to create a job.
 * Used in creating a single job from web or local machine
 * Used in creating batch conversions on web or local machine
 * @author Laurie Sherve
 *
 */


public interface IJobFactory {

	public void setInputDocument(File input);
	public void setOutputDocument(File output);
	public void setScriptHandler(ScriptHandler script);
}
