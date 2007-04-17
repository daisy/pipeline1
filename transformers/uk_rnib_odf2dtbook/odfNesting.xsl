<?xml version="1.0" encoding="utf-8"?>
 <!-- 
#
# odf2Nesting.xsl,  Dave Pawson; http://www.dpawson.co.uk
# Process an odf writer file.
# Convert nesting into structure
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
  xmlns:dp="http://www.dpawson.co.uk/ns#"
  xmlns:xs="http://www.w3.org/2001/XMLSchema-datatypes"
  version="2.0">

<d:doc xmlns:d="rnib.org.uk/tbs#">
 <revhistory>
   <purpose><para>Analyse $inputFile, derived from xxx.odt, for suitable nesting for Daisy use</para></purpose>
   <revision>
    <revnumber>1.0</revnumber>
    <date>2007-03-28T14:24:35Z</date>
    <authorinitials>DaveP</authorinitials>
    <revdescription>
     <para></para>
    </revdescription>
    <revremark></revremark>
   </revision>
  </revhistory>
  </d:doc>
  

  <xsl:output method="xml" indent="yes" encoding="utf-8"/>

 <!-- set to true to output debug -->
  <xsl:variable name="debug" select="false()"/>


  <xsl:template match="/">
    <report>
      <head>
        <hd>Test on file <xsl:value-of select="document-uri(.)"/></hd>
        <date>Tested: <xsl:value-of select="current-dateTime()"/></date>
      </head>
        <xsl:apply-templates/>
      </report>
  </xsl:template>


  <xsl:template match="document">
    <xsl:apply-templates/>
  </xsl:template>

 <xsl:template match="body">
   <xsl:for-each-group select="*" 
     group-starting-with="heading[@level='1']">
     <xsl:apply-templates select="." mode="group"/>
   </xsl:for-each-group>
 </xsl:template>


 <xsl:template match="heading" mode="group">
   <xsl:if test="$debug">
   <xsl:message>
    At level <xsl:value-of select="@level"/>
   Next is <xsl:value-of select="following-sibling::heading[1]/@level"/>
   </xsl:message>
 </xsl:if>
   <xsl:element name="{concat('level', @level)}">
     <xsl:element name="{concat('h',@level)}">
       <xsl:apply-templates/>
     </xsl:element>
     <xsl:for-each-group 
       select="current-group() except ."
       group-starting-with="heading">
       <xsl:apply-templates select="." mode="group"/>
     </xsl:for-each-group>
   </xsl:element>
 </xsl:template>


 <xsl:template match="px" mode="group">
     <xsl:copy-of select="current-group()"/>
 </xsl:template>



  <xsl:template match="*">
  <xsl:message>
    *****<xsl:value-of select="name(..)"/>/{<xsl:value-of select="namespace-uri()"/>}<xsl:value-of select="name()"/>******
    </xsl:message>
</xsl:template>



</xsl:stylesheet>
