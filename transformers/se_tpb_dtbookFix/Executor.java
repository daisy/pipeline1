package se_tpb_dtbookFix;

import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.daisy.pipeline.exception.TransformerRunException;

/**
 * Abstract base for all executors.
 * @author Markus Gylling
 */
abstract class Executor {
	protected Map<String, String> mParameters = null;
	protected String mNiceName = null;
		
	Executor(Map<String, String> parameters, String niceName) {
		mParameters = parameters;
		mNiceName = niceName;
	}
				
	abstract boolean supportsVersion(String version);
	
	String getNiceName() {
		return mNiceName;
	}
					
	abstract void execute(Source source, Result result) throws TransformerRunException;
	
}
