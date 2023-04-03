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
package se_tpb_zed2daisy202;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerAbortException;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.css.stylesheets.Css;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.file.TempFile;
import org.daisy.util.fileset.AudioFile;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetErrorHandler;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.ImageFile;
import org.daisy.util.fileset.OpfFile;
import org.daisy.util.fileset.Z3986DtbookFile;
import org.daisy.util.fileset.Z3986NcxFile;
import org.daisy.util.fileset.Z3986SmilFile;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.exception.FilesetFileWarningException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.text.URIUtils;
import org.daisy.util.xml.SmilClock;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.TransformerCache;
import org.daisy.util.xml.xslt.XSLTException;
import org.daisy.util.xml.xslt.stylesheets.Stylesheets;
import org.xml.sax.SAXException;

/**
 * Creates a Daisy 2.02 full text, full audio fileset from a Z3986-2005
 * full text, full audio fileset.
 * 
 * Current known assumptions and limitations:
 * <ul>
 *  <li>The first par of each zed smil file must be a heading (daisy 2.02 style)</li>
 *  <li>The smil clip of a note must immediately follow the smil clip of a noteref</li>
 *  <li>No parts of the book may reside in subfolders</li>
 *  <li>Only one DTBook document (per book) may be used</li>
 *  <li>The docTitle element in the NCX must have an audio clip</li>
 * </ul>
 * @author Linus Ericson
 */
public class Zed2Daisy202 extends Transformer implements FilesetErrorHandler {

    private static final String XSLT_FACTORY = "net.sf.saxon.TransformerFactoryImpl";
    //public static final String XSLT_FACTORY = "com.icl.saxon.TransformerFactoryImpl";
    
    // Main progress values
    private static final double FILESET_DONE = 0.01;
    private static final double SMIL_DONE = 0.65;
    private static final double XHTML_DONE = 0.68;
    private static final double NCC_DONE = 0.85;
    private static final double COPY_DONE = 0.99;
    
    private String contentXHTML = "content.html";
    private File inputDir = null;
    private File outputDir = null;
    
    public Zed2Daisy202(InputListener inListener,  Boolean isInteractive) {
        super(inListener,  isInteractive);
    }

    protected boolean execute(Map<String,String> parameters) throws TransformerRunException {
        String manifest = parameters.remove("manifest");
        String outDir = parameters.remove("outDir");
		String hrefTarget = parameters.remove("hrefTarget");
		String smilPrefix = parameters.remove("smilPrefix");
        
        inputDir = new File(manifest).getParentFile();
        outputDir = new File(outDir);
        
        // If the output is written to the same dir as input, the smil files must have a prefix
        if (inputDir.equals(outputDir) && smilPrefix.equals("")) {
            throw new TransformerRunException(i18n("NO_SMILPREFIX"));
        }

        try {
            // Build fileset            
            this.sendMessage(i18n("BUILDING_FILESET"), MessageEvent.Type.DEBUG, MessageEvent.Cause.SYSTEM);
            Fileset fileset = this.buildFileSet(manifest);   
            if (fileset.hadErrors()) {
            	String detail = null;
            	for (Iterator<Exception> iterator = fileset.getErrors().iterator(); iterator.hasNext();) {
					FilesetFileException exc = (FilesetFileException) iterator.next();
					if (exc instanceof FilesetFileWarningException) {
					    continue;
					}
					detail = exc.getMessage();	
					break;
				}
            	if (detail != null) {
            	    throw new TransformerRunException(i18n("ERROR_ABORTING",detail));
            	}
            }
            this.progress(FILESET_DONE);
            this.checkAbort();
            
            Set<FilesetFile> filesToCopy = new HashSet<FilesetFile>();
            
            // Create output directory
            outputDir = FileUtils.createDirectory(new File(outDir)); 
            
            if (smilPrefix == null) {
                smilPrefix = "";
            }
            
            // Find NCX, OPF, DTBook and files to copy
            File ncx = null;
            OpfFile opf = null;
            File dtbook = null;
            for (Iterator<FilesetFile> it = fileset.getLocalMembers().iterator(); it.hasNext(); ) {
                FilesetFile fsf = it.next();
                if (fsf instanceof Z3986SmilFile) {
                    // Do smils later
                } else if (fsf instanceof Z3986NcxFile) {
                    ncx = fsf.getFile();
                } else if (fsf instanceof OpfFile) {
                    opf = (OpfFile)fsf;
                } else if (fsf instanceof Z3986DtbookFile) {
                    dtbook = fsf.getFile();
                } else if (fsf instanceof AudioFile) {
                    filesToCopy.add(fsf);
                } else if (fsf instanceof ImageFile) {
                    filesToCopy.add(fsf);
                } else {
                	sendMessage(i18n("IGNORING_FILE", fsf.getFile().getName()), MessageEvent.Type.INFO_FINER);
                }
            }
                        
            // Create smils
            SmilClock totalElapsedTime = this.createSmils(opf, dtbook, ncx, smilPrefix);            
            this.progress(SMIL_DONE);
            this.checkAbort();
            
            // Create xhtml 
            this.sendMessage(i18n("CREATING_XHTML", contentXHTML), 
            		MessageEvent.Type.INFO_FINER, MessageEvent.Cause.SYSTEM);
            
            this.createXhtml(dtbook, opf, hrefTarget, smilPrefix);            
            this.progress(XHTML_DONE);
            this.checkAbort();

            // Create NCC
            this.sendMessage(i18n("BUILDING_NCC", "ncc.html"), 
            		MessageEvent.Type.INFO_FINER, MessageEvent.Cause.SYSTEM);
            this.createNcc(opf.getFile(), totalElapsedTime, hrefTarget, smilPrefix);            
            this.progress(NCC_DONE);
            this.checkAbort();
            
            // Copy files
            this.copyFiles(filesToCopy, fileset);            
            this.progress(COPY_DONE);
            this.checkAbort();
            
        } catch (CatalogExceptionNotRecoverable e) {
            throw new TransformerRunException(i18n("ERROR_ABORTING",e.getMessage()), e);
        } catch (XSLTException e) {
        	throw new TransformerRunException(i18n("ERROR_ABORTING",e.getMessage()), e);
        } catch (IOException e) {
        	throw new TransformerRunException(i18n("ERROR_ABORTING",e.getMessage()), e);
        } catch (XMLStreamException e) {
        	throw new TransformerRunException(i18n("ERROR_ABORTING",e.getMessage()), e);
        } catch (FilesetFatalException e) {
        	throw new TransformerRunException(i18n("ERROR_ABORTING",e.getMessage()), e);
        } catch (SAXException e) {
        	throw new TransformerRunException(i18n("ERROR_ABORTING",e.getMessage()), e);
		}
        
        return true;
    }

    /**
     * Creates the NCC.
     * @param opf the z3986 package file
     * @param totalElapsedTime the total time of the book in milliseconds
     * @param hrefTarget whether to make smil references point to text or par
     * @throws CatalogExceptionNotRecoverable
     * @throws XSLTException
     * @throws XMLStreamException
     * @throws IOException
     */
    private void createNcc(File opf, SmilClock totalElapsedTime, String hrefTarget, String smilPrefix) throws CatalogExceptionNotRecoverable, XSLTException, XMLStreamException, IOException {
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put("baseDir", outputDir.toURI());
        parameters.put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        parameters.put("hrefTarget", hrefTarget);
        parameters.put("smilPrefix", smilPrefix);
        File nccFile = new File(outputDir, "ncc.html");
        File createNcc = new File(this.getTransformerDirectory(), "ncc-create.xsl");
        File nccRemoveDupes = new File(this.getTransformerDirectory(), "ncc-remove-dupes.xsl");
        File nccClean = new File(this.getTransformerDirectory(), "ncc-clean.xsl");
                
        DOMResult dom1 = new DOMResult();
        DOMResult dom2 = new DOMResult();
        File tempFile = TempFile.create();
        
        // Step 1: Create the ncc by looking through the smil files and identifying headings in the content document
        Stylesheet.apply(opf.getAbsolutePath(), createNcc.getAbsolutePath(), dom1, XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
        
        // Step 2: Remove duplicate entries in the ncc (step 1 created two entries for a heading containing two sync points)
        Stylesheet.apply(new DOMSource(dom1.getNode()), nccRemoveDupes.getAbsolutePath(), dom2, XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
        
        // Step 3: add class="title" to first h1 and fix some metadata
        Stylesheet.apply(new DOMSource(dom2.getNode()), nccClean.getAbsolutePath(), tempFile.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
        
        // Step 4: insert ncc:totalTime and indent file
        NccFixer nccFixer = new NccFixer();
        nccFixer.fix(tempFile, nccFile, totalElapsedTime);
        
        tempFile.delete();
    }
    
    /**
     * Creates the SMIL files.
     * @param opf the z3986 package file
     * @param dtbook the dtbook document
     * @param ncx the ncx document
     * @return the total time of the book (in milliseconds)
     * @throws XMLStreamException
     * @throws IOException
     * @throws XSLTException
     * @throws TransformerAbortException
     * @throws SAXException 
     */
    private SmilClock createSmils(OpfFile opf, File dtbook, File ncx, String smilPrefix) throws XMLStreamException, IOException, XSLTException, TransformerAbortException, SAXException {

        // Extract the information the smil2smil stylesheet needs
        // from the DTBook and the OPF. This will speed up the
        // transformation significantly.
        PreCalc preCalc = new PreCalc(dtbook, ncx);            
        File preCalcFile = preCalc.getPreCalcFile();        
        
        SmilFileClockFixer smilClockFixer = new SmilFileClockFixer();
        SmilClock totalElapsedTime = new SmilClock();
        File smil2smil = new File(this.getTransformerDirectory(), "smil2smil.xsl");
        
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put("xhtml_document", contentXHTML);
        //parameters.put("dtbook_document", dtbook.toURI());
        parameters.put("baseDir", inputDir.toURI());
        //parameters.put("ncx_document", ncx.toURI());
//        parameters.put("add_title", "true");
        parameters.put("precalc_document", preCalcFile.toURI());
        
        TransformerCache cache = new TransformerCache();
        
        // For each SMIL file
        Collection<FilesetFile> spineItems = opf.getSpineItems();      
        int smilNum = spineItems.size();
        int smilCount = 0;
        for (Iterator<FilesetFile> it = spineItems.iterator(); it.hasNext(); ) {
            smilCount++;
            Z3986SmilFile smilZed = (Z3986SmilFile)it.next();
            File smil202 = new File(outputDir, smilPrefix + smilZed.getFile().getName());
            //Object[] params = {new Integer(smilNum), new Integer(smilCount), smil202.getName()};  
            //this.sendMessage(i18n("SMIL", params), MessageEvent.Type.DEBUG, MessageEvent.Cause.SYSTEM);            
            
            File temp2 = TempFile.create();
                        
            // Step 1: Flatten the Z3986 smil to a Daisy 2.02 compatible one 
            Stylesheet.apply(smilZed.getFile().getAbsolutePath(), cache.get(smil2smil.getAbsolutePath(), XSLT_FACTORY), temp2.getAbsolutePath(), parameters, CatalogEntityResolver.getInstance());
                        
            // Step 2: Update the ncc:timeInThisSmil, ncc:totalElapsedTime and the dur attribute of the main seq
            totalElapsedTime = totalElapsedTime.addTime(smilClockFixer.fix(temp2, smil202, totalElapsedTime, null));
            
            temp2.delete();
            parameters.put("add_title", "false");
            this.progress(FILESET_DONE + (SMIL_DONE-FILESET_DONE)*((double)smilCount/smilNum));
            this.checkAbort();
        }
        
        return totalElapsedTime;
    }
    
    /**
     * Creates the XHTML content document.
     * @param dtbook the DTBook document
     * @param hrefTarget whether to make smil references point to text or par
     * @throws CatalogExceptionNotRecoverable
     * @throws XSLTException
     * @throws IOException
     */
    private void createXhtml(File dtbook, OpfFile opf, String hrefTarget, String smilPrefix) throws CatalogExceptionNotRecoverable, XSLTException, IOException {
    	String cssName = "default.css";
        File xhtmlOut = new File(outputDir, contentXHTML);
        
        Iterator<FilesetFile> it = opf.getSpineItems().iterator();
        
        URI uri = (it.next()).getFile().toURI();
        uri = opf.getFile().getParentFile().toURI().relativize(uri);
        
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("filter_word", "no");
        parameters.put("baseDir", inputDir.toURI().toString());
        parameters.put("first_smil", uri.toString());
        parameters.put("split_simple_table", "true");
        parameters.put("css_path", cssName);
        parameters.put("hrefTarget", hrefTarget);
        parameters.put("smilPrefix", smilPrefix);
        
        URL url = Stylesheets.get("dtbook2xhtml.xsl");
        
        // Step 1: Convert the DTBook to XHTML
        Stylesheet.apply(dtbook.toURI().toString(), url, xhtmlOut.toURI().toString(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
        
        // Step 2: Insert a stylesheet
        FileUtils.writeInputStreamToFile(Css.get(Css.DocumentType.D202_XHTML).openStream(), new File(outputDir, cssName));
    }
        
    
    /**
     * Copy the rest of the book (audio and images).
     * @param files the set of files to copy
     * @throws IOException
     * @throws TransformerAbortException
     */
    private void copyFiles(Set<FilesetFile> files, Fileset fileset) throws IOException, TransformerAbortException {
        int fileNum = files.size();
        int fileCount = 0;
        for (Iterator<FilesetFile> it = files.iterator(); it.hasNext(); ) {
            fileCount++;
            FilesetFile fsf = it.next();
            //Object[] params = {new Integer(fileNum), new Integer(fileCount), fsf.getFile().getName()};
            //this.sendMessage(i18n("COPYING_FILE", params), MessageEvent.Type.DEBUG, MessageEvent.Cause.SYSTEM);
            URI relativeURI = fileset.getManifestMember().getRelativeURI(fsf);
            File out = new File(URIUtils.resolve(outputDir.toURI(), relativeURI));
            FileUtils.copy(fsf.getFile(), out);
            this.progress(0.85 + (0.99-0.85)*((double)fileCount/fileNum));
            this.checkAbort();
        }
    }
    
    private Fileset buildFileSet(String manifest) throws FilesetFatalException {
        return new FilesetImpl(FilenameOrFileURI.toFile(manifest).toURI(), this, false, true);
    }

	public void error(FilesetFileException ffe) throws FilesetFileException {
		this.sendMessage(ffe);	
	}
    
}
