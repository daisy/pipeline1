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

import org.daisy.util.file.EFile;

/**
 * Represents an extension segment of a filename, including the separator (period).
 * @author Markus Gylling
 */
public class ExtensionSegment extends Segment {
	
	private ExtensionSegment(String content) {
		super(content);		
	}
		
	/**
	 * Create a new segment, regarding the inparam string as readymade content
	 */
	public static ExtensionSegment create(String content) {
		if(content==null || content.length()==0) return new ExtensionSegment("");
		if(content.length()>0 && !content.startsWith(".")) content = "."+content;
		//for now, a Z3986 jpeg fixer here. 
		if(content.toLowerCase().contentEquals("jpeg")) content = "jpg";
		return new ExtensionSegment(content);
	}
	
	/**
	 * Create a new segment, regarding the inparam files extension as content
	 */
	public static ExtensionSegment create(EFile file) {
		if(file.getExtension()!=null && file.getExtension().length()>0) {
			return new ExtensionSegment("."+file.getExtension());
		}
		return new ExtensionSegment("");
	}
}
