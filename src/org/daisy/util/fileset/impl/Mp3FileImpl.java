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

package org.daisy.util.fileset.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Header;

import org.daisy.util.fileset.interfaces.audio.Mp3File;

/**
 * @author Markus Gylling
 */
final class Mp3FileImpl extends AudioFileImpl implements Mp3File {

	private FileInputStream fis = null;
	private Bitstream bts = null;
	private Header header = null;		
	private int version;	
	private int layer;
	private int bitrate;
	private int sampleFrequency;
	private boolean isMono;	
	private boolean isVbr;	
	private boolean hasID3v2;
	private float duration; 
	
	
	Mp3FileImpl(URI uri) throws FileNotFoundException, IOException {
		super(uri, Mp3File.mimeStringConstant);
	}    
		
	public void parse() throws IOException, BitstreamException {		 		
		fis = new FileInputStream(this.getCanonicalPath());
		bts = new Bitstream(fis);
		header = bts.readFrame();			
		
		this.version = header.version();
		this.layer = header.layer();
		this.bitrate = header.bitrate();
		this.sampleFrequency = header.frequency();
		this.isMono = (header.mode()==3);
		this.isVbr = header.vbr();
		this.duration = header.total_ms(fis.available());		
		InputStream id3in = bts.getRawID3v2();
		this.hasID3v2 = (id3in!=null);
		try{
		  id3in.close();
		} catch (Exception e) {
			
		}
		fis.close();
		bts.close();
		fis.close();
	}

	public int getLayer() {		
		return this.layer;
	}

	public int getBitrate() {
		return this.bitrate;
	}

	public int getSampleFrequency() {
		return this.sampleFrequency;
	}

	public boolean isMono() {
		return this.isMono;
	}

	public boolean isVbr() {
		return this.isVbr;
	}

	public long getCalculatedDurationMillis() {
		return new Float(duration).longValue();
	}
    
	public boolean hasID3v2() {
		return this.hasID3v2;
	}

	public boolean isMpeg1() {
		return (this.version == 1);
	}

	public boolean isMpeg2Lsf() {
		return (this.version == 0);
	}

	public boolean isMpeg25Lsf() {
		return (this.version == 2);
	}
	
	private static final long serialVersionUID = -639041680742343889L;
}