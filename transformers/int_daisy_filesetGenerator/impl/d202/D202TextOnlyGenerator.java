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
package int_daisy_filesetGenerator.impl.d202;

import int_daisy_filesetGenerator.FilesetGeneratorException;
import int_daisy_filesetGenerator.IFilesetGenerator;
import int_daisy_filesetGenerator.FilesetGeneratorFactory.OutputType;
import int_daisy_filesetGenerator.util.IDPopulator;
import int_daisy_filesetGenerator.util.d202.D202NccBuilder;
import int_daisy_filesetGenerator.util.d202.D202SmilBuilder;
import int_daisy_filesetGenerator.util.d202.D202NccBuilder.NccNavItemType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.util.file.Directory;
import org.daisy.util.file.EFile;
import org.daisy.util.file.TempFile;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.Xhtml10File;
import org.daisy.util.fileset.util.FilesetFileFilter;
import org.daisy.util.fileset.util.FilesetLabelProvider;
import org.daisy.util.i18n.CharUtils;
import org.daisy.util.xml.Namespaces;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.pool.StAXOutputFactoryPool;
import org.daisy.util.xml.stax.AttributeByName;
import org.daisy.util.xml.stax.BookmarkedXMLEventReader;
import org.daisy.util.xml.stax.StaxEntityResolver;


/**
 * Generate a Daisy 2.02 Text-only fileset, a la Bruno.
 * @author Markus Gylling
 */
public class D202TextOnlyGenerator implements IFilesetGenerator {
	
	public static final String PARAM_CHARSET = "Charset";
	public static final String PARAM_SMILREF = "SmilRef";
	public static final String PARAM_SMILREF_VAL_TEXT = "TEXT";
	public static final String PARAM_SMILREF_VAL_PAR = "PAR";
		
	private Directory mDestination = null;
	private List<Fileset> mInputFilesets = null;	
	private Map<String, Object> mConfiguration = null;
	private D202NccBuilder mNccBuilder;
	private List<D202SmilBuilder> mSmilBuilders;
	private D202SmilBuilder mCurrentSmilBuilder = null;	
	private final static QName qA = new QName(Namespaces.XHTML_10_NS_URI,"a");
	private final static QName qHref = new QName("","href");
	private TransformerDelegateListener mListener = null;
	private GlobalMetadata mGlobalMetadata = null;
	private Charset mOutputCharset = null;
	

	/*
	 * (non-Javadoc)
	 * @see int_daisy_filesetGenerator.IFilesetGenerator#execute(org.daisy.util.file.EFolder)
	 */
	
	/*
	 * This class owns the NCC and SMIL builders, 
	 * and handles generating the modded content doc internally.
	 */
	public void execute(Directory destination) throws FilesetGeneratorException {		

		mDestination = destination;
		
		/*
		 * Create some general metadata to be passed around
		 */
		mGlobalMetadata = new GlobalMetadata();
		/*
		 * Figure out which output encoding to use
		 */
		if(mConfiguration.containsKey(PARAM_CHARSET) && (mConfiguration.get(PARAM_CHARSET)!=null)) {
			mOutputCharset  = (Charset)mConfiguration.get(PARAM_CHARSET);
		}else{
			mOutputCharset = Charset.forName("utf-8");
		}
		
		/*
		 * We have 1-n Xhtml Filesets as input. These are turned into one 2.02 fileset.
		 */
		
		File ncc = new File(mDestination,"ncc.html");
		mNccBuilder = new D202NccBuilder(ncc,mGlobalMetadata,mOutputCharset);		
		mSmilBuilders = new ArrayList<D202SmilBuilder>();
		
		Map<String,Object> xifProperties = null;	
		Map<String,Object> xofProperties = null;
		XMLOutputFactory xof = null;
		XMLInputFactory xif = null;
		XMLEventFactory xef = null;
		try{
			xifProperties = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(false);
			xofProperties = StAXOutputFactoryPool.getInstance().getDefaultPropertyMap();
			xif = StAXInputFactoryPool.getInstance().acquire(xifProperties);
			xif.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
			xef = StAXEventFactoryPool.getInstance().acquire();
			xof = StAXOutputFactoryPool.getInstance().acquire(xofProperties);
		
			int i = 0;
			for(Fileset fileset : mInputFilesets) {
				i++;
				//get current fileset manifest		
				File mInput = fileset.getManifestMember().getFile();
				//add IDs to it
				File tempContentDoc = addIDs(mInput);
				//create destination 			
				EFile sourceFile = new EFile(mInput);
				File outputContentDoc = new File(mDestination,sourceFile.getNameMinusExtension()+".html");
				//run through the input doc, populate builders, mod content doc with smilrefs etc
				build(tempContentDoc,outputContentDoc,xif,xof,xef,i);				
			} //for(Fileset fileset : mInputFilesets)
		
			mNccBuilder.render();
			for(D202SmilBuilder smilBuilder : mSmilBuilders) {
				smilBuilder.render();
			}
		}catch (CatalogExceptionNotRecoverable e) {
			e.printStackTrace();
		} catch (IOException e) {
			throw new FilesetGeneratorException(e.getLocalizedMessage(),e);
		} catch (XMLStreamException e) {
			throw new FilesetGeneratorException(e.getLocalizedMessage(),e);
		}finally{
			StAXInputFactoryPool.getInstance().release(xif, xifProperties);
			StAXEventFactoryPool.getInstance().release(xef);
			StAXOutputFactoryPool.getInstance().release(xof,xofProperties);
		}
	}

	private void build(File tempContentDoc, File outputContentDoc, XMLInputFactory xif, XMLOutputFactory xof
			, XMLEventFactory xef, int contentDocCounter) throws XMLStreamException, FileNotFoundException {
		
		InputStream is = null;
		FileOutputStream fos = null;
		XMLEventWriter writer = null;
		BookmarkedXMLEventReader reader = null;
		try{			
			is = new FileInputStream(tempContentDoc);			
			reader = new BookmarkedXMLEventReader(xif.createXMLEventReader(is));
			fos = new FileOutputStream(outputContentDoc);
			writer = xof.createXMLEventWriter(fos,mOutputCharset.name());
			
			while(reader.hasNext()) {
				XMLEvent xe = reader.nextEvent();
				
				if(xe.isStartElement()){
					writeStartElement(xe.asStartElement(),writer,xef);
					if(xe.asStartElement().getName().getLocalPart()=="head") {						
						handleHead(reader,writer,xef,contentDocCounter);									
					}else if(xe.asStartElement().getName().getLocalPart()=="body") {						
						handleBody(reader,writer,outputContentDoc,xef,contentDocCounter,mInputFilesets.size());
					}
				}else if(xe.isStartDocument()) {
					writer.add(xef.createStartDocument(mOutputCharset.name(), "1.0"));
				}else{
					writer.add(xe);
				}
			}
		}finally{
			try {
				reader.close();
				is.close();
				writer.flush();
				writer.close();				
				fos.flush();
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
	}

	/**
	 * Make sure the input doc has ID attributes added where needed.
	 */
	private File addIDs(File input) throws IOException, XMLStreamException {
		InputStream is = null;
		FileOutputStream fos = null;
		File out = null;
		try{
			out = TempFile.create();
			is = new FileInputStream(input);
			StreamSource source = new StreamSource(is);
			fos = new FileOutputStream(out);
			StreamResult result = new StreamResult(fos);
			IDPopulator idProvider = new IDPopulator(source,result);
			idProvider.execute();		
		}finally{
			try {
				fos.flush();
				fos.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return out;
	}

	/**
	 * Write a start element, stripping out any SMIL xmlns and smil:sync attrs
	 * We dont filter out any non-xhtml ns here since we may want to support compound docs later on. 
	 * @throws XMLStreamException 
	 */
	private void writeStartElement(StartElement se,XMLEventWriter writer, XMLEventFactory xef) throws XMLStreamException {
		Set<Attribute> attributes = new HashSet<Attribute>();
		Set<Namespace> namespaces = new HashSet<Namespace>();
		
		for (Iterator<?> iterator = se.getNamespaces(); iterator.hasNext();) {
			Namespace ns = (Namespace) iterator.next();		
			if(ns.getNamespaceURI() != Namespaces.SMIL_20_NS_URI) {
				namespaces.add(ns); 			
			}
		}
		
		for (Iterator<?> iterator = se.getAttributes(); iterator.hasNext();) {
			Attribute a = (Attribute) iterator.next();
			if(a.getName().getNamespaceURI() != Namespaces.SMIL_20_NS_URI) {
				attributes.add(a); 			
			}
		}
		
		writer.add(xef.createStartElement(se.getName(), attributes.iterator(), namespaces.iterator()));
	}

	/**
	 * Handle all events after head open until head close. 
	 * Only add metas to NCC builder if this is the first XHTML input doc.
	 */
	private void handleHead(BookmarkedXMLEventReader reader, XMLEventWriter writer, XMLEventFactory xef, int contentDocCounter) throws XMLStreamException {
		while(reader.hasNext()) {
			XMLEvent xe = reader.nextEvent();
			
			if(xe.isEndElement() && xe.asEndElement().getName().getLocalPart() == "head") {
				writer.add(xe);
				return;
			}			
			
			if(contentDocCounter==1) {
				if(xe.isStartElement() && xe.asStartElement().getName().getLocalPart() == "meta") {
					/*
					 * Handle classic metas
					 */
					Attribute name = getAttribute(xe.asStartElement(),new QName("", "name"));
					String nameValue = null;			
					if(name!=null)nameValue = handleMetaName(name.getValue());
					
					Attribute content = getAttribute(xe.asStartElement(),new QName("", "content"));
					String contentValue = null;				
					if(content!=null) contentValue = content.getValue();
									
					if(nameValue!=null && contentValue!=null) {					
						mNccBuilder.addMetadataItem(nameValue, contentValue);
					}	
					
					/*
					 * Handle http-equiv, we may be transcoding
					 */
					Attribute httpEquiv = getAttribute(xe.asStartElement(),new QName("", "http-equiv"));
					if(httpEquiv!=null) {
						Set<Attribute> attrs = new HashSet<Attribute>();
						attrs.add(xef.createAttribute(new QName("http-equiv"), "Content-type"));
						attrs.add(xef.createAttribute(new QName("content"), "application/xhtml+xml; charset=" + mOutputCharset.name()));
						xe = xef.createStartElement(new QName(Namespaces.XHTML_10_NS_URI,"meta"), attrs.iterator(), xe.asStartElement().getNamespaces());
					}
				}	
			}
			writer.add(xe);
		}
	}

	/**
	 * Avoid the dreaded Woodstox attribute iterator bug.
	 * @return the attribute if existing, else null.
	 */
	Attribute getAttribute(StartElement element, QName attrName) {
		Iterator<?> iter = element.getAttributes();
		while(iter.hasNext()) {
			Attribute a = (Attribute) iter.next();
			if(a.getName().getNamespaceURI().equals(attrName.getNamespaceURI()) &&
					a.getName().getLocalPart().equals(attrName.getLocalPart())) {
				return a;
			}
		}
		return null;
	}

	/**
	 * Handle all events after body open and before body close. 
	 * @param outputContentDoc 
	 * @param xef 
	 */
	private void handleBody(BookmarkedXMLEventReader reader, XMLEventWriter writer, File outputContentDoc, 
			XMLEventFactory xef, int contentDocCounter, int contentDocCount) throws XMLStreamException {
						
		while(reader.hasNext()) {
			XMLEvent xe = reader.nextEvent();
			
			if(xe.isEndElement() && xe.asEndElement().getName().getLocalPart() == "body") {
				writer.add(xe);
				//if this is the last input content doc, add the very last smilbuilder,
				//else let it be open since the next doc may not begin with a heading
				if(contentDocCounter == contentDocCount) {
					mSmilBuilders.add(mCurrentSmilBuilder);
				}
				return;
			}
			
			if (xe.isStartElement()) {

				if(shouldSync(xe)) {

					String elementText = getElementText(reader, xe.asStartElement().getName());
					
					/*
					 * Check if we need to swap smil builder 
					 */
					if(isHeading(xe.asStartElement())) {
						
						if(mCurrentSmilBuilder!=null) {
							//add the prev smil files builder to the stack
							mSmilBuilders.add(mCurrentSmilBuilder);						
						}
						//open a new smilBuilder
						mCurrentSmilBuilder = new D202SmilBuilder(getSmilFileName(mSmilBuilders.size()+1),elementText
								,mGlobalMetadata,mOutputCharset);
					}
					
					/*
					 * mCurrentSmilBuilder is null until we have encountered the first heading of the first input doc.
					 * TODO Maybe change this and mod the first input doc to have an h1.title as first body descendant.
					 */
					if(mCurrentSmilBuilder!=null) {

						/*
						 * Add ref in current smil builder
						 */
						boolean useTextUri = PARAM_SMILREF_VAL_TEXT.equals(mConfiguration.get(PARAM_SMILREF));
						String smilParURI = mCurrentSmilBuilder.addPar(outputContentDoc.getName()+"#"+getID(xe.asStartElement()),useTextUri);
																							
						/*
						 * Add ref in NCC
						 */
						NccNavItemType navType = mNccBuilder.getType(xe.asStartElement());
						if(navType!=null) {
							mNccBuilder.addNccNavItem(navType, smilParURI, elementText);
						}	
										
						/*
						 * Add new content to content doc writer. We need
						 * a nested anchor.
						 * 
						 * Note - if the current element has an anchor descendant,
						 * we cannot add smilrefs since its invalid to the DTD.
						 * 
						 * This doesnt hurt playback as the ref from smil to content
						 * is intact
						 * 
						 */
						if(isAnchorOrHasAnchorDescendants(xe.asStartElement(),reader)) {
							writeStartElement(xe.asStartElement(), writer, xef);
						}else{
							QName outerElement = xe.asStartElement().getName();
							Stack<QName> openElementsOfSameName = new Stack<QName>();						
							//write the current startelement event
							writeStartElement(xe.asStartElement(), writer, xef);
							//open an anchor
							writer.add(xef.createStartElement(qA, null, null));					
							writer.add(xef.createAttribute(qHref, smilParURI));
							//write everything that appears within the current readers start element
							XMLEvent xe2 = null;
							while(reader.hasNext()) {
								xe2 = reader.nextEvent();
								if(xe2.isStartElement() && xe2.asStartElement().getName().equals(outerElement)) {
									openElementsOfSameName.push(xe2.asStartElement().getName());
								}
								if(xe2.isEndElement() && xe2.asEndElement().getName().equals(outerElement)) {
									if(openElementsOfSameName.isEmpty()) {
										//we have reached the end point, exit loop
										break;
									}
									openElementsOfSameName.pop();
								}
								//we have not reached the end point, write whatever we have
								if(xe2.isStartElement()) {
									writeStartElement(xe2.asStartElement(), writer, xef);
								}else{
									writer.add(xe2);
								}	
							}
							//close the anchor
							writer.add(xef.createEndElement(qA, null));
							//close the parent, xe2 is an EndElement here
							writer.add(xe2);
						} // if(hasAnchorDescendants(xe,reader))
						
						
					}else{
						//smilBuilder is null, we havent encountered the first heading
						writeStartElement(xe.asStartElement(), writer, xef);
					}
					
				}else{
					//should not sync
					writeStartElement(xe.asStartElement(), writer, xef);
				}				
			}else{
				//is not a start element
				writer.add(xe);
			}
		}

	}

	/**
	 * Move through all descendants of xe, return true if an xhtml:a is found, or if base itself is an anchor.
	 * @throws XMLStreamException 
	 */
	private boolean isAnchorOrHasAnchorDescendants(StartElement base, BookmarkedXMLEventReader reader) throws XMLStreamException {
		if(base.getName().equals(qA)) return true;
		reader.setBookmark("testingAnchor");
		Stack<QName> openElementsOfSameName = new Stack<QName>();
		while(reader.hasNext()) {
			XMLEvent xe = reader.nextEvent();
			
			if(xe.isStartElement() && xe.asStartElement().getName().equals(base.getName())) {
				openElementsOfSameName.push(xe.asStartElement().getName());
			}
			
			else if(xe.isEndElement() && xe.asEndElement().getName().equals(base.getName())) {
				if(openElementsOfSameName.isEmpty()) {
					//we have reached the end point, exit loop
					break;
				}
				openElementsOfSameName.pop();
			}
			
			if(xe.isStartElement() && xe.asStartElement().getName().equals(qA)) {
				return true;
			}
		}
		
		reader.gotoAndRemoveBookmark("testingAnchor");
		
		return false;
	}

	/**
	 * With reader positioned at a startelement, retrieve compiled whiespace normalized String of all Character descendants. 
	 * @throws XMLStreamException 
	 */
	private String getElementText(BookmarkedXMLEventReader reader, QName parent) throws XMLStreamException {
		
		reader.setBookmark("begin");
		
		StringBuilder sb = new StringBuilder();
		Stack<QName> openWithSameName = new Stack<QName>();
		
		while(reader.hasNext()) {
			XMLEvent e = reader.nextEvent();
			if(e.isStartElement() && e.asStartElement().getName().equals(parent)) openWithSameName.push(parent);
			if(e.isEndElement() && e.asEndElement().getName().equals(parent)) {
				if(openWithSameName.isEmpty()) break;
				openWithSameName.pop();
			}
				
			if(e.isCharacters()) {
				sb.append(e.asCharacters().getData());
			}
		}
		
		StringBuilder sb2 = new StringBuilder();
		boolean nonWhitespaceCharSeen = false;
		for (int i = 0; i < sb.length(); i++) {
			char ch = sb.charAt(i);
			if (CharUtils.isXMLWhiteSpace(ch)) {
				if(nonWhitespaceCharSeen && !CharUtils.isXMLWhiteSpace(sb2.charAt(sb2.length()-1))) {
					sb2.append(' ');
				}				
			}else{
				nonWhitespaceCharSeen = true;
				sb2.append(ch);
			}
		}
		
		if(sb2.length()>0 && CharUtils.isXMLWhiteSpace(sb2.charAt(sb2.length()-1))) {
			sb2.deleteCharAt(sb2.length()-1);
		}
		
		reader.gotoAndRemoveBookmark("begin");
		return sb2.toString();
	}

	private String getID(StartElement se) {
		Iterator<?> i = se.getAttributes();
		while(i.hasNext()) {
			Attribute a = (Attribute) i.next();
			if(a.getName().getLocalPart()=="id") {
				return a.getValue();
			}
		}		
		throw new NullPointerException("No id: " + se.getName().toString());
	}


	private boolean shouldSync(XMLEvent xe) {
		if(!xe.isStartElement()) return false;
		if(AttributeByName.get(new QName(Namespaces.SMIL_20_NS_URI, "sync"),xe.asStartElement())!=null) {
			return true;
		}
		return false;
	}


	private static Pattern headingMatcher = null;
	private boolean isHeading(StartElement se) {		
		if(headingMatcher==null) headingMatcher = Pattern.compile("h1|h2|h3|h4|h5|h6");		
		return headingMatcher.matcher(se.getName().getLocalPart()).matches();
	}

	/**
	 * Handle a meta element from input doc, fix case if recognized.
	 * Return null if not recognized.
	 */
	private String handleMetaName(String nameAttributeValue) {
		
		if(nameAttributeValue == null) return nameAttributeValue;
		
		String name = nameAttributeValue.toLowerCase();
		
		if(name.startsWith("dc:")) {
			return name;
		}
		
		if(name.equals("dtb:uid")) {
			return "dc:identifier";
		}
		
		if(!name.contains(":")) {
			return "dtb:" + nameAttributeValue;			
		}
		
		return null;
	}

	private File getSmilFileName(int size) {
		return new File(mDestination,"d202_"+size+".smil");		
	}

	/*
	 * (non-Javadoc)
	 * @see int_daisy_filesetGenerator.IFilesetGenerator#configure(java.util.List, int_daisy_filesetGenerator.FilesetGeneratorFactory.OutputType, java.util.Map)
	 */
	public void configure(List<Fileset> input, OutputType output, Map<String,Object> config) throws FilesetGeneratorException {
		if(output!= OutputType.D202_TEXTONLY) throw new FilesetGeneratorException(output.name());
		
		if(input.isEmpty()) throw new FilesetGeneratorException("input is empty"); //TODO i18n
		for(Fileset fs : input) {
			if(fs.getFilesetType()!= org.daisy.util.fileset.FilesetType.XHTML_DOCUMENT) {
				throw new FilesetGeneratorException(fs.getFilesetType().toNiceNameString());	
			}
		}
		
		mConfiguration = config;
		mInputFilesets = input;		
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.util.FilesetFileFilter#acceptFile(org.daisy.util.fileset.FilesetFile)
	 */
	public short acceptFile(FilesetFile file) {
		if(file instanceof Xhtml10File) return FilesetFileFilter.REJECT;
		return FilesetFileFilter.ACCEPT;
	}
	
	/**
	 * Encapsulate recurring meta items that can
	 * have several sources
	 * @author Markus Gylling
	 */
	public class GlobalMetadata {
		public String mPublicationIdentifier = null;
		public String mPublicationTitle = null;

		public GlobalMetadata() {
			
			FilesetLabelProvider labels = new FilesetLabelProvider(mInputFilesets.get(0));
			
			mPublicationIdentifier = labels.getFilesetIdentifier();
			mPublicationTitle = labels.getFilesetTitle();
			
			if(mConfiguration!=null) {
				String uid = (String)mConfiguration.get("uid");
				if(uid != null && uid.length()>0) {
					mPublicationIdentifier = uid;
				}
			}
			
			if(mPublicationIdentifier==null) {
				mPublicationIdentifier = UUID.randomUUID().toString();
			}
			
			if(mPublicationTitle==null) {
				//TODO get first hx in input fileset
				mPublicationTitle = "Unknown Title";
			}
		}		
	}

}

///*
//* (non-Javadoc)
//* @see int_daisy_filesetGenerator.IFilesetGenerator#setDestination(org.daisy.util.file.EFolder)
//*/
//public void setDestination(EFolder destination) {
//	mDestination = destination;		
//}

///*
//* (non-Javadoc)
//* @see int_daisy_filesetGenerator.IFilesetGenerator#setInput(java.net.URL)
//*/
//public void setInput(Fileset input) {
//	if(input.getFilesetType()!= org.daisy.util.fileset.FilesetType.XHTML_DOCUMENT) {
//		throw new IllegalStateException();
//	}
//	mInputFileset = input;		
//}
///*
//* (non-Javadoc)
//* @see int_daisy_filesetGenerator.IFilesetGenerator#setConfig(java.util.Map)
//*/
//public void setConfig(Map<String, Object> config) {
//	mConfiguration = config;
//}
