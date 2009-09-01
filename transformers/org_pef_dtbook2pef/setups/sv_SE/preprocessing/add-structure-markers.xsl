<?xml version="1.0" encoding="UTF-8"?>
<!--
    Add structure markers (sv_SE)
    Version
    2009-07-01
    
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

	<xsl:template match="dtb:em">
		<xsl:call-template name="addMarkers">
			<xsl:with-param name="prefix-single-word" select="'&#x2820;&#x2804;'"/> <!-- 2820, 2804 -->
			<xsl:with-param name="postfix-single-word" select="''"/>
			<xsl:with-param name="prefix-multi-word" select="'&#x2824;&#x2804;'"/> <!-- 2824, 2804 -->
			<xsl:with-param name="postfix-multi-word" select="'&#x2820;&#x2804;'"/> <!-- 2820, 2804 -->
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="dtb:strong">
		<xsl:call-template name="addMarkers">
			<xsl:with-param name="prefix-single-word" select="'&#x2820;&#x2804;'"/> <!-- 2820, 2804 -->
			<xsl:with-param name="postfix-single-word" select="''"/>
			<xsl:with-param name="prefix-multi-word" select="'&#x2824;&#x2804;'"/> <!-- 2824, 2804 -->
			<xsl:with-param name="postfix-multi-word" select="'&#x2820;&#x2804;'"/> <!-- 2820, 2804 -->
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="dtb:sup">
		<xsl:message terminate="yes">add-structure-markers.xsl: Element "sup" not supported</xsl:message>
	</xsl:template>

	<xsl:template match="dtb:sub">
		<xsl:message terminate="yes">add-structure-markers.xsl: Element "sub" not supported</xsl:message>
	</xsl:template>

	<xsl:template name="addMarkers">
		<xsl:param name="prefix-single-word"/>
		<xsl:param name="postfix-single-word"/>
		<xsl:param name="prefix-multi-word"/>
		<xsl:param name="postfix-multi-word"/>
		<xsl:variable name="value" select="normalize-space(text())"/>
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:choose>
				<!-- if text contains one word only -->
				<xsl:when test="translate($value, ' ', '')=$value">
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
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>
