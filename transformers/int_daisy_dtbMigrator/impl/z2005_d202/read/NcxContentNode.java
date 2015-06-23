package int_daisy_dtbMigrator.impl.z2005_d202.read;

/**
 * A singular NCX content node
 * @author Markus Gylling
 */
class NcxContentNode {
	final String uri;
	final String xpathContext;
	final String text;

	NcxContentNode(String uri, String xpathContext, String text) {
		this.uri = uri;
		this.xpathContext = xpathContext;
		this.text = text;
	}
}
