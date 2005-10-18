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
package se_tpb_dtbErrorScout;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.dtb.validation.errorscout.DtbErrorScoutException;
import org.daisy.util.dtb.validation.errorscout.DtbErrorScoutImpl;
import org.daisy.util.dtb.validation.errorscout.DtbErrorScoutingLevel;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.FilesetType;
import org.xml.sax.SAXParseException;

/**
 * Transformer wrapper for the DtbErrorScout in the util library.
 * @author Linus Ericson
 * @see org.daisy.util.dtb.validation.errorscout.DtbErrorScout
 */
public class DtbErrorScout extends Transformer {
    
    /**
     * @param inListener
     * @param eventListeners
     * @param isInteractive
     */
    public DtbErrorScout(InputListener inListener, Set eventListeners,
            Boolean isInteractive) {
        super(inListener, eventListeners, isInteractive);        
    }
    
    protected boolean execute(Map parameters) throws TransformerRunException {
        String manifest = (String)parameters.remove("manifest");
        String scoutLevel = (String)parameters.remove("scoutLevel");
        String filesetType = (String)parameters.remove("filesetType");
        
        sendMessage(Level.FINER, i18n("USING_MANIFEST", manifest));
        sendMessage(Level.FINER, i18n("USING_SCOUT_LEVEL", scoutLevel));
        sendMessage(Level.FINER, i18n("USING_FILESET_TYPE", filesetType));
        
        try {
            org.daisy.util.dtb.validation.errorscout.DtbErrorScout errorScout = new DtbErrorScoutImpl(FilesetType.parse(filesetType), DtbErrorScoutingLevel.parse(scoutLevel));
            long startTime = System.currentTimeMillis();
            if (errorScout.scout(FilenameOrFileURI.toFile(manifest).toURI())) {             
                Iterator it = errorScout.getErrorsIterator();                           
                while (it.hasNext()) {
                    Exception e = (Exception)it.next();
                    sendMessage(Level.WARNING, e.getMessage());
                    //System.err.print(e.toString());
                    if(e instanceof SAXParseException) {
                        SAXParseException se = (SAXParseException) e;
                        sendMessage(Level.INFO, i18n("AT_LINE", new Integer(se.getLineNumber())));
                        sendMessage(Level.INFO, i18n("IN_ENTITY", se.getSystemId()));                        
                    }                    
                }
                return false;
            }
            long endTime = System.currentTimeMillis();
            sendMessage(Level.INFO, i18n("NO_ERRORS_FOUND", new Long(endTime - startTime)));                
        } catch (DtbErrorScoutException e) {
            throw new TransformerRunException(e.getMessage(), e);
        }
        return true;
    }
    
    
}
