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
import java.net.URI;
import java.util.Iterator;
import java.util.Map;

import javax.xml.stream.events.XMLEvent;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.EFolder;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.interfaces.xml.SmilFile;
import org.daisy.util.fileset.interfaces.xml.XmlFile;
import org.daisy.util.fileset.manipulation.FilesetFileManipulator;
import org.daisy.util.fileset.manipulation.FilesetManipulationException;
import org.daisy.util.fileset.manipulation.FilesetManipulator;
import org.daisy.util.fileset.manipulation.FilesetManipulatorListener;
import org.daisy.util.fileset.manipulation.manipulators.XMLEventValueConsumer;
import org.daisy.util.fileset.manipulation.manipulators.XMLEventValueExposer;
import org.daisy.util.xml.stax.ContextStack;

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
public class UCNormalizer extends Transformer implements FilesetManipulatorListener, XMLEventValueConsumer {

	private Normalizer.Mode mode = null; 
	private int xmlFileCount = 0; //for progress
	private int processedCount = 0; //for progress
	private boolean textnodesOnly = false; 
	
    public  UCNormalizer (InputListener inListener,  Boolean isInteractive) {
        super(inListener,  isInteractive);        
    }
    
	protected boolean execute(Map parameters) throws TransformerRunException {
		
		try{
			FilesetManipulator fm = new FilesetManipulator();
			//implement FilesetManipulatorListener
			fm.setListener(this);
			//set input fileset
			File f = new File((String)parameters.remove("input"));
			fm.setInputFileset(f.toURI());
			//set destination
			fm.setOutputFolder((EFolder)FileUtils.createDirectory(new EFolder((String)parameters.remove("output"))));
			//set restriction, only listen to XmlFile
			fm.setFileTypeRestriction(XmlFile.class);
			//set normalization form
			this.mode = setNormalizationForm((String)parameters.remove("normalizationForm"));			
			//set a counter for progress sake
			this.xmlFileCount = getXmlFileCount(fm.getInputFileset());
			//check user settable node restriction
			this.textnodesOnly = ((String)parameters.remove("textnodesOnly")).equals("true");
			//run...
			fm.iterate();
			//done.
			sendMessage(i18n("COMPLETED_NORM", processedCount));
		} catch (Exception e) {			
			String message = i18n("ERROR_ABORTING",e.getMessage());
			throw new TransformerRunException(message,e);
		}			
		return true;
	}
	
	/**
	 * FilesetManipulatorListener impl
	 * @throws FilesetManipulationException 
	 */
	public FilesetFileManipulator nextFile(FilesetFile file) throws FilesetManipulationException {		
		//we only get XmlFile callbacks since we set a restriction above
		try {
			//transformer generics
			this.checkAbort();
			processedCount++;
			this.progress(processedCount/xmlFileCount);
			//smilfiles are only empty elements so abort if textnodesOnly to save time
			if(this.textnodesOnly && file instanceof SmilFile) return null;			
			//else, set self to listen to exposed value, using default constructor
			XMLEventValueExposer xeve = new XMLEventValueExposer(this);
			//if textnodesOnly skip attributes etc
			if(this.textnodesOnly) xeve.setEventTypeRestriction(XMLEvent.CHARACTERS);
			//and go.
			return xeve;			
		} catch (Exception e) {
			throw new FilesetManipulationException(e.getMessage(),e);
		}			
	}

	/**
	 * FilesetManipulatorListener impl
	 */
	public void error(FilesetFileException ffe) throws FilesetFileException {		
		this.sendMessage(ffe);
	}

	/**
	 * XMLEventValueConsumer impl
	 */
	public String nextValue(String value, ContextStack context) {
		//Normalize the value and return
		try{
			return Normalizer.normalize(value,this.mode);			
		}catch (Exception e) {
			this.sendMessage(i18n("ERROR", e.getMessage()), MessageEvent.Type.ERROR,MessageEvent.Cause.SYSTEM);
			return null;
		}
	}

	private Normalizer.Mode setNormalizationForm(String name) {
		name = name.intern();
		if (name == "NFC") {
			sendMessage(i18n("USING_FORM", "NFC"));
			return Normalizer.NFC; 
		}else if (name == "NFD") {
			sendMessage(i18n("USING_FORM", "NFD"));
			return Normalizer.NFD; 
		}else if (name == "NFKC") {
			sendMessage(i18n("USING_FORM", "NFKC"));
			return Normalizer.NFKC; 
		}else if (name == "NFKD") {
			sendMessage(i18n("USING_FORM", "NFKD"));
			return Normalizer.NFKD; 
		}
		//fallback
		this.sendMessage(i18n("ERROR", "Unrecognized normalization form: " + name), MessageEvent.Type.ERROR,MessageEvent.Cause.INPUT);
		sendMessage(i18n("USING_FORM", "NFC"));
		return Normalizer.NFC;
	}
	
	private int getXmlFileCount(Fileset inputFileset) {
		int count = 0;
		Iterator i = inputFileset.getLocalMembers().iterator();
		while(i.hasNext()) {
			FilesetFile f = (FilesetFile) i.next();
			if(f instanceof XmlFile){
				count++;
			}
		}
		return count;
	}
}