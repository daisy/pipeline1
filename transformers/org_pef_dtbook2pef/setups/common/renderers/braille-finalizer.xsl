<?xml version="1.0" encoding="UTF-8"?>
<!--	
		Braille finalizer.
		The Braille finalizer performs post-rendering character-by-character braille injection 
		replacing space, no-break space, hyphen and soft hyphen.
-->
<!--
		Joel Håkansson, TPB
		Version 2009-06-26
 -->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pef="http://www.daisy.org/ns/2008/pef" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions">

	<xsl:output method="xml" media-type="application/x-pef+xml" encoding="utf-8" indent="no"/>
	
	<xsl:template match="text()[parent::pef:row]">
		<xsl:value-of select="translate(., ' &#x00a0;-&#x00ad;','&#x2800;&#x2800;&#x2824;&#x2824;')"/>
	</xsl:template>

	<xsl:template match="*|comment()|processing-instruction()">
		<xsl:call-template name="copy"/>
	</xsl:template>

	<xsl:template name="copy">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>
