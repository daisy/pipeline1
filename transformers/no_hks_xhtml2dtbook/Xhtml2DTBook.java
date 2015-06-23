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
package no_hks_xhtml2dtbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.Directory;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.CssFile;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetErrorHandler;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.ImageFile;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.util.FilesetFileFilter;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.TransformerFactoryConstants;
import org.daisy.util.xml.xslt.stylesheets.Stylesheets;

/**
 * 
 * Main transformer class. See doc/transformers/no_hks_xhtml2dtbook for details.
 * 
 * @author Per Sennels
 */
public class Xhtml2DTBook extends Transformer implements FilesetErrorHandler, FilesetFileFilter {

	private Fileset mInputFileset = null;
	
	public Xhtml2DTBook(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	/**
	 * Run a 1-n length chain of stylesheets.
	 * <p>
	 * The <strong>last</strong> one is the static and canonical XHTML to
	 * DTBook transform.
	 * </p>
	 * <p>
	 * Any preceding ones are XHTML to XHTML, aiming at normalization. These
	 * preceding ones can be overriden/exchanged by the user using transformer
	 * inparams.
	 * </p>
	 * 
	 * <p>
	 * Note - use URLs to reference XSLTS as to support jarness.
	 * </p>
	 */
	@Override
	protected boolean execute(Map<String,String> parameters) throws TransformerRunException {
		try {
			/*
			 * TODO add any pre-canonical (xhtml-xhtml) XSLTs using inparams
			 */

			// get the canonical xslt
			// URL xslt = this.getClass().getResource("xhtml2dtbook.xsl");
			URL xsltURL = Stylesheets.get("xhtml2dtbook.xsl");

			// get input file
			File input = FilenameOrFileURI.toFile(parameters.remove("input"));

			// prep output dir
			File output = FilenameOrFileURI.toFile(parameters.remove("output"));
			FileUtils.createDirectory(output.getParentFile());

			// copy any referred files over
			Directory outputFolder = new Directory(output.getParentFile());
			mInputFileset = new FilesetImpl(input.toURI(), this, false, true);
			outputFolder.addFileset(mInputFileset, true, this);
			
			//copy user CSS if set and existing
			String userCSS = parameters.get("outputCSS");
			if(userCSS!=null && userCSS.length()>0) {
				File f = FilenameOrFileURI.toFile(userCSS);
				if(f!=null && f.exists()) {
					FileInputStream fis = new FileInputStream(f);
					outputFolder.writeToFile(f.getName(), fis);
					fis.close();					
				}else{
					this.sendMessage(i18n("FILE_NOT_FOUND", userCSS)
							, MessageEvent.Type.WARNING, MessageEvent.Cause.INPUT);					
				}
			}
			
			//fix the map
			Map<String,Object> xslParams = new HashMap<String,Object>();
			xslParams.putAll(parameters);
			// Add an optional user specified css as parameter to the xslt
			xslParams.put("cssURI", parameters.get("outputCSS"));
			// Title as specified by user
			xslParams.put("title", parameters.get("title"));
			// UID as specified by user
			xslParams.put("uid", parameters.get("uid"));
			// Avoid DTB Migration specific transformation
			xslParams.put("transformationMode","standalone");
			// apply the canonical xslt
			Stylesheet.apply(input.getAbsolutePath(), xsltURL, output
					.getAbsolutePath(), TransformerFactoryConstants.SAXON8,
					xslParams, CatalogEntityResolver.getInstance());

		} catch (Exception e) {
			String message = i18n("ERROR_ABORTING", e.getMessage());
			throw new TransformerRunException(message, e);
		}

		return true;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.daisy.util.fileset.interfaces.FilesetErrorHandler#error(org.daisy.util.fileset.exception.FilesetFileException)
	 */
	public void error(FilesetFileException ffe) throws FilesetFileException {
		this.sendMessage(ffe);
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.util.FilesetFileFilter#acceptFile(org.daisy.util.fileset.FilesetFile)
	 */
	public short acceptFile(FilesetFile file) {
		//dont copy the manifest
		try {
			if(file.getFile().getCanonicalPath().equals(
					mInputFileset.getManifestMember().getFile().getCanonicalPath())) {
				return FilesetFileFilter.REJECT;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//dont copy the input XHTML CSS, nor any images 
		//that are referenced uniquely by CSS files
		
		if(file instanceof CssFile) return FilesetFileFilter.REJECT;
		
		if(file instanceof ImageFile) {
			try{
				Iterator<FilesetFile> referers = file.getReferringLocalMembers().iterator();
				while(referers.hasNext()) {
					FilesetFile referer = referers.next();
					if(!(referer instanceof CssFile)) {
						return FilesetFileFilter.ACCEPT;
					}					
				}
				return FilesetFileFilter.REJECT;
			}catch (NullPointerException e) {
				//referers collection not populated
				e.printStackTrace();
			}				
		}		
		
		return FilesetFileFilter.ACCEPT;
	}
}
