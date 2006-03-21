package org.daisy.dmfc.qmanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.Prompt;

public class LocalInputListener implements InputListener {

	public String getInputAsString(Prompt prompt) {
		System.err.println("[" + prompt.getMessageOriginator() + "] Prompt: " + prompt.getMessage());
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		try {
		    line = br.readLine();
        } catch (IOException e) {
        }
		return line;
	}

    public boolean isAborted() {
        return false;
    }
	
}
