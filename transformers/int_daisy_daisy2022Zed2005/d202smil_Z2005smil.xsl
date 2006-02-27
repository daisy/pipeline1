<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:smil="http://www.w3.org/TR/REC-smil/SMIL10.dtd"
	xmlns="http://www.daisy.org/z3986/2005/dtbsmil-2005-1.dtd"
	exclude-result-prefixes="smil">
<!-- 
	NCX has to point either to par element or to 
	Markus to provide a flag to say whether to drop text; and an example of whether par with only audio is necessary

	assume that references in ncc.html or content.html that pointed to text IDs have been fixed
	
	To do:
	- use new parameter "isNcxOnly" for customAttributes/customTest
	- layout/region?

	Bugs:
	- Java: totalElapsedTime is broken

	Questions:
	- do we make page numbers skippable by default?
-->
	
<xsl:output doctype-public="-//NISO//DTD dtbsmil 2005-1//EN" 
	doctype-system="http://www.daisy.org/z3986/2005/dtbsmil-2005-1.dtd" 
	method="xml" 
	encoding="UTF-8" 
	indent="yes" />

<xsl:param name="uid" />
<xsl:param name="title" />
<xsl:param name="totalElapsedTime" />
<xsl:param name="timeinThisSmil" />
<xsl:param name="dtbookFileName" />
<xsl:param name="isNcxOnly" />

<xsl:template match="smil">
	<smil>
		<xsl:apply-templates select="head" />
		<xsl:apply-templates select="body" />	
	</smil>
</xsl:template>

<xsl:template match="head">
	<head>
		<meta name="dtb:uid" content="{$uid}" />
		<meta name="dtb:totalElapsedTime" content="{$totalElapsedTime}" />
		<meta name="dtb:generator" content="DMFC" />
		<!-- customAttributes -->
		
	</head>
</xsl:template>

<xsl:template match="body">
	<body>
		<seq dur="{$timeinThisSmil}">
			<xsl:apply-templates select="seq/*" />
		</seq>
	</body>
</xsl:template>

<xsl:template match="par">
	<par id="{@id}">
		<xsl:apply-templates select="node()" />
	</par>
</xsl:template>

<xsl:template match="text">
	<text src="{$dtbookFileName}#{substring-after(@src, '#')}">
		<xsl:choose>
			<xsl:when test="@id"><xsl:attribute name="id" select="@id" /></xsl:when>
			<xsl:otherwise><xsl:attribute name="id" select="generate-id()" /></xsl:otherwise>
		</xsl:choose>
	</text>
</xsl:template>

<xsl:template match="seq">
<seq>
	<xsl:if test="@id"><xsl:attribute name="id" select="@id" /></xsl:if>
	<xsl:apply-templates />
</seq>
</xsl:template>

<xsl:template match="audio">
<audio id="{@id}" src="{@src}" clipBegin="{@clip-begin}" clipEnd="{@clip-end}" />
</xsl:template>

</xsl:stylesheet>

