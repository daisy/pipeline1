package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.EFolder;

public class ValidatorDTBook1 extends PipelineTest {

	public ValidatorDTBook1(EFolder dataInputDir, EFolder dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {		
		mParameters.add("--input=" + mDataInputDir + "/dtbook/hauy_valid.xml");
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "Test should validate a dtbook document and return all valid";
	}

	@Override
	public boolean supportsScript(String scriptName) {
		if("DTBookValidator.taskScript".equals(scriptName)) {
			return true;
		}		
		return false;
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub
		
	}

}
