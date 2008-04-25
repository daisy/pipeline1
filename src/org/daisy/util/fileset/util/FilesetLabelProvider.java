package org.daisy.util.fileset.util;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.daisy.util.fileset.AudioFile;
import org.daisy.util.fileset.D202NccFile;
import org.daisy.util.fileset.D202SmilFile;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.SmilFile;
import org.daisy.util.fileset.Xhtml10File;
import org.daisy.util.fileset.XmlFile;
import org.daisy.util.fileset.Z3986NcxFile;
import org.daisy.util.fileset.Z3986OpfFile;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.exception.FilesetTypeNotSupportedException;
import org.daisy.util.xml.XPathUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Obtain labels describing filesets, and labels for individual members of a fileset.
 * <p>A label is a string of characters representing a summary of the 
 * content of a fileset or a fileset member; concept typically analogous to "title" or "header".</p>
 * <p>On the member level, a label can be obtained through inherency (label carried by the member itself) 
 * or by exherency, ie the member itself is label-less, but 
 * another superordinate member of the fileset carries it.</p>
 * <p>This implementation does not per se support all Fileset and FilesetFile types, but is
 * extended on a need basis.</p>
 * @author Markus Gylling
 */
public class FilesetLabelProvider {
	private Fileset mFileset = null;
	private FilesetType mFilesetType = null;
	private boolean mFilesetIsDTB = false;	
	private Document mNavigationDOM = null;
	private Map mDtbSmilLabelMap = null;  	//A map of labels of smil files in a DTB spine. <URI>,<String> where the key is that files URI and the value is the label
	private String mFilesetTitle =  null;
	private String mFilesetIdentifier =  null;
		
	/**
	 * Constructor.
	 * @param fileset The Fileset instance from which subsequent queries to {@link #getFilesetFileTitle(FilesetFile)} are taken.
	 */	
	public FilesetLabelProvider(Fileset fileset) { 
		mFileset = fileset;
		mFilesetType = fileset.getFilesetType();
		if (FilesetType.DAISY_202 == mFilesetType || FilesetType.Z3986 == mFilesetType) mFilesetIsDTB = true;					
	}

	/**
	 * @return the Fileset registered with this instance
	 */
	public Fileset getFileset() {
		return mFileset;
	}

	/**
	 * Retrieve a label describing an identifier of the intellectual content within the Fileset registered with this instance, or null if none can be found.
	 */
	public String getFilesetIdentifier() {
		if(mFilesetIdentifier==null){
	        if(mFileset.getFilesetType()== FilesetType.Z3986) {
	        	Z3986OpfFile opf = (Z3986OpfFile) mFileset.getManifestMember();
	        	mFilesetIdentifier = opf.getUID();        	
	        }else if(mFileset.getFilesetType()== FilesetType.DAISY_202) {
	        	D202NccFile ncc = (D202NccFile) mFileset.getManifestMember();
	        	mFilesetIdentifier = ncc.getDcIdentifier();        	
	        }else if(mFileset.getFilesetType()== FilesetType.XHTML_DOCUMENT) {
	        	Xhtml10File xht = (Xhtml10File) mFileset.getManifestMember();
	        	mFilesetIdentifier = xht.getIdentifier();        	
	        }
		}
		return mFilesetIdentifier;
	}

	/**
	 * Retrieve a label describing a title of the intellectual content within the Fileset registered with this instance, or null if none can be found.
	 */
	public String getFilesetTitle() {
		if(mFilesetTitle==null){
	        if(mFileset.getFilesetType()== FilesetType.Z3986) {
	        	Z3986OpfFile opf = (Z3986OpfFile) mFileset.getManifestMember();
	        	mFilesetTitle = opf.getMetaDcTitle();        	
	        }else if(mFileset.getFilesetType()== FilesetType.DAISY_202) {
	        	D202NccFile ncc = (D202NccFile) mFileset.getManifestMember();
	        	mFilesetTitle = ncc.getDcTitle();        	
	        }else if(mFileset.getFilesetType()== FilesetType.XHTML_DOCUMENT) {
	        	Xhtml10File xht = (Xhtml10File) mFileset.getManifestMember();
	        	mFilesetTitle = xht.getTitle();        	
	        }
		}
		return mFilesetTitle;
	}


	/**
	 * Retrieve a label describing a creator (aka author) of the intellectual content within the 
	 * Fileset registered with this instance, or null if none can be found.
	 */
	public String getFilesetCreator() {
		//TODO
		return null;
	}
	
	/**
	 * Retrieve a label for inparam filesetfile, or null if none can be found.
	 * @param file the FilesetFile to obtain a label for.
	 */
	public String getFilesetFileTitle(FilesetFile file) throws FilesetFileException {
		try{
			String label = getInherentLabel(file);
			if (null == label) label = getExherentLabel(file);
			return label;
		}catch (Exception e) {
			throw new FilesetFileException(file,e);
		}
	}
	

	/**
	 * Obtain an inherent label - carried by the file itself 
	 */
	private String getInherentLabel(FilesetFile file) {

		/*
		 * Get the Daisy 2.02 smil meta field.
		 * This could be disabled, to use same exherent
		 * smil label getter as in Z3986
		 */		
		if(file instanceof D202SmilFile ) return ((D202SmilFile) file).getMetaTitle();
		

		/*
		 * TODO add other inherents here...
		 */

		return null;
	}

	/**
	 * Obtain an exherent label - carried by a referencing fileset member 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws FilesetTypeNotSupportedException 
	 */
	private String getExherentLabel(FilesetFile file) throws ParserConfigurationException, SAXException, IOException, FilesetTypeNotSupportedException {
		if(file instanceof SmilFile && mFilesetIsDTB) {
			return getDtbSmilExherentLabel((SmilFile)file);
		}
		
		if(file instanceof AudioFile && mFilesetIsDTB) {
			String label = null;
			SmilFile referer = getFirstSmilReferenceTo(file);
			if(null!=referer){
				label = getInherentLabel(referer);
				if(null==label) {
					label = getExherentLabel(referer);
				}
			}	
			return label;
		}
		
		/*
		 * TODO add other exherents here...
		 */
		
		return null;
	}
	

	/**
	 * @return the smilfile in a DTB presentation that first references inparam media object.
	 * If no Smil file references inparam file, null is returned.
	 * @throws FilesetTypeNotSupportedException 
	 */
	private SmilFile getFirstSmilReferenceTo(FilesetFile file) throws FilesetTypeNotSupportedException {

		Collection spine = FilesetSpineProvider.getSmilSpine(mFileset);
		if(null==spine)return null; 
		
		URI uri = file.getFile().toURI();				
		for (Iterator iter = spine.iterator(); iter.hasNext();) {
			SmilFile sf = (SmilFile) iter.next();
			if (sf.getReferencedLocalMember(uri)!=null) {
				return sf;
			}
		}
				
		return null;
	}

	/**
	 * Retrieve an exherent label for a SmilFile in a DTB
	 * @return the label, or null if no label was found
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws FilesetTypeNotSupportedException 
	 */
	private String getDtbSmilExherentLabel(SmilFile smilFile) throws ParserConfigurationException, SAXException, IOException, FilesetTypeNotSupportedException {
		if(null==mDtbSmilLabelMap) {
			//<URI>,<String> where the value is the label			
			populateDtbSmilLabelMap();
		}
		return (String)mDtbSmilLabelMap.get(smilFile.getFile().toURI());
	}

	@SuppressWarnings("unchecked")
	private void populateDtbSmilLabelMap() throws ParserConfigurationException, SAXException, IOException, FilesetTypeNotSupportedException {
		mDtbSmilLabelMap = new HashMap();
		//go through the DTB fileset and create the
		//<URI>,<String> map for each	
		Collection spine = FilesetSpineProvider.getSmilSpine(mFileset);
		for (Iterator iter = spine.iterator(); iter.hasNext();) {
			SmilFile sf = (SmilFile) iter.next();
			String label = getDtbNavigationLabelForSmilFile(sf);
			if(null!=label) {
				mDtbSmilLabelMap.put(sf.getFile().toURI(), label);
			}
		}
	}

	
	private String getDtbNavigationLabelForSmilFile(SmilFile sf) throws ParserConfigurationException, SAXException, IOException {
		/*
		 * store the ncc or ncx as a DOM, run xpath getters
		 * to retrieve the node which carries a ref to the smilfile.
		 * We rely on NCC/NCX since it is a least common denominator for
		 * different types of DTBs and specs. For zed DTBs this might however
		 * break, since there is no assumed smilfile resolution as in 2.02.
		 */
		
		if(mNavigationDOM==null) {
			XmlFile nav = getDTBNavigationMember();
			mNavigationDOM = nav.asDocument(false);  //namespace aware off
		}
		
		Node node = null;
		if(mFileset.getFilesetType() == FilesetType.DAISY_202) {			
			node = XPathUtils.selectSingleNode(mNavigationDOM.getDocumentElement(), "./body/*/a[contains(@href,'"+ sf.getName() +"')]");			
		}else if(mFileset.getFilesetType() == FilesetType.Z3986) {
			Node contentElem = XPathUtils.selectSingleNode(mNavigationDOM.getDocumentElement(), "./navMap/navPoint/content[contains(@src,'"+ sf.getName() +"')]");
			if(contentElem!=null){
				Node navPoint = contentElem.getParentNode(); 
				node = XPathUtils.selectSingleNode(navPoint, "./navLabel/text");
			}
		}	 	
				
		if(node!=null) return node.getTextContent();
		return null;
	}

	
	private XmlFile getDTBNavigationMember() {
		for (Iterator iter = mFileset.getLocalMembers().iterator(); iter.hasNext();) {
			FilesetFile f = (FilesetFile) iter.next();
			if(f instanceof D202NccFile||f instanceof Z3986NcxFile) {
				return (XmlFile)f;
			}			
		}
		return null;
	}

}