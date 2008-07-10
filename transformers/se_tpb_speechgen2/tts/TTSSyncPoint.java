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
 * @author Martin Blomberg
 *
 */
public class TTSSyncPoint implements TTSInput {

	private Document syncPoint;
	private File outputFile;
	private int number;
	
	/**
	 * @param doc the document, ie the xml fragment, representing the
	 * syncpoint.
	 * @param file the destination of the generated output.
	 * @param nr this input's sequence number.
	 */
	public TTSSyncPoint(Document doc, File file, int nr) {
		syncPoint = doc;
		outputFile = file;
		number = nr;
	}
	
	/* (non-Javadoc)
	 * @see se_tpb_speechgen2.tts.TTSInput#getAnnouncements()
	 */
	public List<StartElement> getAnnouncements() {
		return null;
	}

	/* (non-Javadoc)
	 * @see se_tpb_speechgen2.tts.TTSInput#getFile()
	 */
	public File getFile() {
		return outputFile;
	}

	/* (non-Javadoc)
	 * @see se_tpb_speechgen2.tts.TTSInput#getNumber()
	 */
	public int getNumber() {
		return number;
	}

	/* (non-Javadoc)
	 * @see se_tpb_speechgen2.tts.TTSInput#getSyncPoint()
	 */
	public Document getSyncPoint() {
		return syncPoint;
	}

	/* (non-Javadoc)
	 * @see se_tpb_speechgen2.tts.TTSInput#isAnnouncement()
	 */
	public boolean isAnnouncement() {
		return false;
	}

	/* (non-Javadoc)
	 * @see se_tpb_speechgen2.tts.TTSInput#isSyncPoint()
	 */
	public boolean isSyncPoint() {
		return true;
	}

	/* (non-Javadoc)
	 * @see se_tpb_speechgen2.tts.TTSInput#getQName()
	 */
	public QName getQName() {
		return null;
	}
}
