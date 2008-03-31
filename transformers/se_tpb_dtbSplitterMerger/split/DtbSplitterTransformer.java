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
package se_tpb_dtbSplitterMerger.split;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.fileset.D202NccFile;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.Z3986OpfFile;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.util.DefaultFilesetErrorHandlerImpl;

import se_tpb_dtbSplitterMerger.DtbSplitterMergerConstants;
import se_tpb_dtbSplitterMerger.DtbTransformationReporter;
import se_tpb_dtbSplitterMerger.DtbVolume;
import se_tpb_dtbSplitterMerger.TransformationAbortedByUserException;
import se_tpb_dtbSplitterMerger.XmlParsingException;

/**
 * @author Piotr Kiernicki
 *
 */
public class DtbSplitterTransformer extends Transformer implements DtbTransformationReporter{

	 private long bookSizeInBytes;
	 private long copiedFilesSize;
	 private double progress;
	 private long copiedFilesSizeInterval;
	 
	/**
	 * @param inListener
	 * @param eventListeners
	 * @param isInteractive
	 */
	public DtbSplitterTransformer(InputListener inListener, Set eventListeners,
			Boolean isInteractive) {
		super(inListener, eventListeners, isInteractive);
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.core.transformer.Transformer#execute(java.util.Map)
	 */
	protected boolean execute(Map parameters) throws TransformerRunException {
		//obligatory params
		String inparamFilePath = (String)parameters.remove("inputFilePath");
        String inparamOutDirPath = (String)parameters.remove("outDirPath");
        String inparamVolumeSizeInMB = (String)parameters.remove("volumeSizeInMB");
//      optional params
        String inparamUserPrompt = (String)parameters.remove("userPrompt");
        String inparamKeepInput = (String)parameters.remove("keepInput");
        String inparamMaxSplitLevel = (String)parameters.remove("maxSplitLevel");
        String inparamPromptFilesManifestPath = (String)parameters.remove("promptFilesManifestPath");
        String inparamAlwaysIdSubdir = (String)parameters.remove("alwaysIdSubdir");
		
        //check input file path
        File inputFile = new File(inparamFilePath);
        if(!(inputFile.exists() && inputFile.canRead())){
            throw new TransformerRunException(super.i18n("INVALID_INPUT_PARAMETER", inparamFilePath));
        }
        //check prompt files manifest path
        File promptManifest = new File(this.getTransformerDirectory(), inparamPromptFilesManifestPath);
        if(!(promptManifest.exists() && promptManifest.isFile() && promptManifest.canRead())){
            throw new TransformerRunException(super.i18n("INVALID_INPUT_PARAMETER",inparamPromptFilesManifestPath));
        }
        // check out dir path
        File outDir = new File(inparamOutDirPath);
        if(!(outDir.exists() && outDir.isDirectory())){
            outDir.mkdir();
        }
        try {
            long maxVolumeSizeInBytes = Integer.parseInt(inparamVolumeSizeInMB)*DtbSplitterMergerConstants.volumeSizeMeasureUnit;
            
            //generate file set
            Fileset fs = this.getFileSet(inparamFilePath);
            
            //copy the book as is if its size is exceeded by volume size
            this.bookSizeInBytes = fs.getByteSize();
    		if(this.bookSizeInBytes <= maxVolumeSizeInBytes){
                //copy the book to the outDir and quit
                super.sendMessage(Level.INFO,i18n("BOOK_SMALLER_THAN_VOL_SIZE",inparamVolumeSizeInMB+" MB"));
                super.sendMessage(Level.INFO,i18n("COPYING_BOOK",outDir.getAbsolutePath()));
                
                String identifier = "book";
                if(FilesetType.DAISY_202.toString().equals(fs.getFilesetType().toString())) { 
                	D202NccFile ncc = (D202NccFile)fs.getManifestMember();
                	identifier = ncc.getDcIdentifier();
                } else if(FilesetType.Z3986.toString().equals(fs.getFilesetType().toString())) { 
                	Z3986OpfFile opf = (Z3986OpfFile)fs.getManifestMember();
                	identifier = opf.getUID();
                }
                if (Boolean.parseBoolean(inparamAlwaysIdSubdir)) {
                	outDir = new File(outDir, identifier);
                }
                DtbVolume outVolume = new DtbVolume(outDir,this);
                Collection inputFileset = new ArrayList();
                Collection filesetFiles = fs.getLocalMembers();
                Iterator i = filesetFiles.iterator();
                while(i.hasNext()){
                    File f = ((FilesetFile)i.next()).getFile();
                    inputFileset.add(f);
                }
                try {
                    outVolume.copyFilesIntoVolume(inputFileset,Boolean.parseBoolean(inparamUserPrompt),Boolean.parseBoolean(inparamKeepInput),inputFile.getParentFile().getAbsolutePath(),true);
                } catch (TransformationAbortedByUserException e) {
                    super.sendMessage(Level.INFO, super.i18n("SPLITTER_ABORTED_BY_USER"));
                    return true;
                } catch (IOException e) {
                    throw new TransformerRunException(e.getLocalizedMessage(),e);
                }
                return true;
    		}
    	
    		//split the book
            FilesetType fsType = fs.getFilesetType();

			DtbSplitter splitter = null;
			if(FilesetType.DAISY_202.toString().equals(fsType.toString())){
				String dirPrefix = "";
				if (Boolean.parseBoolean(inparamAlwaysIdSubdir)) {
                	dirPrefix = ((D202NccFile)fs.getManifestMember()).getDcIdentifier() + "_";
                }
			    splitter = new DtbSplitter202(fs, outDir, promptManifest, maxVolumeSizeInBytes, this, dirPrefix);
			}else if(FilesetType.Z3986.toString().equals(fsType.toString())){
				String dirPrefix = "";
				if (Boolean.parseBoolean(inparamAlwaysIdSubdir)) {
                	dirPrefix = ((Z3986OpfFile)fs.getManifestMember()).getUID() + "_";
                }
			    splitter = new DtbSplitter3(fs, outDir, promptManifest, maxVolumeSizeInBytes, this, dirPrefix);
			}
            if(splitter!=null){
                //set optional params
    			splitter.setKeepInputDtb(Boolean.parseBoolean(inparamKeepInput));
    			splitter.setMaxSplitLevel(Integer.parseInt(inparamMaxSplitLevel));
    			splitter.setUserPromptOn(Boolean.parseBoolean(inparamUserPrompt));
    			//split
    			splitter.executeSplitting();
            }else{
                throw new TransformerRunException(super.i18n("UNSUPPORTED_FILESET_TYPE",fsType.toString()));
            }
		}catch (XmlParsingException e) {
			String filePath = e.getMessage();
			throw new TransformerRunException(super.i18n("XML_PARSING_ERROR",filePath));
		} catch (TransformationAbortedByUserException e) {
            super.sendMessage(Level.INFO, super.i18n("SPLITTER_ABORTED_BY_USER"));
			return true;
		} catch (MaxVolumeSizeExceededException e) {
            throw new TransformerRunException(super.i18n("MAX_VOLUME_SIZE_EXCEEDED",inparamVolumeSizeInMB));
		} catch (Exception e) {
            throw new TransformerRunException(e.getLocalizedMessage(),e);
        }

		return true;
	}
    
	private Fileset getFileSet(String inparamFilePath) throws TransformerRunException {
        Fileset fs = null;
        boolean throwExceptionOnError = true;
        boolean dtdValidate = true;
        try {
            fs = (Fileset)new FilesetImpl(new File(inparamFilePath).toURI(),new DefaultFilesetErrorHandlerImpl(throwExceptionOnError),dtdValidate);
        } catch (FilesetFatalException e) {
            throw new TransformerRunException(super.i18n("FILESET_COULD_NOT_BE_BUILT",inparamFilePath));
        }           
        
        return fs;
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
        if(this.copiedFilesSize <= this.bookSizeInBytes)
            this.progress = (double)this.copiedFilesSize/this.bookSizeInBytes;
    	
    	this.copiedFilesSizeInterval = this.copiedFilesSizeInterval + size;
    	double progressInterval = (double)this.copiedFilesSizeInterval/this.bookSizeInBytes;
    	if(progressInterval > 0.01){
    		//System.err.println(progress);
    		super.progress(this.progress);
    		this.copiedFilesSizeInterval = 0;
    	}
    }

}
