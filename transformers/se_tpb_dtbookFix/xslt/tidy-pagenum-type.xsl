<?xml version="1.0" encoding="utf-8"?>
<!--
	fix pagenum elements where the page attribute and the content don't match
	
		Version
			2007-11-29

		Description
			Update the @page attribute to make it match the contents of the pagenum
			element.
			
			If @page="front" but the contents of the element doesn't match "front"
			content, the @page attribute is changed to:
			  - @page="normal" if the contents is digits only
			  - @page="special" otherwise			

		Nodes
			pagenum

		Namespaces
			(x) "http://www.daisy.org/z3986/2005/dtbook/"

		Doctype
			(x) DTBook

		Author
			Linus Ericson, TPB
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/">

	<xsl:include href="recursive-copy2.xsl"/>
	<xsl:include href="output2.xsl"/>
	
	
	<!-- 
		Match:  This is a @page="front", but the content doesn't look like that
		Action: If the contents are numbers only, change to @page="normal",
		        otherwise change to @page="special"
	 -->
	<xsl:template match="dtb:pagenum[@page='front' and not(matches(.,'^\s*[Mm]*([Dd]?[Cc]{0,3}|[Cc][DdMm])([Ll]?[Xx]{0,3}|[Xx][LlCc])([Vv]?[Ii]{0,3}|[Ii][VvXx])\s*$','s'))]">
		<xsl:choose>			
			<xsl:when test="matches(.,'^\s*\d+\s*$','s')">
				<xsl:message>changing page="front" to page="normal"</xsl:message>
				<xsl:copy>
					<xsl:attribute name="page">normal</xsl:attribute>				
					<xsl:copy-of select="@*[local-name()!='page']"/>
					<xsl:apply-templates/>			
				</xsl:copy>
			</xsl:when>			
			<xsl:otherwise>
				<xsl:message>changing page="front" to page="special"</xsl:message>
				<xsl:copy>
					<xsl:attribute name="page">special</xsl:attribute>				
					<xsl:copy-of select="@*[local-name()!='page']"/>
					<xsl:apply-templates/>			
				</xsl:copy>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
