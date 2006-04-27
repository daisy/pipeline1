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
package uk_rnib_odf2dtbook;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.file.TempFile;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.XSLTException;

/**
 * Transforms an Open Document Format text file to DTBook.
 * @author Linus Ericson
 */
public class Odf2Dtbook extends Transformer {

    private static final int BUFFER = 2048;
    private static final String FACTORY = "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl";
    
    public Odf2Dtbook(InputListener inListener, Set eventListeners, Boolean isInteractive) {
        super(inListener, eventListeners, isInteractive);
    }

    protected boolean execute(Map parameters) throws TransformerRunException {
        String odf = (String)parameters.remove("odf");
        String dtbook = (String)parameters.remove("dtbook");
        
        File odfFile = FilenameOrFileURI.toFile(odf);
        File dtbookFile = FilenameOrFileURI.toFile(dtbook);        
        File stylesheet = new File(this.getTransformerDirectory(), "odf2daisy.xsl");
        File dtbookDir = dtbookFile.getParentFile();
        
        try {
            // Create temporary directory
            File tempDir = TempFile.createDir();
            
            // Extract needed zip contents
            ZipFile zipFile = new ZipFile(odfFile);
            File content = new File(tempDir, "content.xml");
            File meta = new File(tempDir, "meta.xml");
            this.extractFromZip(zipFile, "content.xml", content);
            this.extractFromZip(zipFile, "meta.xml", meta);
            this.extractImages(zipFile, dtbookDir);
            zipFile.close();
            
            // Apply transformation
            Stylesheet.apply(content.toString(), stylesheet.toString(), dtbookFile.toString(),
                    FACTORY, null, CatalogEntityResolver.getInstance());
            
            // Remove temporary directory
            content.delete();
            meta.delete();
            tempDir.delete();            
            
        } catch (ZipException e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (IOException e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (CatalogExceptionNotRecoverable e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (XSLTException e) {
            throw new TransformerRunException(e.getMessage(), e);
        }
        return true;
    }
    
    /**
     * Extracts a file from a zip file.
     * @param odfFile the zip file to extract from
     * @param name name of the entry in the zip
     * @param outputFile the output filename
     * @throws IOException
     */
    private void extractFromZip(ZipFile odfFile, String name, File outputFile) throws IOException {
        ZipEntry entry = odfFile.getEntry(name);
        if (entry == null) {
            throw new FileNotFoundException(name + " not found in zip file");
        }
        InputStream is = new BufferedInputStream(odfFile.getInputStream(entry));
        OutputStream os = new BufferedOutputStream(new FileOutputStream(outputFile), BUFFER);
        int count;
        byte data[] = new byte[BUFFER];
        while ((count = is.read(data, 0, BUFFER)) != -1) {
            os.write(data, 0, count);
        }
        os.flush();
        os.close();
        is.close();
    }
    
    /**
     * Extracts all embedded images from the ODF file.
     * @param odfFile
     * @param outputDir
     * @throws IOException
     */
    private void extractImages(ZipFile odfFile, File outputDir) throws IOException {
    	Enumeration enumer = odfFile.entries();
    	File pictureDir = new File(outputDir, "Pictures");
    	while (enumer.hasMoreElements()) {
    		ZipEntry entry = (ZipEntry)enumer.nextElement();
    		//System.err.println("entry" + entry.getName());
    		if (entry.getName().startsWith("Pictures/")) {
    			File picture = new File(outputDir, entry.getName());
    			FileUtils.createDirectory(pictureDir);
    			this.extractFromZip(odfFile, entry.getName(), picture);
    		}
    	}
    }
    
}
