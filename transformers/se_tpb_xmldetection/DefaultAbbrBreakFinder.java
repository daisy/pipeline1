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
import java.util.logging.Logger;
import java.util.regex.Matcher;

import javax.xml.stream.XMLStreamException;

import org.daisy.util.collection.MultiHashMap;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;

/**
 * @author Linus Ericson
 */
/*package*/ class DefaultAbbrBreakFinder extends BreakFinder {
    
    private static Logger logger = Logger.getLogger(DefaultAbbrBreakFinder.class.getName());
    
    private LangSettingsResolver resolver = null;
    private LangSettings langSettings = null; 
    private Map langSettingsMap = new HashMap();
    
    private MultiHashMap baseInitialisms = new MultiHashMap(false);
    private MultiHashMap baseAcronyms = new MultiHashMap(false);
    
    /**
     * Creates an initialism, acronym, abbreviation and fix break finder.
     * @param xmllang the set of languages to be loaded, typically the set of languages present in the document.
     * @throws CatalogExceptionNotRecoverable
     * @throws XMLStreamException
     * @throws IOException
     */
    public DefaultAbbrBreakFinder(Set xmllang) throws CatalogExceptionNotRecoverable, XMLStreamException, IOException {
        resolver = LangSettingsResolver.getInstance();
        
        logger.info("Loading language: common");
        LangSettings lscommon = new LangSettings(null, resolver.resolve("common"));
        langSettingsMap.put("common", lscommon);
        baseInitialisms.putAll(lscommon.getInitialisms());
        baseAcronyms.putAll(lscommon.getAcronyms());
        
        for (Iterator it = xmllang.iterator(); it.hasNext(); ) {
            String lang = (String)it.next();
            logger.info("Loading language: " + lang);
            Locale loc = new Locale(lang);
            URL langURL = resolver.resolve(loc);
            LangSettings ls = null;
            if (langURL != null) {
                ls = new LangSettings(lang, langURL);
            } else {
                logger.warning("No language settings found for " + loc);
                ls = new LangSettings(lang, lscommon);
            }
            baseInitialisms.putAll(ls.getInitialisms());
            baseAcronyms.putAll(ls.getAcronyms());
            langSettingsMap.put(loc.toString(), ls);
        }        
    }
    
    public Vector findBreaks(String text, ArrayList al) {
        // Has the locale changed?
        if ((newLocale != null && !newLocale.equals(current)) ||
                (newLocale == null && current != null)) {            
            current = newLocale;
            
            if (!langSettingsMap.containsKey(current.toString())) {
                throw new IllegalStateException("Language " + current + " should already be present!");
            } 
            langSettings = (LangSettings)langSettingsMap.get(current.toString());   
            MultiHashMap newInitialisms = new MultiHashMap(baseInitialisms);
            MultiHashMap newAcronyms = new MultiHashMap(baseAcronyms);
            newInitialisms.putAll(langSettings.getInitialisms());
            newAcronyms.putAll(langSettings.getAcronyms());
            langSettings.setInitialisms(newInitialisms);
            langSettings.setAcronyms(newAcronyms);            
        }
        //System.err.println("\nLanguage: " + current);
        //System.err.println("Text: " + text);        
        
        Vector result = new Vector();
        
        Matcher m = langSettings.getCompletePattern().matcher(text);
        while (m.find()) {
            String match = "";
            for (int i = m.groupCount(); i > 0; --i) {
                if (m.group(i) != null) {
                    match = m.group(i);
                }
            }
            
            // OK, we have found a match. What type was it?
            int type = langSettings.getType(match);
            if (type != Abbr.INITIALISM && type != Abbr.ACRONYM && type != Abbr.ABBREVIATION && type != Abbr.FIX) {
                // Not a single type. Not much to do really since different types are
                // allowed in different contexts. Let's skip this match.
                logger.warning("Not a single match (text=" + text.substring(m.start(), m.end()) + ", type=" + type + "). Skipping...");
            } else {
	            // Is that allowed in this context? 
	            if (langSettings.allowedContext(text, m.start(), m.end(), type)) {
	                Abbr abbr = new Abbr(match, langSettings.expand(match, type), type, m.start(), m.end());
	                result.add(abbr);
	                logger.info("Expansion [lang=" + current + "]: " + abbr);
	                //System.err.println(match + "\t" + langSettings.expand(match, type) + "\t" + current + "\t" + type);
	            } else {
	                logger.info("Not allowed in context: ..." + text.substring(Math.max(0,m.start()-5), Math.min(m.end()+5, text.length())) + "... [" + text.substring(m.start(), m.end()) + "]");	                
	            }
            }
        }
        
        return result;        
    }
 
}
