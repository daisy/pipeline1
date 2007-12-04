<?xml version="1.0" encoding="UTF-8"?>
<!--
	Remove empty elements
		Version
			2007-11-29

		Description
			Removes
				* empty/whitespace p except when preceded by hx and 
						followed only by other empty p
				* empty/whitespace em, strong, sub, sup

		Nodes
			p, em, strong, sub, sup

		Namespaces
			(x) "http://www.daisy.org/z3986/2005/dtbook/"

		Doctype
			(x) DTBook

		Author
			Joel Håkansson, TPB
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/" exclude-result-prefixes="dtb">

	<xsl:include href="recursive-copy.xsl"/>
	<xsl:include href="output.xsl"/>
 
 	<xsl:template match="dtb:p[(text() and count(node())=1 and normalize-space()='') or not(node())]">
		<xsl:choose>
			<xsl:when test="preceding-sibling::*[1][starts-with(name(), 'h')] and count(following-sibling::*)=count(following-sibling::dtb:p[(text() and count(node())=1 and normalize-space()='') or not(node())])"><xsl:call-template name="copy"/></xsl:when>
			<xsl:otherwise><xsl:apply-templates/></xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="dtb:strong[(text() and count(node())=1 and normalize-space()='') or not(node())]">
		<xsl:apply-templates/>
	</xsl:template>
 
	<xsl:template match="dtb:em[(text() and count(node())=1 and normalize-space()='') or not(node())]">
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="dtb:sub[(text() and count(node())=1 and normalize-space()='') or not(node())]">
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="dtb:sup[(text() and count(node())=1 and normalize-space()='') or not(node())]">
		<xsl:apply-templates/>
	</xsl:template>
  
</xsl:stylesheet>
