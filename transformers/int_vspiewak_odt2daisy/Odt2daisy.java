/*
 * Vincent Spiewak for DAISY Pipeline (C) 2005-2009 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package int_vspiewak_odt2daisy;

import java.util.Map;

import net.sf.saxon.TransformerFactoryImpl;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.EFile;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.xml.xslt.TransformerFactoryConstants;


/**
 * Transforms an OpenDocument Text file to DAISY 3.0
 * @author Vincent Spiewak
 */
public class Odt2daisy extends Transformer {
	//private File mTempDir = null;
	
    private static final String FACTORY = TransformerFactoryConstants.SAXON8;
    
    public Odt2daisy(InputListener inListener, Boolean isInteractive) {
        super(inListener, isInteractive);        
    }
    
    protected boolean execute(Map<String,String> parameters) throws TransformerRunException {
        String systemTransformerFactory = null;
    	
        EFile inputOdt = new EFile(FilenameOrFileURI.toFile(parameters.remove("odt")));
        EFile outputDtbook = new EFile(FilenameOrFileURI.toFile(parameters.remove("dtbook")));     
        String uidDtbook = parameters.remove("uid");
        String titleDtbook = parameters.remove("title");
        String creatorDtbook = parameters.remove("creator");
        String publisherDtbook = parameters.remove("publisher");
        String producerDtbook = parameters.remove("producer");
        Boolean isAltDtbook = new Boolean(parameters.remove("alt"));
        Boolean isCssDtbook = new Boolean(parameters.remove("css"));
        Boolean isPageDtbook = new Boolean(parameters.remove("page"));
        
        //File outputDir = outputDtbook.getParentFile();
        
        try {
        	systemTransformerFactory = System.getProperty("javax.xml.transform.TransformerFactory");
        	System.setProperty("javax.xml.transform.TransformerFactory",FACTORY);
        	
        	// Create temporary directory
            //mTempDir = TempFile.createDir();  
            //this.sendMessage(i18n("USING_TEMPDIR",mTempDir.getAbsolutePath()), MessageEvent.Type.DEBUG);
      
            com.versusoft.packages.ooo.odt2daisy.Odt2Daisy odt2daisy = 
            	new com.versusoft.packages.ooo.odt2daisy.Odt2Daisy(inputOdt.getAbsolutePath());
            
            odt2daisy.init();

            if (!odt2daisy.isUsingHeadings()) {
            	String no_heading_message = "You SHOULD use Headings Styles to structure your documents.\n Will export in a unique level tag";
                sendMessage(no_heading_message, MessageEvent.Type.WARNING, MessageEvent.Cause.INPUT);  
            }

            if (uidDtbook != null && uidDtbook.length() > 0) {
                //System.out.println("arg uid:"+cmd.getOptionValue("u"));
                odt2daisy.setUidParam(uidDtbook);
            }
	        
            if (titleDtbook != null && titleDtbook.length() > 0) {
                //System.out.println("arg title:"+cmd.getOptionValue("t"));
                odt2daisy.setTitleParam(titleDtbook);
            }
            
            if (creatorDtbook != null && creatorDtbook.length() > 0) {
                //System.out.println("arg creator:"+cmd.getOptionValue("c"));
                odt2daisy.setCreatorParam(creatorDtbook);
            }

            if (publisherDtbook != null && publisherDtbook.length() > 0) {
                //System.out.println("arg publisher:"+cmd.getOptionValue("p"));
                odt2daisy.setPublisherParam(publisherDtbook);
            }

            if (producerDtbook != null && producerDtbook.length() > 0) {
                //System.out.println("arg producer:"+cmd.getOptionValue("pr"));
                odt2daisy.setProducerParam(producerDtbook);
            }

			if (isAltDtbook) {
                //System.out.println("arg alt:"+cmd.getOptionValue("alt"));
                odt2daisy.setUseAlternateLevelParam(true);
            }

            if (isCssDtbook) {
                odt2daisy.setWriteCSSParam(true);
            }

            if (isPageDtbook) {
                odt2daisy.paginationProcessing();
            }
            
            odt2daisy.correctionProcessing();

            if (odt2daisy.isEmptyDocument()) {
            	
            	TransformerRunException tre = new TransformerRunException("Blank document");
            	sendMessage(tre.getMessage(), MessageEvent.Type.ERROR, MessageEvent.Cause.INPUT);
            	throw new TransformerRunException(tre.getMessage(), tre);
            }
            
            System.out.println("Main Language detected: " + odt2daisy.getLangParam());
            
            odt2daisy.convertAsDTBook(outputDtbook.getAbsolutePath(), com.versusoft.packages.ooo.odt2daisy.Configuration.DEFAULT_IMAGE_DIR);
            
            // Remove temporary directory
            //Directory eTemp = new Directory(mTempDir);
            //eTemp.deleteContents();
            //mTempDir.delete();
            
        } catch (Exception e) {
        	this.sendMessage(i18n("ERROR_ABORTING",e.getMessage()),MessageEvent.Type.ERROR,MessageEvent.Cause.SYSTEM);
            throw new TransformerRunException(e.getMessage(), e);
		}finally{
        	System.setProperty("javax.xml.transform.TransformerFactory",systemTransformerFactory);        	
        }
        return true;
    }
    
}
