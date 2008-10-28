package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.Directory;

public class DTBook2LaTeX extends PipelineTest {

	public DTBook2LaTeX(Directory dataInputDir, Directory dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {
		mParameters.add("--input=" + mDataInputDir + "/dtbook/hauy-2005-1.xml");
		mParameters.add("--output=" + mDataOutputDir + "/DTBook2LaTeX/fromDTBook.tex");		
		mParameters.add("--fontsize="+"20pt");
		mParameters.add("--fontfamily="+"cmss");
		mParameters.add("--defaultLanguage="+"ngerman");
		mParameters.add("--papersize="+"letterpaper");
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "";
	}

	@Override
	public boolean supportsScript(String scriptName) {
		if("DTBookToLaTeX.taskScript".equals(scriptName)) {
			return true;
		}		
		return false;
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub
		
	}

}
