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
package int_daisy_dtbMigrator.impl.z2005_d202;

import int_daisy_dtbMigrator.DtbDescriptor;
import int_daisy_dtbMigrator.DtbVersion;
import int_daisy_dtbMigrator.Migrator;
import int_daisy_dtbMigrator.MigratorException;
import int_daisy_dtbMigrator.impl.z2005_d202.read.SmilMerger;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

import org.daisy.pipeline.core.event.MessageEvent.Cause;
import org.daisy.pipeline.core.event.MessageEvent.Type;
import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.util.file.Directory;
import org.daisy.util.file.TempFile;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.TransformerFactoryConstants;
import org.daisy.util.xml.xslt.XSLTException;
import org.daisy.util.xml.xslt.stylesheets.Stylesheets;

/**
 * An implementation of DtbMigrator that supports downgrading z2005 books to Daisy 2.02
 * @author Markus Gylling, Per Sennels, Linus Ericson
 */

public class MigratorImpl implements Migrator, TransformerDelegateListener {
	private TransformerDelegateListener mTransformer = null;
	
	/*
	 * (non-Javadoc)
	 * @see int_daisy_dtbMigrator.DtbMigrator#migrate(int_daisy_dtbMigrator.DtbDescriptor, int_daisy_dtbMigrator.DtbDescriptor, java.util.Map, org.daisy.util.fileset.interfaces.Fileset, org.daisy.util.file.EFolder)
	 */
	@SuppressWarnings("unused")
	public void migrate(DtbDescriptor input, DtbDescriptor output, Map<String, String> parameters,
			Fileset inputFileset, Directory destination) throws MigratorException {		
			
		try {			
			/*
			 * Create the behemoth SMIL file from the input zed DTB.
			 */
			SmilMerger merger = new SmilMerger(this);
			TempFile behemoth = new TempFile();				
			merger.render(inputFileset, behemoth.getFile());
			merger = null;
			System.err.println("Tempfile Behemoth rendered to: " + behemoth.getFile().getAbsolutePath());
						
			//temp: create a pretty printed version for debugging
			File outFile = new File(destination,"behemoth.smil");
			Stylesheet.apply(behemoth.getFile().getAbsolutePath(), Stylesheets.get("echo.xsl"), outFile.getAbsolutePath(), TransformerFactoryConstants.SAXON8, null, CatalogEntityResolver.getInstance());			
			System.err.println("Prettyprinted behemoth rendered to: " + outFile.getAbsolutePath());
			//end temp
			

			
			
			/*
			 * Render the DTB.
			 */
			
		} catch (IOException e) {
			throw new MigratorException(e.getLocalizedMessage(),e);
		} catch (XMLStreamException e) {
			throw new MigratorException(e.getLocalizedMessage(),e);
		} catch (CatalogExceptionNotRecoverable e) {
			throw new MigratorException(e.getLocalizedMessage(),e);
		} catch (XSLTException e) {
			throw new MigratorException(e.getLocalizedMessage(),e);
		}		
	}

	/*
	 * (non-Javadoc)
	 * @see int_daisy_dtbMigrator.DtbMigrator#setDelegateListener(org.daisy.pipeline.core.transformer.TransformerDelegateListener)
	 */
	public void setListener(
			TransformerDelegateListener transformer) {
		mTransformer = transformer;		
	}

	/*
	 * (non-Javadoc)
	 * @see int_daisy_dtbMigrator.DtbMigrator#supports(int_daisy_dtbMigrator.DtbDescriptor, int_daisy_dtbMigrator.DtbDescriptor, java.util.Map)
	 */
	@SuppressWarnings("unused")
	public boolean supports(DtbDescriptor input,  Fileset inputFileset, 
				DtbDescriptor output, Map<String,String> parameters) {
		
		if(input.getVersion() == DtbVersion.Z2005 
				&& output.getVersion() == DtbVersion.D202) {
			return true;
		}		
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see int_daisy_dtbMigrator.DtbMigrator#getNiceName()
	 */
	public String getNiceName() {
		return mTransformer.delegateLocalize("Z39862005_TO_D202", null);
	}

	public boolean delegateCheckAbort() {		
		return mTransformer.delegateCheckAbort();
	}

	public String delegateLocalize(String key, Object[] params) {
		return mTransformer.delegateLocalize(key, params);
	}

	public void delegateMessage(Object delegate, String message, Type type, Cause cause, Location location) {
		mTransformer.delegateMessage(delegate, message, type, cause, location);		
	}

	public void delegateProgress(Object delegate, double progress) {
		//assuming SmilMerger and DtbRenderer split the burden in half
		if(delegate instanceof SmilMerger) {
			mTransformer.delegateProgress(this, progress/2);
		}else{
			mTransformer.delegateProgress(this, 0.5+progress/2);
		}		
	}


}
