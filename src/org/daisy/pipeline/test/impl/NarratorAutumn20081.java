package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.Directory;

public class NarratorAutumn20081 extends PipelineTest {

	public NarratorAutumn20081(Directory dataInputDir, Directory dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {		
		mParameters.add("--input=" + mDataInputDir + "/dtbook/hauy-2005-1-short.xml");
		mParameters.add("--outputPath=" + mDataOutputDir + "/NarratorAutumn20081/");
		mParameters.add("--doSentDetection=false");
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "";
	}

	@Override
	public boolean supportsScript(String scriptName) {
		if("Narrator-Autumn2008.taskScript".equals(scriptName)) {
			return true;
		}		
		return false;
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub		
	}

}
