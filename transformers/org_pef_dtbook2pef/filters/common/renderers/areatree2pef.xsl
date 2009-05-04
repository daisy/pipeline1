<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.daisy.org/ns/2008/pef" >

	<xsl:output method="xml" media-type="application/x-pef" encoding="utf-8" indent="yes"/>
	
	<xsl:preserve-space elements="word space"/>
	<xsl:strip-space elements="*"/>
	
	<xsl:param name="cols" select="30"/>
	<xsl:param name="rows" select="29"/>
	<xsl:param name="rowgap" select="0"/>
	<xsl:param name="duplex" select="'true'"/>
	<xsl:param name="title" select="'title?'"/>
	<xsl:param name="creator" select="'creator?'"/>
	<xsl:param name="language" select="'language?'"/>
	<xsl:param name="date" select="'date?'"/>
	<xsl:param name="identifier" select="'identifier?'"/>

	<xsl:variable name="char_width" select="7"/> <!-- in pts -->
	<xsl:variable name="char_height" select="12"/> <!-- in pts -->
	<xsl:variable name="page_width" select="30"/> <!-- in characters -->
	<xsl:variable name="page_height" select="29"/> <!-- in rows -->

	<xsl:template match="areaTree">
		<xsl:processing-instruction name="xml-stylesheet">type="text/xsl" href="pef2xhtml.xsl"</xsl:processing-instruction>
		<pef version="2008-1">
			<head>
				<meta xmlns:dc="http://purl.org/dc/elements/1.1/">
					<dc:format>application/x-pef+xml</dc:format>
					<dc:identifier><xsl:value-of select="$identifier"/></dc:identifier>
					<dc:title><xsl:value-of select="$title"/></dc:title>
					<dc:creator><xsl:value-of select="$creator"/></dc:creator>
					<dc:language><xsl:value-of select="$language"/></dc:language>
					<dc:date><xsl:value-of select="$date"/></dc:date>
				</meta>
			</head>
			<body>
				<volume cols="{$cols}" rows="{$rows}" rowgap="{$rowgap}" duplex="{$duplex}">
					<section>
						<xsl:apply-templates/>
					</section>
				</volume>
			</body>
		</pef>
	</xsl:template>
	
	<xsl:template match="page">
		<page><xsl:apply-templates/></page>
	</xsl:template>
	
	<xsl:template match="lineArea">
		<row><xsl:if test="ancestor::*[@space-start]">
		<xsl:call-template name="addSpace">
			<xsl:with-param name="amount" select="floor((ancestor::*[@space-start][1]/@space-start) div (1000 * $char_width))"/>
		</xsl:call-template></xsl:if><xsl:apply-templates/><xsl:if test="ancestor::*[@space-end]">
		<xsl:call-template name="addSpace">
			<xsl:with-param name="amount" select="floor((ancestor::*[@space-end][1]/@space-end) div (1000 * $char_width))"/>
		</xsl:call-template>
		</xsl:if></row>
	</xsl:template>
	
	<xsl:template name="addSpace">
		<xsl:param name="amount" select="0"/>
		<xsl:if test="$amount&gt;0"><xsl:text> </xsl:text><xsl:call-template name="addSpace">
			<xsl:with-param name="amount" select="$amount - 1"/>
		</xsl:call-template></xsl:if>
	</xsl:template>
	
	<xsl:template match="inlineSpace"><xsl:text> </xsl:text></xsl:template>
	
	<!--
	<xsl:template match="text()">
		<xsl:value-of select="translate(., '1234567890abcdefghijklmnopqrstuvwxyzåäö', '&#x2801;&#x2803;&#x2809;&#x2819;&#x2811;&#x280B;&#x281B;&#x2813;&#x280A;&#x281A;&#x2801;&#x2803;&#x2809;&#x2819;&#x2811;&#x280B;&#x281B;&#x2813;&#x280A;&#x281A;&#x2805;&#x2807;&#x280D;&#x281D;&#x2815;&#x280F;&#x281F;&#x2817;&#x280E;&#x281E;&#x2825;&#x2827;&#x283A;&#x282D;&#x283D;&#x2835;&#x2821;&#x281C;&#x282A;')"/>
	</xsl:template>
-->
</xsl:stylesheet>
