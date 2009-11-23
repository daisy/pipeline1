<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="no"/>

	<xsl:template match="/">
		<root><sequence initial-page-number="1" master="plain"><xsl:apply-templates/></sequence></root>
	</xsl:template>

	<xsl:template match="*">
		<xsl:choose>
			<xsl:when test="parent::*/text()[not(normalize-space()='')] or count(descendant::text()[not(normalize-space()='')])=0">
				<xsl:apply-templates/>
			</xsl:when>
			<xsl:otherwise>
				<block margin-bottom="1"><xsl:apply-templates/></block></xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="text()[normalize-space()='']"/>
	
</xsl:stylesheet>
