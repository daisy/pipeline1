<?xml version="1.0" encoding="utf-8"?>
<!-- 
#
# getStyles.xsl,  Dave Pawson; http://www.dpawson.co.uk
# 
# Process the contents and styles of an odf writer file
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
                         form script ooo ooow oooc dom dp"                version="2.0">

  <xsl:import href="identity2.xsl"/>

  <xsl:param name="headingsfile" select="'none'"/>

<d:doc xmlns:d="rnib.org.uk/ns#">
 <revhistory>
   <purpose><para>Remove lists from around text:h headings </para></purpose>

   <revision>
    <revnumber>1.0</revnumber>
    <date>2007-04-11T10:58:54.0Z</date>
    <authorinitials>DaveP</authorinitials>
    <revdescription>
     <para>Clean up headings to the top level</para>
    </revdescription>
    <revremark></revremark>
   </revision>

   <revision>
    <revnumber>1.1</revnumber>
    <date>2007-04-17T12:34:37.0Z</date>
    <authorinitials>DaveP</authorinitials>
    <revdescription>
     <para>Added check for first child of office:text, must be a heading level 1</para>
    </revdescription>
    <revremark></revremark>
   </revision>



  </revhistory>
  </d:doc>

  <xsl:output method="xml" indent="yes"/>


<!-- Set to true for debug output -->
<xsl:variable name="debug" select="false()"/>


<xsl:template match="/" name="initial">
  <xsl:if test="$headingsfile='none'">
       <xsl:message terminate="yes">
         odf2.cleanHeadings. Unable to find headings file, Quitting
       </xsl:message>
     </xsl:if>
  <xsl:apply-templates />
</xsl:template>



<xsl:template match="office:text">
<xsl:variable name="headings" select="document($headingsfile)"/>

  <xsl:if test="not(*[1]/@text:style-name = ($headings/headings/level[1]/h))">
    <xsl:message terminate="yes">
      odf2.cleanHeadings.xsl. First child of office:text is not a heading level 1.
      Found <xsl:value-of select="name(*[1])"/> with content "<xsl:value-of select="*[1]/text()"/>"
    </xsl:message>
  </xsl:if>



</xsl:template>


  <xsl:template match="text:list[.//text:h[not(@text:is-list-header)]]">
      <xsl:apply-templates select=".//text:h"/>
  </xsl:template>

 
  <xsl:template match="text:sequence-decls| text:user-field-decls| office:forms"/>


</xsl:stylesheet>
