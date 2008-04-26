package int_daisy_filesetGenerator.util.d202;

import int_daisy_filesetGenerator.impl.d202.D202TextOnlyGenerator.GlobalMetadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.spec.MGF1ParameterSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;

import org.daisy.util.xml.IDGenerator;
import org.daisy.util.xml.SmilClock;
import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.daisy.util.xml.pool.StAXOutputFactoryPool;

/**
 * A builder for Daisy 2.02 SMIL.
 * @author Markus Gylling
 */
public class D202SmilBuilder {
	private final File mDestination;
	private final String mHeadingText;
	private String mIdentifier;
	private String mPublicationTitle;
	private static IDGenerator mParIDGenerator = null;
	private static IDGenerator mTextIDGenerator = null;
	private List<ParContainer> mParList;
	
	private static String SMIL_DTD;
	private static QName qSmil;
	private static QName qHead;
	private static QName qBody;
	private static QName qSeq;
	private static QName qPar;
	private static QName qText;
	private static QName qMeta;
	private static QName qLayout;
	private static QName qRegion;
	private final Charset mOutputCharset;
	
	public D202SmilBuilder(File destination, String headingText, GlobalMetadata metadata, Charset outputCharset) {
		mDestination = destination;
		mHeadingText = headingText;
		mOutputCharset = outputCharset;
		mIdentifier = metadata.mPublicationIdentifier;
		mPublicationTitle = metadata.mPublicationTitle;
		if(mParIDGenerator == null) {
			mParIDGenerator = new IDGenerator("par_");
			mTextIDGenerator = new IDGenerator("txt_");
		}		
		mParList = new ArrayList<ParContainer>();
		if(SMIL_DTD==null) {
			SMIL_DTD = "<!DOCTYPE smil PUBLIC \"-//W3C//DTD SMIL 1.0//EN\" \"http://www.w3.org/TR/REC-smil/SMIL10.dtd\">";
			qSmil = new QName("smil");
			qHead = new QName("head");
			qBody = new QName("body");
			qSeq = new QName("seq");
			qPar = new QName("par");
			qText = new QName("text");
			qMeta = new QName("meta");
			qLayout = new QName("layout");
			qRegion = new QName("region");
		}
	}

	/**
	 * Add a new par container to this smil file with a single text media child.
	 * @param contentDocURI the URI the text child shall carry in its src attribute.
	 * @return the URI to reference this container with.
	 */
	public String addPar(String contentDocURI) {
		ParContainer pc = new ParContainer(contentDocURI);
		mParList.add(pc);
		StringBuilder sb = new StringBuilder();
		sb.append(mDestination.getName()).append('#');		
		sb.append(pc.mParId);
		return sb.toString();
	}
	
	/**
	 * Finalize and render this SMIL file.
	 * @return The file written.
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	public File render() throws FileNotFoundException, XMLStreamException {
		Map<String, Object> properties = StAXOutputFactoryPool.getInstance().getDefaultPropertyMap();
		XMLOutputFactory xof = StAXOutputFactoryPool.getInstance().acquire(properties);
		FileOutputStream fos = new FileOutputStream(mDestination);
		XMLEventWriter writer = xof.createXMLEventWriter(fos,mOutputCharset.name());
		XMLEventFactory xef = StAXEventFactoryPool.getInstance().acquire();
						
		writer.add(xef.createStartDocument(mOutputCharset.name(),"1.0"));
		writer.add(xef.createDTD(SMIL_DTD));
		writer.add(xef.createStartElement(qSmil,null,null));
		
		writer.add(xef.createStartElement(qHead,null,null));
		
		writer.add(xef.createStartElement(qMeta,null,null));
		writer.add(xef.createAttribute("name", "dc:identifier"));
		writer.add(xef.createAttribute("content", mIdentifier));
		writer.add(xef.createEndElement(qMeta,null));
		
		writer.add(xef.createStartElement(qMeta,null,null));
		writer.add(xef.createAttribute("name", "title"));
		writer.add(xef.createAttribute("content", mHeadingText));
		writer.add(xef.createEndElement(qMeta,null));
		
		writer.add(xef.createStartElement(qMeta,null,null));
		writer.add(xef.createAttribute("name", "dc:title"));
		writer.add(xef.createAttribute("content", mPublicationTitle));
		writer.add(xef.createEndElement(qMeta,null));
		
		writer.add(xef.createStartElement(qMeta,null,null));
		writer.add(xef.createAttribute("name", "dc:format"));
		writer.add(xef.createAttribute("content", "Daisy 2.02"));
		writer.add(xef.createEndElement(qMeta,null));
		
		writer.add(xef.createStartElement(qMeta,null,null));
		writer.add(xef.createAttribute("name", "ncc:generator"));
		writer.add(xef.createAttribute("content", "Daisy Pipeline"));
		writer.add(xef.createEndElement(qMeta,null));

		writer.add(xef.createStartElement(qMeta,null,null));
		writer.add(xef.createAttribute("name", "ncc:totalElapsedTime"));
		writer.add(xef.createAttribute("content", "00:00:00"));
		writer.add(xef.createEndElement(qMeta,null));
		
		writer.add(xef.createStartElement(qMeta,null,null));
		writer.add(xef.createAttribute("name", "ncc:timeInThisSmil"));
		writer.add(xef.createAttribute("content", "00:00:00"));
		writer.add(xef.createEndElement(qMeta,null));
		
		writer.add(xef.createStartElement(qLayout,null,null));
		writer.add(xef.createStartElement(qRegion,null,null));
		writer.add(xef.createAttribute("id", "txtView"));
		writer.add(xef.createEndElement(qRegion,null));
		writer.add(xef.createEndElement(qLayout,null));
		
		writer.add(xef.createEndElement(qHead,null));
		
		writer.add(xef.createStartElement(qBody,null,null));
		//start mother seq
		writer.add(xef.createStartElement(qSeq,null,null));
		writer.add(xef.createAttribute("dur", getDuration().toString(SmilClock.TIMECOUNT_SEC)));
		//add pars
		Attribute endsync = xef.createAttribute("endsync", "last");
		for(ParContainer par : mParList) {
			writer.add(xef.createStartElement(qPar,null,null));
			writer.add(endsync);
			writer.add(xef.createAttribute("id", par.mParId));
			writer.add(xef.createStartElement(qText,null,null));
			writer.add(xef.createAttribute("src", par.mContentDocURI));
			writer.add(xef.createAttribute("id", par.mTextId));
			writer.add(xef.createEndElement(qText,null));
			writer.add(xef.createEndElement(qPar,null));	
		}		
		//end mother seq
		writer.add(xef.createEndElement(qSeq,null));					
		writer.add(xef.createEndElement(qBody,null));		
		writer.add(xef.createEndElement(qSmil,null));		
		writer.add(xef.createEndDocument());
		writer.flush();
		writer.close();
		try {
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		StAXOutputFactoryPool.getInstance().release(xof, properties);
		StAXEventFactoryPool.getInstance().release(xef);
		return mDestination;
	}
	
	public SmilClock getDuration() {
		return new SmilClock(0);
	}
	
	class ParContainer {
		final String mContentDocURI;
		final String mParId;
		final String mTextId;
		
		/**
		 * Constructor. Create a par with only a text child.
		 * @param contentDocURI
		 */
		public ParContainer(String contentDocURI) {
			mContentDocURI = contentDocURI;
			mParId = mParIDGenerator.generateId();
			mTextId = mTextIDGenerator.generateId();
		}
	}
}
