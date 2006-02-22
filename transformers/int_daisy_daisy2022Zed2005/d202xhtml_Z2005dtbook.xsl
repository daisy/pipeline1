<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" 
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:html="http://www.w3.org/1999/xhtml"
		exclude-result-prefixes="html">

<xsl:output name="dtbook" doctype-public="-//NISO//DTD resource 2005-1//EN" doctype-system="http://www.daisy.org/z3986/2005/resource-2005-1.dtd" method="xml" encoding="UTF-8" indent="yes" />

<xsl:template match="html:html">
<dtbook>
	<xsl:call-template name="head" />
	<xsl:call-template name="book" />
</dtbook>
</xsl:template>

<xsl:template name="head">
<head>

</head>
</xsl:template>

<xsl:template name="book">
<book>
	<xsl:call-template name="frontmatter" />
	<xsl:call-template name="bodymatter" />
	<xsl:call-template name="rearmatter" />
</book>
</xsl:template>

<xsl:template name="frontmatter">
<frontmatter>

</frontmatter>
</xsl:template>

<xsl:template name="bodymatter">
<bodymatter>

</bodymatter>
</xsl:template>

<xsl:template name="rearmatter">
<rearmatter>

</rearmatter>
</xsl:template>

</xsl:stylesheet>

