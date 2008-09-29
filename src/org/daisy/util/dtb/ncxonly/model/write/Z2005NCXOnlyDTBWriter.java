/*
 * org.daisy.util (C) 2005-2008 Daisy Consortium
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
package org.daisy.util.dtb.ncxonly.model.write;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import org.daisy.util.dtb.meta.MetadataList;
import org.daisy.util.dtb.ncxonly.model.Model;
import org.daisy.util.dtb.ncxonly.model.Semantic;
import org.daisy.util.dtb.ncxonly.model.write.audio.WAVBuilder;
import org.daisy.util.dtb.ncxonly.model.write.ncx.NCXBuilder;
import org.daisy.util.dtb.ncxonly.model.write.opf.OPFBuilder;
import org.daisy.util.dtb.ncxonly.model.write.smil.SMILBuilder;
import org.daisy.util.file.Directory;
import org.daisy.util.xml.Namespaces;
import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.daisy.util.xml.pool.StAXOutputFactoryPool;

/**
 * Create a Z3986-2005 NCX-Only DTB based on a Model instance.
 * @author Markus Gylling
 */

public class Z2005NCXOnlyDTBWriter {
	private Model mModel = null;
	private Map<String,String> mParameters = null;
	
	
	/**
	 * Constructor.
	 * <p>The Models MetadataList must contain the following items:</p>
	 * <ul>
	 *  <li>dc:Title</li>
	 *  <li>dc:Creator</li>
	 *  <li>dc:Language</li>
	 *  <li>dc:Identifier</li>
	 *  <li>dc:Date</li>
	 * </ul>
	 * @param model The Model to render
	 * @param parameters Recognized parameters are: "pageAutoNumber" ['off'|1-10])
	 */
	public Z2005NCXOnlyDTBWriter(Model model, Map<String,String> parameters) {
		
		if(model==null) throw new NullPointerException();
		if(parameters==null) throw new NullPointerException();
		if(!containsRequiredMetadata(model.getMetadata())) {
			throw new IllegalStateException("missing required metadata");
		}
		mModel = model;		
		mParameters = parameters;
		
	}

	private boolean containsRequiredMetadata(MetadataList metadata) {
		
		String dcNS = Namespaces.DUBLIN_CORE_NS_URI;
		String dcPfx = "dc";		
		Set<QName> reqd = new HashSet<QName>();
		
		reqd.add(new QName(dcNS,"Title",dcPfx));
		reqd.add(new QName(dcNS,"Creator",dcPfx));
		reqd.add(new QName(dcNS,"Language",dcPfx));
		reqd.add(new QName(dcNS,"Identifier",dcPfx));
		reqd.add(new QName(dcNS,"Date",dcPfx));
		
		for(QName req : reqd) {
			if(metadata.get(req)==null) return false;
		}
		return true;
				
	}

	public void write(Directory destination) throws IOException, XMLStreamException {
		XMLEventFactory xef = null;
		XMLOutputFactory xof = null;
		Map<String,Object> properties = null;
		try{
			xef = StAXEventFactoryPool.getInstance().acquire();
			properties = StAXOutputFactoryPool.getInstance().getDefaultPropertyMap();
			//properties.put(XMLOutputFactory.IS_REPAIRING_NAMESPACES, Boolean.FALSE);
			xof = StAXOutputFactoryPool.getInstance().acquire(properties);		
						
			
			/*
			 * Page (re)numbering
			 */
			String autoNumber = mParameters.get("pageAutoNumber"); 
			if(autoNumber!=null &&!autoNumber.equals("off")) {
				mModel.revalue(Semantic.PAGE_NORMAL,Integer.decode(autoNumber));				
			}
			
			/*
			 * Instantiate the SMILBuilder but dont render until
			 * we have changed audiofile references.
			 */
									
			SMILBuilder smilBuilder = new SMILBuilder(mModel);
			
			/*
			 * Create one WAV file per SMIL file, render the audio.
			 * Change references in smilBuilder to the new audio.
			 */
			new WAVBuilder(smilBuilder,destination);
	
			/*
			 * Merge clips that are marked for merging, if any.
			 */			
			smilBuilder.mergeClips();
			
			//TODO phrase detection on the smilBuilder
			
			/*
			 * Render the SMIL files.
			 */
			smilBuilder.render(destination,xef,xof);
			
			/*
			 * Create the NCX, render. Every Item in Model is represented there.
			 */
			NCXBuilder ncxBuilder = new NCXBuilder(mModel,smilBuilder,destination,xof,xef);
			
			/*
			 * Finally create and render the OPF.
			 */
			new OPFBuilder(mModel,smilBuilder,ncxBuilder,destination,xof,xef);
			
		}finally{
			StAXEventFactoryPool.getInstance().release(xef);
			StAXOutputFactoryPool.getInstance().release(xof,properties);
		}
	}

}
