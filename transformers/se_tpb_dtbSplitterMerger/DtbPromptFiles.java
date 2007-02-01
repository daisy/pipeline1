/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2006  Daisy Consortium
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

package se_tpb_dtbSplitterMerger;
/*
 * 
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * DtbPromptFiles major task is to give access to the 
 * smil and audio prompt files manifest stored in 
 * an xml document.
 * 
 * @author Piotr Kiernicki
 */
/*
 * 2005-08-31 PK
 * Added a check whether a language is supported in the prompt manifest.
 * If not, the language of prompts defaults to English.
 */
public class DtbPromptFiles {
    
    private HashMap smilSet = new HashMap();
    private HashMap audioSet = new HashMap();
        
    private String lang = null;
    private final static String DEFAULT_PROMPT_LANG = "en";
    private final static String SMIL_FILE_ELEMENT_NAME = "smilFile";
    private final static String AUDIO_FILE_ELEMENT_NAME = "audioFile";
    
    public final static String TITLE_SMIL_FILE_NAME = "dtbsm_title.smil";
    
    private DtbTransformationReporter reportGenerator = null;
    private Document promptManifestDoc = null;
    
    public DtbPromptFiles(File promptFilesXmlManifest, DtbTransformationReporter reportGen, String language) throws XmlParsingException, IOException{
        this.reportGenerator = reportGen;

        DtbParsingInitializer pInit = new DtbParsingInitializer(this.reportGenerator);
        //retrieve the DOM document with prompt files 
        this.promptManifestDoc = pInit.parseDocWithDOM(promptFilesXmlManifest);
        Collection availableLanguages = new ArrayList();
        NodeList langSets = this.promptManifestDoc.getElementsByTagName("set");
        for(int i=0; i<langSets.getLength(); i++){
            Element setElem = (Element)langSets.item(i);
            String lang = setElem.getAttribute("lang");
            if(lang!=null){
                availableLanguages.add(lang);
            }
        }
        
        if(language!=null && availableLanguages.contains(language)){
            this.lang = language;
        }else{
            this.lang = DtbPromptFiles.DEFAULT_PROMPT_LANG;//default value
        }
        
        this.setPromtCollections(promptFilesXmlManifest);
            
    }
    
    
    private void setPromtCollections(File promptFilesXmlManifest) throws IOException{
        
        //retrive the manifest directory
        String promptFilesDirPath = null;
        //set the directory with prompt files 
        promptFilesDirPath = promptFilesXmlManifest.getParentFile().getCanonicalPath();

        
        //fill the smil and audio collections
        Element setElement = this.promptManifestDoc.getElementById(this.lang);
        NodeList volumeList = setElement.getElementsByTagName("volume");
        
        for(int i=0; i<volumeList.getLength(); i++){
            
            Element volumeElement = (Element)volumeList.item(i);
            //add smil file to the smil collection
            NodeList smilList = volumeElement.getElementsByTagName(DtbPromptFiles.SMIL_FILE_ELEMENT_NAME);
            //we know there is only one file here as defined in dtd/promptFiles.dtd
            Element smilElement = (Element)smilList.item(0);
            
            String smilFileRelativePath = smilElement.getAttribute("src");
            File smilFile = new File(promptFilesDirPath + File.separator + smilFileRelativePath);
            
            this.smilSet.put(new Integer(i+1), smilFile);
            
            //add audio file to the audio collection            
            NodeList audioList = volumeElement.getElementsByTagName(DtbPromptFiles.AUDIO_FILE_ELEMENT_NAME);
            //we know there is only one file here as defined in dtd/promptFiles.dtd
            Element audioElement = (Element)audioList.item(0);
            
            String audioFileRelativePath = audioElement.getAttribute("src");
            File audioFile = new File(promptFilesDirPath + File.separator + audioFileRelativePath);
            
            this.audioSet.put(new Integer(i+1), audioFile);         
            
        }

    }
    
    public File getSmilPromt(int volNr){
        File smil = (File)this.smilSet.get(new Integer(volNr));
        return smil;
    }
    
    public String getSmilParId(int volNr) throws XmlParsingException{
        String audioId = "";
        
        File smil = this.getSmilPromt(volNr);
        DtbParsingInitializer parseInit = new DtbParsingInitializer(this.reportGenerator);
        
        Document smilDoc = parseInit.parseDocWithDOM(smil);
        
        NodeList audioList = smilDoc.getElementsByTagName("par");
        
        Element audioElement = (Element)audioList.item(0);//there should be only one
        
        audioId = audioElement.getAttribute("id");
        
        return audioId;
    }
    
    public HashMap getSmilSet(int volumeSetSize){
        return this.smilSet;
    }
    
    public HashMap getAudioSet(int volumeSetSize){
        return this.audioSet;
    }
    
}
