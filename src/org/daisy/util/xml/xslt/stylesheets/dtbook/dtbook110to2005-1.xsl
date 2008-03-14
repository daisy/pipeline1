<?xml version="1.0"?>
<!-- Stylesheet to convert dtbook3 version 3-06 to dtbook-2005-1
     James Pritchett, RFB&D
	 Based on dtb36to39.xsl (Aug 2001)
     April 2006
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output omit-xml-declaration="yes" 
		    encoding="UTF-8"
		    doctype-public="-//NISO//DTD dtbook 2005-1//EN"
		    doctype-system="http://www.daisy.org/z3986/2005/dtbook-2005-1.dtd"
		    indent="yes"
		    method="xml" />

<!-- NOTE:  As a temporary thing for doing TTS work, we kill the links -->
<xsl:template match="a">
	<xsl:apply-templates />
</xsl:template>

<xsl:template match="*">
	<xsl:copy>
		<xsl:if test="name()!='head' and name()!='meta'">
			<xsl:attribute name="id"><xsl:value-of select="generate-id()" /></xsl:attribute>
		</xsl:if>
		<xsl:copy-of select="@*" />
		<xsl:apply-templates />
	</xsl:copy>
</xsl:template>

<!-- Root element name is changed, plus add version & namespace -->
<xsl:template match="dtbook">
	<dtbook version="2005-1">
		<xsl:apply-templates />
	</dtbook>
</xsl:template>

<!-- levelhd is transformed to hd, including all attributes excluding the depth attribute -->
<xsl:template match="levelhd">
	<hd>
		<xsl:copy-of select="@*[name()!='depth']" />
		<xsl:apply-templates />
	</hd>
</xsl:template>

<!-- Kill hr's, style's, and head/title's -->
<xsl:template match="hr | style | head/title" />

<!-- prodnote.photoDesc is now an imggroup, 
     and the span.photoCaption inside it is a caption -->
<xsl:template match="prodnote[@class='photoDesc']">
	<imggroup>
		<xsl:attribute name="id"><xsl:value-of select="generate-id()" /></xsl:attribute>
		<xsl:apply-templates />
	</imggroup>
</xsl:template>

<xsl:template match="prodnote/span[@class='photoCaption']">
	<caption>
		<xsl:attribute name="id"><xsl:value-of select="generate-id()" /></xsl:attribute>
		<xsl:apply-templates />
	</caption>
</xsl:template>

<!-- spans of classes sidebarHead and sidebarTitle become hd's -->
<xsl:template match="span[@class='sidebarHead']">
	<hd>
		<xsl:attribute name="id"><xsl:value-of select="generate-id()" /></xsl:attribute>
		<xsl:copy-of select="@*" />
		<xsl:apply-templates />
	</hd>
</xsl:template>

<xsl:template match="span[@class='sidebarTitle']">
	<hd>
		<xsl:attribute name="id"><xsl:value-of select="generate-id()" /></xsl:attribute>
		<xsl:copy-of select="@*" />
		<xsl:apply-templates />
	</hd>
</xsl:template>

<!-- span.defineTerm is now em.defineTerm -->
<xsl:template match="span[@class='defineTerm']">
	<em>
		<xsl:attribute name="id"><xsl:value-of select="generate-id()" /></xsl:attribute>
		<xsl:copy-of select="@*" />
		<xsl:apply-templates />
	</em>
</xsl:template>

<!-- sidebars and prodnotes need @render (default = optional) -->
<xsl:template match="sidebar | prodnote">
	<xsl:copy>
		<xsl:attribute name="id"><xsl:value-of select="generate-id()" /></xsl:attribute>
		<xsl:attribute name="render">optional</xsl:attribute>
		<xsl:copy-of select="@*" />
		<xsl:apply-templates />
	</xsl:copy>
</xsl:template>

<!-- Convert the various specialized lists to the generic list element -->
<xsl:template match="ol">
	<list type="ol">
		<xsl:attribute name="id"><xsl:value-of select="generate-id()" /></xsl:attribute>
		<xsl:for-each select="@*">
		    <xsl:choose>
			<xsl:when test="name()!='style'">
				<xsl:copy-of select="."/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:attribute name="enum"><xsl:value-of select="."/></xsl:attribute>
			</xsl:otherwise>
		    </xsl:choose>
		</xsl:for-each>
		<xsl:apply-templates />
	</list>
</xsl:template>

<xsl:template match="ul">
	<list type="ul">
		<xsl:attribute name="id"><xsl:value-of select="generate-id()" /></xsl:attribute>
		<xsl:copy-of select="@*" />
		<xsl:apply-templates />
	</list>
</xsl:template>

<!-- lin becomes line -->
<xsl:template match="lin">
	<line>
		<xsl:attribute name="id"><xsl:value-of select="generate-id()" /></xsl:attribute>
		<xsl:copy-of select="@*" />
		<xsl:apply-templates />
	</line>


</xsl:template>
</xsl:stylesheet>
<!-- Stylus Studio meta-information - (c) 2004-2006. Progress Software Corporation. All rights reserved.
<metaInformation>
<scenarios ><scenario default="yes" name="Scenario1" userelativepaths="yes" externalpreview="no" url="rf003.xml" htmlbaseurl="" outputurl="RF003_2005.xml" processortype="internal" useresolver="yes" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" validateoutput="no" validator="internal" customvalidator=""/></scenarios><MapperMetaTag><MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/><MapperBlockPosition></MapperBlockPosition><TemplateContext></TemplateContext><MapperFilter side="source"></MapperFilter></MapperMetaTag>
</metaInformation>
-->