<?xml version="1.0" encoding="UTF-8"?>
<!--
    
    Version
    2008-04-04
    
    Description
    Prepare a dtbook to the Narrator schematron rules:
    - Rule 14: Don't allow <h x+1> in <level x+1> unless <h x> in <level x> is present
    - Rule 100: Every document needs at least one heading on level 1
    - Rule 104: Headings may not be empty elements
    Adds a text node to empty headings. 
    
    
    
    Nodes
    dtbook/h1|h2|h3|h4|h5|h6|hd
    
    Namespaces
    (x) "http://www.daisy.org/z3986/2005/dtbook/"
    
    Doctype
    (x) DTBook
    
    Author
    Romain Deltour, DAISY
	
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/" xmlns="http://www.daisy.org/z3986/2005/dtbook/" version="1.0" exclude-result-prefixes="dtb">
	
	<xsl:include href="recursive-copy.xsl"/>
	<xsl:include href="output.xsl"/>
	
	<xsl:template match="dtb:level1">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates select="node()" mode="leadingPagenum"/>
			<xsl:if test="not(dtb:h1) or dtb:h1[normalize-space(.)='']">
				<xsl:message terminate="no">Adding a dummy h1</xsl:message>
				<xsl:element name="h1" namespace="http://www.daisy.org/z3986/2005/dtbook/">dummy1</xsl:element>
			</xsl:if>
			<xsl:apply-templates select="node()" mode="notLeadingPagenum"/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="dtb:level2">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates select="node()" mode="leadingPagenum"/>
			<xsl:if test="(not(dtb:h2) or dtb:h2[normalize-space(.)='']) and descendant::*[(self::dtb:h3 or self::dtb:h4 or self::dtb:h5 or self::dtb:h6) and normalize-space(.)!='']">
				<xsl:message terminate="no">Adding a dummy h2</xsl:message>
				<xsl:element name="h2" namespace="http://www.daisy.org/z3986/2005/dtbook/">dummy2</xsl:element>
			</xsl:if>
			<xsl:apply-templates select="node()" mode="notLeadingPagenum"/>
		</xsl:copy>
	</xsl:template>
	
	
	
	<xsl:template match="dtb:level3">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates select="node()" mode="leadingPagenum"/>
			<xsl:if test="(not(dtb:h3) or dtb:h3[normalize-space(.)='']) and descendant::*[(self::dtb:h4 or self::dtb:h5 or self::dtb:h6) and normalize-space(.)!='']">
				<xsl:message terminate="no">Adding a dummy h3</xsl:message>
				<xsl:element name="h3" namespace="http://www.daisy.org/z3986/2005/dtbook/">dummy3</xsl:element>
			</xsl:if>
			<xsl:apply-templates select="node()" mode="notLeadingPagenum"/>
		</xsl:copy>
	</xsl:template>
	
	
	<xsl:template match="dtb:level4">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates select="node()" mode="leadingPagenum"/>
			<xsl:if test="(not(dtb:h4) or dtb:h4[normalize-space(.)='']) and descendant::*[(self::dtb:h5 or self::dtb:h6) and normalize-space(.)!='']">
				<xsl:message terminate="no">Adding a dummy h4</xsl:message>
				<xsl:element name="h4" namespace="http://www.daisy.org/z3986/2005/dtbook/">dummy4</xsl:element>
			</xsl:if>
			<xsl:apply-templates select="node()" mode="notLeadingPagenum"/>
		</xsl:copy>
	</xsl:template>
	
	
	<xsl:template match="dtb:level5">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates select="node()" mode="leadingPagenum"/>
			<xsl:if test="(not(dtb:h5) or dtb:h5[normalize-space(.)='']) and descendant::*[self::dtb:h6 and normalize-space(.)!='']">
				<xsl:message terminate="no">Adding a dummy h2</xsl:message>
				<xsl:element name="h5" namespace="http://www.daisy.org/z3986/2005/dtbook/">dummy5</xsl:element>
			</xsl:if>
			<xsl:apply-templates select="node()" mode="notLeadingPagenum"/>
		</xsl:copy>
	</xsl:template>
	
	
	<xsl:template match="dtb:level">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates select="node()" mode="leadingPagenum"/>
			<xsl:if test="(not(dtb:hd) or dtb:hd[normalize-space(.)='']) and descendant::dtb:level/dtb:hd[normalize-space(.)!='']">
				<xsl:element name="hd" namespace="http://www.daisy.org/z3986/2005/dtbook/">dummyHd</xsl:element>
			</xsl:if>
			<xsl:apply-templates select="node()" mode="notLeadingPagenum"/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="dtb:pagenum" mode="leadingPagenum">
		<xsl:if test="not(preceding-sibling::dtb:*[not(self::dtb:pagenum)])">
			<xsl:copy>
				<xsl:copy-of select="@*"/>
				<xsl:apply-templates/>
			</xsl:copy>
		</xsl:if>
	</xsl:template>	
	
	<xsl:template match="*" mode="notLeadingPagenum">
		
		<xsl:choose>
			<!-- if it is a leading pagenum, ignore it (already processed) -->
			<xsl:when test="self::dtb:pagenum and not(preceding-sibling::dtb:*[not(self::dtb:pagenum)])">
			</xsl:when>
			<!-- if it is an empty heading, remove it -->
			<xsl:when test="(self::dtb:h1 or self::dtb:h2 or self::dtb:h3 or self::dtb:h4 or self::dtb:h5 or self::dtb:h6 or self::dtb:hd) and (normalize-space(.)='')">
				<xsl:message terminate="no">Removing an empty <xsl:value-of select="name(.)" /></xsl:message>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="."/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>		
 
	<xsl:template match="*" mode="leadingPagenum">
	</xsl:template>
 
	 <xsl:template match="processing-instruction()|comment()" mode="leadingPagenum">
		<xsl:copy-of select="."/>
	 </xsl:template>
</xsl:stylesheet>
