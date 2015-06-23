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
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.util.FilesetRegex;
import org.daisy.util.text.URIUtils;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.stax.StaxEntityResolver;
import org.daisy.util.xml.stax.StaxFilter;

/**
 * This executor repairs invalid URIs by escaping illegal characters.
 * 
 * @author Linus Ericson
 */
public class InvalidURIExecutor extends Executor {

    private TransformerDelegateListener mTransformer = null;

    public InvalidURIExecutor(Map<String, String> parameters, String niceName,
            TransformerDelegateListener tdl) {
        super(parameters, niceName);
        mTransformer = tdl;
    }

    @Override
    boolean supportsVersion(String version) {
        return true;
    }

    @Override
    void execute(Source source, Result result) throws TransformerRunException {
        File inputFile = FilenameOrFileURI.toFile(source.getSystemId());
        File outputFile = FilenameOrFileURI.toFile(result.getSystemId());

        Map<String, Object> xifProp = null;
        XMLInputFactory factory = null;
        FileInputStream fis = null;
        try {
            xifProp = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(
                    false);
            factory = StAXInputFactoryPool.getInstance().acquire(xifProp);
            factory.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver
                    .getInstance()));
            fis = new FileInputStream(inputFile);
            XMLEventReader reader = factory.createXMLEventReader(fis);
            OutputStream outstream = new FileOutputStream(outputFile);
            StaxFilter filter = new InvalidURIFilter(reader, outstream);
            filter.filter();
            filter.close();
        } catch (Exception e) {
            mTransformer.delegateMessage(this, mTransformer.delegateLocalize(
                    "INVALID_URI_FAIL", new String[] { e.getMessage() }),
                    MessageEvent.Type.ERROR, MessageEvent.Cause.SYSTEM, null);
            try {
                FileUtils.copy(inputFile, outputFile);
            } catch (IOException ioe) {
                throw new TransformerRunException(ioe.getMessage(), ioe);
            }
        } finally {
            if (fis != null)
                try {
                    fis.close();
                } catch (IOException e) {
                }
            StAXInputFactoryPool.getInstance().release(factory, xifProp);
        }
    }

    private class InvalidURIFilter extends StaxFilter {

        private FilesetRegex regex = FilesetRegex.getInstance();

        public InvalidURIFilter(XMLEventReader xer, OutputStream outStream)
                throws XMLStreamException {
            super(xer, outStream);
        }

        @Override
        protected StartElement startElement(StartElement event) {
            Iterator iter = event.getAttributes();
            List<Attribute> attrs = new ArrayList<Attribute>();
            while (iter.hasNext()) {
                Attribute attr = (Attribute) iter.next();
                StringBuilder prefixAndLocal = new StringBuilder();
                String prefix = attr.getName().getPrefix();
                if (prefix != null && !"".equals(prefix)) {
                    prefixAndLocal.append(prefix).append(":");
                }
                prefixAndLocal.append(attr.getName().getLocalPart());
                if (regex.matches(regex.DTBOOK_ATTRIBUTES_WITH_URIS,
                        prefixAndLocal.toString())
                        || regex.matches(
                                regex.DTBOOK_COMPOUND_ATTRIBUTES_WITH_URIS,
                                prefixAndLocal.toString())) {
                    String uriString = attr.getValue();
                    try {
                        new URI(uriString);
                    } catch (URISyntaxException e) {
                        // Oops. This wasn't a valid URI. Let's try to fix it...
                        try {
                            URI newUri = URIUtils.createURI(uriString);                            
                            mTransformer.delegateMessage(this, mTransformer.delegateLocalize(
                                    "INVALID_URI_FIXED", new String[] { uriString }),
                                    MessageEvent.Type.INFO, MessageEvent.Cause.SYSTEM, null);
                            uriString = newUri.toString();
                        } catch (URISyntaxException e1) {
                            // That didn't work. The old value will be used even though it's incorrect
                            mTransformer.delegateMessage(this, mTransformer.delegateLocalize(
                                    "INVALID_URI_FAILED", new String[] { e1.getMessage() }),
                                    MessageEvent.Type.ERROR, MessageEvent.Cause.SYSTEM, null);
                        }
                    }
                    attrs.add(getEventFactory().createAttribute(attr.getName(),
                            uriString));
                } else {
                    attrs.add(attr);
                }
            }

            return getEventFactory().createStartElement(event.getName(),
                    attrs.iterator(), event.getNamespaces());
        }

    }

}
