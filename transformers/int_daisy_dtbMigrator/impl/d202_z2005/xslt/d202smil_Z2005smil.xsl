<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" 
	xmlns="http://www.w3.org/2001/SMIL20/"	
	exclude-result-prefixes="xs"	
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
	<xsl:param name="defaultStatePagenumbers" as="xs:string" select="'true'" />		<!-- value for head/customAttributes/customTest/@defaultState -->
	<xsl:param name="defaultStateSidebars" as="xs:string" select="'true'" /> 		<!-- value for head/customAttributes/customTest/@defaultState -->
	<xsl:param name="defaultStateFootnotes" as="xs:string" select="'true'" />		<!-- value for head/customAttributes/customTest/@defaultState -->
	<xsl:param name="defaultStateProdnotes" as="xs:string" select="'true'" /> 		<!-- value for head/customAttributes/customTest/@defaultState -->

<xsl:template match="smil">
	<smil>
<!-- 		<xsl:comment>
			Parametre:
			uid: <xsl:value-of select="$uid" />
			title: <xsl:value-of select="$title" />
			totalElapsedTime: <xsl:value-of select="$totalElapsedTime" />
			timeinThisSmil: <xsl:value-of select="$timeinThisSmil" />
			isNcxOnly: <xsl:value-of select="$isNcxOnly" />
		</xsl:comment> -->
		<xsl:apply-templates select="head" />
		<xsl:apply-templates select="body" />	
	</smil>
</xsl:template>

<xsl:template match="head">
	<head>
		<meta name="dtb:uid" content="{$uid}" />
		<meta name="dtb:totalElapsedTime" content="{$totalElapsedTime}" />
		<meta name="dtb:generator" content="DAISY Pipeline" />
		<!-- psps: Added customAttributes, what to do with defaultState (give value as param?) -->
		<xsl:if test="//par/@system-required">
			<customAttributes>
				<xsl:for-each select="distinct-values(//par/@system-required)">
					<customTest id="{substring-before(.,'-on')}" defaultState="false" override="visible">
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
					</customTest>
				</xsl:for-each>				
			</customAttributes>
		</xsl:if>	
	</head>
</xsl:template>

<xsl:template match="body">
	<body>
		<seq dur="{$timeinThisSmil}" id="mseq">
			<xsl:apply-templates select="seq/*" />
		</seq>
	</body>
</xsl:template>

<!-- psps: Changed in order to handle par with no @id  
<xsl:template match="par">
	<par id="{@id}">
		<xsl:apply-templates select="node()" />
	</par>
</xsl:template> -->

<xsl:template match="par">
	<par>
		<xsl:attribute name="id">
			<!-- Use @id on par if present, else @id on text child if present, or generate by system  -->
			<xsl:value-of select="if (@id) then @id else if (text/@id) then text/@id else generate-id()" />
		</xsl:attribute>
		<xsl:if test="@system-required">
			<xsl:attribute name="customTest">
				<xsl:value-of select="substring-before(@system-required,'-on')" />
			</xsl:attribute>
		</xsl:if>
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
		 <!--  TODO below breaks if filename contains period chars 
		<text src="{substring-before(@src, '.')}.xml#{substring-after(@src, '#')}"> -->
		<!-- psps: fixed -->
		<text src="{replace(@src,'(.+)htm(l?)#(.+)','$1xml#$3')}">
			<xsl:choose>
				<xsl:when test="@id"><xsl:attribute name="id" select="concat('text-',@id)" /></xsl:when> <!-- added 'text-' in front of @id to be sure it's unique -->
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

