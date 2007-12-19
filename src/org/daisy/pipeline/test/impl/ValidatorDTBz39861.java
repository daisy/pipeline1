package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.EFolder;

public class ValidatorDTBz39861 extends PipelineTest {

	public ValidatorDTBz39861(EFolder dataInputDir, EFolder dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {		
		mParameters.add("--validatorInputFile=" + mDataInputDir + "/dtb/z3986-2005/06-speechgen.opf");
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "Should report no errors.";
	}

	@Override
	public boolean supportsScript(String scriptName) {
		if("Z3986DTBValidator.taskScript".equals(scriptName)) {
			return true;
		}		
		return false;
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub
		
	}

}
