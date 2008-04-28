package int_daisy_dtbMigrator.impl.z2005_d202.read;

import int_daisy_dtbMigrator.BookStruct;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventConsumer;

import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.util.dtb.meta.MetadataItem;
import org.daisy.util.dtb.meta.MetadataList;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.Z3986DtbookFile;
import org.daisy.util.fileset.Z3986NcxFile;
import org.daisy.util.fileset.Z3986OpfFile;
import org.daisy.util.fileset.Z3986SmilFile;
import org.daisy.util.xml.IDGenerator;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.pool.StAXOutputFactoryPool;
import org.daisy.util.xml.stax.AttributeByName;
import org.daisy.util.xml.stax.StaxEntityResolver;

/**
 * Merge all SMIL files of a DTB into a single behemoth SMIL file.
 * <p>Add extra decorator attributes describing NCX and text content relational data.</p>
 * @author Markus Gylling
 */
public class SmilMerger  {
	
	private TransformerDelegateListener mListener = null;
	
	private static final String SMIL_NS = "http://www.w3.org/2001/SMIL20/";
	private static final String SMIL_ADD_NS = "http://www.daisy.org/pipeline/smil/";
	private static final String NCX_NS = "http://www.daisy.org/pipeline/ncx/";
	private static final String DTBOOK_NS = "http://www.daisy.org/pipeline/dtbook/";
	private int ncxNodesAdded = 0;
	/**
	 * Constructor.
	 */
	public SmilMerger(TransformerDelegateListener listener) {
		mListener = listener;
	}

	/**
	 * Merge and render the DTB as a behemoth SMIL file to disk.
	 * @param input Z3986-2005 DTB Fileset to merge
	 * @param destination File to write the merged SMIL to
	 * @throws XMLStreamException 
	 * @throws CatalogExceptionNotRecoverable 
	 * @throws IOException 
	 */
	public void render(Fileset input, File destination) throws XMLStreamException, IOException, CatalogExceptionNotRecoverable {
		Map<String,Object> properties = StAXOutputFactoryPool.getInstance().getDefaultPropertyMap();
		XMLOutputFactory xof = StAXOutputFactoryPool.getInstance().acquire(properties);
		FileOutputStream fos = new FileOutputStream(destination);
		XMLEventWriter writer = xof.createXMLEventWriter(fos);
		render(input,writer);
		writer.flush();
		writer.close();
		fos.close();
		StAXOutputFactoryPool.getInstance().release(xof, properties);		
	}
		
	/**
	 * Render the DTB as a behemoth SMIL file to an XMLEventConsumer.
	 * @param input Z3986-2005 DTB Fileset to merge
	 * @param destination Consumer to write the merged SMIL to
	 * @throws XMLStreamException 
	 * @throws CatalogExceptionNotRecoverable 
	 * @throws IOException 
	 */
	
	public  void render(Fileset input, XMLEventConsumer destination) throws XMLStreamException, CatalogExceptionNotRecoverable, IOException {				
		Collection<FilesetFile> inputSpine = ((Z3986OpfFile)input.getManifestMember()).getSpineItems();				
		List<Z3986DtbookFile> inputDtbook = getDtbookFiles(inputSpine);
		
		/*
		 * Get global skippability info from the NCX. While writing, we will make sure that all 
		 * customTest IDREF-ID pairs in the resulting SMIL file use predefined names, namely
		 * the BookStruct strings as defined in z38962005. 
		 */		
		Z3986NcxFile inputNcx = getNcxFile(input);
		NcxSmilCustomTests ncxSmilCustomTests = new NcxSmilCustomTests(inputNcx);
		NcxContentNodes ncxContentNodes = new NcxContentNodes(inputNcx);
		System.err.println("ncxContentNode size: " + ncxContentNodes.size());
		
		/*
		 * Get StAX factories from pool
		 */
		XMLEventFactory xef = StAXEventFactoryPool.getInstance().acquire();	
		Map<String,Object> properties = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(false);
		XMLInputFactory xif = StAXInputFactoryPool.getInstance().acquire(properties);
		xif.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
		
		/*
		 * Create an idgenerator guaranteed to generate globally unique IDs 
		 */
		IDGenerator idg = createIdGenerator();
		
		/*
		 * Open the document
		 */
		destination.add(xef.createStartDocument());
		destination.add(xef.createStartElement("",SMIL_NS, "smil"));
		destination.add(xef.createNamespace("smil", SMIL_ADD_NS));
		destination.add(xef.createNamespace("ncx", NCX_NS));
		destination.add(xef.createNamespace("dtbook", DTBOOK_NS));
		
		/*
		 * Write the head
		 */
		writeHead(xef,destination,input,ncxSmilCustomTests);
		
		/*
		 * Write the body
		 */		
		destination.add(xef.createStartElement("",SMIL_NS, "body"));
		for(FilesetFile smil : inputSpine) {
			InputStream is = smil.asInputStream();
			XMLEventReader reader = xif.createXMLEventReader(smil.asInputStream());		
			int i = 0;
			while(reader.hasNext()) {
				XMLEvent e = reader.nextEvent();
				if(e.isStartElement() && e.asStartElement().getName().getLocalPart().equals("body")) {
					writeBody(xef,reader, destination, ncxSmilCustomTests, ncxContentNodes,inputDtbook, smil.getName(),idg);					
					break;
				}				
			}
			is.close();
			mListener.delegateProgress(this, (double)(++i)/inputSpine.size());
		}		
		destination.add(xef.createEndElement("",SMIL_NS, "body"));
		
		/*
		 * Close the document
		 */
		destination.add(xef.createEndElement("",SMIL_NS, "smil"));
		destination.add(xef.createEndDocument());
		
		/*
		 * Return StAX factories to pool
		 */
		StAXInputFactoryPool.getInstance().release(xif, properties);
		StAXEventFactoryPool.getInstance().release(xef);
		
		System.err.println("NCX nodes added to behemoth: " + ncxNodesAdded);
	}

	private IDGenerator createIdGenerator() {		
		String uid = UUID.randomUUID().toString();
		return new IDGenerator("id_"+uid.substring(uid.length()-8));
	}

	/**
	 * Write the contents of a SMIL files body to destination, excluding body open and close tags.
	 * Inparam reader located before body open element. Return with the reader placed before body close element.
	 * @param inputDtbook 
	 * @throws XMLStreamException 
	 */
	private void writeBody(XMLEventFactory xef, XMLEventReader reader , XMLEventConsumer destination, NcxSmilCustomTests ncxSmilCustomTests, 
			NcxContentNodes ncxContentNodes, List<Z3986DtbookFile> inputDtbook, String currentSmilFileName, IDGenerator idg) throws XMLStreamException {
		
		final QName customTestQName = new QName(null,"customTest");
		final QName idQName = new QName(null,"id");
		final QName prevUriQName = new QName(SMIL_ADD_NS,"prevURI");
		final QName targetQName = new QName(DTBOOK_NS,"targetQName");
//		final QName targetContextQName = new QName(DTBOOK_NS,"targetContext");
		final QName ncxTextQName = new QName(NCX_NS,"text");
		final QName ncxContextQName = new QName(NCX_NS,"context");
		final QName srcQname = new QName(null,"src");
		final String prevUriBase = currentSmilFileName+"#";
						
		while(reader.hasNext()) {
			XMLEvent e = reader.nextEvent();
		
			if(e.isStartElement() && e.asStartElement().getName().getLocalPart().equals("body")) continue;
			if(e.isEndElement() && e.asEndElement().getName().getLocalPart().equals("body")) break;
			
			if(e.isStartElement()) {
				StartElement se = e.asStartElement();
				destination.add(xef.createStartElement("", SMIL_NS, se.getName().getLocalPart()));
				
				//if element has a customTest, redefine value to be the canonical one				
				Attribute a = AttributeByName.get(customTestQName,se);
				if(a!=null) {					
					NcxSmilCustomTest test = ncxSmilCustomTests.get(a.getValue());
					if(test!=null && test.bookStruct!=null && isSupportedIn202(test.bookStruct)) {
						Attribute b = xef.createAttribute(customTestQName, test.bookStruct.toString());
						destination.add(b);
					}else{
						System.err.println("warning: not writing customTest " + a.getValue());
					}
				}
				
				//if the element has an ID
				
				a = AttributeByName.get(idQName,se);
				if(a!=null) {
					//store locally the value of a URI referencing this node before id change
					String origURI = prevUriBase+a.getValue();
					destination.add(xef.createAttribute(prevUriQName, origURI));
					//check if its referenced by NCX and note the nature of the referencee
					NcxContentNode ncxNode = ncxContentNodes.get(origURI);
					//TODO: several nodes in ncx can refer to same smil destination
					if(ncxNode!=null) {
						destination.add(xef.createAttribute(ncxContextQName, ncxNode.xpathContext));
						destination.add(xef.createAttribute(ncxTextQName, ncxNode.text));
						ncxNodesAdded++;
					}
				}
				
				//generate a new ID
				destination.add(xef.createAttribute(idQName, idg.generateId()));
				
				//if text element, add the qname of the dtbook destination element
				if(se.getName().getLocalPart() == "text" && inputDtbook!=null && !inputDtbook.isEmpty()) {					
					String srcValue = AttributeByName.get(srcQname, se).getValue();
					String dtbookFilename = srcValue.substring(0, srcValue.indexOf('#'));
					String dtbookId = srcValue.substring(srcValue.indexOf('#')+1);
					QName qName = null;
					for (Z3986DtbookFile dtbk : inputDtbook) {
						if(dtbk.getName().equals(dtbookFilename)) {
							qName = dtbk.getQName(dtbookId);
							break;
						}
					}
					if(qName!=null) {
						destination.add(xef.createAttribute(targetQName, qName.toString()));						
					}
				}
								
				//add all remaining attributes
				for (Iterator<?> iterator = se.getAttributes(); iterator.hasNext();) {
					Attribute attr = (Attribute) iterator.next();
					String attrName = attr.getName().getLocalPart();
					if(attrName !="id" && attrName != "class" && attrName != "customTest" 
						&& attrName != "end" && attrName != "fill") {
						destination.add(attr);
					}					
				}
				
			} else if (e.isEndElement()) {
				destination.add(e);
			}			
		}		
	}

	/**
	 * Write the behemoth SMIL head, head start and end elements inclusive
	 * @throws XMLStreamException 
	 * @throws CatalogExceptionNotRecoverable 
	 * @throws IOException 
	 */
	private void writeHead(XMLEventFactory xef, XMLEventConsumer destination, Fileset input, 
			NcxSmilCustomTests ncxSmilCustomTests) throws XMLStreamException, CatalogExceptionNotRecoverable, IOException {
		
		destination.add(xef.createStartElement("", SMIL_NS, "head"));
		
		/*
		 * Add all needed publication metadata
		 */
		MetadataList metadata = createMetadata(input);
		metadata.asXMLEvents(destination);			
		
		/*
		 * Add customTests, using the canonical ID values that match bookStruct
		 * Only add those tests that we can represent in the output 2.02
		 */
		if(ncxSmilCustomTests!=null && !ncxSmilCustomTests.isEmpty()) {
			destination.add(xef.createStartElement("", SMIL_NS, "customAttributes"));
			for(NcxSmilCustomTest test : ncxSmilCustomTests) {
				if(test.bookStruct!=null && isSupportedIn202(test.bookStruct)) {
					destination.add(xef.createStartElement("", SMIL_NS, "customTest"));						
					destination.add(xef.createAttribute("id", test.bookStruct.toString()));
					destination.add(xef.createAttribute("defaultState", test.defaultState));
					destination.add(xef.createAttribute("override", test.override));
					destination.add(xef.createEndElement("", SMIL_NS, "customTest"));
				}	
			}
			destination.add(xef.createEndElement("", SMIL_NS, "customAttributes"));		
		}		
		destination.add(xef.createEndElement("", SMIL_NS, "head"));		
	}

	private MetadataList createMetadata(Fileset input) throws CatalogExceptionNotRecoverable, XMLStreamException, IOException {
		final String dcNS = "http://purl.org/dc/elements/1.1/";		
		MetadataList list = new MetadataList();		
		Z3986OpfFile opf = (Z3986OpfFile)input.getManifestMember();
		Map<String,Object> properties = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(false);
		XMLInputFactory xif = StAXInputFactoryPool.getInstance().acquire(properties);
		xif.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
		InputStream is = opf.asInputStream();
		XMLStreamReader reader = xif.createXMLStreamReader(is);
		while(reader.hasNext()) {
			reader.next();
			if(reader.isEndElement() && reader.getLocalName().equals("dc-metadata")) break;			
			if(reader.isStartElement() && reader.getNamespaceURI().equals(dcNS)) {
				MetadataItem item = new MetadataItem(new QName(SMIL_NS,"meta"));
				item.addAttribute("name", "dc:"+reader.getLocalName().toLowerCase());
				item.addAttribute("content", reader.getElementText());
				list.add(item);
			}			
		}		
		reader.close();
		is.close();		
		StAXInputFactoryPool.getInstance().release(xif, properties);
		return list;
	}

		
	/**
	 * Return true if given bookstruct is supported by the 2.02 skippability recommendation.
	 */
	boolean isSupportedIn202(BookStruct bookStruct) {
		return (bookStruct == BookStruct.PAGE_NUMBER 
				|| bookStruct == BookStruct.NOTE 
				|| bookStruct == BookStruct.NOTE_REFERENCE
				|| bookStruct == BookStruct.OPTIONAL_PRODUCER_NOTE
				|| bookStruct == BookStruct.OPTIONAL_SIDEBAR
				);
	}
		
	
	private List<Z3986DtbookFile> getDtbookFiles(Collection<FilesetFile> inputSpine) {
		List<Z3986DtbookFile> dtbooks = new LinkedList<Z3986DtbookFile>();
		for(FilesetFile f : inputSpine) {
			Z3986SmilFile smil = (Z3986SmilFile)f;
			Collection<FilesetFile> referenced = smil.getReferencedLocalMembers();
			for(FilesetFile ff : referenced) {
				if(ff instanceof Z3986DtbookFile) {
					if(!dtbooks.contains(ff)){						
						dtbooks.add((Z3986DtbookFile)ff);
					}	
				}
			}
		}
		return dtbooks;
	}

	private Z3986NcxFile getNcxFile(Fileset inputFileset) {
		Iterator<?> i = inputFileset.getLocalMembers().iterator();
		while(i.hasNext()) {
			FilesetFile ff = (FilesetFile)i.next();
			if(ff instanceof Z3986NcxFile) return (Z3986NcxFile)ff;
		}
		throw new IllegalStateException("No ncx found in fileset");
	}
		
}
