<?xml version="1.0" encoding="utf-8"?>
<!--
	List fix
		Version
			2007-11-26

		Description
			List fix:
				- wrapps a list in li when the parent of the list is another list
				- adds @type if missing (default value is "pl")

		Nodes
			list, li

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

	<xsl:template match="dtb:list[parent::dtb:list]">
		<xsl:element name="li" namespace="http://www.daisy.org/z3986/2005/dtbook/">
			<xsl:call-template name="copyList"/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="dtb:list">
		<xsl:call-template name="copyList"/>
	</xsl:template>
	
	<xsl:template name="copyList">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:if test="not(@type)"><xsl:attribute name="type">pl</xsl:attribute></xsl:if>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>
