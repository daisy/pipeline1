<?xml version="1.0" encoding="UTF-8"?>
<!--

    Add structure markers (sv_SE)
    Version
    2009-10-15
    
    Description
    Adds braille markers for dtbook elements
    
    Nodes
    *
    
    Namespaces
    (x) "http://www.daisy.org/z3986/2005/dtbook/"
    
    Doctype
    (x) DTBook
    
    Author
	Joel Håkansson, TPB
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/" xmlns="http://www.daisy.org/z3986/2005/dtbook/" exclude-result-prefixes="dtb">

    <xsl:include href="recursive-copy.xsl"/>
    <xsl:include href="output.xsl"/>

	<!-- Svenska skrivregler för punktskrift 2009, page 34 -->
	<xsl:template match="dtb:em[not(ancestor::dtb:list[@class='toc'])]">
		<xsl:call-template name="addMarkers">
			<xsl:with-param name="prefix-single-word" select="'&#x2820;&#x2804;'"/>
			<xsl:with-param name="prefix-multi-word" select="'&#x2820;&#x2824;'"/>
			<xsl:with-param name="postfix-multi-word" select="'&#x2831;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Svenska skrivregler för punktskrift 2009, page 34 -->
	<xsl:template match="dtb:strong[not(ancestor::dtb:list[@class='toc'])]">
		<xsl:call-template name="addMarkers">
			<xsl:with-param name="prefix-single-word" select="'&#x2828;'"/>
			<xsl:with-param name="prefix-multi-word" select="'&#x2828;&#x2828;'"/>
			<xsl:with-param name="postfix-multi-word" select="'&#x2831;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Svenska skrivregler för punktskrift 2009, page 32 -->
	<xsl:template match="dtb:sup">
		<xsl:call-template name="addMarkersAlfaNum">
			<xsl:with-param name="prefix" select="'&#x282c;'"/>
			<xsl:with-param name="postfix" select="''"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Svenska skrivregler för punktskrift 2009, page 32 -->
	<xsl:template match="dtb:sub">
		<xsl:call-template name="addMarkersAlfaNum">
			<xsl:with-param name="prefix" select="'&#x2823;'"/>
			<xsl:with-param name="postfix" select="''"/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- Redigering och avskrivning, page 148 -->
	<xsl:template match="dtb:dd">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:text>&#x2820;&#x2804; </xsl:text><xsl:apply-templates/>
		</xsl:copy>
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
				<xsl:message terminate="no">Error: sub/sub contains a complex expression for which there is no specified formatting.</xsl:message>
				<xsl:apply-templates/>
			</xsl:otherwise>		
		</xsl:choose>
	</xsl:template>

	<xsl:template name="addMarkers">
		<xsl:param name="prefix-single-word" select="''"/>
		<xsl:param name="postfix-single-word" select="''"/>
		<xsl:param name="prefix-multi-word" select="''"/>
		<xsl:param name="postfix-multi-word" select="''"/>
		<xsl:choose>
			<!-- if text contains one word only -->
			<xsl:when test="count(text())=1 and translate(text(), ' ', '')=text()">
				<xsl:value-of select="$prefix-single-word"/>
				<xsl:apply-templates/>
				<xsl:value-of select="$postfix-single-word"/>
			</xsl:when>
			<!-- text contains several words -->
			<xsl:otherwise>
				<xsl:value-of select="$prefix-multi-word"/>
				<xsl:apply-templates/>
				<xsl:value-of select="$postfix-multi-word"/>
			</xsl:otherwise>		
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
