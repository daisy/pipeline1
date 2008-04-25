package int_daisy_filesetGenerator;

import int_daisy_filesetGenerator.FilesetGeneratorFactory.OutputType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.EFolder;
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
public class FilesetGenerator extends Transformer implements FilesetErrorHandler {

	public FilesetGenerator(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}
	
	@Override
	protected boolean execute(Map parameters) throws TransformerRunException {
		try{
			
			/*
			 * Grok parameters
			 */
			File input = FilenameOrFileURI.toFile((String)parameters.remove("input"));			
			OutputType outputType = getFilesetType((String)parameters.remove("outputType"));			
			EFolder destination = new EFolder(FileUtils.createDirectory(new File((String)parameters.remove("outputDirectory"))));			
			String uid = (String) parameters.remove("uid");
			
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
			if(uid!=null && uid.length()>0) config.put("uid", uid);
			
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


}
