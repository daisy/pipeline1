/*
 * org.daisy.util - The DAISY java utility library
 * Copyright (C) 2005  Daisy Consortium
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

package org.daisy.util.fileset;

import org.daisy.util.mime.MIMEConstants;

/**
 * Represents an MP3 file.
 * @author Markus Gylling
 */
public interface Mp2File extends AudioFile { 
	
	static String mimeStringConstant = MIMEConstants.MIME_AUDIO_MPEG2;
		
	public boolean isMpeg1();
	
	public boolean isMpeg2Lsf();
	
	public boolean isMpeg25Lsf();
	
	public int getLayer();
	
	public int getBitrate();
			
	public boolean isVbr();
	
	public boolean hasID3v2();
	
	/**
	 * @deprecated use AudioFile.getLength instead
	 */

	public long getCalculatedDurationMillis();
	
}
