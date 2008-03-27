package se_tpb_aligner.textpre.impl;

import java.io.File;
import java.util.ArrayList;

import org.daisy.util.execution.Command;
import org.daisy.util.file.FilenameOrFileURI;

import se_tpb_aligner.textpre.PreProcessor;
import se_tpb_aligner.textpre.PreProcessorException;
import se_tpb_aligner.util.Result;
import se_tpb_aligner.util.Source;

/**
 * A wrapper around Christina Ericssons linguistic preprocessor.
 * <p>Location of the binary executable is donw through the System property "pipeline.aligner.tpbpreprocessor.path".</p>
 * @author Markus Gylling
 */
public class TpbPreProcessorImpl extends PreProcessor {
	private String exePath = null;
	
	public TpbPreProcessorImpl() {
		super();
	}
	
	@Override
	public Result process(Source source, String language, Result result) throws PreProcessorException {
		
		try{
			/*
			 * Check that we have the binary available
			 */
			exePath = System.getProperty("pipeline.aligner.tpbpreprocessor.path");
			File f = FilenameOrFileURI.toFile(exePath);
			if(!f.exists()) throw new PreProcessorException(exePath + " does not exist");
			
			/*
			 * Build the command string.
			 * perl path/textprocXML.pl --inputXML=pathspec --inputLanguage=langspec --resultPath=pathspec --mode=align 
			 */						
	        ArrayList<String> arr = new ArrayList<String>();
	        arr.add("perl");
	        arr.add(exePath);        
	        arr.add("--inputXML="+source.getCanonicalPath());        
	        arr.add("--inputLanguage="+language);
	        arr.add("--resultPath="+result.getCanonicalPath());
	        arr.add("--mode="+"align");
	
	        /*
	         * Execute
	         */
	        int ret;
	        System.err.println("Encoding: " + arr.toString());        	
            ret = Command.execute((String[])(arr.toArray(new String[arr.size()])));
            if(ret == -1) {
            	throw new PreProcessorException(exePath + " returned -1");
            }
            
        } catch (Exception e) {
            throw new PreProcessorException(e.getMessage());
        }
				
		return result;		
	}

	@Override
	public boolean supportsLanguage(String language) {
		if(language.startsWith("sv")||language.startsWith("en")||language.startsWith("nob")) {
			return true;
		}
		return false;
	}

}
