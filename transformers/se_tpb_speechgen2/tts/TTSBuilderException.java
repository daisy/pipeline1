/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package se_tpb_speechgen2.tts;

/**
 * Exception thrown during tts builder phase.
 * 
 * @author Martin Blomberg
 *
 */
public class TTSBuilderException extends Exception {

	public TTSBuilderException(String msg) {
		super(msg);
	}
	
	public TTSBuilderException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	private static final long serialVersionUID = -463951243806137978L;
}
