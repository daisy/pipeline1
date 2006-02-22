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
package int_daisy_daisy2022Zed2005;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.stream.XMLStreamException;

import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.file.TempFile;
import org.daisy.util.fileset.AudioFile;
import org.daisy.util.fileset.CssFile;
import org.daisy.util.fileset.D202MasterSmilFile;
import org.daisy.util.fileset.D202NccFile;
import org.daisy.util.fileset.D202SmilFile;
import org.daisy.util.fileset.D202TextualContentFile;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetException;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.FilesetImpl;
import org.daisy.util.fileset.ImageFile;
import org.daisy.util.fileset.OpfFile;
import org.daisy.util.fileset.Z3986DtbookFile;
import org.daisy.util.fileset.Z3986NcxFile;
import org.daisy.util.fileset.Z3986SmilFile;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.XSLTException;
import org.daisy.util.fileset.SmilClock;

/**
 * Creates a Z3986-2005 DTB fileset from a Daisy 2.02 DTB fileset.
 * 
 * Current known assumptions and limitations:
 * <ul>
 *  <li></li>
 *  <li></li>
 *  <li></li>
 *  <li></li>
 *  <li></li>
 * </ul>
 * @author Brandon Nelson
 * @author Markus Gylling
 */
public class Daisy2022Zed2005 extends Transformer {

    private static final String XSLT_FACTORY = "net.sf.saxon.TransformerFactoryImpl";

    
    // Main progress values
    private static final double FILESET_DONE = 0.01;
    private static final double SMIL_DONE = 0.65;
    private static final double XHTML_DONE = 0.68;
    private static final double NCX_DONE = 0.85;
    private static final double OPF_DONE = 0.85;
    private static final double COPY_DONE = 0.99;
        
    private File inputDir = null;
    private File outputDir = null;
    
    public Daisy2022Zed2005(InputListener inListener, Set eventListeners, Boolean isInteractive) {
        super(inListener, eventListeners, isInteractive);
    }

    protected boolean execute(Map parameters) throws TransformerRunException {
        String ncc = (String)parameters.remove("ncc");
        String outDir = (String)parameters.remove("outDir");
        
        inputDir = new File(ncc).getParentFile();
        outputDir = new File(outDir);      

        try {
// Build input fileset
            this.sendMessage(Level.INFO, i18n("BUILDING_FILESET"));
            Fileset fileset = this.buildFileSet(ncc);            
            this.progress(FILESET_DONE);            
            Set filesToCopy = new HashSet();
            
// Check for properties that should make us abort before even attempting the transform 
            //abortcause: 2.02 DTB with > 1 content doc
            int xhtmlContentDocCount = 0;
            for (Iterator it = fileset.getLocalMembers().iterator(); it.hasNext(); ) {
            	FilesetFile fsf = (FilesetFile)it.next();
                if (fsf instanceof D202TextualContentFile) {              
                	xhtmlContentDocCount ++;	
                }            	
            }
            
            if (xhtmlContentDocCount > 1) {
            	//more than one content doc, throw exception
            	throw new TransformerRunException("more than one XHTML content doc in input DTB");
            }	
            
            //abortcause: fileset had errors??
            
            
// Create output directory
            outputDir = FileUtils.createDirectory(new File(outDir)); 
                                                          
// get ncc and collect variables that need to be sent as parameters to the XSLTs
            
            String nccDcIdentifier = "dc-identifier-unset";
            String nccDcTitle = "dc-title-unset";
            
            D202NccFile d202_ncc = null;
            d202_ncc = (D202NccFile) fileset.getManifestMember();
            nccDcIdentifier = d202_ncc.getDcIdentifier();
            nccDcTitle = d202_ncc.getDcTitle(); 

// determine name for output DTB filenames (has to change from *.html etc)            
            
            //smil, audio and image names are copied as-is
            String dtbookFileName = "unset.xml";
            String opfFileName = "unset.opf";
            String ncxFileName = "unset.ncx";
            
            if(nccDcIdentifier.equals("dc-identifier-unset")) {
            	//ncc identifier was not set, use content doc name for filename
                for (Iterator it = fileset.getLocalMembers().iterator(); it.hasNext(); ) {
                	FilesetFile fsf = (FilesetFile)it.next();
                    if (fsf instanceof D202TextualContentFile) {
                    	dtbookFileName = fsf.getName();
                    	break;
                    }            	
                }            	
            	dtbookFileName = dtbookFileName.substring(0,dtbookFileName.lastIndexOf(".")) + ".xml";             	
            	ncxFileName = dtbookFileName.substring(0,dtbookFileName.lastIndexOf(".")) + ".ncx";
            	opfFileName = dtbookFileName.substring(0,dtbookFileName.lastIndexOf(".")) + ".opf";
            	
            }else{
            	//ncc identifier was set, use this for filename
            	dtbookFileName = nccDcIdentifier + ".xml";             	
            	ncxFileName = nccDcIdentifier + ".ncx";
            	opfFileName = nccDcIdentifier + ".opf";
            }	
                                                            
// Iterate through Fileset and create Zed Smil from the 2.02 SMIL input files            

            //remember: the XSLTs are written context unaware, 
            //so they need to get all necessary context info as inparams.
            
            D202SmilFile d202_smil = null;
            long totalElapsedTime = 0; //in milliseconds
            
            for (Iterator it = fileset.getLocalMembers().iterator(); it.hasNext(); ) {
                FilesetFile fsf = (FilesetFile)it.next();
                if (fsf instanceof D202SmilFile) {
                	d202_smil = (D202SmilFile) fsf;
                	this.createZedSmil(d202_smil, nccDcIdentifier, nccDcTitle, 
                			new SmilClock(totalElapsedTime).toString(SmilClock.FULL), 
                			d202_smil.getCalculatedDuration().toString(SmilClock.FULL),
                			dtbookFileName);
                	//up the elapsed value for next loop iter
                	totalElapsedTime += d202_smil.getCalculatedDurationMillis();
                } 
            }//for
            this.progress(SMIL_DONE);
            //now, the totalElapsedTime variable equals DTB totaltime
            SmilClock dtbTotalTime = new SmilClock(totalElapsedTime);
            
//5. Iterate again through fileset and create ncx, dtbook, opf
            
            //remember: the XSLTs are written context unaware, 
            //so they need to get all necessary context info as inparams.
            
            for (Iterator it = fileset.getLocalMembers().iterator(); it.hasNext(); ) {
                FilesetFile fsf = (FilesetFile)it.next();
                if (fsf instanceof D202NccFile) {
                	D202NccFile nccFile = (D202NccFile) fsf;
                	//create ncx
                	this.sendMessage(Level.INFO, i18n("BUILDING_NCC", "ncc.html"));                            	
                	this.createZedNcx(nccFile,nccDcIdentifier,ncxFileName);
                    this.progress(NCX_DONE);
                    //create opf
                    this.sendMessage(Level.INFO, i18n("BUILDING_NCC", "ncc.html"));                            	
                	this.createZedOpf(nccFile,dtbTotalTime,opfFileName);
                    this.progress(OPF_DONE);

                    
                    
                } else if (fsf instanceof D202TextualContentFile) {
                    this.sendMessage(Level.INFO, i18n("CREATING_XHTML", dtbookFileName));
                	D202TextualContentFile xhtFile = (D202TextualContentFile) fsf;
                	this.createZedDtbook(xhtFile,nccDcIdentifier,nccDcTitle,"./dtbook-dmfc-default.css", dtbookFileName);   
                    this.progress(XHTML_DONE);
                } else if (fsf instanceof AudioFile) {
                    filesToCopy.add(fsf);
                } else if (fsf instanceof ImageFile) {
                    filesToCopy.add(fsf);
                } else if (fsf instanceof CssFile) { 
                	//dont add this to filesToCopy since it is an xhtml css
                } else if (fsf instanceof D202SmilFile) {
                    // smil already done                    
                } else if (fsf instanceof D202MasterSmilFile) {
                    // ignore master smil completely
                } else {
                    System.err.println("what is this? :: " + fsf.getName());
                }
            }//for
            
            
//after transforming input fileset, create add zed files that dont have a 2.02 counterpart 
            //TODO copy in a default resource
            
            //TODO copy in a default dtbook css (if dtbook is present)

//finally copy files that move over unabridged           

            this.copyFiles(filesToCopy, fileset);            
            this.progress(COPY_DONE);
            
        } catch (FilesetException e) {            
            throw new TransformerRunException(e.getMessage(), e);
        } catch (CatalogExceptionNotRecoverable e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (XSLTException e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (IOException e) {
            throw new TransformerRunException(e.getMessage(), e);
//        } catch (XMLStreamException e) {
//            throw new TransformerRunException(e.getMessage(), e);
        }
        
        return true;
    }

    private void createZedSmil(D202SmilFile smil, String uid, String title, String totalElapsedTime, String timeinThisSmil, String dtbookFileName) throws CatalogExceptionNotRecoverable, XSLTException {
      File smilOut = new File(outputDir, smil.getName());
      File xsltFile = new File(this.getTransformerDirectory(), "d202smil_Z2005smil.xsl");
      
      Map parameters = new HashMap();
      parameters.put("uid", uid);
      parameters.put("title", title);
      parameters.put("totalElapsedTime",totalElapsedTime);
      parameters.put("timeinThisSmil",timeinThisSmil);
      parameters.put("dtbookFileName",dtbookFileName);
      
      File inFile = (File)smil;      
      Stylesheet.apply(inFile.getAbsolutePath(), xsltFile.getAbsolutePath(), smilOut.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
    }
    
    private void createZedNcx(D202NccFile ncc, String uid, String ncxFileName) throws CatalogExceptionNotRecoverable, XSLTException {
        File ncxOut = new File(outputDir, ncxFileName);
        File xsltFile = new File(this.getTransformerDirectory(), "d202ncc_Z2005ncx.xsl");
        
        Map parameters = new HashMap();
        parameters.put("uid", uid);
                        
        File inFile = (File)ncc;      
        Stylesheet.apply(inFile.getAbsolutePath(), xsltFile.getAbsolutePath(), ncxOut.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
    	
    }
    
    private void createZedDtbook(D202TextualContentFile xhtml, String uid, String title, String cssUri, String dtbookFileName) throws CatalogExceptionNotRecoverable, XSLTException {
        File dtbookOut = new File(outputDir, dtbookFileName);
        File xsltFile = new File(this.getTransformerDirectory(), "d202xhtml_Z2005dtbook.xsl");
        
        Map parameters = new HashMap();
        parameters.put("uid", uid);
        parameters.put("cssUri", cssUri);
                        
        File inFile = (File)xhtml;            
        Stylesheet.apply(inFile.getAbsolutePath(), xsltFile.getAbsolutePath(), dtbookOut.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());        
    }
    
    private void createZedOpf(D202NccFile ncc, SmilClock dtbTotalTime, String opfFileName) throws CatalogExceptionNotRecoverable, XSLTException {
        File opfOut = new File(outputDir, opfFileName);
        File xsltFile = new File(this.getTransformerDirectory(), "d202ncc_Z2005opf.xsl");
        
        Map parameters = new HashMap();
        parameters.put("dtbTotalTime", dtbTotalTime.toString(SmilClock.FULL));
                                
        File inFile = (File)ncc;      
        Stylesheet.apply(inFile.getAbsolutePath(), xsltFile.getAbsolutePath(), opfOut.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());            	    	
    }
    
        
    
    /**
     * Copy the rest of the book (audio and images).
     * @param files the set of files to copy
     * @throws IOException
     */
    private void copyFiles(Set files, Fileset fileset) throws IOException {
        int fileNum = files.size();
        int fileCount = 0;
        for (Iterator it = files.iterator(); it.hasNext(); ) {
            fileCount++;
            FilesetFile fsf = (FilesetFile)it.next();
            Object[] params = {new Integer(fileNum), new Integer(fileCount), fsf.getName()};
            this.sendMessage(Level.INFO, i18n("COPYING_FILE", params));
            URI relativeURI = fileset.getRelativeURI(fsf);
            File out = new File(outputDir.toURI().resolve(relativeURI));
            FileUtils.copy(fsf.getFile(), out);
            this.progress(0.85 + (0.99-0.85)*((double)fileCount/fileNum));
        }
    }
    
    private Fileset buildFileSet(String manifest) throws FilesetException {
        return new FilesetImpl(FilenameOrFileURI.toFile(manifest).toURI(), false, true);
    }
    
}


///**
//* Creates the NCC.
//* @param opf the z3986 package file
//* @param totalElapsedTime the total time of the book in milliseconds
//* @throws CatalogExceptionNotRecoverable
//* @throws XSLTException
//* @throws XMLStreamException
//* @throws IOException
//*/
//private void createNcc(File opf, long totalElapsedTime) throws CatalogExceptionNotRecoverable, XSLTException, XMLStreamException, IOException {
// Map parameters = new HashMap();
// parameters.put("baseDir", outputDir.toURI());
// parameters.put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
// File nccFile = new File(outputDir, "ncc.html");
// File createNcc = new File(this.getTransformerDirectory(), "ncc-create.xsl");
// File nccRemoveDupes = new File(this.getTransformerDirectory(), "ncc-remove-dupes.xsl");
// File nccClean = new File(this.getTransformerDirectory(), "ncc-clean.xsl");
// 
// File temp = TempFile.create();
// File temp2 = TempFile.create();
// File temp3 = TempFile.create();
// 
// // Step 1: Create the ncc by looking through the smil files and identifying headings in the content document
// Stylesheet.apply(opf.getAbsolutePath(), createNcc.getAbsolutePath(), temp.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
// 
// // Step 2: Remove duplicate entries in the ncc (step 1 created two entries for a heading containing two sync points)
// Stylesheet.apply(temp.getAbsolutePath(), nccRemoveDupes.getAbsolutePath(), temp2.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
// 
// // Step 3: add class="title" to first h1 and fix some metadata
// Stylesheet.apply(temp2.getAbsolutePath(), nccClean.getAbsolutePath(), temp3.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
// 
// // Step 4: insert ncc:totalTime and indent file
// NccFixer nccFixer = new NccFixer();
// nccFixer.fix(temp3, nccFile, totalElapsedTime);
// 
// temp.delete();
// temp2.delete();
// temp3.delete();
//}

///**
//* Creates the SMIL files.
//* @param opf the z3986 package file
//* @param dtbook the dtbook document
//* @param ncx the ncx document
//* @return the total time of the book (in milliseconds)
//* @throws XMLStreamException
//* @throws IOException
//* @throws CatalogExceptionNotRecoverable
//* @throws XSLTException
//* @throws FilesetException
//*/
//private long createSmils(OpfFile opf, File dtbook, File ncx) throws XMLStreamException, IOException, CatalogExceptionNotRecoverable, XSLTException, FilesetException {
// SmilFileClockFixer smilClockFixer = new SmilFileClockFixer();
// long totalElapsedTime = 0;
// File smil2smil = new File(this.getTransformerDirectory(), "smil2smil.xsl");
// File smilAddTitle = new File(this.getTransformerDirectory(), "smilAddTitle.xsl");
// 
// Map parameters = new HashMap();
// parameters.put("xhtml_document", contentXHTML);
// parameters.put("dtbook_document", dtbook.toURI());
// parameters.put("baseDir", inputDir.toURI());
// parameters.put("ncx_document", ncx.toURI());
// parameters.put("add_title", "true");
// 
// // For each SMIL file
// Collection spineItems = opf.getSpineItems();      
// int smilNum = spineItems.size();
// int smilCount = 0;
// for (Iterator it = spineItems.iterator(); it.hasNext(); ) {
//     smilCount++;
//     Z3986SmilFile smilZed = (Z3986SmilFile)it.next();
//     File smil202 = new File(outputDir, smilZed.getName());
//     Object[] params = {new Integer(smilNum), new Integer(smilCount), smil202.getName()};
//     this.sendMessage(Level.INFO, i18n("SMIL", params));            
//     
//     File temp1 = TempFile.create();
//     File temp2 = TempFile.create();
//     
//     // Step 1: Flatten the Z3986 smil to a Daisy 2.02 compatible one 
//     Stylesheet.apply(smilZed.getFile().getAbsolutePath(), smil2smil.getAbsolutePath(), temp1.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
//     
//     // Step 2: Add the smil clip that reads the <h1 class="title"> to the first smil
//     Stylesheet.apply(temp1.getAbsolutePath(), smilAddTitle.getAbsolutePath(), temp2.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
//     
//     // Step 3: Update the ncc:timeInThisSmil, ncc:totalElapsedTime and the dur attribute of the main seq
//     totalElapsedTime += smilClockFixer.fix(temp2, smil202, totalElapsedTime, null);
//     
//     temp1.delete();
//     temp2.delete();
//     parameters.put("add_title", "false");
//     this.progress(FILESET_DONE + (SMIL_DONE-FILESET_DONE)*((double)smilCount/smilNum));
// }
// 
// return totalElapsedTime;
//}
//
///**
//* Creates the XHTML content document.
//* @param dtbook the DTBook document
//* @throws CatalogExceptionNotRecoverable
//* @throws XSLTException
//* @throws IOException
//*/
//private void createXhtml(File dtbook) throws CatalogExceptionNotRecoverable, XSLTException, IOException {
// File xhtmlOut = new File(outputDir, contentXHTML);
// File xsltFile = new File(this.getTransformerDirectory(), "dtbook2xhtml.xsl");
// Map parameters = new HashMap();
// parameters.put("filter_word", "yes");
// parameters.put("baseDir", inputDir.toURI());
// 
// // Step 1: Convert the DTBook to XHTML
// Stylesheet.apply(dtbook.getAbsolutePath(), xsltFile.getAbsolutePath(), xhtmlOut.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
// 
// // Step 2: Insert a stylesheet
// FileUtils.copy(new File(this.getTransformerDirectory(), "default.css"), new File(outputDir, "default.css"));
//}
