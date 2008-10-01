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

    final static String[] hex = { "%00", "%01", "%02", "%03", "%04", "%05",
            "%06", "%07", "%08", "%09", "%0a", "%0b", "%0c", "%0d", "%0e",
            "%0f", "%10", "%11", "%12", "%13", "%14", "%15", "%16", "%17",
            "%18", "%19", "%1a", "%1b", "%1c", "%1d", "%1e", "%1f", "%20",
            "%21", "%22", "%23", "%24", "%25", "%26", "%27", "%28", "%29",
            "%2a", "%2b", "%2c", "%2d", "%2e", "%2f", "%30", "%31", "%32",
            "%33", "%34", "%35", "%36", "%37", "%38", "%39", "%3a", "%3b",
            "%3c", "%3d", "%3e", "%3f", "%40", "%41", "%42", "%43", "%44",
            "%45", "%46", "%47", "%48", "%49", "%4a", "%4b", "%4c", "%4d",
            "%4e", "%4f", "%50", "%51", "%52", "%53", "%54", "%55", "%56",
            "%57", "%58", "%59", "%5a", "%5b", "%5c", "%5d", "%5e", "%5f",
            "%60", "%61", "%62", "%63", "%64", "%65", "%66", "%67", "%68",
            "%69", "%6a", "%6b", "%6c", "%6d", "%6e", "%6f", "%70", "%71",
            "%72", "%73", "%74", "%75", "%76", "%77", "%78", "%79", "%7a",
            "%7b", "%7c", "%7d", "%7e", "%7f", "%80", "%81", "%82", "%83",
            "%84", "%85", "%86", "%87", "%88", "%89", "%8a", "%8b", "%8c",
            "%8d", "%8e", "%8f", "%90", "%91", "%92", "%93", "%94", "%95",
            "%96", "%97", "%98", "%99", "%9a", "%9b", "%9c", "%9d", "%9e",
            "%9f", "%a0", "%a1", "%a2", "%a3", "%a4", "%a5", "%a6", "%a7",
            "%a8", "%a9", "%aa", "%ab", "%ac", "%ad", "%ae", "%af", "%b0",
            "%b1", "%b2", "%b3", "%b4", "%b5", "%b6", "%b7", "%b8", "%b9",
            "%ba", "%bb", "%bc", "%bd", "%be", "%bf", "%c0", "%c1", "%c2",
            "%c3", "%c4", "%c5", "%c6", "%c7", "%c8", "%c9", "%ca", "%cb",
            "%cc", "%cd", "%ce", "%cf", "%d0", "%d1", "%d2", "%d3", "%d4",
            "%d5", "%d6", "%d7", "%d8", "%d9", "%da", "%db", "%dc", "%dd",
            "%de", "%df", "%e0", "%e1", "%e2", "%e3", "%e4", "%e5", "%e6",
            "%e7", "%e8", "%e9", "%ea", "%eb", "%ec", "%ed", "%ee", "%ef",
            "%f0", "%f1", "%f2", "%f3", "%f4", "%f5", "%f6", "%f7", "%f8",
            "%f9", "%fa", "%fb", "%fc", "%fd", "%fe", "%ff" };

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
                        uriString = encode(uriString);
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

        /**
         * Encodes a string by escaping all characters except the ones that are
         * reserved in rfc3986. See
         * http://en.wikipedia.org/wiki/Percent-encoding
         * 
         * @param str
         * @return
         */
        private String encode(String str) {
            String reservedChars = "!*'();:@&=+$,/?%#[]";
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < str.length(); ++i) {
                char ch = str.charAt(i);
                boolean reserved = false;
                for (int j = 0; j < reservedChars.length(); ++j) {
                    char res = reservedChars.charAt(j);
                    if (ch == res) {
                        reserved = true;
                        break;
                    }
                }
                if (reserved) {
                    result.append(ch);
                } else {
                    result.append(escape(ch));
                }
            }
            return result.toString();
        }

        /**
         * Hex-escapes a single character.
         * <p>
         * This is a modified version of the code found at
         * http://www.w3.org/International/O-URL-code.html
         * </p>
         * <p>
         * See also http://en.wikipedia.org/wiki/Percent-encoding
         * </p>
         * 
         * @param ch
         * @return
         */
        private String escape(char ch) {
            if ('A' <= ch && ch <= 'Z') { // 'A'..'Z'
                return String.valueOf(ch);
            } else if ('a' <= ch && ch <= 'z') { // 'a'..'z'
                return String.valueOf(ch);
            } else if ('0' <= ch && ch <= '9') { // '0'..'9'
                return String.valueOf(ch);
            } else if (ch == ' ') { // space
                return "%20";
            } else if (ch == '-' || ch == '_' // unreserved
                    || ch == '.' || ch == '~') {
                return String.valueOf(ch);
            } else if (ch <= 0x007f) { // other ASCII
                return String.valueOf(hex[ch]);
            } else if (ch <= 0x07FF) { // non-ASCII <= 0x7FF
                return String.valueOf(hex[0xc0 | (ch >> 6)]
                        + hex[0x80 | (ch & 0x3F)]);
            } else { // 0x7FF < ch <= 0xFFFF
                return String.valueOf(hex[0xe0 | (ch >> 12)]
                        + hex[0x80 | ((ch >> 6) & 0x3F)]
                        + hex[0x80 | (ch & 0x3F)]);
            }

        }

    }

}
