<?xml version="1.0" encoding="utf-8"?>
 <!-- 
#
# odfNestCheck.xsl,  Dave Pawson; http://www.dpawson.co.uk
# Process an odf writer file.
#  check nesting levels 
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
xmlns:xs="http://www.w3.org/2001/XMLSchema"
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
   <xsl:if test="not(heading/@level = '1') ">
     <xsl:message terminate="yes">
       First heading must be a heading level 1
     </xsl:message>
   </xsl:if>
   <xsl:apply-templates/>
 </xsl:template>


 <xsl:template match="heading" >
  
   <xsl:variable name='thisHead' select="@level" as="xs:integer"/>
   <xsl:variable name='nextHead' select="($thisHead,following-sibling::heading[1]/@level)[last()]" as="xs:integer"/>

   <xsl:choose>
     <xsl:when test="(max(($thisHead,$nextHead)) - min(($thisHead,$nextHead))) le 1">
       <xsl:if test="$debug">
       <xsl:message>
         Yes. OK
       </xsl:message>
     </xsl:if>
     </xsl:when>
     <xsl:otherwise>
       <xsl:message terminate='yes'>
         Structure Error. This heading level <xsl:value-of select="$thisHead"/>, followed by  <xsl:value-of select="$nextHead"/>
       Heading is in position <xsl:value-of select="count(preceding::heading) + 1 "/> 
       <xsl:if test="string(.)">
         Heading content is: "<xsl:value-of select="."/>"
       </xsl:if>
       </xsl:message>
     </xsl:otherwise>
   </xsl:choose>

 </xsl:template>







</xsl:stylesheet>
