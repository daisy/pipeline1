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
package int_daisy_unicodeNormalizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.XMLEvent;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.EFile;
import org.daisy.util.file.EFolder;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetErrorHandler;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.interfaces.xml.SmilFile;
import org.daisy.util.fileset.interfaces.xml.XmlFile;
import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.pool.StAXOutputFactoryPool;

import com.ibm.icu.text.Normalizer;

/**
 * Performs unicode normalization on all XML documents in a fileset using 
 * one of the four standard normalization forms provided by the Unicode 
 * Consortium. 
 * 
 * <p>See <a href="http://www.w3.org/TR/charmod-norm">http://www.w3.org/TR/charmod-norm</a></p>
 * 
 *<p>From <a href="http://www.w3.org/TR/charmod-norm/#sec-ChoiceNFC>http://www.w3.org/TR/charmod-norm/#sec-ChoiceNFC</a>:</p>
 *<p>The Unicode Consortium provides four standard normalization forms 
 *(see Unicode Normalization Forms  [UTR #15]). These forms differ in 1 
 *whether they normalize towards decomposed characters (NFD, NFKD) or 
 *precomposed characters (NFC, NFKC) and 2) whether the normalization 
 *process erases compatibility distinctions (NFKD, NFKC) or not (NFD, NFC).</p>
 *		
 *<p>The NFKD and NFKC normalization forms are therefore excluded. 
 *Among the remaining two forms, NFC has the advantage that almost all 
 *legacy data (if transcoded trivially, one-to-one, to a Unicode encoding) 
 *as well as data created by current software is already in this form; 
 *NFC also has a slight compactness advantage and a better match to user 
 *expectations with respect to the character vs. grapheme issue. This document 
 *therefore chooses NFC as the base for Web-related early normalization.</p>	
 * @author Markus Gylling
 */
public class UCNormalizer extends Transformer implements FilesetErrorHandler {
	private EFile mInputManifest = null;
	private Fileset mInputFileset = null;	
	private EFolder mOutputDir;
	private EFolder mInputDir;
	
	private Normalizer.Mode mode = null; 
	private boolean textnodesOnly = false; 
	
    public  UCNormalizer (InputListener inListener,  Boolean isInteractive) {
        super(inListener,  isInteractive);        
    }
    
	@SuppressWarnings("unchecked")
	protected boolean execute(Map parameters) throws TransformerRunException {
		
		final String mTempFileRegex = ".+\\.normalized$";
				
		try{
			/*
			 * Set input file
			 */
			mInputManifest = new EFile(FilenameOrFileURI.toFile((String)parameters.remove("input")));
			
			/*
			 * Set input fileset
			 */
			mInputFileset = new FilesetImpl(mInputManifest.toURI(),this,false,false);
			
			/*
			 * Set input dir
			 */						
			mInputDir = new EFolder(mInputFileset.getManifestMember().getFile().getParentFile());
			
			/*
			 * Set output directory
			 */
			mOutputDir = (EFolder)FileUtils.createDirectory(new EFolder((String)parameters.remove("output")));
			
			/*
			 * Set normalization form
			 */
			mode = setNormalizationForm((String)parameters.remove("normalizationForm"));			
						
			/*
			 * Check user settable node restriction
			 */		
			textnodesOnly = ((String)parameters.remove("textnodesOnly")).equals("true");
									
			this.sendMessage(0.10);
			
			/*
			 * Normalize XML members to temporary files,
			 * copy non-XML untouched.
			 * Note: input and output directory may be the same,
			 * in which case copy is cancelled.
			 */
			int count = 0;
			int processed = 0;
			double filesetSize = Double.parseDouble(Integer.toString(mInputFileset.getLocalMembers().size())+".0");			
			Set<File> tempFiles = new HashSet<File>();
			
			for (Iterator<?> iterator = mInputFileset.getLocalMembers().iterator(); iterator.hasNext();) {		
				checkAbort();
				FilesetFile ffile = (FilesetFile) iterator.next();
				File destination = getDestination(ffile);
				if((ffile instanceof XmlFile) && (!(this.textnodesOnly && ffile instanceof SmilFile))) {
					tempFiles.add(normalize((XmlFile)ffile, new File(destination.getAbsolutePath()+".normalized")));
					processed++;
				}else{
					copy(ffile,destination);
				}
				count++;
				this.sendMessage(0.1 + ((count/filesetSize)*0.9));
			}
						
			/*
			 * Set changed files to final names
			 */			
			realize(tempFiles);
			
			/*
			 * Done.
			 */
			sendMessage(i18n("COMPLETED_NORM", processed), MessageEvent.Type.INFO_FINER);
			this.sendMessage(1.0);
					
			
		} catch (Exception e) {			
			try {
				//clean out the temp files
				mOutputDir.deleteContents(true, mTempFileRegex);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			String message = i18n("ERROR_ABORTING", e.getMessage());
			throw new TransformerRunException(message, e);
		}			
		return true;
	}
	
	public String normalize(String value) {
		//Normalize the value and return
		try{
			return Normalizer.normalize(value,this.mode);			
		}catch (Exception e) {
			this.sendMessage(i18n("ERROR", e.getMessage()), MessageEvent.Type.ERROR,MessageEvent.Cause.SYSTEM);
			return value;
		}
	}
	
	/**
	 * @return the normalized result
	 * @throws XMLStreamException 
	 * @throws IOException 
	 */
	private File normalize(XmlFile input, File destination) throws XMLStreamException, IOException {
		
		XMLEventFactory xef = null;
		XMLInputFactory xif = null;
		Map<String, Object> xifProperties = null;
		XMLOutputFactory xof = null;
		Map<String, Object> xofProperties = null;
		InputStream is = null;
		FileOutputStream fos = null;
		XMLEventWriter xew = null;
		
		try{
			xef = StAXEventFactoryPool.getInstance().acquire();
			xifProperties = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(false);
			xif = StAXInputFactoryPool.getInstance().acquire(xifProperties);
			xofProperties = StAXOutputFactoryPool.getInstance().getDefaultPropertyMap();			
			xof = StAXOutputFactoryPool.getInstance().acquire(xofProperties);
			is = input.asInputStream();			
			XMLEventReader xer = xif.createXMLEventReader(is);
			fos = new FileOutputStream(destination);				
						
			while(xer.hasNext()) {
				XMLEvent event = xer.nextEvent();
				if(event.getEventType() == XMLEvent.START_DOCUMENT) {
					StartDocument sd = (StartDocument) event;
					String enc = sd.getCharacterEncodingScheme();
					if(enc==null||enc.equals(""))enc="utf-8";
					xew = xof.createXMLEventWriter(fos,enc);					
				}				
				
				if(event.getEventType() == XMLEvent.CHARACTERS) {
					Characters chrs = (Characters) event;
					xew.add(xef.createCharacters(normalize(chrs.getData())));
				//TODO normalize element names, attr values, cdata etc	
				}else{
					xew.add(event);
				}
			}			
		}finally{
			xew.flush();
			xew.close();
			if(is!=null)is.close();
			if(fos!=null)fos.close();
			StAXEventFactoryPool.getInstance().release(xef);	
			StAXInputFactoryPool.getInstance().release(xif,xifProperties);
			StAXOutputFactoryPool.getInstance().release(xof,xofProperties);			
		}
		return destination;
	}
	
	/**
	 * @return the copied result
	 * @throws IOException 
	 */
	private File copy(FilesetFile input, File destination) throws IOException {
		if(!(input.getFile().getCanonicalPath().equals(destination.getCanonicalPath()))) {
			FileUtils.copyFile((File)input, destination);
		}
		return destination;
	}

	
	/**
	 * the incoming file is a member of input Fileset
	 * determine where in outFolder it should be located
	 * return a file describing the location 
	 */
	private File getDestination(FilesetFile file) throws IOException {				
		if(mInputDir!=null) {
			if(file.getFile().getParentFile().getCanonicalPath().equals(
					mInputDir.getCanonicalPath())) {
				//file is in same dir as manifestfile
				return new File(mOutputDir, file.getName());
			}
			//file is in subdir
			URI relative = mInputDir.toURI().relativize(file.getFile().getParentFile().toURI());
			if(relative.toString().startsWith("..")) 
				throw new IOException("fileset member "+file.getName()+" " +
						"does not live in a sibling or descendant folder of manifest member");
			EFolder subdir = new EFolder(mOutputDir,relative.getPath());
			FileUtils.createDirectory(subdir);
			return new File(subdir, file.getName());			
		}
		throw new IOException("mInputDir is null");							
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.interfaces.FilesetErrorHandler#error(org.daisy.util.fileset.exception.FilesetFileException)
	 */
	public void error(FilesetFileException ffe) throws FilesetFileException {		
		this.sendMessage(ffe);
	}

	private Normalizer.Mode setNormalizationForm(String name) {
		name = name.intern();
		if (name == "NFC") {
			sendMessage(i18n("USING_FORM", "NFC"),MessageEvent.Type.INFO_FINER);
			return Normalizer.NFC; 
		}else if (name == "NFD") {
			sendMessage(i18n("USING_FORM", "NFD"),MessageEvent.Type.INFO_FINER);
			return Normalizer.NFD; 
		}else if (name == "NFKC") {
			sendMessage(i18n("USING_FORM", "NFKC"),MessageEvent.Type.INFO_FINER);
			return Normalizer.NFKC; 
		}else if (name == "NFKD") {
			sendMessage(i18n("USING_FORM", "NFKD"),MessageEvent.Type.INFO_FINER);
			return Normalizer.NFKD; 
		}
		//fallback
		this.sendMessage(i18n("ERROR", "Unrecognized normalization form: " + name), MessageEvent.Type.ERROR, MessageEvent.Cause.INPUT);
		sendMessage(i18n("USING_FORM", "NFC"),MessageEvent.Type.INFO_FINER);
		return Normalizer.NFC;
	}
	
//	private int getXmlFileCount(Fileset inputFileset) {
//		int count = 0;
//		Iterator<?> i = inputFileset.getLocalMembers().iterator();
//		while(i.hasNext()) {
//			FilesetFile f = (FilesetFile) i.next();
//			if(f instanceof XmlFile){
//				count++;
//			}
//		}
//		return count;
//	}
	
	/**
	 * Settle filenames to final state. All files that have been prettyPrinted exist as '*.*.normalized' and may be in the same dir
	 * as the originals.
	 */
	private void realize(Collection<File> tempFiles) throws IOException {
		
		for (File file : tempFiles) {						
			File original = new File(file.getParentFile(), file.getName().replace(".normalized", ""));
			
			File doubleTemp = null;
			
			if(original.exists()) {
				doubleTemp = new File(original.getParentFile(), original.getName()+".doubleTemp");				
				if(!original.renameTo(doubleTemp)){
					String message=i18n("IOERROR", original.getName());				
					throw new IOException(message);
				}
			}
			
			if(!file.renameTo(original)) {
				String message=i18n("IOERROR", file.getName());				
				throw new IOException(message);				
			}
			
			if(doubleTemp!=null) {
				if(!doubleTemp.delete()) {
					String message=i18n("IOERROR", doubleTemp.getName());				
					throw new IOException(message);		
				}
			}				
		}
	}
}