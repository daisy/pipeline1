package se_tpb_dtbookFix;

import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.daisy.pipeline.exception.TransformerRunException;

/**
 * Abstract base for all executors.
 * Any executor is a member of 1.-n ExecutorCategory
 * @author Markus Gylling
 */
abstract class Executor {
	protected Map<String, String> mParameters = null;
	protected String mNiceName = null;
		
	/**
	 * Constructor.
	 * @param parameters
	 * @param niceName
	 */
	Executor(Map<String, String> parameters, String niceName) {
		mParameters = parameters;
		mNiceName = niceName;
	}
		
	/**
	 * Does this Executor support inparam DTBook version?
	 */
	abstract boolean supportsVersion(String version);
	
	/**
	 * A user friendly name of this Executor.
	 */
	String getNiceName() {
		return mNiceName;
	}
	
	/**
	 * Apply the executor to <code>source</code> and place the output in <code>result</code>.
	 */
	abstract void execute(Source source, Result result) throws TransformerRunException;
	
}
