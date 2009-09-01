<?xml version="1.0" encoding="UTF-8"?>
<!--	
		Default splitter
-->
<!--
		Joel Håkansson, TPB
		Version 2009-08-31
 -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pef="http://www.daisy.org/ns/2008/pef" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions">

	<xsl:output method="xml" media-type="application/x-pef+xml" encoding="utf-8" indent="no"/>

	<!-- min: the minimum number of sheets in each volume that will trigger an equal distribution -->
	<xsl:param name="min" select="40" as="xs:integer"/> 
	<!-- target: the preferred number of sheets in each volume -->
	<xsl:param name="target" select="50" as="xs:integer"/>
	<!-- max: the maximum number of sheets in any volume -->
	<xsl:param name="max" select="55" as="xs:integer"/>
	<!-- duplex: two pages per sheet -->
	<xsl:param name="duplex" select="true()" as="xs:boolean"/>

	<xsl:variable name="p_min" select="(if ($duplex) then 2 else 1)*$min" as="xs:integer"/> 
	<xsl:variable name="p_target" select="(if ($duplex) then 2 else 1)*$target" as="xs:integer"/>
	<xsl:variable name="p_max" select="(if ($duplex) then 2 else 1)*$max" as="xs:integer"/>

	<xsl:variable name="pages" select="count(descendant::pef:page)" as="xs:integer"/>
	<xsl:variable name="v_target" select="ceiling($pages div $p_target) cast as xs:integer" as="xs:integer"/>
	<xsl:variable name="v_max" select="ceiling($pages div $p_max) cast as xs:integer" as="xs:integer"/>
	<xsl:variable name="volumes" select="min(($v_target, $v_max))" as="xs:integer"/>
	<xsl:variable name="ppv" select="ceiling($pages div $volumes) cast as xs:integer" as="xs:integer"/>
	<xsl:variable name="ppv2" select="if ($duplex) then (ceiling($ppv div 2) * 2) cast as xs:integer else $ppv" as="xs:integer"/>
	<xsl:variable name="breakpoint" select="if($ppv2>=$p_min) then $ppv2 else $p_target" as="xs:integer"/>

	<xsl:template match="/">
		<xsl:if test="count(//pef:volume)>1">
			<xsl:message terminate="yes">File is already broken into several volumes.</xsl:message>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="pef:volume">
		<xsl:copy>
			<xsl:copy-of select="@*[not(name()='duplex')]"/>
			<xsl:attribute name="duplex" select="$duplex"/>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="pef:section">
		<xsl:copy>
			<xsl:copy-of select="@*[not(name()='duplex')]"/>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="pef:page">
		<xsl:if test="((count(preceding::pef:page)) mod $breakpoint) = 0">
			<xsl:if test="preceding::pef:page">
				<xsl:text disable-output-escaping="yes">&lt;/section>&lt;/volume>&lt;volume</xsl:text>
				<xsl:for-each select="ancestor::pef:volume/attribute()[not(name()='duplex')]">
					<xsl:value-of select="concat(' ', name(),'=&quot;', ., '&quot;')"/>
				</xsl:for-each>
				<xsl:value-of select="concat(' duplex=&quot;', $duplex, '&quot;')"/>
				<xsl:text disable-output-escaping="yes">>&lt;section</xsl:text>
				<xsl:for-each select="parent::pef:section/attribute()[not(name()='duplex')]">
					<xsl:value-of select="concat(' ', name(),'=&quot;', ., '&quot;')"/>
				</xsl:for-each>
				<xsl:text disable-output-escaping="yes">></xsl:text>
			</xsl:if>
		</xsl:if>
		<xsl:call-template name="copy"/>
	</xsl:template>

	<xsl:template match="*|comment()|processing-instruction()">
		<xsl:call-template name="copy"/>
	</xsl:template>

	<xsl:template name="copy">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>