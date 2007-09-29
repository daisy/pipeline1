package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.EFolder;

public class MultiFormatMedia1 extends PipelineTest {

	public MultiFormatMedia1(EFolder dataInputDir, EFolder dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {
		mParameters.add("--inputDtbook=" + mDataInputDir + "/dtbook/hauy-2005-1-short.xml");
		mParameters.add("--outputPath=" + mDataOutputDir + "/mfm1/");				
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return null;
	}

	@Override
	public boolean supportsScript(String scriptName) {
		if("MultiFormatMedia.taskScript".equals(scriptName)) {
			return true;
		}		
		return false;
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub
		
	}

}
