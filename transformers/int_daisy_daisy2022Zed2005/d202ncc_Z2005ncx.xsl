<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/1999/xhtml"
	xmlns:c="http://daisymfc.sf.net/xslt/config"
	xmlns="http://www.daisy.org/z3986/2005/ncx-2005-1.dtd"
	exclude-result-prefixes="html">

<!-- To do: fill in docTitle <audio> 
	audio elements (whether or not it's a headings-only file... transformer wish list)
	
-->

<c:config>
	<c:generator>DMFC Daisy 2.02 to z3986-2005</c:generator>
	<c:name>d202ncc_Z2005ncx</c:name>
	<c:version>0.1</c:version>
	
	<c:author>Brandon Nelson</c:author>
	<c:description>Creates the Z2005 ncx file.</c:description>    
</c:config>

<xsl:output name="ncx" 
	doctype-public="-//NISO//DTD ncx 2005-1//EN" 
	doctype-system="http://www.daisy.org/z3986/2005/ncx-2005-1.dtd" 
	method="xml" 
	encoding="UTF-8" 
	indent="yes" />

<xsl:template match="/html:html">
	<ncx>
		<xsl:apply-templates select="html:head" />
		<xsl:apply-templates select="html:body" />
	</ncx>
</xsl:template>

<xsl:template match="html:head">
	<head>
		<meta name="dtb:uid" content="{html:meta[@name='dc:identifier']/@content}" />
		<meta name="dtb:depth" content="{html:meta[@name='ncc:depth']/@content}" />
		<meta name="dtb:generator" content="DMFC Daisy 2.02 to z39.86-2005" />
		<meta name="dtb:totalPageCount" content="{html:meta[@name='ncc:pageFront']/@content + html:meta[@name='ncc:pageNormal']/@content + html:meta[@name='ncc:pageSpecial']/@content}" />
		<meta name="dtb:maxPageNumber" content="{max(//html:span[@class='page-normal'])}" />
		<smilCustomTest />
	</head>
	<docTitle>
		<text><xsl:value-of select="html:title" /></text>
	</docTitle>
	<xsl:if test="html:meta[@name='dc:creator']">
		<docAuthor>
			<text><xsl:value-of select="html:meta[@name='dc:creator']/@content" /></text>
		</docAuthor>
	</xsl:if>
</xsl:template>

<xsl:template match="html:body">
	<xsl:call-template name="navMap" />
	<xsl:call-template name="pageList" />
	<xsl:call-template name="navList" />
</xsl:template>

<xsl:template name="navMap">
	<navMap>
		<xsl:apply-templates select="html:h1" />
	</navMap>
</xsl:template>

<xsl:template match="html:h1|html:h2|html:h3|html:h4|html:h5|html:h6">
	<navPoint id="{@id}" playOrder="{count(preceding::*[self::html:h1 or self::html:h2 or self::html:h3 or self::html:h4 or self::html:h5 or self::html:h6])}">
		<navLabel>
			<text><xsl:value-of select="." /></text>
		</navLabel>
		<content src="{html:a/@href}" />
		<xsl:apply-templates select="key('nextHeadings', generate-id(.))" />
	</navPoint>
</xsl:template>

<xsl:template name="pageList">
<pageList>

</pageList>
</xsl:template>

<xsl:template name="navList">
<navList>

</navList>
</xsl:template>

<xsl:key name="nextHeadings"	
			match="html:h6"
			use="generate-id(preceding-sibling::*[self::html:h1 or self::html:h2 or self::html:h3 or self::html:h4 or self::html:h5][1])"/>
					 
<xsl:key name="nextHeadings"	
			match="html:h5"
			use="generate-id(preceding-sibling::*[self::html:h1 or self::html:h2 or self::html:h3 or self::html:h4][1])"/>

<xsl:key name="nextHeadings"	
			match="html:h4"
			use="generate-id(preceding-sibling::*[self::html:h1 or self::html:h2 or self::html:h3][1])" />
					 
<xsl:key name="nextHeadings"	
			match="html:h3"
			use="generate-id(preceding-sibling::*[self::html:h1 or self::html:h2][1])"/>

<xsl:key name="nextHeadings"
			match="html:h2"
			use="generate-id(preceding-sibling::html:h1[1])"/>

</xsl:stylesheet>
