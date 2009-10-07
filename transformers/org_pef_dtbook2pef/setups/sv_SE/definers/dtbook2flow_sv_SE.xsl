<?xml version="1.0" encoding="utf-8"?>
<!--
	TODO:
		- komplexa sub, sup
		- länkar, e-postadresser
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/" exclude-result-prefixes="dtb">

	<xsl:output method="xml" encoding="utf-8" indent="no"/>
	<xsl:include href="dtbook2flow.xsl"/>

	<xsl:template match="dtb:frontmatter" mode="apply-sequence-attributes">
		<xsl:attribute name="master">front</xsl:attribute>
		<xsl:attribute name="hyphenate">true</xsl:attribute>
		<xsl:attribute name="format">i</xsl:attribute>
		<xsl:attribute name="initial-page-number">1</xsl:attribute>
	</xsl:template>
	<xsl:template match="dtb:bodymatter | dtb:rearmatter" mode="apply-sequence-attributes">
		<xsl:attribute name="master">main</xsl:attribute>
		<xsl:attribute name="hyphenate">true</xsl:attribute>
		<xsl:attribute name="format">1</xsl:attribute>
		<xsl:attribute name="initial-page-number">1</xsl:attribute>
	</xsl:template>
	<xsl:template match="dtb:h1" mode="apply-block-attributes">
		<xsl:attribute name="margin-top">3</xsl:attribute>
		<xsl:attribute name="margin-bottom">1</xsl:attribute>
		<xsl:attribute name="keep">all</xsl:attribute>
		<xsl:attribute name="keep-with-next">1</xsl:attribute>
		<!--
		<xsl:if test="parent::dtb:level1/preceding-sibling::dtb:level1">
			<xsl:attribute name="break-before">page</xsl:attribute>
		</xsl:if>-->
	</xsl:template>
	<xsl:template match="dtb:h2" mode="apply-block-attributes">
		<xsl:attribute name="margin-top">2</xsl:attribute>
		<xsl:attribute name="margin-bottom">1</xsl:attribute>
		<xsl:attribute name="keep">all</xsl:attribute>
		<xsl:attribute name="keep-with-next">1</xsl:attribute>
	</xsl:template>
	<xsl:template match="dtb:level1" mode="apply-block-attributes">
		<xsl:attribute name="break-before">page</xsl:attribute>
		<xsl:if test="not(dtb:h1)">
			<xsl:attribute name="margin-top">3</xsl:attribute>
		</xsl:if>
	</xsl:template>
	<xsl:template match="dtb:level2" mode="apply-block-attributes">
		<xsl:if test="not(dtb:h2)">
			<xsl:attribute name="margin-top">2</xsl:attribute>
		</xsl:if>
	</xsl:template>
	<xsl:template match="dtb:blockquote" mode="apply-block-attributes">
		<xsl:attribute name="margin-left">2</xsl:attribute>
		<xsl:attribute name="margin-top">1</xsl:attribute>
		<xsl:attribute name="margin-bottom">1</xsl:attribute>
	</xsl:template>
	<xsl:template match="dtb:p" mode="apply-block-attributes">
		<xsl:if test="preceding-sibling::dtb:p">
			<xsl:attribute name="first-line-indent">2</xsl:attribute>
		</xsl:if>
	</xsl:template>
	<xsl:template match="dtb:list" mode="apply-block-attributes">
		<xsl:attribute name="margin-top">1</xsl:attribute>
		<xsl:attribute name="margin-bottom">1</xsl:attribute>
		<xsl:attribute name="list-type"><xsl:value-of select="@type"/></xsl:attribute>
			<xsl:choose>
				<xsl:when test="ancestor::dtb:list"><xsl:attribute name="margin-left">3</xsl:attribute></xsl:when>
				<xsl:otherwise><xsl:attribute name="margin-left">2</xsl:attribute></xsl:otherwise>
			</xsl:choose>
	</xsl:template>
	<xsl:template match="dtb:li" mode="apply-block-attributes">
		<xsl:choose>
			<xsl:when test="parent::dtb:list/@type='pl'"><xsl:attribute name="text-indent">3</xsl:attribute></xsl:when>
			<xsl:otherwise>
				<xsl:attribute name="first-line-indent">3</xsl:attribute>
				<xsl:attribute name="text-indent">3</xsl:attribute>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dtb:div[@class='pgroup']" mode="apply-block-attributes">
		<xsl:attribute name="margin-bottom">1</xsl:attribute>
	</xsl:template>
	<!-- Exclude docauthor and doctitle in frontmatter. These will be inserted on a title page later. -->
	<xsl:template match="dtb:doctitle[parent::dtb:frontmatter] | dtb:docauthor[parent::dtb:frontmatter]"></xsl:template>
	
	<!-- Override default processing -->
	<xsl:template match="dtb:prodnote[ancestor::dtb:imggroup]" priority="10">
		<block keep="all" keep-with-next="1"><xsl:text>:: Bildbeskrivning </xsl:text><leader position="100%" pattern=":"/></block>
		<block>
			<xsl:apply-templates/>
		</block>
		<block><leader align="right" position="100%" pattern=":"/><xsl:text>:</xsl:text></block>
	</xsl:template>
	
	<!-- Override default processing -->
	<xsl:template match="dtb:caption[ancestor::dtb:imggroup]" priority="10">
		<block keep="all" keep-with-next="1"><xsl:text>:: Bildtext </xsl:text><leader position="100%" pattern=":"/></block>
		<block>
			<xsl:apply-templates/>
		</block>
		<block><leader position="100%" pattern=":"/></block>
	</xsl:template>
	
	<!-- Svenska skrivregler för punktskrift 2009, page 34 -->
	<xsl:template match="dtb:em" mode="inline-mode">
		<xsl:call-template name="addMarkers">
			<xsl:with-param name="prefix-single-word" select="'&#x2820;&#x2804;'"/>
			<xsl:with-param name="prefix-multi-word" select="'&#x2820;&#x2824;'"/>
			<xsl:with-param name="postfix-multi-word" select="'&#x2831;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Svenska skrivregler för punktskrift 2009, page 34 -->
	<xsl:template match="dtb:strong" mode="inline-mode">
		<xsl:call-template name="addMarkers">
			<xsl:with-param name="prefix-single-word" select="'&#x2828;'"/>
			<xsl:with-param name="prefix-multi-word" select="'&#x2828;&#x2828;'"/>
			<xsl:with-param name="postfix-multi-word" select="'&#x2831;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Svenska skrivregler för punktskrift 2009, page 32 -->
	<xsl:template match="dtb:sup" mode="inline-mode">
		<xsl:call-template name="addMarkersAlfaNum">
			<xsl:with-param name="prefix" select="'&#x282c;'"/>
			<xsl:with-param name="postfix" select="''"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Svenska skrivregler för punktskrift 2009, page 32 -->
	<xsl:template match="dtb:sub" mode="inline-mode">
		<xsl:call-template name="addMarkersAlfaNum">
			<xsl:with-param name="prefix" select="'&#x2823;'"/>
			<xsl:with-param name="postfix" select="''"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="addMarkersAlfaNum">
		<xsl:param name="prefix" select="''"/>
		<xsl:param name="postfix" select="''"/>
		<xsl:choose>
			<!-- text contains a single alfa/numerical string -->
			<xsl:when test="count(node())=1 and text() and matches(text(),'^[a-zA-Z0-9]*$')">
				<xsl:value-of select="$prefix"/>
				<xsl:apply-templates/>
				<xsl:value-of select="$postfix"/>
			</xsl:when>
			<!-- Otherwise -->
			<xsl:otherwise>
				<xsl:message terminate="yes">Error: sub/sub contains a complex expression for which there is no specified formatting.</xsl:message>
				<xsl:apply-templates/>
			</xsl:otherwise>		
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
