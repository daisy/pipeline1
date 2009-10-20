<?xml version="1.0" encoding="UTF-8"?>
<!--	
		Simple splitter

		Splits a PEF file with a single volume into several volumes.
		The splitter will distribute pages into volumes equally with a
		maximum of "max" pages per volume if the number
		of pages in the last volume will be greater than or equal to the "min"
		parameter. Otherwise the "target" parameter will be used for all 
		volumes except the last, which will contain significantly fewer
		pages than "min".

		Note: Simple splitter does not preserve or respect the duplex attribute
		anywhere in the input file. The duplex attribute will be overwritten 
		using the value of the duplex parameter supplied when calling the xslt.

		Future improvements: Support existing duplex attributes in input.
-->
<!--
		Joel Håkansson, TPB
		Version 2009-10-08
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
	
	<xsl:variable name="breakpoint">
		<xsl:call-template name="calcBreakPoint">
			<xsl:with-param name="pages" select="if ($duplex) then 
				count(descendant::pef:page)+count(descendant::pef:section[(count(pef:page) mod 2)=1])
				else count(descendant::pef:page)" as="xs:integer"/>
		</xsl:call-template>
	</xsl:variable>

	<xsl:template name="calcBreakPoint">
		<xsl:param name="pages" select="0"/>

		<xsl:variable name="p_min" select="(if ($duplex) then 2 else 1)*$min" as="xs:integer"/> 
		<xsl:variable name="p_target" select="(if ($duplex) then 2 else 1)*$target" as="xs:integer"/>
		<xsl:variable name="p_max" select="(if ($duplex) then 2 else 1)*$max" as="xs:integer"/>
		<xsl:variable name="volumes" select="ceiling($pages div $p_max) cast as xs:integer" as="xs:integer"/>
		<xsl:variable name="ppv" select="ceiling($pages div $volumes) cast as xs:integer" as="xs:integer"/>
		<xsl:variable name="ppv2" select="if ($duplex) then (ceiling($ppv div 2) * 2) cast as xs:integer else $ppv" as="xs:integer"/>
		<xsl:variable name="plv" select="$pages - ($ppv2 * ($volumes - 1))"/>
		<xsl:variable name="breakpoint" select="if($plv>=$p_min) then $ppv2 else $p_target" as="xs:integer"/>

		<xsl:message terminate="no">---Debug---</xsl:message>
		<xsl:message terminate="no">ppv: <xsl:value-of select="$ppv"/>, ppv2: <xsl:value-of select="$ppv2"/>, plv: <xsl:value-of select="$plv"/></xsl:message>
		<xsl:message terminate="no">Pages: <xsl:value-of select="$pages"/></xsl:message>
		<xsl:message terminate="no">Volumes: <xsl:value-of select="$volumes"/></xsl:message>
		<xsl:message terminate="no">Breakpoint (pages): <xsl:value-of select="$breakpoint"/></xsl:message>
		<xsl:message terminate="no">Pages in last volume: <xsl:value-of select="$pages - ($breakpoint * ($volumes - 1))"/>
</xsl:message>
		<xsl:value-of select="$breakpoint"/>
	</xsl:template>

<!--	
	<xsl:template name="i">
		<xsl:param name="n"/>
		<xsl:param name="b" select="300"/>
		<xsl:choose>
			<xsl:when test="$n>=$b"></xsl:when>
			<xsl:otherwise>
				<xsl:message terminate="no">
					<xsl:call-template name="calcBreakPoint">
						<xsl:with-param name="pages" select="$n"/>
					</xsl:call-template>
				</xsl:message>
				<xsl:call-template name="i">
					<xsl:with-param name="n" select="$n+1"/>
					<xsl:with-param name="b" select="$b"/>
				</xsl:call-template>
			</xsl:otherwise>		
		</xsl:choose>
	</xsl:template>
-->

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
		<xsl:variable name="pageIndex" select="if ($duplex) then 
				count(preceding::pef:page) + count(preceding::pef:section[(count(pef:page) mod 2)=1])
				else count(preceding::pef:page)
				"/>
		<xsl:comment>Page: <xsl:value-of select="$pageIndex"/></xsl:comment>
		<xsl:if test="($pageIndex mod $breakpoint) = 0">
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