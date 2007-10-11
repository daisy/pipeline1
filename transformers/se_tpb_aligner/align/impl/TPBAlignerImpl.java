package se_tpb_aligner.align.impl;

import java.io.File;
import java.util.ArrayList;

import org.daisy.util.execution.Command;
import org.daisy.util.file.FilenameOrFileURI;

import se_tpb_aligner.align.Aligner;
import se_tpb_aligner.align.AlignerException;
import se_tpb_aligner.util.AudioSource;
import se_tpb_aligner.util.XMLResult;
import se_tpb_aligner.util.XMLSource;

/**
 * A wrapper around Kåre Sjölanders aligner.
 * <p>Location of the binary executable is done through the System property "pipeline.aligner.tpbaligner.path".</p>
 * @author Markus Gylling
 */
public class TPBAlignerImpl extends Aligner {
	
	private String exePath = null;
	
	public TPBAlignerImpl() {
		super();
	}
	
	@Override
	public XMLResult process(XMLSource inputXML, AudioSource inputAudioFile, String inputLanguage, XMLResult result) throws AlignerException {
		try{
			/*
			 * Check that we have the binary available
			 */
			exePath = System.getProperty("pipeline.aligner.tpbaligner.path");
			File f = FilenameOrFileURI.toFile(exePath);
			if(!f.exists()) throw new AlignerException(exePath + " does not exist");
			
			/*
			 * Build the command string.
			 * tclsh path/dtbalign.tcl -inputXML pathspec -inputAudio pathspec -inputLanguage langspec -resultPath pathspec
			 */
			
	        ArrayList<String> arr = new ArrayList<String>();
	        arr.add("tclsh");
	        arr.add(exePath);        
	        arr.add("-inputXML");
	        arr.add(inputXML.getCanonicalPath());
	        arr.add("-inputAudio");
	        arr.add(inputAudioFile.getCanonicalPath());
	        arr.add("-inputLanguage");
	        arr.add(inputLanguage);
	        arr.add("-resultPath");
	        arr.add(result.getCanonicalPath());
	
	        /*
	         * Execute
	         */
	        int ret;                	
            ret = Command.execute((String[])(arr.toArray(new String[arr.size()])));
            if(ret == -1) {
            	throw new AlignerException(exePath + " returned -1");
            }
                            		
		} catch (Exception e) {
			throw new AlignerException(e.getMessage(),e);
		}		
		return result;		
	}

	
	@Override
	public boolean supportsLanguage(String language) {
		if(language.startsWith("sv")||language.startsWith("en")) {
			return true;
		}
		return false;
	}
	
}
