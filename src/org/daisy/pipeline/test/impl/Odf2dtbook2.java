package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.EFolder;

public class Odf2dtbook2 extends PipelineTest {

	public Odf2dtbook2(EFolder dataInputDir, EFolder dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {
		mParameters.add("--odf=" + mDataInputDir + "/odf/error.odf.odt");
		mParameters.add("--dtbook=" + mDataOutputDir + "/Odf2dtbook2/fromOdf.xml");				
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "should return error";
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
