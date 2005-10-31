/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2005  Daisy Consortium
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
package se_tpb_xmldetection;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.collection.MultiHashMap;

/**
 * @author Linus Ericson
 */
public class LangSettings {
    
    private static Logger logger = Logger.getLogger(LangSettings.class.getName());
    
    protected String language = null;
    
    protected MultiHashMap initialisms = null;
    protected MultiHashMap acronyms = null;
    protected MultiHashMap abbrs = null;
    protected Map fixes = null;
    
    protected Pattern beforeInitialism = null;
    protected Pattern afterInitialism = null;
    protected Pattern beforeAcronym = null;
    protected Pattern afterAcronym = null;
    protected Pattern beforeAbbr = null;
    protected Pattern afterAbbr = null;
    protected Pattern beforeFix = null;
    protected Pattern afterFix = null;
    
    protected Pattern fixPattern = null;
    
    protected String initialismSuffixPattern = null;
    protected String acronymSuffixPattern = null;
    
    public LangSettings(String lang, LangSettings defaultSettings) {
        language = lang;
        this.setInitialisms(defaultSettings.getInitialisms());
        this.setAcronyms(defaultSettings.getAcronyms());
        this.setAbbrs(defaultSettings.getAbbrs());
        this.setFixes(defaultSettings.getFixes());
        this.setBeforeInitialism(defaultSettings.getBeforeInitialism().pattern());
        this.setBeforeAcronym(defaultSettings.getBeforeAcronym().pattern());
        this.setBeforeAbbr(defaultSettings.getBeforeAbbr().pattern());
        this.setBeforeFix(defaultSettings.getBeforeFix().pattern());
        this.setAfterInitialism(defaultSettings.getAfterInitialism().pattern());
        this.setAfterAcronym(defaultSettings.getAfterAcronym().pattern());
        this.setAfterAbbr(defaultSettings.getAfterAbbr().pattern());
        this.setAfterFix(defaultSettings.getAfterFix().pattern());
        this.setInitialismSuffixPattern(defaultSettings.initialismSuffixPattern);
        this.setAcronymSuffixPattern(defaultSettings.acronymSuffixPattern);        
    }
    
    public LangSettings(String lang, URL url) throws XMLStreamException, IOException {
        language = lang;
        
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty("javax.xml.stream.isCoalescing", Boolean.TRUE);
        factory.setProperty("javax.xml.stream.supportDTD", Boolean.FALSE);
        factory.setProperty("javax.xml.stream.isNamespaceAware", Boolean.TRUE);
        
        XMLEventReader er = factory.createXMLEventReader(url.openStream());
                       
        MultiHashMap initialismsMap = new MultiHashMap(false);
        MultiHashMap acronymsMap = new MultiHashMap(false);
        MultiHashMap abbrsMap = new MultiHashMap(false);
        Map fixesMap = new HashMap();
        Map currentMap = null;
        
        while (er.hasNext()) {
            XMLEvent event = er.nextEvent();
            
            if (event.isStartElement()) {
                StartElement se = event.asStartElement();
                String name = se.getName().getLocalPart();
                
                if (name.equals("item")) {
                    // An item inside a fix
                    Attribute key = se.getAttributeByName(new QName("key"));
                    Attribute value = se.getAttributeByName(new QName("value"));
                    currentMap.put(key.getValue(), new Item(key.getValue(), value.getValue(), null));
                } else if (name.equals("key")) {
                    // Delegate to other method
                    MultiHashMap result = handleKey(lang, er);
                    currentMap.putAll(result);
                } else if (name.equals("initialism")) {
                    currentMap = initialismsMap;
                    this.setBeforeInitialism(se.getAttributeByName(new QName("before")).getValue());
                    this.setAfterInitialism(se.getAttributeByName(new QName("after")).getValue());
                    this.setInitialismSuffixPattern(se.getAttributeByName(new QName("suffix")).getValue());
                } else if (name.equals("acronym")) {
                    currentMap = acronymsMap;
                    this.setBeforeAcronym(se.getAttributeByName(new QName("before")).getValue());
                    this.setAfterAcronym(se.getAttributeByName(new QName("after")).getValue());
                    this.setAcronymSuffixPattern(se.getAttributeByName(new QName("suffix")).getValue());
                } else if (name.equals("abbreviation")) {
                    currentMap = abbrsMap;
                    this.setBeforeAbbr(se.getAttributeByName(new QName("before")).getValue());
                    this.setAfterAbbr(se.getAttributeByName(new QName("after")).getValue());
                } else if (name.equals("fix")) {
                    currentMap = fixesMap;
                    this.setBeforeFix(se.getAttributeByName(new QName("before")).getValue());
                    this.setAfterFix(se.getAttributeByName(new QName("after")).getValue());
                }
            }
        }
        er.close();
                
        this.setInitialisms(initialismsMap);
        this.setAcronyms(acronymsMap);
        this.setAbbrs(abbrsMap);
        this.setFixes(fixesMap);        
    }
    
    private MultiHashMap handleKey(String lang, XMLEventReader er) throws XMLStreamException {
        List nameList = new ArrayList();
        List mayEndSentenceList = new ArrayList();
        Set itemSet = null;
        
        MultiHashMap resultMap = new MultiHashMap(false);
        
        // Parser states
        boolean inName = false;
        boolean inExpansion = false;
        
        while (er.hasNext()) {
            XMLEvent event = er.nextEvent();
            
            // Stop when the 'key' end event is found
            if (event.isEndElement() && event.asEndElement().getName().getLocalPart().equals("key")) {
                return resultMap;
            }
            
            if (event.isStartElement()) {
                // Start element
                StartElement se = event.asStartElement();
                
                if (se.getName().getLocalPart().equals("name")) {
                    Attribute mayEndSentence = se.getAttributeByName(new QName("mayEndSentence"));
                    mayEndSentenceList.add(mayEndSentence!=null?mayEndSentence.getValue():null);
                    inName = true;
                } else if (se.getName().getLocalPart().equals("expansion")) {
                    Attribute attId = se.getAttributeByName(new QName("id"));
                    Attribute attPrio = se.getAttributeByName(new QName("priority"));
                    String id = attId!=null?attId.getValue():null;
                    int prio = attPrio!=null?Integer.parseInt(attPrio.getValue()):-1;                    
                    
                    if (nameList.size() != mayEndSentenceList.size()) {
                        throw new RuntimeException("This is not supposed to happen! (empty name element?)");
                    }
                    // For each name element, create a new item. Since we don't know
                    // the expansion yet, we'll fill in that value later
                    itemSet = new HashSet();
                    Iterator itName = nameList.iterator();
                    Iterator itEndSent = mayEndSentenceList.iterator();                    
                    while (itName.hasNext()) {                        
                        String name = (String)itName.next();
                        String mayEndSentence = (String)itEndSent.next();
                        Item item = new Item(name, null, mayEndSentence);
                        item.setId(id);
                        item.setPriority(prio);
                        item.setLang(lang);
                        itemSet.add(item);
                    }
                    inExpansion = true;
                }
            } else if (event.isEndElement()) {
                // End element
                EndElement ee = event.asEndElement();
                
                if (ee.getName().getLocalPart().equals("name")) {
                    inName = false;
                } else if (ee.getName().getLocalPart().equals("expansion")) {
                    inExpansion = false;
                }
            } else if (event.isCharacters()) {
                // Characters
                if (inName) {
                    nameList.add(event.asCharacters().getData());
                } else if (inExpansion) {
                    String expansion = event.asCharacters().getData();
                    // Now we know the expansion. Fill in this value in the items.
                    for (Iterator it = itemSet.iterator(); it.hasNext(); ) {
                        Item item = (Item)it.next();
                        item.setValue(expansion);
                        resultMap.put(item.getKey(), item);
                    }                    
                }
            }
            
        }
        throw new XMLStreamException("End </key> tag was never found!");
    }
    
    
    protected boolean allowedContext(String text, int start, int end, int type) {
        Pattern before = null;
        Pattern after = null;
        switch (type) {
        case Abbr.INITIALISM:
            before = getBeforeInitialism();
        	after = getAfterInitialism(); 
            break;
        case Abbr.ACRONYM:
            before = getBeforeAcronym();
        	after = getAfterAcronym();
            break;
        case Abbr.ABBREVIATION:
            before = getBeforeAbbr();
        	after = getAfterAbbr();
            break;
        case Abbr.FIX:
            before = getBeforeFix();
        	after = getAfterFix();
            break;
        default:
            throw new IllegalArgumentException("Not a valid type: " + type);
        }
        String b = text.substring(0, start);
        String a = text.substring(end);        
        if (before.matcher(b).matches()) {
            if (after.matcher(a).matches()) {
                return true;
            }
        }
        return false;        
    }
    
    protected Pattern buildRegex(Map map, boolean quote, String suffixPattern) {
        StringBuffer pattern = new StringBuffer();
        for (Iterator it = map.keySet().iterator(); it.hasNext(); ) {
            String key = (String)it.next();
            pattern.append("(" + (quote?Pattern.quote(key):key) + ")" + suffixPattern + "|");
        }
        // Remove last '|'
        if (pattern.length() > 0) {
            pattern.setLength(pattern.length() - 1);
        }
        //System.out.println("Pattern: " + pattern);
        return Pattern.compile(pattern.toString(), Pattern.DOTALL);
    }
    
    public String removeSuffix(String key, String expansion, int type) {
        Collection coll = null;
        String suffix = "";
        if (type == Abbr.INITIALISM) {
            suffix = "(?:" + getInitialismSuffixPattern() + ")?";
        } else if (type == Abbr.ACRONYM) {
            suffix = "(?:" + getAcronymSuffixPattern() + ")?";
        }
        Pattern p = Pattern.compile("(" + Pattern.quote(key) + ")" + suffix);
        Matcher m = p.matcher(key);
        if (m.matches()) {
            Item item = null;
            String exp = null;            
            switch (type) {
            case Abbr.INITIALISM:
                coll = initialisms.getCollection(m.group(1));
            	if (coll != null) {
            	    for (Iterator it = coll.iterator(); it.hasNext(); ) {
            	        item = (Item)it.next();
            	        exp = item.getValue();
            	        if (exp.equals(expansion)) {
            	            return m.group(1);
            	        }
            	    }
            	}                
                break;
            case Abbr.ACRONYM:
                coll = acronyms.getCollection(m.group(1));
	        	if (coll != null) {
	        	    for (Iterator it = coll.iterator(); it.hasNext(); ) {
	        	        item = (Item)it.next();
	        	        exp = item.getValue();
	        	        if (exp.equals(expansion)) {
	        	            return m.group(1);
	        	        }
	        	    }
	        	}
                break;
            case Abbr.ABBREVIATION:
                coll = abbrs.getCollection(m.group(1));
	        	if (coll != null) {
	        	    for (Iterator it = coll.iterator(); it.hasNext(); ) {
	        	        item = (Item)it.next();
	        	        exp = item.getValue();
	        	        if (exp.equals(expansion)) {
	        	            return m.group(1);
	        	        }
	        	    }
	        	}
                break;
            default:
                logger.warning("Unknown type: " + key);
            	return null;
            }
            logger.warning("Not found in word list: " + key);
            return null;
        }
        logger.warning("No match found for: " + key);
        return null;
    }
    
    public boolean mayNotEndSentence(String key, String expansion, int type) {
        Collection coll;
        String exp;
        if (key == null) {
            return false;
        }
        
        Item item = null;
        switch (type) {
        case Abbr.INITIALISM:
            coll = initialisms.getCollection(key);
	    	if (coll != null) {
	    	    for (Iterator it = coll.iterator(); it.hasNext(); ) {
	    	        item = (Item)it.next();
	    	        exp = item.getValue();
	    	        if (exp.equals(expansion)) {
	    	            return "no".equals(item.getEndSentence());
	    	        }
	    	    }
	    	} 
	    	break;
        case Abbr.ACRONYM:
            coll = acronyms.getCollection(key);
	    	if (coll != null) {
	    	    for (Iterator it = coll.iterator(); it.hasNext(); ) {
	    	        item = (Item)it.next();
	    	        exp = item.getValue();
	    	        if (exp.equals(expansion)) {
	    	            return "no".equals(item.getEndSentence());
	    	        }
	    	    }
	    	} 
	    	break;
        case Abbr.ABBREVIATION:
            coll = abbrs.getCollection(key);
	    	if (coll != null) {
	    	    for (Iterator it = coll.iterator(); it.hasNext(); ) {
	    	        item = (Item)it.next();
	    	        exp = item.getValue();
	    	        if (exp.equals(expansion)) {
	    	            return "no".equals(item.getEndSentence());
	    	        }
	    	    }
	    	} 
	    	break;
        case Abbr.FIX:
            // FIXME this is wrong
            item = (Item)fixes.get(key);
        	return "no".equals(item.getEndSentence());
        }
        return false;
    }
    
    private boolean sameId(Collection coll) {
        Iterator it = coll.iterator();
        Item firstItem = (Item)it.next();
        String firstId = firstItem.getId();
        if (firstId == null) {
            //System.err.println("First ID = null");
            return false;
        }
        while (it.hasNext()) {
            Item item = (Item)it.next();
            String id = item.getId();
            //System.err.println("ID = " + id);
            if (!firstId.equals(id)) {
                return false;
            }
        }
        return true;
    }
    
    private String getFromBestLanguage(Collection coll) {
        String result = null;
        //System.err.println("Current lang: " + language);
        for (Iterator it = coll.iterator(); it.hasNext(); ) {
            Item item = (Item)it.next();
            //System.err.println("Found lang: " + item.getLang());
            if (language.equals(item.getLang())) {
                result = item.getValue();
                logger.finer("Found match in current language [" + language + "]: " + item.getValue());
                return result;
            } else if (item.getLang() == null) {
                result = item.getValue();
            }
        }
        if (result != null) {
            logger.finer("Found match in language [common]: " + result);
        }
        return result;
    }
    
    public String expand(String key, int type) {
        Collection coll;
        Item item;
        if (type == Abbr.INITIALISM || type == Abbr.ACRONYM) {
            if (type == Abbr.INITIALISM) {
                coll = initialisms.getCollection(key);
            } else {
                coll = acronyms.getCollection(key);
            }
            if (coll == null) {
                logger.warning("No expansion found for " + key);
        	    return null;
        	} else if (coll.size() != 1) {
        	    logger.finer("Multiple choices for " + key + ": " + coll);
        	    String result = null;
        	    if (sameId(coll)) {
        	        //System.err.println("Same ID!");
        	        result = getFromBestLanguage(coll);
        	    }
        	    return result;
        	}        	
    	    item = (Item)coll.iterator().next();
    	    return item.getValue();    
        } else if (type == Abbr.ABBREVIATION) {
            coll = abbrs.getCollection(key);
	    	if (coll == null) {
	    	    logger.warning("No abbr expansion found for " + key);
	    	    return null;
	    	} else if (coll.size() != 1) {
	    	    logger.finer("Multiple abbr choices for " + key + ": " + coll);
	    	    return null;
	    	}
		    item = (Item)coll.iterator().next();
		    return item.getValue();
        } else if (type == Abbr.FIX) {
            for (Iterator it = fixes.keySet().iterator(); it.hasNext(); ) {
                String k = (String)it.next();
                if (Pattern.compile(k, Pattern.DOTALL).matcher(key).matches()) {
                    item = (Item)fixes.get(k);
                    return item!=null?item.getValue():null;
                }
            }
            throw new RuntimeException("No fix match found for " + key);
        } else {
            throw new IllegalArgumentException("Not a valid type: " + type);
        }        
    }
      
    public int getType(String text) {
        int type = 0;
        if (initialisms.containsKey(text)) {
            type |= Abbr.INITIALISM;
        }
        if (acronyms.containsKey(text)) {
            type |= Abbr.ACRONYM;
        }
        if (abbrs.containsKey(text)) {
            type |= Abbr.ABBREVIATION;
        }
        if (type == 0) {
            // FIXME don't assume this, check it!
            //System.err.println("it is a fix");
            type = Abbr.FIX;
        } 
        return type;
    }
    
    public MultiHashMap getAbbrs() {
        return abbrs;
    }
    
    public void setAbbrs(MultiHashMap abbreviations) {
        this.abbrs = abbreviations;        
    }
    
    public MultiHashMap getAcronyms() {
        return acronyms;
    }
    
    public void setAcronyms(MultiHashMap acro) {
        this.acronyms = acro;        
    }
    
    public Collection getCompleteStringCollection() {
        Collection coll = new ArrayList();
        coll.addAll(initialisms.keySet());        
        coll.addAll(acronyms.keySet());
        coll.addAll(abbrs.keySet());        
        return coll;
    }
    
    public Map getFixes() {
        return fixes;
    }
    
    public void setFixes(Map fix) {
        this.fixes = fix;
        fixPattern = buildRegex(fix, false, "");
    }
        
    public Pattern getFixPattern() {
        return fixPattern;
    }
    
    public MultiHashMap getInitialisms() {
        return initialisms;
    }
    
    public void setInitialisms(MultiHashMap inits) {
        this.initialisms = inits;        
    }    
    
    public Pattern getAfterAbbr() {
        return afterAbbr;
    }
    public void setAfterAbbr(String after) {
        this.afterAbbr = Pattern.compile(after, Pattern.DOTALL);
    }
    public Pattern getAfterAcronym() {
        return afterAcronym;
    }
    public void setAfterAcronym(String after) {
        this.afterAcronym = Pattern.compile(after, Pattern.DOTALL);
    }
    public Pattern getAfterFix() {
        return afterFix;
    }
    public void setAfterFix(String after) {
        this.afterFix = Pattern.compile(after, Pattern.DOTALL);
    }
    public Pattern getAfterInitialism() {
        return afterInitialism;
    }
    public void setAfterInitialism(String after) {
        this.afterInitialism = Pattern.compile(after, Pattern.DOTALL);
    }
    public Pattern getBeforeAbbr() {
        return beforeAbbr;
    }
    public void setBeforeAbbr(String before) {
        this.beforeAbbr = Pattern.compile(before, Pattern.DOTALL);
    }
    public Pattern getBeforeAcronym() {
        return beforeAcronym;
    }
    public void setBeforeAcronym(String before) {
        this.beforeAcronym = Pattern.compile(before, Pattern.DOTALL);
    }
    public Pattern getBeforeFix() {
        return beforeFix;
    }
    public void setBeforeFix(String before) {
        this.beforeFix = Pattern.compile(before, Pattern.DOTALL);
    }
    public Pattern getBeforeInitialism() {
        return beforeInitialism;
    }
    public void setBeforeInitialism(String beforeInits) {
        this.beforeInitialism = Pattern.compile(beforeInits, Pattern.DOTALL);
    }
    
    public void setAcronymSuffixPattern(String suffixPattern) {
        this.acronymSuffixPattern = suffixPattern;
    }
    public void setInitialismSuffixPattern(String suffixPattern) {
        this.initialismSuffixPattern = suffixPattern;
    }
    public String getAcronymSuffixPattern() {
        return acronymSuffixPattern;
    }
    public String getInitialismSuffixPattern() {
        return initialismSuffixPattern;
    }
}
