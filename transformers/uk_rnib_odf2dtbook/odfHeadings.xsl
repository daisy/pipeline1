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
version="2.0"
>
  

<d:doc xmlns:d="rnib.org.uk/ns#">
 <revhistory>
   <purpose><para>Obtain a list of heading styles, from _styles.xml</para></purpose>
   <revision>
    <revnumber>1.0</revnumber>
    <date>2007-04-11T11:21:10.0Z</date>
    <authorinitials>DaveP</authorinitials>
    <revdescription>
     <para></para>
    </revdescription>
    <revremark></revremark>
   </revision>
  </revhistory>
  </d:doc>
  <xsl:output method="xml" indent="yes"/>

  <xsl:variable name="debug" select="false()"/>

  <xsl:template match="/" name="initial">
    <headings>
      <xsl:call-template name="oneLevel">
        <xsl:with-param name="level" select="1"/>
      </xsl:call-template>
    </headings>
  </xsl:template>


 <xsl:key name="headings" match="style" use="@name"/>
<!--  -->


  <xsl:template name="oneLevel">
    <xsl:param name="level" as="xs:integer"/>
    <xsl:variable name="headingName" select="concat('Heading_20_',string($level))"/>
    <xsl:variable name="nextHeading" select="concat('Heading_20_',string($level+1))"/>
    
    <xsl:if test="$debug">
     <xsl:choose>
       <xsl:when test="key('headings', $headingName)"/>
       <xsl:otherwise>
         <xsl:message>
           No heading found at level <xsl:value-of select="$level"/>
         </xsl:message>
       </xsl:otherwise>
     </xsl:choose>
    </xsl:if>

    <!-- output a level if matched. -->
    <xsl:if test="key('headings', $headingName)[not(@name=$nextHeading)]">
      <level n="{$level}">
        <h><xsl:value-of select="$headingName"/></h>
      <!-- Also captured styles based on. No longer does -->
      </level>
    </xsl:if>
    <!-- Recurse up to level 6 -->
    <xsl:choose>
      <xsl:when test="$level &lt; 6">
          <xsl:call-template name="oneLevel">
            <xsl:with-param name="level" select="$level + 1"/>
          </xsl:call-template>
      </xsl:when>
      <xsl:otherwise/>
    </xsl:choose>
  </xsl:template>





  <xsl:template match="*" >
  <xsl:message>
    *****<xsl:value-of select="name(..)"/>/{<xsl:value-of select="namespace-uri()"/>}<xsl:value-of select="name()"/>******
    </xsl:message>
</xsl:template>


</xsl:stylesheet>
