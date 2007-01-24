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
import java.util.Set;
import java.util.logging.Level;

import javax.crypto.SecretKey;

import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.file.EFolder;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.encryption.EncryptionType;
import org.daisy.util.fileset.encryption.Encryptor;
import org.daisy.util.fileset.encryption.EncryptorFactory;
import org.daisy.util.fileset.encryption.exception.EncryptionException;
import org.daisy.util.fileset.encryption.exception.EncryptorNotSupportedException;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.exception.FilesetFileFatalErrorException;
import org.daisy.util.fileset.impl.FilesetFileFactory;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetErrorHandler;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.interfaces.xml.d202.D202NccFile;
import org.daisy.util.fileset.util.ManifestFinder;

/**
 * Encrypt filesets. Note - depends on an Encryptor being registered as described in
 * org.daisy.util.fileset.encryption.EncryptorFactory.
 * @author Markus Gylling
 */

public class EncryptionDriver extends Transformer implements FilesetErrorHandler {
	
	private static final double FILESET_DONE = 0.1;
	private static final double ENCRYPTION_DONE = 1;
	 	
	public EncryptionDriver(InputListener inListener, Set eventListeners, Boolean isInteractive) {
		super(inListener, eventListeners, isInteractive);
	}
	
	protected boolean execute(Map parameters) throws TransformerRunException {
		
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
			
			EFolder inputBaseDir = new EFolder(FilenameOrFileURI.toFile((String)parameters.remove("input")));					
					
			EncryptionType encType = EncryptionType.parse((String)parameters.remove("encryptionType"));									
			Collection inputFiles = ManifestFinder.getManifests(true,inputBaseDir);
			
			//get manifest types to accept as input for encryption
			ArrayList acceptedTypes = new ArrayList();
			String accept = (String)parameters.remove("accept");			
			if(accept!=null){
				String[] strs = accept.split(",");
				for (int i = 0; i < strs.length; i++) {
					strs[i] = strs[i].trim();
				}
				Collections.addAll(acceptedTypes, strs);  								
			}
			
			//create the active manifest collection
			//by filtering out unwanted entries
			Collection manifests = new ArrayList();
			for (Iterator i = inputFiles.iterator(); i.hasNext();) {
				FilesetFile manifest = FilesetFileFactory.newInstance().newFilesetFile((File)i.next());
				//if this is a file we should work on given acceptedTypes inparam
				if(acceptedTypes.contains(manifest.getClass().getSimpleName())) {
					manifests.add(manifest);
				}				
			}
			
			if(manifests.size() > 0){
				//create the base output dir
				EFolder outputBaseDir = (EFolder)FileUtils.createDirectory(new EFolder(FilenameOrFileURI.toFile((String)parameters.remove("output"))));
				
				SecretKey secretKey = null;
				
				//go through each manifest item in the input collection
				int count = 0;
				for (Iterator i = manifests.iterator(); i.hasNext();) {												 																								
					FilesetFile manifest = (FilesetFile)i.next();
					//create input fileset
					Fileset inputFileset = new FilesetImpl(manifest.getFile().toURI(), this, false, false);
					this.sendMessage(Level.INFO, i18n("INPUT_FILESET", inputFileset.getManifestMember().getFile()));
					this.progress((double)count/manifests.size() + FILESET_DONE/manifests.size());
					this.checkAbort();
					//locate an impl that can encrypt this fileset				
					Encryptor encryptor = null;
					try{
						encryptor = EncryptorFactory.newInstance().newEncryptor(encType, inputFileset.getFilesetType());						
					}catch (EncryptorNotSupportedException e) {
						//no encryptor could be produced for this fileset type and set encryption type
						sendMessage(Level.WARNING, i18n("NOTSUPPORTED", e.getMessage()));
						totalSuccess = false;
						continue;
					}	
					//start prepping for output.
					//recreate the holding folder relative to the inputBaseDir
					//(the inparam folder may contain subfolders where the actual stuff resides)
					EFolder outputDir;
					if(!inputBaseDir.getCanonicalPath().equals(inputFileset.getManifestMember().getParentFolder().getCanonicalPath())){
						URI relative = inputBaseDir.toURI().relativize(inputFileset.getManifestMember().getFile().toURI());
						File hypo = new File(outputBaseDir, relative.getPath());
						outputDir = new EFolder(FileUtils.createDirectory(hypo.getParentFile()));
					}else{
						outputDir = outputBaseDir;
					}
					this.sendMessage(Level.INFO, i18n("OUTPUT_DIR", outputDir));
					
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
							this.sendMessage(Level.FINE, i18n("MULTI_VOLUME"));
							secretKey = encryptor.encrypt(secretKey);
						} else {
							this.sendMessage(Level.FINE, i18n("SINGLE_VOLUME"));
							encryptor.encrypt();
						}
						this.sendMessage(Level.INFO, i18n("ENCRYPTED"));						
					}catch (EncryptionException e) {
						sendMessage(Level.SEVERE, i18n("EXCEPTION", e.getMessage()));
						totalSuccess = false;
					}
					this.progress((double)count/manifests.size() + ENCRYPTION_DONE/manifests.size());
					this.checkAbort();
					count++;					
				}//for i
			}else{//if(manifests.size() > 0){
				this.sendMessage(Level.WARNING, i18n("ZERO_MANIFESTS"));
			}
								
		} catch (Exception e) {
			throw new TransformerRunException(e.getMessage(), e);
		} 
		if (totalSuccess) return true;  
		throw new TransformerRunException("encryption not successfully completed");
	}
	
	private Map getEncryptorParams(Map inParams, Encryptor encryptor) throws InvalidPropertiesFormatException, MalformedURLException, IOException {				
		//this string is in URL form so that we can take jar inparams
		String param = (String)inParams.get("properties");				
		if(param != null|param.length()<1) {
			Map params = null;
			try {			
				URL paramURL = new URL(param);
				Properties props = new Properties();
				props.loadFromXML(paramURL.openStream());
				params = props;
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
		if(ffe instanceof FilesetFileFatalErrorException) {
			throw ffe;
		} 
		this.sendMessage(Level.WARNING,ffe.getCause() + " in " + ffe.getOrigin());		
	}		
}