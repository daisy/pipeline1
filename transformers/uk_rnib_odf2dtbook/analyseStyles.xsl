<?xml version="1.0" encoding="utf-8"?>
 <!-- 
#
# odf2daisy.xsl,  Dave Pawson; http://www.dpawson.co.uk
# Process an odf writer file.
# 
#
# 
# Original: 2006-03-23T14:25:46.0Z
#       :Initial issued
#
#
# Copyright &#xA9; Dave Pawson,  2006
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.

# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.

# You should have seen a copy of the GNU General Public License;
# if not, write to the Free Software
# Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
# uses other files: See the includes
# Generates a daisy output file to dtbook-2005-1
#


 -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:xdt="http://www.w3.org/2005/02/xpath-datatypes"
  xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0" 
  xmlns:style="urn:oasis:names:tc:opendocument:xmlns:style:1.0" 
  xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0" 
  xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0" 
  xmlns:draw="urn:oasis:names:tc:opendocument:xmlns:drawing:1.0" 
  xmlns:fo="urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0" 
  xmlns:xlink="http://www.w3.org/1999/xlink" 
  xmlns:dc="http://purl.org/dc/elements/1.1/" 
  xmlns:meta="urn:oasis:names:tc:opendocument:xmlns:meta:1.0" 
  xmlns:number="urn:oasis:names:tc:opendocument:xmlns:datastyle:1.0" 
  xmlns:svg="urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0" 
  xmlns:chart="urn:oasis:names:tc:opendocument:xmlns:chart:1.0" 
  xmlns:dr3d="urn:oasis:names:tc:opendocument:xmlns:dr3d:1.0" 
  xmlns:math="http://www.w3.org/1998/Math/MathML" 
  xmlns:form="urn:oasis:names:tc:opendocument:xmlns:form:1.0" 
  xmlns:script="urn:oasis:names:tc:opendocument:xmlns:script:1.0" 
  xmlns:ooo="http://openoffice.org/2004/office" 
  xmlns:ooow="http://openoffice.org/2004/writer" 
  xmlns:oooc="http://openoffice.org/2004/calc" 
  xmlns:dom="http://www.w3.org/2001/xml-events" 
  xmlns:dp="http://www.dpawson.co.uk/ns#"
exclude-result-prefixes="xs xdt office style text table draw fo xlink dc meta number svg chart dr3d math
                         form script ooo ooow oooc dom dp"
                version="2.0">

<d:doc xmlns:d="rnib.org.uk/ns#">
 <revhistory>
   <purpose><para>Analyze ODF styles file</para></purpose>
   <revision>
    <revnumber>1.0</revnumber>
    <date>2007-03-22T09:37:58.0Z</date>
    <authorinitials>DaveP</authorinitials>
    <revdescription>
     <para></para>
    </revdescription>
    <revremark></revremark>
   </revision>
  </revhistory>
  </d:doc>
  <xsl:output method="xml" indent="yes"/>
  <xsl:strip-space elements="*"/>

  <xsl:template match="/">
        <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="office:document-styles">
    <xsl:apply-templates/>
  </xsl:template>



<xsl:template match="office:styles">
  <styles>
 <!-- Analyse file styles.xml -->
    <xsl:apply-templates/>

  </styles>
</xsl:template>



<xsl:template match="style:default-style">
  <style name="default-style">
    <xsl:attribute name="family">
      <xsl:value-of select="@style:family"/>
    </xsl:attribute>

     <size>
      <xsl:choose>
        <xsl:when test="*[@fo:font-size]">
          <xsl:value-of select="*/@fo:font-size"/>
        </xsl:when>
        <xsl:when test="@style:parent-style-name">
          <xsl:variable name="parent" select="@style:parent-style-name" as="xs:string"/>
          <xsl:call-template name="ancestor-font-size">
            <xsl:with-param name="nd" select="//style:style[@style:name=$parent][1]"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>None</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </size>
    <xsl:apply-templates/>
  </style>
</xsl:template>


 <!-- for content.xml -->
 <xsl:template match="office:document-content">
   <xsl:apply-templates select=".//style:style"/>
 </xsl:template>
 <!-- Page based? -->
<xsl:template match="office:master-styles"/>

 <!-- page based -->
<xsl:template match="office:automatic-styles"/>

 <!-- ?? -->
<xsl:template match="text:outline-style"/>

<xsl:template match="text:linenumbering-configuration"/>


 <!-- lists -->
<xsl:template match="text:list-style"/>

 <!-- tables -->
 <xsl:template match="style:table-properties"/>

 <!-- rows -->
 <xsl:template match="style:table-row-properties"/>

 <xsl:template match="style:style[@style:family='graphic']"/>
 <xsl:template match="style:style[@style:family='presentation']"/>

<xsl:template match="style:style">
  <style name="{@style:name}">
    <xsl:attribute name="family">
      <xsl:value-of select="@style:family"/>
    </xsl:attribute>
    <xsl:if test="@style:parent-style-name">  
      <xsl:attribute name="parentStyle">
        <xsl:value-of select="@style:parent-style-name"/>
      </xsl:attribute>
    </xsl:if>
    <dname>
      <xsl:value-of select="(@style:name,@style:display-name)[last()]"/>
    </dname>
    <mapsTo>
      <xsl:value-of select="concat('level',dp:limitedSize(substring(@style:name,string-length(@style:name),1)))"/>
    </mapsTo>
    <size>
      <xsl:choose>
        <xsl:when test="*[@fo:font-size]">
          <xsl:value-of select="*/@fo:font-size"/>
        </xsl:when>
        <xsl:when test="@style:parent-style-name">
          <xsl:variable name="parent" select="@style:parent-style-name" as="xs:string"/>
          <xsl:message>
     Chasing       <xsl:value-of select="@style:parent-style-name"/>
          </xsl:message>
          <xsl:call-template name="ancestor-font-size">
            <xsl:with-param name="nd" select="//style:style[@style:name=$parent][1]"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>None</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </size>

  </style>
</xsl:template>



<xsl:function name="dp:limitedSize" as="xs:string">
  <xsl:param name="level" as="xs:string"/>
  <xsl:variable name='n' select="$level" as="xs:integer"/>

  <xsl:sequence select="min(($n,6))" as="xs:string"/>

</xsl:function>




 <!-- retrieve the ancestors font-size, or None -->
<xsl:template name="ancestor-font-size">
 <!-- the style:style node to retrieve the font size from  -->
  <xsl:param name="nd" as="item()"/>
  <xsl:choose>
    <xsl:when test="$nd/*/@fo:font-size">
      <xsl:value-of select="$nd/*/@fo:font-size"/>
    </xsl:when>
    <xsl:when test="$nd/@style:parent-style-name">
      <xsl:variable name="ancestor" select="$nd/@style:parent-style-name"/>
      <xsl:call-template name="ancestor-font-size">
        <xsl:with-param name="nd" select="//style:style[@style:name=$ancestor]"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:sequence select="None"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>



<xsl:template match="style:text-properties|style:paragraph-properties"/>
 

 <!-- declarations -->
  <xsl:template match="office:font-face-decls"/>

 <!-- No interest -->
  <xsl:template match="style:graphic-properties"/>

  <xsl:template match="text:bibliography-configuration"/>
 

  <xsl:template match="text:notes-configuration"/>



  <xsl:template match="*" >
  <xsl:message>
    *****<xsl:value-of select="name(..)"/>/{<xsl:value-of select="namespace-uri()"/>}<xsl:value-of select="name()"/>******
    </xsl:message>
</xsl:template>


</xsl:stylesheet>
