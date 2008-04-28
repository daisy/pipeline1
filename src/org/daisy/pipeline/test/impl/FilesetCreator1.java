package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.Directory;

public class FilesetCreator1 extends PipelineTest {

	public FilesetCreator1(Directory dataInputDir, Directory dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {				
//		mParameters.add("--manuscriptFilename=" + "D:\\_temp_dev\\aligner-out\\pipeline__temp\\03-aligned.xml");		
//		mParameters.add("--outputDTBFilename=" + "output.xml");
//		mParameters.add("--outputDirectory="+ "D:\\_temp_dev\\aligner-out\\temp"); 

		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "";
	}

	@Override
	public boolean supportsScript(String scriptName) {
		if("FilesetCreatorTemp.taskScript".equals(scriptName)) {
			return true;
		}		
		return false;
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub		
	}

}
