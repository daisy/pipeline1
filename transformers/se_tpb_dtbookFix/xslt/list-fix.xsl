<?xml version="1.0" encoding="utf-8"?>
<!--
	List fix
		Version
			2007-09-27

		Description
			...

		Nodes
			levelx

		Namespaces
			(x) ""
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
			<xsl:call-template name="copy"/>
		</xsl:element>
	</xsl:template>
	
</xsl:stylesheet>
