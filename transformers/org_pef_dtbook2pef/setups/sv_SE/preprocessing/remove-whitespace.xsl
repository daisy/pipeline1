<?xml version="1.0" encoding="UTF-8"?>
<!--
    Remove whitespace (sv_SE)
    Version
    2009-09-14

    Description
    Removes redundant whitespace text nodes
    
    Nodes
    *
    
    Namespaces
    (x) ""
    
    Doctype
    ( ) 
    
    Author
	Joel Håkansson, TPB
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output indent="no"/>
    <xsl:include href="recursive-copy.xsl"/>
    
    <xsl:template match="block">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:variable name="size" select="count(node())"/>
			<xsl:for-each select="node()">
				<xsl:choose>
					<xsl:when test="self::text() and string-length(normalize-space(.))=0"></xsl:when>
					<xsl:when test="self::text()">
						<xsl:choose>
							<xsl:when test="$size=1">
								<xsl:value-of select="normalize-space(.)"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:if test="position()>1 and matches(substring(., 1, 1), '\s+')">
									<xsl:text> </xsl:text>
								</xsl:if>
								<xsl:value-of select="normalize-space(.)"/>
								<xsl:if test="position()&lt;$size and matches(substring(., string-length(.), 1), '\s+')">
									<xsl:text> </xsl:text>
								</xsl:if>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise><xsl:apply-templates select="."/></xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
			<!--
			<xsl:choose>
				<xsl:when test="text()[position()=1]">
					<xsl:value-of select="normalize-space(text()[position()=1])"/><xsl:text> </xsl:text>
					<xsl:apply-templates select="node()[position()>1]"/>
				</xsl:when>
				<xsl:otherwise><xsl:apply-templates/></xsl:otherwise>
			</xsl:choose>-->
		</xsl:copy>
    </xsl:template>
<!--
	<xsl:template match="text()[string-length(normalize-space(.))=0]">
		<xsl:value-of select="normalize-space(.)"/>
	</xsl:template>
-->
</xsl:stylesheet>
