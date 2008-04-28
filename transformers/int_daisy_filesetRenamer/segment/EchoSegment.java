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
package int_daisy_filesetRenamer.segment;

import org.daisy.util.fileset.FilesetFile;

/**
 * Represents a segment of a filename, which is an exact copy of the input filename (minus extension segment).
 * @author Markus Gylling
 */
public class EchoSegment extends Segment {
		
	private EchoSegment(String content) {
		super(content);		
	}

	/**
	 * Create a new segment, regarding the inparam string as the value to echo.
	 */
	public static EchoSegment create(String toEcho) {
		if(toEcho==null)return new EchoSegment("");
		return new EchoSegment(toEcho);
	}
	
	/**
	 * Create a new segment, regarding the inparam FilesetFiles name as the value to echo.
	 */
	public static EchoSegment create(FilesetFile toEcho) {
		if(toEcho==null)return new EchoSegment("");
		return new EchoSegment(toEcho.getNameMinusExtension());
	}
	
}
