package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.Directory;

public class Aligner1 extends PipelineTest {

	public Aligner1(Directory dataInputDir, Directory dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {				
		mParameters.add("--inputXML=" + "C:/eclipse/aligner-input/xml/1/christ-pages.xml");		
		mParameters.add("--inputAudio=" + "C:/eclipse/aligner-input/audio/1/christ-pages");
		mParameters.add("--outputPath="+ "C:/eclipse/aligner-output/");
		mParameters.add("--divider="+ "pages");		
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
