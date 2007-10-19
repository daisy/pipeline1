<?xml version="1.0" encoding="UTF-8"?>
<!--
		Add author and title
		Inserts docauthor and doctitle

		Joel Håkansson, TPB
		Version 2007-10-15
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/" xmlns="http://www.daisy.org/z3986/2005/dtbook/" exclude-result-prefixes="dtb">

	<xsl:include href="recursive-copy.xsl"/>
	<xsl:include href="output.xsl"/>

	<xsl:template name="insertDoctitle">
		<xsl:if test="not(//dtb:doctitle)">
			<xsl:for-each select="//dtb:meta[@name='dc:Title']">
				<xsl:if test="@content!=''">
					<xsl:element name="doctitle">
						<xsl:value-of select="@content"/>
					</xsl:element>
				</xsl:if>
			</xsl:for-each>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="insertDocauthor">
		<xsl:if test="not(//dtb:docauthor)">
			<xsl:for-each select="//dtb:meta[@name='dc:Creator']">
				<xsl:if test="@content!=''">
					<xsl:element name="docauthor">
						<xsl:value-of select="@content"/>
					</xsl:element>
				</xsl:if>
			</xsl:for-each>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="dtb:frontmatter">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:call-template name="insertDoctitle"/>
			<xsl:call-template name="insertDocauthor"/>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="dtb:book">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:if test="not(dtb:frontmatter)">
				<xsl:element name="frontmatter">
					<xsl:call-template name="insertDoctitle"/>
					<xsl:call-template name="insertDocauthor"/>
				</xsl:element>
			</xsl:if>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>
	
</xsl:stylesheet>
