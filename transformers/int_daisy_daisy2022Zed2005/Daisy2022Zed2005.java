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
package int_daisy_daisy2022Zed2005;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.AudioFile;
import org.daisy.util.fileset.CssFile;
import org.daisy.util.fileset.D202MasterSmilFile;
import org.daisy.util.fileset.D202NccFile;
import org.daisy.util.fileset.D202SmilFile;
import org.daisy.util.fileset.D202TextualContentFile;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetException;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.FilesetFileFactory;
import org.daisy.util.fileset.FilesetImpl;
import org.daisy.util.fileset.GifFile;
import org.daisy.util.fileset.ImageFile;
import org.daisy.util.fileset.JpgFile;
import org.daisy.util.fileset.Mp2File;
import org.daisy.util.fileset.Mp3File;
import org.daisy.util.fileset.OpfFile;
import org.daisy.util.fileset.PngFile;
import org.daisy.util.fileset.SmilClock;
import org.daisy.util.fileset.SmilFile;
import org.daisy.util.fileset.WavFile;
import org.daisy.util.fileset.Z3986DtbookFile;
import org.daisy.util.fileset.Z3986NcxFile;
import org.daisy.util.fileset.Z3986ResourceFile;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.XSLTException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * Creates a Z3986-2005 DTB fileset from a Daisy 2.02 DTB fileset.
 * 
 * Current known assumptions and limitations:
 * <ul>
 *  <li></li>
 *  <li></li>
 *  <li></li>
 *  <li></li>
 *  <li></li>
 * </ul>
 * @author Brandon Nelson
 * @author Markus Gylling
 */
public class Daisy2022Zed2005 extends Transformer {

    private static final String XSLT_FACTORY = "net.sf.saxon.TransformerFactoryImpl";
        
    Map manifestItems = new HashMap(); //<FileAbsolutePath>,<FilesetFile>
    
    // Main progress values
    private static final double FILESET_DONE = 0.01;
    private static final double SMIL_DONE = 0.65;
    private static final double DTBOOK_DONE = 0.68;
    private static final double NCX_DONE = 0.85;
    private static final double OPF_DONE = 0.85;
    private static final double COPY_DONE = 0.99;
        
    private File outputDir = null;
    
    public Daisy2022Zed2005(InputListener inListener, Set eventListeners, Boolean isInteractive) {
        super(inListener, eventListeners, isInteractive);
    }

    protected boolean execute(Map parameters) throws TransformerRunException {
        String inparamNccPath = (String)parameters.remove("ncc");
        String inparamOutDir = (String)parameters.remove("outDir");
        String inparamCssPath = (String)parameters.remove("css");
        String inparamResourceFilePath = (String)parameters.remove("resourcefile");
                      
        try {
// Build input fileset
            this.sendMessage(Level.INFO, i18n("BUILDING_FILESET"));
            Fileset fileset = this.buildFileSet(inparamNccPath);
            D202NccFile d202_ncc = (D202NccFile) fileset.getManifestMember();
            this.progress(FILESET_DONE);      
                                                           
// Check for properties that should make us abort before even attempting the transform 
            //abortcause: 2.02 DTB with > 1 content doc
            int xhtmlContentDocCount = 0;
            for (Iterator it = fileset.getLocalMembers().iterator(); it.hasNext(); ) {
            	FilesetFile fsf = (FilesetFile)it.next();
                if (fsf instanceof D202TextualContentFile) {              
                	xhtmlContentDocCount ++;	
                }            	
            }            
            if (xhtmlContentDocCount > 1) {
            	//more than one content doc
            	throw new TransformerRunException("more than one XHTML content doc in input DTB - merge first!");
            }	
            
            //abortcause: fileset is part of multivolume
            if(d202_ncc.hasMultiVolumeIndicators()){
            	//rel attrs in body && setInfo with value other than '1 of 1'
            	throw new TransformerRunException("input DTB indicates it is multivolume - merge first!");
            }
                        
            //abortcause: fileset had errors
            if(fileset.hadErrors()) {
            	//since it was built nonvalidating, this is prolly a serious error (malformedness, files missing)
            	String errors=null;
            	for(Iterator i = fileset.getErrorsIterator(); i.hasNext();) {
            		Exception e = (Exception )i.next();
            		errors = errors + "\n" +  e.getMessage();            		
            	}
            	//TODO send to a listener instead and try continuing
            	throw new TransformerRunException("input fileset had " 
            			+ fileset.getErrors().size() 
            			+ " errors: "
            			+ errors
            			);
            }
            
                        
            //TODO abortcause: may be more

                        
// Create output directory
            outputDir = FileUtils.createDirectory(new File(inparamOutDir)); 
            
// If we are going to have dtbook in the output, instantiate a dtbook css to copy along
            
            //set the install provided first
            File sourceCss = new File(this.getTransformerDirectory().getCanonicalPath()+"/resources/dtbook-dmfc-default.css");
            
            //try to get user inparam css, fall back to install provided if fail            
            	if(inparamCssPath!=null) {
            		try{
            		   File temp = new File(inparamCssPath);
            		   if (temp.exists() && temp.canRead()) {
            			   sourceCss = temp;		   
            		   }
            		}catch (Exception e) {
            			
            		}
            	}
                                                                   
            
// get ncc and collect variables that need to be sent as parameters to the XSLTs            
            String nccDcIdentifier = "dc-identifier-unset";
            String nccDcTitle = "dc-title-unset";
                        
            if(d202_ncc.getDcIdentifier()!=null)nccDcIdentifier = d202_ncc.getDcIdentifier();
            if(d202_ncc.getDcTitle()!=null) nccDcTitle = d202_ncc.getDcTitle(); 

// determine names for output DTB files (has to change from *.html etc)            
// smil, audio and image names are copied with names as-they-were

            String dtbookFileName = "dtbook-unset.xml";
            String opfFileName = "opf-unset.opf";
            String ncxFileName = "ncx-unset.ncx";
            
            if(nccDcIdentifier.equals("dc-identifier-unset")) {
            	//ncc identifier was not set, use content doc name for filename
                for (Iterator it = fileset.getLocalMembers().iterator(); it.hasNext(); ) {
                	FilesetFile fsf = (FilesetFile)it.next();
                    if (fsf instanceof D202TextualContentFile) {
                    	dtbookFileName = fsf.getName();
                    	break;
                    }            	
                }            	
            	dtbookFileName = dtbookFileName.substring(0,dtbookFileName.lastIndexOf(".")) + ".xml";             	
            	ncxFileName = dtbookFileName.substring(0,dtbookFileName.lastIndexOf(".")) + ".ncx";
            	opfFileName = dtbookFileName.substring(0,dtbookFileName.lastIndexOf(".")) + ".opf";            	
            }else{
            	//ncc identifier was set, use this for filename
            	dtbookFileName = nccDcIdentifier + ".xml";             	
            	ncxFileName = nccDcIdentifier + ".ncx";
            	opfFileName = nccDcIdentifier + ".opf";
            }	
                                                            
// Iterate through Fileset and create Zed Smil from the 2.02 SMIL input files            

            //remember: the XSLTs are written context unaware, 
            //so they need to get all necessary context info as inparams.
            
            D202SmilFile d202_smil = null;
            long totalElapsedTime = 0; //in milliseconds
            this.sendMessage(Level.INFO, i18n("CREATING_SMIL", ""));
            
            for (Iterator it = fileset.getLocalMembers().iterator(); it.hasNext(); ) {
                FilesetFile fsf = (FilesetFile)it.next();
                if (fsf instanceof D202SmilFile) {
                	d202_smil = (D202SmilFile) fsf;
                	this.createZedSmil(d202_smil, nccDcIdentifier, nccDcTitle, 
                			new SmilClock(totalElapsedTime).toString(SmilClock.FULL), 
                			d202_smil.getCalculatedDuration().toString(SmilClock.FULL),
                			dtbookFileName);
                	totalElapsedTime += d202_smil.getCalculatedDurationMillis();                	              	
                } 
            }
            
            this.progress(SMIL_DONE);
            //now the totalElapsedTime variable equals DTB totaltime
            SmilClock dtbTotalTime = new SmilClock(totalElapsedTime);
            
//5. Iterate again through fileset and create ncx, dtbook, opf
            
            //remember: the XSLTs are written context unaware, 
            //so they need to get all necessary context info as inparams.
            
            Set filesToCopy = new HashSet();
            
            for (Iterator it = fileset.getLocalMembers().iterator(); it.hasNext();) {
                FilesetFile fsf = (FilesetFile)it.next();
                if (fsf instanceof D202NccFile) {
                	D202NccFile nccFile = (D202NccFile) fsf;
                	//create ncx
                	this.sendMessage(Level.INFO, i18n("CREATING_NCX", ncxFileName));                            	
                	this.createZedNcx(nccFile,nccDcIdentifier,ncxFileName);                	
                    this.progress(NCX_DONE);                                       
                } else if (fsf instanceof D202TextualContentFile) {
                    this.sendMessage(Level.INFO, i18n("CREATING_DTBOOK", dtbookFileName));
                	D202TextualContentFile xhtFile = (D202TextualContentFile) fsf;
                	this.createZedDtbook(xhtFile,nccDcIdentifier,nccDcTitle,sourceCss.getName(), dtbookFileName);   
                    this.progress(DTBOOK_DONE);
                } else if (fsf instanceof AudioFile) {
                    filesToCopy.add(fsf);
                } else if (fsf instanceof ImageFile) {
                    filesToCopy.add(fsf);
                } else if (fsf instanceof CssFile) { 
                	//dont add this to filesToCopy since it is an xhtml css
                } else if (fsf instanceof D202SmilFile) {
                    // smil already done                    
                } else if (fsf instanceof D202MasterSmilFile) {
                    // ignore master smil completely
                } else {
                    System.err.println("what is this? :: " + fsf.getName());
                }
            }//for
            
            
//after transforming input fileset, create add zed files that dont have a 2.02 counterpart 
            //TODO copy in a resource file, either default or per inparam
            
            
            //copy in dtbook css, using sourceCss set above 
            if (xhtmlContentDocCount > 0) { //means that dtbook will be in output            	
            	File cssOut = new File(outputDir,sourceCss.getName());
            	FileUtils.copy(sourceCss,cssOut);
            	manifestItems.put(cssOut.getAbsolutePath(),FilesetFileFactory.newCssFile(cssOut.toURI()));
            }
            
//copy files that move over unabridged
            this.copyFiles(filesToCopy, fileset);            
            this.progress(COPY_DONE);

//finally, the output topology is getting stable, create the opf
                                    
            this.sendMessage(Level.INFO, i18n("CREATING_OPF", opfFileName));                            	
        	this.createZedOpf(d202_ncc,dtbTotalTime,this.getTopLevelMediaTypes(),opfFileName);
            this.progress(OPF_DONE); 
                               
        } catch (FilesetException e) {            
            throw new TransformerRunException(e.getMessage(), e);
        } catch (CatalogExceptionNotRecoverable e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (XSLTException e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (IOException e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (ParserConfigurationException e) {
        	throw new TransformerRunException(e.getMessage(), e);
		} catch (SAXException e) {
			throw new TransformerRunException(e.getMessage(), e);		}
		System.err.println("----------> zedification done.");
        return true;
        
    }

    private void createZedSmil(D202SmilFile smil, String uid, String title, String totalElapsedTime, String timeinThisSmil, String dtbookFileName) throws CatalogExceptionNotRecoverable, XSLTException, FilesetException {
      File smilOut = new File(outputDir, smil.getName());
      File xsltFile = new File(this.getTransformerDirectory(), "d202smil_Z2005smil.xsl");
      
      Map parameters = new HashMap();
      parameters.put("uid", uid);
      parameters.put("title", title);
      parameters.put("totalElapsedTime",totalElapsedTime);
      parameters.put("timeinThisSmil",timeinThisSmil);
      parameters.put("dtbookFileName",dtbookFileName);
      
      File inFile = (File)smil;      
      Stylesheet.apply(inFile.getAbsolutePath(), xsltFile.getAbsolutePath(), smilOut.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
      
      manifestItems.put(smilOut.getAbsolutePath(), FilesetFileFactory.newZ3986SmilFile(smilOut.toURI()));
      
    }
    
    private void createZedNcx(D202NccFile ncc, String uid, String ncxFileName) throws CatalogExceptionNotRecoverable, XSLTException, FilesetException {
        File ncxOut = new File(outputDir, ncxFileName);
        File xsltFile = new File(this.getTransformerDirectory(), "d202ncc_Z2005ncx.xsl");
        
        Map parameters = new HashMap();
        parameters.put("uid", uid);
                        
        File inFile = (File)ncc;      
        Stylesheet.apply(inFile.getAbsolutePath(), xsltFile.getAbsolutePath(), ncxOut.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
    
        manifestItems.put(ncxOut.getAbsolutePath(), FilesetFileFactory.newZ3986NcxFile(ncxOut.toURI()));
        
    }
    
    private void createZedDtbook(D202TextualContentFile xhtml, String uid, String title, String cssUri, String dtbookFileName) throws CatalogExceptionNotRecoverable, XSLTException, FilesetException {
        File dtbookOut = new File(outputDir, dtbookFileName);
        File xsltFile = new File(this.getTransformerDirectory(), "d202xhtml_Z2005dtbook.xsl");
        
        Map parameters = new HashMap();
        parameters.put("uid", uid);
        parameters.put("title", title);
        parameters.put("cssUri", cssUri);
                        
        File inFile = (File)xhtml;            
        Stylesheet.apply(inFile.getAbsolutePath(), xsltFile.getAbsolutePath(), dtbookOut.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
                
        manifestItems.put(dtbookOut.getAbsolutePath(), FilesetFileFactory.newZ3986DtbookFile(dtbookOut.toURI()));
        
    }
    
    private void createZedOpf(D202NccFile ncc, SmilClock dtbTotalTime, String dtbMultimediaContent, String opfFileName) throws XSLTException, IOException, ParserConfigurationException, SAXException, FilesetException {
    	//the xslt creates metadata, the spine, and adds the smilfiles to manifest
    	//this java object adds the other stuff to manifest (in .finalizeManifest).
    	
    	File opfOut = new File(outputDir, opfFileName);
        File xsltFile = new File(this.getTransformerDirectory(), "d202ncc_Z2005opf.xsl");
        
        Map parameters = new HashMap();
        parameters.put("dtbTotalTime", dtbTotalTime.toString(SmilClock.FULL));
        parameters.put("dtbMultimediaContent", dtbMultimediaContent);
                                
        File inFile = (File)ncc;      
        Stylesheet.apply(inFile.getAbsolutePath(), xsltFile.getAbsolutePath(), opfOut.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
                              
        manifestItems.put(opfOut.getAbsolutePath(), FilesetFileFactory.newOpfFile(opfOut.toURI()));

        //run finalize after manifestItems.put so that the opf gets included in the manifest itemlist
        finalizeManifest(opfOut);
        
    }
    
    private void finalizeManifest(File unfinishedOpf) throws IOException, ParserConfigurationException, SAXException {
//    	the xslt has already added the smilfiles to manifest
// 		note: since this function relativizes item URIs, 
//    	opf and its friends must be placed in final form in relation to eachoter
//		ie cant have stuff in temp locations.    	

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setExpandEntityReferences(false);
        DocumentBuilder parser;
        parser = factory.newDocumentBuilder();
        parser.setEntityResolver(CatalogEntityResolver.getInstance());                
		Document opfDom = parser.parse(unfinishedOpf);		
		NodeList nl = opfDom.getElementsByTagName("manifest");
		Element manifest = (Element)nl.item(0); 
		
		int k = 1;
		for (Iterator i = manifestItems.keySet().iterator(); i.hasNext(); ) {		  
		  FilesetFile fsf = (FilesetFile)manifestItems.get(i.next());
		  String mime = fsf.getMimeType();		
		  
		  URI opfURI = unfinishedOpf.getParentFile().toURI();
		  
		  if (!(fsf instanceof SmilFile)) { //since xslt added smilfiles already
			  
		    Element item = opfDom.createElement("item");
		    //set the mime
		    item.setAttribute("mime-type", mime);
		    //set the href
		    URI itemURI = new File(fsf.getFile().getAbsolutePath()).toURI();		    
		    URI relative = opfURI.relativize(itemURI);
		    item.setAttribute("href", relative.toString());
		    //set an id
		    String id = null;
		    if(fsf instanceof Z3986NcxFile) {
		    	id="ncx";
		    }else if(fsf instanceof OpfFile) {
			    	id="opf";	
		    }else if(fsf instanceof Z3986ResourceFile) {
		    	id="resource";
		    }else if(fsf instanceof Z3986DtbookFile) {
		    	id="dtbook";
		    }else{
		    	id="dmfc_"+k;
		    }
		    item.setAttribute("id", id);		    
		    manifest.appendChild(item);		    
		    k++;
		  }
		}
		
    	//save the dom
		
        OutputFormat outputFormat = new OutputFormat(opfDom);
        outputFormat.setPreserveSpace(false);
        outputFormat.setIndenting(true);
        outputFormat.setIndent(3);
        outputFormat.setOmitComments(false);
        outputFormat.setEncoding("utf-8");        
        OutputStream fout= new FileOutputStream(unfinishedOpf);
        XMLSerializer serializer = new XMLSerializer(fout, outputFormat);
        serializer.serialize(opfDom);
    	
    }
            
    /**
     * Copy the rest of the book (audio and images).
     * @param files the set of files to copy
     * @throws IOException
     * @throws FilesetException 
     */
    private void copyFiles(Set files, Fileset fileset) throws IOException, FilesetException {
        int fileNum = files.size();
        int fileCount = 0;
        for (Iterator it = files.iterator(); it.hasNext(); ) {
            fileCount++;
            FilesetFile fsf = (FilesetFile)it.next();
            Object[] params = {new Integer(fileNum), new Integer(fileCount), fsf.getName()};
            this.sendMessage(Level.INFO, i18n("COPYING_FILE", params));
            URI relativeURI = fileset.getRelativeURI(fsf);
            File out = new File(outputDir.toURI().resolve(relativeURI));
            FileUtils.copy(fsf.getFile(), out);
            if(fsf instanceof Mp3File) {
              manifestItems.put(out.getAbsolutePath(), FilesetFileFactory.newMp3File(out.toURI()));
            }else if(fsf instanceof Mp2File) {
                manifestItems.put(out.getAbsolutePath(), FilesetFileFactory.newMp2File(out.toURI()));                
            }else if(fsf instanceof WavFile) {
              manifestItems.put(out.getAbsolutePath(), FilesetFileFactory.newWavFile(out.toURI()));  
            }else if (fsf instanceof JpgFile) {
              manifestItems.put(out.getAbsolutePath(), FilesetFileFactory.newJpgFile(out.toURI())); 
            }else if (fsf instanceof PngFile) {
                manifestItems.put(out.getAbsolutePath(), FilesetFileFactory.newPngFile(out.toURI())); 
            }else if (fsf instanceof GifFile) {
                manifestItems.put(out.getAbsolutePath(), FilesetFileFactory.newGifFile(out.toURI())); 
            }else{
              manifestItems.put(out.getAbsolutePath(), out); 
            }                        
            this.progress(0.85 + (0.99-0.85)*((double)fileCount/fileNum));
        }
    }
    
    private Fileset buildFileSet(String manifest) throws FilesetException {
        return new FilesetImpl(FilenameOrFileURI.toFile(manifest).toURI(), false, true);
    }
    
    private String getTopLevelMediaTypes() {
    	//use manifestItems
    	return "audio,text,image"; //TODO
    }
}


///**
//* Creates the NCC.
//* @param opf the z3986 package file
//* @param totalElapsedTime the total time of the book in milliseconds
//* @throws CatalogExceptionNotRecoverable
//* @throws XSLTException
//* @throws XMLStreamException
//* @throws IOException
//*/
//private void createNcc(File opf, long totalElapsedTime) throws CatalogExceptionNotRecoverable, XSLTException, XMLStreamException, IOException {
// Map parameters = new HashMap();
// parameters.put("baseDir", outputDir.toURI());
// parameters.put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
// File nccFile = new File(outputDir, "ncc.html");
// File createNcc = new File(this.getTransformerDirectory(), "ncc-create.xsl");
// File nccRemoveDupes = new File(this.getTransformerDirectory(), "ncc-remove-dupes.xsl");
// File nccClean = new File(this.getTransformerDirectory(), "ncc-clean.xsl");
// 
// File temp = TempFile.create();
// File temp2 = TempFile.create();
// File temp3 = TempFile.create();
// 
// // Step 1: Create the ncc by looking through the smil files and identifying headings in the content document
// Stylesheet.apply(opf.getAbsolutePath(), createNcc.getAbsolutePath(), temp.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
// 
// // Step 2: Remove duplicate entries in the ncc (step 1 created two entries for a heading containing two sync points)
// Stylesheet.apply(temp.getAbsolutePath(), nccRemoveDupes.getAbsolutePath(), temp2.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
// 
// // Step 3: add class="title" to first h1 and fix some metadata
// Stylesheet.apply(temp2.getAbsolutePath(), nccClean.getAbsolutePath(), temp3.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
// 
// // Step 4: insert ncc:totalTime and indent file
// NccFixer nccFixer = new NccFixer();
// nccFixer.fix(temp3, nccFile, totalElapsedTime);
// 
// temp.delete();
// temp2.delete();
// temp3.delete();
//}

///**
//* Creates the SMIL files.
//* @param opf the z3986 package file
//* @param dtbook the dtbook document
//* @param ncx the ncx document
//* @return the total time of the book (in milliseconds)
//* @throws XMLStreamException
//* @throws IOException
//* @throws CatalogExceptionNotRecoverable
//* @throws XSLTException
//* @throws FilesetException
//*/
//private long createSmils(OpfFile opf, File dtbook, File ncx) throws XMLStreamException, IOException, CatalogExceptionNotRecoverable, XSLTException, FilesetException {
// SmilFileClockFixer smilClockFixer = new SmilFileClockFixer();
// long totalElapsedTime = 0;
// File smil2smil = new File(this.getTransformerDirectory(), "smil2smil.xsl");
// File smilAddTitle = new File(this.getTransformerDirectory(), "smilAddTitle.xsl");
// 
// Map parameters = new HashMap();
// parameters.put("xhtml_document", contentXHTML);
// parameters.put("dtbook_document", dtbook.toURI());
// parameters.put("baseDir", inputDir.toURI());
// parameters.put("ncx_document", ncx.toURI());
// parameters.put("add_title", "true");
// 
// // For each SMIL file
// Collection spineItems = opf.getSpineItems();      
// int smilNum = spineItems.size();
// int smilCount = 0;
// for (Iterator it = spineItems.iterator(); it.hasNext(); ) {
//     smilCount++;
//     Z3986SmilFile smilZed = (Z3986SmilFile)it.next();
//     File smil202 = new File(outputDir, smilZed.getName());
//     Object[] params = {new Integer(smilNum), new Integer(smilCount), smil202.getName()};
//     this.sendMessage(Level.INFO, i18n("SMIL", params));            
//     
//     File temp1 = TempFile.create();
//     File temp2 = TempFile.create();
//     
//     // Step 1: Flatten the Z3986 smil to a Daisy 2.02 compatible one 
//     Stylesheet.apply(smilZed.getFile().getAbsolutePath(), smil2smil.getAbsolutePath(), temp1.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
//     
//     // Step 2: Add the smil clip that reads the <h1 class="title"> to the first smil
//     Stylesheet.apply(temp1.getAbsolutePath(), smilAddTitle.getAbsolutePath(), temp2.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
//     
//     // Step 3: Update the ncc:timeInThisSmil, ncc:totalElapsedTime and the dur attribute of the main seq
//     totalElapsedTime += smilClockFixer.fix(temp2, smil202, totalElapsedTime, null);
//     
//     temp1.delete();
//     temp2.delete();
//     parameters.put("add_title", "false");
//     this.progress(FILESET_DONE + (SMIL_DONE-FILESET_DONE)*((double)smilCount/smilNum));
// }
// 
// return totalElapsedTime;
//}
//
///**
//* Creates the XHTML content document.
//* @param dtbook the DTBook document
//* @throws CatalogExceptionNotRecoverable
//* @throws XSLTException
//* @throws IOException
//*/
//private void createXhtml(File dtbook) throws CatalogExceptionNotRecoverable, XSLTException, IOException {
// File xhtmlOut = new File(outputDir, contentXHTML);
// File xsltFile = new File(this.getTransformerDirectory(), "dtbook2xhtml.xsl");
// Map parameters = new HashMap();
// parameters.put("filter_word", "yes");
// parameters.put("baseDir", inputDir.toURI());
// 
// // Step 1: Convert the DTBook to XHTML
// Stylesheet.apply(dtbook.getAbsolutePath(), xsltFile.getAbsolutePath(), xhtmlOut.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
// 
// // Step 2: Insert a stylesheet
// FileUtils.copy(new File(this.getTransformerDirectory(), "default.css"), new File(outputDir, "default.css"));
//}
