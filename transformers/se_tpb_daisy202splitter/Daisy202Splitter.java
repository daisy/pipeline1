/*
 * Daisy Pipeline (C) 2005-2009 Daisy Consortium
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
package se_tpb_daisy202splitter;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.Directory;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.file.TempFile;
import org.daisy.util.fileset.D202NccFile;
import org.daisy.util.fileset.D202SmilFile;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetErrorHandler;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.exception.FilesetFileWarningException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.pool.StAXOutputFactoryPool;

/**
 * Daisy 2.02 splitter.
 * <p>This transformer splits a Daisy 2.02 book into multiple volumes.
 * The basic steps are:</p>
 * <ul>
 *   <li>Build a fileset instance of the input book</li>
 *   <li>Create the title SMIL</li>
 *   <li>Build NCC items</li>
 *   <li>Create SMIL groups (i.e. the NCC items are divided into groups)</li>
 *   <li>Divide the SMIL groups into DTB volumes</li>
 *   <li>Copy SMIL, audio and images into the volumes</li>
 *   <li>Copy prompt files</li>
 *   <li>Copy (with some modifications) any content documents into the volumes</li>
 *   <li>Copy (with some modifications) the NCC into the volumes</li>
 * </ul>
 * @author Linus Ericson
 */
public class Daisy202Splitter extends Transformer implements FilesetErrorHandler {
	
	public static final String NAME = "Daisy Pipeline - Daisy 2.02 Splitter";
	
	private StAXInputFactoryPool staxInPool = StAXInputFactoryPool.getInstance();
    private StAXOutputFactoryPool staxOutPool = StAXOutputFactoryPool.getInstance();
    private StAXEventFactoryPool staxEvPool = StAXEventFactoryPool.getInstance();
    private Map<String,Object> staxInProperties = staxInPool.getDefaultPropertyMap(false);
    private Map<String,Object> staxOutProperties = staxOutPool.getDefaultPropertyMap();

	public Daisy202Splitter(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.pipeline.core.transformer.Transformer#execute(java.util.Map)
	 */
	@Override
	protected boolean execute(Map<String, String> parameters) throws TransformerRunException {
		String paramInput = parameters.remove("input");
		String paramOutput = parameters.remove("output");
		String paramVolumeSizeInMB = parameters.remove("volumeSizeInMB");
		String paramPromptFilesManifestPath = parameters.remove("promptFilesManifestPath");
		String paramMaxSplitLevel = parameters.remove("maxSplitLevel");
		String paramAlwaysIdSubdir = parameters.remove("alwaysIdSubdir");
		
		XMLInputFactory xif = staxInPool.acquire(staxInProperties);
		XMLOutputFactory xof = staxOutPool.acquire(staxOutProperties);
		XMLEventFactory xef = staxEvPool.acquire();
		
		try {
			// Build fileset etc
			this.sendMessage("Building fileset", MessageEvent.Type.INFO_FINER);
			File input = FilenameOrFileURI.toFile(paramInput);
			Fileset fileset = new FilesetImpl(input.toURI(), this, false, false);
			if (fileset.hadErrors()) {	
				String detail = null;
            	for (Iterator<Exception> iterator = fileset.getErrors().iterator(); iterator.hasNext();) {
					FilesetFileException exc = (FilesetFileException) iterator.next();
					if (exc instanceof FilesetFileWarningException) {
					    continue;
					}
					detail = exc.getMessage();	
					break;
				}
            	if (detail != null) {
            	    throw new TransformerRunException("Fileset had errors: " + detail);
            	}
			}
			Directory outputDir = new Directory(FilenameOrFileURI.toFile(paramOutput));
			NccItem.Type maxSplitLevel = NccItem.Type.valueOf("H" + paramMaxSplitLevel);
			long maxVolumeSize = Integer.parseInt(paramVolumeSizeInMB) * 1024 * 1024;
			D202NccFile nccFile = (D202NccFile)fileset.getManifestMember();
			String dcIdentifier = nccFile.getDcIdentifier();
			boolean useIdSubdir = "true".equals(paramAlwaysIdSubdir);			
			
			// Do we need to split the book?
			long inputDirSize = FileUtils.getSize(input.getParentFile());
			if (inputDirSize < maxVolumeSize) {
				this.sendMessage("Book is smaller than " + paramVolumeSizeInMB + ". No splitting needed.", MessageEvent.Type.INFO_FINER);
				
				// No need to split anything, just copy to output
				if (useIdSubdir) {
					outputDir = new Directory(outputDir, dcIdentifier);
				}
				outputDir.addFileset(fileset, true);
				
				// Nothing more to do here. We're done.
				return true;
			}
			
			// Not needed anymore
			fileset = null;
						
			// Create title smil
			this.sendMessage("Creating title smil", MessageEvent.Type.INFO_FINER);
			TempFile tempTitleSmil = new TempFile();
			Collection<D202SmilFile> spineItems = nccFile.getSpineItems();
			D202SmilFile firstSmil = spineItems.iterator().next();
			TitleSmilBuilder titleSmilBuilder = new TitleSmilBuilder(xif, xof);
			Set<FilesetFile> titleMediaFiles = titleSmilBuilder.createTitleSmil(firstSmil, tempTitleSmil.getFile());
			
			// Not needed anymore
			titleSmilBuilder = null;
			firstSmil = null;
			
			// Build NCC items
			this.sendMessage("Building NCC items", MessageEvent.Type.INFO_FINER);
			NccItemBuilder nccItemBuilder = new NccItemBuilder(xif, nccFile.getFile());
			// A list of all the "items" (not metadata) in the NCC
			List<NccItem> nccItems = nccItemBuilder.getNccItemList();
			// A mapping between a SMIL file and the type (h1, h2 etc) of the first reference to
			// that smil. In Daisy 2.02, each SMIL must begin with a par representing a heading
			Map<File, NccItem.Type> smilLevelMap = nccItemBuilder.getSmilLevelMap();
			String dcLanguage = nccItemBuilder.getLanguage();
			
			// Not needed anymore
			nccItemBuilder = null;
			
			// Build SMIL groups
			this.sendMessage("Building SMIL groups", MessageEvent.Type.INFO_FINER);
			List<SmilGroup> smilGroups = this.buildSmilGroups(spineItems, smilLevelMap, maxSplitLevel);
			
			// Not needed anymore
			smilLevelMap = null;
			spineItems = null;
			
			// Divide into volumes
			this.sendMessage("Dividing into volumes", MessageEvent.Type.INFO_FINER);
			List<Volume> volumes = new LinkedList<Volume>();
			// A mapping between a SMIL file and its volume number
			Map<FilesetFile,Integer> smilVolumeNumberMap = new HashMap<FilesetFile,Integer>();
			int volumeNumber = 1;
			Volume currentVolume = new Volume(volumeNumber, outputDir, dcIdentifier, useIdSubdir);
			for (SmilGroup smilGroup : smilGroups) {
				// Check if the current smil group fits into the existing volume
				if (!currentVolume.willItFit(smilGroup, maxVolumeSize)) {
					// Smil group did not fit. Create a new volume.
				    currentVolume.fillSmilVolumeNumberMap(smilVolumeNumberMap);
					volumes.add(currentVolume);					
					volumeNumber++;
					currentVolume = new Volume(volumeNumber, outputDir, dcIdentifier, useIdSubdir);
					// Add title smil media files in all volumes but the first
					currentVolume.addTitleSmilMediaFiles(titleMediaFiles);
				}
				currentVolume.addSmilGroup(smilGroup);
			}
			currentVolume.fillSmilVolumeNumberMap(smilVolumeNumberMap);
			volumes.add(currentVolume);
			
			// Not needed anymore
			smilGroups = null;
			titleMediaFiles = null;
			currentVolume = null;
			
			// Build prompt set
			this.sendMessage("Building prompt set", MessageEvent.Type.INFO_FINER);
			File promptManifest = new File(this.getTransformerDirectory(), paramPromptFilesManifestPath);
			PromptSetBuilder promptSetBuilder = new PromptSetBuilder(xif);
			PromptSet promptSet = promptSetBuilder.getPromptSet(promptManifest, dcLanguage);
			
			// Not needed anymore
            promptSetBuilder = null;
            promptManifest = null;
			
			// Write everything except ncc and content docs to the volume output dirs
			this.sendMessage("Writing most files to the volume dirs", MessageEvent.Type.INFO_FINER);
			for (Volume volume : volumes) {
				// Add smil, audio, etc
			    volume.writeNonNccToOutput(nccFile.getParentFolder(), promptSet, smilVolumeNumberMap, xif, xof);
				
				// Add title smil to all volumes except the first
				if (volume.getVolumeNumber() != 1) {
				    FileUtils.copyFile(tempTitleSmil.getFile(), new File(volume.getVolumeDir(), "title.smil"));
				}
			}
			
			// Not needed anymore
			tempTitleSmil.delete();
			tempTitleSmil = null;
			
			// Copy prompts to output dirs
			this.sendMessage("Copying prompt files", MessageEvent.Type.INFO_FINER);
			for (Volume volume : volumes) {
                for (int i = 1; i <= volumes.size(); ++i) {
                    if (i != volume.getVolumeNumber()) {
                        File audioFile = promptSet.getPromptVolume(i).audioFile;
                        FileUtils.copyFile(audioFile, new File(volume.getVolumeDir(), audioFile.getName()));
                        File smilFile = promptSet.getPromptVolume(i).smilFile;
                        FileUtils.copyFile(smilFile, new File(volume.getVolumeDir(), smilFile.getName()));
                    }
                }
            }
			
			// Write ncc to the output dirs
			this.sendMessage("Writing NCC to output dirs", MessageEvent.Type.INFO_FINER);
			NccCreator nccCreator = new NccCreator(promptSet, smilVolumeNumberMap, xif, xof, volumes.size());
			for (Volume volume : volumes) {
				nccCreator.createNcc(nccFile, new File(volume.getVolumeDir(), nccFile.getName()), volume.getVolumeNumber(), nccItems);
			}
			
			// Not needed anymore
			nccItems = null;
			smilVolumeNumberMap = null;
			promptSet = null;
			nccCreator = null;
			
			
		} catch (FilesetFatalException e) {
			throw new TransformerRunException(e.getMessage(), e);
		} catch (IOException e) {
			throw new TransformerRunException(e.getMessage(), e);
		} catch (XMLStreamException e) {
			throw new TransformerRunException(e.getMessage(), e);
		} finally {
		    staxInPool.release(xif, staxInProperties);
		    staxOutPool.release(xof, staxOutProperties);
		    staxEvPool.release(xef);
		}
		
		return true;
	}
	
	private List<SmilGroup> buildSmilGroups(Collection<D202SmilFile> spineItems, Map<File, NccItem.Type> smilLevelMap, NccItem.Type maxSplitLevel) {
		List<SmilGroup> smilGroups = new LinkedList<SmilGroup>();
		SmilGroup currentSmilGroup = new SmilGroup();
		for (D202SmilFile smilFile : spineItems) {
			NccItem.Type smilLevel = smilLevelMap.get(smilFile.getFile());
			if (smilLevel.ordinal() <= maxSplitLevel.ordinal()) {
				if (currentSmilGroup.getSize() > 0) {
					// The if clause is needed to prevent an initial empty group
					smilGroups.add(currentSmilGroup);
					currentSmilGroup.calculateDiskUsage();
				}
				currentSmilGroup = new SmilGroup();
			}
			currentSmilGroup.add(smilFile);
		}
		// Add the last smil group to the list
		smilGroups.add(currentSmilGroup);
		currentSmilGroup.calculateDiskUsage();
		return smilGroups;
	}

	public void error(FilesetFileException ffe) throws FilesetFileException {
		this.sendMessage(ffe);
	}

}
