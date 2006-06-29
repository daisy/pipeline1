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
 package se_tpb_xmldetection;

import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;

/**
 * @author Linus Ericson
 */
class MatchingTags {

	private class TagInfo {
		public QName qname = null;
		public boolean hasMatchingTag = false;
		public boolean isStartTag = false;
		public TagInfo(QName name, boolean start) {
			qname = name;
			isStartTag = start;
		}
	}
	
	private Vector tagInfo = new Vector();
	
	public void add(StartElement se) {
		tagInfo.add(new TagInfo(se.getName(), true));
	}
	
	public void add(EndElement ee) {
		TagInfo info = new TagInfo(ee.getName(), false);
		
		for (int i = tagInfo.size() - 1; i >= 0; i--) {
			TagInfo tagi = (TagInfo)tagInfo.get(i);
			if (info.qname.equals(tagi.qname) && tagi.isStartTag && !tagi.hasMatchingTag) {
				tagi.hasMatchingTag = true;
				info.hasMatchingTag = true;
				break;
			} 
		}
		
		tagInfo.add(info);
	}
	
	public boolean hasMatchingTag(int i) {
		if (i < 0 || i > tagInfo.size() -1) {
			throw new IllegalArgumentException();
		}
		TagInfo info = (TagInfo)tagInfo.get(i);		
		return info.hasMatchingTag;
	}
}
