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
package us_rfbd_dtbookMigrator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.Location;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.EFile;
import org.daisy.util.file.Directory;
import org.daisy.util.file.FileJuggler;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetErrorHandler;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.exception.FilesetFileWarningException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.xml.LocusTransformer;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.peek.PeekResult;
import org.daisy.util.xml.peek.Peeker;
import org.daisy.util.xml.peek.PeekerPool;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.TransformerFactoryConstants;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import us_rfbd_dtbookMigrator.DtbookVersionManager.ChronologicalRelation;

/**
 * <p>A wrapper for XSLT-based DTBook single- or multistep upwards migration.</p>
 * <p>Note that the actual XSLTs reside in <code>org.daisy.util.xml.xslt.stylesheets.dtbook</code>.</p>
 * 
 * <p>To add support for a new DTBook version, perform the following steps:</p>  
 * <ol>
 * 	<li>Create the XSLT and add it to <code>org.daisy.util.xml.xslt.stylesheets.dtbook</code>.
 *  This XSLT should include logic to go from previous to current version only.</li>
 * 	<li>Register the new XSLT in <code>org.daisy.util.xml.xslt.stylesheets.catalog.xml</code></li>
 *  <li>Update the <code>us_rfbd_dtbookMigrator.DtbookVersion</code> enum.</li>
 *  <li>Update the <code>us_rfbd_dtbookMigrator.DtbookVersionManager</code> class.</li>
 * </ol>
 * @author Markus Gylling
 */
public class DtbookMigrator extends Transformer implements FilesetErrorHandler {

	public DtbookMigrator(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	@Override
	protected boolean execute(Map<String,String> parameters)throws TransformerRunException {
		try{
			
			/*
			 * Get handles to input and output.
			 */
			EFile input = new EFile(FilenameOrFileURI.toFile(parameters.remove("xml")));
			EFile output = new EFile(FilenameOrFileURI.toFile(parameters.remove("out")));
			
			/*
			 * Determine the version of the input document.
			 */			
			DtbookVersion inputVersion = getVersion(input);
			if(inputVersion==null)
				throw new TransformerRunException(i18n("COULD_NOT_DETERMINE_INPUT_VERSION"));
			
			/*
			 * Determine what version the user wants in the result.
			 * On failure, set version to latest possible.
			 */			
			DtbookVersion outputVersion = getVersion(parameters.remove("version"));
			if(outputVersion==null) {
				outputVersion = DtbookVersionManager.getChronology()
					.get(DtbookVersionManager.getChronology().size()-1);
			}
			
			/*
			 * Make sure that output version is higher than or equal to input version
			 * If not, throw an exception.
			 */
			ChronologicalRelation relation = DtbookVersionManager.getRelation(inputVersion, outputVersion);
			if(relation==ChronologicalRelation.EARLIER) 
				throw new TransformerRunException(
						i18n("DOWNGRADE_NOT_SUPPORTED", 
								inputVersion.toString(), outputVersion.toString()));
			
			/*
			 * Send a message on what is about to happen
			 */
			String[] msgs = new String[]{input.getName(), inputVersion.toString(), outputVersion.toString()};
			this.sendMessage(i18n("FROM_TO", msgs), MessageEvent.Type.INFO_FINER);
			
			/*
			 * Line up a list of XSLTs to apply to the input document. 
			 */
			List<URL> xslts = getStylesheets(inputVersion,outputVersion);
						
			/*
			 * Create a FileJuggler and loop through the defined XSLTs.
			 * We do support the case where the input version equals 
			 * the output version from the onset.
			 */
			final String FACTORY = TransformerFactoryConstants.SAXON8;
			FileJuggler juggler = new FileJuggler(input, output);
			for(URL xslt : xslts) {				
				Stylesheet.apply(
						juggler.getInput().getAbsolutePath(), 
						xslt, 
						juggler.getOutput().getAbsolutePath(), 
						FACTORY, 
						null,
						CatalogEntityResolver.getInstance());				
				juggler.swap();
			}
			
			/*
			 * Get the result to final output dir,
			 * including aux files
			 */			
			finalize(input, output, juggler);
			
		}catch (Exception e) {
			throw new TransformerRunException(e.getLocalizedMessage(),e);
		}
		return true;
	}
	
	private List<URL> getStylesheets(DtbookVersion inputVersion, DtbookVersion outputVersion) {
		List<URL> list = new LinkedList<URL>();		
		if(inputVersion==outputVersion) return list;		
		DtbookVersion current = inputVersion;
		while(true) {
			list.add(DtbookVersionManager.getMigratorStylesheet(current));
			DtbookVersion next = DtbookVersionManager.getNextVersion(current);
			if (next==null||next==outputVersion) {
				break;
			}
			current = next; 			
		}		
		return list;
	}

	/**
	 * Parse a file and determine DTBook version.
	 * If fail, throw a beefy exception.
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws TransformerRunException 
	 */
	private DtbookVersion getVersion(EFile input) throws SAXException, IOException {
		Peeker peeker = null;
		try{
			peeker = PeekerPool.getInstance().acquire();
			PeekResult result = peeker.peek(input);
			Attributes attrs = result.getRootElementAttributes();
			return getVersion(attrs.getValue("version"));
		} finally{
			PeekerPool.getInstance().release(peeker);
		}
	}

	/**
	 * Parse a string and determine what version it represents.
	 * If fail, return null.
	 */
	private DtbookVersion getVersion(String version) {
		if(version!=null){
			version=version.trim();
			if(version.equals("1.1.0")) {
				return DtbookVersion.v110;
			}else if(version.equals("2005-1")) {
				return DtbookVersion.v2005_1;
			}else if(version.equals("2005-2")) {
				return DtbookVersion.v2005_2;
			}else if(version.equals("2005-3")) {
				return DtbookVersion.v2005_3;
			}
		}
		return null;
	}

	/**
	 * Copy aux files over to dest dir if not same as input.
	 * Delete the manifest file since it will be replaced
	 * with the output from the Juggler.
	 * Close the juggler.
	 * @throws IOException If juggle close fails. 
	 */
	private void finalize(File input, File output, FileJuggler files) throws IOException  {
		if(!input.getParentFile().equals(output.getParentFile())) {
			try{
				Fileset toCopy = new FilesetImpl(input.toURI(),this,false,false);
				Directory dest = new Directory(output.getParentFile());
				dest.addFileset(toCopy, true);
				File manifest = new File(dest,input.getName());
				manifest.delete();
			}catch (Exception e) {
				this.sendMessage(i18n("AUX_COPY_ERROR",e.getMessage()), MessageEvent.Type.ERROR);
			}							
		}
		files.close();
	}

	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.interfaces.FilesetErrorHandler#error(org.daisy.util.fileset.exception.FilesetFileException)
	 */
	@SuppressWarnings("unused")
	public void error(FilesetFileException ffe) throws FilesetFileException {		
		Throwable root =ffe.getRootCause();		
		if(root==null) root = ffe.getCause();	
		
		Location loc = null;
		if(root instanceof SAXParseException) {
			loc=LocusTransformer.newLocation((SAXParseException)root);
		}
		
		if(!(ffe instanceof FilesetFileWarningException)) {
			/*
			 * Because we are using tempdirs, have to filter out all
			 * exceptions about missing referenced files.
			 */	
			if (!(root instanceof FileNotFoundException)) {		
				this.sendMessage(root.getMessage(), MessageEvent.Type.DEBUG, MessageEvent.Cause.INPUT, loc);
			}			
		}else{
			this.sendMessage(root.getMessage(), MessageEvent.Type.DEBUG, MessageEvent.Cause.INPUT, loc);
		}		
	}
}
