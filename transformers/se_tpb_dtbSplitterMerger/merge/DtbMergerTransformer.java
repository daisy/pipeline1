/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
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

package se_tpb_dtbSplitterMerger.merge;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.xml.transform.TransformerException;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.Directory;
import org.daisy.util.fileset.util.FilesetRegex;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se_tpb_dtbSplitterMerger.DtbParsingInitializer;
import se_tpb_dtbSplitterMerger.DtbTransformationReporter;
import se_tpb_dtbSplitterMerger.TransformationAbortedByUserException;
import se_tpb_dtbSplitterMerger.XmlParsingException;


/**
 * @author Piotr Kiernicki
 *
 */
@SuppressWarnings("deprecation")
public class DtbMergerTransformer extends Transformer implements DtbTransformationReporter{

	 private long bookSize;
	 private long copiedFilesSize;
	 private double progress;
	 private long copiedFilesSizeInterval;
	 
	
	/**
	 * @param inListener
	 * @param isInteractive
	 */
	public DtbMergerTransformer(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.core.transformer.Transformer#execute(java.util.Map)
	 */
	
	protected boolean execute(Map<String,String> parameters) throws TransformerRunException {
		String inparamBookPath = parameters.remove("multiVolumeDTBPath");
        String inparamOutDirPath = parameters.remove("outDirPath");
        String inparamUserPrompt = parameters.remove("userPrompt");
        String inparamKeepInput= parameters.remove("keepInput");
        String inparamKeepRedundant = parameters.remove("keepRedundant");

		List<File> inputFilesList = this.getInputFilesList(inparamBookPath);

        File outDir = new File(inparamOutDirPath);
        if(!(outDir.exists() && outDir.isDirectory())){
            outDir.mkdir();
        }
        
        this.bookSize = this.calculateBookSize(inparamBookPath);
		try {
            int daisyVersion = this.retrieveDaisyVersion(inputFilesList.get(0));
            DtbMerger merger = null;
            switch(daisyVersion){
                case 2: {
                    merger = new DtbMerger202(inputFilesList, outDir, this);
                    break;
                }
                case 3:{
                    merger = new DtbMerger3(inputFilesList, outDir, this);
                    break;
                }
            }
            merger.setUserPromptOn(Boolean.parseBoolean(inparamUserPrompt));
            merger.setKeepInputDtb(Boolean.parseBoolean(inparamKeepInput));
            merger.setKeepRedundantFiles(Boolean.parseBoolean(inparamKeepRedundant));
            
            merger.executeMerging();
        } catch (TransformationAbortedByUserException e) {
            super.sendMessage(Level.INFO, super.i18n("MERGER_ABORTED_BY_USER"));
            return true;
        } catch (XmlParsingException e) {
            throw new TransformerRunException(super.i18n("XML_PARSING_ERROR",e.getMessage()));
        } catch (Exception e) {
            throw new TransformerRunException(e.getLocalizedMessage(),e);
        }
		return true;
	}
    
    private long calculateBookSize(String inparamBookPath) throws TransformerRunException {
		long size = 0;
		Directory bookDir = null;
        try {
            bookDir = new Directory(inparamBookPath);
        } catch (IOException e) {
            throw new TransformerRunException(super.i18n("WRONG_DIR_PATH", inparamBookPath));
        }
        
        Collection<File> files = bookDir.getFiles(true);
        Iterator<File> i = files.iterator();
        while(i.hasNext()){
            File f = i.next();
            size = size + f.length();
        }
        //System.err.println("book size: "+size);
		return size;
	}


	private int retrieveDaisyVersion(File inputFile) throws XmlParsingException, TransformerRunException{
        int version = 0;
        
        DtbParsingInitializer parseInit = new DtbParsingInitializer(null);
        Document inputFileDoc = parseInit.parseDocWithDOM(inputFile);
        
        //check if it is Daisy 2*
        NodeList metaElementList = inputFileDoc.getElementsByTagName("meta");
        if(metaElementList!=null){
            for(int i=0; i<metaElementList.getLength(); i++){
                Element meta = (Element)metaElementList.item(i);
                
                if( meta.getAttribute("name").equalsIgnoreCase("dc:format") 
                    && meta.getAttribute("content").toLowerCase().startsWith("daisy 2")){
                    //QUESTION should it allow all Daisy 2.* here??
                    version = 2;
                }
            }
        }
        
        if(version==0){
            //check if it is Daisy 3
            NodeList list = inputFileDoc.getElementsByTagName("dc:Format");
            if(list!=null && list.getLength()>0){
                Element dcFormat = (Element)list.item(0);
                Node dcFormatText = dcFormat.getChildNodes().item(0);
                if(dcFormatText.getNodeValue().startsWith("ANSI/NISO Z39.86")){
                    version = 3;             
                }
            }            
        }

        if(version==0){
            throw new TransformerRunException(super.i18n("DC_FORMAT_NOT_FOUND",inputFile.getAbsolutePath()));
        }
        
        return version;
    }

	/**
     * 
	 * @param inparamBookPath
	 * @return a collection with NCC or OPF files
	 * @throws TransformerException
	 */
	private List<File> getInputFilesList(String inparamBookPath) throws TransformerRunException {
		List<File> inputList = new ArrayList<File>();
        FilesetRegex rgx = FilesetRegex.getInstance();
        
		File bookDir = new File(inparamBookPath);
		if(bookDir.exists()&&bookDir.isDirectory()){
			File volDirs[] = bookDir.listFiles();
			for(int i=0; i< volDirs.length; i++){
				
				if(volDirs[i].isDirectory()){
					File volDir = volDirs[i];
					boolean inputFileFound = false;
					File volFiles[] = volDir.listFiles();
					for(int j=0; j<volFiles.length; j++){
						File f = volFiles[j];
        				if(rgx.matches(rgx.FILE_OPF,f.getName())
                                ||rgx.matches(rgx.FILE_NCC,f.getName())){
							inputList.add(f);
							inputFileFound = true;
						}
					}
					if(!inputFileFound){
						throw new TransformerRunException(super.i18n("INPUT_FILE_NOT_FOUND", volDir.getAbsolutePath()));
					}
				}
			}
		}else{
			throw new TransformerRunException(super.i18n("WRONG_DIR_PATH", inparamBookPath));
		}
		
		return inputList;
	}
    public void sendTransformationMessage(Level l, String msgKey) {
        super.sendMessage(l, super.i18n(msgKey));
        
    }
    public void sendTransformationMessage(Level l, String msgKey, Object param) {
        super.sendMessage(l, super.i18n(msgKey, param));
        
    }
    public void sendTransformationMessage(Level l, String msgKey, Object param1, Object param2) {
        super.sendMessage(l, super.i18n(msgKey, param1, param2));
        
    }
    
//    public String getTransformationUserInput(Level l, String msgId, String value){
//        return super.getUserInput(l,super.i18n(msgId),value);        
//    }
  /*
   * mg 20070327: use new event api
   */
  public String getTransformationUserInput(Level l, String msgId, String value){
	  return super.getUserInput(super.i18n(msgId),value).getReply();        
  }
    
    public void reportCopiedFile(long size){
        this.copiedFilesSize = this.copiedFilesSize + size;
        if(this.copiedFilesSize <= this.bookSize)
            this.progress = (double)this.copiedFilesSize/this.bookSize;
        
        this.copiedFilesSizeInterval = this.copiedFilesSizeInterval + size;
        double progressInterval = (double)this.copiedFilesSizeInterval/this.bookSize;
        if(progressInterval > 0.01){
            //System.err.println(progress);
            super.progress(this.progress);
            this.copiedFilesSizeInterval = 0;
        }
    }
}
