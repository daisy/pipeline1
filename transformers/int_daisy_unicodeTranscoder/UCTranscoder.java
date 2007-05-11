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

package int_daisy_unicodeTranscoder;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.stream.events.XMLEvent;

import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.file.EFolder;
import org.daisy.util.file.FileUtils;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.interfaces.xml.SmilFile;
import org.daisy.util.fileset.interfaces.xml.XmlFile;
import org.daisy.util.fileset.manipulation.FilesetFileManipulator;
import org.daisy.util.fileset.manipulation.FilesetManipulationException;
import org.daisy.util.fileset.manipulation.FilesetManipulator;
import org.daisy.util.fileset.manipulation.FilesetManipulatorListener;
import org.daisy.util.fileset.manipulation.manipulators.XMLEventFeeder;
import org.daisy.util.fileset.manipulation.manipulators.XMLEventValueConsumer;
import org.daisy.util.fileset.manipulation.manipulators.XMLEventValueExposer;
import org.daisy.util.i18n.CharUtils;
import org.daisy.util.i18n.UCharReplacer;
import org.daisy.util.xml.stax.ContextStack;

/**
 * Performs character set transcoding on all XML documents in a fileset, roundtripping 
 * through a Unicode representation. Can optionally replace characters in the XML file 
 * with substitution strings.
 * @author Markus Gylling
 */
public class UCTranscoder extends Transformer implements FilesetManipulatorListener, XMLEventValueConsumer {
	
	private int mXmlFileCount = 0; 								//for progress
	private int mProcessedCount = 0; 							//for progress	
	private UCharReplacer mUCharReplacer = null; 				//if null at access, dont perform ucharacter replacement
	private boolean mPerformUCharSubstitution = false; 			//whether to perform UCharReplacement 
	private boolean mSubstituteInAttributeValues = false; 		//perform UCharReplacement on textnodes only					
	private Charset mOutputEncoding = null;  					//if null at access, maintain input file encoding
		
    public  UCTranscoder (InputListener inListener, Set eventListeners, Boolean isInteractive) {
        super(inListener, eventListeners, isInteractive);        
    }
	
	protected boolean execute(Map parameters) throws TransformerRunException {
		
		try{
			//TODO set system properties; StAX Writer
			
			String param;
			
			FilesetManipulator fm = new FilesetManipulator();
			
			//implement FilesetManipulatorListener
			fm.setListener(this);
			
			//set input fileset
			fm.setInputFileset(new File((String)parameters.remove("input")).toURI());
			
			//set destination
			fm.setOutputFolder((EFolder)FileUtils.createDirectory(new EFolder((String)parameters.remove("output"))));
			
			//set restriction, only listen to XmlFile
			fm.setFileTypeRestriction(XmlFile.class);
			
			//determine what charset encoding to use in output 
			param = ((String)parameters.remove("outputEncoding"));
			if(param!=null) mOutputEncoding = Charset.forName(param);	
		
			//check user settable node restriction
			this.mSubstituteInAttributeValues = ((String)parameters.remove("substituteInAttributeValues")).equals("true");
			
			//configure the UCharReplacer, if used		
			mPerformUCharSubstitution = ((String)parameters.remove("performCharacterSubstitution")).equals("true");
			if(mPerformUCharSubstitution) configureUCR(parameters);

			//set a counter for progress sake
			this.mXmlFileCount = getXmlFileCount(fm.getInputFileset());
			
			//run...
			fm.iterate();
			
			//done.
			this.sendMessage(Level.FINE, "Completed transcoding of " + mProcessedCount + " files.");
			
			//print any transliteration info and warnings
			if(mUCharReplacer!=null) {
				//number of successful table hits
				if(!mUCharReplacer.getTranslationTables().isEmpty()) {
					this.sendMessage(Level.INFO, "Performed " 
							+ mUCharReplacer.getTranslationTableHitCount() 
							+ " character substitions using loaded tables");
				}
				
				//warnings (table failures)
				Integer codePoint;
				String value;
				Map map = mUCharReplacer.getTranslationTableFailures();
				for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
					codePoint = (Integer) iter.next();					
					value = (String)map.get(codePoint);
					StringBuilder sb = new StringBuilder(127);
					sb.append("No user provided replacement text found for ");
					sb.append(CharUtils.unicodeHexEscape(codePoint.intValue()));
					sb.append(" [");
					sb.append(String.copyValueOf(Character.toChars(codePoint.intValue())));
					sb.append(']');
				    if(value!=null) {
				    	sb.append(". Using fallback '").append(value).append("' instead.");
				    }else{
				    	sb.append(". Input character forwarded to output.");
				    }						
				    this.sendMessage(Level.WARNING, sb.toString());
				}
							
			}
			
		} catch (Exception e) {
			this.sendMessage(Level.SEVERE, e.getMessage());
			throw new TransformerRunException(e.getMessage(),e);
		} finally {
			//TODO reset system properties: StAX Writer
		}
			
		return true;
	}

	/**
	 * Configure the UCharReplacer object.
	 */
	private void configureUCR(Map parameters) throws IllegalCharsetNameException, UnsupportedCharsetException, UnsupportedOperationException {		
		String param = ((String)parameters.remove("substitutionTables"));
		if(param!=null){
			mUCharReplacer = new UCharReplacer();
			String[] tables = param.split(File.pathSeparator);
			for (int i = 0; i < tables.length; i++) {
				File t = new File(tables[i].trim());
				if(t.exists()) {
					try{
						mUCharReplacer.addSubstitutionTable(t.toURI().toURL());
					}catch (Exception e) {
						this.sendMessage(Level.WARNING,"Translation table " + t.getPath() + " exception: " + e.getMessage());
					}	
				}else{
					this.sendMessage(Level.WARNING,"Translation table " + t.getPath() + " could not be found");
				}				
			} //for				
		} //if param != null
		
		param = (String)parameters.remove("fallbackToUCD");		
		mUCharReplacer.setFallbackState(mUCharReplacer.FALLBACK_USE_UCD_NAMES, param.equals("true"));
		
		param = (String)parameters.remove("fallbackToLatinTransliteration");		
		mUCharReplacer.setFallbackState(mUCharReplacer.FALLBACK_TRANSLITERATE_ANY_TO_LATIN, param.equals("true"));
		
		param = (String)parameters.remove("fallbackToNonSpacingMarkRemovalTransliteration");		
		mUCharReplacer.setFallbackState(mUCharReplacer.FALLBACK_TRANSLITERATE_REMOVE_NONSPACING_MARKS, param.equals("true"));
		
		param = (String)parameters.remove("excludeFromSubstitution");
		if(param!=null) {			
			Charset chrs = Charset.forName(param);
			mUCharReplacer.setExclusionRepertoire(chrs);			
		}
				
		//if we havent got an ok configuration of UCharReplacer
		if((mUCharReplacer.getTranslationTables().isEmpty()) 
				&& (!mUCharReplacer.getFallbackState(mUCharReplacer.FALLBACK_USE_UCD_NAMES)
						&&!mUCharReplacer.getFallbackState(mUCharReplacer.FALLBACK_TRANSLITERATE_ANY_TO_LATIN)
						&&!mUCharReplacer.getFallbackState(mUCharReplacer.FALLBACK_TRANSLITERATE_REMOVE_NONSPACING_MARKS)
						)) {
			this.sendMessage(Level.WARNING,"No translation tables loaded, and no fallbacks enabled");
			mUCharReplacer=null;
		}

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
			mProcessedCount++;
			this.progress(mProcessedCount/mXmlFileCount);
														
			//determine manipulation type and return
			FilesetFileManipulator ffm;			
			//smilfiles are only empty elements so no replace if not replaceOnAttributeValues to save time
			if(mUCharReplacer!=null &&(!(!this.mSubstituteInAttributeValues && file instanceof SmilFile))) { 
				//return a value exposer, use the outcharset constructor
				XMLEventValueExposer xeve = new XMLEventValueExposer(this,true,mOutputEncoding);
				if(!this.mSubstituteInAttributeValues) xeve.setEventTypeRestriction(XMLEvent.CHARACTERS);				
				ffm = xeve;
			}else{
				//return a feeder, use the outcharset constructor  
				ffm = new XMLEventFeeder(mOutputEncoding);				
			}			
			return ffm;
						
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
		try{						
			return mUCharReplacer.replace(value).toString();			
		}catch (Exception e) {
			this.sendMessage(Level.WARNING,e.getMessage());
			return null;
		}
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