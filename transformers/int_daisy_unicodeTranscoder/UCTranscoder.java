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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
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
import org.daisy.util.fileset.exception.FilesetFileFatalErrorException;
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
import org.daisy.util.i18n.CharsetDetector;
import org.daisy.util.i18n.UCharReplacer;
import org.daisy.util.xml.stax.ContextStack;

/**
 * Performs character set transcoding on all XML documents in a fileset, roundtripping 
 * through a Unicode representation. Can optionally substitute characters in the XML file 
 * with replacement strings.
 * @author Markus Gylling
 */
public class UCTranscoder extends Transformer implements FilesetManipulatorListener, XMLEventValueConsumer {
	
	private int xmlFileCount = 0; //for progress
	private int processedCount = 0; //for progress	
	private UCharReplacer ucr = null; //if null at access, dont perform ucharacter replacement
	private boolean replaceOnAttributeValues = false; //perform UCharReplacement on textnodes only					
	private Charset outputEncoding = null;  //if null at access, maintain input file encoding
		
    public  UCTranscoder (InputListener inListener, Set eventListeners, Boolean isInteractive) {
        super(inListener, eventListeners, isInteractive);        
    }
	
	protected boolean execute(Map parameters) throws TransformerRunException {
		
		try{
			String param;
			
			FilesetManipulator fm = new FilesetManipulator();
			//implement FilesetManipulatorListener
			fm.setListener(this);
			//set input fileset
			fm.setInputFileset(new File((String)parameters.remove("inXML")).toURI());
			//set destination
			fm.setOutputFolder((EFolder)FileUtils.createDirectory(new EFolder((String)parameters.remove("outDir"))));
			//set restriction, only listen to XmlFile
			fm.setFileTypeRestriction(XmlFile.class);
			//determine what charset encoding to use in output 
			param = ((String)parameters.remove("outputEncoding"));
			if(param!=null) outputEncoding = Charset.forName(param);				 									
			//configure the UCharReplacer			
			configureUCR(parameters);		
			//check user settable node restriction
			this.replaceOnAttributeValues = ((String)parameters.remove("replaceOnAttributeValues")).equals("true");
			//set a counter for progress sake
			this.xmlFileCount = getXmlFileCount(fm.getInputFileset());
			//run...
			fm.iterate();
			//done.
			this.sendMessage(Level.FINE, "Completed transcoding of " + processedCount + " files.");
			
		} catch (Exception e) {
			this.sendMessage(Level.SEVERE, e.getMessage());
			throw new TransformerRunException(e.getMessage(),e);
		}
			
		return true;
	}

	private void configureUCR(Map parameters) throws IOException, MalformedURLException {
		
		String param = ((String)parameters.remove("translationTable"));
		if(param!=null){
			ucr = new UCharReplacer();
			String[] tables = param.split(",");
			for (int i = 0; i < tables.length; i++) {
				File t = new File(tables[i].trim());
				if(t.exists()) {
					try{
						ucr.addTranslationTable(t.toURL(),getEncoding(t));
					}catch (Exception e) {
						this.sendMessage(Level.WARNING,"Translation table " + t.getPath() + " exception: " + e.getMessage());
					}	
				}else{
					this.sendMessage(Level.WARNING,"Translation table " + t.getPath() + " could not be found");
				}				
			}	
			param = (String) parameters.remove("fallbackToUCD");
			ucr.setFallbackToUCD(param.equals("true"));		
			
			//if we havent got an ok configuration of ucr
			if(!ucr.hasUserTables() && !ucr.getFallbackToUCD()) {
				this.sendMessage(Level.WARNING,"No translation tables loaded");
				ucr=null;
			}
			
		} //if != null
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
									
			//smilfiles are only empty elements so abort if not replaceOnAttributeValues to save time
			if(!this.replaceOnAttributeValues && file instanceof SmilFile) return null;	
			
			//determine manipulation type and return
			FilesetFileManipulator ffm;			
			if(ucr!=null) { 
				//return a value exposer, use the outcharset constructor
				XMLEventValueExposer xeve = new XMLEventValueExposer(this,true,this.outputEncoding);
				if(!this.replaceOnAttributeValues) xeve.setEventTypeRestriction(XMLEvent.CHARACTERS);				
				ffm = xeve;
			}else{
				//return a feeder, use the outcharset constructor  
				ffm = new XMLEventFeeder(this.outputEncoding);				
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
		if(ffe instanceof FilesetFileFatalErrorException && !(ffe.getCause() instanceof FileNotFoundException)) {			
			this.sendMessage(Level.SEVERE,ffe.getCause() + " in " + ffe.getOrigin());
		}else{
			this.sendMessage(Level.WARNING,ffe.getCause() + " in " + ffe.getOrigin());
		}		
	}

	/**
	 * XMLEventValueConsumer impl
	 */
	public String nextValue(String value, ContextStack context) {		
		try{						
			return ucr.toReplacementString(value);			
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
	
	private String getEncoding(File translationTable) throws MalformedURLException, IOException {
		 CharsetDetector det = new CharsetDetector();
		 String charset = det.detect(translationTable.toURL());
		 if(null==charset) {
		 	charset = det.getProbableCharsetUsingLocale();
		 }
		 return charset;
	}
}