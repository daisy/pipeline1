<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" 
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
		xmlns:html="http://www.w3.org/1999/xhtml"
		exclude-result-prefixes="html">

<xsl:output name="opf" doctype-public="+//ISBN 0-9673008-1-9//DTD OEB 1.2 Package//EN" doctype-system="http://openebook.org/dtds/oeb-1.2/oebpkg12.dtd" method="xml" encoding="UTF-8" indent="yes" />

<xsl:template match="html:html">
<package>
	<xsl:call-template name="metadata" />
	<xsl:call-template name="manifest" />
	<xsl:call-template name="spine" />	
</package>
</xsl:template>

<xsl:template name="metadata">
<metadata>

</metadata>
</xsl:template>

<xsl:template name="manifest">
<manifest>

</manifest>
</xsl:template>

<xsl:template name="spine">
<spine>

</spine>
</xsl:template>

</xsl:stylesheet>
