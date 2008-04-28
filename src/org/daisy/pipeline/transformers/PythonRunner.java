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
package org.daisy.pipeline.transformers;

import java.util.EventListener;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.execution.Command;
import org.daisy.util.execution.ExecutionException;

/**
 * 
 * @author Linus Ericson
 */
public class PythonRunner extends Transformer {
	
	private static String pythonCommand = null;
	private static boolean supported = false;
	
	private static Pattern variablePattern = Pattern.compile("\\$\\{(\\w+)\\}");

	public PythonRunner(InputListener inListener, @SuppressWarnings("unused")
	Set<EventListener> eventListeners, Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	protected boolean execute(Map<String,String> parameters) throws TransformerRunException {
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
		String[] args = new String[argc + 1];
		args[0] = pythonCommand;
		for (int i = 0; i < argc; ++i) {
			String argNum = "arg" + String.valueOf(i);
			args[i+1] = expandCommandPattern(parameters.get(argNum), parameters);
			//System.err.println("Item: '" + argNum + "', '" + (String)parameters.get(argNum) + "', '" + args[i] + "'");
		}
		
		int result = -1;
		try {
			result = Command.execute(args);
		} catch (ExecutionException e) {
			throw new TransformerRunException(e.getMessage(), e);
		}
		
		return result == 0;
	}
	
	private String expandCommandPattern(String commandPattern, Map<String,String> parameters) throws TransformerRunException {
        if (commandPattern == null) {
            return "";
        }
        Matcher matcher = variablePattern.matcher(commandPattern);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String variable = matcher.group(1);
            String value = parameters.get(variable);
            if (value == null) {
                throw new TransformerRunException(i18n("UNRECOGIZED_COMMAD_PATTERN_VARIABLE", variable));
            }
            matcher.appendReplacement(sb, value);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
	
	public static boolean isSupported() {
		boolean result = supported;
		if (pythonCommand == null) {
			synchronized (PythonRunner.class) {
				if (pythonCommand == null) {
					pythonCommand = System.getProperty("dmfc.python.path");
					String[] params = new String[2];
					params[0] = pythonCommand;
					params[1] = "--version";
					
					try {
						if (Command.execute(params) == 0) {
							result = true;
						}
					} catch (ExecutionException e) {						
						e.printStackTrace();
						result = false;
					}
				}
			}
		}
		return result;
	}

}
