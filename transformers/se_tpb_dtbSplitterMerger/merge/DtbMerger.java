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
import java.util.Iterator;
import java.util.List;

import org.daisy.util.fileset.exception.FilesetFatalException;

import se_tpb_dtbSplitterMerger.DtbTransformationReporter;
import se_tpb_dtbSplitterMerger.TransformationAbortedByUserException;
import se_tpb_dtbSplitterMerger.XmlParsingException;

/**
 * <p>An abstract class defining a public interface for merging Daisy DTB file sets.</p>
 * <p>It also implements some basic functionality that can be used or overridden by its subclasses.</p> 
 * 
 * @author Piotr Kiernicki 
 */
public abstract class DtbMerger {

	//obligatory values
	private List<File> inputFiles = new ArrayList<File>();
    private File outputDir = null;
    
	//optional values
	private boolean userPromptOn = false;
	private boolean keepInputDtb = true;
	private boolean keepRedundantFiles = true;
	//end of input values
    
    private DtbTransformationReporter reportGenerator = null;
    
    public DtbMerger(List<File> inFiles, File outDir, DtbTransformationReporter r){
        this.inputFiles = inFiles;
        this.outputDir = outDir;
        this.reportGenerator = r;
    }
    
	static final String REDUNDANT_FILES_DIR_NAME = "redundant";
	
	/**
	 * 
	 * @throws IOException 
	 * @throws FilesetFatalException 
	 */
	public abstract void executeMerging() throws TransformationAbortedByUserException, IOException, XmlParsingException, FilesetFatalException;

	protected void removeInputVolumesFiles(){
		//TODO implement handling of volume subfolders (if it is worth bothering)
		for(Iterator<File> input=this.inputFiles.iterator(); input.hasNext();){
			File inputDir = (input.next()).getParentFile();
			inputDir.deleteOnExit();
			File[] files = inputDir.listFiles();
			for(int i=0; i<files.length; i++){
				files[i].deleteOnExit();	
			}
		}
	}

	protected DtbTransformationReporter getReportGenarator() {
		return this.reportGenerator;
	}
	
	protected File getOutputDir() {
		return this.outputDir;
	}
	
	protected boolean isKeepInputDtb() {
		return this.keepInputDtb;
	}
	
	protected List<File> getInputFiles(){
		return this.inputFiles;
	}
	protected boolean isKeepRedundantFiles() {
		return keepRedundantFiles;
	}

    protected boolean isUserPromptOn() {
        return userPromptOn;
    }

    public void setKeepInputDtb(boolean keepInputDtb) {
        this.keepInputDtb = keepInputDtb;
    }

    public void setKeepRedundantFiles(boolean keepRedundantFiles) {
        this.keepRedundantFiles = keepRedundantFiles;
    }

    public void setUserPromptOn(boolean userPromptOn) {
        this.userPromptOn = userPromptOn;
    }

}
