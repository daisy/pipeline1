<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns="http://www.w3.org/2001/SMIL20/"		
	>
<!-- 
	To do:
	- 2.02 ncc normally points to smil <text/>, in zed this is 
			allowed but not recommended, should be parent timecontainer
	- layout/region?
	- skippability
-->
	
<xsl:output doctype-public="-//NISO//DTD dtbsmil 2005-1//EN" 
	doctype-system="http://www.daisy.org/z3986/2005/dtbsmil-2005-1.dtd" 
	method="xml" 
	encoding="UTF-8" 
	indent="yes" />

<!-- inparams: -->
	<xsl:param name="uid" /> 				<!-- uid of publication -->
	<xsl:param name="title" />  			<!-- title of publication -->
	<xsl:param name="totalElapsedTime" /> 	<!-- formatted SMIL clock value -->
	<xsl:param name="timeinThisSmil" /> 	<!-- formatted SMIL clock value -->
	<xsl:param name="isNcxOnly" /> 			<!-- whether to drop text elements -->

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
		<meta name="dtb:generator" content="DAISY Pipeline" />
		<!-- customAttributes -->		
	</head>
</xsl:template>

<xsl:template match="body">
	<body>
		<seq dur="{$timeinThisSmil}" id="mseq">
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
	<xsl:choose>
	<xsl:when test="($isNcxOnly = 'true')">
		<!-- no text element rendered -->
	</xsl:when>
	<xsl:otherwise>
		<!-- 
			the text docs have the same filename barring extension
			e.g. foo.html into foo.xml.
		 -->
		 <!--  TODO below breaks if filename contains period chars -->
		<text src="{substring-before(@src, '.')}.xml#{substring-after(@src, '#')}">
			<xsl:choose>
				<xsl:when test="@id"><xsl:attribute name="id" select="@id" /></xsl:when>
				<xsl:otherwise><xsl:attribute name="id" select="generate-id()" /></xsl:otherwise>
			</xsl:choose>
		</text>
	</xsl:otherwise>	
	</xsl:choose>
</xsl:template>

<xsl:template match="seq">
	<seq>
		<xsl:choose>
			<xsl:when test="@id"><xsl:attribute name="id" select="@id" /></xsl:when>
			<xsl:otherwise><xsl:attribute name="id" select="generate-id()" /></xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates />
	</seq>
</xsl:template>

<xsl:template match="audio">
	<audio id="{@id}" src="{@src}" clipBegin="{@clip-begin}" clipEnd="{@clip-end}" />
</xsl:template>

<xsl:template match="image">
	<image id="{@id}" src="{@src}" />
</xsl:template>

</xsl:stylesheet>

