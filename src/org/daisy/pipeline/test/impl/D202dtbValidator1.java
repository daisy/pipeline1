package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.EFolder;

public class D202dtbValidator1 extends PipelineTest {

	public D202dtbValidator1(EFolder dataInputDir, EFolder dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {		
		mParameters.add("--input=" + mDataInputDir + "/dtb/d202/dontworrybehappy/ncc.html");
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "Should report no errors.";
	}

	@Override
	public boolean supportsScript(String scriptName) {
		if("Daisy202DTBValidator.taskScript".equals(scriptName)) {
			return true;
		}		
		return false;
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub
		
	}

}