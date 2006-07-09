package org.daisy.util.xml.validation.jaxp;

import java.io.IOException;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.InputSource;

/**
 * 
 * @author Markus Gylling
 */
public abstract class AbstractSchema extends Schema {
	URL schemaURL = null;
	SchemaFactory originator = null;
	Source[] sources = null;
	
	protected AbstractSchema(URL schema, SchemaFactory originator){
		this.schemaURL = schema;	
		this.originator = originator;
	}

	protected AbstractSchema(Source[] sources, SchemaFactory originator){
		this.sources = sources;
		this.originator = originator;
	}
	
	/**
	 * @return all schema resources loaded in this Schema object as an InputSource array.
	 * Will attempt to set the systemId on each InputSource.
	 * @throws IOException 
	 */
	public InputSource[] asInputSources() throws IOException {
		InputSource[] inputSources = null;
		if(schemaURL != null) {
			inputSources = new InputSource[1];
			InputSource is = new InputSource(schemaURL.openStream());
			is.setSystemId(schemaURL.toExternalForm());
			inputSources[0] = is;
		} else {
			inputSources = new InputSource[sources.length];
			for (int i = 0; i < sources.length; i++) {
				Source s = sources[i];
				InputSource is = SAXSource.sourceToInputSource(s);
				inputSources[i] = is;
			}									
		}		
		return inputSources;
	}
}
