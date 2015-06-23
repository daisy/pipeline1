package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.Directory;

public class DTBAudioEncoderRenamer1 extends PipelineTest {

	public DTBAudioEncoderRenamer1(Directory dataInputDir, Directory dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {				
		mParameters.add("--input=" + "D:\\data\\workspace\\dmfc\\samples\\input\\dtb\\z3986-wav\\06-speechgen.opf");
		mParameters.add("--output=" + mDataOutputDir + "/DTBAudioEncoderRenamer1/");
		mParameters.add("--bitrate=" + "48");
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "";
	}

	@Override
	public boolean supportsScript(String scriptName) {
		if("DTBAudioEncoderRenamer.taskScript".equals(scriptName)) {
			return true;
		}		
		return false;
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub		
	}

}
