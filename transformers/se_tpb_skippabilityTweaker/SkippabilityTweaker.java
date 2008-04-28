/*
 * Daisy Pipeline
 * Copyright (C) 2008  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package se_tpb_skippabilityTweaker;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.Directory;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.file.TempFile;
import org.daisy.util.fileset.D202MasterSmilFile;
import org.daisy.util.fileset.D202NccFile;
import org.daisy.util.fileset.D202SmilFile;
import org.daisy.util.fileset.D202TextualContentFile;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetErrorHandler;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.pool.StAXOutputFactoryPool;

/**
 * Adds skippability to a Daisy 2.02 fileset.
 * 
 * <p>Limitations:</p>
 * <ul>
 *  <li>Skippability in NCC only books is not supported</li>
 *  <li>Multiple content documents is currently not supported (but fixable)</li>
 *  <li>Books where the files in the book reside in different subfolders is not supported (but fixable)</li> 
 * </ul>
 * 
 * Todo:
 *  - status info
 * 
 * @author Linus Ericson
 */
public class SkippabilityTweaker extends Transformer implements FilesetErrorHandler {

    // Basic algorithm:
    //  1. Search for skippable items in the NCC
    //  2. For each SMIL file containing skippable items
    //     - Add system-required attribute for pagenum, sidebar and prodnote
    //     - Get the link to the noteref in the full text document
    //  3. Find the note bodies in the full text document by looking at the bodyref attributes
    //  4. Get the SMIL links for each note body
    //  5. For each noteref in SMIL, copy or move the corresponding note body	
	//  6. Recalculate all SMIL time values
    //  7. If the note bodies were moved, repoint the content doc to the new SMIL locations    
    //  8. Remove the internal DTD subset from the NCC and content doc. Update the
    //     total time in the NCC
    //  9. Copy all other files from the input filesset

    
	private boolean skipPagenum;
	private boolean skipSidebar;
	private boolean skipProdnote;
	private boolean skipFootnote;
    private boolean moveFootnote;
    private String backupDir = null;
    
    private StAXInputFactoryPool staxInPool = StAXInputFactoryPool.getInstance();
    private StAXOutputFactoryPool staxOutPool = StAXOutputFactoryPool.getInstance();
    private StAXEventFactoryPool staxEvPool = StAXEventFactoryPool.getInstance();
    private Map<String,Object> staxInProperties = staxInPool.getDefaultPropertyMap(false);
    private Map<String,Object> staxOutProperties = staxOutPool.getDefaultPropertyMap();
    
    private NccItemFinder nccItemFinder;
    private SimpleSkippabilityAdder simpleSkippabilityAdder;
    
    private int count = 0;
    private int countMax = 0;
	
	public SkippabilityTweaker(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
        simpleSkippabilityAdder = new SimpleSkippabilityAdder(staxInPool, staxInProperties, staxOutPool, staxOutProperties, staxEvPool);
	}

	@Override
	protected boolean execute(Map<String,String> parameters) throws TransformerRunException {
		String paramInput = parameters.remove("input");
		String paramOutput = parameters.remove("output");
		String paramPagenum = parameters.remove("pagenum");
		String paramSidebar = parameters.remove("sidebar");
		String paramProdnote = parameters.remove("prodnote");
		String paramNote = parameters.remove("note");
		String paramBackupPrefix = parameters.remove("backupPrefix");
		String paramCopyNonFilesetFiles = parameters.remove("copyNonFilesetFiles");
		
		if (paramBackupPrefix != null && !"".equals(paramBackupPrefix)) {
        	backupDir = paramBackupPrefix + new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
        }
		
		skipPagenum = Boolean.parseBoolean(paramPagenum);
		skipSidebar = Boolean.parseBoolean(paramSidebar);
		skipProdnote = Boolean.parseBoolean(paramProdnote);
		skipFootnote = "copy".equals(paramNote) || "move".equals(paramNote);
        moveFootnote = "move".equals(paramNote);
		
		try {			
			File input = FilenameOrFileURI.toFile(paramInput);
            Directory outputDir = new Directory(FilenameOrFileURI.toFile(paramOutput));
            
            List<File> manifests = new ArrayList<File>();            
            if ("ncc.html".equalsIgnoreCase(input.getName()) ||"ncc.htm".equalsIgnoreCase(input.getName())) {
                // Single book volume. Just one book to process.
                manifests.add(input);                
            } else {
            	// Copy discinfo file
            	if (input.getName().toLowerCase().matches("discinfo\\.html?")) {
            		FileUtils.copy(input, new File(outputDir, input.getName()));
            	}
            	
            	// If the input is a multi book volume, we need to tweak each sub book
            	DiscInfoNccFinder discInfoNccFinder = new DiscInfoNccFinder(staxInPool, staxInProperties);
            	List<File> nccFiles = discInfoNccFinder.getNccFilesFromDiscInfo(input);
            	manifests.addAll(nccFiles);
            }
            
            // Iterate over each book
            countMax = manifests.size();
            for (File manifest : manifests) {
                count++;
                File bookOutputDir;
                if (input.getParentFile().getCanonicalPath().equals(manifest.getParentFile().getCanonicalPath())) {
                    // Manifest dir equals original input dir. Then the book output dir
                    // should equal the original output dir.
                    bookOutputDir = outputDir;
                } else {
                    // Find the relative path to the input dir and use that to create the output dir
                    URI relative = input.getParentFile().toURI().relativize(manifest.getParentFile().toURI());
                    bookOutputDir = new File(outputDir.toURI().resolve(relative));
                }
                
                // Tweak the book
                tweakBook(manifest, new Directory(bookOutputDir));
            }
            
            // Copy files not in the fileset (i.e. copy everything but don't overwrite anything)?
            if ("true".equals(paramCopyNonFilesetFiles)) {
            	Directory inputDir = new Directory(input.getParentFile());
            	inputDir.copyChildrenTo(outputDir, false);
            }
            
		} catch (IOException e) {			
			throw new TransformerRunException(e.getMessage(), e);
		} catch (FilesetFatalException e) {
			throw new TransformerRunException(e.getMessage(), e);
		} catch (CatalogExceptionNotRecoverable e) {
			throw new TransformerRunException(e.getMessage(), e);
		} catch (XMLStreamException e) {
			throw new TransformerRunException(e.getMessage(), e);
		}
		
		return true;
	}
    
    
    @Override
    protected void progress(double progress) {
        if (count > 0 && countMax > 0) {
            double start = (count - 1) / (double)countMax;
            double current = progress / countMax;
            super.progress(start + current);
        } else {
            super.progress(progress);
        }
    }

    private void tweakBook(File input, Directory outputDir) throws IOException, TransformerRunException, FilesetFatalException, CatalogExceptionNotRecoverable, XMLStreamException {
        File inputDir = input.getParentFile();
        URI inputDirUri = inputDir.toURI();
        //URI outputDirUri = outputDir.toURI();
        Map<File, Map<String, NccItem>> data = new LinkedHashMap<File, Map<String, NccItem>>();
        Map<File, Map<String, String>> noteData = new LinkedHashMap<File, Map<String, String>>();
        
        SkippableContentIds skippableContentIds = new SkippableContentIds();
        
        // Build a fileset of the input
        this.sendMessage(i18n("BUILDING_FILESET"), MessageEvent.Type.INFO_FINER);
        Fileset fileset = new FilesetImpl(input.toURI(), this, false, false);
        if (fileset.hadErrors()) {
            throw new TransformerRunException("Fileset had errors");
        }
        if (fileset.getFilesetType() != FilesetType.DAISY_202) {
            throw new TransformerRunException("Only Daisy 2.02 filesets are allowed");
        }
        this.progress(0.20);
        this.checkAbort();
        
        // Do we need to create a backup of changed files?
        if (backupDir != null) {
        	File fullBackupDir = new File(outputDir, backupDir);
        	for (FilesetFile filesetFile : fileset.getLocalMembers()) {
                if (filesetFile instanceof D202SmilFile || filesetFile instanceof D202NccFile
                		|| filesetFile instanceof D202TextualContentFile || filesetFile instanceof D202MasterSmilFile) {
                    URI relative = inputDirUri.relativize(filesetFile.getFile().toURI());
                    File outputFile = new File(fullBackupDir, relative.toString());
                    FileUtils.copyFile(filesetFile.getFile(), outputFile);
                }                
            }
        }
        
        // Copy all SMIL files to the desired location (they will be updated
        // later on as modifications are performed
        for (FilesetFile filesetFile : fileset.getLocalMembers()) {
            if (filesetFile instanceof D202SmilFile || filesetFile instanceof D202NccFile || filesetFile instanceof D202TextualContentFile) {
                URI relative = inputDirUri.relativize(filesetFile.getFile().toURI());
                //URI outputUri = outputDirUri.resolve(relative);
                File outputFile = new File(outputDir, relative.toString());
                FileUtils.copyFile(filesetFile.getFile(), outputFile);
            }
            if (filesetFile instanceof D202TextualContentFile) {
                ContentDocSkippableItemFinder contentDocSkippableItemFinder = new ContentDocSkippableItemFinder(staxInPool, staxInProperties);
                SkippableContentIds result = contentDocSkippableItemFinder.findSkippableItems(filesetFile.getFile(), skipPagenum, skipSidebar, skipProdnote);
                skippableContentIds.addAll(result);
            }
        }
        this.progress(0.30);
        this.checkAbort();
        
        // STEP 1
        // Get a list of interesting (pagenum, sidebar, prodnote, footnote) NCC items
        this.sendMessage(i18n("STEP_SEARCH_NCC_ITEMS"), MessageEvent.Type.INFO_FINER);
        nccItemFinder = new NccItemFinder(staxInPool, staxInProperties, skipPagenum, skipSidebar, skipProdnote, skipFootnote);
        List<NccItem> items = nccItemFinder.getNccItemList(new File(outputDir, input.getName()));
        
        // Insert items into data structure
        for (NccItem item : items) {
            File file = new File(outputDir, item.getUri().substring(0, item.getUri().indexOf("#")));
            String fragment = item.getUri().substring(item.getUri().indexOf("#") + 1);
            if (!data.containsKey(file)) {
                data.put(file, new HashMap<String,NccItem>());
            }
            data.get(file).put(fragment, item);
        }
        this.progress(0.35);
        this.checkAbort();

        
        // STEP 2
        // Loop through all SMIL files containing some kind of skippable items
        this.sendMessage(i18n("STEP_ADD_SIMPLE_SKIPPABILITY"), MessageEvent.Type.INFO_FINER);
        for (File file : data.keySet()) {
            Map<String,NccItem> map = data.get(file);
            
            TempFile tempFile = new TempFile(file);
            
            // Add simple skippability
            Map<String,String> result = simpleSkippabilityAdder.addSimpleSkippability(tempFile.getFile(), file, map, skippableContentIds);
            tempFile.delete();
            noteData.put(file, result);
            
            // We don't need this data anymore
            data.put(file, null);
            
            this.checkAbort();
        }
        
        // We don't need this data anymore
        data = null;
        this.progress(0.50);
        this.checkAbort();
        
        // We have now added pagenum, sidebar and prodnote skippability in SMIL. Time to deal
        // with the footnotes. For each content doc file, we build a set of all noteref IDs.
        // These will be used to find the SMIL references in the note bodies.            
        Map<File, Set<String>> contentNoterefIds = new HashMap<File, Set<String>>();
        for (Map<String,String> idContentRefMap : noteData.values()) {
            for (String ref : idContentRefMap.values()) {
                String doc = ref.substring(0, ref.indexOf("#"));
                String fragment = ref.substring(ref.indexOf("#")+1);
                File file = new File(outputDir, doc);
                if (!contentNoterefIds.containsKey(file)) {
                    Set<String> idSet = new HashSet<String>();
                    contentNoterefIds.put(file, idSet);
                }
                contentNoterefIds.get(file).add(fragment);
                //System.err.println("Fragment " + fragment + " added");
            }
        }
        
        // STEP 3 and 4
        // Go bodyref huntin'
        this.sendMessage(i18n("STEP_FIND_NOTEBODY_SMILREFS"), MessageEvent.Type.INFO_FINER);
        ContentDocSearcher contentDocSearcher = new ContentDocSearcher(staxInPool, staxInProperties);
        Map<File, Map<String,NoterefNotebodySmilInfo>> contentNoterefBodySmilrefs = contentDocSearcher.search(contentNoterefIds);
        this.progress(0.55);
        this.checkAbort();
        
        // Discard some data we don't need anymore
        //noteData = null;            
        contentNoterefIds = null;
        
        // STEP 5
        // For each content doc file we now have a Map between a noteref ID
        // and the note body ID and its SMIL references. Now it is time to copy
        // or move the SMIL elements for the note bodies. 
        this.sendMessage(i18n("STEP_FIX_NOTEBODY_SMIL"), MessageEvent.Type.INFO_FINER);

        // Sort by SMIL containing note body data
        Map<File, Set<String>> smilNotenody = new HashMap<File, Set<String>>();
        for (Map<String,NoterefNotebodySmilInfo> noterefBodySmilRefs : contentNoterefBodySmilrefs.values()) {
            for (NoterefNotebodySmilInfo notebodySmilInfo : noterefBodySmilRefs.values()) {
                for (FileAndFragment smilRef : notebodySmilInfo.getSmilRefs()) {
                    File smil = new File(outputDir, smilRef.getFile());
                    if (!smilNotenody.containsKey(smil)) {
                        smilNotenody.put(smil, new HashSet<String>());
                    }
                    smilNotenody.get(smil).add(smilRef.getFragment());
                }
            }
        }
        // For each SMIL file we now have a set of IDs we are interested in (the note body IDs).
        // Get the XMLEvents for those SMIL clips.
        SmilEventFetcher smilEventFetcher = new SmilEventFetcher(staxInPool, staxInProperties, staxOutPool, staxOutProperties, staxEvPool, moveFootnote);
        Map<File, Map<String, List<XMLEvent>>> smilIdEventMap = new HashMap<File, Map<String, List<XMLEvent>>>();
        // We must iterate over all noteref IDs (not note body IDs) since there may
        // be more than one reference to each note body
        for (File smil : smilNotenody.keySet()) {
            TempFile tempFile = new TempFile(smil);
            Map<String, List<XMLEvent>> idEventMap = smilEventFetcher.fetch(tempFile.getFile(), smilNotenody.get(smil), smil);
            tempFile.delete();
            smilIdEventMap.put(smil, idEventMap);
        }
        
        // Now we need to inject those SMIL clips into the correct SMIL files
        Map<File,Map<String,String>> smilContentBodySmilBodyMap = new HashMap<File,Map<String,String>>();
        SmilEventInjector smilEventInjector = new SmilEventInjector(staxInPool, staxInProperties, staxOutPool, staxOutProperties, staxEvPool);
        for (File smil : noteData.keySet()) {
            Set<String> smilIds = noteData.get(smil).keySet();                
            if(!smilIds.isEmpty()) {
                String contentBodyRef = null;
                // For each SMIL file we have a set of IDs containing note references
                Map<String,Pair<String,List<XMLEvent>>> smilNoterefIdXMLEventMap = new HashMap<String, Pair<String,List<XMLEvent>>>();
                for (String smilNoterefId : smilIds) {
                    //FileAndFragment faf = this.getSmilNoteBodyId(smil, smilNoterefId);
                    String contentref = noteData.get(smil).get(smilNoterefId);
                    //System.err.println("NotrefSsmil=" + smil + ", noterefId=" + smilNoterefId + ", contentRef=" + contentref);
                    File f = new File(outputDir, contentref.substring(0, contentref.indexOf("#")));                     
                    Map<String,NoterefNotebodySmilInfo> noterefBodySmilRefs = contentNoterefBodySmilrefs.get(f);
                    NoterefNotebodySmilInfo nnsi = noterefBodySmilRefs.get(contentref.substring(contentref.indexOf("#") + 1));
                    List<FileAndFragment> fileFragmentList = nnsi.getSmilRefs();
                    String contentBodyId = nnsi.getNotebodyId();
                    contentBodyRef = f.getName() + "#" + contentBodyId;
                    //System.err.println((++counter) + "NotrefSmil=" + smil + ", noterefId=" + smilNoterefId + 
                    //      ", contentRef=" + contentref + ", bodyId=" + contentBodyId + ", smilrefs:" + fileFragmentList.size());
                    List<XMLEvent> xmlEventList = new ArrayList<XMLEvent>();
                    for (FileAndFragment faf : fileFragmentList) {
                        File bodySmil = new File(outputDir, faf.getFile());
                        String bodyFragment = faf.getFragment();
                        xmlEventList.addAll(smilIdEventMap.get(bodySmil).get(bodyFragment));
                    }
                    //System.err.println(xmlEventList.size());
                    smilNoterefIdXMLEventMap.put(smilNoterefId, new Pair<String,List<XMLEvent>>(contentBodyRef, xmlEventList));
                }
                
                // So we finally have everything we need to insert those XMLEvents into this SMIL file.
                // Let's do it!
                TempFile tempFile = new TempFile(smil);
                //System.err.println("Injecting " + smilNoterefIdXMLEventMap.keySet().size() + " into smil " + smil);
                Map<String,String> contentBodySmilBodyMap = smilEventInjector.injectSmilEvents(tempFile.getFile(), smil, smilNoterefIdXMLEventMap);
                //System.err.println("After injection, " + contentBodySmilBodyMap.keySet().size() + " returned.");
                smilContentBodySmilBodyMap.put(smil, contentBodySmilBodyMap);
                tempFile.delete();
            }
            this.checkAbort();
        }
        this.progress(0.65);
        this.checkAbort();
                    
        // STEP 6
        // The SMIL files are almost done. We just need to update ncc:timeInThisSmil, ncc:totalElapsedTime
        // and the body seq dur attribute.
        this.sendMessage(i18n("STEP_RECALC_SMIL_TIMING"), MessageEvent.Type.INFO_FINER);
        SmilFileClockFixer smilFileClockFixer = new SmilFileClockFixer();
        long totalElapsedTime = 0;
        // We reuse the fileset instance from the input fileset although we are modifying the
        // output fileset. Since we haven't renamed any SMIL files this should be OK...
        D202NccFile ncc = (D202NccFile)fileset.getManifestMember();
        for (D202SmilFile smil : ncc.getSpineItems()) {
            URI relative = inputDirUri.relativize(smil.getFile().toURI());
            //URI outputUri = outputDirUri.resolve(relative);
            File outputFile = new File(outputDir, relative.toString());
            TempFile tempFile = new TempFile(outputFile);
            totalElapsedTime += smilFileClockFixer.fix(tempFile.getFile(), outputFile, totalElapsedTime);
            tempFile.delete();
            this.checkAbort();
        }
        this.progress(0.70);
        this.checkAbort();
        
        
        // STEP 7
        // If the note bodies were moved, repoint the content doc to the new SMIL locations
        if (moveFootnote) {
        
            this.sendMessage(i18n("STEP_REPOINT_CONTENT_SMIL_LINKS"), MessageEvent.Type.INFO_FINER);
            // smilContentBodySmilBodyMap contains the mapping between the content document body
            // and the corresponding SMIL body for each SMIL file. Lets start by restructuring
            // the data so we can process each content document in order.
            Map<File,Map<String,String>> contentBodySmilBodyMap = new HashMap<File,Map<String,String>>();
            for (Map<String,String> bodyRefSmilBodyIdMap : smilContentBodySmilBodyMap.values()) {
                for (String bodyRef : bodyRefSmilBodyIdMap.keySet()) {                  
                    File outputFile = new File(outputDir, bodyRef.substring(0, bodyRef.indexOf("#")));
                    String bodyId = bodyRef.substring(bodyRef.indexOf("#")+1);
                    if (!contentBodySmilBodyMap.containsKey(outputFile)) {
                        contentBodySmilBodyMap.put(outputFile, new HashMap<String,String>());
                    }
                    contentBodySmilBodyMap.get(outputFile).put(bodyId, bodyRefSmilBodyIdMap.get(bodyRef));
                }
            }
            // Now we can process this for each content doc.
            ContentDocSmilRepointer contentDocSmilRepointer = new ContentDocSmilRepointer(staxInPool, staxInProperties, staxOutPool, staxOutProperties, staxEvPool);
            for (File contentDoc : contentBodySmilBodyMap.keySet()) {
                TempFile tempFile = new TempFile(contentDoc);
                contentDocSmilRepointer.repoint(tempFile.getFile(), contentDoc, contentBodySmilBodyMap.get(contentDoc));
                tempFile.delete();
            }
            this.progress(0.75);
            this.checkAbort();
        
        }
        
        // STEP 8 & 9
        // Remove the internal DTD subset from the NCC and content doc.
        // Copy all other files to the output folder (i.e. MP3 and CSS).            
        this.sendMessage(i18n("STEP_COPYING_OTHER_FILES"), MessageEvent.Type.INFO_FINER);
        InternalSubsetAndMetaFilter internalSubsetAndMetaFilter = new InternalSubsetAndMetaFilter(staxInPool, staxInProperties, staxOutPool, staxOutProperties, staxEvPool);
        for (FilesetFile filesetFile : fileset.getLocalMembers()) {
            URI relative = inputDirUri.relativize(filesetFile.getFile().toURI());
            //URI outputUri = outputDirUri.resolve(relative);
            File outputFile = new File(outputDir, relative.toString());
            
            if (filesetFile instanceof D202SmilFile ||
                    filesetFile instanceof D202MasterSmilFile) {
                //System.err.println("skipping " + filesetFile);
            } else if (filesetFile instanceof D202TextualContentFile || filesetFile instanceof D202NccFile) {
                TempFile tempFile = new TempFile(outputFile);
                internalSubsetAndMetaFilter.filter(tempFile.getFile(), outputFile, totalElapsedTime);
                tempFile.delete();
            } else {                    
                FileUtils.copyFile(filesetFile.getFile(), outputFile);
            }
            this.checkAbort();
        }
        
        this.progress(1.00);
        this.checkAbort();
    }

	public void error(FilesetFileException ffe) throws FilesetFileException {
		this.sendMessage(ffe);
	}

}
