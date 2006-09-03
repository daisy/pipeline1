package org.daisy.util.fileset.validation.message;

import java.net.URI;

/**
 * Base class for all ValidatorMessages.
 * @author Markus Gylling
 */
public class ValidatorMessage {
	
	private final int mLine;
	private final String mMessage;
	private final int mColumn;
	private final URI mFileURI;

	public ValidatorMessage(URI file, String message, int line, int column) {
		this.mFileURI = file;
		this.mMessage = message;
		this.mLine = line;
		this.mColumn = column;		
	}

	public ValidatorMessage(URI file, String message) {
		this.mFileURI = file;
		this.mMessage = message;
		this.mLine = -1;
		this.mColumn = -1;
	}
	
	public URI getFile() {
		return mFileURI;
	}

	public String getMessage() {
		return mMessage;
	}
	
	public int getLine() {
		return mLine;
	}
	
	public int getColumn() {
		return mColumn;
	}

	/**
	 * Render this validator message as a convenience string.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(this instanceof ValidatorWarningMessage) {
			sb.append("Warning in ");
		}else if(this instanceof ValidatorErrorMessage) {
			sb.append("Error in ");
		}else if(this instanceof ValidatorSevereErrorMessage) {
			sb.append("Severe error in ");
		}else{
			sb.append("Error with unknown severity in ");
		}		
		
		if(mFileURI!=null) {
			sb.append(mFileURI.toString());
		}else{
			sb.append("[unknown]");
		}
		sb.append(' ');		
		if(mLine!=-1) {
			sb.append('[').append(mLine);
			if(mColumn!=-1) {
				sb.append(", ").append(mColumn);
			}
			sb.append(']');
		}
		sb.append(": ").append(mMessage);
		
		return sb.toString();
	}
}
