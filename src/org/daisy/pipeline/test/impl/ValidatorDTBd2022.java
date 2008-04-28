package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.Directory;

public class ValidatorDTBd2022 extends PipelineTest {

	public ValidatorDTBd2022(Directory dataInputDir, Directory dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {		
		mParameters.add("--input=" + mDataInputDir + "/dtb/d202/dontworrybehappy-invalid/ncc.html");
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "Should report errors.";
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
