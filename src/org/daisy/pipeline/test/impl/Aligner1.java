package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.EFolder;

public class Aligner1 extends PipelineTest {

	public Aligner1(EFolder dataInputDir, EFolder dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {				
		mParameters.add("--inputXML=" + mDataInputDir + "/dtbook/hauy-2005-1.xml");		
		mParameters.add("--inputAudio=" + "D:/dtbs/d202/bcakeP/");
		mParameters.add("--outputPath="+ "D:/aligner-out/"); 
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "";
	}

	@Override
	public boolean supportsScript(String scriptName) {
		if("XMLAudioAligner.taskScript".equals(scriptName)) {
			return true;
		}		
		return false;
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub		
	}

}
