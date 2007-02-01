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

package se_tpb_dtbSplitterMerger;
/*
 * 
 */

import java.util.logging.Level;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * When <code>org.daisy.dtbsm.util.DtbErrorHandler</code> receives an error event it sends a message to the <code>org.daisy.dtbsm.util.DtbReportGenerator</code>. 
 * If the <code>org.daisy.dtbsm.util.DtbReportGenerator</code> parameter in the consrtuctor is <code>null</code>, the errors are not reported.  
 * 
 * @see DtbTransformationReporter
 * 
 * @author Piotr Kiernicki
 */
public class DtbErrorHandler implements ErrorHandler {
    private static final int PARSING_WARNING = 0;
    private static final int PARSING_ERROR = 1; 
    private static final int PARSING_FATAL_ERROR = 2;
	
	private String parsedFilePath = null;
	private DtbTransformationReporter reportGenerator = null;
	
	public DtbErrorHandler(String f, DtbTransformationReporter reportGen){
		this.parsedFilePath = f;
		this.reportGenerator = reportGen;
	}
	
	public void error(SAXParseException e){
		this.sendReportMessage(DtbErrorHandler.PARSING_ERROR, e);
	}
	
	public void fatalError(SAXParseException e){
		this.sendReportMessage(DtbErrorHandler.PARSING_FATAL_ERROR, e);
	}
	
	public void warning(SAXParseException e){
		this.sendReportMessage(DtbErrorHandler.PARSING_WARNING, e);
	}

    private void sendReportMessage(int errorType, SAXParseException e){
        if(this.reportGenerator!=null){
            switch(errorType){
                case DtbErrorHandler.PARSING_WARNING:{
                    this.reportGenerator.sendTransformationMessage(Level.WARNING,"XML_PARSING_ERROR",this.parsedFilePath, e.getMessage());
                }case DtbErrorHandler.PARSING_ERROR:{
                    this.reportGenerator.sendTransformationMessage(Level.WARNING,"XML_PARSING_ERROR",this.parsedFilePath, e.getMessage());
                }case DtbErrorHandler.PARSING_FATAL_ERROR:{
                    this.reportGenerator.sendTransformationMessage(Level.SEVERE,"XML_PARSING_ERROR",this.parsedFilePath, e.getMessage());
                }
            }
            
        } 

    }
//	private void sendReportMessage(String errorType, SAXParseException e){
//		
//		DtbErrorMessage errorMsg = new DtbErrorMessage(errorType, e);
//		errorMsg.setParsingErrorSourcePath(this.parsedFilePath);
//		if(this.reportGenerator!=null){
//			this.reportGenerator.addErrorMessage(errorMsg);	
//		} 
//
//	}
}
