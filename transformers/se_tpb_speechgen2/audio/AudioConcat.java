package se_tpb_speechgen2.audio;

/*
 *	AudioConcat.java
 *
 *	This file is part of jsresources.org
 */

/*
 * Copyright (c) 1999 - 2001 by Matthias Pfisterer
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 |<---            this code is formatted to fit into 80 columns             --->|
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;


//TODO: the name AudioConcat is no longer appropriate. 
//There should be a name that is neutral to concat/mix.

/**	<titleabbrev>AudioConcat</titleabbrev>
 <title>Concatenating or mixing audio files</title>
 
 <formalpara><title>Purpose</title>
 <para>This program reads multiple audio files and
 writes a single one either
 containing the data of all the other
 files in order (concatenation mode, option <option>-c</option>)
 or containing a mixdown of all the other files
 (mixing mode, option <option>-m</option>).
 For concatenation, the input files must have the same audio
 format. They need not have the same file type.</para>
 </formalpara>
 
 <formalpara><title>Usage</title>
 <para>
 <cmdsynopsis>
 <command>java AudioConcat</command>
 <arg choice="plain"><option>-h</option></arg>
 </cmdsynopsis>
 <cmdsynopsis>
 <command>java AudioConcat</command>
 <arg choice="opt"><option>-D</option></arg>
 <group choice="plain">
 <arg><option>-c</option></arg>
 <arg><option>-m</option></arg>
 </group>
 <arg choice="plain"><option>-o <replaceable>outputfile</replaceable></option></arg>
 <arg choice="plain" rep="repeat"><replaceable>inputfile</replaceable></arg>
 </cmdsynopsis>
 </para>
 </formalpara>
 
 <formalpara><title>Parameters</title>
 <variablelist>
 <varlistentry>
 <term><option>-c</option></term>
 <listitem><para>selects concatenation mode</para></listitem>
 </varlistentry>
 <varlistentry>
 <term><option>-m</option></term>
 <listitem><para>selects mixing mode</para></listitem>
 </varlistentry>
 <varlistentry>
 <term><option>-o <replaceable>outputfile</replaceable></option></term>
 <listitem><para>The filename of the output file</para></listitem>
 </varlistentry>
 <varlistentry>
 <term><replaceable>inputfile</replaceable></term>
 <listitem><para>the name(s) of input file(s)</para></listitem>
 </varlistentry>
 </variablelist>
 </formalpara>
 
 <formalpara><title>Bugs, limitations</title>
 <para>
 This program is not well-tested. Output is always a WAV
 file. Future versions should be able to convert
 different audio formats to a dedicated target format.
 </para></formalpara>
 
 <formalpara><title>Source code</title>
 <para>
 <ulink url="AudioConcat.java.html">AudioConcat.java</ulink>,
 <ulink url="SequenceAudioInputStream.java.html">SequenceAudioInputStream.java</ulink>,
 <ulink url="MixingAudioInputStream.java.html">MixingAudioInputStream.java</ulink>,
 <ulink url="http://www.urbanophile.com/arenn/hacking/download.html">gnu.getopt.Getopt</ulink>
 </para>
 </formalpara>
 
 */
public class AudioConcat
{
	
	/**	Flag for debugging messages.
	 *	If true, some messages are dumped to the console
	 *	during operation.	
	 */
	private static boolean DEBUG = false;
	
	
	/* 
	 * Martin Blomberg 2007-01-10
	 * Returns the highest quality AudioFormat.
	 */
	private static AudioFormat highestQuality(AudioFormat f1, AudioFormat f2) {
		// basics
		if (null == f1) {
			return f2;
		}
		
		if (null == f2) {
			return f1;
		}
		
		if (f1.matches(f2)) {
			return f1;
		}
		
		float sampleRate = 
			Math.max(f1.getSampleRate(), f2.getSampleRate());
		int sampleSizeInBits = 
			Math.max(f1.getSampleSizeInBits(), f2.getSampleSizeInBits());
		int channels = 
			Math.max(f1.getChannels(), f2.getChannels());
		boolean signed = true;
		boolean bigEndian = false;
		
		return new AudioFormat(
				sampleRate, 
				sampleSizeInBits, 
				channels, 
				signed, 
				bigEndian);
	}
	
	/*
	 * Martin Blomberg 2007-01-10
	 * Concatenates the audio in inputList, the result is written to 
	 * outputFile.
	 * 
	 * If the input files has got different AudioFormats, an attempt is made
	 * to convert all the files to the same AudioFormat. The format chosen is
	 * the format with the highest quality found among the input files.
	 * 
	 * For the conversion to actually happen (not produce an Exception because 
	 * the conversion was not supported), I had to add two jar files to the 
	 * class path:
	 * 	tritonus_share-0.3.6.jar, and
	 * 	tritonus_remaining-0.3.6.jar
	 * Both provided by Tritonus (www.tritonus.org) under LGPL.
	 * 	
	 */
	public static void concat(List<File> inputFiles, File outputFile) {
		AudioFormat highestQuality = null;
		
		for (Iterator<File> it = inputFiles.iterator(); it.hasNext(); ) {
			File f = it.next();
			try {
				AudioInputStream ais = AudioSystem.getAudioInputStream(f);
				AudioFormat af = ais.getFormat();
				highestQuality = highestQuality(af, highestQuality);
				ais.close();
			} catch (UnsupportedAudioFileException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
				throw new IllegalArgumentException(e.getMessage(), e);
			} catch (IOException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
				throw new IllegalArgumentException(e.getMessage(), e);
			}
		}
		
		if (null == highestQuality) {
			String msg = "Unable to determine audio format for input " +
					"files, qty: " + inputFiles.size();
			throw new IllegalArgumentException(msg);
		}
		
		concat(inputFiles, outputFile, highestQuality);
	}
	
	public static void concat(List<File> inputFiles, File outputFile, AudioFormat outputFormat) {
		AudioFormat	audioFormat = null;
		List<AudioInputStream> audioInputStreamList = new ArrayList<AudioInputStream>();
		
		/*
		 *	All remaining arguments are assumed to be filenames of
		 *	soundfiles we want to play.
		 */
		for (int i = 0; i < inputFiles.size(); i++)
		{
			File soundFile = inputFiles.get(i);
			
			/*
			 *	We have to read in the sound file.
			 */
			AudioInputStream audioInputStream = null;
			try
			{
				AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
				audioInputStream = AudioSystem.getAudioInputStream(outputFormat, ais);
			}
			catch (Exception e)
			{
				/*
				 *	In case of an exception, we dump the exception
				 *	including the stack trace to the console output.
				 *	Then, we exit the program.
				 */
				e.printStackTrace();
				System.exit(1);
			}
			AudioFormat	format = audioInputStream.getFormat();
			/*
			 The first input file determines the audio format. This stream's
			 AudioFormat is stored. All other streams are checked against
			 this format.
			 */
			if (audioFormat == null)
			{
				audioFormat = format;
				if (DEBUG) { out("AudioConcat.main(): format: " + audioFormat); }
			}
			else if ( ! audioFormat.matches(format))
			{
				// TODO: try to convert
				out("AudioConcat.concat(): WARNING: AudioFormats don't match");
				out("AudioConcat.concat(): master format: " + audioFormat);
				out("AudioConcat.concat(): this format: " + format);
			}
			audioInputStreamList.add(audioInputStream);
		}
		
		if (audioFormat == null)
		{
			out("No input filenames!");
			printUsageAndExit();
		}
		
		AudioInputStream audioInputStream = null;
		
		audioInputStream = new SequenceAudioInputStream(audioFormat, audioInputStreamList);
		
		try
		{
			AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, outputFile);
			audioInputStream.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		} finally {
			for (Iterator<AudioInputStream> it = audioInputStreamList.iterator(); it.hasNext(); ) {
				AudioInputStream ais = it.next();
				try {
					ais.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		if (DEBUG) { 
			out("AudioConcat.main(): before exit"); 
		}
	}
	
	private static void printUsageAndExit()
	{
		out("AudioConcat: usage:");
		out("\tjava AudioConcat -h");
		out("\tjava AudioConcat [-D] -c|-m -o <outputfile> <inputfile> ...");
		System.exit(1);
	}
		
	
	private static void out(String strMessage)
	{
		System.out.println(strMessage);
	}
}



/*** AudioConcat.java ***/

