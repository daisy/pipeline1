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
 * TTSAnnouncements.
 * @author Martin Blomberg
 *
 */
public class TTSAnnouncement implements TTSInput {

	private List<StartElement> mAnnouncements;	// the list of start elelements containing announcements
	private QName mQname;						// qname pointing out attributes 
	private File mOutputFile;					// the file in which to store the produced audio
	private int mQueueNumber;					// the queue number for this tts job
	
	/**
	 * @param ann a List containing StAX StartElements with announcement attributes.
	 * @param qn the qname used to determine which attributes to use for announcements.
	 * @param file the destination of the generated output.
	 * @param nr this input's sequence number.
	 */
	public TTSAnnouncement(List<StartElement> ann, QName qn, File file, int nr) {
		if (null == ann) {
			String msg = "List of announcements is null, that is not ok.";
			throw new IllegalArgumentException(msg);
		}
		
		if (ann.size() == 0) {
			String msg = "List of announcements is empty, that is not ok.";
			throw new IllegalArgumentException(msg);
		}
		
		mAnnouncements = ann;
		mQname = qn;
		mOutputFile = file;
		mQueueNumber = nr;
	}
	
	/* (non-Javadoc)
	 * @see se_tpb_speechgen2.tts.TTSInput#getAnnouncements()
	 */
	public List<StartElement> getAnnouncements() {
		return mAnnouncements;
	}

	/* (non-Javadoc)
	 * @see se_tpb_speechgen2.tts.TTSInput#getFile()
	 */
	public File getFile() {
		return mOutputFile;
	}

	/* (non-Javadoc)
	 * @see se_tpb_speechgen2.tts.TTSInput#getNumber()
	 */
	public int getNumber() {
		return mQueueNumber;
	}

	/* (non-Javadoc)
	 * @see se_tpb_speechgen2.tts.TTSInput#getSyncPoint()
	 */
	public Document getSyncPoint() {
		return null;
	}

	/* (non-Javadoc)
	 * @see se_tpb_speechgen2.tts.TTSInput#isAnnouncement()
	 */
	public boolean isAnnouncement() {
		return true;
	}

	/* (non-Javadoc)
	 * @see se_tpb_speechgen2.tts.TTSInput#isSyncPoint()
	 */
	public boolean isSyncPoint() {
		return false;
	}

	/* (non-Javadoc)
	 * @see se_tpb_speechgen2.tts.TTSInput#getQName()
	 */
	public QName getQName() {
		return mQname;
	}
}
