package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.EFolder;

public class OcfCreator1 extends PipelineTest {

	public OcfCreator1(EFolder dataInputDir, EFolder dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {
		mParameters.add("--input=" + mDataInputDir + "/ops/package.opf,"+mDataInputDir + "/txt/wasteland.txt");
		mParameters.add("--output=" + mDataOutputDir + "/OcfCreator1/ocf.epub");				
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "Create an epub with OEBPS and TXT entries";
	}

	@Override
	public boolean supportsScript(String scriptName) {
		if("OCFCreator.taskScript".equals(scriptName)) {
			return true;
		}		
		return false;
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub
		
	}

}
