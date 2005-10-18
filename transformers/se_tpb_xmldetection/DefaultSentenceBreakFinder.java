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
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.xml.stream.XMLStreamException;

import org.daisy.util.collection.MultiHashMap;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;

/**
 * @author Linus Ericson
 */
/*package*/ class DefaultSentenceBreakFinder extends BreakFinder {

    private static Logger logger = Logger.getLogger(DefaultSentenceBreakFinder.class.getName());
    
    protected BreakIterator iterator = BreakIterator.getSentenceInstance();
    
    protected LangSettingsResolver resolver = null;
    protected LangSettings langSettings = null; 
    protected Map langSettingsMap = new HashMap();
    
    protected MultiHashMap baseInitialisms = new MultiHashMap(false);
    protected MultiHashMap baseAcronyms = new MultiHashMap(false);
    
    public DefaultSentenceBreakFinder(Set xmllang) throws CatalogExceptionNotRecoverable, XMLStreamException, IOException {
        resolver = LangSettingsResolver.getInstance();
        LangSettings lscommon = new LangSettings(null, resolver.resolve("common"));
        langSettingsMap.put("common", lscommon);
        baseInitialisms.putAll(lscommon.getInitialisms());
        baseAcronyms.putAll(lscommon.getAcronyms());
        for (Iterator it = xmllang.iterator(); it.hasNext(); ) {
            String lang = (String)it.next();
            logger.info("Lang: " + lang);
            Locale loc = new Locale(lang);
            URL langURL = resolver.resolve(loc);
            LangSettings ls = null;
            if (langURL != null) {
                ls = new LangSettings(lang, langURL);
            } else {
                logger.warning("Warning: no lang settings for " + loc);
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
            iterator = BreakIterator.getSentenceInstance(newLocale);
            current = newLocale;
            try {
                if (!langSettingsMap.containsKey(current.toString())) {
                    URL url = resolver.resolve(current);                    
                    logger.info("Reading lang settings: " + url);
                    langSettings = new LangSettings(current.toString(), url);
                    baseInitialisms.putAll(langSettings.getInitialisms());
                    baseAcronyms.putAll(langSettings.getAcronyms());
                    langSettingsMap.put(current.toString(), langSettings);
                } else {
                    langSettings = (LangSettings)langSettingsMap.get(current.toString());
                    MultiHashMap newInitialisms = new MultiHashMap(baseInitialisms);
                    MultiHashMap newAcronyms = new MultiHashMap(baseAcronyms);
                    newInitialisms.putAll(langSettings.getInitialisms());
                    newAcronyms.putAll(langSettings.getAcronyms());
                    langSettings.setInitialisms(newInitialisms);
                    langSettings.setAcronyms(newAcronyms);
                }                
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
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
                        logger.info("abbr inside last: " + abbr.getKey());
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
