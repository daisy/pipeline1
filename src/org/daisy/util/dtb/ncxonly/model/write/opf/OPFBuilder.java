/*
 * org.daisy.util (C) 2005-2008 Daisy Consortium
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
package org.daisy.util.dtb.ncxonly.model.write.opf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.Version;
import org.daisy.util.dtb.meta.MetadataItem;
import org.daisy.util.dtb.meta.MetadataList;
import org.daisy.util.dtb.ncxonly.model.Model;
import org.daisy.util.dtb.ncxonly.model.write.ncx.NCXBuilder;
import org.daisy.util.dtb.ncxonly.model.write.smil.SMILBuilder;
import org.daisy.util.dtb.ncxonly.model.write.smil.SMILFile;
import org.daisy.util.dtb.resource.ResourceFile;
import org.daisy.util.file.Directory;
import org.daisy.util.file.FileUtils;
import org.daisy.util.mime.MIMEConstants;
import org.daisy.util.xml.IDGenerator;
import org.daisy.util.xml.Namespaces;
import org.daisy.util.xml.pool.StAXEventFactoryPool;

/**
 *
 * @author Markus Gylling
 */
public class OPFBuilder {
	
	private final String OPF_DOCTYPE = "<!DOCTYPE package PUBLIC \"+//ISBN 0-9673008-1-9//DTD OEB 1.2 Package//EN\" \"http://openebook.org/dtds/oeb-1.2/oebpkg12.dtd\" >"; 
	private final String LINEBREAK = "\n";
	private final String TAB = "\t";
	private XMLEvent nl = null;
	private XMLEvent tab = null;
	private XMLEvent tab2 = null;
	//private XMLEvent tab3 = null;
	private Namespace opfNamespace = null;
	private Namespace dcNamespace = null;
	private Directory mDestinationDirectory = null;
	
	
	public OPFBuilder(Model model, SMILBuilder smilBuilder, NCXBuilder ncxBuilder, Directory dest, XMLOutputFactory xof, XMLEventFactory xef) throws XMLStreamException, FileNotFoundException {
		nl = xef.createCharacters(LINEBREAK);
		tab = xef.createCharacters(TAB);
		tab2 = xef.createCharacters(TAB+TAB);
		//tab3 = xef.createCharacters(TAB+TAB+TAB);
		opfNamespace = xef.createNamespace(Namespaces.OPF_10_NS_URI);
		dcNamespace = xef.createNamespace(Namespaces.DUBLIN_CORE_NS_URI);
		mDestinationDirectory = dest;
		
		IDGenerator idg = new IDGenerator("opf_");				
		xef = StAXEventFactoryPool.getInstance().acquire();
		File outputOPF = new File(dest,getName());
		XMLEventWriter writer = xof.createXMLEventWriter(new FileOutputStream(outputOPF));
		
		QName pkg = new QName(opfNamespace.getNamespaceURI(),"package");
		
		writer.add(xef.createStartDocument("utf-8", "1.0")); writer.add(nl);
		writer.add(xef.createDTD(OPF_DOCTYPE)); writer.add(nl);		
		writer.add(xef.createStartElement("",pkg.getNamespaceURI(),pkg.getLocalPart()));		
		writer.add(xef.createAttribute("unique-identifier", "uid"));
		
		createMetadata(xef,writer,model,smilBuilder);
		List<String> spineIdList = createManifest(xef,writer,smilBuilder,idg, ncxBuilder.getName());
		createSpine(xef,writer,spineIdList);
					
		writer.add(xef.createEndElement("",pkg.getNamespaceURI(),pkg.getLocalPart()));						
		writer.add(xef.createEndDocument());
	}

	private void createSpine(XMLEventFactory xef, XMLEventWriter writer, List<String> spineIdList) throws XMLStreamException {
		QName spine = new QName(opfNamespace.getNamespaceURI(),"spine");
		QName itemref = new QName(opfNamespace.getNamespaceURI(),"itemref");		
		
		writer.add(nl);
		writer.add(xef.createStartElement(spine,null,null));writer.add(nl);
		
		for (String id : spineIdList) {
			writer.add(tab);
			writer.add(xef.createStartElement(itemref,null,null));
			writer.add(xef.createAttribute("idref", id));			
			writer.add(xef.createEndElement(itemref,null));
			writer.add(nl);
		}
		
		writer.add(xef.createEndElement(spine,null));writer.add(nl);
	}
	
	private List<String> createManifest(XMLEventFactory xef, XMLEventWriter writer, SMILBuilder smilBuilder, IDGenerator idg, String ncxFileName) throws XMLStreamException {
		writer.add(nl);
		QName manifest = new QName(opfNamespace.getNamespaceURI(),"manifest");				
		List<String> spineIdList = new LinkedList<String>();		
		writer.add(xef.createStartElement(manifest,null,null));
		writer.add(nl);
		
		for(SMILFile smil : smilBuilder.getSpine()) {			
			String id = idg.generateId();			
			spineIdList.add(id);
			writeItem(xef,writer,id,smil.getFileName(),MIMEConstants.MIME_APPLICATION_SMIL);
			for(File file : smil.getAudioFiles()) {
				writeItem(xef,writer,idg.generateId(),file.getName(),MIMEConstants.MIME_AUDIO_X_WAV);
			}
		}
				
		//add ncx
		writeItem(xef,writer,"ncx",ncxFileName,MIMEConstants.MIME_APPLICATION_X_DTBNCX_XML);

		//add opf
		writeItem(xef,writer,"opf",getName(),MIMEConstants.MIME_TEXT_XML);
				
		//if skippable elements, add resource file
		if(smilBuilder.hasSkippableItems()) {
			addResourceFile(xef,writer);
		}
		
		writer.add(xef.createEndElement(manifest,null));
		
		return spineIdList;
	}

	/**
	 * Add the resource file to the output dir, add entries to manifest.
	 * @param idg 
	 */
	private void addResourceFile(XMLEventFactory xef, XMLEventWriter writer) {
		try{
			Set<URL> res = ResourceFile.get(ResourceFile.Type.TEXT_ONLY);			
			for(URL u : res) {
				String fileName = u.toString().substring(u.toString().lastIndexOf('/')+1);
				File output = new File(mDestinationDirectory,fileName);
				FileUtils.writeInputStreamToFile(u.openStream(), output);	
				writeItem(xef, writer, "resource", fileName, MIMEConstants.MIME_APPLICATION_X_DTBRESOURCE_XML);
			}
		}catch(Exception e) {
			e.printStackTrace();			
		}				
	}

	private void writeItem(XMLEventFactory xef, XMLEventWriter writer, String id, String href, String mime) throws XMLStreamException {
		QName item = new QName(opfNamespace.getNamespaceURI(),"item");
		writer.add(tab);
		writer.add(xef.createStartElement(item,null,null));
		writer.add(xef.createAttribute("id", id));			
		writer.add(xef.createAttribute("href", href));
		writer.add(xef.createAttribute("media-type", mime));
		writer.add(xef.createEndElement(item,null));
		writer.add(nl);	
	}
	
	private void createMetadata(XMLEventFactory xef, XMLEventWriter writer, Model model, SMILBuilder smilBuilder) throws XMLStreamException {
		QName metadata = new QName(opfNamespace.getNamespaceURI(),"metadata");		
		QName dcMetadata = new QName(opfNamespace.getNamespaceURI(),"dc-metadata");
		QName xMetadata = new QName(opfNamespace.getNamespaceURI(),"x-metadata");
		
		writer.add(nl);
		writer.add(xef.createStartElement(metadata,null,null));
		writer.add(nl);
		writer.add(tab);
		
		writer.add(xef.createStartElement(dcMetadata,null,null));		
		writer.add(xef.createNamespace("dc", dcNamespace.getNamespaceURI()));
		writer.add(xef.createNamespace("oebpackage", opfNamespace.getNamespaceURI()));		
		
		MetadataList meta = model.getMetadata();
		//add all dc ns items
		meta.add(new QName(dcNamespace.getNamespaceURI(),"Format","dc"), "ANSI/NISO Z39.86-2005");				
		for (int j = 0; j < meta.size(); j++) {
			MetadataItem item = model.getMetadata().get(j);	
			if(item.getQName().getNamespaceURI().equals(dcNamespace.getNamespaceURI())) {				
				writer.add(nl);
				writer.add(tab2);
				item.asXMLEvents(writer);
			}
		}		
		writer.add(nl);
		writer.add(tab);
		writer.add(xef.createEndElement(dcMetadata,null));
		writer.add(nl);
		writer.add(tab);
		writer.add(xef.createStartElement(xMetadata,null,null));
		
		//add all x-meta items
		writeMetaElement(xef,writer,"prod:generator","Pipeline " + Version.getVersion());
		writeMetaElement(xef,writer,"dtb:multimediaType","audioNCX");
		writeMetaElement(xef,writer,"dtb:multimediaContent","audio");
		writeMetaElement(xef,writer,"dtb:totalTime", smilBuilder.getDurationClock().toString());
		
		writer.add(nl);
		writer.add(tab);		
		writer.add(xef.createEndElement(xMetadata,null));
		
		writer.add(nl);
		writer.add(xef.createEndElement(metadata,null));
		
	}

	private void writeMetaElement(XMLEventFactory xef, XMLEventWriter writer, String name, String content) throws XMLStreamException {
		writer.add(nl);
		writer.add(tab2);
		writer.add(xef.createStartElement("", opfNamespace.getNamespaceURI(), "meta"));	
		writer.add(xef.createAttribute("name", name));
		writer.add(xef.createAttribute("content", content));
		writer.add(xef.createEndElement("", opfNamespace.getNamespaceURI(), "meta"));
	}
	
	private String getName() {		
		return "package.opf";
	}

}
