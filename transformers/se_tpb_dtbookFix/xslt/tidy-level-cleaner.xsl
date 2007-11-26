<?xml version="1.0" encoding="UTF-8"?>
<!--
	Level cleaner
		Version
			2007-09-17

		Description
			Redundant level structure is sometimes used to mimic the original layout, 
			but can pose a problem in some circumstances. "Level cleaner" simplifies 
			the level structure by removing redundant levels (subordinate levels will 
			be moved upwards). Note that the headings of the affected levels will 
			also change, which will alter the appearance of the layout.

		Nodes
			levelx
			hx

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

	<!-- if levelx parent has only one child (this), then remove levelx -->
	<xsl:template match="dtb:level2|dtb:level3|dtb:level4|dtb:level5|dtb:level6">
		<xsl:param name="remove" select="0"/>
		<xsl:choose>
			<xsl:when test="count(parent::node()/*)=1">
				<xsl:apply-templates>
					<xsl:with-param name="remove" select="$remove+1"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:when test="$remove&gt;0">
				<xsl:element name="level{substring(name(), 6)-$remove}" namespace="http://www.daisy.org/z3986/2005/dtbook/">
					<xsl:copy-of select="@*"/>
					<xsl:apply-templates>
						<xsl:with-param name="remove" select="$remove"/>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:when>
			<xsl:when test="not(node())"/>
			<xsl:otherwise>
				<xsl:copy>
					<xsl:copy-of select="@*"/>
					<xsl:apply-templates>
						<xsl:with-param name="remove" select="$remove"/>
					</xsl:apply-templates>
				</xsl:copy>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- remove empty level1 -->
	<xsl:template match="dtb:level1[not(node())]"/>
	
	<xsl:template match="dtb:h2|dtb:h3|dtb:h4|dtb:h5|dtb:h6">
		<xsl:param name="remove" select="0"/>
		<xsl:element name="h{substring(name(), 2)-$remove}" namespace="http://www.daisy.org/z3986/2005/dtbook/">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>

</xsl:stylesheet>
