<?xml version="1.0" encoding="utf-8"?>
<!--
	Fix idref attribute on noteref and annoref elements
		Version
			2007-11-23

		Description
			The value of the idref must include a fragment identifier.
			Add a hash mark in the beginning of all idref attributes that don't
			contain a hash mark.

		Nodes
			noteref, annoref

		Namespaces
			(x) "http://www.daisy.org/z3986/2005/dtbook/"

		Doctype
			(x) DTBook

		Author
			Linus Ericson, TPB
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/">

	<xsl:include href="recursive-copy.xsl"/>
	<xsl:include href="output.xsl"/>
	
	<xsl:template match="dtb:noteref|dtb:annoref">
		<xsl:copy>
			<xsl:copy-of select="@*[not(local-name()='idref')]"/>
			<xsl:choose>
				<xsl:when test="not(contains(@idref,'#'))">
					<xsl:attribute name="idref">
						<xsl:text>#</xsl:text>
						<xsl:value-of select="@idref"/>
					</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:copy-of select="@idref"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>
