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
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;

import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.dmfc.logging.LineFormatter;
import org.daisy.dmfc.logging.LogHandler;
import org.daisy.util.file.FileBunchCopy;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.file.TempFile;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.interfaces.Fileset;
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
    public XMLDetection(InputListener inListener, Set eventListeners,
            Boolean isInteractive) {
        super(inListener, eventListeners, isInteractive);
    }


    protected boolean execute(Map parameters) throws TransformerRunException {
        String input = (String)parameters.remove("input");
        String output = (String)parameters.remove("output");
        String doAbbrAcronymDetection = (String)parameters.remove("doAbbrAcronymDetection");
        String doSentenceDetection = (String)parameters.remove("doSentenceDetection");
        String doWordDetection = (String)parameters.remove("doWordDetection");
        String customLang = (String)parameters.remove("customLang");
        String doOverride = (String)parameters.remove("doOverride");
        String logFile = (String)parameters.remove("logFile");
        String copyReferredFiles = (String)parameters.remove("copyReferredFiles");        
        
        sendMessage(Level.FINER, i18n("USING_INPUT", input));
        sendMessage(Level.FINER, i18n("USING_OUTPUT", output));
        
        if (customLang != null) {
            if (customLang.equals("")) {
                customLang = null;
            } else {
                sendMessage(Level.FINER, i18n("USING_CUSTOMLANG", customLang));
                if (Boolean.parseBoolean(doOverride)) {
                    sendMessage(Level.FINER, i18n("OVERRIDING"));
                }
            }
        }
        
        if (logFile != null && !logFile.equals("")) {
            sendMessage(Level.FINER, i18n("USING_LOGFILE", logFile));
        }
        
        File currentInput = FilenameOrFileURI.toFile(input);
        File finalOutput = FilenameOrFileURI.toFile(output);
        
        /* Setup logger. Only add logger once. */        
        Logger logger = Logger.getLogger(this.getClass().getPackage().getName());
        Handler[] handlers = logger.getHandlers();
        
        boolean hasLogHandler = false;
        boolean hasFileHandler = false;
        for (int i = 0; i < handlers.length; ++i) {
            if (handlers[i] instanceof LogHandler) {
                hasLogHandler = true;
            } else if (handlers[i] instanceof FileHandler) {
                hasFileHandler = true;
            }
        }
        
        try {            
            if (!hasLogHandler) {
                LogHandler handler = new LogHandler();
                handler.setLevel(Level.WARNING);
                logger.addHandler(handler);
            }
            if (!hasFileHandler && logFile != null && !logFile.equals("")) {
                FileHandler fileHandler = new FileHandler(logFile);
                fileHandler.setLevel(Level.ALL);
                fileHandler.setFormatter(new LineFormatter());
                logger.addHandler(fileHandler);
            }
            
            // Abbr + Acronym
            if (Boolean.parseBoolean(doAbbrAcronymDetection)) {
                sendMessage(Level.FINER, i18n("STARTING_ABBR_ACRONYM"));
	            TempFile temp = new TempFile();
	            sendMessage(Level.FINER, "Temp abbr: " + temp.getFile());
	            URL customLangFileURL = null;
	            if (customLang != null) {
	                customLangFileURL = new File(customLang).toURL(); 
	            }
	            XMLAbbrDetector abbrDetector = new XMLAbbrDetector(currentInput, temp.getFile(), customLangFileURL, Boolean.parseBoolean(doOverride));                
	            abbrDetector.detect(null);
	            currentInput = temp.getFile();
	            sendMessage(Level.FINER, i18n("FINISHING_ABBR_ACRONYM"));
            }

            // Sentence
            if (Boolean.parseBoolean(doSentenceDetection)) {
                sendMessage(Level.FINER, i18n("STARTING_SENTENCE"));
                TempFile temp = new TempFile();
                sendMessage(Level.FINER, "Temp sent: " + temp.getFile());
                URL customLangFileURL = null;
	            if (customLang != null) {
	                customLangFileURL = new File(customLang).toURL(); 
	            }
                XMLSentenceDetector sentDetector = new XMLSentenceDetector(currentInput, temp.getFile(), customLangFileURL, Boolean.parseBoolean(doOverride));                
                sentDetector.detect(null);
                currentInput = temp.getFile();
                sendMessage(Level.FINER, i18n("FINISHING_SENTENCE"));
            }
            
            // Word
            if (Boolean.parseBoolean(doWordDetection)) {
                sendMessage(Level.FINER, i18n("STARTING_WORD"));
                TempFile temp = new TempFile();
                sendMessage(Level.FINER, "Temp word: " + temp.getFile());
                XMLWordDetector wordDetector = new XMLWordDetector(currentInput, temp.getFile());
                wordDetector.detect(null);
                currentInput = temp.getFile();
                sendMessage(Level.FINER, i18n("FINISHING_WORD"));
            }
            
            // Copy to output file
            sendMessage(Level.FINER, i18n("STARTING_COPY"));
            FileUtils.copy(currentInput, finalOutput);
            
            if (Boolean.parseBoolean(copyReferredFiles)) {
                sendMessage(Level.FINER, i18n("COPYING_REFERRED_FILES"));
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
            throw new TransformerRunException("Catalog problem", e);
        } catch (IOException e) {
            throw new TransformerRunException("IO problem", e);
        } catch (XMLStreamException e) {
            throw new TransformerRunException("StAX problem", e);
        } catch (UnsupportedDocumentTypeException e) {
            throw new TransformerRunException("Unsupported DOCTYPE", e);
        } catch (FilesetFatalException e) {
            throw new TransformerRunException("Fileset problem", e);
        }           
        
        return true;
    }

}