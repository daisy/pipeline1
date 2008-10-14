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
package se_tpb_speechgen2.tts.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.daisy.util.xml.XPathUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Linus Ericson
 */
class YearAnnouncer {
    
    private Map<Integer,Map<String,String>> numberMapMap = null;
    private Pattern before = null;
    private Pattern after = null;
    private Pattern match = null;
    private int min = 1000;
    private int max = 2499;
    
    public YearAnnouncer(URL configURL) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(configURL.openStream());
        parseConfig(doc);
    }
    
    
    /**
     * Filter a string.
     * @param str the string to filter
     * @return the filtered string
     */
    public String filter(String str) {
        Matcher m = match.matcher(str);
        StringBuffer buffer = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(buffer, maybeReplace(str, m));
        }
        m.appendTail(buffer);
        return buffer.toString();
    }
    
    
    /**
     * Replace the match provided a set of preconditions are met. 
     */
    private String maybeReplace(String str, Matcher matcher) {
        // Check before and after
        String strBefore = str.substring(0, matcher.start());
        if (!before.matcher(strBefore).matches()) {
            return matcher.group(0);
        }
        String strAfter = str.substring(matcher.end(), str.length());
        if (!after.matcher(strAfter).matches()) {
            return matcher.group(0);
        }
        
        // Check interval
        String text = "";
        for (int i = 1; i <= matcher.groupCount(); ++i) {
            Integer in = new Integer(i);
            if (numberMapMap.containsKey(in)) {
                text = text + matcher.group(i);
            }
        }
        if (!"".equals(text)) {
	        int value = Integer.parseInt(text);
	        if (value < min || value > max) {
	            return matcher.group(0);
	        }
        } else {
            return matcher.group(0);
        }
        
        // Replace
        String result = "";
        for (int i = 1; i <= matcher.groupCount(); ++i) {
            Integer in = new Integer(i);
            if (numberMapMap.containsKey(in)) {
                Map<String,String> groupMap = numberMapMap.get(in);
                result = result + groupMap.get(matcher.group(i));
            } else if (matcher.group(i) != null) {
                result = result + matcher.group(i);
            }
        }        
        
        return result;
    }
    
    
    /**
     * Parse configuration file.
     */
    private void parseConfig(Document doc) {
        before = Pattern.compile(XPathUtils.valueOf(doc.getDocumentElement(), "/year/@before"));
        after = Pattern.compile(XPathUtils.valueOf(doc.getDocumentElement(), "/year/@after"));
        match = Pattern.compile(XPathUtils.valueOf(doc.getDocumentElement(), "/year/@match"));
        min = Integer.parseInt(XPathUtils.valueOf(doc.getDocumentElement(), "/year/@min"));
        max = Integer.parseInt(XPathUtils.valueOf(doc.getDocumentElement(), "/year/@max"));
        
        Map<Integer,Map<String,String>> mapmap = new HashMap<Integer,Map<String,String>>();        
        NodeList groups = XPathUtils.selectNodes(doc.getDocumentElement(), "//group");
        for (int i = 0; i < groups.getLength(); ++i) {
            Element group = (Element)groups.item(i);
            Integer groupNumber = Integer.valueOf(group.getAttribute("number"));
            
            Map<String,String> map = new HashMap<String,String>();
            NodeList values = XPathUtils.selectNodes(group, "value");
            for (int j = 0; j < values.getLength(); ++j) {
                Element value = (Element)values.item(j);
                String number = value.getAttribute("number");
                String text = value.getAttribute("text");
                map.put(number, text);
            }
            
            mapmap.put(groupNumber, map);
        }
        
        numberMapMap = mapmap;
    }
    
    
    public String test(String str) {
        return str + " -- " + filter(str); 
    }
    
    
    public static void main(String args[]) throws MalformedURLException, ParserConfigurationException, SAXException, IOException {
        YearAnnouncer ya = new YearAnnouncer(new File("h:/year_se.xml").toURI().toURL());
        System.err.println(ya.test("Jag åt 174 korvar till middag."));
        System.err.println(ya.test("Huset byggdes 1856."));
        System.err.println(ya.test("Huset byggdes på 1920-talet av en slump."));
        
        ya = new YearAnnouncer(new File("h:/year_en.xml").toURI().toURL());
        System.err.println(ya.test("I ate 174 sausages for dinner."));
        System.err.println(ya.test("The house was built 1856."));
        System.err.println(ya.test("The house was built in the 1920s by accident."));
    }

}
