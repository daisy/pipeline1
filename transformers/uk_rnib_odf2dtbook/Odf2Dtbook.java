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

import javax.xml.stream.Location;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.Controller;
import net.sf.saxon.event.MessageEmitter;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.EFile;
import org.daisy.util.file.Directory;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.file.TempFile;
import org.daisy.util.xml.LocusTransformer;
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
    
    protected boolean execute(Map<String,String> parameters) throws TransformerRunException {
        String systemTransformerFactory = null;
    	
        EFile inputOdf = new EFile(FilenameOrFileURI.toFile(parameters.remove("odf")));
        EFile outputDtbook = new EFile(FilenameOrFileURI.toFile(parameters.remove("dtbook")));           
        File outputDir = outputDtbook.getParentFile();
        
        try {
        	systemTransformerFactory = System.getProperty("javax.xml.transform.TransformerFactory");
        	System.setProperty("javax.xml.transform.TransformerFactory",FACTORY);
        	
        	// Create temporary directory
            mTempDir = TempFile.createDir();  
            this.sendMessage(i18n("USING_TEMPDIR",mTempDir.getAbsolutePath()), MessageEvent.Type.DEBUG);
                        
            // Extract needed zip contents
            ZipFile zipFile = new ZipFile(inputOdf);
            File content = new File(mTempDir, "content.xml");
            File meta = new File(mTempDir, "meta.xml");
            File styles = new File(mTempDir, "styles.xml");
            this.extractFromZip(zipFile, "content.xml", content);
            this.extractFromZip(zipFile, "meta.xml", meta);
            this.extractFromZip(zipFile, "styles.xml", styles);
            this.extractImages(zipFile, outputDir);
            zipFile.close();
        
            //a map holding all XSLTs for convenience 
            Map<String,URL> stylesheets = new HashMap<String,URL>(); 
            stylesheets.put("odfGetStyles", this.getClass().getResource("odfGetStyles.xsl"));
            stylesheets.put("odfStructure", this.getClass().getResource("odfStructure.xsl"));
            stylesheets.put("odfNestCheck", this.getClass().getResource("odfNestCheck.xsl"));
            stylesheets.put("odf2daisy", this.getClass().getResource("odf2daisy.xsl"));
            
        	MessageEmitter me = new MessageEmitter();
        	me.setWriter(new MessageEmitterWriter(this)); // output redirection
            
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
                        
            // Step 2. Create struct file by applying odfStructure.xsl to content.xml
            File _struct = new File(mTempDir, inputOdf.getNameMinusExtension() + ".struct.xml");
            StreamSource ss2 = new StreamSource(stylesheets.get("odfStructure").openStream());
            saxon = tfac.newTransformer(ss2);
            saxon.setURIResolver(this);
            ((Controller)saxon).setMessageEmitter(me);
            saxon.setParameter("stylefile", "_styles.xml");
            saxon.transform(new StreamSource(content), new StreamResult(_struct));
            
            // Step 3. Create the report file by applying odfNestCheck.xsl to the struct file
            File _report = new File(mTempDir, inputOdf.getNameMinusExtension() + ".report.xml");
            StreamSource ss3 = new StreamSource(stylesheets.get("odfNestCheck").openStream());
            saxon = tfac.newTransformer(ss3);
            saxon.setURIResolver(this);
            ((Controller)saxon).setMessageEmitter(me);
            saxon.setParameter("stylefile", "_styles.xml");
            saxon.transform(new StreamSource(_struct), new StreamResult(_report));
            
            // Step 4. Create the dtbook file
            StreamSource ss4 = new StreamSource(stylesheets.get("odf2daisy").openStream());
            saxon = tfac.newTransformer(ss4);
            saxon.setURIResolver(this);
            ((Controller)saxon).setMessageEmitter(me);
            saxon.setParameter("stylefile", "_styles.xml");
            FileUtils.createDirectory(outputDir);
            saxon.transform(new StreamSource(content), new StreamResult(outputDtbook));            
                        
            // Remove temporary directory
            Directory eTemp = new Directory(mTempDir);
            eTemp.deleteContents();
            mTempDir.delete();
            
        } catch (Exception e) {
        	this.sendMessage(i18n("ERROR_ABORTING",e.getMessage()),MessageEvent.Type.ERROR,MessageEvent.Cause.SYSTEM);
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
    	Enumeration<?> enumer = odfFile.entries();
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

    /*
     * (non-Javadoc)
     * @see javax.xml.transform.URIResolver#resolve(java.lang.String, java.lang.String)
     */
	@SuppressWarnings("unused")
	public Source resolve(String href, String base) throws TransformerException {
		if(href.equals("content.xml")||href.equals("styles.xml")||href.equals("_styles.xml")||href.equals("meta.xml")) {			
			return new StreamSource(new File(mTempDir,href));
		}
		if (href.endsWith(".xsl")) {
			return new StreamSource(new File(this.getTransformerDirectory(), href));
		}
		if(href.length()>0){
			this.sendMessage(i18n("UNRESOLVED",href),MessageEvent.Type.DEBUG,MessageEvent.Cause.SYSTEM);
		}else{
			System.err.println("href with length 0 in Odf2Dtbook#resolve");
		}
		return null;
	}

	@SuppressWarnings("unused")
	public void error(TransformerException e) throws TransformerException {
		Location loc = LocusTransformer.newLocation(e);
		this.sendMessage(i18n("ERROR",e.getMessage()),MessageEvent.Type.ERROR,MessageEvent.Cause.INPUT,loc);	
	}

	@SuppressWarnings("unused")
	public void fatalError(TransformerException e) throws TransformerException {
		Location loc = LocusTransformer.newLocation(e);
		this.sendMessage(i18n("ERROR",e.getMessage()),MessageEvent.Type.ERROR,MessageEvent.Cause.INPUT,loc);
	}

	@SuppressWarnings("unused")
	public void warning(TransformerException e) throws TransformerException {
		Location loc = LocusTransformer.newLocation(e);
		this.sendMessage(i18n("ERROR",e.getMessage()),MessageEvent.Type.WARNING,MessageEvent.Cause.INPUT,loc);
	}
    	
	class MessageEmitterWriter extends Writer {
		Transformer mT = null;
		MessageEmitterWriter(Transformer t) {
			mT = t;
		}
		
		@SuppressWarnings("unused")
		@Override
		public void close() throws IOException {
			// TODO Auto-generated method stub
			
		}

		@SuppressWarnings("unused")
		@Override
		public void flush() throws IOException {
			// TODO Auto-generated method stub
			
		}

		@SuppressWarnings("unused")
		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {
			String s = new String(cbuf, off, len).trim();
			if(s.length()>0){
				mT.sendMessage(s,MessageEvent.Type.INFO,MessageEvent.Cause.SYSTEM,null);
			}
		}
		 
	}
}
