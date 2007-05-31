package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.EFolder;

public class DTBAudioEncoderSplitter1 extends PipelineTest {

	public DTBAudioEncoderSplitter1(EFolder dataInputDir, EFolder dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {				
		//mParameters.add("--input=" + "D:/dtbs/d202/redP/out/ncc.html");
		//mParameters.add("--input=" + "D:/dtbs/d202/norP/ncc.html");
		mParameters.add("--input=" + "D:\\data\\workspace\\dmfc\\samples\\input\\dtb\\z3986-wav\\06-speechgen.opf");
		mParameters.add("--output=" + mDataOutputDir + "/DTBAudioEncoderSplitter1/");
		mParameters.add("--bitrate=" + "48");
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "";
	}

	@Override
	public boolean supportsScript(String scriptName) {
		if("DTBAudioEncoderSplitter.taskScript".equals(scriptName)) {
			return true;
		}		
		return false;
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub		
	}

}
