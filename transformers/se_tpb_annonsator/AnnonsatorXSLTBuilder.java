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
package se_tpb_annonsator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.xml.XPathUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Generates an XSLT to introduce structural announcements in a XML file.
 * By setting the value of <tt>outputFilename</tt> to other than 
 * <tt>null</tt> by using the the <tt>setOutputFilename(String)</tt> method, the
 * <tt>AnnonsatorXSLTBuilder</tt> instance writes the produced xsl to file. 
 * 
 * @author Linus Ericson
 * @author Martin Blomberg
 */
public class AnnonsatorXSLTBuilder {

	private DocumentBuilder builder;
	private Set<String> languages;
	private Document config;
	private Document template;
	
//	private String xmlnsAttr;
//	private String xmlnsValue;
	private Map<String,String> xmlnsAttrs;
	
	
	private String outputFilename;
	
	
	/**
	 * Constructs an <tt>AnnonsatorXSLTBuilder</tt> instance that uses the following 
	 * files to work.
	 * 
	 * @param configFile the file used for configuration of the XSLT generator.
	 * @param templateFileAttrib the template file used in case of attribute generation.
	 * @param templateFileText the template file used in case of next node modifiactions.
	 * @throws TransformerRunException
	 */
	public AnnonsatorXSLTBuilder(URL configFile, URL templateFileAttrib, URL templateFileText) throws TransformerRunException {
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    factory.setNamespaceAware(true);
	    //factory.setValidating(true);
	    try {
	    	xmlnsAttrs = new HashMap<String,String>();
	        builder = factory.newDocumentBuilder();
	        config = builder.parse(configFile.openStream());
	        
	        // find the xmlns
	        NamedNodeMap rootAttributes = config.getDocumentElement().getAttributes();
	        for (int i = 0; i < rootAttributes.getLength(); ++i) {
	            Attr attr = (Attr)rootAttributes.item(i);
	            if (attr.getName().startsWith("xmlns")) {
	            	xmlnsAttrs.put(attr.getName(), attr.getValue());	            	
	            }
	        }
	        
	        // find out which languages are used in the rules
	        languages = new HashSet<String>();
	        NodeList list = XPathUtils.selectNodes(config.getDocumentElement(), "//lang[@lang]");
			for (int i = 0; i < list.getLength(); ++i) {
				Node node = list.item(i);
				Element element = (Element)node;
				String lang = element.getAttribute("lang");
				languages.add(lang);
			}
			
			// add attributes or modify text nodes?
			if (XPathUtils.valueOf(config, "//addAttributes").equals("true")) {
 				addAttributes(templateFileAttrib);
			} else {
				modifyTextNodes(templateFileText);
			}
			
		} catch (ParserConfigurationException e) {
			throw new TransformerRunException(e.getMessage(), e);
		} catch (SAXException e) {
			throw new TransformerRunException(e.getMessage(), e);
		} catch (IOException e) {
			throw new TransformerRunException(e.getMessage(), e);
		}
	}
        
    
	/**
	 * Generates an XSLT from the template pointed to by <tt>templateFile</tt>. The
	 * generated XSLT modifies text nodes in an input XML document in order to make 
	 * strucural announcements. 
	 * 
	 * @param templateFile a <tt>URL</tt> pointing at the xsl-template file
	 * @throws TransformerRunException
	 */
	private void modifyTextNodes(URL templateFile) throws TransformerRunException {
	
		try {
			template = builder.parse(templateFile.openStream());
			Element elem = template.getDocumentElement(); 
			Iterator<String> iter = xmlnsAttrs.keySet().iterator();
			while(iter.hasNext()) {
				String name = iter.next();
				elem.setAttribute(name, xmlnsAttrs.get(name));	
			}
			
			
			Element ifBefore = template.getElementById("ifBefore");
			for (Iterator<String> it = languages.iterator(); it.hasNext(); ) {
			    String lang = it.next();
			    Element ifBeforeClone = (Element)ifBefore.cloneNode(true);
			    ifBeforeClone.setAttribute("test", "lang('" + lang + "')");
			    ifBeforeClone.removeAttribute("id");
			    
			    NodeList params = ifBeforeClone.getElementsByTagNameNS("*", "with-param");
			    for (int i = 0; i < params.getLength(); ++i) {
			        Element param = (Element)params.item(i);
			        if ("ifBeforeLang".equals(param.getAttribute("id"))) {
			            param.removeAttribute("id");
			            param.getFirstChild().setNodeValue(lang);
			        }
			    }
			    
			    ifBefore.getParentNode().insertBefore(ifBeforeClone, ifBefore);
			}            
			ifBefore.getParentNode().removeChild(ifBefore);
			
			Element ifAfter = template.getElementById("ifAfter");
			for (Iterator<String> it = languages.iterator(); it.hasNext(); ) {
			    String lang = it.next();
			    Element ifAfterClone = (Element)ifAfter.cloneNode(true);
			    ifAfterClone.setAttribute("test", "lang('" + lang + "')");
			    ifAfterClone.removeAttribute("id");
			    
			    NodeList params = ifAfterClone.getElementsByTagNameNS("*", "with-param");
			    for (int i = 0; i < params.getLength(); ++i) {
			        Element param = (Element)params.item(i);
			        if ("ifAfterLang".equals(param.getAttribute("id"))) {
			            param.removeAttribute("id");
			            param.getFirstChild().setNodeValue(lang);
			        }
			    }
			    
			    ifAfter.getParentNode().insertBefore(ifAfterClone, ifAfter);
			}            
			ifAfter.getParentNode().removeChild(ifAfter);
			
			Element testBefore = template.getElementById("testBefore");
			Element testAfter = template.getElementById("testAfter");
			NodeList rules = config.getElementsByTagName("rule");
			for (int i = 0; i < rules.getLength(); ++i) {
			    Element rule = (Element)rules.item(i);
			    String match = rule.getAttribute("match");
			    Element testBeforeClone = (Element)testBefore.cloneNode(true);
			    Element testAfterClone = (Element)testAfter.cloneNode(true);
			    testBeforeClone.setAttribute("test", testBeforeClone.getAttribute("test").replaceAll("XXX", match));
			    testBeforeClone.removeAttribute("id");
			    testAfterClone.setAttribute("test", testAfterClone.getAttribute("test").replaceAll("XXX", match));
			    testAfterClone.removeAttribute("id");
			    
			    NodeList whens = testBeforeClone.getElementsByTagNameNS("*", "when");
			    for (int j = 0; j < whens.getLength(); ++j) {
			        Element when = (Element)whens.item(j);
			        if ("testBeforeLang".equals(when.getAttribute("id"))) {
			            when.removeAttribute("id");
			            
			            NodeList langs = rule.getElementsByTagName("lang");
			            for (int k = 0; k < langs.getLength(); ++k) {
			                Element lang = (Element)langs.item(k);
			                String langAttr = lang.getAttribute("lang");
			                String before = XPathUtils.valueOf(lang, "before");
			                
			                Element whenClone = (Element)when.cloneNode(true);
			                whenClone.setAttribute("test", "$lang='" + langAttr + "'");
			                whenClone.getFirstChild().setNodeValue(before + " ");
			                when.getParentNode().insertBefore(whenClone, when);
			            }
			            when.getParentNode().removeChild(when);
			        }
			    }                
			    testBefore.getParentNode().insertBefore(testBeforeClone, testBefore);
			    
			    
			    whens = testAfterClone.getElementsByTagNameNS("*", "when");
			    for (int j = 0; j < whens.getLength(); ++j) {
			        Element when = (Element)whens.item(j);
			        if ("testAfterLang".equals(when.getAttribute("id"))) {
			            when.removeAttribute("id");
			            
			            NodeList langs = rule.getElementsByTagName("lang");
			            for (int k = 0; k < langs.getLength(); ++k) {
			                Element lang = (Element)langs.item(k);
			                String langAttr = lang.getAttribute("lang");
			                String after = XPathUtils.valueOf(lang, "after");
			                
			                Element whenClone = (Element)when.cloneNode(true);
			                whenClone.setAttribute("test", "$lang='" + langAttr + "'");
			                whenClone.getFirstChild().setNodeValue(" " + after);
			                when.getParentNode().insertBefore(whenClone, when);
			            }
			            when.getParentNode().removeChild(when);
			        }
			    }                
			    testAfter.getParentNode().insertBefore(testAfterClone, testAfter);
			    
			}
			testBefore.getParentNode().removeChild(testBefore);
			testAfter.getParentNode().removeChild(testAfter);	
			
			NodeList copyNode = XPathUtils.selectNodes(config, "//copy[@attributes='false']/*");
			/* Insert any copied nodes */
			if (copyNode != null) {
				for (int i = 0; i < copyNode.getLength(); ++i) {
				    Node copyElem = copyNode.item(i);
				    copyElem = template.importNode(copyElem, true);
				    template.getDocumentElement().appendChild(copyElem);
				}
			}
		} catch (FileNotFoundException e) {
			throw new TransformerRunException(e.getMessage(), e);
		} catch (SAXException e) {
			throw new TransformerRunException(e.getMessage(), e);
		} catch (IOException e) {
			throw new TransformerRunException(e.getMessage(), e);
		}
    }
	
	
	/**
	 * Prints the generated XSLT to the file pointed to by the
	 * variable <tt>outputFilename</tt>. Returns <tt>true</tt> in
	 * case of success, <tt>false</tt> otherwise.
	 * 
	 * @return Returns <tt>true</tt> in
	 * case of success, <tt>false</tt> otherwise.
	 */
	public boolean printToFile() {
		if (null == outputFilename) {
			return false;
		}
		
		if (0 == outputFilename.trim().length()) {
			return false;
		}
		
		try {
			TransformerFactory xformFactory = TransformerFactory.newInstance();  
			Transformer idTransform = xformFactory.newTransformer();
			Source input = new DOMSource(template);
			Result output = new StreamResult(new FileOutputStream(outputFilename));
			idTransform.transform(input, output);
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
			ioe.printStackTrace();
			return false;
		} catch (TransformerConfigurationException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			return false;
		} catch (TransformerException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
    
	/**
	 * Generates an XSLT that adds attributes to the input XML-file. The attributes
	 * can be used to announce structural events.
	 * 
	 * @param templateFile a <tt>URL</tt> pointing at the xsl-template file
	 * @throws TransformerRunException
	 */
    private void addAttributes(URL templateFile) throws TransformerRunException {
    	try {
			template = builder.parse(templateFile.openStream());
			
			Element elem = template.getDocumentElement(); 
			Iterator<String> iter = xmlnsAttrs.keySet().iterator();
			while(iter.hasNext()) {
				String name = iter.next();
				elem.setAttribute(name, xmlnsAttrs.get(name));	
			}
			
			String nsPrefix = XPathUtils.valueOf(config, "//prefix");
			String nsURI = XPathUtils.valueOf(config, "//namespace");
			String attribBefore = XPathUtils.valueOf(config, "//attributeBefore");
			String attribAfter = XPathUtils.valueOf(config, "//attributeAfter");
			
			NodeList copyNode = XPathUtils.selectNodes(config, "//copy[@attributes='true']/*");
			
			/* templateRuleClone represents a rule template from the xsl template*/
			Element templateRule = template.getElementById("templateRule");
			
			/* rules represents the rules found in the configuration file */
			NodeList rules = config.getElementsByTagName("rule");
			for (int i = 0; i < rules.getLength(); ++i) {
			    
				Element rule = (Element) rules.item(i);
				
 			    Element templateRuleClone = (Element) templateRule.cloneNode(true);
			    templateRuleClone.setAttribute("match", rule.getAttribute("match"));
			    templateRuleClone.removeAttribute("id");
			    
			    
			    /* get the first xsl:when inside the rule template */
			    Element when = (Element)XPathUtils.selectSingleNode(templateRuleClone, "descendant::*[@id='whenLang']");		    	
			    constructWhenElements(attribBefore, attribAfter, rule, when, "lang('", "')");
			    when.getParentNode().removeChild(when);
			    
			    when = (Element)XPathUtils.selectSingleNode(templateRuleClone, "descendant::*[@id='whenOverride']");	
			    constructWhenElements(attribBefore, attribAfter, rule, when, "$overrideLang='", "'");		    	
			    when.getParentNode().removeChild(when);
			    
			    templateRule.getParentNode().insertBefore(templateRuleClone, templateRule);
			}
			
			templateRule.getParentNode().removeChild(templateRule);
			
			/* set the xml parameters */
			NodeList params = template.getElementsByTagNameNS("*", "param");
			Element paramTemplate = (Element) params.item(0);
			addParameter("prefix", nsPrefix, paramTemplate);
			addParameter("ns", nsURI, paramTemplate);
			addParameter("attrib_before", attribBefore, paramTemplate);
			addParameter("attrib_after", attribAfter, paramTemplate);
			paramTemplate.getParentNode().removeChild(paramTemplate);
			
			/* Insert any copied nodes */
			if (copyNode != null) {
				for (int i = 0; i < copyNode.getLength(); ++i) {
				    Node copyElem = copyNode.item(i);
				    copyElem = template.importNode(copyElem, true);
				    template.getDocumentElement().appendChild(copyElem);
				}
			}

		} catch (SAXException e) {
			throw new TransformerRunException(e.getMessage(), e);
		} catch (IOException e) {
			throw new TransformerRunException(e.getMessage(), e);
		}
    }
    
    /**
     * Used to set the xsl:with-param elements in the case of attributes
     * 
     * @param attribBefore
     * @param attribAfter
     * @param rule
     * @param when
     */
    private void constructWhenElements(String attribBefore, String attribAfter, Element rule, Element when, String before, String after) {
        /* for each language defined for this rule in the configuration file: */
        NodeList langs = rule.getElementsByTagName("lang");
        for (int j = 0; j < langs.getLength(); j++) {
        	Element lang = (Element) langs.item(j);
        	
        	String langAttr = lang.getAttribute("lang");
        	String beforeText = XPathUtils.valueOf(lang, "before");
        	String afterText = XPathUtils.valueOf(lang, "after");
        	
        	// clone the xsl:when, change its parameters
        	// place the clone in the templateRuleClone
        	Element whenClone = (Element) when.cloneNode(true);
        	whenClone.removeAttribute("id");
        	whenClone.setAttribute("test", before + langAttr + after);
        	
        	/* Add the attributes to the "xsl:with-param"-elements: */
        	NodeList params = whenClone.getElementsByTagNameNS("*", "with-param");
        	Element paramTemplate = (Element) params.item(0);
        	for (int paramIndex = 1; paramIndex < params.getLength(); paramIndex++) {
        		params.item(paramIndex).getParentNode().removeChild(params.item(paramIndex));
        	}
        	
        	Element paramTemplateClone = null;
        	// before
        	if (!"".equals(beforeText)) {
        		paramTemplateClone = (Element) paramTemplate.cloneNode(true);
        		paramTemplateClone.setAttribute("name", attribBefore);
        		paramTemplateClone.setTextContent(beforeText);
        		paramTemplate.getParentNode().insertBefore(paramTemplateClone, paramTemplate);
        	}
        	
        	// after...
        	if (!"".equals(afterText)) {
        		paramTemplateClone = (Element) paramTemplate.cloneNode(true);
        		paramTemplateClone.setAttribute("name", attribAfter);
        		paramTemplateClone.setTextContent(afterText);
        		paramTemplate.getParentNode().insertBefore(paramTemplateClone, paramTemplate);
        	}
        	
        	paramTemplate.getParentNode().removeChild(paramTemplate);        	
        	when.getParentNode().insertBefore(whenClone, when);
        }
    }


    /**
     * Adds a parameter to the produced XSL. The parameter <tt>template</tt> is an 
     * <tt>xsl:param<tt> element that is used as a template.
     *  
     * @param paramName name of the produced parameter.
     * @param paramValue value of the produced parameter.
     * @param paramTemplate an <tt>xsl:param</tt> element used as a template. 
     */
    private void addParameter(String paramName, String paramValue, Element paramTemplate) {
    	Element templateClone = (Element) paramTemplate.cloneNode(true);
    	templateClone.setAttribute("name", paramName);
    	templateClone.setTextContent(paramValue);
    	paramTemplate.getParentNode().insertBefore(templateClone, paramTemplate);
    }
   
    
    /**
     * Returns the generated template.
     * @return returns the generated template.
     */
    public Document getTemplate() {
        return template;
    }
    
    /**
     * Sets the value of the variable <tt>outputFilename</tt>.
     * 
     * @param newOutputFilename the new filename.
     */
    public void setOutputFile(String newOutputFilename) {
    	outputFilename = newOutputFilename;
    }
    
    /**
     * Returns the value of <tt>outputFilename</tt>.
     * @return returns the value of <tt>outputFilename</tt>.
     */
    public String getOutputFilename() {
    	return outputFilename;
    }
}
