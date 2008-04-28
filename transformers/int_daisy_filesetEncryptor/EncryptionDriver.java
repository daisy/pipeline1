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
package int_daisy_filesetEncryptor;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.crypto.SecretKey;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.Directory;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.D202NccFile;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetErrorHandler;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.encryption.EncryptionType;
import org.daisy.util.fileset.encryption.Encryptor;
import org.daisy.util.fileset.encryption.EncryptorFactory;
import org.daisy.util.fileset.encryption.exception.EncryptionException;
import org.daisy.util.fileset.encryption.exception.EncryptorNotSupportedException;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.exception.FilesetFileFatalErrorException;
import org.daisy.util.fileset.impl.FilesetFileFactory;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.util.ManifestFinder;

/**
 * Encrypt filesets. Note - depends on an Encryptor being registered as described in
 * org.daisy.util.fileset.encryption.EncryptorFactory.
 * @author Markus Gylling
 */

public class EncryptionDriver extends Transformer implements FilesetErrorHandler {
	
	private static final double FILESET_DONE = 0.1;
	private static final double ENCRYPTION_DONE = 1;
	 	
	public EncryptionDriver(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}
	
	protected boolean execute(Map<String,String> parameters) throws TransformerRunException {
		
		boolean totalSuccess = true;
		
		try {
			/*
			 * Since encryption happens late in a pipeline,
			 * its possible that a DTB has already been splitted.
			 * This is one reason that we take a folder as inparam,
			 * and apply the protection on any filesets found
			 * in that folder or subfolders.
			 */
			
			//TODO new inparam: encryptOn = ALL|DTB_FULLTEXT		
			
			Directory inputBaseDir = new Directory(FilenameOrFileURI.toFile(parameters.remove("input")));					
					
			EncryptionType encType = EncryptionType.parse(parameters.remove("encryptionType"));									
			Collection<File> inputFiles = ManifestFinder.getManifests(true,inputBaseDir);
			
			//get manifest types to accept as input for encryption
			ArrayList<String> acceptedTypes = new ArrayList<String>();
			String accept = parameters.remove("accept");			
			if(accept!=null){
				String[] strs = accept.split(",");
				for (int i = 0; i < strs.length; i++) {
					strs[i] = strs[i].trim();
				}
				Collections.addAll(acceptedTypes, strs);  								
			}
			
			//create the active manifest collection
			//by filtering out unwanted entries
			Collection<FilesetFile> manifests = new ArrayList<FilesetFile>();
			for (Iterator<File> i = inputFiles.iterator(); i.hasNext();) {
				FilesetFile manifest = FilesetFileFactory.newInstance().newFilesetFile(i.next());
				//if this is a file we should work on given acceptedTypes inparam
				if(acceptedTypes.contains(manifest.getClass().getSimpleName())) {
					manifests.add(manifest);
				}				
			}
			
			if(manifests.size() > 0){
				//create the base output dir
				Directory outputBaseDir = (Directory)FileUtils.createDirectory(new Directory(FilenameOrFileURI.toFile(parameters.remove("output"))));
				
				SecretKey secretKey = null;
				
				//go through each manifest item in the input collection
				int count = 0;
				for (Iterator<FilesetFile> i = manifests.iterator(); i.hasNext();) {												 																								
					FilesetFile manifest = i.next();
					//create input fileset
					Fileset inputFileset = new FilesetImpl(manifest.getFile().toURI(), this, false, false);					
					sendMessage(i18n("INPUT_FILESET", inputFileset.getManifestMember().getFile()),MessageEvent.Type.INFO_FINER);
					this.progress((double)count/manifests.size() + FILESET_DONE/manifests.size());
					this.checkAbort();
					//locate an impl that can encrypt this fileset				
					Encryptor encryptor = null;
					try{
						encryptor = EncryptorFactory.newInstance().newEncryptor(encType, inputFileset.getFilesetType());						
					}catch (EncryptorNotSupportedException e) {
						//no encryptor could be produced for this fileset type and set encryption type						
						sendMessage(i18n("NOTSUPPORTED", e.getMessage()),MessageEvent.Type.INFO_FINER);
						totalSuccess = false;
						continue;
					}	
					//start prepping for output.
					//recreate the holding folder relative to the inputBaseDir
					//(the inparam folder may contain subfolders where the actual stuff resides)
					Directory outputDir;
					if(!inputBaseDir.getCanonicalPath().equals(inputFileset.getManifestMember().getParentFolder().getCanonicalPath())){
						URI relative = inputBaseDir.toURI().relativize(inputFileset.getManifestMember().getFile().toURI());
						File hypo = new File(outputBaseDir, relative.getPath());
						outputDir = new Directory(FileUtils.createDirectory(hypo.getParentFile()));
					}else{
						outputDir = outputBaseDir;
					}					
					sendMessage(i18n("OUTPUT_DIR", outputDir),MessageEvent.Type.INFO_FINER);
					
					//do the encryption		
					encryptor.setInputFileset(inputFileset);
					encryptor.setOutputDir(outputDir);					
					encryptor.setParameters(getEncryptorParams(parameters, encryptor));
					try{
						boolean multiVolume = false;
						if (inputFileset.getManifestMember() instanceof D202NccFile) {
							D202NccFile d202 = (D202NccFile)inputFileset.getManifestMember();
							multiVolume = d202.hasMultiVolumeIndicators();
						}
						if (multiVolume) {							
							sendMessage(i18n("MULTI_VOLUME"),MessageEvent.Type.INFO_FINER);
							secretKey = encryptor.encrypt(secretKey);
						} else {							
							sendMessage(i18n("SINGLE_VOLUME"),MessageEvent.Type.INFO_FINER);
							encryptor.encrypt();
						}
						sendMessage(i18n("ENCRYPTED"),MessageEvent.Type.INFO_FINER);
					}catch (EncryptionException e) {
						sendMessage(i18n("EXCEPTION", e.getMessage()),MessageEvent.Type.ERROR);
						totalSuccess = false;
					}
					this.progress((double)count/manifests.size() + ENCRYPTION_DONE/manifests.size());
					this.checkAbort();
					count++;					
				}//for i
			}else{//if(manifests.size() > 0){
				sendMessage(i18n("ZERO_MANIFESTS"),MessageEvent.Type.WARNING);
			}
								
		} catch (Exception e) {
			//this.sendMessage(i18n("ERROR_ABORTING", e.getMessage()), MessageEvent.Type.ERROR);
			String message = i18n("ERROR_ABORTING", e.getMessage());
			throw new TransformerRunException(message, e);
		} 
		if (totalSuccess) return true;  
		throw new TransformerRunException("encryption not successfully completed");
	}
	
	private Map<String,String> getEncryptorParams(Map<?,?> inParams, Encryptor encryptor) throws InvalidPropertiesFormatException, MalformedURLException, IOException {				
		//this string is in URL form so that we can take jar inparams
		String param = (String)inParams.get("properties");				
		if(param != null|param.length()<1) {
			Map<String,String> params = null;
			try {			
				URL paramURL = new URL(param);
				Properties props = new Properties();
				props.loadFromXML(paramURL.openStream());
				for (Iterator<Object> iterator = props.keySet().iterator(); iterator.hasNext();) {
					String key = (String) iterator.next();
					String value = (String)props.get(key);
					params.put(key, value);
				}				
			} catch (IOException e) {
				params = encryptor.loadParameters(param);
			}
			return params;			
		}
		
		//else, no properties file explicitly set in script.	
		return null;			
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.interfaces.FilesetErrorHandler#error(org.daisy.util.fileset.exception.FilesetFileException)
	 */
	public void error(FilesetFileException ffe) throws FilesetFileException {
		this.sendMessage(ffe);
		if(ffe instanceof FilesetFileFatalErrorException) {
			throw ffe;
		} 
				
	}		
}