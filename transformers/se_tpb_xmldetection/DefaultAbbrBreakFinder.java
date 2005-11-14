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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;

import org.daisy.util.collection.MultiHashMap;
import org.daisy.util.i18n.LocaleUtils;
import org.daisy.util.text.CombinedMatcher;
import org.daisy.util.text.RegexMatcher;
import org.daisy.util.text.StringCollectionMatcher;
import org.daisy.util.text.TextMatcher;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;

/**
 * @author Linus Ericson
 */
/*package*/ class DefaultAbbrBreakFinder extends BreakFinder {
    
    private static Logger logger = Logger.getLogger(DefaultAbbrBreakFinder.class.getName());
    static {        
        logger.setLevel(Level.ALL);
    }
        
    private LangSettingsResolver resolver = null;
    private LangSettings langSettings = null; 
    private Map langSettingsMap = new HashMap();
    
    private MultiHashMap baseInitialisms = new MultiHashMap(false);
    private MultiHashMap baseAcronyms = new MultiHashMap(false);
    
    private boolean override = false;
    
    /**
     * Creates an initialism, acronym, abbreviation and fix break finder.
     * @param xmllang the set of languages to be loaded, typically the set of languages present in the document.
     * @throws CatalogExceptionNotRecoverable
     * @throws XMLStreamException
     * @throws IOException
     */
    public DefaultAbbrBreakFinder(Set xmllang, URL customLang, boolean overrideLang) throws CatalogExceptionNotRecoverable, XMLStreamException, IOException {
        resolver = LangSettingsResolver.getInstance();
        
        logger.info("Loading language: common");
        LangSettings lscommon = new LangSettings(null, resolver.resolve("common"), null);
        langSettingsMap.put("common", lscommon);
        baseInitialisms.putAll(lscommon.getInitialisms());
        baseAcronyms.putAll(lscommon.getAcronyms());
        
        if (customLang != null) {
            logger.info("Loading language: custom");
            LangSettings lscustom = new LangSettings("custom", customLang, null);
            langSettingsMap.put("custom", lscustom);
            baseInitialisms.putAll(lscustom.getInitialisms());
            baseAcronyms.putAll(lscustom.getAcronyms());  
            override = overrideLang;
        }
        
        for (Iterator it = xmllang.iterator(); it.hasNext(); ) {
            String lang = (String)it.next();            
            Locale loc = LocaleUtils.string2locale(lang);
            lang = loc.toString();
            if (!loc.getCountry().equals("")) {
                logger.info("Preloading language: " + loc.getLanguage());
                loadLanguage(loc.getLanguage(), lscommon);
            }
            
            logger.info("Loading language: " + lang);
            loadLanguage(lang, lscommon);
        }
        switchToLang("common");
    }
    
    private void loadLanguage(String locale, LangSettings defaultLS) throws XMLStreamException, IOException {
        if (!langSettingsMap.containsKey(locale)) {
	        URL langURL = resolver.resolve(locale);
	        
	        // Check to see if parent locale should be used as base
	        Locale loc = LocaleUtils.string2locale(locale);
	        if (!loc.getCountry().equals("")) {
	            if (langSettingsMap.containsKey(loc.getLanguage())) {
	                logger.info("Using " + loc.getLanguage() + " as base language for " + loc.toString());
	                defaultLS = (LangSettings)langSettingsMap.get(loc.getLanguage());
	            }
	        }
	        
	        LangSettings ls = null;
	        if (langURL != null) {
	            ls = new LangSettings(locale, langURL, defaultLS);
	        } else {
	            logger.warning("No language settings found for " + locale);
	            ls = new LangSettings(locale, defaultLS);
	        }
	        baseInitialisms.putAll(ls.getInitialisms());
	        baseAcronyms.putAll(ls.getAcronyms());
	        langSettingsMap.put(locale, ls);
        } else {
            //System.err.println(locale + " already exists.");
        }
    }
    
    private void switchToLang(String lang) {
        langSettings = (LangSettings)langSettingsMap.get(lang);   
        MultiHashMap newInitialisms = new MultiHashMap(baseInitialisms);
        MultiHashMap newAcronyms = new MultiHashMap(baseAcronyms);
        newInitialisms.putAll(langSettings.getInitialisms());
        newAcronyms.putAll(langSettings.getAcronyms());
        langSettings.setInitialisms(newInitialisms);
        langSettings.setAcronyms(newAcronyms);
        
        if (override) {
	        LangSettings lscustom = (LangSettings)langSettingsMap.get("custom");        
	        replaceWithCustom(lscustom.getInitialisms(), langSettings.getInitialisms());
	        replaceWithCustom(lscustom.getAcronyms(), langSettings.getAcronyms());
	        replaceWithCustom(lscustom.getAbbrs(), langSettings.getAbbrs());
        }
               
    }
    
    private void replaceWithCustom(MultiHashMap customMap, MultiHashMap langMap) {
        for (Iterator it = customMap.keySet().iterator(); it.hasNext(); ) {
            String key = (String)it.next();
            if (langMap.containsKey(key)) {
                langMap.remove(key);
                langMap.putAll(key, customMap.getCollection(key));
            }
        }
    }
    
    public Vector findBreaks(String text, ArrayList al) {
        // Has the locale changed?
        if ((newLocale != null && !newLocale.equals(current)) ||
                (newLocale == null && current != null)) {            
            current = newLocale;
            if (current == null) {
                current = new Locale("common");
            }
            if (!langSettingsMap.containsKey(current.toString())) {
                throw new IllegalStateException("Language " + current + " should already be present!");
            } 
            switchToLang(current.toString());               
        }
        
        Vector result = new Vector();
        
        TextMatcher acroScm = new StringCollectionMatcher(langSettings.getAcronyms().keySet(), text, langSettings.getAcronymSuffixPattern());
        TextMatcher initScm = new StringCollectionMatcher(langSettings.getInitialisms().keySet(), text, langSettings.getInitialismSuffixPattern());
        TextMatcher abbrScm = new StringCollectionMatcher(langSettings.getAbbrs().keySet(), text, null);        
        TextMatcher rm = new RegexMatcher(langSettings.getFixPattern(), text, 0);
        
        TextMatcher m1 = new CombinedMatcher(acroScm, initScm);
        TextMatcher m2 = new CombinedMatcher(abbrScm, rm);
        TextMatcher m = new CombinedMatcher(m1, m2);
        while (m.find()) {
            String match = m.getMatch();
            
            // OK, we have found a match. What type was it?
            int type = langSettings.getType(match);
            if (type != Abbr.INITIALISM && type != Abbr.ACRONYM && type != Abbr.ABBREVIATION && type != Abbr.FIX) {
                // Not a single type. Not much to do really since different types are
                // allowed in different contexts. Let's skip this match.
                logger.finer("Not a single match (text=" + text.substring(m.getStart(), m.getEnd()) + ", type=" + type + "). Skipping...");
            } else {
	            // Is that allowed in this context? 
	            if (langSettings.allowedContext(text, m.getStart(), m.getEnd(), type)) {
	                Abbr abbr = new Abbr(match, langSettings.expand(match, type), type, m.getStart(), m.getEnd());
	                result.add(abbr);
	                logger.finer(abbr + "\t" + current);	                
	            } else {
	                logger.finer("Not allowed in context: ..." + text.substring(Math.max(0,m.getStart()-5), Math.min(m.getEnd()+5, text.length())) + "... [" + text.substring(m.getStart(), m.getEnd()) + "]");	                
	            }
            }
        }        
        
        return result;        
    }
 
}
