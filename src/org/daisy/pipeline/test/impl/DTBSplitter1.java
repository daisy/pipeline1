package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.Directory;

public class DTBSplitter1 extends PipelineTest {

	public DTBSplitter1(Directory dataInputDir, Directory dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {		
		mParameters.add("--input=" + mDataInputDir + "/dtb/d202/dontworrybehappy/ncc.html");
		mParameters.add("--output=" + mDataOutputDir + "/DTBSplitter1");
		mParameters.add("--splitterMaxSize=" + 1);
		mParameters.add("--splitterMaxLevel=" + 6);		
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "Test should split a DTB into 1 volume";
	}

	@Override
	public boolean supportsScript(String scriptName) {
		if("DTBSplitter.taskScript".equals(scriptName)) {
			return true;
		}		
		return false;
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub		
	}

}
