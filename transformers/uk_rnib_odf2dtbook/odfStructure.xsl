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

  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:oooc="http://openoffice.org/2004/calc"
  xmlns:xml="http://www.w3.org/XML/1998/namespace"
  xmlns:meta="urn:oasis:names:tc:opendocument:xmlns:meta:1.0"
  xmlns:dom="http://www.w3.org/2001/xml-events"
  xmlns:number="urn:oasis:names:tc:opendocument:xmlns:datastyle:1.0"
  xmlns:xforms="http://www.w3.org/2002/xforms"
  xmlns:xsd="http://www.w3.org/2001/XMLSchema"
  xmlns:svg="urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0"
  xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
  xmlns:chart="urn:oasis:names:tc:opendocument:xmlns:chart:1.0"
  xmlns:style="urn:oasis:names:tc:opendocument:xmlns:style:1.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:dr3d="urn:oasis:names:tc:opendocument:xmlns:dr3d:1.0"
  xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0"
  xmlns:math="http://www.w3.org/1998/Math/MathML"
  xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0"
  xmlns:form="urn:oasis:names:tc:opendocument:xmlns:form:1.0"
  xmlns:draw="urn:oasis:names:tc:opendocument:xmlns:drawing:1.0"
  xmlns:script="urn:oasis:names:tc:opendocument:xmlns:script:1.0"
  xmlns:fo="urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0"
  xmlns:ooo="http://openoffice.org/2004/office"
  xmlns:xlink="http://www.w3.org/1999/xlink"
  xmlns:ooow="http://openoffice.org/2004/writer"
  xmlns:dp="http://www.dpawson.co.uk/ns#"
  version="2.0"
  exclude-result-prefixes="xsi xsd xforms dom oooc ooow ooo 
                         script form math dr3d chart svg 
                         number meta dc xlink fo draw table 
                         text style office  xs
                          dp"
  >

  <xsl:param name="stylefile" select="'none'"/>

<xsl:output method="xml" indent="yes" encoding="utf-8"/>

<xsl:strip-space elements="*"/>

<!-- Set to true for debug output -->
<xsl:variable name="debug" select="false()"/>

   <xsl:template match="/">
     <xsl:if test="$stylefile='none'">
       <xsl:message terminate="yes">
         Unable to file style file, Quitting
       </xsl:message>
     </xsl:if>

     <document>
      <xsl:apply-templates/>
    </document>
   </xsl:template>

   <xsl:template match="office:document-content">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="office:scripts">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="office:font-face-decls">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="style:font-face">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="office:automatic-styles">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="style:style">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="style:table-properties">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="style:table-column-properties">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="style:table-cell-properties">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="style:background-image">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="style:table-row-properties">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="style:text-properties">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="style:paragraph-properties">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="style:tab-stops">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="style:tab-stop">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="style:graphic-properties">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="style:columns">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="style:section-properties">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="text:list-style">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="text:list-level-style-number">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="style:list-level-properties">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="text:list-level-style-bullet">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="number:date-style">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="number:day">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="number:text">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="number:month">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="number:year">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="office:body">
     <body>
      <xsl:apply-templates/>
    </body>
   </xsl:template>

   <xsl:template match="office:forms">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="text:tracked-changes">
 <!-- Not recorded -->
 <changeHistory/>
   </xsl:template>


   <xsl:template match="dc:creator">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="dc:date">
      <xsl:apply-templates/>
   </xsl:template>

 <!-- May be needed? E.g. if a header is deleted -->
   <xsl:template match="text:deletion">
      <xsl:apply-templates/>
   </xsl:template>

 <!-- No need to generate. Done for sizing  -->
   <xsl:template match="text:p[not(following-sibling::*[1][self::text:p])]">
     <p/>
   </xsl:template>

   <xsl:template match="text:p[following-sibling::*[1][self::text:p]]"/>

   <xsl:template match="text:span">
   </xsl:template>

   <xsl:template match="text:s">
   </xsl:template>

   <xsl:template match="text:sequence-decls"/>

 

   <xsl:template match="text:user-field-decls"/>

   <xsl:template match="text:user-field-decl">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="draw:frame">
  
   </xsl:template>

   <xsl:template match="draw:image">
       <image/>
   </xsl:template>

   <xsl:template match="svg:desc">
     <desc/>
   </xsl:template>

   <xsl:template match="text:user-field-get">
   </xsl:template>

   <xsl:template match="text:a">
   </xsl:template>

   <xsl:template match="text:table-of-content">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="text:table-of-content-source">
   </xsl:template>

   <xsl:template match="text:index-title-template">
   </xsl:template>

   <xsl:template match="text:table-of-content-entry-template">
   </xsl:template>

   <xsl:template match="text:index-entry-link-start">
   </xsl:template>

   <xsl:template match="text:index-entry-span">
   </xsl:template>

   <xsl:template match="text:index-entry-text">
   </xsl:template>

   <xsl:template match="text:index-entry-tab-stop">
   </xsl:template>

   <xsl:template match="text:index-entry-page-number">
   </xsl:template>

   <xsl:template match="text:index-entry-link-end">
   </xsl:template>

   <xsl:template match="text:index-body">
     <index-body/>
   </xsl:template>

   <xsl:template match="text:tab">
   </xsl:template>



 <!-- Variable holding style information from abstractStyles.xsl -->
   <xsl:variable name="styles" >
     <xsl:choose>
       <xsl:when test="doc-available($stylefile)">
         <xsl:copy-of select="document($stylefile)//*"/>
       </xsl:when>
       <xsl:otherwise>
         <xsl:message terminate="yes">
           Unable to find styles file, Quitting
         </xsl:message>
       </xsl:otherwise>
     </xsl:choose>
   </xsl:variable>

   <xsl:template match="text:h">
     <heading >
       <xsl:attribute name="level">
         <xsl:value-of select="dp:level(@text:style-name)"/>
       </xsl:attribute>
       <xsl:value-of select="."/>
     </heading>
   </xsl:template>

   <xsl:template match="text:list">
     <list/>
     <xsl:if test=".//text:h">
       <xsl:message terminate="yes">
         Heading found within list. Invalid structure
         Heading is "<xsl:value-of select=".//text:h"/>"
       </xsl:message>
     </xsl:if>
     <!--    <xsl:apply-templates/> -->
   </xsl:template>

   <xsl:template match="text:list-item">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="text:line-break">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="table:table">
     <table/>
       <xsl:if test=".//text:h">
       <xsl:message terminate="yes">
         Heading found within table. Invalid structure
         Heading is "<xsl:value-of select=".//text:h"/>"
       </xsl:message>
     </xsl:if>
     <!--      <xsl:apply-templates/> -->
   </xsl:template>

   <xsl:template match="table:table-column">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="table:table-row">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="table:table-cell">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="text:change-start">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="text:change-end">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="text:change">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="draw:text-box">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="text:sequence">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="draw:plugin">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="draw:param">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="draw:rect">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="draw:line">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="draw:object">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="table:table-header-rows">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="table:covered-table-cell">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="text:sequence-ref">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="text:reference-mark">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="text:alphabetical-index-mark">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="text:list-header">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="text:section">
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="text:bookmark">
      <xsl:apply-templates/>
   </xsl:template>


 <!-- Determine level from style name, else zero -->
 <xsl:function name="dp:level" as="xs:integer">
   <xsl:param name="stylename" as="xs:string"/>
   <xsl:if test="$debug">
   <xsl:message>
     <xsl:value-of select="$stylename"/> -   [<xsl:value-of select="$styles/styles/style[@name=$stylename]/@name"/>]   
   </xsl:message>
 </xsl:if>
   <xsl:sequence select="dp:last-char-as-int($stylename)"/>
 </xsl:function>



 <!-- Convert style-name into int, or 0 -->
 <xsl:function name="dp:last-char-as-int" as="xs:integer">
   <xsl:param name='stylename' as="xs:string"/>

   <xsl:variable name="char" select="substring($stylename,string-length($stylename),1)" as="xs:string"/>

   <xsl:value-of select="if ($char castable as xs:integer) then xs:integer($char) else 0"/>
   <!--
   <xsl:choose>
     <xsl:when test="$char castable-as xs:integer">
       <xsl:sequence select="xs:integer($char)"/>
     </xsl:when>
     <xsl:otherwise>
       <xsl:sequence select="0"/>
     </xsl:otherwise>
   </xsl:choose>
-->
 </xsl:function>

</xsl:stylesheet>