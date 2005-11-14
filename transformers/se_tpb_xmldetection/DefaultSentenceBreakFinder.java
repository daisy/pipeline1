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
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.xml.stream.XMLStreamException;

import org.daisy.util.collection.MultiHashMap;
import org.daisy.util.i18n.LocaleUtils;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;

/**
 * @author Linus Ericson
 */
/*package*/ class DefaultSentenceBreakFinder extends BreakFinder {

    private static Logger logger = Logger.getLogger(DefaultSentenceBreakFinder.class.getName());
    static {        
        logger.setLevel(Level.ALL);
    }
    
    protected BreakIterator iterator = BreakIterator.getSentenceInstance();
    
    protected LangSettingsResolver resolver = null;
    protected LangSettings langSettings = null; 
    protected Map langSettingsMap = new HashMap();
    
    protected MultiHashMap baseInitialisms = new MultiHashMap(false);
    protected MultiHashMap baseAcronyms = new MultiHashMap(false);
    
    private boolean override = false;
    
    public DefaultSentenceBreakFinder(Set xmllang, URL customLang, boolean overrideLang) throws CatalogExceptionNotRecoverable, XMLStreamException, IOException {
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
                logger.info("No language specified. Using default BreakIterator instance.");
                iterator = BreakIterator.getSentenceInstance();
                current = new Locale("common");
            } else {
                iterator = BreakIterator.getSentenceInstance(newLocale);
            }
            if (!langSettingsMap.containsKey(current.toString())) {
                throw new IllegalStateException("Language " + current + " should already be present!");
            } 
            switchToLang(current.toString());
        }
        
        Vector result = new Vector();
        iterator.setText(text);
        int start = iterator.first();
        // Don't add the first break.
        // Add the rest of the breaks to the result
        boolean noBreak = false;
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            noBreak = false;
            for (Iterator it = al.iterator(); it.hasNext(); ) {
                Abbr abbr = (Abbr)it.next();                
                if (abbr.getStart() >= start && abbr.getEnd() <= end) {
                    // There is an Abbr in the current sentence
                    if (Pattern.compile(".*" + Pattern.quote(abbr.getKey()) + "\\s*", Pattern.DOTALL).matcher(text.substring(start, end)).matches()) {
                        // The Abbr is the last thing in this sentence. Is that allowed or is this a false positive
                        logger.finer("abbr inside last: " + abbr.getKey());
                        if (langSettings.mayNotEndSentence(langSettings.removeSuffix(abbr.getKey(), abbr.getExpansion(), abbr.getType()), abbr.getExpansion(), abbr.getType())) {
                            // It was a false positive. Don't add it.
                            noBreak = true;                                
                        }
                    }
                }
            }
            if (!noBreak) {
                result.add(new Integer(end));
            }
        }
        if (result.size() > 0) {
            result.remove(result.size() - 1);
        }
        
        return result;        
    }

}
