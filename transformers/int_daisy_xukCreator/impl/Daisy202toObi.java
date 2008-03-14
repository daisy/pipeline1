package int_daisy_xukCreator.impl;

import java.io.File;
import java.net.URL;
import java.util.Map;

import org.daisy.util.file.EFolder;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.interfaces.audio.AudioFile;
import org.daisy.util.fileset.interfaces.image.ImageFile;
import org.daisy.util.fileset.util.FilesetFileFilter;
import org.daisy.util.fileset.util.FilesetLabelProvider;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.TransformerFactoryConstants;
import org.daisy.util.xml.xslt.XSLTException;

import int_daisy_xukCreator.XukFilter;
import int_daisy_xukCreator.XukFilterException;

/**
 * Convert a Daisy 2.02 DTB into a XUK file consumable by Obi.
 * <p>Factorywise, this implementation responds to 2.02 filesets and the parameter outputType with the value 'Obi'.</p>
 * @author Julien Quint
 */
public class Daisy202toObi extends XukFilter implements FilesetFileFilter {

	@Override
	public void createXuk(Fileset inputFileset, Map<String, String> parameters, EFolder destination) throws XukFilterException {
		
		URL xslt = this.getClass().getResource("Daisy202toObi.xsl");
		File input = inputFileset.getManifestMember().getFile();		
		File output = new File(destination,getFileName(inputFileset));
		
		try {
			
			Stylesheet.apply(
					input.getAbsolutePath(),
					xslt,
					output.getAbsolutePath(), 
					TransformerFactoryConstants.SAXON8,
					parameters,
					CatalogEntityResolver.getInstance());
			
		} catch (CatalogExceptionNotRecoverable e) {
			e.printStackTrace();
		} catch (XSLTException e) {
			throw new XukFilterException(e.getMessage(),e);
		}
		
	}

	private String getFileName(Fileset inputFileset) {		
		FilesetLabelProvider labelProvider = new FilesetLabelProvider(inputFileset);
		String name = labelProvider.getFilesetTitle();		
		if(name==null||name.length()<1) name="converted";
		name=name.trim().replace(" ", "_");
		if(name.length()>64) name = name.substring(0, 63);		
		name+=".obi";
		return name;
	}

	@Override
	public boolean supports(Fileset inputFileset, Map<String, String> parameters) {
		if(inputFileset.getFilesetType() == FilesetType.DAISY_202 
				&& parameters.get("outputType").equals("Obi")) {
			return true;
		}		
		return false;
	}

	@Override
	public FilesetFileFilter getCopyFilter() {
		return this;		
	}
	
	public short acceptFile(FilesetFile file) {
		if(file instanceof AudioFile || file instanceof ImageFile)
			return FilesetFileFilter.ACCEPT;
		return FilesetFileFilter.REJECT;
	}

}
