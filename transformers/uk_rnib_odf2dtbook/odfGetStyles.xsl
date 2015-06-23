<?xml version="1.0" encoding="utf-8"?>
<!-- 
#
# getStyles.xsl,  Dave Pawson; http://www.dpawson.co.uk
# 
# Process the contents and styles  an odf writer file
# to generate styles information
# 
# Original: 2007-03-27T11:11:50.0Z
#       :Initial issued
#
#
# Copyright &#xA9; Dave Pawson,  2007
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
   <purpose><para>Abstract style info from  ODT  file</para></purpose>

   <revision>
    <revnumber>1.0</revnumber>
    <date>2007-03-22T09:37:58.0Z</date>
    <authorinitials>DaveP</authorinitials>
    <revdescription>
     <para>Abstract style info from content.xml and style.xml</para>
    </revdescription>
    <revremark></revremark>
   </revision>

   <revision>
    <revnumber>1.1</revnumber>
    <date>2007-03-27T13:40:13.0Z</date>
    <authorinitials>DaveP</authorinitials>
    <revdescription>
     <para>Changes made after submitting to ODF fellowship site. </para>
    </revdescription>
    <revremark></revremark>
   </revision>


  </revhistory>
  </d:doc>

  <xsl:output method="xml" indent="yes"/>


<!-- Set to true for debug output -->
<xsl:variable name="debug" select="false()"/>

  <xsl:strip-space elements="*"/>

  <xsl:template match="/" name="initial">

 <!-- Collate into a variable -->
    <xsl:variable name="styles" as="element()*">
    <styles>
       <!-- Styles from content.xml -->
      <xsl:apply-templates select="document('content.xml')//style:style"/> 
       <!-- Styles from styles.xml, two locations. -->
      <xsl:apply-templates select="document('styles.xml')//style:style"/> 
      <xsl:apply-templates select="document('styles.xml')//style:default-style"/> 
    </styles>
  </xsl:variable>

   <!-- Announce the count of styles found -->
   <xsl:if test="$debug">
       <xsl:message>
    <xsl:value-of select="count($styles/style)"/> styles found
    <xsl:copy-of select="$styles"/>
  </xsl:message>
   </xsl:if>


 <!-- Now output the styles, with all sizes resolved. -->
 <!-- By processing the variable 'styles' -->
 <!-- in the sort mode.  -->
  <styles>
    <xsl:apply-templates select="$styles/style" mode = 'sort'>
      <xsl:with-param name="nds" select="$styles"/>
    </xsl:apply-templates>
  </styles>

  </xsl:template>



 <!-- finish off filling in the styles. -->
 <!-- param nds is the sequence being processed - the styles. -->
  <xsl:template match="style" mode="sort">
    <xsl:param name="nds" as="element()*"/>
    <xsl:choose>
       <!-- Ignore the 'text' family - no size information is available  -->
      <xsl:when test="@family='text'"/>
       <!-- copy size to output if available -->
      <xsl:when test="size">
        <xsl:choose>
          <xsl:when test="not(contains(size,'%'))">
            <xsl:copy>
              <xsl:copy-of select="@*|*"/>
            </xsl:copy>
          </xsl:when>
          <xsl:otherwise>
            <!-- Size is as a percentage. Needs calculating -->
            <xsl:copy>
              <xsl:copy-of select="dname|mapsTo|@*"/>
               <!-- Recalculate the size if it is x% of parent style -->
              <size>
                <xsl:variable name='mult' select="xs:float(substring-before(size,'%')) div 100" as="xs:float"/>
                <xsl:variable name='parentSize' select="xs:float(substring-before($nds//style[@name=current()/@parentStyle]/size,'pt'))" as="xs:float"/>
                <xsl:variable name="thisSz"  select="format-number($mult * $parentSize, '0.0')" />

                <xsl:value-of select="concat( xs:string($thisSz), 'pt')"/>
              </size>
            </xsl:copy>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
       <!-- Ignore the text family, both table family .  -->
        <!-- Ignore Standard parent style when no size -->
      <xsl:when test="@family='text'"/>
      <xsl:when test="@family='table-cell'"/>
      <xsl:when test="@family='table-column'"/>
      <!-- <xsl:when test="@family='paragraph'"/>-->
      <xsl:when test="@parentStyle='Standard' and not(size)"/>

      <xsl:when test="@parentStyle">
         <!-- When parentStyle is present, resolve recursively to ancestor with a size.  -->
        <xsl:copy>
          <xsl:copy-of select="dname|mapsTo|@*"/>
        <xsl:call-template name="size">
          <xsl:with-param name="parent" select="@parentStyle"/>
          <xsl:with-param name="nds" select="$nds"/>
        </xsl:call-template>
      </xsl:copy>
      </xsl:when>
        <xsl:otherwise>
          <xsl:copy>
         <xsl:copy-of select="*|@*"/>
       </xsl:copy>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>



 <!-- find the font size or zero if not available -->
 <xsl:template name="size">
   <xsl:param name="parent" as="xs:string"/>
   <xsl:param name="nds" as="element()*"/>
   <xsl:choose>
     <xsl:when test="$nds//style[@name=$parent][size]">
       <size>
       <xsl:value-of select="size"/>
     </size>
     </xsl:when>
     <xsl:when test="$nds//style[@name=$parent]">

       <xsl:choose>
         <xsl:when test="$nds//style[@name=$parent][1]/@parentStyle">
              <xsl:call-template name="size">
         <xsl:with-param name="parent" select="$nds//style[@name=$parent][1]/@parentStyle"/>
       </xsl:call-template>
         </xsl:when>
         <xsl:otherwise>
            <!-- No output -->
            <xsl:if test="$debug">
              <xsl:message>
                Unable to find size for <xsl:value-of select="$parent"/>
              </xsl:message>
            </xsl:if>
          </xsl:otherwise>
       </xsl:choose>

     </xsl:when>
     <xsl:otherwise>
     <!-- No output -->
     </xsl:otherwise>
   </xsl:choose>

 </xsl:template>



  <xsl:template match="office:document-styles">
    <xsl:apply-templates/>
  </xsl:template>



<xsl:template match="office:styles">
    <xsl:apply-templates/>
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


 <xsl:template match="style:default-style[@style:family='table']"/>
 <xsl:template match="style:default-style[@style:family='table-row']"/>


 <!-- Default styles are added.  -->
 <!--  -->
<xsl:template match="style:default-style">
  <style name="default-style">
    <xsl:attribute name="family">
      <xsl:value-of select="@style:family"/>
    </xsl:attribute>
    <xsl:if test=".//*[@fo:font-size]">
      <size>
        <xsl:value-of select=".//*[@fo:font-size]/@fo:font-size"/>
      </size>
    </xsl:if>
  </style>

 
</xsl:template>


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
    <xsl:if test="contains(@style:name, 'Heading_')">
    <mapsTo>
      <xsl:if test="$debug">
      <xsl:message>
        <xsl:value-of select="@style:name"/>
      </xsl:message>
    </xsl:if>
      <xsl:value-of select="concat('level',dp:limitedSize(substring(@style:name,string-length(@style:name),1)))"/>
    </mapsTo>
  </xsl:if>

    <xsl:if test=".//*[@fo:font-size]">
      <size>
        <xsl:value-of select=".//*[@fo:font-size]/@fo:font-size"/>
      </size>
    </xsl:if>
   </style>
</xsl:template>




<xsl:template match="style:text-properties|style:paragraph-properties"/>
 

 <!-- declarations -->
  <xsl:template match="office:font-face-decls"/>

 <!-- No interest -->
  <xsl:template match="style:graphic-properties"/>

  <xsl:template match="text:bibliography-configuration"/>
 

  <xsl:template match="text:notes-configuration"/>

<xsl:function name="dp:limitedSize" as="xs:string">
  <xsl:param name="level" as="xs:string"/>
  <xsl:variable name='n' select="xs:integer($level)" as="xs:integer"/>

  <xsl:sequence select="xs:string(min(($n,6)))" />

</xsl:function>

  <xsl:template match="*" >
  <xsl:message>
    *****<xsl:value-of select="name(..)"/>/{<xsl:value-of select="namespace-uri()"/>}<xsl:value-of select="name()"/>******
    </xsl:message>
</xsl:template>


</xsl:stylesheet>
