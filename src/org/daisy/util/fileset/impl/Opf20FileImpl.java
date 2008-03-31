package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;

import org.daisy.util.fileset.OPF20File;
import org.xml.sax.SAXException;

/**
 *
 * @author Markus Gylling
 */
public class Opf20FileImpl extends OpfFileImpl implements OPF20File {

	Opf20FileImpl(URI uri) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
		super(uri, OPF20File.mimeStringConstant);
	}
	private static final long serialVersionUID = -8075114501624569437L;


}
