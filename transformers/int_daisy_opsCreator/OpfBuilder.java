package int_daisy_opsCreator;


import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.Namespace;


import org.daisy.util.dtb.meta.MetadataList;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.xml.IDGenerator;
import org.daisy.util.xml.pool.PoolException;
import org.daisy.util.xml.pool.StAXEventFactoryPool;

/**
 * Build the Package file of an OPS 2.0 publication.
 * @author Markus Gylling
 */
class OpfBuilder extends Builder {
	private Namespace opfNamespace = null;
	private Namespace dcNamespace = null;
	private List<String> spineIdList = null;
	private IDGenerator mIdGenerator = null;
		
	OpfBuilder(List<Fileset> inputFilesets, MetadataList metadata) {
		super(inputFilesets,metadata);		
		mIdGenerator = new IDGenerator("opf");
	}
	
	@Override
	void build() throws PoolException {
		XMLEventFactory xef = null;
		
		try{
			xef = StAXEventFactoryPool.getInstance().acquire();
			opfNamespace = xef.createNamespace(OpsCreator.OPF_NS);
			dcNamespace = xef.createNamespace(OpsCreator.DC_NS);
		
			QName pkg = new QName(opfNamespace.getNamespaceURI(),"package");
			
			mEventList.add(xef.createStartDocument("utf-8", "1.0"));
						
			mEventList.add(xef.createStartElement("",pkg.getNamespaceURI(),pkg.getLocalPart()));
			mEventList.add(xef.createNamespace("", pkg.getNamespaceURI()));
			mEventList.add(xef.createAttribute("version", "2.0"));
			mEventList.add(xef.createAttribute("unique-identifier", "uid"));
			
			createMetadata(xef);
			createManifest(xef);
			createSpine(xef);
						
			mEventList.add(xef.createEndElement("",pkg.getNamespaceURI(),pkg.getLocalPart()));						
			mEventList.add(xef.createEndDocument());
		}finally{
			StAXEventFactoryPool.getInstance().release(xef);
		}

	}

	private void createSpine(XMLEventFactory xef) {
		QName spine = new QName(opfNamespace.getNamespaceURI(),"spine");
		QName itemref = new QName(opfNamespace.getNamespaceURI(),"itemref");
		
		mEventList.add(xef.createStartElement(spine,null,null));
		mEventList.add(xef.createAttribute("toc", "ncx"));

		for (String id : spineIdList) {
			mEventList.add(xef.createStartElement(itemref,null,null));
			mEventList.add(xef.createAttribute("idref", id));			
			mEventList.add(xef.createEndElement(itemref,null));
		}
		
		mEventList.add(xef.createEndElement(spine,null));
	}
	
	private void createManifest(XMLEventFactory xef) {
		QName manifest = new QName(opfNamespace.getNamespaceURI(),"manifest");
		QName item = new QName(opfNamespace.getNamespaceURI(),"item");
		Set<URI> cache = new HashSet<URI>();  //need to cache since several filesets may refer to same resource
		spineIdList = new LinkedList<String>();
		
		mEventList.add(xef.createStartElement(manifest,null,null));
		
		for (Fileset fileset : mInputFilesets) {
			FilesetFile manifestMember = fileset.getManifestMember(); 
			for (Iterator iter = fileset.getLocalMembers().iterator(); iter.hasNext();) {
				FilesetFile file = (FilesetFile) iter.next();
				URI uri = file.getFile().toURI();
				if(!cache.contains(uri)) {
					cache.add(uri);
					mEventList.add(xef.createStartElement(item,null,null));
					String id = mIdGenerator.generateId();
					if(file == manifestMember) spineIdList.add(id);
					mEventList.add(xef.createAttribute("id", id));
					mEventList.add(xef.createAttribute("href", manifestMember.getRelativeURI(file).toASCIIString()));
					mEventList.add(xef.createAttribute("media-type", file.getMimeType().dropParametersPart()));
					mEventList.add(xef.createEndElement(item,null));
				}
			}							
		}
		
		//add ncx
		mEventList.add(xef.createStartElement(item,null,null));
		mEventList.add(xef.createAttribute("id", "ncx"));
		mEventList.add(xef.createAttribute("href", "navigation.ncx"));
		mEventList.add(xef.createAttribute("media-type", "application/x-dtbncx+xml"));
		mEventList.add(xef.createEndElement(item,null));
				
		mEventList.add(xef.createEndElement(manifest,null));		
	}

	private void createMetadata(XMLEventFactory xef) {
		QName metadata = new QName(opfNamespace.getNamespaceURI(),"metadata");				
		mEventList.add(xef.createStartElement(metadata,null,null));
		mEventList.add(xef.createNamespace("dc", dcNamespace.getNamespaceURI()));
		mEventList.add(xef.createNamespace("opf", opfNamespace.getNamespaceURI()));		
		mEventList.addAll(mMetaData.asXMLEvents());		
		mEventList.add(xef.createStartElement("", opfNamespace.getNamespaceURI(), "meta"));	
		mEventList.add(xef.createAttribute("name", "generator"));
		mEventList.add(xef.createAttribute("content", OpsCreator.APP_NAME));
		mEventList.add(xef.createEndElement("", opfNamespace.getNamespaceURI(), "meta"));

		mEventList.add(xef.createEndElement(metadata,null));		
	}

}
