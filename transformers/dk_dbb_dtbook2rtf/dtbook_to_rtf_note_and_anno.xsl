<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/"
	>
	<xsl:import href="dtbook_to_rtf_encode.xsl"/>
	<xsl:import href="dtbook_to_rtf_styles.xsl"/>
	<xsl:output method="text" indent="yes" encoding="Windows-1252"/>
	<xsl:strip-space elements="*"/>
	
	<!-- ##### notes and annotations ##### -->
	
	<!-- #### noteref ELEMENT #### -->
	<xsl:template match="noteref|dtb:noteref">
		<xsl:variable name="noteid" select="@idref"/>
		<xsl:if test="//*[self::note|self::dtb:note][@id=$noteid]">
			<xsl:choose>
				<xsl:when test="ancestor::note|ancestor::dtb:note|ancestor::annotation|ancestor::dtb:annotation">
					<!-- Nested notes are displayed in their full in superscript -->
					<xsl:text>{\super </xsl:text>
					<xsl:apply-templates select="//*[self::note|self::dtb:note][@id=$noteid]/node()"/>
					<xsl:text>}</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>{\super \chftn}{\footnote</xsl:text>
					<xsl:call-template name="NORMAL_STYLE_FONT_ONLY"/>
					<xsl:text>\chftn\tab </xsl:text>
					<xsl:apply-templates select="//*[self::note|self::dtb:note][@id=$noteid]/node()"/>
					<xsl:text>}</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>
	
	<!-- #### annoref ELEMENT #### -->
	<xsl:template match="annoref|dtb:annoref">
		<xsl:variable name="annoid" select="@idref"/>
		<!-- Ignore nested notes/annotations -->
		<xsl:if test="//*[self::annotation|self::dtb:annotation][@id=$annoid]">
			<xsl:choose>
				<xsl:when test="ancestor::note|ancestor::dtb:note|ancestor::annotation|ancestor::dtb:annotation">
					<!-- Nested annotations are displayed in their full in superscript -->
					<xsl:text>{\super </xsl:text>
					<xsl:apply-templates select="//*[self::annotation|self::dtb:annotation][@id=$annoid]/node()"/>
					<xsl:text>}</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>{\super \chftn}{\footnote\ftnalt</xsl:text>
					<xsl:call-template name="NORMAL_STYLE_FONT_ONLY"/>
					<xsl:text>\chftn\tab </xsl:text>
					<xsl:apply-templates select="//*[self::annotation|self::dtb:annotation][@id=$annoid]/node()"/>
					<xsl:text>}</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<!-- #### note and annotation ELEMENTS (are skipped) #### -->
	<xsl:template match="note|annotation|dtb:note|dtb:annotation"/>
	
	
</xsl:stylesheet>
