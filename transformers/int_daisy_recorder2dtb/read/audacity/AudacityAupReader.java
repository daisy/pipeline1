package int_daisy_recorder2dtb.read.audacity;

import int_daisy_recorder2dtb.InputType;
import int_daisy_recorder2dtb.read.Reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.dtb.ncxonly.model.AudioClip;
import org.daisy.util.dtb.ncxonly.model.Item;
import org.daisy.util.dtb.ncxonly.model.Model;
import org.daisy.util.dtb.ncxonly.model.AudioClip.Nature;
import org.daisy.util.file.EFolder;
import org.daisy.util.xml.Namespaces;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.peek.PeekResult;
import org.daisy.util.xml.peek.Peeker;
import org.daisy.util.xml.peek.PeekerPool;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.stax.StaxEntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * A reader for Audacity .aup project files.
 * @author Markus Gylling
 */
public final class AudacityAupReader extends Reader implements ErrorHandler {

	private static final String AUDACITY_NSURI = "http://audacity.sourceforge.net/xml/";
	
	public AudacityAupReader() {
		
	}
	
	public AudacityAupReader(URL file, TransformerDelegateListener tdl) {
		super(file,tdl);
	}
	
	@Override
	public Model createModel() throws TransformerRunException {
		
		Model model = new Model();
		XMLInputFactory xif = null;
		XMLStreamReader reader = null;
		Map properties = null;
		InputStream is = null;
		
		try{
				
			/*
			 * Get a list of all audiofiles in the physical Audacity project dir.
			 * In the .aup XML file, the refs are not resolvable URLs, but mere local names.
			 * We assume that all file local names are unique. 
			 */			
			File projectFile = new File(mInputURL.toURI());
			EFolder start = new EFolder(projectFile.getParentFile());
			Collection<File> coll = start.getFiles(true, "(.+\\.wav$)|(.+\\.au$)|(.+\\.aiff?$)");
			Map<String,File> projectAudioFiles = new HashMap<String, File>();
			for(File f : coll) {
				projectAudioFiles.put(f.getName(), f);
			}
						
			/*
			 * Prep an XML reader 
			 */
			properties = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(false);
			xif = StAXInputFactoryPool.getInstance().acquire(properties);
			xif.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
			is = mInputURL.openStream();
			reader = xif.createXMLStreamReader(is);
			
			/*
			 * Build the labeltrack and wavetrack lists.
			 * We need to populate all before building Model Items since 
			 * clips can traverse several tracks, so we need to jump around
			 */
			List<AupLabelTrack> aupLabelTracks = new LinkedList<AupLabelTrack>();
			AupWaveTracks aupWaveTracks = new AupWaveTracks();
			
			while(reader.hasNext()) {
				reader.next();
				if(reader.isStartElement()) {
					if(reader.getLocalName().equals("tags")) {
						setMetadata(reader,model);
					}else if (reader.getLocalName().equals("wavetrack")) {
						aupWaveTracks.add(readWaveTrack(reader,projectAudioFiles));
					}else if (reader.getLocalName().equals("labeltrack")) {						
						aupLabelTracks.add(readLabelTrack(reader));
					}
				}
			}
			
			//debugPrint(aupWaveTracks);
			
			/*
			 * now we have all tracks, populate the model
			 */
			populate(model,aupLabelTracks,aupWaveTracks);
			
			
		} catch (Exception e) {			
			throw new TransformerRunException(e.getMessage(),e);
		}finally{			
			try {
				reader.close();
				is.close();
				StAXInputFactoryPool.getInstance().release(xif, properties);			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
		return model;
	}

	private void debugPrint(AupWaveTracks aupWaveTracks) {
		int i = 0;
		for(AupWaveTrack track : aupWaveTracks) {
			i++;
			System.err.println("Track " + i + ":");
			for(AupBlockFile bf : track) {
				System.err.println(bf.getFile().getName());
			}
		}		
	}

	/**
	 * Populate the Model with Item objects.
	 */
	private void populate(Model model, List<AupLabelTrack> aupLabelTracks, AupWaveTracks aupWaveTracks) {
		
		if (aupLabelTracks.size()!= aupWaveTracks.size()) {
			String message = mTransformer.delegateLocalize("WAVE_LABEL_TRACK_COUNT_MISMATCH",null);
			throw new IllegalStateException(message);
		}	

		double totalElapsedSeconds = 0;//total presentation time
		
		for (int i = 0; i < aupLabelTracks.size(); i++) {
//			debug
//			System.err.print("Track " + (i+1));
//			if(i==aupLabelTracks.size()-1) {
//				System.err.print("=last track");
//			}
//			System.err.println("");
//			end debug
			
			AupLabelTrack labelTrack = aupLabelTracks.get(i);
			AupWaveTrack waveTrack = aupWaveTracks.get(i);
									
			for (int j = 0; j < labelTrack.size(); j++) {
				AupLabel label = labelTrack.get(j);				
				//get the presentation start and end durs for this label
				//may or may not be in same file, may or may not be in same wavetrack				
				//the labelStart and labelEnd values represent clocks vis-a-vis				
				//the whole presentation.
				
				double labelStartSeconds;
				if(i==0) {
					labelStartSeconds = label.getStartTimeSeconds();
				}else{
					//append passed time
					labelStartSeconds = totalElapsedSeconds+label.getStartTimeSeconds();
				}
				
				//get the start of the next label (ignore label self-duration for now)
				double nextLabelStartSeconds;
				if(j<labelTrack.size()-1) {
					//next label in same wavetrack
					nextLabelStartSeconds = labelTrack.get(j+1).getStartTimeSeconds();
				}else{
					//next label in next wavetrack
					if(i<aupWaveTracks.size()-1) {
						//a next wavetrack exists
						AupLabelTrack nextTrack = aupLabelTracks.get(i+1);
						AupLabel nextLabel = nextTrack.getFirst();						
						nextLabelStartSeconds = nextLabel.getStartTimeSeconds() + waveTrack.getDurationSeconds();
					}else{
						//we are at the last  label of the last wavetrack
						//this clip should be set to physical end of the track
						nextLabelStartSeconds = waveTrack.getDurationSeconds();
					}
				}				
				if(i>0) {
					//append passed time
					nextLabelStartSeconds += totalElapsedSeconds;  
				}
												
				/*
				 * Get the clips that represent this interval
				 * (say, an entire page).
				 */
				List<AudioClip> intervalClips = new LinkedList<AudioClip>();
				
				//if label.hasDuration, then we should create a shouldPersist clip for that
				//this is typically done for pagenum and heading announcements
												
				if(label.hasDuration()) {
					List<AudioClip> labelClips = null;
					//the clip is typically one file == one clip, but if we are unlucky,
					//its split over several physical files.
					labelClips = 
						aupWaveTracks.getClips(
								labelStartSeconds, labelStartSeconds + label.getDurationSeconds());
					int k = 0;
					for(AudioClip clip : labelClips) {
						k++;
						if(k==1) {
							clip.setNature(Nature.NONTRANSIENT);
						}else{
							//the smil renderer should make this one audio element
							clip.setNature(Nature.TRANSIENT);
						}
					}
					intervalClips.addAll(labelClips);
					//reset labelStartSeconds to be beyond the clip(s) we just got 
					labelStartSeconds = labelStartSeconds+ label.getDurationSeconds();
				}
												
				//now get all clips until the next label start
				intervalClips.addAll(aupWaveTracks.getClips(labelStartSeconds,nextLabelStartSeconds));
												
				//done.
				model.add(new Item(label.getSemantic(),label.getValue(),intervalClips));
				
			}//for (int j = 0; j < labelTrack.size(); j++)	
			totalElapsedSeconds += waveTrack.getDurationSeconds();
		}//for (int i = 0; i < aupLabelTracks.size(); i++				
	}

	/**
	 * Populate an AupLabelTrack with 1-n AupLabel.
	 */
	private AupLabelTrack readLabelTrack(XMLStreamReader reader) throws XMLStreamException {
		AupLabelTrack labelTrack = new AupLabelTrack();
		while(reader.hasNext()) {
			reader.next();
			if(reader.isEndElement() && reader.getLocalName().equals("labeltrack")) {
				break;
			}else if(reader.isStartElement() && reader.getLocalName().equals("label")) {
				String title = null;
				String t = null;
				String t1 = null;
				for (int i = 0; i < reader.getAttributeCount(); i++) {
					String name = reader.getAttributeName(i).getLocalPart();
					if(name.equals("t")) {
						t = reader.getAttributeValue(i);
					}else if(name.equals("t1")) {
						t1 = reader.getAttributeValue(i);
					}else if(name.equals("title")) {
						title = reader.getAttributeValue(i);
					}
				}	
				AupLabel label = new AupLabel(t,t1,title,mTransformer);
				labelTrack.add(label);
			}
		}
		return labelTrack;
	}
	
	/**
	 * Retrieve with reader positioned at wavetrack element, 
	 * return with reader positioned at wavetrack close element.
	 * @throws XMLStreamException 
	 * @throws IOException 
	 * @throws UnsupportedAudioFileException 
	 */
	private AupWaveTrack readWaveTrack(XMLStreamReader reader, Map<String, File> projectAudioFiles) throws XMLStreamException, UnsupportedAudioFileException, IOException {
		AupWaveTrack wavetrack = new AupWaveTrack();
		while(reader.hasNext()) {
			reader.next();
			if(reader.isEndElement() && reader.getLocalName().equals("wavetrack")) {
				break;
			}else if(reader.isStartElement()) {
				//Note: we are only aware of simpleblockfile at this time
				if(reader.getLocalName().equals("simpleblockfile")) {
					File file = null;
					String len = "unset";
					for (int i = 0; i < reader.getAttributeCount(); i++) {
						if(reader.getAttributeName(i).getLocalPart().equals("filename")) {
							String filename = reader.getAttributeValue(i);
							file = projectAudioFiles.get(filename);
							if(file==null) throw new FileNotFoundException(file.getAbsolutePath());													
						}else if(reader.getAttributeName(i).getLocalPart().equals("len")) {
							len = reader.getAttributeValue(i);
						}
					}	
					wavetrack.add(new AupBlockFile(file,len));	
				}
			}									
		}
		return wavetrack;
	}

	/**
	 * Retrieve with reader positioned at tags element, 
	 * return with reader positioned at tags close element,
	 * @throws XMLStreamException 
	 */
	private void setMetadata(XMLStreamReader reader, Model model) throws XMLStreamException {
		String dcNS = Namespaces.DUBLIN_CORE_NS_URI;
		String dcPfx = "dc";
		
		//reader positioned at <tags/>
		for (int i = 0; i < reader.getAttributeCount(); i++) {
			String name = reader.getAttributeName(i).getLocalPart();
			if(name.equals("title")) {
				QName q = new QName(dcNS,"Title",dcPfx);
				model.getMetadata().add(q, reader.getAttributeValue(i));
			}else if(name.equals("artist")) {
				QName q = new QName(dcNS,"Creator",dcPfx);
				model.getMetadata().add(q, reader.getAttributeValue(i));
			}else if(name.equals("year")) {
				QName q = new QName(dcNS,"Date",dcPfx);
				model.getMetadata().add(q, reader.getAttributeValue(i));
			}			
		}		
		//check if any <tag> children
		while(reader.hasNext()) {
			reader.next();
			if(reader.isEndElement() && reader.getLocalName().equals("tags")) {
				return;
			}else if(reader.isStartElement() && reader.getLocalName().equals("tag")) {
				String name = ""; 
				String value = ""; 
				for (int i = 0; i < reader.getAttributeCount(); i++) {
					String attrName = reader.getAttributeName(i).getLocalPart();
					if(attrName.equals("name")) {
						name = reader.getAttributeValue(i);
					}else if(attrName.equals("value")) {
						value = reader.getAttributeValue(i);
					}
				}	
				if(name.length()>0&&value.length()>0) {
					model.getMetadata().add(name, value);
				}
			}
		}		
	}
	
	@Override
	public boolean supports(URL u) {
		Peeker peeker = null;
		try{
			peeker = PeekerPool.getInstance().acquire();
			PeekResult result = peeker.peek(u);
			QName qn = result.getRootElementQName();
			if(qn.getLocalPart().equals("project") 
					&& qn.getNamespaceURI().equals(AUDACITY_NSURI)) {
				return true;
			}			
		} catch (SAXException e) {
			
		} catch (IOException e) {
			
		}finally{
			PeekerPool.getInstance().release(peeker);
		}
		return false;
	}
	
	@Override
	public InputType getSupportedInputType() {
		return InputType.AUDACITY_AUP;
	}

	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
	 */
	public void error(SAXParseException exception) throws SAXException {
		throw exception;
	}

	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
	 */
	public void fatalError(SAXParseException exception) throws SAXException {
		throw exception;		
	}

	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
	 */
	public void warning(SAXParseException exception) throws SAXException {
				
	}

	
}
