package se_tpb_dtbookFix;

import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.daisy.pipeline.exception.TransformerRunException;

/**
 * Abstract base for all executors.
 * @author Markus Gylling
 */
public abstract class DTBookFixExecutor {
	protected Map<String, String> mParameters = null;
	protected String mNiceName = null;
		
	public DTBookFixExecutor(Map<String, String> parameters, String niceName) {
		mParameters = parameters;
		mNiceName = niceName;
	}
				
	public abstract boolean supportsVersion(String version);
	
	public String getNiceName() {
		return mNiceName;
	}
					
	public abstract void execute(Source source, Result result) throws TransformerRunException;
	
}
