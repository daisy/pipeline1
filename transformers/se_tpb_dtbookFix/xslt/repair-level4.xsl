<?xml version="1.0" encoding="utf-8"?>
<!--
	Add Level4
		Version
			2007-09-27

		Description
			See "level-tools.xls" for description

		Nodes
			level4

		Namespaces
			(x) "http://www.daisy.org/z3986/2005/dtbook/"

		Doctype
			(x) DTBook

		Author
			Joel Håkansson, TPB
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/">

	<xsl:include href="recursive-copy.xsl"/>
	<xsl:include href="output.xsl"/>
	<xsl:include href="level-tools.xsl"/>
	
	<xsl:template match="dtb:level3">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:call-template name="addSubStructure">
				<xsl:with-param name="level" select="4"/>
			</xsl:call-template>
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>
