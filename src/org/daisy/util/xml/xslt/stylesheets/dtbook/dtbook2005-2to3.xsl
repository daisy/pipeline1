<?xml version="1.0" encoding="UTF-8"?>
<!-- Stylesheet to migrate a dtbook 2005-2 document to 2005-3 
     This stylesheet performs no destructive actions
     jpritchett@rfbd.org
    
     First version:  13 Feb 2008
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/" 
    xmlns="http://www.daisy.org/z3986/2005/dtbook/"
    exclude-result-prefixes="dtb">

<!-- Change #1:  Rewrite the public and system IDs -->
    <xsl:output encoding="utf-8" method="xml" version="1.0" indent="no"
        doctype-public="-//NISO//DTD dtbook 2005-3//EN"
        doctype-system="http://www.daisy.org/z3986/2005/dtbook-2005-3.dtd"/>

<!-- Change #2:  Update dtbook/@version to "2005-3" -->
    <xsl:template match="dtb:dtbook">
        <xsl:copy>
            <xsl:for-each select="@*">
                <xsl:choose>
                    <xsl:when test="local-name()='version'">
                        <xsl:attribute name="version">2005-3</xsl:attribute>
                    </xsl:when>
                    <!-- All other attributes pass through unchanged -->
                    <xsl:otherwise>
                        <xsl:copy-of select="." />
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
            <xsl:apply-templates />
        </xsl:copy>
    </xsl:template>

<!-- ====  DEFAULT TEMPLATES ==== -->
    <xsl:template match="*">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates />
        </xsl:copy>
    </xsl:template>
    <xsl:template match="@*|comment()|text()">
        <xsl:copy />
    </xsl:template>
</xsl:stylesheet>
