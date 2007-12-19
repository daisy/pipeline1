<?xml version="1.0" encoding="UTF-8"?>
<!--
    Add xml:lang to dtbook
    Version
    2007-11-30
    
    Description
    Adds @xml:lang to dtbook, if dc:Language metadata is present
    
    Nodes
    dtbook
    
    Namespaces
    (x) "http://www.daisy.org/z3986/2005/dtbook/"
    
    Doctype
    (x) DTBook
    
    Author
    James Pritchett, RFB&D
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/" xmlns="http://www.daisy.org/z3986/2005/dtbook/" exclude-result-prefixes="dtb">
    
    <xsl:include href="recursive-copy.xsl"/>
    <xsl:include href="output.xsl"/>

    <xsl:template match="dtb:dtbook">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:if test="count(@xml:lang)=0 and count(dtb:head/dtb:meta[@name='dc:Language'])&gt;0">
            	<xsl:message terminate="no">Adding @xml:lang to dtbook element</xsl:message>
                <xsl:attribute name="xml:lang">
                    <xsl:value-of select="dtb:head/dtb:meta[@name='dc:Language']/@content"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:apply-templates />
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
