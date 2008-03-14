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
	- creation of smilCustomTest must be changed
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
<xsl:param name="nccFolder" as="xs:string" select="'path'" /> 					<!-- path to D202 DTB folder -->

<xsl:variable name="NCC.Folder" as="xs:string" select="translate($nccFolder,'\','/')" />

<xsl:template match="/html:html">
	<ncx version="2005-1">
		<xsl:attribute name="xml:lang">
			<xsl:choose>
				<xsl:when test="@xml:lang">
					<xsl:value-of select="@xml:lang" />
				</xsl:when>
				<xsl:when test="//html:meta[@name eq 'dc:language']">
					<xsl:value-of select="//html:meta[@name eq 'dc:language'][1]/@content" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'en'" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:attribute>
		<xsl:apply-templates select="html:head" />
		<xsl:apply-templates select="html:body" />
	</ncx> 
</xsl:template>


<xsl:variable name="List.smilCustomTest" as="xs:string*"> <!-- collect a list of all distinct customTests in all smil fles -->
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
		<xsl:choose>
			<xsl:when test="true()">
				<smilCustomTest id="pagenumber"  defaultState="true" override="visible" bookStruct="PAGE_NUMBER"/>
				<smilCustomTest id="prodnote"  defaultState="true" override="visible" bookStruct="OPTIONAL_PRODUCER_NOTE"/>
				<smilCustomTest id="footnote"  defaultState="true" override="visible" bookStruct="NOTE"/>
				<smilCustomTest id="sidebar"  defaultState="true" override="visible" bookStruct="OPTIONAL_SIDEBAR"/>
			</xsl:when>
			<xsl:otherwise>
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
			</xsl:otherwise>
		</xsl:choose>
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
	<xsl:if test="html:div[@class eq 'group']">
		<xsl:call-template name="navListDiv" />
	</xsl:if>
	<xsl:if test="html:span[matches(@class,'^sidebar$|^optional-prodnote$|^noteref$')]">
		<xsl:call-template name="navListSpan" />	<!-- Sidebars, prodnotes and/or noterefs -->
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
		<content src="{html:a/@href}" /> <!-- psps: tweak -->
		<xsl:apply-templates select="key('nextHeadings', generate-id(.))" />
	</navPoint>
</xsl:template>

<xsl:template name="pageList">
<pageList>
	<xsl:apply-templates select="html:span[@class='page-normal' or @class='page-front' or @class='page-special']" />
</pageList>
</xsl:template>

<!-- psps: Use normalize-space(.) to avoid lots of white-space -->
<xsl:template match="html:span[@class='page-normal' or @class='page-front' or @class='page-special']">
	<pageTarget class="pagenum" type="{substring-after(@class, '-')}" value="{normalize-space(.)}" id="{@id}">
		<xsl:attribute name="playOrder"><xsl:call-template name="positionInNCC" /></xsl:attribute>
		<navLabel>
			<text><xsl:value-of select="normalize-space(.)" /></text>
		</navLabel>
		<content src="{html:a/@href}" /> 
	</pageTarget>
</xsl:template>

<xsl:template name="navListDiv">
<navList id="navlist-group" class="group">
	<navInfo>
		<!-- psps: Language? -->
		<text>This list contains the <xsl:value-of select="count(html:div[@class eq 'group'])" /> groups found in this book.</text>
	</navInfo>
	<navLabel>
		<text>Groups</text>
	</navLabel>
	<xsl:apply-templates select="html:div[@class eq 'group']" />
</navList>
</xsl:template>

<xsl:template name="navListSpan">
	<xsl:variable name="DTB.body" as="element()" select="//html:body" />
	<!-- Create a list of all the distinct class attribute values for all the span.??? -->
	<xsl:variable name="span.type" as="xs:string*"
		select="distinct-values(for $e in html:span[matches(@class,'^sidebar$|^optional-prodnote$|^noteref$')] return $e/@class)" />
	<xsl:for-each select="$span.type">
		<xsl:variable name="type" as="xs:string" select="." />
		<navList id="navlist-{$type}" class="{$type}">
			<navInfo>
				<text>The list contains the <xsl:value-of select="count($DTB.body/html:span[@class eq $type])" />
					<xsl:text> </xsl:text>
					<xsl:choose>
						<xsl:when test="$type eq 'sidebar'">sidebars</xsl:when>
						<xsl:when test="$type eq 'optional-prodnote'">optional producer notes</xsl:when>
						<xsl:otherwise>notes</xsl:otherwise>
					</xsl:choose>
					found in this book.</text>
			</navInfo>
			<navLabel>
				<text>
					<xsl:choose>
						<xsl:when test="$type eq 'sidebar'">Sidebars</xsl:when>
						<xsl:when test="$type eq 'optional-prodnote'">Optional producer notes</xsl:when>
						<xsl:otherwise>Notes</xsl:otherwise>
					</xsl:choose>
				</text>
			</navLabel>
			<xsl:apply-templates select="$DTB.body/html:span[@class eq $type]" />
		</navList>
	</xsl:for-each>
</xsl:template>

<xsl:template match="html:span[matches(@class,'^sidebar$|^optional-prodnote$|^noteref$')] | html:div[@class eq 'group']">
	<navTarget class="{@class}" id="{concat(@class,'-',position())}">
		<xsl:attribute name="playOrder"><xsl:call-template name="positionInNCC" /></xsl:attribute>
		<navLabel>
			<text><xsl:value-of select="normalize-space(.)" /></text>
		</navLabel>
		<content src="{html:a/@href}" />
	</navTarget>
</xsl:template>


<xsl:template name="positionInNCC">
	<xsl:value-of select="count(preceding::*[
		self::html:span[matches(@class,'^sidebar$|^optional-prodnote$|^noteref$')]
		or self::html:div[@class eq 'group'] 
		or self::html:h1 or self::html:h2 or self::html:h3 or self::html:h4 or self::html:h5 or self::html:h6 
		or self::html:span[substring-before(@class, '-')='page']]) + 1" />
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
