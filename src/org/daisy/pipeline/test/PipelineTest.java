package org.daisy.pipeline.test;

import java.util.LinkedList;
import java.util.List;

import org.daisy.util.file.EFolder;

public abstract class PipelineTest {
	protected String mDataInputDir = null;
	protected String mDataOutputDir = null;
	protected List<String> mParameters = null;
	
	public PipelineTest(EFolder dataInputDir, EFolder dataOutputDir) {
		mDataInputDir = dataInputDir.getAbsolutePath();
		mDataOutputDir = dataOutputDir.getAbsolutePath();
		mParameters= new LinkedList<String>();
	}
	
	/**
	 * Localname of script to be run against
	 */
	public abstract boolean supportsScript(String scriptName); 	
	
	/**
	 * Expectations
	 */
	public abstract String getResultDescription();
	
	/**
	 * Get parameters for commandlinegui, excluding script name
	 */
	public abstract List<String> getParameters();
	
	/**
	 * Callback made after pipeline run, gives test opportunity to check output properties 
	 */
	public abstract void confirm() throws PipelineTestException;
	
}
