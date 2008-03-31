/*
 * DMFC - The DAISY Multi Format Converter
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
package se_tpb_xmldetection;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.FileBunchCopy;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.file.TempFile;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.util.DefaultFilesetErrorHandlerImpl;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;


/**
 * @author Linus Ericson
 */
public class XMLDetection extends Transformer {

    /**
     * @param inListener
     * @param eventListeners
     * @param isInteractive
     */
    public XMLDetection(InputListener inListener,  Boolean isInteractive) {
        super(inListener, isInteractive);
    }

    protected boolean execute(Map parameters) throws TransformerRunException {
        String input = (String)parameters.remove("input");
        String output = (String)parameters.remove("output");
        String doAbbrAcronymDetection = (String)parameters.remove("doAbbrAcronymDetection");
        String doSentenceDetection = (String)parameters.remove("doSentenceDetection");
        String doWordDetection = (String)parameters.remove("doWordDetection");
        String customLang = (String)parameters.remove("customLang");
        String doOverride = (String)parameters.remove("doOverride");
        //String logFile = (String)parameters.remove("logFile");
        String copyReferredFiles = (String)parameters.remove("copyReferredFiles");        
                
        this.sendMessage(i18n("USING_INPUT", input), MessageEvent.Type.INFO_FINER, MessageEvent.Cause.SYSTEM);
        this.sendMessage(i18n("USING_OUTPUT", output), MessageEvent.Type.INFO_FINER, MessageEvent.Cause.SYSTEM);
        
        if (customLang != null) {
            if (customLang.equals("")) {
                customLang = null;
            } else {
                this.sendMessage(i18n("USING_CUSTOMLANG", customLang), MessageEvent.Type.INFO_FINER, MessageEvent.Cause.SYSTEM);
                if (Boolean.parseBoolean(doOverride)) {
                	this.sendMessage(i18n("OVERRIDING"), MessageEvent.Type.INFO_FINER, MessageEvent.Cause.SYSTEM);
                }
            }
        }
                
        File currentInput = FilenameOrFileURI.toFile(input);
        File finalOutput = FilenameOrFileURI.toFile(output);
                
        try {            
            
            // Abbr + Acronym
            if (Boolean.parseBoolean(doAbbrAcronymDetection)) {                
                this.sendMessage(i18n("STARTING_ABBR_ACRONYM"), MessageEvent.Type.INFO_FINER, MessageEvent.Cause.SYSTEM);
	            TempFile temp = new TempFile();
	            this.sendMessage("Temp abbr: " + temp.getFile(), MessageEvent.Type.INFO_FINER);	            
	            URL customLangFileURL = null;
	            if (customLang != null) {
	                customLangFileURL = new File(customLang).toURI().toURL(); 
	            }
	            XMLAbbrDetector abbrDetector = new XMLAbbrDetector(currentInput, temp.getFile(), customLangFileURL, Boolean.parseBoolean(doOverride));                
	            abbrDetector.detect(null);
	            currentInput = temp.getFile();
	            this.sendMessage(i18n("FINISHING_ABBR_ACRONYM"), MessageEvent.Type.INFO_FINER, MessageEvent.Cause.SYSTEM);
            }

            // Sentence
            if (Boolean.parseBoolean(doSentenceDetection)) {
                this.sendMessage(i18n("STARTING_SENTENCE"), MessageEvent.Type.INFO_FINER, MessageEvent.Cause.SYSTEM);
                TempFile temp = new TempFile();
                this.sendMessage("Temp sent: " + temp.getFile(), MessageEvent.Type.INFO_FINER);                
                URL customLangFileURL = null;
	            if (customLang != null) {
	                customLangFileURL = new File(customLang).toURI().toURL(); 
	            }
                XMLSentenceDetector sentDetector = new XMLSentenceDetector(currentInput, temp.getFile(), customLangFileURL, Boolean.parseBoolean(doOverride));                
                sentDetector.detect(null);
                currentInput = temp.getFile();
                this.sendMessage(i18n("FINISHING_SENTENCE"), MessageEvent.Type.INFO_FINER, MessageEvent.Cause.SYSTEM);
            }
            
            // Word
            if (Boolean.parseBoolean(doWordDetection)) {
                this.sendMessage(i18n("STARTING_WORD"), MessageEvent.Type.INFO_FINER, MessageEvent.Cause.SYSTEM);
                TempFile temp = new TempFile();
                this.sendMessage("Temp word: " + temp.getFile(), MessageEvent.Type.DEBUG);                
                XMLWordDetector wordDetector = new XMLWordDetector(currentInput, temp.getFile());
                wordDetector.detect(null);
                currentInput = temp.getFile();
                this.sendMessage(i18n("FINISHING_WORD"), MessageEvent.Type.INFO_FINER, MessageEvent.Cause.SYSTEM);
            }
            
            // Copy to output file            
            this.sendMessage(i18n("STARTING_COPY"), MessageEvent.Type.INFO_FINER, MessageEvent.Cause.SYSTEM);
            FileUtils.copy(currentInput, finalOutput);
            
            if (Boolean.parseBoolean(copyReferredFiles)) {
                this.sendMessage(i18n("COPYING_REFERRED_FILES"), MessageEvent.Type.INFO_FINER, MessageEvent.Cause.SYSTEM);
                Collection filesToCopy = new HashSet();
	            Fileset fileset = new FilesetImpl(FilenameOrFileURI.toURI(input), new DefaultFilesetErrorHandlerImpl(), false, true);
	            filesToCopy.addAll(fileset.getLocalMembersURIs());
	            filesToCopy.remove(fileset.getManifestMember().getFile().toURI());
	            if (fileset.hadErrors()) {
	                filesToCopy.addAll(fileset.getMissingMembersURIs());
	            }
	            FileBunchCopy.copyFiles(fileset, finalOutput.getParentFile(), filesToCopy, null, true);
            }
            
        } catch (CatalogExceptionNotRecoverable e) {
            throw new TransformerRunException(i18n("ERROR_ABORTING",e.getMessage()));
        } catch (IOException e) {
        	throw new TransformerRunException(i18n("ERROR_ABORTING",e.getMessage()));
        } catch (XMLStreamException e) {
        	throw new TransformerRunException(i18n("ERROR_ABORTING",e.getMessage()));
        } catch (UnsupportedDocumentTypeException e) {
        	throw new TransformerRunException(i18n("ERROR_ABORTING",e.getMessage()));
        } catch (FilesetFatalException e) {
        	throw new TransformerRunException(i18n("ERROR_ABORTING",e.getMessage()));
        }           
        
        return true;
    }

}
