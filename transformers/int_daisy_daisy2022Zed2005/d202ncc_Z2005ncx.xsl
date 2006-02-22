<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" 
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
		xmlns:html="http://www.w3.org/1999/xhtml"
		exclude-result-prefixes="html">

<xsl:output name="ncx" doctype-public="-//NISO//DTD ncx 2005-1//EN" doctype-system="http://www.daisy.org/z3986/2005/ncx-2005-1.dtd" method="xml" encoding="UTF-8" indent="yes" />

<xsl:template match="html:html">
<ncx>
	<xsl:call-template name="head" />
	<xsl:call-template name="docTitle" />
	<xsl:call-template name="docAuthor" />
	<xsl:call-template name="navMap" />
	<xsl:call-template name="pageList" />
	<xsl:call-template name="navList" />
</ncx>
</xsl:template>

<xsl:template name="head">
<head>

</head>
</xsl:template>

<xsl:template name="docTitle">
<docTitle>

</docTitle>
</xsl:template>

<xsl:template name="docAuthor">
<docAuthor>

</docAuthor>
</xsl:template>

<xsl:template name="navMap">
<navMap>

</navMap>
</xsl:template>

<xsl:template name="pageList">
<pageList>

</pageList>
</xsl:template>

<xsl:template name="navList">
<navList>

</navList>
</xsl:template>

</xsl:stylesheet>
