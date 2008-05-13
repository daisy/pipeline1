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
package int_daisy_filesetGenerator;

import int_daisy_filesetGenerator.FilesetGeneratorFactory.OutputType;
import int_daisy_filesetGenerator.impl.d202.D202TextOnlyGenerator;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.Location;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.event.MessageEvent.Cause;
import org.daisy.pipeline.core.event.MessageEvent.Type;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.Directory;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetErrorHandler;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.impl.FilesetImpl;

/**
 * Main transformer class. This acts as an entry point to the factory-based allocator of a supporting IFilesetGenerator implementation.
 * @author Markus Gylling
 */
public class FilesetGenerator extends Transformer implements FilesetErrorHandler, TransformerDelegateListener {

	public FilesetGenerator(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}
	
	@Override
	protected boolean execute(Map<String,String> parameters) throws TransformerRunException {
		try{
			
			/*
			 * Grok parameters
			 */
			File input = FilenameOrFileURI.toFile(parameters.remove("input"));			
			OutputType outputType = getFilesetType(parameters.remove("outputType"));			
			Directory destination = new Directory(FileUtils.createDirectory(new File(parameters.remove("outputDirectory"))));			
			String uid = parameters.remove("uid");
			String outputEncoding = parameters.remove("outputEncoding");
			String hrefTarget = parameters.remove("hrefTarget");
			Charset charset = null;
			try{
				charset = Charset.forName(outputEncoding);
			}catch (Exception e) {
				this.sendMessage(i18n("UNSUPPORTED_ENCODING_USING_UTF8",outputEncoding),MessageEvent.Type.WARNING,MessageEvent.Cause.SYSTEM);
				charset = Charset.forName("utf-8");
			}	
			
			/*
			 * Make sure inputdir != outputdir
			 */			
			if(input.getParentFile().getCanonicalPath().equals(destination.getCanonicalPath())) {
				throw new TransformerRunException(i18n("INPUT_OUTPUT_SAME"));
			}
			
			/*
			 * Prepare
			 */
			Fileset inputFileset = new FilesetImpl(input.toURI(),this,false,false);
			List<Fileset> filesetList = new ArrayList<Fileset>();
			filesetList.add(inputFileset);
			
			Map<String,Object> config = new HashMap<String,Object>();
			config.put("TransformerDelegateListener", this);
			if(uid!=null && uid.length()>0) config.put("uid", uid);
			config.put(D202TextOnlyGenerator.PARAM_CHARSET, charset);
			config.put(D202TextOnlyGenerator.PARAM_HREFTARGET, hrefTarget);
			
			/*
			 * Set up a fileset generator
			 */
			FilesetGeneratorFactory fac = FilesetGeneratorFactory.newInstance();
			fac.setProperty(FilesetGeneratorFactory.Property.INPUT, filesetList);
			fac.setProperty(FilesetGeneratorFactory.Property.OUTPUT, outputType);
			fac.setProperty(FilesetGeneratorFactory.Property.CONFIG, config);
			
			IFilesetGenerator generator = fac.newGenerator();	
			if(generator==null) throw new TransformerRunException(i18n("ERROR_ABORTING",
					"No fileset generator could be allocated for input and output"));
						
			/*
			 * Copy input fileset satellite files to destination
			 */
			destination.addFileset(inputFileset, true, generator);
			
			/*
			 * Execute generator
			 */
			generator.execute(destination);
			
		} catch (Exception e) {
			throw new TransformerRunException(e.getLocalizedMessage(),e);
		}		
		return true;
	}

	private OutputType getFilesetType(String type) {		
		return OutputType.valueOf(type);
	}

	public void error(FilesetFileException ffe)throws FilesetFileException {
		this.sendMessage(ffe);		
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.pipeline.core.transformer.TransformerDelegateListener#delegateCheckAbort()
	 */
	public boolean delegateCheckAbort() {
		return isAborted();
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.pipeline.core.transformer.TransformerDelegateListener#delegateLocalize(java.lang.String, java.lang.Object[])
	 */
	public String delegateLocalize(String key,Object[] params) {		
		return i18n(key,params);
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.pipeline.core.transformer.TransformerDelegateListener#delegateMessage(java.lang.Object, java.lang.String, org.daisy.pipeline.core.event.MessageEvent.Type, org.daisy.pipeline.core.event.MessageEvent.Cause, javax.xml.stream.Location)
	 */
	public void delegateMessage(@SuppressWarnings("unused")Object delegate,String message, Type type,Cause cause, Location location) {
		sendMessage(message,type,cause,location);		
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.pipeline.core.transformer.TransformerDelegateListener#delegateProgress(java.lang.Object, double)
	 */
	public void delegateProgress(@SuppressWarnings("unused")Object delegate,double progress) {
		sendMessage(progress);		
	}


}
