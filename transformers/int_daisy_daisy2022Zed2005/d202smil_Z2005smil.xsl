<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" 
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:smil="http://www.w3.org/TR/REC-smil/SMIL10.dtd"
		exclude-result-prefixes="smil">

<xsl:output name="smil" doctype-public="-//NISO//DTD dtbsmil 2005-1//EN" doctype-system="http://www.daisy.org/z3986/2005/dtbsmil-2005-1.dtd" method="xml" encoding="UTF-8" indent="yes" />

<xsl:param name="uid" />
<xsl:param name="title" />
<xsl:param name="totalElapsedTime" />
<xsl:param name="timeinThisSmil" />
<xsl:param name="dtbookFileName" />

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
</head>
</xsl:template>

<xsl:template match="body">
<body>
	<seq dur="{$timeinThisSmil}">
		<xsl:apply-templates select="seq/*" />
	</seq>
	<xsl:comment><xsl:copy-of select="*" /></xsl:comment>
</body>
</xsl:template>

<xsl:template match="par">
<!-- assume that references in ncc or content.html that pointed to text IDs have been fixed -->
<par id="{@id}">
	<xsl:apply-templates select="node()" />
</par>
</xsl:template>

<xsl:template match="text">
<text src="{$dtbookFileName}#{substring-after(@src, '#')}" id="{@id}" />
</xsl:template>

<xsl:template match="audio">
<audio id="{@id}" src="{@src}" clipBegin="{@clip-begin}" clipEnd="{@clip-end}" />
</xsl:template>

</xsl:stylesheet>

