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
package int_daisy_xukCreator;

import java.io.IOException;
import java.util.Map;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.Directory;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetErrorHandler;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.impl.FilesetImpl;

/**
 * Main Transformer class.
 * @author Markus Gylling, Julien Quint
 */
public class XukCreator extends Transformer implements FilesetErrorHandler {

	public XukCreator(InputListener inListener,Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean execute(Map parameters) throws TransformerRunException {
		
		try {
			
			/*
			 * Get handles to input and output.
			 */				
			Fileset inputFileset = new FilesetImpl(FilenameOrFileURI.toFile((String)parameters.remove("input")).toURI(),this,false,false);
			Directory destination = new Directory(FileUtils.createDirectory(FilenameOrFileURI.toFile((String)parameters.remove("destination"))));		
					
			/*
			 * Instantiate a factory and see if it can produce a compatible filter.
			 */ 
			XukFilterFactory factory = XukFilterFactory.newInstance();
			XukFilter converter = factory.newFilter(inputFileset, parameters);
					
			/*
			 * Execute.
			 */
			if(converter!=null) {
				converter.createXuk(inputFileset, parameters, destination);
			}else{
				throw new TransformerRunException(i18n("NO_IMPL_AVAIL", inputFileset.getFilesetType().toNiceNameString()));
			}
			
			/*
			 * Copy remaining members to destination.
			 */
			destination.addFileset(inputFileset, true, converter.getCopyFilter());
			
		} catch (IOException e) {
			throw new TransformerRunException(e.getLocalizedMessage(),e);
		} catch (FilesetFatalException e) {
			throw new TransformerRunException(e.getLocalizedMessage(),e);
		} catch (XukFilterException e) {
			throw new TransformerRunException(e.getLocalizedMessage(),e);
		}
		return true;
	}

	public void error(FilesetFileException ffe) throws FilesetFileException {
		this.sendMessage(ffe);		
	}

}
