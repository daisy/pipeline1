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
package se_tpb_nccNcxOnly;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerAbortException;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.EFolder;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.D202MasterSmilFile;
import org.daisy.util.fileset.D202NccFile;
import org.daisy.util.fileset.D202SmilFile;
import org.daisy.util.fileset.D202TextualContentFile;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetErrorHandler;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.ImageFile;
import org.daisy.util.fileset.ManifestFile;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.exception.FilesetFileErrorException;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.exception.FilesetFileFatalErrorException;
import org.daisy.util.fileset.exception.FilesetFileWarningException;
import org.daisy.util.fileset.impl.FilesetFileFactory;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.util.ManifestFinder;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.stax.BookmarkedXMLEventReader;
import org.daisy.util.xml.stax.StaxEntityResolver;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.XSLTException;

/**
 * Create a NCC/NCX only version of a fileset. The textual content document
 * is removed from the fileset.
 * 
 * FIXME add support for z3986 filesets
 *  
 * @author Linus Ericson 
 */
public class NccNcxOnly extends Transformer implements FilesetErrorHandler {

	private static final String XSLT_FACTORY = "net.sf.saxon.TransformerFactoryImpl";
	
	// Main progress values
    private static final double FILESET_DONE = 0.15;
    private static final double NCCURI_DONE = 0.17;
    private static final double SMIL_DONE = 0.35;
    private static final double NCC_DONE = 0.37;
    private static final double COPY_DONE = 0.99;
    
    private StAXInputFactoryPool mInputFactoryPool = null;
    
    private int total = 1;
    private int count = 0;
	
    /**
     * Constructor.
     * @param inListener
     * @param isInteractive
     */
	public NccNcxOnly(InputListener inListener,  Boolean isInteractive) {
		super(inListener, isInteractive);	
		mInputFactoryPool = StAXInputFactoryPool.getInstance();        
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.pipeline.core.transformer.Transformer#execute(java.util.Map)
	 */
	protected boolean execute(Map parameters) throws TransformerRunException {
		String input = (String)parameters.remove("manifest");
        String outDir = (String)parameters.remove("outDir");

        File outputBaseDir = new File(outDir);
        
        try {
        	File manifestFile = FilenameOrFileURI.toFile(input);
        	if (manifestFile.isDirectory()) {
        		EFolder inputBaseDir = new EFolder(manifestFile);
        		Collection inputFiles = ManifestFinder.getManifests(true, inputBaseDir);
        		Collection manifests = new ArrayList();
        		for (Iterator i = inputFiles.iterator(); i.hasNext();) {
    				FilesetFile manifest = FilesetFileFactory.newInstance().newFilesetFile((File)i.next());    				
    				//if this is a file we should work on given acceptedTypes inparam
    				if("D202NccFileImpl".equals((manifest.getClass().getSimpleName()))) {
    					manifests.add(manifest);
    				}
    			}
        		
        		total = manifests.size();
        		for (Iterator it = manifests.iterator(); it.hasNext(); ) {   
        			FilesetFile manifest = (FilesetFile)it.next();
        			File outputDir;
        			Fileset inputFileset = new FilesetImpl(manifest.getFile().toURI(), this, false, false);	
					if(!inputBaseDir.getCanonicalPath().equals(inputFileset.getManifestMember().getParentFolder().getCanonicalPath())){
						URI relative = inputBaseDir.toURI().relativize(inputFileset.getManifestMember().getFile().toURI());
						File hypo = new File(outputBaseDir, relative.getPath());
						outputDir = FileUtils.createDirectory(hypo.getParentFile());
					}else{
						outputDir = outputBaseDir;
					}
					this.sendMessage(Level.INFO, i18n("OUTPUT_DIR", outputDir));
					
					this.nccNcxOnly(manifest.getFile().getAbsolutePath(), outputDir);					
					count++;
        		}
        		
        	} else {        	
        		this.nccNcxOnly(input, outputBaseDir);
        	}
        } catch (FilesetFatalException e) {
        	throw new TransformerRunException(i18n("ERROR_ABORTING",e.getMessage()), e);
        } catch (IOException e) {
        	throw new TransformerRunException(i18n("ERROR_ABORTING",e.getMessage()), e);
		} catch (CatalogExceptionNotRecoverable e) {
			throw new TransformerRunException(i18n("ERROR_ABORTING",e.getMessage()), e);
		} catch (XMLStreamException e) {
			throw new TransformerRunException(i18n("ERROR_ABORTING",e.getMessage()), e);
		} catch (URISyntaxException e) {
			throw new TransformerRunException(i18n("ERROR_ABORTING",e.getMessage()), e);
		} catch (XSLTException e) {
			throw new TransformerRunException(i18n("ERROR_ABORTING",e.getMessage()), e);
		}
        
		return true;
	}
	
	private void nccNcxOnly(String manifest, File outputDir) throws FilesetFatalException, TransformerRunException, IOException, CatalogExceptionNotRecoverable, URISyntaxException, XMLStreamException, XSLTException {
		// Build a fileset
        this.sendMessage(Level.INFO, i18n("BUILDING_FILESET"));
        Fileset fileset = this.buildFileSet(manifest);   
        if (fileset.hadErrors()) {
        	throw new TransformerRunException(i18n("FILESET_HAD_ERRORS"));
        }
        D202NccFile nccFile = (D202NccFile)fileset.getManifestMember();
        this.progress((double)count/total + FILESET_DONE/total);
        this.checkAbort();
        
        // Create output directory
        outputDir = FileUtils.createDirectory(outputDir); 
        
        // Collect smil URIs from the NCC            
        NccIdUriList idUri = NccIdUriList.parseNcc(nccFile.getFile());
        this.progress((double)count/total + NCCURI_DONE/total);
        this.checkAbort();
        
        // Loop through smil files and change links
        Collection spineItems = nccFile.getSpineItems();
        int i = 0;
        for (Iterator it = spineItems.iterator(); it.hasNext(); ) {
        	D202SmilFile smilFile = (D202SmilFile)it.next();
        	i++;
        	this.updateSmil(smilFile, outputDir, idUri);
        	this.progress((double)count/total + (NCCURI_DONE + (SMIL_DONE-NCCURI_DONE)*((double)i/spineItems.size()))/total);	        	
            this.checkAbort();
        }
        this.progress((double)count/total + SMIL_DONE/total);
        this.checkAbort();
        
        // Oops, there are unmatched NCC items. Not good.
        if (!nccFile.hasMultiVolumeIndicators() && idUri.canAdvance()) {
        	throw new TransformerRunException(i18n("NCC_ITEMS_LEFT", idUri.getCurrentUriToNcc()));
        }
        
        // Update the NCC meta data (ncc:files, ncc:kByteSize, ncc:multimediaType)
        File inputFile = fileset.getManifestMember().getFile();
        File outputFile = new File(outputDir, "ncc.html");
        File sheet = new File(this.getTransformerDirectory(), "ncc-meta.xsl");	        
        Stylesheet.apply(inputFile.getAbsolutePath(), sheet.getAbsolutePath(), outputFile.getAbsolutePath(), XSLT_FACTORY, null, CatalogEntityResolver.getInstance());
        this.progress((double)count/total + NCC_DONE/total);
        this.checkAbort();
        
        // Copy other fileset members (mp3s)	        
        this.copyFiles(nccFile, fileset, outputDir);
        this.progress((double)count/total + COPY_DONE/total);
        this.checkAbort();
	}
	
	/**
	 * Update a single Daisy 2.02 SMIL file.
	 * @param smilFile the smil file to update
	 * @param outDir the output directory
	 * @param idUriList
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 * @throws CatalogExceptionNotRecoverable 
	 */
	private void updateSmil(D202SmilFile smilFile, File outDir, NccIdUriList idUriList) throws FileNotFoundException, XMLStreamException, CatalogExceptionNotRecoverable {
		Map properties = null;
		XMLInputFactory xif = null;
		try{
			properties = mInputFactoryPool.getDefaultPropertyMap(false);			
			xif = mInputFactoryPool.acquire(properties);
			xif.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
			XMLEventReader reader = xif.createXMLEventReader(new FileInputStream(smilFile.getFile()));
			BookmarkedXMLEventReader bookmarked = new BookmarkedXMLEventReader(reader);
			File outFile = new File(outDir, smilFile.getName());
			OutputStream os = new FileOutputStream(outFile);
			SmilUpdater updater = new SmilUpdater(bookmarked, os, idUriList, smilFile.getName());
			updater.filter();
		}finally{
			mInputFactoryPool.release(xif, properties);
		}
	}
	
	/**
	 * Copy the remaining fileset files.
	 * @param manifest
	 * @param fileset
	 * @param outputDir
	 * @throws IOException
	 * @throws TransformerAbortException
	 */
	private void copyFiles(ManifestFile manifest, Fileset fileset, File outputDir) throws IOException, TransformerAbortException {
		Collection toCopy = new ArrayList();
        long totalSize = 0;
        long currentSize = 0;
        for (Iterator it = fileset.getLocalMembers().iterator(); it.hasNext(); ) {
        	FilesetFile fsf = (FilesetFile)it.next();
        	if (fsf instanceof D202TextualContentFile ||
        		fsf instanceof D202NccFile ||
        		fsf instanceof D202SmilFile ||
        		fsf instanceof D202MasterSmilFile ||
        		fsf instanceof ImageFile) {
        		// ignore
        	} else {
        		toCopy.add(fsf);
        		totalSize += fsf.getFile().length();	        		
        	}
        }
        for (Iterator it = toCopy.iterator(); it.hasNext(); ) {
        	FilesetFile fsf = (FilesetFile)it.next();
        	currentSize += fsf.getFile().length();
        	URI relative = manifest.getRelativeURI(fsf);
    		File out = new File(outputDir.toURI().resolve(relative));
    		FileUtils.copy(fsf.getFile(), out);
    		this.progress((double)count/total + (NCC_DONE + (COPY_DONE-NCC_DONE)*((double)currentSize/totalSize))/total);
            this.checkAbort();
        }
	}
	
	/**
	 * Builds a fileset.
	 * @param manifest
	 * @return
	 * @throws FilesetFatalException
	 */
	private Fileset buildFileSet(String manifest) throws FilesetFatalException {
        return new FilesetImpl(FilenameOrFileURI.toFile(manifest).toURI(), this, false, true);
    }

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.interfaces.FilesetErrorHandler#error(org.daisy.util.fileset.exception.FilesetFileException)
	 */
	public void error(FilesetFileException ffe) throws FilesetFileException {		
		this.sendMessage(ffe);		
	}

}
