/*
 * Daisy Pipeline
 * Copyright (C) 2007  Daisy Consortium
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
package org.daisy.pipeline.core.script;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.stream.StreamSource;

import org.daisy.pipeline.core.script.datatype.BooleanDatatype;
import org.daisy.pipeline.core.script.datatype.DirectoryDatatype;
import org.daisy.pipeline.core.script.datatype.EnumDatatype;
import org.daisy.pipeline.core.script.datatype.EnumItem;
import org.daisy.pipeline.core.script.datatype.FileDatatype;
import org.daisy.pipeline.core.script.datatype.FilesDatatype;
import org.daisy.pipeline.core.script.datatype.IntegerDatatype;
import org.daisy.pipeline.core.script.datatype.StringDatatype;
import org.daisy.pipeline.exception.NotSupposedToHappenException;
import org.daisy.util.xml.pool.PoolException;
import org.daisy.util.xml.pool.StAXInputFactoryPool;

/**
 * A singleton class for parsing task script files into Script objects.
 * @author Linus Ericson
 */
class Parser {

	// Singleton instance
	private static Parser sInstance = null;
			
	private static String SCRIPT = "taskScript";
	private static String SCRIPT_NICENAME = "nicename";
	private static String SCRIPT_DESCRIPTION = "description";
	private static String SCRIPT_DOCUMENTATION = "documentation";
	
	private static String TASK = "task";
	
	private static String DATATYPE = "datatype";
	private static String DATATYPE_FILE = "file";
	private static String DATATYPE_FILES = "files";
	private static String DATATYPE_DIRECTORY = "directory";
	private static String DATATYPE_STRING = "string";
	private static String DATATYPE_INTEGER = "integer";
	private static String DATATYPE_ENUM = "enum";
	private static String DATATYPE_BOOLEAN = "boolean";
	private static String DATATYPE_ENUMITEM = "item";
	
	private Map<String, Object> mXifProperties = null;
	private XMLInputFactory mFactory;
	
	/**
	 * Constructor.
	 * @throws XMLStreamException
	 */
	private Parser() throws XMLStreamException {
		mXifProperties = new HashMap<String, Object>();
		mXifProperties.put(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
		mXifProperties.put(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
		mXifProperties.put(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.TRUE);
		mXifProperties.put(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.TRUE);
		mXifProperties.put(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
		StAXInputFactoryPool pool = StAXInputFactoryPool.getInstance();

		try {
			mFactory = pool.acquire(mXifProperties);
		} catch (PoolException e) {		
			throw new XMLStreamException("Could not acquire an XMLInputFactory", e);
		}
	}
	
	/**
	 * Gets the singleton Parser instance.
	 * @return a Parser
	 * @throws XMLStreamException
	 */
	public static Parser getInstance() throws XMLStreamException {
		if (sInstance == null) {
			synchronized (Parser.class) {
				if (sInstance == null) {
					sInstance = new Parser();
				}
			}
		}
		return sInstance;
	}
	
	/**
	 * Create a new script object from a URL.
	 * @param url the url of the script file
	 * @return a Script object
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	public Script newScript(URL url) throws XMLStreamException, IOException {
		
		Script script = null;

		// Open reader
		StreamSource ss = new StreamSource(url.openStream());
		ss.setSystemId(url.toExternalForm());
		
		XMLEventReader reader = mFactory.createXMLEventReader(ss);
		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
			
			if (event.isStartElement()) {
				StartElement se = event.asStartElement();
				if (SCRIPT.equals(se.getName().getLocalPart())) {
					// Script start tag found. Create the script object.
					script = new Script(url);
					Attribute attrName = se.getAttributeByName(new QName("name"));
					if (attrName != null) {
						script.setName(attrName.getValue());
					}
					// Build the rest of the script object
					this.buildScript(script, reader);
				}
			}
			
		}
		reader.close();

		return script;
	}
	
	/**
	 * Helper method for building the script object.
	 * Stop when the end script tag is found.
	 * @param script the Script object to build
	 * @param reader an XMLEventReader
	 * @throws XMLStreamException
	 */
	private void buildScript(Script script, XMLEventReader reader) throws XMLStreamException {
		StringBuilder characterData = new StringBuilder();
		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
			
			if (event.isStartElement()) {
				StartElement se = event.asStartElement();
				String local = se.getName().getLocalPart();
				if (SCRIPT_NICENAME.equals(local)) {
					characterData.setLength(0);
				} else if (SCRIPT_DESCRIPTION.equals(local)) {
					characterData.setLength(0);
				} else if (SCRIPT_DOCUMENTATION.equals(local)) {
					Attribute uri = se.getAttributeByName(new QName("uri"));
					try {
						script.setDocumentation(script.getScriptURL().toURI().resolve(new URI(uri.getValue())));
					} catch (URISyntaxException e) {
						//non-terminating, alas no contact with listeners here
						System.err.println("URISyntaxException when parsing script documentation URI: " + uri.getValue());
					}
				} else if ("property".equals(local)) {
					// Add a property
					Attribute attrName = se.getAttributeByName(new QName("name"));
					Attribute attrValue = se.getAttributeByName(new QName("value"));
					if (attrName != null && attrValue != null) {
						String name = attrName.getValue();
						String value = attrValue.getValue();
						try {
							script.addProperty(name, new ScriptProperty(name, value, script.getProperties()));
						} catch (ScriptValidationException e) {
							throw new XMLStreamException(e.getMessage(), attrValue.getLocation(), e);
						}
					}
				} else if ("parameter".equals(local)) {
					// Add a parameter
					Attribute attrName = se.getAttributeByName(new QName("name"));
					Attribute attrValue = se.getAttributeByName(new QName("value"));
					Attribute attrRequired = se.getAttributeByName(new QName("required"));
					if (attrName != null) {
						String name = attrName.getValue();
						String value = attrValue!=null?attrValue.getValue():null;
													
						boolean required = attrRequired!=null?attrRequired.getValue().equals("true"):false;
						
						// Create the parameter...
						ScriptParameter parameter;
						try {
							parameter = new ScriptParameter(name, value, script.getProperties(), required);
						} catch (ScriptValidationException e) {
							throw new XMLStreamException(e.getMessage(), attrValue.getLocation(), e);
						}
						script.addProperty(name, parameter);
						// ...but create the content of the parameter in a separate method.
						this.buildScriptParameter(parameter, reader);
					}					
				} else if (TASK.equals(local)) {
					// Add a task
					Attribute attrName = se.getAttributeByName(new QName("name"));
					Attribute attrInteractive = se.getAttributeByName(new QName("interactive"));
					if (attrName != null) {
						String name = attrName.getValue();
						boolean interactive = attrInteractive!=null?attrInteractive.getValue().equals("true"):false;
						
						// Create the task...
						Task task = new Task(name, interactive);
						script.addTask(task);
						// ...but create the content of the task in a separate method.
						this.buildTask(task, reader, script.getProperties());
					}
				} 
				
			} else if (event.isEndElement()) {
				EndElement ee = event.asEndElement();
				String local = ee.getName().getLocalPart();
				if (SCRIPT_NICENAME.equals(local)) {
					script.setNicename(characterData.toString());
				} else if (SCRIPT_DESCRIPTION.equals(local)) {
					script.setDescription(characterData.toString());
				} else if (SCRIPT.equals(local)) {
					// End script tag found. Our work here is done.
					return;
				}
			} else if (event.isCharacters()) {
				Characters characters = event.asCharacters();
				characterData.append(characters.getData());
			}
		}		
		throw new NotSupposedToHappenException("We should have found the end script tag, but didn't");
	}
	
	/**
	 * Helper method for building a task object.
	 * Stop when the end task tag is found.
	 * @param task the task object to build
	 * @param reader an XMLEventReader
	 * @param properties the properties for the script
	 * @throws XMLStreamException
	 */
	private void buildTask(Task task, XMLEventReader reader, Map<String,AbstractProperty> properties) throws XMLStreamException {
		StringBuilder characterData = new StringBuilder();
		String paramName = null;
		String paramValue = null;
		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
			
			if (event.isStartElement()) {
				StartElement se = event.asStartElement();
				String local = se.getName().getLocalPart();
				if ("name".equals(local)) {
					characterData.setLength(0);
				} else if ("value".equals(local)) {
					characterData.setLength(0);
				}   
			} else if (event.isEndElement()) {
				EndElement ee = event.asEndElement();
				String local = ee.getName().getLocalPart();
				if ("name".equals(local)) {
					paramName = characterData.toString();
				} else if ("value".equals(local)) {
					paramValue = characterData.toString();
				} else if ("parameter".equals(local)) {					
					try {
						task.addParameter(paramName, new TaskParameter(paramName, paramValue, properties));
					} catch (ScriptValidationException e) {
						throw new XMLStreamException(e.getMessage(), ee.getLocation(), e);
					}
				} else if (TASK.equals(local)) {
					// End task tag found. Our work here is done.
					return;
				}
			} else if (event.isCharacters()) {
				Characters characters = event.asCharacters();
				characterData.append(characters.getData());
			}
		}
		throw new NotSupposedToHappenException("We should have found the end task tag, but didn't");
	}
	
	/**
	 * Helper method for building a script parameter object.
	 * Stop when the end parameter tag is found.
	 * @param parameter the parameter to build
	 * @param reader an XMLEventReader
	 * @throws XMLStreamException
	 */
	private void buildScriptParameter(ScriptParameter parameter, XMLEventReader reader) throws XMLStreamException {
		StringBuilder characterData = new StringBuilder();
		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
			
			if (event.isStartElement()) {
				StartElement se = event.asStartElement();
				String local = se.getName().getLocalPart();
				if ("nicename".equals(local)) {
					characterData.setLength(0);
				} else if ("description".equals(local)) {
					characterData.setLength(0);
				} else if (DATATYPE.equals(local)) {
					// Build the datatype in a separate method
					this.buildDatatype(parameter, reader);
				}
			} else if (event.isEndElement()) {
				EndElement ee = event.asEndElement();
				String local = ee.getName().getLocalPart();
				if ("nicename".equals(local)) {
					parameter.setNicename(characterData.toString());
				} else if ("description".equals(local)) {
					parameter.setDescription(characterData.toString());
				} else if ("parameter".equals(local)) {
					// End parameter tag found. Our work here is done.
					return;
				}
			} else if (event.isCharacters()) {
				Characters characters = event.asCharacters();
				characterData.append(characters.getData());
			}
		}
		throw new NotSupposedToHappenException("We should have found the end parameter tag, but didn't");
	}
	
	/**
	 * Helper method for building a datatype object.
	 * Stop when the end datatype tag is found.
	 * @param parameter the parameter the datatype will belong to
	 * @param reader an XMLEventReader
	 * @throws XMLStreamException
	 */
	private void buildDatatype(ScriptParameter parameter, XMLEventReader reader) throws XMLStreamException {
		List<EnumItem> items = null;
		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
			
			if (event.isStartElement()) {
				StartElement se = event.asStartElement();
				String local = se.getName().getLocalPart();
				if (DATATYPE_FILE.equals(local)) {
					Attribute attrMime = se.getAttributeByName(new QName("mime"));
					Attribute attrType = se.getAttributeByName(new QName("type"));					
					if (attrMime != null && attrType != null) {
						parameter.setDatatype(new FileDatatype(attrMime.getValue(), attrType.getValue()));
					}
				} else if (DATATYPE_FILES.equals(local)) {
					Attribute attrMime = se.getAttributeByName(new QName("mime"));
					Attribute attrType = se.getAttributeByName(new QName("type"));					
					if (attrMime != null && attrType != null) {
						parameter.setDatatype(new FilesDatatype(attrMime.getValue(), attrType.getValue()));
					} 	
				} else if (DATATYPE_DIRECTORY.equals(local)) {
					Attribute attrType = se.getAttributeByName(new QName("type"));					
					if (attrType != null) {
						parameter.setDatatype(new DirectoryDatatype(attrType.getValue()));
					}
				} else if (DATATYPE_BOOLEAN.equals(local)) {
					Attribute attrTrue = se.getAttributeByName(new QName("true"));
					Attribute attrFalse = se.getAttributeByName(new QName("false"));
					String trueVal = attrTrue!=null?attrTrue.getValue():null;
					String falseVal = attrFalse!=null?attrFalse.getValue():null;					
					parameter.setDatatype(new BooleanDatatype(trueVal, falseVal));
										
				} else if (DATATYPE_STRING.equals(local)) {
					Attribute attrRegex = se.getAttributeByName(new QName("regex"));					
					try {
						Pattern regex = attrRegex!=null ? Pattern.compile(attrRegex.getValue()) : null;
						parameter.setDatatype(new StringDatatype(regex));
					} catch (PatternSyntaxException e) {
						throw new XMLStreamException(e.getMessage(), e);
					}						
				} else if (DATATYPE_INTEGER.equals(local)) {
					Attribute attrMin = se.getAttributeByName(new QName("min"));
					Attribute attrMax = se.getAttributeByName(new QName("max"));
					try {
						Integer min = attrMin!=null ? Integer.valueOf(attrMin.getValue()) : null;
						Integer max = attrMax!=null ? Integer.valueOf(attrMax.getValue()) : null;
						parameter.setDatatype(new IntegerDatatype(min, max));
					} catch (NumberFormatException e) {
						throw new XMLStreamException(e.getMessage(), e);
					}
				} else if (DATATYPE_ENUM.equals(local)) {
					items = new LinkedList<EnumItem>();
				} else if (DATATYPE_ENUMITEM.equals(local)) {
					Attribute attrNicename = se.getAttributeByName(new QName("nicename"));
					Attribute attrValue = se.getAttributeByName(new QName("value"));
					if (attrNicename != null && attrValue != null) {
						items.add(new EnumItem(attrNicename.getValue(), attrValue.getValue()));
					}
				} 
			} else if (event.isEndElement()) {
				EndElement ee = event.asEndElement();
				String local = ee.getName().getLocalPart();
				if (DATATYPE_ENUM.equals(local)) {
					parameter.setDatatype(new EnumDatatype(items));
				} else if (DATATYPE.equals(local)) {
					// End datatype tag found. Our work here is done.
					return;
				}
			}			
		}
		throw new NotSupposedToHappenException("We should have found the end datatype tag, but didn't");
	}
	
}
