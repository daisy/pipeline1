package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.EFolder;

public class Odf2dtbook1 extends PipelineTest {

	public Odf2dtbook1(EFolder dataInputDir, EFolder dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {
		mParameters.add("--odf=" + mDataInputDir + "/odf/simple2.odt");
		//mParameters.add("--odf=" + mDataInputDir + "/odf/simple.odt");
		mParameters.add("--dtbook=" + mDataOutputDir + "/Odf2dtbook1/fromOdf.xml");				
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "Should generate a valid DTBook file par possible metadata missing";
	}

	@Override
	public boolean supportsScript(String scriptName) {
		if("OdfToDtbook.taskScript".equals(scriptName)) {
			return true;
		}		
		return false;
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub
		
	}

}
