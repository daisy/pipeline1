package se_tpb_dtbookFix;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.EFile;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.xml.Namespaces;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.stax.StaxEntityResolver;
import org.daisy.util.xml.stax.StaxFilter;

/**
 * This executor changes deals with images pointing to a local .jpeg image. It
 * renames the src attribute value and copy the image to the output directory.
 * 
 * @author Romain Deltour
 * 
 */
public class JpegRenameExecutor extends Executor {

	private static final QName imgQN = new QName(
			Namespaces.Z2005_DTBOOK_NS_URI, "img");
	private static final QName srcQN = new QName("src");

	private File inDir;
	private File outDir;
	private TransformerDelegateListener mTransformer = null;

	JpegRenameExecutor(Map<String, String> parameters, String niceName,
			TransformerDelegateListener tdl) {
		super(parameters, niceName);
		inDir = FilenameOrFileURI.toFile(parameters.get("input"))
				.getParentFile();
		outDir = FilenameOrFileURI.toFile(parameters.get("output"))
				.getParentFile();
		mTransformer = tdl;
	}

	@Override
	void execute(Source source, Result result) throws TransformerRunException {
		File input = new EFile(FilenameOrFileURI.toFile(source.getSystemId()));
		File output = FilenameOrFileURI.toFile(result.getSystemId());
		Map<String, Object> xifProp = null;
		XMLInputFactory factory = null;
		FileInputStream fis = null;
		try {
			xifProp = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(false);
			factory = StAXInputFactoryPool.getInstance().acquire(xifProp);
			factory.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
			fis = new FileInputStream(input);
			XMLEventReader reader = factory.createXMLEventReader(fis);
			OutputStream outstream = new FileOutputStream(output);
			StaxFilter filter = new JpegRenamerFilter(reader, outstream);
			filter.filter();
		} catch (Exception e) {
			mTransformer.delegateMessage(this, mTransformer.delegateLocalize(
					"JPEG_RENAMER_FAIL", new String[] { e.getMessage() }),
					MessageEvent.Type.ERROR, MessageEvent.Cause.SYSTEM, null);
			try {
				FileUtils.copy(input, output);
			} catch (IOException ioe) {
				throw new TransformerRunException(ioe.getMessage(), ioe);
			}
		}finally{
			if(fis!=null) try {fis.close();} catch (IOException e) {}
			StAXInputFactoryPool.getInstance().release(factory, xifProp);
		}
	}

	@Override
	boolean supportsVersion(String version) {
		return true;
	}

	/**
	 * StAX filter that performs the .jpg renaming for each img element with a
	 * relative src URI pointing to a .jpeg.
	 * 
	 */
	private class JpegRenamerFilter extends StaxFilter {

		public JpegRenamerFilter(XMLEventReader xer, OutputStream outStream)
				throws XMLStreamException {
			super(xer, outStream);
		}

		@Override
		protected StartElement startElement(StartElement event) {
			if (imgQN.equals(event.getName())) {
				Attribute src = event.getAttributeByName(srcQN);
				String srcVal = (src != null) ? src.getValue() : null;
				if (srcVal != null && srcVal.endsWith(".jpeg")) {
					try {
						URI uri = new URI(srcVal);
						if (!uri.isAbsolute()) {
							return renameJpeg(event, srcVal);
						}
					} catch (URISyntaxException e) {
						// do nothing
					}
				}
			}
			return super.startElement(event);
		}

		private StartElement renameJpeg(StartElement event, String oldSrc) {
			String newSrc = oldSrc.substring(0, oldSrc.length()-4)+"jpg";
			EFile oldImg = new EFile(inDir, oldSrc);
//			String newSrc = oldImg.getNameMinusExtension() + ".jpg";
			File newImg = new File(outDir, newSrc);

			mTransformer.delegateMessage(this, mTransformer.delegateLocalize(
					"JPEG_RENAME", new String[] { oldSrc }),
					MessageEvent.Type.INFO, MessageEvent.Cause.SYSTEM, null);
			// Copy the .jpg to the output directory
			try {
				FileUtils.copy(oldImg, newImg);
			} catch (IOException e) {

				mTransformer.delegateMessage(this, mTransformer
						.delegateLocalize("JPEG_RNEMER_FAIL", new String[] { e
								.getMessage() }), MessageEvent.Type.INFO,
						MessageEvent.Cause.SYSTEM, null);
			}
			// Create a new start element with a changed attribute
			List<Attribute> newAttrs = new ArrayList<Attribute>();
			Iterator<?> oldAttrs = event.getAttributes();
			while (oldAttrs.hasNext()) {
				Attribute attr = (Attribute) oldAttrs.next();
				if (srcQN.equals(attr.getName())) {
					newAttrs.add(getEventFactory().createAttribute(
							attr.getName(), newSrc));
				} else {
					newAttrs.add(attr);
				}
			}

			return getEventFactory().createStartElement(event.getName(),
					newAttrs.iterator(), event.getNamespaces());
		}

	}

}
