/*
 * org.daisy.util (C) 2005-2008 Daisy Consortium
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
package org.daisy.util.file.detect;

import javax.xml.stream.events.StartElement;

/**
 *
 * @author Markus Gylling
 */
/*package*/ class ResourceXMLProperties extends ResourceProperties {

	private String mPublicId;
	private String mSystemId;
	private StartElement mStartElement;

	protected ResourceXMLProperties(String fileName, String pid, String sid, StartElement se) {
		super(fileName);
		mPublicId = pid;
		mSystemId = sid;
		mStartElement = se;
	}

	/*package*/ String getPublicId() {
		return mPublicId;
	}
	
	/*package*/ String getSystemId() {
		return mSystemId;
	}
	
	/*package*/ StartElement getRootElement() {
		return mStartElement;
	}
}
