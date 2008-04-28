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

package se_tpb_dtbSplitterMerger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.daisy.util.file.FileUtils;
import org.daisy.util.fileset.util.FilesetRegex;
import org.w3c.dom.Document;

/**
 * 
 * @author Piotr Kiernicki
 */
@SuppressWarnings("deprecation")
public class DtbSerializer {

	public static final String DEFAULT_ENCODING = "utf-8";;

	/**
	 * <p>
	 * Serielizes a document to a file. The file is being encoded in accordance with 
	 * the value given in the xml declaration. If no encoding is found then "utf-8" is used. 
	 * </p>
	 * @throws IOException 
	 * 
	 */
	public static File serializeDocToFile(Document doc, String outputFilePath) throws IOException{
		
		File outputFile = new File(outputFilePath);
		if(outputFile.exists()){
			//just in case the user runs DTBSM with the same output URI
			outputFile.delete();
		}		

		FileOutputStream fos = null;
		OutputStreamWriter osWriter = null;
		try {
			String encoding = doc.getXmlEncoding();
			if(encoding==null){
			    encoding = DtbSerializer.DEFAULT_ENCODING;
			}
			boolean prettyIndenting= true;
			
			OutputFormat of = new OutputFormat(doc, encoding, prettyIndenting);
			
			XMLSerializer ser = new XMLSerializer(of);
			//Create a new (empty) file on disk in the target directory
			fos = new FileOutputStream(outputFile);
			
			if(encoding!=null){
				osWriter = new OutputStreamWriter(fos, encoding);
				ser.setOutputCharStream(osWriter);	
			}else{
				osWriter = new OutputStreamWriter(fos);
				ser.setOutputCharStream(osWriter);
			}
			//Serialize our document into the output stream held by the xml-serializer
			ser.serialize(doc);
		}catch(IOException e) {
			throw e;
		}finally{
			try {
				fos.close();
				osWriter.close();
			} catch (IOException e) {
				throw e;
			}
		}

		return outputFile;		
	}
	/**
	 * 
	 * <p>
	 * Serielizes a document to a file. The file is being encoded in accordance with 
	 * the value given in the xml declaration. If no encoding is found then "utf-8" is used. 
	 * </p>
	 * The method covers for some Daisy 2.02 readers related problems:
	 * <ol>
	 * <li> 
	 * navigation point line breaks
	 * </li>
	 * <li>
	 * order of attributes in meta elements
	 * </li>
	 * </ol>
	 * <p>
	 * 
	 * Ad. 1.<br/>
	 * Some Daisy readers require that each navigation point in an ncc document 
	 * constitues a single line in html code.<br/><br/>
	 * 
	 * Such readers require also CRLF line break markers.<br/><br/>
	 * 
	 * The input navigation file document uses LF as line break markers, 
	 * which formatNavPointsLineBreaks replaces with LFCR in such a manner 
	 * that each navigation point constitues a single line in html code of an ncc.<br/><br/>
	 * 
	 * In other kinds of documents it just replaces LF with CRLF.
	 * </p>
	 * <p>
	 * 
	 * Ad. 2.<br/>
	 * PlexTalk Daisy reader requires a specific order of attributes,
	 * <ul>
	 * <li>
	 * in meta elements: - name="" content="" 
	 * </li>
	 * <li>
	 * in smil/audio elements: src="" clip-begin="" clip-end="" id=""
	 * </li>
	 * <ul> 
	 * </p>
	 */
	public static File serializeDaisy202DocToFile(Document doc, String outputFilePath) throws IOException{
		
			String _encoding = doc.getXmlEncoding();

			if(_encoding==null){
				_encoding = DtbSerializer.DEFAULT_ENCODING;
			}
			File _file = DtbSerializer.serializeDocToFile(doc, outputFilePath);
			if(FilesetRegex.getInstance().matches(FilesetRegex.getInstance().FILE_NCC, _file.getName())){
				DtbSerializer.formatNavPointsLineBreaks(_file, _encoding);
			}
			DtbSerializer.formatPlexTalkAttributesOrder(_file, _encoding);
			return _file;
		}


	private static void formatNavPointsLineBreaks(File navigationFile, String encoding) throws IOException{
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(navigationFile), encoding));
		
		File tmpOutputFile = new File(navigationFile.getAbsolutePath()+"_tmp");
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmpOutputFile), encoding));
		
		String line = br.readLine();

		StringBuffer sBuffer = new StringBuffer();
		if(FilesetRegex.getInstance().matches(FilesetRegex.getInstance().FILE_NCC, navigationFile.getName())){
			//handle ncc
			boolean isInsideNavPoint = false;
			while(line!=null){
				String l = line.toLowerCase();
			
				if((l.indexOf("<h")!=-1 && (l.indexOf("<html")==-1) && l.indexOf("<head")==-1)
											|| l.indexOf("<span")!=-1){
					isInsideNavPoint = true;
				}else if(l.indexOf("</h")!=-1||l.indexOf("</span")!=-1){
					isInsideNavPoint = false;
				}
			
				if(isInsideNavPoint){
					sBuffer.append(line.trim());
					if(!l.trim().endsWith(">")){
						sBuffer.append(" ");
					}
				}else{
					sBuffer.append(line.trim());
					sBuffer.append("\r\n");
					bw.write(sBuffer.toString());
					sBuffer.setLength(0);
				}
				line = br.readLine();
				
			}//move to the next line in the ncc
			
		}else{
			//handle non ncc documents
			while(line!=null){
				sBuffer.append(line);
				sBuffer.append("\r\n");
			
				line = br.readLine();	
				bw.write(sBuffer.toString());
				sBuffer.setLength(0);
			}//move to the next line in the full text
		
		}
		
		br.close();
		//BufferedWriter bw = new BufferedWriter(new FileWriter(navigationFile));
		//bw.write(sBuffer.toString());
		bw.flush();
		bw.close();
		FileUtils.copy(tmpOutputFile,navigationFile); 
		tmpOutputFile.delete();

	}

	private static void formatPlexTalkAttributesOrder(File file, String encoding ) throws IOException {
			
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
			
			File tempOutputFile = new File(file.getAbsolutePath()+"__tmp");
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempOutputFile), encoding));

			String line = br.readLine();
			StringBuffer elemStringBuffer = new StringBuffer();
	
			boolean elementComplete = false;
			boolean buildingElement = false;
			while(line!=null){
				String trimmedLine = line.trim();
				//1. RETRIEVE A meta OR audio ELEMENT
				if((trimmedLine.startsWith("<meta") || trimmedLine.startsWith("<audio")) 
					&& trimmedLine.endsWith("/>")){
					//a line with a complete meta or audio element
					elemStringBuffer.append(line);
					elementComplete = true;
				}else if((trimmedLine.startsWith("<meta") || trimmedLine.startsWith("<audio"))
							&& !trimmedLine.endsWith("/>")){
					//a line that starts meta or audio element
					elemStringBuffer.append(line + " ");
					buildingElement = true;
					line = br.readLine();
					continue;
						
				}else if(buildingElement){
					elemStringBuffer.append(trimmedLine);
					if(trimmedLine.endsWith("/>")){
						//a line that ends meta or audio element
						elementComplete = true;
						buildingElement = false;
					}else{
						//a line with a mid part of meta or audio element
						elemStringBuffer.append(" ");
						line = br.readLine();
						continue;
					}
				}
				//2. MOVE THE audio/@src OR meta/@name ATTRIBUTE TO THE FIRST POSITION
				if(elementComplete){
					int srcAttStart = elemStringBuffer.indexOf("src=");
					int nameAttrStart = elemStringBuffer.indexOf("name=");
					int attrStart = -1;
					if(srcAttStart!=-1){
						attrStart = srcAttStart;
					}else if(nameAttrStart!=-1){
						attrStart = nameAttrStart;
					}
					if(attrStart!=-1){

						String attrElemTrail = elemStringBuffer.substring(attrStart);
						int attrLength = attrElemTrail.indexOf(" ");
						if(attrLength==-1){
							attrLength = attrElemTrail.indexOf("/>");
						}
						String attr = elemStringBuffer.substring(attrStart, attrStart+attrLength);
							
						elemStringBuffer.delete(attrStart, attrStart+attrLength);
							
						int newAttrstart = 0;
						int metaIndex = elemStringBuffer.indexOf("<meta");
						int audioIndex = elemStringBuffer.indexOf("<audio");
						if(metaIndex!=-1){
							newAttrstart = metaIndex + "<meta".length()+1;
						}else if(audioIndex!=-1){
							newAttrstart = audioIndex + "<audio".length()+1;
						}
						elemStringBuffer.insert(newAttrstart, attr+" ");
		
					}
					String element = elemStringBuffer.toString();
					bw.write(element+"\r\n");
						
					elemStringBuffer.setLength(0);
					elementComplete = false;
				}else if (!buildingElement){
					bw.write(line+"\r\n");
				}
					
				line = br.readLine();	
			}//move to the next line 
					
			br.close();
			bw.flush();
			bw.close();
			FileUtils.copy(tempOutputFile,file);
			tempOutputFile.delete();
		} catch (IOException ioe) {
			throw ioe;
		}
			
	}
	
}