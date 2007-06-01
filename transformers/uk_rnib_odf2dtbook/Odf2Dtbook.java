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
import java.io.Writer;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.Controller;
import net.sf.saxon.event.MessageEmitter;

import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.file.EFile;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.file.TempFile;
import org.daisy.util.xml.xslt.TransformerFactoryConstants;


/**
 * Transforms an Open Document Format Text file to DTBook.
 * @author Dave Pawson (the XSLT)
 * @author Linus Ericson (the Transformer wrapper)
 * @author Markus Gylling (the Transformer wrapper)
 */
public class Odf2Dtbook extends Transformer  implements URIResolver, ErrorListener {
	private File mTempDir = null;
	
    private static final int BUFFER = 2048;

    private static final String FACTORY = TransformerFactoryConstants.SAXON8;
    
    public Odf2Dtbook(InputListener inListener, Boolean isInteractive) {
        super(inListener, isInteractive);        
    }
    
    protected boolean execute(Map parameters) throws TransformerRunException {
        String systemTransformerFactory = null;
    	
        EFile odfInput = new EFile(FilenameOrFileURI.toFile((String)parameters.remove("odf")));
        EFile dtbookOutput = new EFile(FilenameOrFileURI.toFile((String)parameters.remove("dtbook")));           
        File dtbookDir = dtbookOutput.getParentFile();
        
        try {
        	systemTransformerFactory = System.getProperty("javax.xml.transform.TransformerFactory");
        	System.setProperty("javax.xml.transform.TransformerFactory",FACTORY);
        	
        	// Create temporary directory
            mTempDir = TempFile.createDir();            
            System.err.println("tempDir is: " + mTempDir.getAbsolutePath());
            
            // Extract needed zip contents
            ZipFile zipFile = new ZipFile(odfInput);
            File content = new File(mTempDir, "content.xml");
            File meta = new File(mTempDir, "meta.xml");
            File styles = new File(mTempDir, "styles.xml");
            this.extractFromZip(zipFile, "content.xml", content);
            this.extractFromZip(zipFile, "meta.xml", meta);
            this.extractFromZip(zipFile, "styles.xml", styles);
            this.extractImages(zipFile, dtbookDir);
            zipFile.close();
        
            //a map holding all XSLTs for convenience 
            Map<String,URL> stylesheets = new HashMap<String,URL>(); 
            stylesheets.put("odfGetStyles", this.getClass().getResource("odfGetStyles.xsl"));
            stylesheets.put("odfHeadings", this.getClass().getResource("odfHeadings.xsl"));
            stylesheets.put("odfCleanHeadings", this.getClass().getResource("odf2.cleanHeadings.xsl"));

        	MessageEmitter me = new MessageEmitter();
        	me.setWriter(new MessageEmitterWriter()); // output redirection
            
            //get an instance of saxon and ask it to be reasonably quiet
            TransformerFactory tfac = TransformerFactory.newInstance();
            tfac.setAttribute("http://saxon.sf.net/feature/version-warning", Boolean.FALSE);
            tfac.setURIResolver(this);
            tfac.setErrorListener(this);

            //step 1: create _styles.xml
            //odfGetStyles makes inline document() calls to content.xml and styles.xml,
            //which are in the tempdir, we implement URIResolver to redirect
            StreamSource ss = new StreamSource(stylesheets.get("odfGetStyles").openStream());
            javax.xml.transform.Transformer saxon = tfac.newTransformer(ss); 
            saxon.setURIResolver(this);
            ((Controller)saxon).setMessageEmitter(me);
            File _styles = new File(mTempDir, "_styles.xml");            
            URL dummy = this.getClass().getResource("dummy.xml");
            StreamSource dummySource = new StreamSource(dummy.openStream());            
            saxon.transform(dummySource, new StreamResult(_styles));
            
            //Step 2. Create _headings.xml by applying odfHeadings.xsl to _styles.xml
            File _headings = new File(mTempDir, "_headings.xml");
            StreamSource ss2 = new StreamSource(stylesheets.get("odfHeadings").openStream());
            saxon = tfac.newTransformer(ss2);
            ((Controller)saxon).setMessageEmitter(me);
            saxon.transform(new StreamSource(_styles), new StreamResult(_headings));
            //mg: the line above is what renders an empty _headings.xml file for me.
            
            //Step 3. Remove list wrappers from heading X elements, remove declarations
            //op.xml content.xml odf2.cleanHeadings.xsl  "headingsfile=_headings.xml"            
            StreamSource ss3 = new StreamSource(stylesheets.get("odfCleanHeadings").openStream());
            ss3.setSystemId(stylesheets.get("odfCleanHeadings").toExternalForm());
            saxon = tfac.newTransformer(ss3);
            ((Controller)saxon).setMessageEmitter(me);
            saxon.setParameter("headingsfile", _headings.toURI().toASCIIString());
            File op = new File(mTempDir,"op.xml");
            saxon.transform(new StreamSource(content), new StreamResult(op));
            
            //TODO more steps remaning as per the bash script
            
            System.err.println("done");

            
//            // Remove temporary directory
//            content.delete();
//            meta.delete();
//            tempDir.delete();
            
        } catch (Exception e) {
        	//TODO message
            throw new TransformerRunException(e.getMessage(), e);
		}finally{
        	System.setProperty("javax.xml.transform.TransformerFactory",systemTransformerFactory);        	
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

    /**
     * 
     */
	public Source resolve(String href, String base) throws TransformerException {
		System.err.println("resolve: " + href);
		if(href.equals("content.xml")||href.equals("styles.xml")) {			
			return new StreamSource(new File(mTempDir,href));
		}
		return null;
	}

	public void error(TransformerException exception) throws TransformerException {
		// TODO Auto-generated method stub
		System.err.println("stop");		
	}

	public void fatalError(TransformerException exception) throws TransformerException {
		// TODO Auto-generated method stub
		System.err.println("stop");
	}

	public void warning(TransformerException exception) throws TransformerException {
		// TODO Auto-generated method stub
		System.err.println("stop");
	}
    
//  MessageEmitter me = new MessageEmitter();
//  me.setWriter(messagesWriter = new StringWriter()); // output redirection
//  Controller transformer = (Controller) factory.newTransformer(xslSource);
//  transformer.setMessageEmitter(me); // set my own message emitter to get
//  output the way i need it
	
	class MessageEmitterWriter extends Writer {

		@Override
		public void close() throws IOException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void flush() throws IOException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {
			String s = new String(cbuf);
			System.err.println( "MessageEmitterWriter" + s);
		}
		 
	}
}
