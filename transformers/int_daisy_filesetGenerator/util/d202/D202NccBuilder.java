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
package int_daisy_filesetGenerator.util.d202;

import int_daisy_filesetGenerator.impl.d202.D202TextOnlyGenerator.GlobalMetadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;

import org.daisy.util.dtb.meta.MetadataItem;
import org.daisy.util.dtb.meta.MetadataList;
import org.daisy.util.xml.IDGenerator;
import org.daisy.util.xml.Namespaces;
import org.daisy.util.xml.SmilClock;
import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.daisy.util.xml.pool.StAXOutputFactoryPool;
import org.daisy.util.xml.stax.AttributeByName;


/**
 * A builder for Daisy 2.02 Ncc.
 * @author Markus Gylling
 */
public class D202NccBuilder {
	private final File mDestination;	
	private List<NccNavItem> mNavItems = null;
	private MetadataList mMetaItems = null;		
	private static String XHTML_DTD;
	private static QName qHtml;
	private static QName qHead;
	private static QName qMeta;
	private static QName qBody;
	private static QName qH1;
	private static QName qH2;
	private static QName qH3;
	private static QName qH4;
	private static QName qH5;
	private static QName qH6;
	private static QName qSpan;
	private static QName qA;
	private static QName qTitle;
	private GlobalMetadata mGlobalMetadata = null;	
	private SmilClock mDuration = null;
	private final Charset mOutputCharset;
		
	
	public D202NccBuilder(File destination, GlobalMetadata metadata, Charset outputCharset) {
		mDestination = destination;
		mOutputCharset = outputCharset;		
		mNavItems = new ArrayList<NccNavItem>();
		mMetaItems = new MetadataList();
		XHTML_DTD = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";
		qHtml = new QName(Namespaces.XHTML_10_NS_URI,"html");
		qHead = new QName(Namespaces.XHTML_10_NS_URI,"head");
		qMeta = new QName(Namespaces.XHTML_10_NS_URI,"meta");
		qBody = new QName(Namespaces.XHTML_10_NS_URI,"body");
		qH1 = new QName(Namespaces.XHTML_10_NS_URI,"h1");
		qH2 = new QName(Namespaces.XHTML_10_NS_URI,"h2");
		qH3 = new QName(Namespaces.XHTML_10_NS_URI,"h3");
		qH4 = new QName(Namespaces.XHTML_10_NS_URI,"h4");
		qH5 = new QName(Namespaces.XHTML_10_NS_URI,"h5");
		qH6 = new QName(Namespaces.XHTML_10_NS_URI,"h6");
		qSpan = new QName(Namespaces.XHTML_10_NS_URI,"span");
		qA= new QName(Namespaces.XHTML_10_NS_URI,"a");
		qTitle= new QName(Namespaces.XHTML_10_NS_URI,"title");
		mGlobalMetadata = metadata;
		mDuration = new SmilClock(0);
	}

	/**
	 * Add a navigation item to the ncc, in the order it shall occur.
	 */
	public void addNccNavItem(NccNavItemType type, String smilURI, String text) {
		mNavItems.add(new NccNavItem(type,smilURI,text));
	}
	
	/**
	 * Set the value to provide in ncc:totalTime
	 */
	public void setDuration(SmilClock duration) {
		mDuration = duration;
	}
	
	/**
	 * Retrieve the NccNavItemType of a StartElement
	 * @return the NccNavItemType if matching, or null if no NccNavItemType applies.
	 */
	public NccNavItemType getType(StartElement se) {
		String localName = se.getName().getLocalPart();
		
		if(localName == "h1") {
			return NccNavItemType.H1;
		}else if(localName == "h2") {
			return NccNavItemType.H2;
		}else if(localName == "h3") {
			return NccNavItemType.H3;
		}else if(localName == "h4") {
			return NccNavItemType.H4;
		}else if(localName == "h5") {
			return NccNavItemType.H5;
		}else if(localName == "h6") {
			return NccNavItemType.H6;
		}else if(localName == "span") {			
			Attribute a = AttributeByName.get(new QName("", "class"),se);
			if(a==null) return null;
			String pageType = a.getValue();			
			if(pageType.equals("page-normal")) {
				return NccNavItemType.PAGE_NORMAL;
			}else if(pageType.equals("page-special")) {
				return NccNavItemType.PAGE_SPECIAL;
			}else if(pageType.equals("page-front")) {
				return NccNavItemType.PAGE_FRONT;
			}
		}		
		return null;
	}

	/**
	 * Add a classic <code>meta</code> element to the NCC.
	 */
	
	/*
	 * Required dc metas that we need to track:
	 * dc 	date
	 * dc 	identifier
	 * dc 	language
	 * dc 	publisher
	 * dc 	title
	 */
	private boolean dcDateAdded = false;
	private boolean dcIdentifierAdded = false;
	private boolean dcLanguageAdded = false; //repeatable
	private boolean dcPublisherAdded = false;
	private boolean dcTitleAdded = false;
		
	public void addMetadataItem(String nameAttrValue, String contentAttrValue) {
		//track which of the required metas are added from the outside

		if(nameAttrValue.equals("dc:date")) {
			//ncc only allows one instance of dc:date
			if(dcDateAdded) return;
			dcDateAdded = true;
		}
		
		if(nameAttrValue.equals("dc:identifier")) {
			//ncc only allows one instance of dc:identifier
			if(dcIdentifierAdded) return;
			dcIdentifierAdded = true;
		}
		
		if(nameAttrValue.equals("dc:language")) {
			//ncc only allows several dc:language
			dcLanguageAdded = true;
		}

		if(nameAttrValue.equals("dc:publisher")) {
			//ncc only allows one instance of dc:publisher
			if(dcPublisherAdded) return;
			dcPublisherAdded = true;
		}
		
		if(nameAttrValue.equals("dc:title")) {
			//ncc only allows one instance of dc:title
			if(dcTitleAdded) return;
			dcTitleAdded = true;
		}
		
		if(nameAttrValue.equals("dc:format")) {
			//dc:format will later be set to "Daisy 2.02"
			return;
		}
		
		MetadataItem item = new MetadataItem(qMeta);
		XMLEventFactory xef = StAXEventFactoryPool.getInstance().acquire();
		item.addAttribute(xef.createAttribute("name", nameAttrValue));
		item.addAttribute(xef.createAttribute("content", contentAttrValue));	
		mMetaItems.add(item);
		StAXEventFactoryPool.getInstance().release(xef);
	}
	
	
	/**
	 * Finalize and render the NCC: create internal metadata items, and render to destination.
	 * @throws FileNotFoundException 
	 * @throws XMLStreamException 
	 */
	public File render() throws FileNotFoundException, XMLStreamException {
		
		Map<String, Object> properties = StAXOutputFactoryPool.getInstance().getDefaultPropertyMap();
		XMLOutputFactory xof = StAXOutputFactoryPool.getInstance().acquire(properties);
		FileOutputStream fos = new FileOutputStream(mDestination);
		XMLEventWriter writer = xof.createXMLEventWriter(fos,mOutputCharset.name());
		XMLEventFactory xef = StAXEventFactoryPool.getInstance().acquire();
						
		Attribute classPageNormal = xef.createAttribute("class", "page-normal");
		Attribute classPageSpecial = xef.createAttribute("class", "page-special");
		Attribute classPageFront = xef.createAttribute("class", "page-front");
		
		writer.add(xef.createStartDocument(mOutputCharset.name(),"1.0"));
		writer.add(xef.createDTD(XHTML_DTD));
		
		Set<Namespace> xhtmlNS = new HashSet<Namespace>();
		xhtmlNS.add(xef.createNamespace(Namespaces.XHTML_10_NS_URI));
		writer.add(xef.createStartElement(qHtml,null,xhtmlNS.iterator()));
		
		writer.add(xef.createStartElement(qHead,null,null));

		//add internal metas
		
		writer.add(xef.createStartElement(qMeta,null,null));
		writer.add(xef.createAttribute("content", "application/xhtml+xml; charset=" + mOutputCharset.name()));
		writer.add(xef.createAttribute("http-equiv", "Content-type"));
		writer.add(xef.createEndElement(qMeta,null));
		
		writer.add(xef.createStartElement(qMeta,null,null));
		writer.add(xef.createAttribute("name", "dc:format"));
		writer.add(xef.createAttribute("content", "Daisy 2.02"));
		writer.add(xef.createEndElement(qMeta,null));
		
		writer.add(xef.createStartElement(qMeta,null,null));
		writer.add(xef.createAttribute("name", "ncc:charset"));
		writer.add(xef.createAttribute("content", mOutputCharset.name()));
		writer.add(xef.createEndElement(qMeta,null));
		
		writer.add(xef.createStartElement(qMeta,null,null));
		writer.add(xef.createAttribute("name", "ncc:pageNormal"));
		writer.add(xef.createAttribute("content", countType(NccNavItemType.PAGE_NORMAL,mNavItems)));
		writer.add(xef.createEndElement(qMeta,null));
		
		writer.add(xef.createStartElement(qMeta,null,null));
		writer.add(xef.createAttribute("name", "ncc:pageSpecial"));
		writer.add(xef.createAttribute("content", countType(NccNavItemType.PAGE_SPECIAL,mNavItems)));
		writer.add(xef.createEndElement(qMeta,null));
		
		writer.add(xef.createStartElement(qMeta,null,null));
		writer.add(xef.createAttribute("name", "ncc:pageFront"));
		writer.add(xef.createAttribute("content", countType(NccNavItemType.PAGE_FRONT,mNavItems)));
		writer.add(xef.createEndElement(qMeta,null));
		
		writer.add(xef.createStartElement(qMeta,null,null));
		writer.add(xef.createAttribute("name", "ncc:totalTime"));
		writer.add(xef.createAttribute("content", mDuration.toString(SmilClock.FULL)));
		writer.add(xef.createEndElement(qMeta,null));
		
		writer.add(xef.createStartElement(qMeta,null,null));
		writer.add(xef.createAttribute("name", "ncc:maxPageNormal"));
		writer.add(xef.createAttribute("content", getMaxPageNormal(mNavItems)));
		writer.add(xef.createEndElement(qMeta,null));

		writer.add(xef.createStartElement(qMeta,null,null));
		writer.add(xef.createAttribute("name", "ncc:tocItems"));
		writer.add(xef.createAttribute("content", Integer.toString(mNavItems.size())));
		writer.add(xef.createEndElement(qMeta,null));
		
		writer.add(xef.createStartElement(qMeta,null,null));
		writer.add(xef.createAttribute("name", "ncc:depth"));
		writer.add(xef.createAttribute("content", getDepth(mNavItems)));
		writer.add(xef.createEndElement(qMeta,null));
		
		writer.add(xef.createStartElement(qTitle,null,null));
		writer.add(xef.createCharacters(mGlobalMetadata.mPublicationTitle));		
		writer.add(xef.createEndElement(qTitle,null));
		
		//add metas added externally
		mMetaItems.asXMLEvents(writer);
		
		//add dc metas that didnt arrive externally
		if(!dcDateAdded) {
			writer.add(xef.createStartElement(qMeta,null,null));
			writer.add(xef.createAttribute("name", "dc:date"));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");			
			writer.add(xef.createAttribute("content", sdf.format(new Date())));
			writer.add(xef.createEndElement(qMeta,null));
		}
		if(!dcIdentifierAdded) {
			writer.add(xef.createStartElement(qMeta,null,null));
			writer.add(xef.createAttribute("name", "dc:identifier"));
			writer.add(xef.createAttribute("content", mGlobalMetadata.mPublicationIdentifier));
			writer.add(xef.createEndElement(qMeta,null));	
		}
		if(!dcLanguageAdded) {
			writer.add(xef.createStartElement(qMeta,null,null));
			writer.add(xef.createAttribute("name", "dc:language"));
			writer.add(xef.createAttribute("content", Locale.getDefault().getLanguage()));
			writer.add(xef.createEndElement(qMeta,null));
		}
		if(!dcPublisherAdded) {
			writer.add(xef.createStartElement(qMeta,null,null));
			writer.add(xef.createAttribute("name", "dc:publisher"));
			writer.add(xef.createAttribute("content", "Unknown Publisher"));
			writer.add(xef.createEndElement(qMeta,null));			
		}
		if(!dcTitleAdded) {
			writer.add(xef.createStartElement(qMeta,null,null));
			writer.add(xef.createAttribute("name", "dc:title"));
			writer.add(xef.createAttribute("content", mGlobalMetadata.mPublicationTitle));
			writer.add(xef.createEndElement(qMeta,null));				
		}
		
		
		writer.add(xef.createEndElement(qHead,null));	
		
		IDGenerator idGen = new IDGenerator("ncc_");
		writer.add(xef.createStartElement(qBody,null,null));
		
		int i = 0;
		QName currentQName = qH1;
		for(NccNavItem navItem : mNavItems) {			
			if(i>0) {
				currentQName = getQName(navItem.mType);
			}				
			writer.add(xef.createStartElement(currentQName,null,null));
			
			if(i==0)writer.add(xef.createAttribute("class", "title"));
			if(navItem.mType==NccNavItemType.PAGE_FRONT) {writer.add(classPageFront);}
			else if(navItem.mType==NccNavItemType.PAGE_SPECIAL) {writer.add(classPageSpecial);}
			else if(navItem.mType==NccNavItemType.PAGE_NORMAL){writer.add(classPageNormal);}
			
			writer.add(xef.createAttribute("id",idGen.generateId()));
			writer.add(xef.createStartElement(qA,null,null));
			writer.add(xef.createAttribute("href",navItem.mSmilURI));
			writer.add(xef.createCharacters(navItem.mText));
			writer.add(xef.createEndElement(qA,null));			
			writer.add(xef.createEndElement(currentQName,null));
			
			i++;
		}
					
		writer.add(xef.createEndElement(qBody,null));		
		writer.add(xef.createEndElement(qHtml,null));		
		writer.add(xef.createEndDocument());
		writer.flush();
		writer.close();
		try {
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		StAXOutputFactoryPool.getInstance().release(xof, properties);
		StAXEventFactoryPool.getInstance().release(xef);
		return mDestination;
	}
	
	private String getDepth(List<NccNavItem> navItems) {
		int depth = 1;
		for(NccNavItem ni : navItems) {			
			if(ni.mType == NccNavItemType.H2 && depth <2) depth = 2;
			if(ni.mType == NccNavItemType.H3 && depth <3) depth = 3;
			if(ni.mType == NccNavItemType.H4 && depth <4) depth = 4;
			if(ni.mType == NccNavItemType.H5 && depth <5) depth = 5;
			if(ni.mType == NccNavItemType.H6 && depth <6) depth = 6;
		}
		return Integer.toString(depth);		
	}

	private String getMaxPageNormal(List<NccNavItem> navItems) {
		String value = "0"; 
		ListIterator<NccNavItem> iter = navItems.listIterator(navItems.size());
		while(iter.hasPrevious()) {
			NccNavItem ni = iter.previous();
			if(ni.mType == NccNavItemType.PAGE_NORMAL) return ni.mText.trim();
		}	
		return value;
	}

	private String countType(NccNavItemType type, List<NccNavItem> navItems) {
		int count = 0;
		for(NccNavItem ni : navItems) {
			if(ni.mType == type) count++;
		}
		return Integer.toString(count);
	}

	/**
	 * Get an element QName matching the NavItem type.
	 */
	private QName getQName(NccNavItemType type) {
		switch(type) {
			case H1: return qH1;
			case H2: return qH2;
			case H3: return qH3;
			case H4: return qH4;
			case H5: return qH5;
			case H6: return qH6;
		}
		return qSpan;
	}

	class NccNavItem {
		NccNavItemType mType;
		String mSmilURI; 
		String mText;
		
		NccNavItem(NccNavItemType type, String smilURI, String text) {
			mType = type;
			mSmilURI = smilURI;
			mText = text;			
		}
	}
	
	public enum NccNavItemType {
		H1,H2,H3,H4,H5,H6,
		PAGE_NORMAL, PAGE_SPECIAL, PAGE_FRONT;
	}


}
