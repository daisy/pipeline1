<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes" encoding="UTF-8" />

  <xsl:param name="hepp"/>

  <xsl:template match="/">
  	<xsl:choose>
  		<xsl:when test="$hepp">
  			<xsl:text>Hepp: </xsl:text>
  			<xsl:value-of select="$hepp"/>
  		</xsl:when>
  		<xsl:otherwise>
  			<xsl:text>No hepp here.</xsl:text>
  		</xsl:otherwise>
  	</xsl:choose>
  	<x>
    <xsl:copy-of select="." />
    </x>
  </xsl:template>

</xsl:stylesheet>
