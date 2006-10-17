<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:d="http://www.daisy.org/z3986/2005/dtbook/"
	xmlns:meta="rnib.org.uk/tbs#"
	xmlns:hks="http://www.statped.no/huseby/xml/" 
	xmlns:xs="http://www.w3.org/2001/XMLSchema" 
	exclude-result-prefixes="d xs meta hks" 
	>

<!-- DOCUMENTATION -->
<meta:doc xmlns:meta="rnib.org.uk/tbs#">
	<meta:revhistory>
		<meta:purpose>
			<meta:para>Stylesheet for migrating a DTBook110 compliant XML file
				to a DTBook-2005-1 compliant XML file, and also for migrating a
				DTBook-2005-1 compliant XML file to a DTBook-2005-2 compliant XML
file.		
			</meta:para>
			<meta:para>
				Both types of upgrading (DTBook 1.1.0 to DTBook 2005-1) and (DTBook
2005-1 to DTBook 2005-2)
				are handled by the same set of rules, using the fact that a namespace
is defined for 
				DTBook 2005-1 but not for DTBook 1.1.0.
			</meta:para>
			<meta:para>
				The files DTBook110_to_DTBook2005_1.xsl and
DTBook2005_1_to_DTBook2005_2.xsl are used as wrappers
				for the transformation. They set the proper doctypes for the upgraded
DTBook file.
			</meta:para>
		</meta:purpose>
		<meta:revision>
			<meta:revnumber>0.9</meta:revnumber>
			<meta:date>2006-09-27</meta:date>
			<meta:authorinitials>PerS</meta:authorinitials>
			<meta:revdescription>
				<meta:para>Basic starting point. Probably a few things
missing.</meta:para>
			</meta:revdescription>
			<meta:revremark />
		</meta:revision>
		<meta:revision>
			<meta:revnumber>0.9.1</meta:revnumber>
			<meta:date>2006-10-05</meta:date>
			<meta:authorinitials>PerS</meta:authorinitials>
			<meta:revdescription>
				<meta:param>No log is output, only the upgraded DTBook XML
file.</meta:param>
				<meta:param>
					The number of defined functions is reduced to one. It is placed in
this file, 
					and the file Functions.xsl is no longer used.
				</meta:param>
			</meta:revdescription>
			<meta:revremark />
		</meta:revision>
	</meta:revhistory>
	<meta:authors>
		<meta:author>
			<meta:initials>PerS</meta:initials>
			<meta:fullname>Per Sennels</meta:fullname>
			<meta:email>per.sennels@statped.no</meta:email>
		</meta:author>
	</meta:authors>
</meta:doc>

<!-- VARIABLES -->
<!-- Get various information about the input file -->
<xsl:variable name="DTBook.In.filename.complete" as="xs:string" 
	select="substring-after(document-uri(.),$transformerEngine.documentURI.prefix)"
/>

<!-- Miscellaneous -->
<xsl:variable name="verbose.debug" as="xs:boolean" select="false()" />
<xsl:variable name="verbose.eliminated-element" as="xs:boolean"
select="false()" />
<xsl:variable name="verbose.standard-comment" as="xs:boolean"
select="true()" />
<xsl:variable name="transformation.dateTime" as="xs:string"
	select="concat(
		format-date(current-date(),'[Y]-[M01]-[D01]','en',(),()),
		', ',
		format-dateTime(current-dateTime(),'[H]:[m]','en',(),())
		)" />
<xsl:variable name="revision.last" as="element()" 
	select="document('')//meta:doc/meta:revhistory/meta:revision[last()]"/>		
<xsl:variable name="transformerEngine.documentURI.prefix" as="xs:string"
	select="'file:/'" />


<!-- TRANSFORMATION -->	

<xsl:template match="/">
	<xsl:apply-templates />
</xsl:template>


<!-- The rest of this stylesheet contains rules for DTBook-2-DTBook
transformation 
	The code is basically copied from the file dtbookv110Tov20052.xsl,
	and then modified by PerS
-->

<!-- The basic transformation rule for all elements.
	Note the low priority for this template. Special elements that need
special treatment
	will be handled by other templates with higher priority
-->
<xsl:template match="*" priority="-5">
	<xsl:call-template name="debug" />
	<xsl:element name="{local-name()}"
namespace="http://www.daisy.org/z3986/2005/dtbook/">
		<!-- If not present, add required attribute (defaults to ul) for lists
... -->
		<xsl:if test="name()='list' and not(@type)">
			<xsl:attribute name="type">ul</xsl:attribute>
		</xsl:if>
		<!-- ... and for sidebars (defaults to required) -->
		<xsl:if test="name()='sidebar' and not(@render)">
			<xsl:attribute name="render">required</xsl:attribute>
		</xsl:if>
		<xsl:call-template name="attr"/>
		<xsl:apply-templates select="node()"/>
	</xsl:element>
</xsl:template>

<xsl:template match="text()">
	<xsl:copy/>
</xsl:template>


<!-- Exceptions -->
<xsl:template match="level/levelhd">
	<xsl:call-template name="debug" />
	<xsl:element name="hd"
namespace="http://www.daisy.org/z3986/2005/dtbook/">
		<xsl:call-template name="attr"/>
		<xsl:apply-templates select="node()"/>
	</xsl:element>
</xsl:template>

<xsl:template match="/dtbook | /d:dtbook">
    <xsl:element name="dtbook"
namespace="http://www.daisy.org/z3986/2005/dtbook/">
		<xsl:attribute name="version" select="$DTBook.Out.version" />
		<xsl:call-template name="attr"/>
		<xsl:call-template name="standard-comment" />
		<xsl:call-template name="debug" />
		<xsl:apply-templates select="*"/>
    </xsl:element>
</xsl:template>
  
<xsl:template match="head/title">
	<xsl:element name="meta"
namespace="http://www.daisy.org/z3986/2005/dtbook/">
		<xsl:attribute name="name" select="'dc:title'" />
		<xsl:attribute name="content" select="." />
	</xsl:element>
</xsl:template>

<xsl:template match="hr | notice">
	<xsl:call-template name="eliminated-element" />
</xsl:template>

<!-- NAMED TEMPLATES -->
<xsl:template name="eliminated-element">
	<xsl:if test="$verbose.eliminated-element">
		<xsl:comment><xsl:value-of select="concat('Eliminated element:
',local-name())" /></xsl:comment>
	</xsl:if>
</xsl:template>

<xsl:template name="attr">
	<xsl:for-each select="@*">
		<xsl:choose>
			<xsl:when test="name()='lang'">
				<xsl:attribute name="xml:lang">
					<xsl:value-of select="."/>
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="name(..)='dtbook' and name()='version'"/>
			<xsl:when test="name(..)='levelhd' and name()='depth'"/>
			<xsl:otherwise>
				<xsl:attribute name="{name()}">
					<xsl:value-of select="."/>
				</xsl:attribute>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:for-each>
</xsl:template>

<xsl:template name="standard-comment">
	<xsl:if test="$verbose.standard-comment">
		<xsl:text>
		</xsl:text>
			<xsl:comment>
				This DTBook file was automatically created <xsl:value-of
select="$transformation.dateTime" />
				as a result of the transformation of the file <xsl:value-of
select="$DTBook.In.filename.complete" />,
				using the transformation stylesheet <xsl:value-of
select="hks:filenameFromURI(document-uri(document('')))" />
							<xsl:text>, version </xsl:text>
							<xsl:value-of select="$revision.last/meta:revnumber" />
							<xsl:text> (</xsl:text>
							<xsl:value-of select="$revision.last/meta:date" />
							<xsl:text>)
				</xsl:text>
			</xsl:comment>
		<xsl:text>
		</xsl:text>
	</xsl:if>
</xsl:template>

<xsl:template name="debug">
	<xsl:if test="$verbose.debug">
		<xsl:comment>
			<xsl:value-of select="concat('Element: ',name())" />
		</xsl:comment>
	</xsl:if>
</xsl:template>


<!-- FUNCTIONS -->
<xsl:function name="hks:filenameFromURI" as="xs:string">
	<xsl:param as="xs:string" name="uri" />
	<xsl:variable name="uri.normalized" as="xs:string" 
		select="translate($uri,'\','/')" />
	<xsl:value-of select="replace($uri.normalized,'^.*/','')" />
</xsl:function>

</xsl:stylesheet>