package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.EFolder;

public class ValidatorDTBook2 extends PipelineTest {

	public ValidatorDTBook2(EFolder dataInputDir, EFolder dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {		
		mParameters.add("--input=" + mDataInputDir + "/dtbook/hauy_invalid.xml");
		mParameters.add("--validatorGenerateContextInfo=true");
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "Test should validate a dtbook document and return DTD and Schematron errors, context info added";
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
