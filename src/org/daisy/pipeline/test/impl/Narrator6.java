package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.Directory;

public class Narrator6 extends PipelineTest {

	public Narrator6(Directory dataInputDir, Directory dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {		
		//mParameters.add("--input=" + mDataInputDir +"/dtbook/mixed-content-torture.xml");
		mParameters.add("--input=/home/markusg/Desktop/missing-smilref.xml");
		mParameters.add("--outputPath=" + mDataOutputDir + "/Narrator6/");
		mParameters.add("--doSentDetection=true");
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "";
	}

	@Override
	public boolean supportsScript(String scriptName) {
		if("Narrator-DtbookToDaisy202.taskScript".equals(scriptName)) {
			return true;
		}		
		return false;
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub		
	}

}
