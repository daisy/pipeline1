<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="text" omit-xml-declaration="yes" standalone="no" indent="no"/>
	
	
	<xsl:template match="em">
		<xsl:text>&lt;emph></xsl:text>
		<xsl:apply-templates />
		<xsl:text>&lt;/emph></xsl:text>
	</xsl:template>
	
	
	<xsl:template match="strong">
		<xsl:text>&lt;emph></xsl:text>
		<xsl:apply-templates />
		<xsl:text>&lt;/emph></xsl:text>
	</xsl:template>	
	
	<!--
	<xsl:template match="text()">
		<xsl:value-of select="replace(replace(current(), '&amp;', '&amp;amp;'), '&lt;', '&amp;lt;')"/>
	</xsl:template>
	-->
	<xsl:template match="text()">
		<xsl:value-of select="replace(current(), '&lt;', '&amp;lt;')"/>
	</xsl:template>
	
	<xsl:template match="br">
		<xsl:text>. </xsl:text>
	</xsl:template>	
	
	
	<xsl:template match="pagenum[@page='front']">
		<xsl:choose>
			<xsl:when test="lang('sv')">
				<xsl:text>Romersk siffra, sidan </xsl:text>
				<xsl:value-of select="current()"/>
				<xsl:text>. </xsl:text>
			</xsl:when>
			
			<!-- lang('en') as default -->
			<xsl:otherwise>
				<xsl:text>Page, Roman Numeral, </xsl:text>
				<xsl:value-of select="current()"/>
				<xsl:text>. </xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	
	<xsl:template match="pagenum">
		<xsl:choose>
			<xsl:when test="lang('sv')">
				<xsl:text>Sidan </xsl:text>
				<xsl:value-of select="current()"/>
				<xsl:text>. </xsl:text>
			</xsl:when>
			
			<!-- lang('en') as default -->
			<xsl:otherwise>
				<xsl:text>Page </xsl:text>
				<xsl:value-of select="current()"/>
				<xsl:text>. </xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	

	<xsl:template match="/">
		<xsl:apply-templates/>
	</xsl:template>
</xsl:stylesheet>