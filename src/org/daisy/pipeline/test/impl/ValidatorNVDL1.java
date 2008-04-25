package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.EFolder;

public class ValidatorNVDL1 extends PipelineTest {

	public ValidatorNVDL1(EFolder dataInputDir, EFolder dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {		
		mParameters.add("--validatorInputFile=" + mDataInputDir + "/xhtml/xhtml-rdf.xml");
		mParameters.add("--validatorInputSchemas=" + mDataInputDir + "/schema/xhtml-rdf.nvdl");
				
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "Should return one error, xhtml-rdf.nvdl only allows rdf in head";
	}

	@Override
	public boolean supportsScript(String scriptName) {
		if("ConfigurableValidator.taskScript".equals(scriptName)) {
			return true;
		}		
		return false;
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub
		
	}

}