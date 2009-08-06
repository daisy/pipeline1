package int_daisy_metadataEditor;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Templates;

import org.daisy.util.file.FileUtils;
import org.daisy.util.file.TempFile;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.TransformerFactoryConstants;
import org.daisy.util.xml.xslt.XSLTException;

/**
 * A metadata editor using an XSLT stylesheet to edit metadata on an XML file.
 * <p>
 * Metadata name and value and edition mode are forwarded to the stylesheet as
 * XSLT parameters.
 * </p>
 * 
 * @author Romain Deltour
 * 
 */
public class XSLTMetadataEditor implements MetadataEditor {

	private final Templates xslt;

	/**
	 * Creates a new metadata editor using the given stylesheet.
	 * <p>
	 * Note: the stylesheet at the given URL is compiled into a
	 * {@link Templates} object for later use in the
	 * {@link #editMetadata(File, String, String, int_daisy_metadataEditor.MetadataEditor.Mode)
	 * editMetadata(...)} method.
	 * </p>
	 * 
	 * @param xsltURL
	 *            the URL of the stylesheet used to edit metadata.
	 */
	public XSLTMetadataEditor(URL xsltURL) {
		try {
			this.xslt = Stylesheet.createTemplate(xsltURL.toString(),
					TransformerFactoryConstants.SAXON8, null);
		} catch (XSLTException e) {
			throw new IllegalArgumentException("Couldn't compile XSLT", e);
		}
	}

	public void editMetadata(File input, String name, String value, Mode mode)
			throws Exception {

		// Create a temporary output file
		File output = TempFile.create();
		FileUtils.createDirectory(output.getParentFile());

		// Add the XLST params
		Map<String, Object> xslParams = new HashMap<String, Object>();
		xslParams.put("name", name);
		xslParams.put("value", value);
		xslParams.put("mode", mode.toString());

		// Apply the XSLT
		Stylesheet.apply(input.getAbsolutePath(), xslt.newTransformer(), output
				.getAbsolutePath(), xslParams, CatalogEntityResolver
				.getInstance());

		// Overwrite the input with the produced output
		FileUtils.copy(output, input);
	}

}
