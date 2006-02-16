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
package se_tpb_zed2daisy202;

import java.io.File;
import java.io.IOException;
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
public class Zed2Daisy202 extends Transformer {

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
    
    public Zed2Daisy202(InputListener inListener, Set eventListeners, Boolean isInteractive) {
        super(inListener, eventListeners, isInteractive);
    }

    protected boolean execute(Map parameters) throws TransformerRunException {
        String manifest = (String)parameters.remove("manifest");
        String outDir = (String)parameters.remove("outDir");
        
        inputDir = new File(manifest).getParentFile();
        outputDir = new File(outDir);      

        try {
            // Build fileset
            this.sendMessage(Level.INFO, i18n("BUILDING_FILESET"));
            Fileset fileset = this.buildFileSet(manifest);            
            this.progress(FILESET_DONE);
            
            Set filesToCopy = new HashSet();
            
            // Create output directory
            outputDir = FileUtils.createDirectory(new File(outDir)); 
            
            // Find NCX, OPF, DTBook and files to copy
            File ncx = null;
            OpfFile opf = null;
            File dtbook = null;
            for (Iterator it = fileset.getLocalMembers().iterator(); it.hasNext(); ) {
                FilesetFile fsf = (FilesetFile)it.next();
                if (fsf instanceof Z3986SmilFile) {
                    // Do smils later
                } else if (fsf instanceof Z3986NcxFile) {
                    ncx = fsf.getFile();
                } else if (fsf instanceof OpfFile) {
                    opf = (OpfFile)fsf;
                } else if (fsf instanceof Z3986DtbookFile) {
                    dtbook = fsf.getFile();
                } else if (fsf instanceof AudioFile) {
                    filesToCopy.add(fsf.getFile());
                } else if (fsf instanceof ImageFile) {
                    filesToCopy.add(fsf.getFile());
                } else {
                    System.err.println("ignore: " + fsf.getName());
                }
            }
            
            // Create smils
            long totalElapsedTime = this.createSmils(opf, dtbook, ncx);            
            this.progress(SMIL_DONE);
            
            // Create xhtml 
            this.sendMessage(Level.INFO, i18n("CREATING_XHTML", contentXHTML));
            this.createXhtml(dtbook);            
            this.progress(XHTML_DONE);

            // Create NCC
            this.sendMessage(Level.INFO, i18n("BUILDING_NCC", "ncc.html"));
            this.createNcc(opf.getFile(), totalElapsedTime);            
            this.progress(NCC_DONE);
            
            // Copy files
            this.copyFiles(filesToCopy);            
            this.progress(COPY_DONE);
            
        } catch (FilesetException e) {            
            throw new TransformerRunException(e.getMessage(), e);
        } catch (CatalogExceptionNotRecoverable e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (XSLTException e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (IOException e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (XMLStreamException e) {
            throw new TransformerRunException(e.getMessage(), e);
        }
        
        return true;
    }

    /**
     * Creates the NCC.
     * @param opf the z3986 package file
     * @param totalElapsedTime the total time of the book in milliseconds
     * @throws CatalogExceptionNotRecoverable
     * @throws XSLTException
     * @throws XMLStreamException
     * @throws IOException
     */
    private void createNcc(File opf, long totalElapsedTime) throws CatalogExceptionNotRecoverable, XSLTException, XMLStreamException, IOException {
        Map parameters = new HashMap();
        parameters.put("baseDir", outputDir.toURI());
        parameters.put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        File nccFile = new File(outputDir, "ncc.html");
        File createNcc = new File(this.getTransformerDirectory(), "ncc-create.xsl");
        File nccRemoveDupes = new File(this.getTransformerDirectory(), "ncc-remove-dupes.xsl");
        File nccClean = new File(this.getTransformerDirectory(), "ncc-clean.xsl");
        
        File temp = TempFile.create();
        File temp2 = TempFile.create();
        File temp3 = TempFile.create();
        
        // Step 1: Create the ncc by looking through the smil files and identifying headings in the content document
        Stylesheet.apply(opf.getAbsolutePath(), createNcc.getAbsolutePath(), temp.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
        
        // Step 2: Remove duplicate entries in the ncc (step 1 created two entries for a heading containing two sync points)
        Stylesheet.apply(temp.getAbsolutePath(), nccRemoveDupes.getAbsolutePath(), temp2.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
        
        // Step 3: add class="title" to first h1 and fix some metadata
        Stylesheet.apply(temp2.getAbsolutePath(), nccClean.getAbsolutePath(), temp3.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
        
        // Step 4: insert ncc:totalTime and indent file
        NccFixer nccFixer = new NccFixer();
        nccFixer.fix(temp3, nccFile, totalElapsedTime);
        
        temp.delete();
        temp2.delete();
        temp3.delete();
    }
    
    /**
     * Creates the SMIL files.
     * @param opf the z3986 package file
     * @param dtbook the dtbook document
     * @param ncx the ncx document
     * @return the total time of the book (in milliseconds)
     * @throws XMLStreamException
     * @throws IOException
     * @throws CatalogExceptionNotRecoverable
     * @throws XSLTException
     * @throws FilesetException
     */
    private long createSmils(OpfFile opf, File dtbook, File ncx) throws XMLStreamException, IOException, CatalogExceptionNotRecoverable, XSLTException, FilesetException {
        SmilFileClockFixer smilClockFixer = new SmilFileClockFixer();
        long totalElapsedTime = 0;
        File smil2smil = new File(this.getTransformerDirectory(), "smil2smil.xsl");
        File smilAddTitle = new File(this.getTransformerDirectory(), "smilAddTitle.xsl");
        
        Map parameters = new HashMap();
        parameters.put("xhtml_document", contentXHTML);
        parameters.put("dtbook_document", dtbook.toURI());
        parameters.put("baseDir", inputDir.toURI());
        parameters.put("ncx_document", ncx.toURI());
        parameters.put("add_title", "true");
        
        // For each SMIL file
        Collection spineItems = opf.getSpineItems();      
        int smilNum = spineItems.size();
        int smilCount = 0;
        for (Iterator it = spineItems.iterator(); it.hasNext(); ) {
            smilCount++;
            Z3986SmilFile smilZed = (Z3986SmilFile)it.next();
            File smil202 = new File(outputDir, smilZed.getName());
            Object[] params = {new Integer(smilNum), new Integer(smilCount), smil202.getName()};
            this.sendMessage(Level.INFO, i18n("SMIL", params));            
            
            File temp1 = TempFile.create();
            File temp2 = TempFile.create();
            
            // Step 1: Flatten the Z3986 smil to a Daisy 2.02 compatible one 
            Stylesheet.apply(smilZed.getFile().getAbsolutePath(), smil2smil.getAbsolutePath(), temp1.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
            
            // Step 2: Add the smil clip that reads the <h1 class="title"> to the first smil
            Stylesheet.apply(temp1.getAbsolutePath(), smilAddTitle.getAbsolutePath(), temp2.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
            
            // Step 3: Update the ncc:timeInThisSmil, ncc:totalElapsedTime and the dur attribute of the main seq
            totalElapsedTime += smilClockFixer.fix(temp2, smil202, totalElapsedTime, null);
            
            temp1.delete();
            temp2.delete();
            parameters.put("add_title", "false");
            this.progress(FILESET_DONE + (SMIL_DONE-FILESET_DONE)*((double)smilCount/smilNum));
        }
        
        return totalElapsedTime;
    }
    
    /**
     * Creates the XHTML content document.
     * @param dtbook the DTBook document
     * @throws CatalogExceptionNotRecoverable
     * @throws XSLTException
     * @throws IOException
     */
    private void createXhtml(File dtbook) throws CatalogExceptionNotRecoverable, XSLTException, IOException {
        File xhtmlOut = new File(outputDir, contentXHTML);
        File xsltFile = new File(this.getTransformerDirectory(), "dtbook2xhtml.xsl");
        Map parameters = new HashMap();
        parameters.put("filter_word", "yes");
        parameters.put("baseDir", inputDir.toURI());
        
        // Step 1: Convert the DTBook to XHTML
        Stylesheet.apply(dtbook.getAbsolutePath(), xsltFile.getAbsolutePath(), xhtmlOut.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
        
        // Step 2: Insert a stylesheet
        FileUtils.copy(new File(this.getTransformerDirectory(), "default.css"), new File(outputDir, "default.css"));
    }
        
    
    /**
     * Copy the rest of the book (audio and images).
     * @param files the set of files to copy
     * @throws IOException
     */
    private void copyFiles(Set files) throws IOException {
        int fileNum = files.size();
        int fileCount = 0;
        for (Iterator it = files.iterator(); it.hasNext(); ) {
            fileCount++;
            File inFile = (File)it.next();
            Object[] params = {new Integer(fileNum), new Integer(fileCount), inFile.getName()};
            this.sendMessage(Level.INFO, i18n("COPYING_FILE", params));
            File outFile = new File(outputDir, inFile.getName());
            FileUtils.copy(inFile, outFile);
            this.progress(0.85 + (0.99-0.85)*((double)fileCount/fileNum));
        }
    }
    
    private Fileset buildFileSet(String manifest) throws FilesetException {
        return new FilesetImpl(FilenameOrFileURI.toFile(manifest).toURI(), false, true);
    }
    
}
