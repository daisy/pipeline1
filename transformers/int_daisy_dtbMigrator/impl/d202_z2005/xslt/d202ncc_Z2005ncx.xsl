<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" 
	xmlns:html="http://www.w3.org/1999/xhtml"
	xmlns:c="http://daisymfc.sf.net/xslt/config"
	xmlns="http://www.daisy.org/z3986/2005/ncx/"
	exclude-result-prefixes="html c xs">

<!-- 
	To do: 
	- fill in audio elements
	- navList
	- xml:lang
	- smilCustomTest
-->

<c:config>
	<c:generator>DMFC Daisy 2.02 to z3986-2005</c:generator>
	<c:name>d202ncc_Z2005ncx</c:name>
	<c:version>0.1</c:version>	
	<c:author>Brandon Nelson</c:author>
	<c:description>Creates the Z2005 ncx file.</c:description>    
</c:config>

<xsl:output doctype-public="-//NISO//DTD ncx 2005-1//EN" 
	doctype-system="http://www.daisy.org/z3986/2005/ncx-2005-1.dtd" 
	method="xml" 
	encoding="UTF-8" 
	indent="yes" />

<!-- inparams: -->
<xsl:param name="uid" />					<!-- uid of publication -->
<xsl:param name="defaultStatePagenumbers" as="xs:string" select="'true'" />		<!-- value for head/smilCustomTest/@defaultState -->
<xsl:param name="defaultStateSidebars" as="xs:string" select="'true'" /> 		<!-- value for head/smilCustomTest/@defaultState -->
<xsl:param name="defaultStateFootnotes" as="xs:string" select="'true'" />		<!-- value for head/smilCustomTest/@defaultState -->
<xsl:param name="defaultStateProdnotes" as="xs:string" select="'true'" /> 		<!-- value for head/smilCustomTest/@defaultState -->

<xsl:variable name="NCC.Folder" as="xs:string" select="replace(document-uri(/),'(.*)/ncc.html','$1')" />

<xsl:template match="/html:html">
	<ncx version="2005-1">
		<xsl:apply-templates select="html:head" />
		<xsl:apply-templates select="html:body" />
	</ncx> 
</xsl:template>


<xsl:variable name="List.smilCustomTest" as="xs:string*">
	<xsl:variable name="smil.filenames" as="xs:string+" 
		select="distinct-values(
			for $s in //html:a[matches(@href,'(.+)smil#(.+)')]/@href 
			return substring-before($s,'#')
		)" />
	<xsl:variable name="list.sct" as="xs:string*">
		<xsl:for-each select="$smil.filenames">
			<xsl:for-each select="doc(concat($NCC.Folder,'/',.))//par[@system-required]">
				<xsl:sequence select="@system-required" />
			</xsl:for-each>
		</xsl:for-each>
	</xsl:variable>
	<xsl:sequence select="distinct-values($list.sct)" />
</xsl:variable>


<xsl:template match="html:head">
	<head>
		<meta name="dtb:uid" content="{$uid}" />
		<meta name="dtb:depth" content="{html:meta[@name='ncc:depth']/@content}" />
		<meta name="dtb:generator" content="Pipeline Daisy 2.02 to z39.86-2005" />
		<meta name="dtb:totalPageCount" content="{html:meta[@name='ncc:pageFront']/@content + html:meta[@name='ncc:pageNormal']/@content + html:meta[@name='ncc:pageSpecial']/@content}" />
		<xsl:choose>
			<xsl:when test="html:span[@class='page-normal']">
				<meta name="dtb:maxPageNumber" content="{max(//html:span[@class='page-normal'])}" />
			</xsl:when>
			<xsl:otherwise>
				<meta name="dtb:maxPageNumber" content="0" />
			</xsl:otherwise>
		</xsl:choose>
		<!-- psps: smilCustomTest -->
		<xsl:for-each select="$List.smilCustomTest">
			<smilCustomTest id="{substring-before(.,'-on')}" defaultState="false" override="visible">
				<xsl:attribute name="defaultState">
					<xsl:choose>
						<xsl:when test=". eq 'pagenumber-on'"><xsl:value-of select="$defaultStatePagenumbers" /></xsl:when>
						<xsl:when test=". eq 'sidebar-on'"><xsl:value-of select="$defaultStateSidebars" /></xsl:when>
						<xsl:when test=". eq 'footnote-on'"><xsl:value-of select="$defaultStateFootnotes" /></xsl:when>
						<xsl:when test=". eq 'prodnote-on'"><xsl:value-of select="$defaultStateProdnotes" /></xsl:when>
						<!-- There should be no other cases, but just in case: -->
						<xsl:otherwise><xsl:value-of select="'true'" /></xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
				<xsl:attribute name="bookStruct">
					<xsl:choose>
						<xsl:when test=". eq 'pagenumber-on'">PAGE_NUMBER</xsl:when>
						<xsl:when test=". eq 'sidebar-on'">OPTIONAL_SIDEBAR</xsl:when>
						<xsl:when test=". eq 'footnote-on'">NOTE</xsl:when>
						<xsl:when test=". eq 'prodnote-on'">OPTIONAL_PRODUCER_NOTE</xsl:when>
						<xsl:otherwise>UNKNOWN_BOOKSTRUCT</xsl:otherwise>
					</xsl:choose>	
				</xsl:attribute>	
			</smilCustomTest>
		</xsl:for-each>
		<!-- psps: Transfer all meta elements, as long as they don't start with dtb -->
		<xsl:for-each select="html:meta[not(@http-equiv or starts-with(@name,'dtb:'))]">
			<meta>
				<xsl:copy-of select="@*" />
			</meta>		
		</xsl:for-each>
	</head>
	<docTitle>
		<text><xsl:value-of select="html:title" /></text>
	</docTitle>
	<xsl:apply-templates select="html:meta[@name='dc:creator']" />
</xsl:template>

<xsl:template match="html:meta[@name='dc:creator']" >
<docAuthor>
	<text><xsl:value-of select="@content" /></text>
</docAuthor>
</xsl:template>

<xsl:template match="html:body">
	<xsl:call-template name="navMap" />
	<xsl:if test="html:span[@class='page-normal' or @class='page-front' or @class='page-special']">
		<xsl:call-template name="pageList" />
	</xsl:if>
	<xsl:if test="0=1">
		<xsl:call-template name="navList" />
	</xsl:if>
</xsl:template>

<xsl:template name="navMap">
	<navMap>
		<xsl:apply-templates select="html:h1" />
	</navMap>
</xsl:template>

<xsl:template match="html:h1|html:h2|html:h3|html:h4|html:h5|html:h6">
	<navPoint id="{@id}" class="{local-name()}"><xsl:attribute name="playOrder"><xsl:call-template name="positionInNCC" /></xsl:attribute>
		<navLabel>
			<text><xsl:value-of select="." /></text>
		</navLabel>
		<content src="{html:a/@href}" />
		<xsl:apply-templates select="key('nextHeadings', generate-id(.))" />
	</navPoint>
</xsl:template>

<xsl:template name="pageList">
<pageList>
	<xsl:apply-templates select="html:span[@class='page-normal' or @class='page-front' or @class='page-special']" />
</pageList>
</xsl:template>

<xsl:template match="html:span[@class='page-normal' or @class='page-front' or @class='page-special']">
	<pageTarget class="pagenum" type="{substring-after(@class, '-')}" value="{.}" id="{@id}">
		<xsl:attribute name="playOrder"><xsl:call-template name="positionInNCC" /></xsl:attribute>
		<navLabel>
			<text><xsl:value-of select="." /></text>
		</navLabel>
		<content src="{html:a/@href}" />
	</pageTarget>
</xsl:template>

<xsl:template name="positionInNCC">
	<xsl:value-of select="count(preceding::*[self::html:h1 or self::html:h2 or self::html:h3 or self::html:h4 or self::html:h5 or self::html:h6 or self::html:span[substring-before(@class, '-')='page']]) + 1" />
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
