<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:rev="rnib.org.uk/tbs#"
	xmlns:d="http://www.tpb.se/ns/2006/wml2dtbook"
	xmlns:o="urn:schemas-microsoft-com:office:office"
	xmlns:w="http://schemas.microsoft.com/office/word/2003/wordml"
	xmlns="http://www.daisy.org/z3986/2005/dtbook/" exclude-result-prefixes="w d rev o">

<!-- Global variables -->
<xsl:variable name="revision-history" select="document('../docs/revision-history.xml')"/>
<xsl:variable name="revision" select="$revision-history/rev:history/rev:revision[last()]"/>
<xsl:variable name="version" select="$revision/rev:version"/>
<xsl:variable name="date" select="$revision/rev:date"/>

<xsl:template name="addAttributes">
	<xsl:param name="node"/>
	<xsl:for-each select="$node/d:attribute">
		<xsl:attribute name="{@name}"><xsl:value-of select="@value"/></xsl:attribute>
	</xsl:for-each>
</xsl:template>

<xsl:template name="addZeros">
	<xsl:param name="value"/>
	<xsl:param name="padding" select="3"/>
	<xsl:choose>
	<xsl:when test="string-length($value)&lt;number($padding)">
		<xsl:call-template name="addZeros">
			<xsl:with-param name="value" select="concat(0, $value)"/>
		</xsl:call-template>
	</xsl:when>
	<xsl:otherwise><xsl:value-of select="$value"/></xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template name="insertHeader">
	<head>
		<meta name="dtb:uid" content=""/>
		<meta name="dc:Title" content="{o:DocumentProperties/o:Title}"/>
		<meta name="dc:Language" content="{//w:style[w:name/@w:val='Normal']//w:lang/@w:val}"/>
		<meta name="dc:Date" content="{substring-before(o:DocumentProperties/o:LastSaved, 'T')}"/>
		<meta name="dc:Publisher" content="{o:DocumentProperties/o:Company}"/>
		<meta name="wml2dtbook:version" content="{$version}"/>
		<meta name="wml2dtbook:date" content="{$date}"/>
	</head>
</xsl:template>

<xsl:template name="insertVersion">
	<xsl:comment xml:space="preserve">
		WordML2DTBook
		rev: <xsl:value-of select="$version"/>
		date: <xsl:value-of select="$date"/>
	</xsl:comment>
</xsl:template>

<xsl:template name="insertProcessingInstruction">
	<xsl:if test="$stylesheet!=''">
		<xsl:processing-instruction name="xml-stylesheet">type="text/xsl" href="<xsl:value-of select="$stylesheet"/>"</xsl:processing-instruction>
	</xsl:if>
</xsl:template>

</xsl:stylesheet>
