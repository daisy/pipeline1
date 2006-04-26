/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2006  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.daisy.dmfc.transformers;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.dmfc.exception.TransformerRunException;
import org.python.util.jython;

/**
 * 
 * @author Linus Ericson
 */
public class PythonRunner extends Transformer {
	
	private static Pattern variablePattern = Pattern.compile("\\$\\{(\\w+)\\}");

	public PythonRunner(InputListener inListener, Set eventListeners, Boolean isInteractive) {
		super(inListener, eventListeners, isInteractive);
	}

	protected boolean execute(Map parameters) throws TransformerRunException {
		// Count the number of arguments
		int argc = 0;
		while (true) {
			String arg = "arg" + String.valueOf(argc);
			if (!parameters.containsKey(arg)) {
				break;
			}
			++argc;
		}
		
		// Create arguments array
		String[] args = new String[argc];
		for (int i = 0; i < argc; ++i) {
			String argNum = "arg" + String.valueOf(i);
			args[i] = expandCommandPattern((String)parameters.get(argNum), parameters);
			//System.err.println("Item: '" + argNum + "', '" + (String)parameters.get(argNum) + "', '" + args[i] + "'");
		}
		/*
		// Read parameters
		String commandPattern = (String)parameters.remove("python_command");   
		
		// Expand command pattern
        String command = expandCommandPattern(commandPattern, parameters); 
        
        System.err.println("Command: " + command);*/
        jython.main(args);
		return false;
	}
	
	private String expandCommandPattern(String commandPattern, Map parameters) throws TransformerRunException {
        if (commandPattern == null) {
            return "";
        }
        Matcher matcher = variablePattern.matcher(commandPattern);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String variable = matcher.group(1);
            String value = (String)parameters.get(variable);
            if (value == null) {
                throw new TransformerRunException(i18n("UNRECOGIZED_COMMAD_PATTERN_VARIABLE", variable));
            }
            matcher.appendReplacement(sb, value);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

}
