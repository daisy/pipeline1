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
package int_daisy_recorder2dtb;

import int_daisy_recorder2dtb.read.Reader;
import int_daisy_recorder2dtb.read.ReaderFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.event.MessageEvent.Cause;
import org.daisy.pipeline.core.event.MessageEvent.Type;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.dtb.meta.MetadataItem;
import org.daisy.util.dtb.ncxonly.model.Model;
import org.daisy.util.dtb.ncxonly.model.write.Z2005NCXOnlyDTBWriter;
import org.daisy.util.file.Directory;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.xml.Namespaces;

/**
 * Main Transformer class.
 * <p>Transform various playlist/audio editor marker sets to a NCX only Z3986 DTB.</p>
 * <p>Employs DAISY a fileset builder (org.daisy.util.dtb.ncxonly.model) that are reused to make addition of a new
 * playlist format economical.</p>
 * @author Markus Gylling
 */
public class Recorder2dtb extends Transformer implements TransformerDelegateListener {

	/**
	 * Editors to possibly support:
	 * 
	 * Cool Edit 2000/ Audacity 2.x series (.ses in cues in WAV)
	 * Adobe Audition 3 (.ses in xml format)
	 * WaveLab 5 MRK (AES31?)
	 * WaveLab 6 XML
	 * AES31-2-2006, SADiE
	 * ?
	 */
	
	public Recorder2dtb(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean execute(Map parameters) throws TransformerRunException {
				  
		try{
			File input = FilenameOrFileURI.toFile((String)parameters.remove("input"));			
									
			Reader reader = ReaderFactory.newInstance(this).get(input.toURI().toURL());
			
			informType(reader);
						
			Model model = reader.createModel();
						
			model = finalizeMetadata(model,parameters);
						
			sendMessage(0.6);
			
			Z2005NCXOnlyDTBWriter writer = new Z2005NCXOnlyDTBWriter(model,parameters); 	
			
			writer.write(getDestination((String)parameters.remove("output")));	
								
		}catch (Exception e) {
			if(e instanceof TransformerRunException) throw ((TransformerRunException)e);
			throw new TransformerRunException(i18n("ERROR_ABORTING", e.getMessage()));
		}
				
		return true;
	}

	
	private void informType(Reader reader) {
		this.sendMessage(i18n("USING_TYPE", reader.getSupportedInputType().toString())
				, MessageEvent.Type.INFO_FINER, MessageEvent.Cause.INPUT);		
	}
	
	private Directory getDestination(String dest) throws IOException {
		Directory destination = 
			new Directory(FileUtils.createDirectory(FilenameOrFileURI.toFile(dest)));
		
		this.sendMessage(i18n("RENDERING_RESULT_TO", destination.getAbsolutePath())
				, MessageEvent.Type.INFO_FINER, MessageEvent.Cause.INPUT);		
		
		return destination;
	}
	
	/**
	 * Finalize metadata.
	 * <p>The Model may have metadata set that comes from the input project file
	 * The Map parameters may have metadata that comes as pipeline input.</p>
	 * <p>Transformer parameters take precedence over the Model entries.</p>
	 *
	 * <p>If the project/marker file contains metadata, it is up to the parser
	 * to assure the keys adhere to the below (case sensitive):</p>
	 *
	 * <p>Keys for the metadata items that are guaranteed to exist:</p>
	 * <ul>
	 *  <li>dc:Title</li>
	 *  <li>dc:Creator</li>
	 *  <li>dc:Language (if no value, use system default)</li>
	 *  <li>dc:Identifier (if no value, generate GUID, use as dtb:uid)</li>
	 *  <li>dc:Date (if no value, do todays date)</li>
	 * </ul> 
	 */
	private Model finalizeMetadata(Model model, Map<String,String> parameters) {
				
		String[] searchSet = new String[]{"dc:Title","dc:Creator","dc:Language","dc:Identifier","dc:Date","dc:Publisher"};
		for (int i = 0; i < searchSet.length; i++) {
			if (parameters.containsKey(searchSet[i])) {
				model.getMetadata().add(new MetadataItem(new QName(searchSet[i]),parameters.get(searchSet[i])));
			}
		}
		
		Set<String> missingMetas = new HashSet<String>();
		
		String dcNS = Namespaces.DUBLIN_CORE_NS_URI;
		String dcPfx = "dc";
		
		QName q = new QName(dcNS,"Title",dcPfx);
		if(null==model.getMetadata().get(q)) {			
			model.getMetadata().add(new MetadataItem(q,"Title not set"));
			missingMetas.add("dc:Title");
		}
		
		q = new QName(dcNS,"Creator",dcPfx);
		if(null==model.getMetadata().get(q)) {			
			model.getMetadata().add(new MetadataItem(q,"Creator not set"));
			missingMetas.add("dc:Creator");
		}
		
		q = new QName(dcNS,"Language",dcPfx);
		if(null==model.getMetadata().get(q)) {
			String lang = java.util.Locale.getDefault().getLanguage();
			model.getMetadata().add(new MetadataItem(q,lang));
			missingMetas.add("dc:Language");
		}
		
		q = new QName(dcNS,"Identifier",dcPfx);
		if(null==model.getMetadata().get(q)) {			
			String uid = "int-pipeline-" + java.util.UUID.randomUUID().toString();
			MetadataItem item = new MetadataItem(q,uid);
			item.addAttribute("id", "uid");
			model.getMetadata().add(item);
			missingMetas.add("dc:Identifier");
		}
		
		q = new QName(dcNS,"Date",dcPfx);
		if(null==model.getMetadata().get(q)) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String date = format.format(new java.util.Date());
			model.getMetadata().add(new MetadataItem(q,date));
			missingMetas.add("dc:Date");
		}
		
		q = new QName(dcNS,"Publisher",dcPfx);
		if(null==model.getMetadata().get(q)) {						
			model.getMetadata().add(new MetadataItem(q,"Unknown publisher"));
			missingMetas.add("dc:Publisher");
		}
		
		for(String missing : missingMetas) {
			String message = i18n("MISSING_META", missing);
			this.sendMessage( message, MessageEvent.Type.WARNING, 
					MessageEvent.Cause.INPUT, null);
		}
		
		return model;
	}

	@SuppressWarnings("unused")
	private void debugPrint(Model model, String dest) throws IOException, XMLStreamException {
		File f = new File(dest);
		FileUtils.createDirectory(f.getParentFile());
		FileOutputStream fos = new FileOutputStream(f);
		model.toXML(fos);		
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.pipeline.core.transformer.TransformerDelegateListener#delegateLocalize(java.lang.String, java.lang.Object)
	 */
	public String delegateLocalize(String key, Object[] params) {
		if(key==null) return i18n(key);
		return i18n(key,params);
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.pipeline.core.transformer.TransformerDelegateListener#delegateMessage(java.lang.Object, java.lang.String, org.daisy.pipeline.core.event.MessageEvent.Type, org.daisy.pipeline.core.event.MessageEvent.Cause, javax.xml.stream.Location)
	 */
	public void delegateMessage(@SuppressWarnings("unused")Object delegate, String message, Type type, Cause cause, Location location) {
		this.sendMessage(message,type,cause,location);
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.pipeline.core.transformer.TransformerDelegateListener#delegateProgress(java.lang.Object, double)
	 */
	@SuppressWarnings("unused")
	public void delegateProgress(Object delegate, double progress) {
				
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.pipeline.core.transformer.TransformerDelegateListener#delegateCheckAbort()
	 */
	public boolean delegateCheckAbort() {
		return super.isAborted();		
	}

}
