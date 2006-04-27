package org.daisy.util.fileset.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.daisy.util.fileset.interfaces.audio.Mp3File;
import org.daisy.util.mime.MIMETypeException;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Header;

/**
 * @author Markus Gylling
 */
class Mp3FileImpl extends AudioFileImpl implements Mp3File {

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
	
	
	Mp3FileImpl(URI uri) throws FileNotFoundException, IOException, MIMETypeException {
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