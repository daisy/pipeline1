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

import java.io.File;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;

import org.w3c.dom.Document;

/**
 * A TTSAdapters's general view of its input.
 * 
 * @author Martin Blomberg
 *
 */
public interface TTSInput {
	
	/**
	 * Returns <code>true</code> if the object is an instance of TTSAnnouncement,
	 * <code>false</code> otherwise.
	 * @return <code>true</code> if the object is an instance of TTSAnnouncement,
	 * <code>false</code> otherwise.
	 */
	public boolean isAnnouncement();
	
	/**
	 * Returns <code>true</code> if the object is an instance of TTSSyncPoint,
	 * <code>false</code> otherwise.
	 * @return <code>true</code> if the object is an instance of TTSSyncPoint,
	 * <code>false</code> otherwise.
	 */
	public boolean isSyncPoint();
	
	/**
	 * If this object is an instance of TTSAnnouncement, the method returns a 
	 * list of the start elements containing announcements. null otherwise.
	 * @return If this object is an instance of TTSAnnouncement, the method returns a 
	 * list of the start elements containing announcements. null otherwise.
	 */
	public List<StartElement> getAnnouncements();
	
	/**
	 * If this object is an instance of TTSAnnouncement, the method returns the 
	 * qname used to determine which attribute to use for announcements, null otherwise.
	 * @return If this object is an instance of TTSAnnouncement, the method returns the 
	 * qname used to determine which attribute to use for announcements, null otherwise.
	 */
	public QName getQName();
	
	/**
	 * Returns the xml representing the syncpoint.
	 * @return the xml representing the syncpoint.
	 */
	public Document getSyncPoint();
	
	/**
	 * Returns the number from the sequence in which the input was extracted
	 * from the source document and added to the input queue. That is, the first
	 * input from the document returns 1, the second 2, and so on.
	 * @return the number from the sequence in which the input was extracted
	 * from the source document and added to the input queue. 
	 */
	public int getNumber();
	
	/**
	 * Returns the file in which to store the generated output.
	 * @return the file in which to store the generated output.
	 */
	public File getFile();
}
