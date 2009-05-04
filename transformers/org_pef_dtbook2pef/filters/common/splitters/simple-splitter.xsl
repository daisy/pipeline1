<?xml version="1.0" encoding="UTF-8"?>
<!--	
		Default splitter
-->
<!--
		Joel Håkansson, TPB
		Version 2008-05-26
 -->
<!-- 
	TODO: If duplex=true(), the number of pages in each volume should be even.
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pef="http://www.daisy.org/ns/2008/pef" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions">

	<xsl:output method="xml" media-type="application/x-pef" encoding="utf-8" indent="no"/>
	
	<!-- min: the minimum number of sheets in each volume that will trigger an equal distribution -->
	<xsl:param name="min" select="40" as="xs:integer"/> 
	<!-- target: the preferred number of sheets in each volume -->
	<xsl:param name="target" select="50" as="xs:integer"/>
	<!-- max: the maximum number of sheets in any volume -->
	<xsl:param name="max" select="55" as="xs:integer"/>
	<!-- duplex: two pages per side? -->
	<xsl:param name="duplex" select="true()" as="xs:boolean"/>

	<xsl:template match="/">
		<xsl:if test="count(//pef:volume)>1">
				<xsl:message terminate="no">File is already broken into several volumes.</xsl:message>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="pef:volume">
		<xsl:variable name="p_min" select="(if ($duplex) then 2 else 1)*$min" as="xs:integer"/> 
		<xsl:variable name="p_target" select="(if ($duplex) then 2 else 1)*$target" as="xs:integer"/>
		<xsl:variable name="p_max" select="(if ($duplex) then 2 else 1)*$max" as="xs:integer"/>

		<xsl:variable name="pages" select="count(descendant::pef:page)" as="xs:integer"/>
		<xsl:variable name="v_target" select="ceiling($pages div $p_target) cast as xs:integer" as="xs:integer"/>
		<xsl:variable name="v_max" select="ceiling($pages div $p_max) cast as xs:integer" as="xs:integer"/>
		<xsl:variable name="volumes" select="min(($v_target, $v_max))" as="xs:integer"/>
		<xsl:variable name="ppv" select="ceiling($pages div $volumes) cast as xs:integer" as="xs:integer"/>
		<xsl:variable name="breakpoint" select="if($ppv>=$p_min) then $ppv else $p_target" as="xs:integer"/>
		<xsl:variable name="atts" select="@*[not(name()='duplex')]"/>

		<xsl:for-each-group group-starting-with="pef:page[((position()-1) mod $breakpoint) = 0]" select="descendant::pef:page">
			<xsl:element name="volume" namespace="http://www.daisy.org/ns/2008/pef">
				<xsl:copy-of select="$atts"/>
				<xsl:attribute name="duplex" select="$duplex"/>
				<xsl:element name="section" namespace="http://www.daisy.org/ns/2008/pef">
					<xsl:for-each select="current-group()">
						<xsl:call-template name="copy"/>
					</xsl:for-each>
				</xsl:element>
			</xsl:element>
		</xsl:for-each-group>
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
