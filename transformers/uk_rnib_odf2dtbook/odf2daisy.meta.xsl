<?xml version="1.0" encoding="utf-8"?>
 <!-- 
#
# odf2daisy.meta.xsl,  Dave Pawson; http://www.dpawson.co.uk
# Process an odf writer file, meta content.
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
# 
# Generates a daisy output file to dtbook-2005-1
#
 -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns='http://www.daisy.org/z3986/2005/dtbook/'

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
  xmlns:xforms="http://www.w3.org/2002/xforms" 
  xmlns:xsd="http://www.w3.org/2001/XMLSchema" 
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:d="rnib.org.uk/ns#"
  xmlns:util="java:java.util.UUID"

exclude-result-prefixes="xsi xsd xforms dom oooc ooow ooo 
                         script form math dr3d chart svg 
                         number meta dc xlink fo draw table 
                         text style office xdt xs util d"

                version="1.0">
<d:doc >
  <d:revhistory>
    <d:purpose>
      <d:para>This stylesheet uses XSLT 1.0. Processes ODF
      metadata. Imported from odf2daisy.xsl. Works with the input file of
      meta.xml</d:para>
    </d:purpose>
        
    <d:revision>
      <d:revnumber>1.0</d:revnumber>
      <d:date>2006-02-23T13:52:18.0Z</d:date>
      <d:authorinitials>DaveP</d:authorinitials>
      <d:revdescription>
        <d:para></d:para>
      </d:revdescription>
      <d:revremark>&#xA9;Copyright Dave Pawson, RNIB, 2006</d:revremark>
    </d:revision>

    <d:revision>
      <d:revnumber>1.1</d:revnumber>
      <d:date>2006-11-01T15:10:52Z</d:date>
      <d:authorinitials>DaveP</d:authorinitials>
      <d:revdescription>
        <d:para>Added processing for user field declarations</d:para>
      </d:revdescription>
      <d:revremark>&#xA9;Copyright Dave Pawson, RNIB, 2006</d:revremark>
    </d:revision>
    
    <d:revision>
      <d:revnumber>1.2</d:revnumber>
      <d:date>2007-03-17T10:37:24Z</d:date>
      <d:authorinitials>DaveP</d:authorinitials>
      <d:revdescription>
        <d:para>Added processing dc:Identifier</d:para>
      </d:revdescription>
      <d:revremark>&#xA9;Copyright Dave Pawson, RNIB, 2006,2007</d:revremark>
    </d:revision>


  </d:revhistory>
  </d:doc>

  <xsl:variable name="version" select="document('')//d:revision[position()=last()]/d:revnumber"/>

<xsl:output method="xml" indent="yes"/>



  <xsl:template match="office:document-meta">

   <xsl:if test="$debug">
      <xsl:message>
        Processing metadata / office:document-meta
      </xsl:message>
    </xsl:if>
    <xsl:element name="meta">
    <xsl:attribute  name='name'>
      <xsl:value-of select="'office:version'"/>
      </xsl:attribute>
      <xsl:attribute name='content'>
        <xsl:value-of select="@office:version"/>
      </xsl:attribute>
      <xsl:attribute name='scheme'>
        <xsl:value-of select="'urn:oasis:names:tc:opendocument:xmlns:meta:1.0'"/>
      </xsl:attribute>
  </xsl:element>
        <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="office:meta">
    <xsl:variable name="uid" select="util:randomUUID()"/>
    <xsl:element name="meta">
       <xsl:attribute  name='name'>
          <xsl:value-of select="'dc:Identifier'"/>
        </xsl:attribute>
        <xsl:attribute name='content'>
           <xsl:value-of select="util:toString($uid)"/>
         </xsl:attribute>
    </xsl:element>
    <xsl:element name="meta">
       <xsl:attribute  name='name'>
          <xsl:value-of select="'dc:Publisher'"/>
        </xsl:attribute>
        <xsl:attribute name='content'>
           <xsl:value-of select="concat('odf2daisy v',$version)"/>
         </xsl:attribute>
    </xsl:element>



    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="dc:title">
    <meta name="dc:Title" content="{.}" scheme="http://purl.org/dc/elements/1.1/#"/>
  </xsl:template>

  <xsl:template match="dc:description">
    <meta name="dc:Description" content="{.}" scheme="http://purl.org/dc/elements/1.1/#"/>
  </xsl:template>

 <!-- Since dc:date can appear in notes -->
  <xsl:template match="office:meta/dc:date">
    <meta name="dc:Date" 
      content="{substring(.,1,10)}" scheme="http://purl.org/dc/elements/1.1/#"/>
  </xsl:template>

  <xsl:template match="dc:language">
    <meta name="dc:Language" content="{.}" scheme="http://purl.org/dc/elements/1.1/#"/>
  </xsl:template>

  <xsl:template match="dc:subject">
    <meta name="dc:Subject" content="{.}" scheme="http://purl.org/dc/elements/1.1/#"/>
  </xsl:template>

  <xsl:template match="dc:creator">
    <meta name="dc:Creator" content="{.}" scheme="http://purl.org/dc/elements/1.1/#"/>
  </xsl:template>




  <xsl:template match="meta:*[not(local-name()='user-defined')]">
    <xsl:element name="meta">
    <xsl:attribute  name='name'>
      <xsl:value-of select="name()"/>
      </xsl:attribute>
      <xsl:attribute name='content'>
        <xsl:value-of select="."/>
      </xsl:attribute>
      <xsl:attribute name='scheme'>
        <xsl:value-of select="'urn:oasis:names:tc:opendocument:xmlns:meta:1.0'"/>
      </xsl:attribute>
  </xsl:element>
  </xsl:template>

  <xsl:template match="meta:user-defined"/>
  <xsl:template match="meta:document-statistic"/>

<!--  -->
<!-- User variable declarations. Expanded by the application, hence ignored. -->
<!--  -->


  <xsl:template match="text:user-field-decls">
    <xsl:comment>
      <xsl:text> User variables omitted</xsl:text>
    
    </xsl:comment>


  </xsl:template>


  <xsl:template match="text:user-field-decl">
    <xsl:text>
    </xsl:text>    <xsl:value-of select="@office:value-type"/> <xsl:text> </xsl:text>  <xsl:value-of select="@office:string-value"/>


  </xsl:template>

</xsl:stylesheet>
