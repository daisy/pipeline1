package int_daisy_opsCreator;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.TransformerException;

import org.daisy.dmfc.core.event.MessageEvent;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.util.xml.LocusTransformer;
import org.daisy.util.xml.pool.PoolException;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.validation.SimpleValidator;
import org.daisy.util.xml.validation.ValidationException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Configuration of NCX contents.
 * @author Markus Gylling
 */
public class NcxBuilderConfiguration implements ErrorHandler {
	private Set<StartElement> mNavMapIncludeFilter = null;					//Template for elements (inc attributes) to include in ncx:navMap
	private Set<StartElement> mNavListIncludeFilter = null;					//Template for elements (inc attributes) to include in ncx:navList
	private Transformer mOwner = null;
	private static final String configNS = "http://www.daisy.org/pipeline/ncxconfig#";
	private QName mQNameLabel = null;
	private QName mQNameText = null;
	private Map<StartElement, List<StartElement>> mLabelMap = null;
	
	public NcxBuilderConfiguration(URL configFile, Transformer owner) throws Exception  {
		mOwner = owner;
		mQNameLabel = new QName(configNS,"label");
		mQNameText = new QName(configNS,"text");
		mLabelMap = new HashMap<StartElement, List<StartElement>>();
		buildIncludeFilters(configFile);
	}

	/**
	 * @return true if inparam StartElement matches an entry in the NavMap filter
	 */
	public boolean matchesNavMapFilter(StartElement se) {
		return (getMatchingFilterEntry(mNavMapIncludeFilter,se))!=null;
	}
	
	/**
	 * @return true if inparam StartElement matches an entry in the NavList filter
	 */
	public boolean matchesNavListFilter(StartElement se) {
		return (getMatchingFilterEntry(mNavListIncludeFilter,se))!=null;
	}
	
	/**
	 * @return a filter entry that matches inparam StartElement, if no match, null.
	 */
	public StartElement getNavListFilterEntry(StartElement se) {
		return getMatchingFilterEntry(mNavListIncludeFilter,se);
	}

	/**
	 * @return a filter entry that matches inparam StartElement, if no match, null.
	 */
	public StartElement getNavMapFilterEntry(StartElement se) {
		return getMatchingFilterEntry(mNavMapIncludeFilter,se);
	}

	/**
	 * @return a label registered in config file for the particular inparam element type and language
	 */
	public String getNavListLabel(StartElement se, String language) {
		try{
			StartElement match = getMatchingFilterEntry(mLabelMap.keySet(),se);
			List<StartElement> labels = mLabelMap.get(match);
			if(labels!=null) { 
				for (StartElement labelElement : labels) {
					boolean foundLabel = false;
					for (Iterator iter = labelElement.getAttributes(); iter.hasNext();) {
						Attribute a = (Attribute) iter.next();					
						if(a.getName().getLocalPart().equals("lang")) {
							if(a.getValue().equals(language)) {
								foundLabel = true;
							}
						}					
					}
					if(foundLabel) {
						Attribute a = labelElement.getAttributeByName(mQNameText);
						if(a!=null) {
							return a.getValue();
						}
					}
				}
			}
		}catch (Exception e) {
			
		}
		return null;
	}
	
	/**
	 * @return a filter template entry that matches inparam StartElement, if no match, null.
	 */
	public StartElement getTemplate(StartElement se) {
		StartElement template = getMatchingFilterEntry(mNavMapIncludeFilter,se);
		if(template==null) {
			template = getMatchingFilterEntry(mNavListIncludeFilter,se);
		}
		return template;
	}
	
	private StartElement getMatchingFilterEntry(Set<StartElement> filter, StartElement event) {
		for (StartElement filterElement : filter) {
			if(filterElement.getName().equals(event.getName())) {
				//QName is the same, check attributes
				//all filterElement attrs and attrvalues must be present on event
				boolean attributesMatch = true;
				for (Iterator iter = filterElement.getAttributes(); iter.hasNext();) {
					Attribute filterAttr = (Attribute) iter.next();
					if(filterAttr.getName().getNamespaceURI().equals(configNS)) continue;
					Attribute eventAttr = event.getAttributeByName(filterAttr.getName());
					if(eventAttr==null) {
						attributesMatch = false;
					}else{
						if(!eventAttr.getValue().equals(filterAttr.getValue())) {
							attributesMatch = false;
						}
					}					
				}
				if(attributesMatch) return filterElement;
			}
		}		
		return null;
	}
		
	/**
	 * Build the mNavMapIncludeFilter and mNavListIncludeFilter sets using the external ncx-include configuration file.
	 * @throws PoolException 
	 * @throws XMLStreamException 
	 * @throws IOException 
	 * @throws TransformerException 
	 * @throws SAXException 
	 * @throws ValidationException 
	 */
	private void buildIncludeFilters(URL config) throws PoolException, XMLStreamException, IOException, SAXException, TransformerException, ValidationException {
		mNavMapIncludeFilter = new HashSet<StartElement>();
		mNavListIncludeFilter = new HashSet<StartElement>();
		
		SimpleValidator sv = new SimpleValidator(this.getClass().getResource("ncx-config.rng"),this);
		sv.validate(config);
		
		XMLInputFactory xif = null;
		Map properties = null;		
		try {
			properties = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(false);
			xif = StAXInputFactoryPool.getInstance().acquire(properties);									
			XMLEventReader xer = xif.createXMLEventReader(config.openStream());
			boolean inNavMapSection = false;
			boolean inNavListSection = false;
			boolean inNavListLabelsSection = false;
			StartElement currentLabelSelector = null;
			while(xer.hasNext()) {
				XMLEvent xe = xer.nextEvent();
			    if(xe.isStartElement() && xe.asStartElement().getName().getLocalPart().equals("navMap")) {
			    	inNavMapSection = true;
			    	continue;
			    }else if(xe.isStartElement() && xe.asStartElement().getName().getLocalPart().equals("navLists")) {
			    	inNavListSection = true;
			    	continue; 
			    }else if(xe.isStartElement() && xe.asStartElement().getName().getLocalPart().equals("navListLabels")) {
			    	inNavListLabelsSection = true;
			    	continue; 
			    }else if(xe.isEndElement() && xe.asEndElement().getName().getLocalPart().equals("navLists")) {
			    	inNavListSection = false;
			    	continue;
			    }else if(xe.isEndElement() && xe.asEndElement().getName().getLocalPart().equals("navMap")) {
			    	inNavMapSection = false;
			    	continue;
			    }else if(xe.isEndElement() && xe.asEndElement().getName().getLocalPart().equals("navListLabels")) {
			    	inNavListLabelsSection = false;
			    	continue;
			    }
			    
			    if(inNavMapSection && xe.isStartElement()) {
			    	mNavMapIncludeFilter.add(xe.asStartElement());
			    }else if(inNavListSection && xe.isStartElement()) {
			    	mNavListIncludeFilter.add(xe.asStartElement());
			    }else if(inNavListLabelsSection && xe.isStartElement()) {
			    	StartElement se = xe.asStartElement();			    
			    	if(se.getName().equals(mQNameLabel)) {
			    		mLabelMap.get(currentLabelSelector).add(se);
			    	}else{
			    		//it is one of the selector elements in an arbitrary namespace
			    		currentLabelSelector=se;
			    		if(!mLabelMap.containsKey(se)) {
			    			mLabelMap.put(se, new LinkedList<StartElement>());
			    		}
			    	}
			    }
			}				

		}finally {
			StAXInputFactoryPool.getInstance().release(xif, properties);
		}				
	}
	
	
	
	public void error(SAXParseException e) throws SAXException {
		Location loc = LocusTransformer.newLocation(e);
		mOwner.sendMessage(e.getMessage(), MessageEvent.Type.ERROR, MessageEvent.Cause.INPUT, loc);		
	}


	public void fatalError(SAXParseException e) throws SAXException {
		Location loc = LocusTransformer.newLocation(e);
		mOwner.sendMessage(e.getMessage(), MessageEvent.Type.ERROR, MessageEvent.Cause.INPUT, loc);		
	}


	public void warning(SAXParseException e) throws SAXException {
		Location loc = LocusTransformer.newLocation(e);
		mOwner.sendMessage(e.getMessage(), MessageEvent.Type.WARNING, MessageEvent.Cause.INPUT, loc);
	}
}
