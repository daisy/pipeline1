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

import org.daisy.util.fileset.Mp2File;
import org.daisy.util.xml.SmilClock;

/**
 * @author Markus Gylling
 */
final class Mp2FileImpl extends AudioFileImpl implements Mp2File {

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
	
	
	Mp2FileImpl(URI uri) throws FileNotFoundException, IOException {
		super(uri,Mp2File.mimeStringConstant);
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
		this.mLength = new SmilClock(this.duration);
		InputStream id3in = bts.getRawID3v2();
		this.hasID3v2 = (id3in!=null);
		try{
		  id3in.close();
		} catch (Exception e) {
			
		}
		fis.close();
		bts.close();
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

	/**
	 * @deprecated use AudioFile.getLength instead
	 */
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

	private static final long serialVersionUID = 3482068273009090650L;
}

//InputStream id3in = in.getRawID3v2();
//int size = id3in.available();
//PrintStream out = System.err;			
//out.println("version="+header.version());
//out.println("version_string="+header.version_string());
//out.println("layer="+header.layer());
//out.println("frequency="+header.frequency());
//out.println("frequency_string="+header.sample_frequency_string());
//out.println("bitrate="+header.bitrate());
//out.println("bitrate_string="+header.bitrate_string());
//out.println("mode="+header.mode());
//out.println("mode_string="+header.mode_string());
//out.println("slots="+header.slots());
//out.println("vbr="+header.vbr());
//out.println("vbr_scale="+header.vbr_scale());
//out.println("max_number_of_frames="+header.max_number_of_frames(fis.available()));
//out.println("min_number_of_frames="+header.min_number_of_frames(fis.available()));
//out.println("ms_per_frame="+header.ms_per_frame());
//out.println("frames_per_second="+(float) ((1.0 / (header.ms_per_frame())) * 1000.0));
//out.println("total_ms="+header.total_ms(fis.available()));
//out.println("SyncHeader="+header.getSyncHeader());
//out.println("checksums="+header.checksums());
//out.println("copyright="+header.copyright());
//out.println("original="+header.original());
//out.println("padding="+header.padding());
//out.println("framesize="+header.calculate_framesize());
//out.println("number_of_subbands="+header.number_of_subbands());