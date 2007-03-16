package org.daisy.dmfc.core.script;

import java.util.HashMap;
import java.util.Map;

import org.daisy.dmfc.core.script.datatype.DatatypeException;

/**
 * A class used for setting parameters before running a script.
 * @author Linus Ericson
 */
public class ScriptRunner {

	private Script mScript;
	private Map<String,AbstractProperty> mParameters = new HashMap<String,AbstractProperty>();
	
	/**
	 * Creates a new ScriptRunner object from a Script.
	 * @param script
	 */
	public ScriptRunner(Script script) {
		this.mScript = script;
	}
	
	/**
	 * Gets the script object
	 * @return
	 */
	public Script getScript() {
		return mScript;
	}
	
	/**
	 * Gets a specific ScriptParameter. If the specified ScriptParameter is
	 * not found, null is returned.
	 * @param name the name of the ScriptParameter to get
	 * @return a ScriptParameter, or null
	 */
	public ScriptParameter getScriptParameter(String name) {
		return mScript.getParameter(name);
	}
	
	/**
	 * Sets a parameter value.
	 * @param name the name of the parameter
	 * @param value the value of the parameter
	 * @throws DatatypeException
	 */
	public void setParameterValue(String name, String value) throws DatatypeException {
		ScriptParameter parameter = mScript.getParameter(name);
		if (parameter != null) {
			parameter.getDatatype().validate(value);
			mParameters.put(name, new StringProperty(name, value));
		}
	}
	
	/**
	 * Gets the parameter value previously set in this ScriptRunner.
	 * @param name the name of the parameter
	 * @return the value of the parameter or null if the parameter was not set
	 */
	public String getParameterValue(String name) {
		AbstractProperty prop = mParameters.get(name);
		if (prop != null) {
			return prop.getValue();
		}
		return null;
	}
	
	/**
	 * Gets a map containing all parameters set in this ScriptRunner
	 * @return
	 */
	/*package*/ Map<String,AbstractProperty> getParameters() {
		return mParameters;
	}
	
	/**
	 * Have all required script parameters been set in this ScriptRunner?
	 * @return
	 */
	public boolean allRequiredParametersSet() {
		boolean result = true;
		for (ScriptParameter parameter : mScript.getParameters().values()) {
			if (parameter.isRequired() && !mParameters.containsKey(parameter.getName())) {
				result = false;
			}
		}
		return result;
	}
}
